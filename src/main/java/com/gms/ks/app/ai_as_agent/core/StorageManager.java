package com.gms.ks.app.ai_as_agent.core;

import com.gms.ks.app.ai_as_agent.model.Team;
import com.gms.ks.app.ai_as_agent.model.TeamInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {

    public static Map<String,TeamContext> teamContextMap = new HashMap<>();

    public static Map<String, TeamInstance> teamInstanceMap = new HashMap<>();

    public static Map<String, Team> teamMap = new HashMap<>();


}
