package ma.emsi.benyassine.tp2.tests;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Premier essai de LangChain4j : conversation interactive avec Gemini.
 * Saisir /quit pour terminer la conversation.
 */
public class Test1 {

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

        List<ChatMessage> conversation = new ArrayList<>();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Test 1 — Chat avec Gemini (modèle : " + modelName + ")");
            System.out.println("Posez une question. Saisissez /quit pour terminer.");

            while (true) {
                System.out.print("Vous : ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String question = scanner.nextLine().trim();
                if (question.equalsIgnoreCase("/quit")) {
                    break;
                }
                if (question.isEmpty()) {
                    continue;
                }

                conversation.add(UserMessage.from(question));
                AiMessage answer = model.chat(conversation).aiMessage();
                conversation.add(answer);
                System.out.println("Gemini : " + answer.text());
            }
        }
    }
}
