package ma.emsi.benyassine.tp2.tests;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;
import java.util.Scanner;

/** Compare deux templates LangChain4j pour traduire un texte. */
public class Test3Traducteur {

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

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Test 3 — Traducteur avec PromptTemplate (modèle : " + modelName + ")");
            System.out.print("Texte à traduire : ");
            String text = scanner.nextLine().trim();
            if (text.isEmpty()) {
                System.out.println("Le texte à traduire ne peut pas être vide.");
                return;
            }

            PromptTemplate englishTemplate = PromptTemplate.from(
                    "Traduis le texte suivant en anglais : {{text}}");
            String englishPrompt = englishTemplate.apply(Map.of("text", text)).text();
            System.out.println("Prompt (template à une variable) : " + englishPrompt);
            System.out.println("Traduction en anglais : " + model.chat(englishTemplate
                    .apply(Map.of("text", text)).toUserMessage()).aiMessage().text());

            System.out.print("Langue de traduction pour le second template : ");
            String language = scanner.nextLine().trim();
            if (language.isEmpty()) {
                System.out.println("La langue de traduction ne peut pas être vide.");
                return;
            }

            PromptTemplate languageTemplate = PromptTemplate.from(
                    "Traduis le texte suivant en {{language}} : {{text}}");
            String translatedPrompt = languageTemplate.apply(Map.of(
                    "language", language,
                    "text", text)).text();
            System.out.println("Prompt (template à deux variables) : " + translatedPrompt);
            System.out.println("Traduction en " + language + " : " + model.chat(languageTemplate
                    .apply(Map.of("language", language, "text", text))
                    .toUserMessage()).aiMessage().text());
        }
    }
}
