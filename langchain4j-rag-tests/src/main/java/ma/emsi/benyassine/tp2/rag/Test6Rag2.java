package ma.emsi.benyassine.tp2.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.Scanner;

/** Chat avec mémoire et RAG sur le support de cours LangChain4j. */
public class Test6Rag2 {

    private static final String QUESTION_EXEMPLE =
            "Quel est l'objectif du cours 'Agents conversationnels en Java avec LangChain4j' de Richard Grin ?";

    public static void main(String[] args) {
        String apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("La variable d'environnement GOOGLE_AI_GEMINI_API_KEY n'est pas définie.");
            System.err.println("Ajoutez votre clé Gemini dans la configuration d'exécution IntelliJ, sans la committer.");
            return;
        }

        String modelName = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-3.5-flash");
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();

        System.out.println("Test 6 — RAG sur le support PDF (modèle : " + modelName + ")");
        Document course = FileSystemDocumentLoader.loadDocument("langchain4j.pdf");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(course, embeddingStore);
        System.out.println("Le PDF a été découpé en segments et indexé en mémoire.");

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        System.out.println("Question exemple : " + QUESTION_EXEMPLE);
        System.out.println("Gemini : " + assistant.chat(QUESTION_EXEMPLE));
        System.out.println("Posez d'autres questions sur le support. Saisissez /quit pour terminer.");
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
