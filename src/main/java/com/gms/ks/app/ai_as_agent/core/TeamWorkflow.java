package com.gms.ks.app.ai_as_agent.core;

import com.gms.ks.app.ai_as_agent.model.AiAgent;
import com.gms.ks.app.ai_as_agent.model.Team;
import com.gms.ks.app.ai_as_agent.service.AiTeamService;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class TeamWorkflow {


    @Autowired
    AiTeamService aiTeamService;

    @Value("{iteration.limit: 2 }")
    int iterationLimit;

    @Value("{spring.ai.ollama.chat.options.model: llama3.2:1b}")
    String model;


    static String COORD_SYS_PRMPT = """
            You are the cordinator of role play game. The following roles are available:
            {roles}.
            the following roles are available: {role_des}. 
            If the task  needs assistance from a human user (e.g., providing feedback, preferences, or the task is stalled),
              you should select the user_proxy role to provide the necessary information.
              Read the following conversation. Then select the next role from {participants} to play. Only return the role.
            """;
    static String ROLE_SELECTOR_SYS_PROMPT = """
             Read the above conversation. Then select the next role from {participants} to play. Only return the role.
            """;


    public Team createNewTeamInstance(String name, List<AiAgent> teamagents) {
        TeamContext context = new TeamContext(name);
//        create user proxy
        creteUserProxyAgent();
//        create team cordinator
        AiAgent teamCordinator = createTeamCordinator();
//        create tem cordinator prompt
        PromptTemplate roleSelecterSysPromptTem = new PromptTemplate(ROLE_SELECTOR_SYS_PROMPT);
        roleSelecterSysPromptTem.add("participants", "");
        String roleSelector = roleSelecterSysPromptTem.create().getContents();
//        construct the team instance
        Team team = new Team(name, teamagents, teamCordinator, roleSelector);
//
        return team;
    }

    private static void creteUserProxyAgent() {
        createTeamAgent("user_proxy", "An agent that can represent a human user through an input function.", "a human user that should be consulted only when the assistant_agent is unable to verify the information provided by the agent");
    }

    public AiAgent createTeamCordinator() {
        AiAgent teamCord = new AiAgent();
        teamCord.setName("Team Operator");
        teamCord.setDescription("A team that performs deep research using web searches, verification, and summarization.");
        teamCord.setModel(model);
        PromptTemplate promptTemplate = new PromptTemplate(COORD_SYS_PRMPT);
        promptTemplate.add("roles", "");
        promptTemplate.add("role_des", "");
        promptTemplate.add("participants", "");
        Prompt prompt = promptTemplate.create();
        String sysPrompt = prompt.getContents();
        teamCord.setSystemMessage(sysPrompt);
        return teamCord;
    }


    public void invoke(Team team) {


// create the selector agent
        createResearchTeam();
    }

    private void createResearchTeam() {
        List<AiAgent> aiAgents = new ArrayList<>();
//        create list of Agents & add to the Team for now
        AiAgent researcher = createTeamAgent("researcher", "A research assistant that performs web searches and analyzes information", "You are a research assistant focused on finding accurate information." + "Break down complex queries into Simple & analyse the queries & give back the response.\\n        " + "Always verify information in multiple iterations when possible.\\n        " + "When you find relevant information, explain why it's relevant and how it connects to the query. " + "When you get feedback from the a verifier/other agent, act on the feedback and make progress.");
        AiAgent reviewer = createTeamAgent("reviewer", "A verification specialist who ensures research quality and completeness", "You are a research verification specialist.\n        " + "Your role is to:\n        1. Verify that contents are effective and suggest improvements if needed\n        " + "2. Explore drill downs where needed e.g, if the answer is likely in a link in the returned search results, suggest clicking on the link\n        " + "3. Suggest additional angles or perspectives to explore. Be judicious in suggesting new paths to avoid scope creep or wasting resources, " + "if the task appears to be addressed and we can provide a report, do this and respond with \"TERMINATE\".\n        " + "4. Track progress toward answering the original question\n        5. When the research is complete, provide a detailed summary in markdown format\n        \n       " + " For incomplete research, end your message with \"CONTINUE RESEARCH\". \n        " + "For complete research, end your message with APPROVED.\n        \n        " + "Your responses should be structured as:\n        - Progress Assessment\n        - Gaps/Issues (if any)\n       " + " - Suggestions (if needed)\n        - Next Steps or Final Summary");
        aiAgents.add(researcher);
        aiAgents.add(reviewer);

    }

    private static AiAgent createTeamAgent(String name, String description, String systemMessage) {
        AiAgent aiAgent = new AiAgent();
        aiAgent.setName(name);
        aiAgent.setDescription(description);
        aiAgent.setSystemMessage(systemMessage);
        return aiAgent;
    }

}
