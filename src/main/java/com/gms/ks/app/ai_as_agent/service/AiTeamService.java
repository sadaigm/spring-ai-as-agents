package com.gms.ks.app.ai_as_agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gms.ks.app.ai_as_agent.model.AiAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gms.ks.app.ai_as_agent.utils.Constants.TEAM_OPERATOR;
import static com.gms.ks.app.ai_as_agent.utils.Constants.getNextRole;
import static com.gms.ks.app.ai_as_agent.utils.OllamaParser.debugLogger;

@Service
public class AiTeamService {
    Logger logger
            = LoggerFactory.getLogger(AiTeamService.class);
    private final ChatClient chatClient;

    public AiTeamService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public String invoke(AiAgent agent, List<Message> messages){

        OllamaOptions options = OllamaOptions.builder()
                .model(agent.getModel())
                .build();
        Prompt prompt = new Prompt(messages, options);
        debugLogger("prompt", prompt.getContents());
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        return chatResponse.getResult().getOutput().getContent();
//        return null;
    }

    static String[] rolesOrder = { "researcher", "reviewer", "user_proxy"} ;
static String currentRole = "user_proxy";
    public String invokeMock(AiAgent agent, List<Message> messages) {
        if (agent.getName().equals(TEAM_OPERATOR)) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonNode = mapper.createObjectNode();

            String nextRole = getNextRole(currentRole, rolesOrder);
            currentRole = nextRole;
            jsonNode.put("content", "Mocked response");
            jsonNode.put("role", nextRole);
            String response = null;
            try {
                response = mapper.writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return response;
        } else {
            return "Mocked response";
        }
    }

}
