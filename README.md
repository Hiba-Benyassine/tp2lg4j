# tp2lg4j

Projet du TP 2 LangChain4j (EMSI).

## Tests Java avec LangChain4j

Le sous-projet `langchain4j-tests` est un projet Maven Java autonome (JDK 25), séparé de l'application Web Jakarta EE à la racine.

### Test 1 — Chat Gemini

Dans IntelliJ IDEA, ouvrez `langchain4j-tests/pom.xml`, rechargez Maven et configurez `GOOGLE_AI_GEMINI_API_KEY` dans les variables d'environnement de la configuration d'exécution de `Test1`. La clé API ne doit pas être ajoutée au dépôt.

Lancez `ma.emsi.benyassine.tp2.tests.Test1`, posez vos questions dans la console, puis saisissez `/quit` pour terminer. Le programme conserve l'historique de la conversation. Le modèle par défaut est `gemini-3.5-flash`; la variable d'environnement `GEMINI_MODEL` permet de le remplacer si ce modèle n'est pas disponible pour votre clé.

### Test 2 — Coût en tokens

Dans `Test1`, posez la question `Pourquoi le ciel est-il bleu ?`, puis lancez `ma.emsi.benyassine.tp2.tests.Test2Couts` avec la même variable `GOOGLE_AI_GEMINI_API_KEY`. Le programme affiche les tokens d'entrée et de sortie, estime le coût pour `gemini-3.5-flash-lite` et calcule combien de requêtes équivalentes atteindraient 1 $. L'estimation applique les tarifs standard Google AI (0,30 $ par million de tokens en entrée et 2,50 $ par million en sortie) et suppose une facturation payante sans cache ni remise.

### Test 3 — Traducteur avec templates

Lancez `ma.emsi.benyassine.tp2.tests.Test3Traducteur` avec `GOOGLE_AI_GEMINI_API_KEY`. Saisissez un texte, puis une langue pour la seconde traduction. Le programme envoie le même texte à Gemini avec un template à une variable (anglais) puis avec un template à deux variables (langue et texte).

### Test 4 — Embeddings et similarité

Lancez `ma.emsi.benyassine.tp2.tests.Test4Embeddings` avec `GOOGLE_AI_GEMINI_API_KEY`. Le programme compare six couples de phrases françaises avec le modèle BGE local puis avec `gemini-embedding-001`, en mode `SEMANTIC_SIMILARITY`. Il utilise le cosinus pour afficher les scores. Le modèle Google traite les douze phrases par lot afin de limiter les appels réseau.

## Tests RAG

Le sous-projet Maven autonome `langchain4j-rag-tests` garde la dépendance Easy RAG à part du test BGE, comme demandé dans le TP pour éviter les conflits de modèles d'embeddings.

### Test 5 — RAG avec `infos.txt`

Ouvrez `langchain4j-rag-tests/pom.xml` dans IntelliJ, rechargez Maven et lancez `ma.emsi.benyassine.tp2.rag.Test5Rag1` avec `GOOGLE_AI_GEMINI_API_KEY`. Le programme compare la réponse sans document à la réponse RAG, puis garde une conversation en mémoire. Posez ensuite `Quelle est la capitale de la France ?`. Pour l'essai demandé sur la priorité des documents, remplacez la dernière ligne de `langchain4j-rag-tests/infos.txt` par `La capitale de la France est Marseille.`, relancez le programme et posez de nouveau la question.

### Test 6 — RAG avec le support PDF

Le fichier `langchain4j.pdf` fourni par le professeur est à la racine du sous-projet. Lancez `ma.emsi.benyassine.tp2.rag.Test6Rag2` avec `GOOGLE_AI_GEMINI_API_KEY`. Le PDF est découpé en segments indexés dans un magasin vectoriel en mémoire ; l'assistant garde les 20 derniers messages. Le programme pose la question exemple du TP, puis accepte d'autres questions jusqu'à `/quit`.
