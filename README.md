# 🎮 ArcadeManager API

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/Spring_Boot-Framework-brightgreen?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/MongoDB-Database-green?style=for-the-badge&logo=mongodb" />
  <img src="https://img.shields.io/badge/API-REST-orange?style=for-the-badge" />
</p>

------------------------------------------------------------------------

## 📌 Sobre o Projeto

O **ArcadeManager** é uma API REST desenvolvida com foco no
gerenciamento de biblioteca de jogos pessoais.

A proposta do projeto é permitir que usuários cadastrem, organizem e
acompanhem os jogos que possuem, incluindo informações como status de
progresso e tipo de mídia.

------------------------------------------------------------------------

## 🚀 Objetivo

-   Gerenciar coleções pessoais de jogos
-   Controlar progresso de gameplay
-   Registrar tipo de mídia
-   Base para integração com APIs externas

------------------------------------------------------------------------

## 🧱 Arquitetura

-   Controller
-   Service
-   Repository
-   DTOs

------------------------------------------------------------------------

## 🛠️ Tecnologias

-   Java 17
-   Spring Boot
-   Spring Data MongoDB
-   Spring Security
-   MongoDB

------------------------------------------------------------------------

## 🔐 Segurança

Autenticação via JWT garantindo acesso seguro por usuário.

------------------------------------------------------------------------

## 📦 Funcionalidades

### 🎮 UserGame

-   Criar
-   Listar (paginado)
-   Atualizar
-   Deletar

------------------------------------------------------------------------

## 📡 Endpoints

POST /api/user-games\
GET /api/user-games\
PUT /api/user-games/{id}\
DELETE /api/user-games/{id}

------------------------------------------------------------------------

## 🧪 Exemplo

{ "status": "BACKLOG", "mediaType": "CARTRIDGE" }

------------------------------------------------------------------------

## 🔮 Próximas Evoluções

-   Integração com API externa de jogos (ex: IGDB)
-   Cadastro automático de jogos
-   Filtros por status e tipo de mídia
-   Sistema de favoritos
-   Dashboard do usuário
-   Catálogo global de jogos

------------------------------------------------------------------------

## ▶️ Como Executar

### Pré-requisitos

-   Java 17
-   MongoDB

### Passos

git clone https://github.com/wagnerdf/arcade-manager-api.git
cd arcade-manager-api
./mvnw spring-boot:run

------------------------------------------------------------------------

## 📌 Considerações

-   Arquitetura em camadas
-   Uso de DTOs
-   Segurança aplicada

------------------------------------------------------------------------

## 👨‍💻 Autor

Wagnerdf 🚀