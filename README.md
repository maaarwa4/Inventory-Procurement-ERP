<div align="center">

# Inventory & Procurement ERP

**ERP de gestion des stocks et des achats pour un catalogue de terminaux mobiles**

Spring Boot 3 &nbsp;·&nbsp; Java 17 &nbsp;·&nbsp; Angular 20 &nbsp;·&nbsp; Angular Material &nbsp;·&nbsp; PostgreSQL

</div>

<br>

## Contexte

Projet de fin d'année réalisé de juin à septembre 2025. L'objectif était de réunir dans une même application la gestion des produits, des fournisseurs et des commandes d'approvisionnement, structurée autour de trois modules métier.

Le projet a couvert l'ensemble du cycle :

- **Cadrage fonctionnel** : recueil des besoins, définition des modules et des règles de gestion
- **Conception et développement** : API REST Spring Boot et interface Angular
- **Recette fonctionnelle** : validation avec les utilisateurs finaux

<br>

## Modules fonctionnels

### Catalogue produits
Référentiel des terminaux commercialisés : smartphones, tablettes, ordinateurs portables, montres connectées et accessoires.
Chaque fiche décrit la marque, le modèle, la couleur, la capacité de stockage, la taille d'écran, le type de réseau et le prix.

### Fournisseurs
Gestion du portefeuille fournisseurs, avec recherche par nom, ville et pays, et filtre sur les fournisseurs actifs.

### Bons de commande
Création des commandes d'approvisionnement, rattachées à un fournisseur et à un produit, avec calcul automatique du montant total.

| Statut | Signification |
|---|---|
| `PENDING` | Commande créée, en attente de validation |
| `APPROVED` | Commande validée |
| `DELIVERED` | Marchandise réceptionnée |

Chaque bon de commande peut être **exporté en PDF**, prêt à être transmis au fournisseur.

<br>

## Architecture

```
┌──────────────────────┐        REST / JSON        ┌──────────────────────┐        JPA        ┌──────────────┐
│  Frontend Angular 20 │  ───────────────────────► │  API Spring Boot 3   │  ───────────────► │  PostgreSQL  │
│  Angular Material    │                           │  Controllers         │                   │              │
│  SSR                 │  ◄─────────────────────── │  Services · DTOs     │  ◄─────────────── │              │
└──────────────────────┘                           └──────────────────────┘                   └──────────────┘
```

**Backend** : architecture en couches (controllers, services, repositories, DTOs) et énumérations métier mappées sur des types PostgreSQL natifs. Les bons de commande sont générés en PDF avec Apache PDFBox.

**Frontend** : application Angular organisée par fonctionnalités (`core`, `features`, `layouts`, `models`), avec Angular Material et le rendu côté serveur (SSR).

**Qualité** : 24 tests unitaires et d'intégration (JUnit 5, Mockito), exécutés sur une base H2 en mémoire.

<br>

## API REST

| Ressource | Endpoints |
|---|---|
| Produits | `GET` `POST` `PUT` `DELETE` &nbsp;`/api/products` &nbsp;·&nbsp; `GET /api/products/dropdown-data` |
| Fournisseurs | `GET` `POST` `PUT` `DELETE` &nbsp;`/api/suppliers` &nbsp;·&nbsp; `GET /api/suppliers/search` |
| Bons de commande | `GET` `POST` `PUT` `DELETE` &nbsp;`/api/purchase-orders` &nbsp;·&nbsp; `GET /api/purchase-orders/{id}/pdf` |

<br>

## Installation

**Prérequis :** Java 17, Node.js 20+, PostgreSQL

```bash
git clone https://github.com/maaarwa4/Inventory-Procurement-ERP.git
cd Inventory-Procurement-ERP
```

**Base de données** : créer une base PostgreSQL, puis initialiser le schéma avec `backend/BD.sql`.

**Backend**

```bash
cd backend
export DB_URL=jdbc:postgresql://localhost:5432/nom_de_la_base
export DB_USERNAME=postgres
export DB_PASSWORD=votre_mot_de_passe
./mvnw spring-boot:run
```

**Frontend**

```bash
cd frontend
npm install
npm start
```

**Tests**

```bash
cd backend
./mvnw test
```

<br>

---

<div align="center">

**Marwa BOUNOUA** &nbsp;·&nbsp; [LinkedIn](https://linkedin.com/in/marwa-bounoua-877300263) &nbsp;·&nbsp; [GitHub](https://github.com/maaarwa4)

</div>
