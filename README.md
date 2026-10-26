# book-store

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
curl -H 'Content-Type: application/json' -d '{"title":"Dune","kind":"sf","format":"hardcover","bookPriceInCents":1200}' localhost:8080/api/pages/catalog/books
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
   outbound/             outbound/       implémentations JPA (HSQLDB)
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
| `adapter/outbound/` | Implémentation des ports outbound, avec ses entités JPA | `JpaBookRepository`, `BookEntity` |

C'est « simplifié » par rapport à un hexagonal complet :

- ports, implémentations et modèle partagent le package `domain/`, sans couche `application` ni use case séparé ;
- pas de module de configuration par domaine (voir `@Service` ci-dessous).

On garde l'essentiel : le domaine ne dépend d'aucun adapter, et tout passe par des interfaces. Un adapter se remplace sans toucher au domaine, et le domaine se teste avec des fakes.

### BFF : adapter inbound et orchestrateur

Le package `bff/` est l'adapter inbound de l'application : c'est le seul point d'entrée des fronts.
Les briques HTTP communes à toutes les routes (erreurs, request id) vivent à part, dans `web/`.

- **Un contrôleur par page** : `/pages/home`, `/pages/catalog`. Chaque contrôleur (`HomePageController`, `CatalogPageController`) renvoie exactement ce que la page affiche, dans ses propres DTO (`CatalogPageDto`, `HomePageDto`).
- **Orchestrateur** : un contrôleur peut appeler plusieurs domaines. `CatalogPageController` combine `catalog` (les livres) et `pricing` (le prix de chaque format).
- **Traduction typée entre domaines** : `catalog` parle de `BookFormat`, `pricing` de `ProductType`. Le BFF passe de l'un à l'autre avec un `when` exhaustif : un format ajouté sans son type de produit ne compile pas. Côté `pricing`, les prix sont en base : un type de produit ajouté sans son prix dans `data.sql` fait échouer `JpaPriceRepositoryTest`.
- **Ne pas casser l'existant** : le contrat de la page est découplé du modèle du domaine. Le domaine évolue sans casser le front, et une nouvelle page s'ajoute par un nouveau contrôleur, sans modifier les endpoints ni les domaines existants.
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
- `web/error/ErrorHandlingAdvice` est le seul endroit qui les traduit : un code `CBE-*` et un statut HTTP, dans l'enveloppe `{ "error": { "statusCode", "errorCode", "userMessage", "developerSummary", "developerMessage", "requestId" } }`.
- `userMessage` est le seul texte destiné à l'utilisateur final : c'est lui que le front affiche. Les champs `developer*` restent pour le debug.
- `developerMessage` ne contient que des messages écrits dans ce code : celui de l'exception métier, ou la liste des champs invalides. Les messages de Spring et de Jackson ne partent jamais au client, car ils contiennent des noms de classes internes.
- Les erreurs du framework gardent leur statut : route inconnue → `CBE-7` (404), méthode non supportée → `CBE-12` (405).
- Une exception non mappée devient `CBE-0` (500), sans exposer son message.

Ajouter une erreur métier = l'exception dans le domaine, une entrée dans `CbeError` (avec son `userMessage`), un cas dans `ErrorHandlingAdvice.cbeFor`.

### Logs et request id

- `web/observability/RequestIdFilter` attribue un id à chaque requête. Le même id est renvoyé dans l'en-tête `X-Request-Id`, dans le `requestId` des erreurs, et il préfixe chaque ligne de log (`logging.pattern.level`).
- Le front affiche cet id comme « Référence » sous un message d'erreur : un utilisateur peut le transmettre, et on retrouve la requête dans les logs.
- Un 5xx est loggué en `ERROR` avec sa stack trace. Une erreur client est logguée en `WARN` avec son code et sa classe seulement : son message reprend la saisie de l'utilisateur, qui pourrait injecter de fausses lignes de log.

### Persistance : JPA sur HSQLDB embarquée

- **Base** : HSQLDB embarquée dans l'application, stockée en fichiers dans `./data` (relatif au répertoire de lancement). Aucun serveur de base à installer ni à déployer.
- **Pourquoi pas H2** : H2 est sous MPL 2.0 / EPL 1.0, hors des licences autorisées (MIT, Apache 2.0, BSD). HSQLDB est sous licence BSD.
- **Schéma** : Hibernate le crée et le complète à partir des entités (`ddl-auto: update`). Pas d'outil de migration, pour rester simple. Limite : `update` ajoute les tables et les colonnes manquantes, mais ne supprime, ne renomme et ne modifie jamais rien. Renommer un champ laisse l'ancienne colonne en place.
- **Données de départ** : `src/main/resources/data.sql`, joué à chaque démarrage après Hibernate. Chaque requête n'insère que ce qui manque : les prix absents, et les livres de départ seulement si le catalogue est vide. Un redémarrage ne crée donc pas de doublon.
- **Enums stockés en `String`** : `BookEntity.kind`, `BookEntity.format` et `ProductPriceEntity.productType` sont des `String`, convertis vers l'enum dans l'adapter. Avec `@Enumerated`, Hibernate pose un `check (kind in (...))` que `update` ne met jamais à jour : une valeur ajoutée à l'enum serait refusée par la base.
- **Hexagonal** : les entités JPA (`BookEntity`, `ProductPriceEntity`) et les interfaces Spring Data restent dans `adapter/outbound/`. Le domaine ne connaît que ses ports et ses objets (`Book`, `Price`). L'adapter fait la conversion dans les deux sens.
- **Ordre** : `JpaBookRepository.findAll()` trie par id, c'est-à-dire par ordre d'ajout. La page d'accueil en tire ses « derniers ajouts ».

Où vivent les données :

| Environnement | Emplacement | Durée de vie |
|---|---|---|
| `./gradlew :book-store:bootRun` | `book-store/data/` (ignoré par Git) | Jusqu'à suppression du répertoire |
| `docker compose` | Volume `book-store-data` | Survit à un redémarrage ; `docker compose down -v` remet à zéro |
| Tests (`@DataJpaTest`, `contextLoads`) | Base en mémoire, tables créées par Hibernate, `data.sql` joué | Le temps du test |
| Scaleway | Système de fichiers du container | **Perdues à chaque redéploiement ou redémarrage, et propres à chaque instance** |

En production, la base embarquée a donc les mêmes limites que l'ancien stockage en mémoire, avec une vraie base relationnelle en plus. Passer à une base managée (Postgres par exemple) demande d'ajouter le driver, de configurer `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` et `SPRING_DATASOURCE_PASSWORD`, et de vérifier `data.sql` sur ce moteur (`MERGE` et `VALUES` varient selon les bases). Le domaine et les adapters ne changent pas. C'est aussi le moment de remplacer `ddl-auto: update` par un outil de migration.

### Tests

| Couche | Ce qui est testé | Comment |
|---|---|---|
| Unitaires (`book-store`) | Règles du domaine, mapping et orchestration du BFF, traduction des erreurs | Fakes écrits à la main sur les ports, sans librairie de mock ; seul `contextLoads` démarre Spring |
| Adapters JPA (`book-store`) | Les adapters outbound contre le vrai schéma et les données de départ | `@DataJpaTest` : base HSQLDB en mémoire, tables créées par Hibernate, `data.sql` joué |
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
