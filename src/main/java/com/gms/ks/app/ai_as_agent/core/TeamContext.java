package com.gms.ks.app.ai_as_agent.core;

import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class TeamContext {
    String teamName;

    public TeamContext(String teamName) {
        this.teamName = teamName;
    }

    List<Message> messageList = new ArrayList<>();

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
