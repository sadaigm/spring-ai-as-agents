package com.gms.ks.app.ai_as_agent.core;

import com.gms.ks.app.ai_as_agent.model.AiAgent;
import com.gms.ks.app.ai_as_agent.model.Team;
import com.gms.ks.app.ai_as_agent.service.AiTeamService;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.gms.ks.app.ai_as_agent.utils.Constants.TEAM_OPERATOR;
import static com.gms.ks.app.ai_as_agent.utils.Constants.USER_PROXY;

@Component
public class TeamManager {

    @Autowired
    AiTeamService aiTeamService;

    @Value("${iteration.limit:2}")
    Integer iterationLimit;

    @Value("${spring.ai.ollama.chat.options.model:llama3.2:1b}")
    String model;

    public record Response(String role, String content) {
    }

    static String COORD_SYS_PRMPT = """
            <SYSTEM PROMPT>
            You are the cordinator of role play game. The following roles are available:
            {roles}.
            following are the roles & responsibilities: {role_des}.
            If the task  needs assistance from a human user (e.g., providing feedback, preferences, or the task is stalled),
              you should select the {user_proxy} role only to provide the necessary information or use have some questions.
              Read the following conversation. Then select the next role from {participants} to play. Only return the role name.
             </SYSTEM PROMPT>
            """;
    static String ROLE_SELECTOR_SYS_PROMPT = """
             
             <SYSTEM PROMPT>
             Read the above conversation DO NOT REPLY TO THE LAST ROLE. Choose the next role from [ {participants} ] to play using round robin logic. Only return the role.             
            """;


    public Team createNewTeamInstance(String name, List<AiAgent> teamagents) {
        TeamContext context = new TeamContext(name);
//        create user proxy
        AiAgent userProxyAgent = creteUserProxyAgent();
        teamagents.add(userProxyAgent);
//        create team cordinator
        AiAgent teamCordinator = createTeamCordinator(teamagents);
//        create tem cordinator prompt
        BeanOutputConverter<Response> beanOutputConverter =
                new BeanOutputConverter<>(Response.class);
        PromptTemplate roleSelecterSysPromptTem = new PromptTemplate(ROLE_SELECTOR_SYS_PROMPT);
        String participants = teamagents.stream().map(AiAgent::getName).reduce((a, b) -> a + ", " + b).orElse("");
        roleSelecterSysPromptTem.add("participants", participants);
        roleSelecterSysPromptTem.add("format", beanOutputConverter.getFormat());
        String roleSelector = roleSelecterSysPromptTem.create().getContents()+"Generate a valid JSON object with the following key-value pairs:\n" +
                "\n" +
                "role: with the value \"<ROLE>\"\n" +
                "textContent: with the value \"<ANSWER>\"\n" +
                "Ensure that the JSON String output is properly formatted, with all keys enclosed in double quotes, and the values are also valid strings wrapped in double quotes.DO NOT MARKDOWN JUS RETURN JSON STRING  \n" +
                "example valid output: {\n" +
                "  \"role\": \"<ROLE>\",\n" +
                "  \"textContent\": \"<ANSWER>\"\n" +
                "}\n" +
                "end of example\n </SYSTEM PROMPT>";
//        construct the team instance
        Team team = new Team(name, teamagents, teamCordinator, roleSelector);
//
        return team;
    }

    private static AiAgent creteUserProxyAgent() {
        return createTeamAgent(USER_PROXY, "An agent that can represent a human user through an input function.", "a human user that should be consulted only when the assistant_agent is unable to verify the information provided by the agent");
    }

