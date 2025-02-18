package com.gms.ks.app.ai_as_agent.controller;

import com.gms.ks.app.ai_as_agent.core.TeamWorkflow;
import com.gms.ks.app.ai_as_agent.model.TeamInstance;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamController {

    @Autowired
    TeamWorkflow teamWorkflow;

    @PostMapping(path = "/create-team")
    public TeamInstance createTeams(@RequestParam(name = "userInput", required = true) String userInput, @RequestParam(name = "teamName", required = true) String teamName) {
        if (StringUtils.isBlank(userInput) || StringUtils.isBlank(teamName)) {
            throw new IllegalArgumentException("User input and team name are required");
        }
        return teamWorkflow.createTeamInstance(teamName, userInput);
    }

    @GetMapping(path = "/start-team")
    public void callTeam(@RequestParam(name = "teamId") String teamID, @RequestParam(name = "userInput") String userInput, @RequestParam(name = "teamName") String teamName) {
        teamWorkflow.invokeTeam(teamID, teamName, userInput);
    }

    @GetMapping(path = "/get-team-instance")
    public TeamInstance getTeamInstance(@RequestParam(name = "teamId") String teamID) {
        return teamWorkflow.getTeamInstance(teamID);
    }
}
