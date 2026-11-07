package ma.emsi.benyassine.tp2.rag;

/** Interaction de chat utilisée par le service IA LangChain4j. */
public interface Assistant {

    String chat(String message);
}
