package ma.emsi.benyassine.tp2.rag;

/** Vérifie directement l'outil météo sur les coordonnées de Paris. */
public class TestMeteo {

    public static void main(String[] args) {
        MeteoTool meteoTool = new MeteoTool();
        System.out.println("Géolocalisation directe : " + meteoTool.rechercheCoordonnees("Paris"));
        System.out.println("Prévisions météo directes : " + meteoTool.isPluiePrevue(48.85, 2.35));
    }
}
