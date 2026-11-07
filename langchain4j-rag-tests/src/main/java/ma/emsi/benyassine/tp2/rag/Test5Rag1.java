package ma.emsi.benyassine.tp2.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;

/** Compare une réponse Gemini sans document avec un assistant RAG sur infos.txt. */
public class Test5Rag1 {

    private static final String QUESTION_CHAT = "Comment s'appelle le chat de Pierre ?";

    public static void main(String[] args) {
        Path infoPath = Path.of("infos.txt");
        if (!Files.exists(infoPath)) {
            infoPath = Path.of("langchain4j-rag-tests", "infos.txt");
        }
        Document document = FileSystemDocumentLoader.loadDocument(infoPath);
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(document, embeddingStore);
        System.out.println("Le document infos.txt a été indexé en mémoire.");

        String apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("Pour obtenir les réponses RAG, configurez GOOGLE_AI_GEMINI_API_KEY dans IntelliJ.");
            return;
        }

        String modelName = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-3.5-flash");
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();

        System.out.println("Test 5 — RAG 1 (modèle : " + modelName + ")");
        System.out.println("Sans document : " + model.chat(QUESTION_CHAT));

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        System.out.println("Avec RAG : " + assistant.chat(QUESTION_CHAT));
        System.out.println("Posez d'autres questions sur infos.txt. Saisissez /quit pour terminer.");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Vous : ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String question = scanner.nextLine().trim();
                if (question.equalsIgnoreCase("/quit")) {
                    break;
                }
                if (!question.isEmpty()) {
                    System.out.println("Gemini : " + assistant.chat(question));
                }
            }
        }
    }
}
