package com.gms.ks.app.ai_as_agent.service;

import com.gms.ks.app.ai_as_agent.model.AiAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiService {

    Logger logger
            = LoggerFactory.getLogger(AiService.class);

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }



    public String invoke(AiAgent agent, String query){
        List<Message> messages = new ArrayList<>();
        SystemMessage systemMessage = new SystemMessage(agent.getSystemMessage());
        UserMessage userMessage = new UserMessage(query);
        messages.add(systemMessage);
        messages.add(userMessage);
        return invokeAgent(agent, messages);
    }

    private String invokeAgent(AiAgent agent, List<Message> messages) {
        OllamaOptions options = OllamaOptions.builder()
                .model(agent.getModel())
                .build();

        Prompt prompt = new Prompt(messages, options);
        logger.info("Prompt: {}", prompt.getContents());
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        return chatResponse.getResult().getOutput().getContent();
    }

}
