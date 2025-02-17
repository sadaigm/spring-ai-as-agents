package com.gms.ks.app.ai_as_agent.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    String name;
    String type;
    String description;
    String label;
    List<AiAgent> agents = new ArrayList<>();
//Coordinator
    AiAgent cordinator;
    String cordinatorPrompt;

    Integer noOfRounds = 3;

    String topicFromUser;

    List<String> roles = new ArrayList<>();

    public Team() {
//
    }

    public Team(String name, List<AiAgent> agents, AiAgent cordinator, String cordinatorPrompt) {
        this.name = name;
        this.agents = agents;
        this.cordinator = cordinator;
        this.cordinatorPrompt = cordinatorPrompt;
        roles.add(cordinator.name);
        agents.forEach((a) -> roles.add(a.name));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<AiAgent> getAgents() {
        return agents;
    }

    public void setAgents(List<AiAgent> agents) {
        this.agents = agents;
    }

    public AiAgent getCordinator() {
        return cordinator;
    }

    public void setCordinator(AiAgent cordinator) {
        this.cordinator = cordinator;
    }

    public String getCordinatorPrompt() {
        return cordinatorPrompt;
    }

    public void setCordinatorPrompt(String cordinatorPrompt) {
        this.cordinatorPrompt = cordinatorPrompt;
    }

    public Integer getNoOfRounds() {
        return noOfRounds;
    }

    public void setNoOfRounds(Integer noOfRounds) {
        this.noOfRounds = noOfRounds;
    }

    public String getTopicFromUser() {
        return topicFromUser;
    }

    public void setTopicFromUser(String topicFromUser) {
        this.topicFromUser = topicFromUser;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
