package com.gms.ks.app.ai_as_agent.service;

import com.gms.ks.app.ai_as_agent.core.TeamContext;
import com.gms.ks.app.ai_as_agent.model.AiAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiTeamService {
    private final ChatClient chatClient;

    public AiTeamService(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    public String invoke(AiAgent agent, TeamContext teamContext , String query) {
        UserMessage userMessage = new UserMessage(query);
        List<Message> messages = teamContext.getMessageList();
        messages.add(userMessage);
        OllamaOptions options = OllamaOptions.builder().model(agent.getModel())
                .build();
        ChatResponse chatResponse = chatClient.prompt(new Prompt(messages, options)).call().chatResponse();
        return chatResponse.getResult().getOutput().getContent();
    }
}
