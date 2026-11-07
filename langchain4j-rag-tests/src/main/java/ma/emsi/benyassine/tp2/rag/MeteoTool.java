package ma.emsi.benyassine.tp2.rag;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

/** Outils exposés au LLM pour consulter les prévisions Open-Meteo. */
public class MeteoTool {

    private static final int SEUIL_PLUIE = 40;
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Tool("Consulte les probabilités maximales de pluie pour les trois prochains jours à partir de coordonnées GPS.")
    public String isPluiePrevue(
            @P("Latitude de la ville, entre -90 et 90.") double latitude,
            @P("Longitude de la ville, entre -180 et 180.") double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return "Les coordonnées fournies sont invalides.";
        }

        String url = String.format(Locale.ROOT,
                "https://api.open-meteo.com/v1/forecast?latitude=%.5f&longitude=%.5f"
                        + "&daily=precipitation_probability_max&forecast_days=3&timezone=auto",
                latitude, longitude);
        try {
            JsonObject root = getJson(url);
            JsonObject daily = root.getAsJsonObject("daily");
            JsonArray dates = daily.getAsJsonArray("time");
            JsonArray probabilities = daily.getAsJsonArray("precipitation_probability_max");
            StringBuilder result = new StringBuilder("Prévisions de précipitations sur trois jours : ");
            boolean rainLikely = false;

            for (int i = 0; i < Math.min(dates.size(), probabilities.size()); i++) {
                if (i > 0) {
                    result.append("; ");
                }
                if (probabilities.get(i).isJsonNull()) {
                    result.append(dates.get(i).getAsString()).append(" : donnée indisponible");
                    continue;
                }
                int probability = probabilities.get(i).getAsInt();
                result.append(dates.get(i).getAsString()).append(" : ").append(probability).append(" %");
                rainLikely |= probability >= SEUIL_PLUIE;
            }

            result.append(". Seuil de pluie retenu : ").append(SEUIL_PLUIE).append(" %.");
            result.append(rainLikely
                    ? " Au moins une journée dépasse le seuil : un parapluie est conseillé."
                    : " Aucune journée ne dépasse le seuil : un parapluie n'est pas nécessaire selon ce seuil.");
            return result.toString();
        } catch (IOException e) {
            return "Impossible de récupérer les prévisions météo : " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "La récupération des prévisions météo a été interrompue.";
        } catch (RuntimeException e) {
            return "La réponse météo n'a pas pu être interprétée : " + e.getMessage();
        }
    }

    private JsonObject getJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP " + response.statusCode());
        }
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }
}
