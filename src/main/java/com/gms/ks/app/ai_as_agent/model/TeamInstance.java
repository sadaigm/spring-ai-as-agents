package com.gms.ks.app.ai_as_agent.model;

import com.gms.ks.app.ai_as_agent.core.TeamContext;

import java.util.List;
import java.util.UUID;

public class TeamInstance {

    String id;

    Team team;

    int currentRound = 0;

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    TeamContext teamContext;
    public TeamInstance(Team team) {
        this.team = team;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TeamContext getTeamContext() {
        return teamContext;
    }

    public void setTeamContext(TeamContext teamContext) {
        this.teamContext = teamContext;
    }
}
