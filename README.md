# 🚪 ATMate Gateway Service



O `atmate-gateway` é o **serviço central** no ecossistema ATMate, atuando como a **interface principal** entre o backend e a aplicação [ATMate Client](https://github.com/ATMate-Portal/atmate-client/) (React/TypeScript). Expõe uma API RESTful estruturada para consumo eficiente e seguro dos dados armazenados e geridos pelo [ATMate Integration](https://github.com/ATMate-Portal/atmate-integration/).

---


## ✨ Funcionalidades

### 🌐 API RESTful

- Endpoints para:
  - Clientes
  - Obrigações Fiscais
  - Notificações
- API intuitiva e documentada (Swagger).

### 🧩 Agregação e Mapeamento de Dados

- Recolha e junção de dados de múltiplas entidades.
- Conversão de entidades JPA em DTOs otimizados.

### 🛡️ Validação de Requisições

- Garante segurança e integridade dos dados recebidos.

### 🚨 Tratamento de Exceções

- Respostas padronizadas e informativas ao frontend.

---

## 🚀 Tecnologias Utilizadas

### Backend (Java)

- `Spring Boot`, `Spring Data JPA`, `Lombok`, `SLF4J`

### Base de Dados

- `MySQL`

### Outros

- `Maven`
- `Swagger` (SpringDoc OpenAPI)

---

## 📦 Estrutura do Projeto

```bash
atmate-gateway/
├── src/main/java/com/atmate/portal/gateway/atmategateway/
│   ├── controller/          # Controladores REST
│   ├── database/
│   │   ├── entities/        # Entidades JPA
│   │   ├── repos/           # Repositórios JPA
│   │   └── services/        # Serviços de dados (ClientService, etc.)
│   ├── dto/                 # Data Transfer Objects (ClientResponse, etc.)
│   ├── utils/               # Utilitários e exceções
│   ├── config/             # Configurações e segurança
│   └── AtmateGatewayApplication.java
├── src/main/resources/
│   └── application.properties
├── src/test/java/           # Testes
├── pom.xml
└── README.md
```

---

## 📄 Documentação da API

Aceder via Swagger: [http://localhost:8180/atmate-gateway/swagger-ui/index.html](http://localhost:8180/atmate-gateway/swagger-ui/index.html)


