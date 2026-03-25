package com.skipq.backend.service;

import com.skipq.backend.dto.ChatAssistRequest;
import com.skipq.backend.dto.ChatAssistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatAssistantService {

    private static final Set<String> ALLOWED_ACTIONS = Set.of(
            "CONNECT_STORE",
            "SHOW_PRODUCTS",
            "SCAN_BARCODE",
            "ADD_TO_CART",
            "VIEW_CART",
            "REMOVE_ITEM",
            "CHECKOUT",
            "SHOW_BILL",
            "VERIFY_EXIT"
    );

    private final RestClient restClient = RestClient.builder().build();

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${openai.api.base-url:https://api.openai.com/v1}")
    private String openAiBaseUrl;

    @Value("${openai.model:gpt-4.1-mini}")
    private String openAiModel;

    public ChatAssistResponse assist(ChatAssistRequest request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            return fallbackResponse("Please share what you want to do, for example: connect store, scan product, or checkout.", request);
        }

        if (!StringUtils.hasText(openAiApiKey)) {
            return fallbackResponse("I can help step-by-step. Start by connecting to a store using your store code.", request);
        }

        try {
            String llmContent = callOpenAi(request);
            ChatAssistResponse parsed = parseModelResponse(llmContent);
            if (parsed != null) {
                parsed.setSource("OPENAI");
                return parsed;
            }
        } catch (Exception ignored) {
            // Keep chat available even when OpenAI is temporarily unavailable.
        }

        return fallbackResponse("I hit a temporary issue with AI, but I can still guide you through the next step.", request);
    }

    private String callOpenAi(ChatAssistRequest request) {
        String systemPrompt = "You are SkipQ assistant. Tone: friendly, short, step-by-step. " +
                "Guide users through store connection, product search/scan, cart, checkout, payment status, digital bill, and exit verification. " +
                "Use only these actions: CONNECT_STORE, SHOW_PRODUCTS, SCAN_BARCODE, ADD_TO_CART, VIEW_CART, REMOVE_ITEM, CHECKOUT, SHOW_BILL, VERIFY_EXIT. " +
                "Return valid JSON only with keys: reply (string), nextStep (string), suggestedActions (array of action strings).";

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("userId", request.getUserId());
        context.put("storeConnected", request.getStoreConnected());
        context.put("storeId", request.getStoreId());
        context.put("cartItemsCount", request.getCartItemsCount());
        context.put("lastOrderId", request.getLastOrderId());

        String userPrompt = "User message: " + request.getMessage() + "\nSession context: " + context;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", openAiModel);
        payload.put("temperature", 0.2);
        payload.put("response_format", Map.of("type", "json_object"));
        payload.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        Map<?, ?> result = restClient.post()
                .uri(openAiBaseUrl + "/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + openAiApiKey)
                .body(payload)
                .retrieve()
                .body(Map.class);

        if (result == null) {
            return null;
        }

        Object choicesObj = result.get("choices");
        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
            return null;
        }

        Object first = choices.get(0);
        if (!(first instanceof Map<?, ?> firstChoice)) {
            return null;
        }

        Object messageObj = firstChoice.get("message");
        if (!(messageObj instanceof Map<?, ?> messageMap)) {
            return null;
        }

        Object contentObj = messageMap.get("content");
        return contentObj == null ? null : contentObj.toString();
    }

    private ChatAssistResponse parseModelResponse(String content) {
        if (!StringUtils.hasText(content)) {
            return null;
        }

        try {
            String reply = extractJsonString(content, "reply");
            String nextStep = extractJsonString(content, "nextStep");

            List<String> actions = new ArrayList<>();
            for (String action : extractJsonStringArray(content, "suggestedActions")) {
                if (ALLOWED_ACTIONS.contains(action)) {
                    actions.add(action);
                }
            }

            if (!StringUtils.hasText(reply)) {
                return null;
            }

            return ChatAssistResponse.builder()
                    .reply(reply)
                    .nextStep(StringUtils.hasText(nextStep) ? nextStep : "Continue with the next checkout step.")
                    .suggestedActions(actions)
                    .build();
        } catch (Exception ex) {
            return null;
        }
    }

    private String extractJsonString(String json, String key) {
        String pattern = "\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1)
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\\\", "\\");
    }

    private List<String> extractJsonStringArray(String json, String key) {
        String arrayPattern = "\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\[(.*?)]";
        Matcher arrayMatcher = Pattern.compile(arrayPattern, Pattern.DOTALL).matcher(json);
        if (!arrayMatcher.find()) {
            return List.of();
        }

        String body = arrayMatcher.group(1);
        Matcher itemMatcher = Pattern.compile("\\\"((?:\\\\.|[^\\\"])*)\\\"").matcher(body);
        List<String> items = new ArrayList<>();
        while (itemMatcher.find()) {
            String value = itemMatcher.group(1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\\\", "\\");
            items.add(value);
        }
        return items;
    }

    private ChatAssistResponse fallbackResponse(String prefix, ChatAssistRequest request) {
        boolean storeConnected = Boolean.TRUE.equals(request != null ? request.getStoreConnected() : null);
        int cartCount = request != null && request.getCartItemsCount() != null ? request.getCartItemsCount() : 0;
        Long orderId = request != null ? request.getLastOrderId() : null;

        if (!storeConnected) {
            return ChatAssistResponse.builder()
                    .reply(prefix + " Step 1: connect your store code to begin shopping.")
                    .nextStep("Connect store")
                    .suggestedActions(List.of("CONNECT_STORE"))
                    .source("RULE_BASED_FALLBACK")
                    .build();
        }

        if (cartCount <= 0) {
            return ChatAssistResponse.builder()
                    .reply(prefix + " Step 2: scan/search products and add items to your cart.")
                    .nextStep("Add products to cart")
                    .suggestedActions(List.of("SHOW_PRODUCTS", "SCAN_BARCODE", "ADD_TO_CART", "VIEW_CART"))
                    .source("RULE_BASED_FALLBACK")
                    .build();
        }

        if (orderId == null) {
            return ChatAssistResponse.builder()
                    .reply(prefix + " Step 3: your cart is ready, proceed to checkout.")
                    .nextStep("Checkout")
                    .suggestedActions(List.of("VIEW_CART", "CHECKOUT"))
                    .source("RULE_BASED_FALLBACK")
                    .build();
        }

        return ChatAssistResponse.builder()
                .reply(prefix + " Step 4: view your digital bill and complete exit verification.")
                .nextStep("Bill and verification")
                .suggestedActions(List.of("SHOW_BILL", "VERIFY_EXIT"))
                .source("RULE_BASED_FALLBACK")
                .build();
    }
}
