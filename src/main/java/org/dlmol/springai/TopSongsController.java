package org.dlmol.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopSongsController {
    private final ChatClient chatClient;

    @Value("classpath:/prompts/topsongs.st")
    private Resource topSongsPrompt;

    public TopSongsController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/topsongs")
    public String getTopSongs(@RequestParam("year") String year) {
        return chatClient.prompt()
                .user(userSpec -> {
                    userSpec
                            .text(topSongsPrompt)
                            .param("year", year);
                })
                .call()
                .content();
    }
}
