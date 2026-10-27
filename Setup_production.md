# Runbook — Mise en place du container de production

Ce runbook décrit la procédure **ponctuelle** pour créer le container Scaleway
de production utilisé par le job `deploy` de `.github/workflows/ci.yml`.

## Contexte

Le job `deploy` ne fait que mettre à jour un container existant
(`scw container container update` + `scw container container deploy`) via
l'identifiant stocké dans le secret GitHub `SCW_CONTAINER_ID` (environnement
`production`). Il ne crée jamais ce container : contrairement au job
`deploy-pr-preview`, il n'y a pas de logique de création automatique pour la
prod. Ce container doit donc être créé **une seule fois, manuellement**, avant
que le job `deploy` puisse fonctionner.

## Prérequis

- [Scaleway CLI](https://github.com/scaleway/scaleway-cli) installé en local.
- Les credentials Scaleway utilisés par la CI (`SCW_ACCESS_KEY`,
  `SCW_SECRET_KEY`, `SCW_DEFAULT_PROJECT_ID`, `SCW_DEFAULT_ORGANIZATION_ID`)
  déjà présents dans les secrets du repo GitHub, et disponibles en local pour
  exécuter les commandes ci-dessous.
- Droits d'administration sur le repo GitHub `cheerz/book-store` (pour créer
  le secret `SCW_CONTAINER_ID`).
- `gh` CLI authentifié (`gh auth login`).

## 1. Configurer le CLI Scaleway en local

```bash
scw init
# ou, sans prompt interactif :
scw config set access-key=<SCW_ACCESS_KEY>
scw config set secret-key=<SCW_SECRET_KEY>
scw config set default-project-id=<SCW_DEFAULT_PROJECT_ID>
scw config set default-organization-id=<SCW_DEFAULT_ORGANIZATION_ID>
scw config set default-region=fr-par
```

## 2. S'assurer qu'une image existe dans le registry

Le container ne peut pas être créé sans image à pointer. Si aucune image
n'a encore été poussée sur `rg.fr-par.scw.cloud/book-store/springboot` :

- Déclencher un push sur `master` (le job `push-to-scw-registery` réussira
  indépendamment du job `deploy`, qui échouera normalement tant que
  `SCW_CONTAINER_ID` n'existe pas — c'est attendu à ce stade).
- Vérifier que l'image est bien présente :

```bash
scw registry image list region=fr-par
```

## 3. Créer le namespace de containers de production

```bash
scw container namespace create \
  name=book-store-production \
  region=fr-par
```

Noter le `NAMESPACE_ID` retourné.

## 4. Créer le container de production

```bash
scw container container create \
  namespace-id=NAMESPACE_ID \
  name=book-store \
  image=rg.fr-par.scw.cloud/book-store/springboot:latest \
  port=8080 \
  privacy=public \
  environment-variables.JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75" \
  region=fr-par
```

Noter le `CONTAINER_ID` retourné (champ `id` du JSON de sortie).

## 5. Déployer le container

```bash
scw container container deploy CONTAINER_ID \
  region=fr-par
```

## 6. Vérifier manuellement le déploiement

```bash
DOMAIN=$(scw container container get CONTAINER_ID \
  region=fr-par -o json | jq -r .domain_name)

curl --fail --silent --show-error "https://${DOMAIN}/api/health"
```

## 7. Enregistrer l'ID du container dans GitHub

Le secret doit être créé dans l'environnement `production` du repo (celui
référencé par `environment: production` dans le job `deploy`).

```bash
gh secret set SCW_CONTAINER_ID \
  --body "CONTAINER_ID" \
  --env production \
  --repo cheerz/book-store
```

## 8. Relancer le déploiement

Si le job `deploy` avait échoué à l'étape 2 faute de secret, relancer le
workflow depuis GitHub Actions (re-run du job en échec) ou refaire un push
sur `master`. Le job `deploy` doit désormais réussir et afficher l'URL
déployée dans le résumé du run.

## Notes

- Cette procédure n'est à exécuter qu'une fois, à l'initialisation du
  projet. Les déploiements suivants passent uniquement par le job `deploy`
  de la CI.
- En cas de recréation du container (suppression accidentelle, changement de
  namespace, etc.), reprendre ce runbook depuis l'étape 3 et mettre à jour
  le secret `SCW_CONTAINER_ID` à l'étape 7.
