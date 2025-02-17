package com.gms.ks.app.ai_as_agent.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gms.ks.app.ai_as_agent.model.*;
import com.gms.ks.app.ai_as_agent.service.AiTeamService;
import com.gms.ks.app.ai_as_agent.utils.OllamaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gms.ks.app.ai_as_agent.core.StorageManager.*;
import static com.gms.ks.app.ai_as_agent.utils.Constants.USER_PROXY;
import static com.gms.ks.app.ai_as_agent.utils.OllamaParser.debugLogger;


@Component
public class TeamWorkflow {


    Logger logger
            = LoggerFactory.getLogger(TeamWorkflow.class);

    @Autowired
    AiTeamService aiTeamService;

    @Autowired
    TeamManager teamManager;

    public void createTeams(){

        Team storyTeam = teamManager.createStoryTeam();
        teamMap.computeIfAbsent(storyTeam.getName(), t -> storyTeam);
//        create other teams & add the list of teams
    }

    public void invokeTeam(String teamId, String teamName, String query){
//        Optional<Team> teamOptional = teamList.stream().filter(team -> teamName.equals(team.getName())).findFirst();
        TeamInstance teamInstance = null;
        if(teamId !=null & teamInstanceMap.containsKey(teamId)){
            teamInstance = teamInstanceMap.get(teamId);
            if(teamInstance!=null){
                callTeam(teamInstance);
            }
        } else{
            teamInstance = createTeamInstance(teamName, query);
        }
    }

