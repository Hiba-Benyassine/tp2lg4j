package ma.emsi.benyassine.tp2.llm;

/** Interaction de chat prise en charge par un service IA LangChain4j. */
public interface Assistant {

    String chat(String prompt);
}
