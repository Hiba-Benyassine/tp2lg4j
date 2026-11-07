package ma.emsi.benyassine.tp2.rag;

/** Vérifie directement l'outil météo sur les coordonnées de Paris. */
public class TestMeteo {

    public static void main(String[] args) {
        System.out.println(new MeteoTool().isPluiePrevue(48.85, 2.35));
    }
}