    public TeamInstance createTeamInstance(String teamName, String query) {
        createTeams();
//        Optional<Team> teamOptional = teamList.stream().filter(team -> teamName.equals(team.getName())).findFirst();
        ObjectMapper mapper = new ObjectMapper();
        Team teamOptional = teamMap.get(teamName);
        if(teamOptional != null){
            try {
                String s = mapper.writeValueAsString(teamOptional);
                Team team = mapper.readValue(s, Team.class);
                team.setTopicFromUser(query);
                TeamInstance teamInstance = new TeamInstance(team);
                callTeam(teamInstance);
                teamInstanceMap.put(teamInstance.getId(), teamInstance);
                return teamInstance;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void callTeam(TeamInstance teamInstance) {
        Team team = teamInstance.getTeam();
        // start -> cordinator
        TeamContext teamContext = teamContextMap.computeIfAbsent(teamInstance.getId(), t -> new TeamContext(teamInstance.getId()));
        teamContext.getConversationMessageList().add(new ConversationMessage(USER_PROXY, team.getTopicFromUser()));
//        List<Message> messages = prepareRequest(team, team.getCordinator().getName(), teamContext);
        teamInstance.setTeamContext(teamContext);
        callAgent(team.getCordinator(), teamInstance);
//        String response = aiTeamService.invoke(team.getCordinator(), messages);
//        logger.info("Response: {}", response);
//        JsonNode jsonNode = OllamaParser.parse(response);
//        logger.info("Response Json: {}", jsonNode);
//        create agent Message from response



    }


    private void callAgent(AiAgent agent, TeamInstance teamInstance) {
        TeamContext teamContext = teamInstance.getTeamContext();
        Team team = teamInstance.getTeam();
        if(agent.getName().equals(USER_PROXY)){
            logger.debug("Request: {} : {}",agent.getName(), teamContext.getTopicmessageList());
            if(teamInstance.getCurrentRound()<team.getNoOfRounds()){
                logger.info("The agents are looking for user proxy input, the conversation is continued");
                teamInstance.setCurrentRound(teamInstance.getCurrentRound()+1);
                UserProxyMessage agentResponse = new UserProxyMessage( "CONTINUE THE CONVERSATION");
                teamContext.messageList.add(agentResponse);
                teamContext.getConversationMessageList().add(new ConversationMessage(agent.getName(), "CONTINUE THE CONVERSATION"));
                callAgent(team.getCordinator(), teamInstance);
            }
            else {
                logger.info("The agents are looking for user proxy input, the conversation is ended");
                return;
            }
        } else if (agent.getName().equals(team.getCordinator().getName())){
//          role, textContent
            logger.debug("Request: {} : {}",agent.getName(), teamContext.getMessageList());
            prepareRequest(team, team.getCordinator().getName(), teamContext);
            String response = aiTeamService.invokeMock(agent, teamContext.getMessageList());
            teamContext.messageList.add(new AgentMessage((agent.getName()),response));
            teamContext.getConversationMessageList().add(new ConversationMessage(agent.getName(), response));
//            logger.info("Response: {} : {}",agent.getName(), response);
            debugLogger("agent", agent.getName());
            debugLogger("Response", response);
            JsonNode jsonNode = OllamaParser.parse(response);
            String role = jsonNode.get("role").asText();
//            find the next agent's system prompt
            Optional<AiAgent> optionalAiAgent = teamInstance.getTeam().getAgents().stream().filter(aiAgent -> aiAgent.getName().equals(role)).findFirst();

            if(optionalAiAgent.isPresent()){
                callAgent(optionalAiAgent.get(), teamInstance);
            }
            else{
                logger.info("No agent found for role: {}", role);
            }
        }
        else{
//           if the request is for the agent
            logger.info("Request: {}", agent.getName());
            logger.debug("Request: {} : {} : {}", agent.getName(), teamContext.getMessageList());
            teamContext.getTopicmessageList().forEach(message -> {
                logger.debug("Request: {} : {} : {}", agent.getName(), message.getRole(), message.getText());
            });
            List<Message> agentMessages = prepareRequest(team, agent.getName(), teamContext);
            agentMessages.addAll(teamContext.getTopicmessageList());
            String response = aiTeamService.invoke(agent, agentMessages);
            AgentMessage agentResponse = new AgentMessage((agent.getName()), response);
            teamContext.messageList.add(agentResponse);
            teamContext.getConversationMessageList().add(new ConversationMessage(agent.getName(), response));
            teamContext.getTopicmessageList().add(agentResponse);
            if(teamInstance.getCurrentRound()>1 && response !=null && (response.toUpperCase().contains("TERMINATE") || response.toUpperCase().contains("SIGNAL_ZERO")) ){
                            logger.info("Response: {} : {}",agent.getName(), response);
                logger.info("Request is terminated by the agent, end of the conversation");
                teamContext.getConversationMessageList().add(new ConversationMessage(agent.getName(), "Request is terminated by the agent, end of the conversation"));
                return;
            }
            debugLogger("agent", agent.getName());
//            debugLogger("Response", response);
            callAgent(team.getCordinator(), teamInstance);
        }

    }

    private List<Message> prepareRequest(Team team, String role, TeamContext teamContext) {
//        check if it's cordinator or agent role to ge the agent role value is same as name of the agent
        logger.info("prepareRequest: {}", role);
        AiAgent cordinator = team.getCordinator();
        if(role.equals(cordinator.getName())){
            List<Message> agentMessages = new ArrayList<>();
            return prepareCordRequest(cordinator, team, agentMessages ,teamContext);
        }
        else{
            Optional<AiAgent> optionalAiAgent = team.getAgents().stream().filter(aiAgent -> aiAgent.getName().equals(role)).findFirst();
            if(optionalAiAgent.isPresent()){
                AiAgent aiAgent = optionalAiAgent.get();
//                for user_proxy user will trigger the request
                if(!aiAgent.getName().equals(USER_PROXY)){
                    List<Message> agentMessages = new ArrayList<>();
                    List<Message> messageList = prepareAgentRequest(aiAgent, team.getTopicFromUser(), agentMessages,teamContext);
                    logger.debug("agent: {} , {}, {}", aiAgent.getName(), agentMessages,team.getTopicFromUser());
                    return messageList;
                }

            }
        }
        return Collections.emptyList();
    }

    private List<Message> prepareCordRequest(AiAgent aiAgent, Team team,List<Message> messages,TeamContext teamContext) {
        logger.debug("prepareCordRequest: {} , {}", aiAgent.getName(), messages);
        initialMessage(aiAgent, team.getTopicFromUser(), messages,teamContext);
        String cordinatorPrompt = team.getCordinatorPrompt();
        SystemMessage systemMessage = new SystemMessage(cordinatorPrompt);
        messages.add(systemMessage);
        return messages;
    }

    private void initialMessage(AiAgent aiAgent, String query, List<Message> messages, TeamContext teamContext) {
        logger.debug("initialMessage: {} , {}", aiAgent.getName(), messages);

        prepareSysMessage(aiAgent, messages);
        if(teamContext.messageList.isEmpty()){
            prepareAgentMessage(USER_PROXY, query, messages, teamContext);
        }

    }

    private List<Message> prepareAgentRequest(AiAgent aiAgent, String query,List<Message> messages, TeamContext teamContext) {
        logger.debug("prepareRequest: {} , {}", aiAgent.getName(), messages);
        initialAgentMessage(aiAgent, query, messages, teamContext);
        return messages;
    }

    private void initialAgentMessage(AiAgent aiAgent, String query, List<Message> messages, TeamContext teamContext) {
        logger.debug("initialMessage: {} , {}", aiAgent.getName(), messages);
        prepareSysMessage(aiAgent, messages);
        prepareAgentMessage(USER_PROXY, query, messages, teamContext);
    }
    private void prepareAgentMessage(String role, String query, List<Message> messages,TeamContext teamContext) {
        logger.info("prepareAgentMessage: role:  {} , query: {}", role, query);
        if(role.equals(USER_PROXY)){
            UserProxyMessage userMessage = new UserProxyMessage(query);
            messages.add(userMessage);
//            teamContext.getTopicmessageList().add(userMessage);
            teamContext.getMessageList().add(userMessage);
        }
        else{
            AgentMessage agentMessage = new AgentMessage(role, query);
            messages.add(agentMessage);
            teamContext.getTopicmessageList().add(agentMessage);
        }

    }

    private void prepareSysMessage(AiAgent aiAgent, List<Message> messages) {
        logger.info("prepareSysMessage: {} ", aiAgent.getName());
        SystemMessage systemMessage = new SystemMessage(aiAgent.getSystemMessage());
        messages.add(systemMessage);
    }


    public TeamInstance getTeamInstance(String teamID) {
        TeamContext teamContext = teamContextMap.get(teamID);
        TeamInstance teamInstance = teamInstanceMap.get(teamID);
        return teamInstance;
    }
}
