# tp2lg4j

Projet du TP 2 LangChain4j (EMSI).

## Tests Java avec LangChain4j

Le sous-projet `langchain4j-tests` est un projet Maven Java autonome (JDK 25), séparé de l'application Web Jakarta EE à la racine.

### Test 1 — Chat Gemini

Dans IntelliJ IDEA, ouvrez `langchain4j-tests/pom.xml`, rechargez Maven et configurez `GOOGLE_AI_GEMINI_API_KEY` dans les variables d'environnement de la configuration d'exécution de `Test1`. La clé API ne doit pas être ajoutée au dépôt.

Lancez `ma.emsi.benyassine.tp2.tests.Test1`, posez vos questions dans la console, puis saisissez `/quit` pour terminer. Le programme conserve l'historique de la conversation. Le modèle par défaut est `gemini-3.5-flash`; la variable d'environnement `GEMINI_MODEL` permet de le remplacer si ce modèle n'est pas disponible pour votre clé.
