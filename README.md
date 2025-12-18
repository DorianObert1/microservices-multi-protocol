#  Microservices Multi-Protocol

Architecture microservices complète avec **REST**, **GraphQL** et **gRPC**.

##  Vue d'ensemble

Ce projet démontre une architecture microservices moderne utilisant trois protocoles de communication complémentaires :

| Protocole | Usage | Avantages |
|-----------|-------|-----------|
| **REST** | APIs publiques | Standard, cacheable, simple |
| **GraphQL** | BFF Mobile/Web | Requêtes flexibles, agrégation |
| **gRPC** | Communication interne | Ultra-rapide, typage fort, streaming |

## 🏗️ Architecture

```
                            ┌─────────────────────┐
                            │    API Gateway      │
                            │      (8080)         │
                            └──────────┬──────────┘
                                       │
         ┌─────────────────────────────┼─────────────────────────────┐
         │                             │                             │
         ▼                             ▼                             ▼
┌─────────────────┐          ┌─────────────────┐          ┌─────────────────┐
│  GraphQL BFF    │          │   REST APIs     │          │   REST APIs     │
│    (8084)       │          │                 │          │                 │
└────────┬────────┘          └────────┬────────┘          └────────┬────────┘
         │                            │                            │
         │ gRPC                       │                            │
         │                            │                            │
         ▼                            ▼                            ▼
┌─────────────────┐          ┌─────────────────┐          ┌─────────────────┐
│  User Service   │◄────────►│ Product Service │◄────────►│  Order Service  │
│  REST: 8081     │  gRPC    │  REST: 8082     │  gRPC    │  REST: 8083     │
│  gRPC: 9091     │          │  gRPC: 9092     │          │  gRPC: 9093     │
└─────────────────┘          └─────────────────┘          └─────────────────┘
         │                            │                            │
         ▼                            ▼                            ▼
    ┌─────────┐                 ┌─────────┐                 ┌─────────┐
    │   H2    │                 │   H2    │                 │   H2    │
    │ userdb  │                 │productdb│                 │ orderdb │
    └─────────┘                 └─────────┘                 └─────────┘
```

## 📦 Modules

| Module | Description | Port REST | Port gRPC |
|--------|-------------|-----------|-----------|
| `common-proto` | Définitions Protobuf partagées | - | - |
| `user-service` | Gestion des utilisateurs | 8081 | 9091 |
| `product-service` | Catalogue produits | 8082 | 9092 |
| `order-service` | Gestion des commandes | 8083 | 9093 |
| `graphql-bff` | Backend For Frontend GraphQL | 8084 | - |
| `api-gateway` | Point d'entrée unique | 8080 | - |

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.2.0** (Spring MVC - non reactive)
- **Spring Data JPA** + H2 Database
- **gRPC** 1.59.0 + Protobuf 3.25.1
- **Spring GraphQL**
- **Spring Cloud Gateway MVC**

## 🚀 Démarrage

### Prérequis

- Java 17+
- Maven 3.8+

### Compilation

```bash
cd microservices-multi-protocol

# Compiler tout le projet
mvn clean install
```

### Démarrage des services (dans l'ordre)

```bash
# Terminal 1 - User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Product Service
cd product-service
mvn spring-boot:run

# Terminal 3 - Order Service (dépend de user et product)
cd order-service
mvn spring-boot:run

# Terminal 4 - GraphQL BFF
cd graphql-bff
mvn spring-boot:run

# Terminal 5 - API Gateway (optionnel)
cd api-gateway
mvn spring-boot:run
```

## 📡 Tester les APIs

### REST (via Gateway ou directement)

```bash
# Users
curl http://localhost:8080/api/users
curl http://localhost:8080/api/users/1

# Products
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/category/LAPTOP

# Orders
curl http://localhost:8080/api/orders
curl http://localhost:8080/api/orders/user/1

# Créer une commande
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {"productId": 1, "quantity": 1},
      {"productId": 2, "quantity": 2}
    ],
    "shippingAddress": "123 Main Street, Paris"
  }'
```

### GraphQL

Ouvrir GraphiQL dans le navigateur : http://localhost:8084/graphiql

**Exemples de requêtes :**

```graphql
# Récupérer tous les utilisateurs avec leurs commandes
query {
  users {
    id
    username
    email
    orders {
      id
      totalAmount
      status
    }
  }
}

# Récupérer les produits par catégorie
query {
  productsByCategory(category: "LAPTOP") {
    id
    name
    price
    stock
  }
}

# Créer un utilisateur
mutation {
  createUser(input: {
    username: "newuser"
    email: "new@example.com"
    firstName: "New"
    lastName: "User"
    password: "secret123"
  }) {
    id
    username
  }
}

# Créer une commande
mutation {
  createOrder(input: {
    userId: 1
    items: [
      {productId: 1, quantity: 2}
      {productId: 3, quantity: 1}
    ]
    shippingAddress: "456 Avenue des Champs"
  }) {
    id
    totalAmount
    status
    items {
      productName
      quantity
      totalPrice
    }
  }
}
```

## 🔄 Communication gRPC

Le service Order communique avec User et Product via gRPC :

1. **Validation utilisateur** : Vérifie que l'utilisateur existe
2. **Récupération produits** : Obtient les infos et prix des produits
3. **Mise à jour stock** : Décrémente le stock après commande

Le BFF GraphQL agrège les données de tous les services via gRPC.

## 📊 Données de démo

### Users (5)
- john.doe, jane.smith, bob.wilson, alice.johnson, charlie.brown

### Products (10)
- MacBook Pro, iPhone 15 Pro, iPad Air, AirPods Pro, Apple Watch Ultra
- Samsung Galaxy S24, Sony WH-1000XM5, Dell XPS 15, Nintendo Switch, PS5

### Categories
- LAPTOP, PHONE, TABLET, AUDIO, WATCH, GAMING

## 🔍 Monitoring

- **Actuator** : http://localhost:8080/actuator/health
- **H2 Console** :
  - User DB: http://localhost:8081/h2-console (jdbc:h2:mem:userdb)
  - Product DB: http://localhost:8082/h2-console (jdbc:h2:mem:productdb)
  - Order DB: http://localhost:8083/h2-console (jdbc:h2:mem:orderdb)

## 📁 Structure du projet

```
microservices-multi-protocol/
├── pom.xml                      # Parent POM
├── common-proto/                # Définitions Protobuf
│   └── src/main/proto/
│       ├── user.proto
│       ├── product.proto
│       └── order.proto
├── user-service/                # Service utilisateurs
│   └── src/main/java/
│       ├── model/
│       ├── dto/
│       ├── repository/
│       ├── service/
│       ├── controller/          # REST
│       └── grpc/                # gRPC Server
├── product-service/             # Service produits
├── order-service/               # Service commandes
│   └── src/main/java/
│       ├── client/              # gRPC Clients
│       └── ...
├── graphql-bff/                 # Backend For Frontend
│   └── src/main/
│       ├── java/
│       │   ├── client/          # gRPC Clients
│       │   └── resolver/        # GraphQL Resolvers
│       └── resources/graphql/
│           └── schema.graphqls
└── api-gateway/                 # API Gateway
```

## 🎯 Points clés de l'architecture

1. **Séparation des responsabilités** : Chaque service gère son domaine
2. **Communication interne efficace** : gRPC pour les appels inter-services
3. **API flexible** : GraphQL permet aux clients de demander exactement ce dont ils ont besoin
4. **Point d'entrée unique** : API Gateway centralise le routage
5. **Non-reactive** : Spring MVC classique (synchrone/bloquant) pour simplicité
