package com.thehecklers.multi_ai_demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiController {
    private final ChatClient ollamaClient;
    private final ChatClient oaiClient;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public MultiController(@Qualifier("ollamaClient") ChatClient oChatClient,
                           @Qualifier("openaiChatClient") ChatClient oaiClient) {
        this.ollamaClient = oChatClient;
        this.oaiClient = oaiClient;
    }


    @GetMapping
    public String getAnswer(@RequestParam(defaultValue = "Tell me a joke") String message) {
        var content = ollamaClient
                .prompt()
                .user(message)
                .call()
                .content();

        logger.info("\n\n>>>> Content: " + content + "\n\n");

        var critique = oaiClient.prompt()
                .user(String.format("""
                        The following is a joke created by another AI. Please rate it on a scale of 1-5 
                        and explain why you assigned it that rating. Finally, provide suggestions for 
                        improving the joke or replace it with a joke on a similar topic: %s
                        """, content))
                .call()
                .content();

        logger.info("\n\n>>>> Critique: " + critique + "\n\n");

        return "Original joke: " + content + "\n\nCritique: " + critique;
    }
}
