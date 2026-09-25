        # demo-deploy

API Spring Boot (Kotlin) de gestion d'un catalogue de livres, servie sous `/api`.

## Prérequis

- JDK 25
- Docker, avec `docker compose` ou `docker-compose` (sur macOS : `colima start`)

## Lancer l'application

```bash
./gradlew :book-store:bootRun
```

L'API répond sur `http://localhost:8080/api` :

```bash
curl localhost:8080/api/health
curl localhost:8080/api/books
curl -H 'Content-Type: application/json' -d '{"title":"Dune","bookKind":"sf"}' localhost:8080/api/books
```

Pour la lancer dans Docker, comme en production :

```bash
./gradlew :book-store:bootJar
docker compose up -d --build
docker compose down -v
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
