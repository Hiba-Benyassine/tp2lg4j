package ma.emsi.benyassine.tp2.tests;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;
import java.util.List;

/** Compare des similarités de phrases avec BGE local et Google Gemini. */
public class Test4Embeddings {

    private static final List<String[]> PAIRS = List.of(
            new String[]{"Le chat dort sur le canapé.", "Un félin se repose sur le sofa."},
            new String[]{"La température augmente rapidement.", "La température diminue rapidement."},
            new String[]{"J'aime lire des romans le soir.", "La lecture de romans me détend le soir."},
            new String[]{"Le train arrive à la gare à midi.", "Les tomates poussent dans le jardin."},
            new String[]{"Rabat est la capitale du Maroc.", "La capitale marocaine est Rabat."},
            new String[]{"Elle a acheté une voiture neuve.", "Elle a vendu son ancienne voiture."}
    );

    public static void main(String[] args) {
        String apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("La variable d'environnement GOOGLE_AI_GEMINI_API_KEY n'est pas définie.");
            System.err.println("Ajoutez votre clé Gemini dans la configuration d'exécution IntelliJ, sans la committer.");
            return;
        }

        List<String> phrases = PAIRS.stream()
                .flatMap(pair -> List.of(pair[0], pair[1]).stream())
                .toList();

        EmbeddingModel localModel = new BgeSmallEnV15EmbeddingModel();
        printSimilarities("BGE Small EN v1.5 (local)", localModel, phrases);

        EmbeddingModel googleModel = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .taskType(GoogleGenAiEmbeddingModel.TaskTypeEnum.SEMANTIC_SIMILARITY)
                .timeout(Duration.ofSeconds(30))
                .build();
        printSimilarities("Google Gemini Embedding 001", googleModel, phrases);
    }

    private static void printSimilarities(String modelName, EmbeddingModel model, List<String> phrases) {
        List<Embedding> embeddings = model.embedAll(
                phrases.stream().map(TextSegment::from).toList()).content();

        System.out.println("\nSimilarités avec " + modelName + " (cosinus) :");
        for (int i = 0; i < PAIRS.size(); i++) {
            double similarity = CosineSimilarity.between(embeddings.get(2 * i), embeddings.get(2 * i + 1));
            System.out.printf("%d. %.4f%n   A : %s%n   B : %s%n",
                    i + 1, similarity, PAIRS.get(i)[0], PAIRS.get(i)[1]);
        }
    }
}
