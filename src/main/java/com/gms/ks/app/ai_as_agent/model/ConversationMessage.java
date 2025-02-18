package com.gms.ks.app.ai_as_agent.model;

public class ConversationMessage {
    private String content;
    private String role;

    Long timestamp;

    public ConversationMessage(String content, String role) {
        this.timestamp = System.currentTimeMillis();
        this.content = content;
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
