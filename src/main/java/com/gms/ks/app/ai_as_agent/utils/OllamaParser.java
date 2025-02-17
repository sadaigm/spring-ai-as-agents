package com.gms.ks.app.ai_as_agent.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaParser {

    static Logger logger = LoggerFactory.getLogger(OllamaParser.class);

    public static JsonNode parse(String response) {
        // Define the regular expression to match the data with the header and JSON
        Pattern pattern = Pattern.compile("<\\|start_header_id\\|>(.*?)<\\|end_header_id\\|>\\s*\\{(.*?)\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);

        // Check if the pattern for header and JSON is found
        if (matcher.find()) {
            // Extract the header (if needed)
            String header = matcher.group(1);
//            System.out.println("Header: " + header);

            // Extract the JSON-like content
            String jsonContent = matcher.group(2).trim();
//            System.out.println("Extracted JSON Content (before formatting): " + jsonContent);

            // Handle the case where JSON is empty or malformed
            if (jsonContent.isEmpty()) {
                System.out.println("No JSON content found.");
                return null; // Or return an empty JsonNode or an error message
            }

            // Make sure the JSON is properly wrapped in curly braces
            jsonContent = "{" + jsonContent + "}";

            // Replace single quotes with double quotes to make it valid JSON (if necessary)
            jsonContent = jsonContent.replace("'", "\"");

            // Parse the corrected JSON string
            return parseRawJson(jsonContent);
        } else {
            // If no header found, try parsing the response as raw JSON content
//            System.out.println("Header not found, parsing raw JSON...");
            return parseRawJson(response);  // Directly parse the raw JSON content
        }
    }

    // Helper method to directly parse raw JSON if no header is found
    public static JsonNode parseRawJson(String jsonString) {
        // Remove leading/trailing spaces if any
        jsonString = jsonString.trim();

        // Initialize ObjectMapper to parse the JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the string into a JSON object
//            System.out.println("Raw JSON Content: " + jsonString);
            JsonNode jsonNode = mapper.readTree(jsonString);
//            System.out.println("Parsed JSON Node: " + jsonNode);
            return jsonNode;
        } catch (Exception e) {
            // If an error occurs during parsing, print the error
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        // Example input where header is missing, but JSON exists
        String responseWithRawJson = "{\"role\": \"user_proxy\", \"textContent\": \"jvm\"}";

        JsonNode jsonNode = parse(responseWithRawJson);
        if (jsonNode != null) {
            System.out.println("Final Parsed JSON (Raw): " + jsonNode.toString());
        } else {
            System.out.println("Failed to parse JSON or no valid JSON content found.");
        }

        // Example input with header and JSON
        String responseWithHeader = "<|start_header_id|>some_header<|end_header_id|> {\"role\": \"user_proxy\", \"textContent\": \"jvm\"}";

        JsonNode jsonNodeWithHeader = parse(responseWithHeader);
        if (jsonNodeWithHeader != null) {
            System.out.println("Final Parsed JSON (With Header): " + jsonNodeWithHeader.toString());
        } else {
            System.out.println("Failed to parse JSON or no valid JSON content found.");
        }

        // Example input with empty JSON
        String responseEmptyJson = "<|start_header_id|>some_header<|end_header_id|> {}";

        JsonNode emptyJsonNode = parse(responseEmptyJson);
        if (emptyJsonNode != null) {
            System.out.println("Final Parsed JSON (Empty): " + emptyJsonNode.toString());
        } else {
            System.out.println("No valid JSON content found in the response.");
        }
    }


    public static void debugLogger(String key,String prompt) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        JsonNode node = objectMapper.createObjectNode();
        ((ObjectNode) node).put(key, prompt);
        try {
            logger.info("Debug Message: {}", objectMapper.writeValueAsString(node));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
