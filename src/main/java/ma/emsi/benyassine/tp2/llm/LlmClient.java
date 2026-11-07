package ma.emsi.benyassine.tp2.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Client métier Gemini ; l'historique est restauré si la vue JSF est sérialisée. */
@Dependent
public class LlmClient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int MAX_MESSAGES = 20;
    private static final int MAX_SAVED_TURNS = 10;

    private String systemRole;
    private List<ChatTurn> history = new ArrayList<>();

    private transient Assistant assistant;
    private transient ChatMemory chatMemory;

    public LlmClient() {
        this.systemRole = "Tu es un assistant utile. Réponds clairement et précisément à la demande.";
        initializeAssistantIfConfigured();
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
        history.clear();
        if (chatMemory != null) {
            chatMemory.clear();
            chatMemory.add(SystemMessage.from(systemRole));
        }
    }

    public String chat(String prompt) {
        ensureAssistant();
        String answer = assistant.chat(prompt);
        history.add(new ChatTurn(prompt, answer));
        while (history.size() > MAX_SAVED_TURNS) {
            history.remove(0);
        }
        return answer;
    }

    private void ensureAssistant() {
        if (assistant != null) {
            return;
        }
        String apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "La variable GOOGLE_AI_GEMINI_API_KEY doit être définie dans l'environnement du serveur.");
        }
        initializeAssistant(apiKey);
    }

    private void initializeAssistantIfConfigured() {
        String apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY");
        if (apiKey != null && !apiKey.isBlank()) {
            initializeAssistant(apiKey);
        }
    }

    private void initializeAssistant(String apiKey) {
        String modelName = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-3.5-flash");
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();

        chatMemory = MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES);
        if (systemRole != null && !systemRole.isBlank()) {
            chatMemory.add(SystemMessage.from(systemRole));
        }
        for (ChatTurn turn : history) {
            chatMemory.add(UserMessage.from(turn.question));
            chatMemory.add(AiMessage.from(turn.answer));
        }
        assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    @Serial
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        if (history == null) {
            history = new ArrayList<>();
        }
        initializeAssistantIfConfigured();
    }

    private static final class ChatTurn implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final String question;
        private final String answer;

        private ChatTurn(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }
}
