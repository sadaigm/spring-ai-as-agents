package com.gms.ks.app.ai_as_agent.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gms.ks.app.ai_as_agent.model.AiAgent;
import com.gms.ks.app.ai_as_agent.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.gms.ks.app.ai_as_agent.utils.Constants.simpleAgent;

@RestController
public class AgentController {


    @Autowired
    AiService aiService;


    @GetMapping(path = "/agent")
    public String getSimpleAgent(@RequestParam( name = "query") String query) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        AiAgent aiAgent = mapper.readValue(simpleAgent, AiAgent.class);
       return aiService.invoke(aiAgent, query);
    }

}
