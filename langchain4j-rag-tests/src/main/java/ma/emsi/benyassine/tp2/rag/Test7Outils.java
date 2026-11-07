package ma.emsi.benyassine.tp2.rag;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

/** Compare une réponse sans outil avec une conversation pouvant appeler Open-Meteo. */
public class Test7Outils {

    private static final String QUESTION_EXEMPLE =
            "J'ai prévu d'aller aujourd'hui à la ville dont la latitude est 48.85 et la longitude est 2.35 "
                    + "pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?";

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

        System.out.println("Test 7 — Utilisation d'outils (modèle : " + modelName + ")");
        System.out.println("Sans outil : " + model.chat(QUESTION_EXEMPLE));

        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(new MeteoTool())
                .build();

        System.out.println("\nAvec l'outil météo sur coordonnées :");
        System.out.println("Exemple : " + QUESTION_EXEMPLE);
        System.out.println("Posez d'autres questions. Saisissez /quit pour terminer.");
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
