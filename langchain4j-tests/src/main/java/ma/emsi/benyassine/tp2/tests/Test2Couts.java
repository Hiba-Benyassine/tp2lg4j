package ma.emsi.benyassine.tp2.tests;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;

/** Affiche les tokens consommés et estime le prix d'une requête Gemini. */
public class Test2Couts {

    private static final String MODEL_NAME = "gemini-3.5-flash-lite";
    private static final String QUESTION = "Pourquoi le ciel est-il bleu ?";

    // Tarifs standard Google AI au 8 novembre 2026, en dollars par million de tokens.
    private static final double INPUT_PRICE_PER_MILLION = 0.30;
    private static final double OUTPUT_PRICE_PER_MILLION = 2.50;

    public static void main(String[] args) {
        String apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("La variable d'environnement GOOGLE_AI_GEMINI_API_KEY n'est pas définie.");
            System.err.println("Ajoutez votre clé Gemini dans la configuration d'exécution IntelliJ, sans la committer.");
            return;
        }

        ChatResponse response = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(MODEL_NAME)
                .build()
                .chat(UserMessage.from(QUESTION));

        System.out.println("Question : " + QUESTION);
        System.out.println("Réponse : " + response.aiMessage().text());

        TokenUsage usage = response.tokenUsage();
        if (usage == null || usage.inputTokenCount() == null || usage.outputTokenCount() == null) {
            System.out.println("Gemini n'a pas fourni le détail des tokens pour cette réponse.");
            return;
        }

        int inputTokens = usage.inputTokenCount();
        int outputTokens = usage.outputTokenCount();
        double costInDollars = (inputTokens * INPUT_PRICE_PER_MILLION
                + outputTokens * OUTPUT_PRICE_PER_MILLION) / 1_000_000.0;

        System.out.println("Tokens d'entrée : " + inputTokens);
        System.out.println("Tokens de sortie : " + outputTokens);
        System.out.printf("Coût estimé à tarif standard : $%.10f%n", costInDollars);
        if (costInDollars > 0) {
            System.out.printf("Requêtes similaires pour atteindre 1 $ : environ %.0f%n",
                    Math.floor(1.0 / costInDollars));
        }
        System.out.println("Le calcul suppose le tarif payant standard, sans remise ni cache.");
    }
}