    public AiAgent createTeamCordinator(List<AiAgent> agents) {
        AiAgent teamCord = new AiAgent();
        teamCord.setName(TEAM_OPERATOR);
        teamCord.setDescription("A team that performs deep research using web searches, verification, and summarization.");
        teamCord.setModel(model);
        BeanOutputConverter<Response> beanOutputConverter =
                new BeanOutputConverter<>(Response.class);
        PromptTemplate promptTemplate = new PromptTemplate(COORD_SYS_PRMPT);
        List<String> roles = agents.stream().map(AiAgent::getName).toList();
        String participants = roles.stream().reduce((a, b) -> a + ", " + b).orElse("");
        promptTemplate.add("roles", participants);
        promptTemplate.add("format", beanOutputConverter.getFormat());
        promptTemplate.add(USER_PROXY, USER_PROXY);
        agents.stream().map(a -> {
            StringBuilder roleDes = new StringBuilder();
            roleDes.append(a.getName()).append(": ").append(a.getDescription()).append("\n");
            return roleDes.toString();
        }).reduce((a, b) -> a + b).ifPresent(a -> promptTemplate.add("role_des", a));
        promptTemplate.add("participants", participants);
        Prompt prompt = promptTemplate.create();
        String sysPrompt = prompt.getContents();
                ;
        teamCord.setSystemMessage(sysPrompt);
        return teamCord;
    }


    public Team createStoryTeam() {
        // create the selector agent
        List<AiAgent> researchTeamAgents = createResearchTeamAgents();
        Team storyTeam = createNewTeamInstance("Story Teller", researchTeamAgents);
        return storyTeam;
    }

    public  static int WORD_LIMIT = 1000;

    private List<AiAgent> createResearchTeamAgents() {
        List<AiAgent> aiAgents = new ArrayList<>();
//        create list of Agents & add to the Team for now
        String researcher_sys_prompt = "You are a research assistant focused on finding accurate information."
                + "Break down complex queries into Simple & analyse the queries & give back the response in " +
                WORD_LIMIT+" words.\\n        "
                + "Always verify information in multiple iterations when possible.\\n        "
                + "When you find relevant information, explain why it's relevant and how it connects to the query. "
                + "When you get feedback from the a verifier/other agent, act on the feedback and make progress." +
                "once the research is approved, provide a research summary in the response.\\n        "+
                "DO NOT SEND BACK THE YOUR ANALYSIS CONTENT. JUST SEND THE RESEARCH CONTENT. DO NOT EXCEED THE WORD LIMITS. ";
        AiAgent researcher = createTeamAgent("researcher", "A research assistant that performs web searches and analyzes information. ", researcher_sys_prompt);
        String verifier_sys_prompt = "You are a research verification specialist.\n        "
                + "Your role is to:\n        1. Verify that contents are effective and suggest improvements if needed with max "+WORD_LIMIT+" words\n        "
                + "2. Explore drill downs where needed e.g, if the answer is likely in a link in the returned search results, suggest clicking on the link\n        "
                + "3. Suggest additional angles or perspectives to explore. Be judicious in suggesting new paths to avoid scope creep or wasting resources, "
                + "if the task appears to be addressed and we can provide a report.\n        "
                + "4. Track progress toward answering the original question\n      " +
                "  5. When the research is complete, provide a detailed summary max 250 words in markdown format\n        \n       "
                +"DO NOT SEND BACK THE REVIEW CONTENT IN THE RESPONSE. \n        "
                + " For incomplete research, end your message with \"CONTINUE RESEARCH\". \n        " + "For complete research, end your message with APPROVED.\n     " +
                 "Your responses should be structured as:\n        - Progress Assessment\n        - Gaps/Issues (if any)\n       "
                + " - Suggestions (if needed)\n        - Next Steps or Final Summary. DO NOT EXCEED THE WORD LIMITS. ";
        AiAgent reviewer = createTeamAgent("reviewer", "A verification specialist who ensures research quality and completeness", verifier_sys_prompt);
        aiAgents.add(researcher);
        aiAgents.add(reviewer);
        return aiAgents;
    }

    private static AiAgent createTeamAgent(String name, String description, String systemMessage) {
        AiAgent aiAgent = new AiAgent();
        aiAgent.setName(name);
        aiAgent.setDescription(description);
        aiAgent.setSystemMessage(systemMessage);
        return aiAgent;
    }
}
