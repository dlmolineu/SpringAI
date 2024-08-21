package org.dlmol.springai;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {

    private static final Logger logger = LoggerFactory.getLogger(AskController.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;


    @Value("classpath:/prompts/BT Travel Reimbursement Guidelines.txt")
    private Resource rulesResource;

    public AskController(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @PostMapping("/rulesAsk")
    public Answer askRules(@RequestParam String question) {
        if (StringUtils.isBlank(question)) {
            final String missingQuestionMsg = "Question is missing!";
            logger.debug(missingQuestionMsg);
            return new Answer(missingQuestionMsg);
        }

        final Answer answer = chatClient.prompt()
                .system(systemSpec -> {
                    systemSpec
                            .text(rulesResource);
                })
                .user(question)
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .call()
                .entity(Answer.class);
        logger.debug("For question \"{}\", answer: \"{}\"", question, answer.answer());
        return answer;
    }

    @PostMapping("/ask")
    public Answer ask(@RequestParam String question) {
        if (StringUtils.isBlank(question)) {
            final String missingQuestionMsg = "Question is missing!";
            logger.debug(missingQuestionMsg);
            return new Answer(missingQuestionMsg);
        }
        final String questionText = question;
        final Answer answer = chatClient.prompt()
                .user(questionText)
                .call()
                .entity(Answer.class);
        logger.debug("For question \"{}\", answer: \"{}\"", questionText, answer.answer());
        return answer;
    }
}
