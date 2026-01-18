# Administrator Shop - Application E-Commerce Full-Stack

Application e-commerce complète avec un frontend Vue.js et un backend Spring Boot, comprenant l'authentification sécurisée, la gestion des produits et le traitement des commandes.

## Démarrage du Backend

# Ouvrir le projet dans IntelliJ IDEA
cd D:\backend

# Lancer l'application Spring Boot depuis IntelliJ
# OU via Maven
mvn spring-boot:run

# Le backend sera disponible sur http://localhost:8080

## Démarrage du Frontend

### Stack Technologique
- Vue.js 3 - Framework frontend
- Vue Router 4 - Routage côté client
- Axios - Client HTTP
- Tailwind CSS - Framework CSS (thème rouge/noir)
- Vite - Outil de build

### Installation et Lancement

cd D:\client

# Installer les dépendances
npm install

# Lancer le serveur de développement
npm run dev

# Build pour la production
npm run build

Le frontend sera disponible sur http://localhost:5173 (ou 5174/5175 si occupé)

## Pages Disponibles

### Pages Publiques
- Page d'Accueil - Liste des produits avec recherche et filtres
- Connexion - Authentification utilisateur
- Inscription - Enregistrement nouveau utilisateur

### Pages Utilisateur (Authentifiées)
- Profil - Gestion du profil utilisateur
- Commandes - Historique et gestion des commandes

### Pages Admin (ADMIN uniquement)
- Dashboard Admin - Gestion complète des produits
- Gestion Utilisateurs - Administration des utilisateurs
- Gestion Commandes - Traitement des commandes

## Endpoints API

### Authentification (Public)
- POST /api/auth/register - Inscription utilisateur
- POST /api/auth/login - Connexion (retourne JWT token)

### Produits (Public en lecture)
- GET /api/products?page=0&size=12 - Liste paginée des produits
- GET /api/products/{id} - Détails d'un produit
- GET /api/products/search?q=terme - Recherche de produits

### Commandes (Authentifié)
- POST /api/orders - Créer une nouvelle commande
- GET /api/orders/my-orders - Consulter ses commandes

### Admin (ADMIN uniquement)
- GET /api/admin/users - Liste tous les utilisateurs
- POST /api/admin/products - Créer un produit
- PUT /api/admin/products/{id} - Modifier un produit
- DELETE /api/admin/products/{id} - Supprimer un produit
- PUT /api/admin/users/{id} - Modifier un utilisateur

## Rôles et Permissions

### Rôle USER
- Consulter les produits
- Créer et gérer ses commandes
- Voir l'historique de commandes
- Mettre à jour son profil

### Rôle ADMIN
- Toutes les permissions USER
- Gérer les produits (CRUD)
- Gérer les utilisateurs et leurs rôles
- Gérer toutes les commandes

### URL de l'API

Dans client/src/api/client.js :

const API_URL = 'http://localhost:8080/api'

## Design - Thème Rouge & Noir

Le projet utilise Tailwind CSS avec un thème personnalisé :
- Fond : Dégradés noir/gris foncé (from-gray-900 to-black)
- Accents : Rouge (red-600, red-700)
- Navigation : Dégradé rouge vers noir
- Cartes : Noir avec bordures rouges au survol
- Effets : Ombres lumineuses rouges

## Problèmes Courants et Solutions

### Les Produits ne S'Affichent Pas
- Vérifiez que le backend est démarré sur port 8080
- Ouvrez la console (F12) pour voir les erreurs
- Vérifiez que des produits existent en base de données

## Workflow de Développement

1. Démarrer le Backend (IntelliJ IDEA)
   cd D:\backend
   # Clic droit sur BackendApplication.java → Run

2. Démarrer le Frontend (nouveau terminal)
   cd D:\client
   npm run dev

3. Accéder à l'Application
   - Frontend : http://localhost:5175
   - Backend : http://localhost:8080

4. Tester les Fonctionnalités
   - S'inscrire avec un nouveau compte
   - Se connecter
   - Parcourir les produits
   - Créer une commande
   - Accéder au panneau admin (avec rôle ADMIN)

## Modèles de Données

### User (Utilisateur)
- id, username, email, password, roles, enabled, createdAt

### Product (Produit)
- id, name, description, price, category, stockQuantity, lienImage, createdAt

### Categorie
- id, name

### Order (Commande)
- id, user, orderDate, totalAmount, status, produit, quantite

Status possibles : PENDING, PROCESSING, SHIPPED, DELIVERED

## Sécurité

Authentification par JWT (JSON Web Token)
Contrôle d'accès basé sur les rôles (RBAC)
Hashage sécurisé des mots de passe (BCrypt)
Endpoints API protégés
Validation CORS
Intercepteur Axios pour injection automatique du token

## Déploiement

### Backend
cd D:\backend
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar

### Frontend
cd D:\client
npm run build
# Déployer le dossier dist/ sur un serveur web

## Fonctionnalités Implémentées

Inscription et connexion utilisateur
Authentification JWT
Contrôle d'accès par rôles (USER, ADMIN)
Liste et recherche de produits
Page de détail produit
Gestion du profil utilisateur
Création et historique de commandes
Dashboard admin
Gestion des produits (CRUD)
Gestion des utilisateurs
Design responsive avec Tailwind CSS (thème rouge/noir)
Routes protégées
Gestion des erreurs API

## Technologies Utilisées

Backend :
- Spring Boot 3.2.0
- Spring Security 6.2.0
- Spring Data JPA
- MySQL
- JWT (io.jsonwebtoken)
- Lombok

Frontend :
- Vue.js 3
- Vue Router 4
- Axios
- Tailwind CSS
- Vite

## Documentation Supplémentaire

- Vue.js Documentation : https://vuejs.org/
- Tailwind CSS Documentation : https://tailwindcss.com/
- Spring Boot Documentation : https://spring.io/projects/spring-boot
- Spring Security Documentation : https://spring.io/projects/spring-security

## Développement

Outils Recommandés :
- IntelliJ IDEA (Backend)
- VS Code (Frontend)
- Vue.js DevTools (Extension navigateur)
- MySQL Workbench
