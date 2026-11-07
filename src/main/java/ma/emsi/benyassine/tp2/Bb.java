package ma.emsi.benyassine.tp2;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.benyassine.tp2.llm.LlmClient;

import java.io.Serial;
import java.io.Serializable;

/** Backing bean de la page de chat JSF ; une instance est gardée pendant la vue. */
@Named("bb")
@ViewScoped
public class Bb implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_ROLE =
            "Tu es un assistant utile. Réponds clairement et précisément à la demande.";

    @Inject
    private LlmClient llmClient;

    private String role = DEFAULT_ROLE;
    private String question = "";
    private String response = "";
    private String conversation = "";
    private boolean conversationStarted;

    public void envoyer() {
        if (question == null || question.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Écrivez une question avant l'envoi.", null));
            return;
        }

        try {
            if (!conversationStarted) {
                llmClient.setSystemRole(role);
                conversationStarted = true;
            }
            response = llmClient.chat(question.trim());
            conversation += "* User:\n" + question.trim() + "\n* GPT:\n" + response + "\n";
        } catch (RuntimeException e) {
            response = "Erreur pendant la communication avec Gemini : " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "La réponse n'a pas pu être obtenue.", e.getMessage()));
        }
    }

    public void effacer() {
        question = "";
        response = "";
        conversation = "";
        conversationStarted = false;
        llmClient.setSystemRole(role);
    }

    public void changerRole() {
        response = "";
        conversation = "";
        conversationStarted = false;
        llmClient.setSystemRole(role);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Le rôle a changé ; une nouvelle conversation commence.", null));
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public String getConversation() {
        return conversation;
    }
}
