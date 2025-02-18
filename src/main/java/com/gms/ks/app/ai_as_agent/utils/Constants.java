package com.gms.ks.app.ai_as_agent.utils;

import com.gms.ks.app.ai_as_agent.model.Team;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final String USER_PROXY = "user_proxy";
    public static final String TEAM_OPERATOR = "Team Operator";

    public static final String simpleAgent = "{\n" +
            "    \"name\": \"assistant_agent\",\n" +
            "    \"description\": \"An agent that provides assistance with ability to use tools.\",\n" +
            "    \"type\": \"agent\",\n" +
            "     \"model\": \"llama3.2:1b\",\n" +
            "     \"systemMessage\": \"You are a helpful assistant. Solve tasks carefully. When done, say TERMINATE.\"\n" +
            "}";


    public static String getNextRole(String currentRole, String[] rolesOrder) {
        // Find the index of the current role in the rolesOrder array
        for (int i = 0; i < rolesOrder.length; i++) {
            if (rolesOrder[i].equals(currentRole)) {
                // Use modulo arithmetic to cycle through the roles array
                int nextIndex = (i + 1) % rolesOrder.length;
                return rolesOrder[nextIndex];
            }
        }
        // If the role is not found, return null or some default value
        return null;
    }
}
