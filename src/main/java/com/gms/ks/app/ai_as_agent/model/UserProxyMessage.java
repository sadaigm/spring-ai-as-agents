package com.gms.ks.app.ai_as_agent.model;

import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gms.ks.app.ai_as_agent.utils.Constants.USER_PROXY;

public class UserProxyMessage  extends UserMessage {

    String role = USER_PROXY;

    public UserProxyMessage(String textContent) {
        super(textContent);
    }

    public UserProxyMessage(Resource resource) {
        super(resource);
    }

    public UserProxyMessage(String textContent, List<Media> media) {
        super(textContent, media);
    }

    public UserProxyMessage(String textContent, Media... media) {
        super(textContent, media);
    }

    public UserProxyMessage(String textContent, Collection<Media> mediaList, Map<String, Object> metadata) {
        super(textContent, mediaList, metadata);
    }

    public UserProxyMessage(MessageType messageType, String textContent, Collection<Media> media, Map<String, Object> metadata) {
        super(messageType, textContent, media, metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserProxyMessage that = (UserProxyMessage) o;
        return Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }

    @Override
    public String getText() {
        return "Query from the User : "+super.getText()+"\n";
//        return getRoleTagContent(super.getText());
    }

    public String getRoleTagContent(String content) {
        return "<USER_QUERY>" + content+ "</USER_QUERY>";
    }

    @Override
    public String toString() {
        return "UserProxyMessage{" +
                "role='" + role + '\'' +
                ", media=" + media +
                ", messageType=" + messageType +
                ", textContent='" + textContent + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
