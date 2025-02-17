package com.gms.ks.app.ai_as_agent.core;

import com.gms.ks.app.ai_as_agent.model.AgentMessage;
import com.gms.ks.app.ai_as_agent.model.ConversationMessage;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class TeamContext {
    String teamName;

    public TeamContext(String teamName) {
        this.teamName = teamName;
    }


    List<ConversationMessage> conversationMessageList = new ArrayList<>();


    List<AgentMessage> topicmessageList = new ArrayList<>();

    List<Message> messageList = new ArrayList<>();

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public List<AgentMessage> getTopicmessageList() {
        return topicmessageList;
    }

    public void setTopicmessageList(List<AgentMessage> topicmessageList) {
        this.topicmessageList = topicmessageList;
    }

    public List<ConversationMessage> getConversationMessageList() {
        return conversationMessageList;
    }
}
