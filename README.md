# demo-deploy

API Spring Boot (Kotlin) de gestion d'un catalogue de livres, servie sous `/api`.
Les fronts n'appellent que les vues BFF (`/pages/...`).
L'architecture et ses raisons sont décrites dans [Choix techniques](#choix-techniques).

## Prérequis

- JDK 25
- Docker, avec `docker compose` ou `docker-compose` (sur macOS : `colima start`)

### Installer Java 25 sur macOS

Avec [Homebrew](https://brew.sh) :

```bash
brew install openjdk@25
sudo ln -sfn /opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-25.jdk
```

Ou avec [SDKMAN!](https://sdkman.io) :

```bash
curl -s "https://get.sdkman.io" | bash
sdk install java 25-tem
```

Vérifier l'installation :

```bash
java -version
```

## Lancer l'application

```bash
./gradlew :book-store:bootRun
```

L'API répond sur `http://localhost:8080/api` :

```bash
curl localhost:8080/api/health
curl localhost:8080/api/pages/home
curl localhost:8080/api/pages/catalog
curl -H 'Content-Type: application/json' -d '{"title":"Dune","bookKind":"sf","bookFormat":"hardcover","priceInCents":1200}' localhost:8080/api/pages/catalog/books
```

Pour la lancer dans Docker, comme en production :

```bash
./gradlew :book-store:bootJar
docker compose up -d --build
docker compose down -v
```

## Lint

```bash
./gradlew ktlintCheck    # vérifie, comme la CI
./gradlew ktlintFormat   # corrige ce qui peut l'être automatiquement
```

## Tests

Trois couches, de la moins chère à la plus chère.

### 1. Tests unitaires — `book-store`

Domaine et mapping, sans réseau ni Docker.

```bash
./gradlew :book-store:test
```

### 2. Tests d'intégration boîte noire — `book-store-ti`

Le module ne dépend pas de l'application : il l'interroge en HTTP, via son contrat d'API.
Il n'est pas lancé par `./gradlew build`.

Contre l'application dockerisée (build du jar, montée de la stack, tests, démontage) :

```bash
./gradlew ti
```

Contre une application déjà démarrée :

```bash
BASE_URL=http://localhost:8080/api ./gradlew :book-store-ti:integrationTest
```

Variables : `BASE_URL` (défaut `http://localhost:8080/api`), `READINESS_TIMEOUT_SECONDS` (défaut `60`).

### 3. Smoke test post-déploiement

Joué par la CI après chaque déploiement sur `master` : appel de `/api/health` sur le
conteneur déployé, avec retries. Rien à lancer en local.

## Choix techniques

### Vue d'ensemble

```
front ──HTTP──▶ bff/                     adapter inbound + orchestrateur
                 │  appelle les ports inbound (BookManager, PriceManager)
       ┌─────────┴──────────┐
       ▼                    ▼
 catalog/domain        pricing/domain    modèle, règles métier, ports
       │                    │  ports outbound (BookRepository, PriceRepository)
       ▼                    ▼
 catalog/adapter/      pricing/adapter/
   outbound/             outbound/       implémentations (en mémoire)
```

Deux règles structurent le code :

- un domaine ne connaît pas les autres : `catalog` n'importe rien de `pricing`, et inversement ;
- tout ce qui croise plusieurs domaines passe par le BFF.

### Domaines : hexagonal simplifié

Chaque domaine est un package (`catalog`, `pricing`) organisé ainsi :

| Emplacement | Contenu | Exemple |
|---|---|---|
| `domain/` | Modèle et règles métier | `Book`, `Price` (refus d'un prix négatif) |
| `domain/` | Port inbound : ce que l'extérieur peut demander au domaine | `BookManager` |
| `domain/` | Implémentation du port inbound | `BookManagerImpl` |
| `domain/` | Port outbound : ce dont le domaine a besoin | `BookRepository` |
| `domain/` | Exceptions métier nommées | `InvalidBookKindException` |
| `adapter/outbound/` | Implémentation des ports outbound | `InMemoryBookRepository` |

C'est « simplifié » par rapport à un hexagonal complet :

- ports, implémentations et modèle partagent le package `domain/`, sans couche `application` ni use case séparé ;
- pas de module de configuration par domaine (voir `@Service` ci-dessous).

On garde l'essentiel : le domaine ne dépend d'aucun adapter, et tout passe par des interfaces. Un adapter se remplace sans toucher au domaine, et le domaine se teste avec des fakes.

### BFF : adapter inbound et orchestrateur

Le package `bff/` est l'adapter inbound de l'application : c'est le seul point d'entrée des fronts.

- **Une vue par page** : `/pages/home`, `/pages/catalog`. Chaque vue renvoie exactement ce que la page affiche, dans ses propres DTO (`CatalogPageDto`, `HomePageDto`).
- **Orchestrateur** : une vue peut appeler plusieurs domaines. `CatalogPageView` combine `catalog` (les livres) et `pricing` (le prix de chaque format).
- **Traduction typée entre domaines** : `catalog` parle de `BookFormat`, `pricing` de `ProductType`. Le BFF passe de l'un à l'autre avec un `when` exhaustif : un format ajouté sans son type de produit ne compile pas. Côté `pricing`, l'adapter en mémoire est lui aussi un `when` exhaustif : un type de produit ajouté sans prix ne compile pas.
- **Ne pas casser l'existant** : le contrat de la page est découplé du modèle du domaine. Le domaine évolue sans casser le front, et une nouvelle page s'ajoute par une nouvelle vue, sans modifier les endpoints ni les domaines existants.
- **Pas de règle métier dans le BFF** : il agrège, mappe et met en forme. Les règles restent dans les domaines.

### Adapter inbound par domaine : optionnel

Quand une ressource doit être accessible directement, hors d'une page (autre service, batch, API publique), on ajoute un adapter inbound au domaine concerné :

```
catalog/adapter/inbound/rest/BookController.kt   → appelle BookManager
```

Il coexiste avec le BFF, et les deux passent par le même port inbound : aucun ne contourne le domaine. On ne crée cet adapter que lorsque le besoin existe.

### `@Service` dans le domaine : KISS

Les implémentations des ports inbound portent directement `@Service` (`BookManagerImpl`, `PriceManagerImpl`). Ça évite une classe `@Configuration` par domaine qui ne ferait que déclarer des beans.

Le compromis est assumé :

- le domaine dépend d'une annotation Spring, et de rien d'autre dans Spring ;
- il reste testable sans contexte Spring : les tests instancient `BookManagerImpl(FakeBookRepository())`.

Si un domaine doit un jour se passer de Spring (librairie partagée, autre runtime), on retire l'annotation et on déclare le bean dans une `@Configuration` côté adapter. Le changement reste local : les appelants ne voient que le port.

### Erreurs

- Les domaines lèvent des exceptions métier nommées.
- `bff/error/ErrorHandlingAdvice` est le seul endroit qui les traduit : un code `CBE-*` et un statut HTTP, dans l'enveloppe `{ "error": { "status_code", "error_code", "user_message", "developer_summary", "developer_message", "request_id" } }`.
- `user_message` est le seul texte destiné à l'utilisateur final : c'est lui que le front affiche. Les champs `developer_*` restent pour le debug.
- `developer_message` ne contient que des messages écrits dans ce code : celui de l'exception métier, ou la liste des champs invalides. Les messages de Spring et de Jackson ne partent jamais au client, car ils contiennent des noms de classes internes.
- Les erreurs du framework gardent leur statut : route inconnue → `CBE-7` (404), méthode non supportée → `CBE-12` (405).
- Une exception non mappée devient `CBE-0` (500), sans exposer son message.

Ajouter une erreur métier = l'exception dans le domaine, une entrée dans `CbeError` (avec son `userMessage`), un cas dans `ErrorHandlingAdvice.cbeFor`.

### Logs et request id

- `bff/observability/RequestIdFilter` attribue un id à chaque requête. Le même id est renvoyé dans l'en-tête `X-Request-Id`, dans le `request_id` des erreurs, et il préfixe chaque ligne de log (`logging.pattern.level`).
- Le front affiche cet id comme « Référence » sous un message d'erreur : un utilisateur peut le transmettre, et on retrouve la requête dans les logs.
- Un 5xx est loggué en `ERROR` avec sa stack trace. Une erreur client est logguée en `WARN` avec son code et sa classe seulement : son message reprend la saisie de l'utilisateur, qui pourrait injecter de fausses lignes de log.

### Persistance en mémoire

Les adapters outbound gardent les données en mémoire : le template n'embarque pas de base. Les données sont perdues au redémarrage et ne sont pas partagées entre instances. Brancher une vraie base consiste à écrire un nouvel adapter qui implémente le même port outbound.

### Tests

| Couche | Ce qui est testé | Comment |
|---|---|---|
| Unitaires (`book-store`) | Règles du domaine, mapping et orchestration du BFF, traduction des erreurs | Fakes écrits à la main sur les ports, sans librairie de mock ; seul `contextLoads` démarre Spring |
| Intégration (`book-store-ti`) | Le contrat HTTP de l'application déployée | Module séparé, sans dépendance vers `book-store` : un client Feign contre `BASE_URL` |
| Smoke post-déploiement | L'application déployée répond | `curl` sur `/api/health` en CI |

Le module de TI ne connaît que HTTP : il valide l'image réellement livrée, et la même suite tourne contre compose, une preview de PR ou un autre environnement.

### CI/CD

```
ktlint → TU → jar → image construite une fois → TI sur cette image
       → push de la même image (sha + latest) → déploiement Scaleway → smoke test
```

- **Une seule image** : ce qui part en production est exactement ce que les TI ont validé, sans rebuild.
- **Preview par PR** : chaque PR déploie son image dans un container dédié du namespace `book-store-pr-preview`, supprimé à la fermeture de la PR.
- **Dockerfile runtime-only** : Gradle construit le jar, l'image se contente de l'exécuter, avec un utilisateur non-root. `compose.yaml` reprend la limite mémoire (512 Mo) et les options JVM du container Scaleway, pour voir les problèmes de heap en local.

### Qualité : ktlint

- ktlint 1.8.0, via le plugin Gradle `org.jlleitschuh.gradle.ktlint` 14.2.0, appliqué à tous les modules et aux build scripts `.kts`.
- Style `ktlint_official`, configuré dans `.editorconfig`.
- Première étape de la CI : un écart de style fait échouer le build avant les tests.
- La règle `package-name` est désactivée tant que le package `com.example.demo_deploy` (avec un underscore) n'est pas renommé.
