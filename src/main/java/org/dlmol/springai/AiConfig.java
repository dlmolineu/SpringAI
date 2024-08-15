package org.dlmol.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    @Bean
    VectorStore vectorStore(EmbeddingModel model) {
        return new SimpleVectorStore(model);
    }

    @Value("classpath:/prompts/BT Travel Reimbursement Guidelines.txt")
    private Resource rulesResource;

    @Bean
    ApplicationRunner go(VectorStore vectorStore) {
        long startMs = System.currentTimeMillis();
        return args -> {
            System.out.println("Vector store: " + vectorStore);
            List<Document> documents = new TikaDocumentReader(rulesResource).get();
            List<Document> split = new TokenTextSplitter().split(documents);
            vectorStore.add(split);
            System.out.println("Done loading VectorStore in " + (System.currentTimeMillis() - startMs) + " ms.");
        };
    }
}
