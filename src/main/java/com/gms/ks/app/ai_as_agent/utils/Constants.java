package com.gms.ks.app.ai_as_agent.utils;

public class Constants {

    public static final String simpleAgent = "{\n" +
            "    \"name\": \"assistant_agent\",\n" +
            "    \"description\": \"An agent that provides assistance with ability to use tools.\",\n" +
            "    \"type\": \"agent\",\n" +
            "     \"model\": \"llama3.2:1b\",\n" +
            "     \"systemMessage\": \"You are a helpful assistant. Solve tasks carefully. When done, say TERMINATE.\"\n" +
            "}";
}
