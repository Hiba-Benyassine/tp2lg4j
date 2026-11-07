# tp2lg4j

Projet du TP 2 LangChain4j (EMSI).

## Tests Java avec LangChain4j

Le sous-projet `langchain4j-tests` est un projet Maven Java autonome (JDK 25), séparé de l'application Web Jakarta EE à la racine.

### Test 1 — Chat Gemini

Dans IntelliJ IDEA, ouvrez `langchain4j-tests/pom.xml`, rechargez Maven et configurez `GOOGLE_AI_GEMINI_API_KEY` dans les variables d'environnement de la configuration d'exécution de `Test1`. La clé API ne doit pas être ajoutée au dépôt.

Lancez `ma.emsi.benyassine.tp2.tests.Test1`, posez vos questions dans la console, puis saisissez `/quit` pour terminer. Le programme conserve l'historique de la conversation. Le modèle par défaut est `gemini-3.5-flash`; la variable d'environnement `GEMINI_MODEL` permet de le remplacer si ce modèle n'est pas disponible pour votre clé.

### Test 2 — Coût en tokens

Dans `Test1`, posez la question `Pourquoi le ciel est-il bleu ?`, puis lancez `ma.emsi.benyassine.tp2.tests.Test2Couts` avec la même variable `GOOGLE_AI_GEMINI_API_KEY`. Le programme affiche les tokens d'entrée et de sortie, estime le coût pour `gemini-3.5-flash-lite` et calcule combien de requêtes équivalentes atteindraient 1 $. L'estimation applique les tarifs standard Google AI (0,30 $ par million de tokens en entrée et 2,50 $ par million en sortie) et suppose une facturation payante sans cache ni remise.
