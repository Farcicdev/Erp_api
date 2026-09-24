# ERP Gestão

ERP web multiempresa em desenvolvimento, criado para organizar revendas, empresas clientes, lojas e produtos em uma única plataforma. O projeto combina uma API REST em Spring Boot com um frontend React e autenticação centralizada pelo Keycloak.

> Projeto de portfólio desenvolvido para demonstrar práticas de desenvolvimento backend e frontend, modelagem de domínio, controle de acesso por organização e persistência versionada.

## Visão geral

O sistema segue esta hierarquia:

```text
Revenda
  └── Cliente
        └── Loja
              └── Produto
```

Cada loja funciona como um limite de dados para os produtos. O acesso é controlado pela combinação entre a role presente no token JWT e o vínculo do usuário com a revenda ou o cliente.

## Funcionalidades atuais

- Cadastro e consulta de revendas.
- Cadastro, consulta e alteração de status de clientes.
- Cadastro e consulta de lojas vinculadas a clientes.
- Vínculo de usuários a revendas e clientes.
- Controle de acesso organizacional com Keycloak e Spring Security.
- Cadastro de produtos por loja.
- Listagem paginada de produtos.
- Busca de produtos.
- Consulta de produto por identificador.
- Validação de dados e tratamento padronizado de erros.
- Migrations de banco de dados com Flyway.
- Testes unitários, de camada web, persistência, autorização e migrations.

## Tecnologias

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security OAuth2 Resource Server
- PostgreSQL
- Flyway
- Bean Validation
- JUnit, Mockito e AssertJ

### Frontend

- React
- TypeScript
- Vite
- Material UI
- React Router
- Keycloak JavaScript Adapter

### Infraestrutura

- Docker Compose
- PostgreSQL para a aplicação
- PostgreSQL dedicado para o Keycloak
- Keycloak

## Arquitetura

O backend é organizado por domínio, mantendo as responsabilidades separadas:

```text
Controller → Service → Repository → PostgreSQL
     DTOs ↔ Mapper ↔ Entity
```

Essa estrutura facilita a evolução por módulo e mantém as regras de negócio no serviço, em vez de concentrá-las nos controllers.

Os principais domínios são:

- `revenda`: organizações que comercializam o ERP;
- `cliente`: empresas atendidas por uma revenda;
- `loja`: estabelecimentos operacionais de um cliente;
- `produto`: catálogo de produtos separado por loja;
- `user`: usuários e seus vínculos organizacionais;
- `shared`: segurança, configuração e tratamento de exceções.

## Como executar localmente

### Pré-requisitos

- JDK 21;
- Docker e Docker Compose;
- Node.js 20 ou superior;
- npm.

### 1. Configurar variáveis de ambiente

O arquivo `.env` não deve ser versionado. Crie uma cópia do exemplo:

```bash
cp .env.example .env
```

Revise os valores locais antes de iniciar os containers.

### 2. Iniciar PostgreSQL e Keycloak

```bash
docker compose up -d
```

O Compose inicia dois bancos PostgreSQL separados e uma instância do Keycloak. A API usa o banco principal; o Keycloak usa seu próprio banco.

### 3. Configurar o Keycloak

No Keycloak local, configure:

1. o realm `erp`;
2. o client utilizado pelo frontend;
3. o client `erp-api`;
4. as roles `OWNER`, `ADMIN_REVENDA` e `ADMIN_CLIENTE`;
5. usuários de teste e seus vínculos organizacionais.

O frontend espera o Keycloak em `http://localhost:8081`. Os detalhes do ambiente podem ser ajustados na configuração local do projeto.

### 4. Executar a API

Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

A API fica disponível, por padrão, em `http://localhost:8082`. As migrations do Flyway são executadas automaticamente na inicialização.

### 5. Executar o frontend

Em outro terminal:

```bash
cd erp_frontend
npm install
npm run dev
```

O Vite informa no terminal o endereço local do frontend, normalmente `http://localhost:5173`.

## Testes e qualidade

Para executar os testes do backend:

```bash
./mvnw test
```

Para validar o frontend:

```bash
cd erp_frontend
npm run lint
npm run build
```

Os testes do backend cobrem regras de negócio, endpoints HTTP, constraints de persistência, migrations, autorização organizacional e tratamento de exceções.

## Segurança e isolamento

O token JWT identifica o usuário pelo `sub` do Keycloak. As roles indicam o tipo de operação permitida, enquanto as tabelas de vínculo determinam quais revendas, clientes e lojas podem ser acessados.

Por exemplo, possuir `ADMIN_REVENDA` não permite acessar automaticamente dados de qualquer revenda. O usuário também precisa estar vinculado à revenda correspondente.

Essa separação evita que trocar um identificador na URL seja suficiente para acessar dados de outra organização.

## Exemplos de endpoints

```text
GET  /revendas
POST /revendas

GET  /revendas/{revendaId}/clientes
POST /revendas/{revendaId}/clientes

GET  /revendas/{revendaId}/clientes/{clienteId}/lojas
POST /revendas/{revendaId}/clientes/{clienteId}/lojas

GET  /lojas/{lojaId}/produtos
POST /lojas/{lojaId}/produtos
GET  /lojas/{lojaId}/produtos/buscar?busca=...
GET  /lojas/{lojaId}/produtos/{produtoId}
```

As rotas protegidas exigem um token válido emitido pelo Keycloak.

## Próximos passos

- Completar os fluxos administrativos de cliente e loja no frontend.
- Adicionar edição e inativação de produtos.
- Ampliar a documentação OpenAPI.
- Adicionar testes automatizados para o frontend.
- Implementar estoque e movimentações por loja.
- Definir a estratégia de sincronização do futuro PDV local.
- Automatizar build e testes em CI/CD.

## Sobre o projeto

Este projeto está em evolução. As funcionalidades descritas como atuais representam o que já foi estruturado no código; os itens de próximos passos fazem parte do planejamento técnico. A proposta é evoluir o ERP de forma incremental, mantendo separação de responsabilidades, validação, testes e isolamento entre organizações.
