package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import java.io.IOException;

public class ChatGPTClient {

    private static final String API_KEY = System.getenv("CHATGPT_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    private ChatGPTClient() {}

    private static int findMatchingBrace(String source, int start) {
        int braceCount = 0;
        for (int i = start; i < source.length(); i++) {
            if (source.charAt(i) == '{') {
                braceCount++;
            } else if (source.charAt(i) == '}') {
                braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
        }
        return -1; // Matching brace not found
    }

    public static String extractMethodBody(String sourceCode, String methodName) {
        String methodSignature = methodName + "(";
        int start = sourceCode.indexOf(methodSignature);
        if (start == -1) {
            return "Method not found";
        }

        int bodyStart = sourceCode.indexOf("{", start);
        int bodyEnd = findMatchingBrace(sourceCode, bodyStart);
        if (bodyStart == -1 || bodyEnd == -1) {
            return "Invalid method structure";
        }

        return sourceCode.substring(bodyStart + 1, bodyEnd).trim();
    }
//    public static String extractMethodBody(String methodCode) {
//        int methodStart = methodCode.indexOf("op(");
//        int start = methodCode.indexOf("{", methodStart);
//        int end = methodCode.indexOf("}", methodStart);
//
//        if (start != -1 && end != -1 && start < end) {
//            return methodCode.substring(start + 1, end).trim();
//        } else {
//            // Handle cases where the method body is not found or the braces are mismatched
//            return "Method body not found or braces are mismatched";
//        }
//    }

    public static String getGenericBody(String op) throws ParseException {

        String prompt = "write a java method that takes two int inputs a and b. " +
                "The method should " + op + " the inputs and return the result." +
                "The method should be called op";
        try {
            String response = sendPromptToChatGPT(prompt);
            System.out.println("Response from ChatGPT: " + response);
            String content = extractContentFromResponse(response);
            System.out.println("Content: " + content);
            String methodBody = extractMethodBody(content, "op");
            System.out.println("Method body: " + methodBody);
            return methodBody;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String extractContentFromResponse(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        if (rootNode.has("choices") && rootNode.get("choices").isArray()) {
            JsonNode choicesArray = rootNode.get("choices");
            if (choicesArray.size() > 0 && choicesArray.get(0).has("message")) {
                JsonNode messageNode = choicesArray.get(0).get("message");
                if (messageNode.has("content")) {
                    return messageNode.get("content").asText();
                }
            }
        }

        return null;
    }

    private static String sendPromptToChatGPT(String prompt) throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);

            // Set headers
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            // Set the request body
            String jsonBody = "{\"model\": \"" + MODEL + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            request.setEntity(new StringEntity(jsonBody));
            // parse jsonBody into a json object

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Convert the response to a String and return it
                return EntityUtils.toString(response.getEntity());
            }
        }
    }
}

