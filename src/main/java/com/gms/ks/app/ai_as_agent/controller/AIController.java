package com.gms.ks.app.ai_as_agent.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AIController {
    record ActorsFilms(String actor, List<String> movies) {
    }

    private final OllamaChatModel chatModel;
    @Autowired
    public AIController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }
    @GetMapping(path = "/ai")
    public ActorsFilms callAI(String actorName) {
        BeanOutputConverter<ActorsFilms> beanOutputConverter =
                new BeanOutputConverter<>(ActorsFilms.class);

        String format = beanOutputConverter.getFormat();

        String actor = "Tom Hanks";
        if(actorName != null && !actorName.isBlank()) {
            actor = actorName;
        }

        String template = """
        Generate the filmography of 5 movies for {actor}.
        {format}
        """;
        Generation generation = chatModel.call(
                new PromptTemplate(template, Map.of("actor", actor, "format", format)).create()).getResult();

        ActorsFilms actorsFilms = beanOutputConverter.convert(generation.getOutput().getText());
        return actorsFilms;
    }

}
