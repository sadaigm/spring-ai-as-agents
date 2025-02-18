package com.gms.ks.app.ai_as_agent.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.model.Media;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AgentMessage extends AssistantMessage {

    String role;

    public AgentMessage(String Role, String content) {
        super(content);
        this.role = Role;
    }
    public AgentMessage(String content) {
        super(content);
    }

    public AgentMessage(String content, Map<String, Object> properties) {
        super(content, properties);
    }

    public AgentMessage(String content, Map<String, Object> properties, List<ToolCall> toolCalls) {
        super(content, properties, toolCalls);
    }

    public AgentMessage(String content, Map<String, Object> properties, List<ToolCall> toolCalls, List<Media> media) {
        super(content, properties, toolCalls, media);
    }

    @Override
    public String getText() {
//            return "AgentMessage from selected role : "+role+", textContent : "+super.getText();
        return "Conversation Message "+role+" : "+super.getText()+"\n";
//        return getRoleTagContent(super.getText());
    }

    public String getRoleTagContent(String content) {
        return "<"+role.toUpperCase()+">" + content+ "</"+role.toUpperCase()+">";
    }

    @Override
    public String toString() {
        return "AgentMessage{" +
                "role='" + role + '\'' +
                ", media=" + media +
                ", messageType=" + messageType +
                ", textContent='" + textContent + '\'' +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AgentMessage that = (AgentMessage) o;
        return super.equals(o) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
