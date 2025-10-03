## sistemaDePagamento

Documentação e manual de instrução para o projeto `sistemaDePagamento` (API REST em Spring Boot para registro de usuários, autenticação JWT e integração com Pix via SDK EfiPay).

## Visão geral

Este projeto é uma API em Java (Spring Boot 3.x) que fornece:

- Cadastro de usuários com verificação por email.
- Autenticação via JWT (/auth/login).
- Endpoints para criação de cobranças Pix e geração de EVP/QrCode usando o SDK EfiPay.

Estrutura principal:

- `com.sistemaPagamento.sistemaDePagamento.controller` : controladores REST.
- `...dto` : objetos de transferência (requests/responses).
- `...entity` : entidades JPA (ex.: `User`).
- `...services` : regras de negócio (TokenService, PixService, UserService, MailService).
- `...pix` : classes auxiliares para credenciais do PSP.

## Endpoints importantes

1. Autenticação

- POST /auth/login
  - Request JSON: `{"email":"user@example.com","password":"senha"}`
  - Response JSON: `{"token":"<JWT>"}`
  - Uso: retorna token JWT com validade (30 minutos).

2. Usuário

- POST /user/register

  - Request JSON (validações aplicadas):
    - name (string, required)
    - email (string, required)
    - password (string, required, mínimo 8 chars)
    - role (string, required)
  - Response: `UserResponse` com id, name, email, role, enabled
  - Efeito: salva usuário com senha codificada (BCrypt), gera `verificationCode` e envia email de verificação via `MailService`.

- GET /user/verify?code=<code>

  - Usa o `verificationCode` enviado por email. Ativa a conta (`enabled = true`). Retorna `verify_success` ou `verify_error`.

- GET /user/all

  - Lista todos os usuários (mapeado para `UserResponse`). Atualmente aberto (permitAll) para facilitar testes.

- GET /user/teste
  - Retorna string simples indicando que o usuário está logado (requer autenticação).

3. Pix

- GET /api/v1/pix

  - Chama `pixCreateEVP()` no `PixService`. Retorna JSON do PSP (EVP informações).

- POST /api/v1/pix
  - Request JSON: `{"chave":"<chave-pix>","valor":"<valor>"}` (use `PixChargeRequest`)
  - Cria cobrança imediata com corpo preenchido no `PixService` e tenta gerar QR Code (salva `qrCodeImage.png` e tenta abrir no Desktop).

Observação de segurança: O `SecurityConfig` libera sem autenticação os endpoints:

- POST /user/register
- GET /user/verify
- POST /auth/login
- GET /user/all
  qualquer outro endpoint exige JWT no header `Authorization: Bearer <token>`.

## DTOs e entidade User (resumo)

- AuthenticationRequest: (email, password)
- AuthenticationResponse: (token)
- PixChargeRequest: (chave, valor)
- UserRequest: (name, email, password, role)
- UserResponse: (id, name, email, role, enabled)

Entidade `User` (campos relevantes):

- id: Long
- name: String
- email: String
- password: String (armazenada criptografada)
- verificationCode: String
- enabled: boolean
- role: String

## Variáveis de ambiente / configuração

Arquivos principais de configuração:

- `src/main/resources/application-dev.properties` (dev)
- `src/main/resources/application-prod.properties` (prod, vazio no repositório)
- `src/main/resources/credentials.json` (credenciais do PSP/EfiPay)

Propriedades e variáveis necessárias (substitua pelos seus valores reais):

- Banco de dados (dev - `application-dev.properties`):

  - spring.datasource.url (ex.: jdbc:mysql://localhost:3306/payments)
  - spring.datasource.username
  - spring.datasource.password
  - spring.jpa.hibernate.ddl-auto

- Email (usado por `MailService`):

  - spring.mail.username = ${EMAIL_USERNAME}
  - spring.mail.password = ${EMAIL_PASSWORD}
    Essas variáveis são usadas via substitution no `application-dev.properties`.

- JWT secret:

  - `jwt.secret=${JWT_SECRET}` usado pelo `TokenService` (injetado via @Value). Defina `JWT_SECRET` no ambiente.

- Arquivo `credentials.json` (exemplo no repositório):
  {
  "client_id": "client_id",
  "client_secret": "client_secret",
  "certificate": "./certs/producao-836989-PaymentSystem.p12",
  "sandbox": false,
  "debug": false
  }
  - O `Credentials` class lê esse arquivo do classpath e resolve o caminho do certificado. Garanta que o `certificate` aponte para `certs/producao-836989-PaymentSystem.p12` (existente no projeto).

## Dependências relevantes

Veja `pom.xml`. Destaques:

- Spring Boot Starters: web, data-jpa, mail, security, validation
- MySQL Connector
- com.auth0:java-jwt (para JWT)
- br.com.efipay.efisdk (SDK EfiPay para Pix)
- org.json (manipulação JSON)

## Como executar localmente (Windows PowerShell)

Pré-requisitos:

- Java 17+
- Maven (ou use o wrapper `mvnw.cmd` incluso)
- MySQL (ou ajuste `spring.datasource.url` para seu banco)

Exemplo de sessão PowerShell (defina variáveis sensíveis antes de rodar):

```powershell
# No PowerShell, defina variáveis de ambiente temporárias
$env:JWT_SECRET = "sua_chave_jwt_secreta"
$env:EMAIL_USERNAME = "seu.email@gmail.com"
$env:EMAIL_PASSWORD = "senha_de_app_ou_password"

# Construir e executar (usando wrapper no Windows)
cd C:\Users\eskatista\Desktop\Sistema-de-Pagamento\sistemaDePagamento
.\mvnw.cmd -DskipTests package
java -jar target\sistemaDePagamento-0.0.1-SNAPSHOT.jar
```

Alternativa (modo desenvolvimento):

- `mvn spring-boot:run` (ou `./mvnw spring-boot:run` no Windows use `mvnw.cmd`).

Observação: se usar Docker, existe `docker-compose.yml` na raíz do workspace — você pode construir e rodar via `docker-compose up --build` (confirme os serviços e variáveis no compose).

## Exemplos de requisições (cURL)

# Login

curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"senha"}'

# Criar usuário

curl -X POST http://localhost:8080/user/register -H "Content-Type: application/json" -d '{"name":"Fulano","email":"fulano@example.com","password":"minhasenha","role":"USER"}'

# Criar cobrança Pix

curl -X POST http://localhost:8080/api/v1/pix -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"chave":"00000000000","valor":"10.00"}'

## Troubleshooting / dicas

- Erro de autenticação de email (SMTP): verifique se `EMAIL_USERNAME`/`EMAIL_PASSWORD` estão corretos. Para Gmail, crie senha de app e habilite acesso necessário.
- Banco não conecta: verifique `spring.datasource.url`, usuário e senha. Confirme que o MySQL está rodando e que o schema `payments` existe ou permita o Hibernate criar (ddl-auto=update).
- JWT inválido: confira `JWT_SECRET` consistente entre aplicações.
- EfiPay / certificado:
  - O `credentials.json` deve apontar para o arquivo `certs/...p12` presente no projeto. O `Credentials` tenta resolver no classpath e retorna caminho absoluto.
  - Se o SDK lançar exceção, verifique os logs (o `PixService` já imprime detalhes) e confirme client_id/client_secret e se está em sandbox/produção.
- Problemas ao gerar/abrir QR Code: o código salva `qrCodeImage.png` na raiz quando `pixGenerateQRCode` consegue `imagemQrcode` em base64. Permissões e disponibilidade do Desktop (Desktop.getDesktop().open) podem variar em servidores headless.

## Segurança e produção

- Nunca comitar segredos (JWT, senhas, client_secret) no repositório. Use variáveis de ambiente ou soluções de secret manager.
- Configure `application-prod.properties` com dados de produção e remova permissões abertas a endpoints públicos conforme necessário.
- Rotas abertas atualmente (ex.: `/user/all`) devem ser restritas em produção.

## Próximos passos sugeridos

- Adicionar logs estruturados e nível de log por profile.
- Implementar roles/authorities e retornar `getAuthorities()` na entidade `User`.
- Ajustar endpoints Pix para expor apenas dados necessários e tratar erros com responses HTTP apropriados (códigos e bodies padronizados).
- Adicionar testes de integração para endpoints críticos (register, login, pix).

## Arquivos importantes para revisar

- `src/main/resources/credentials.json` — credenciais do PSP.
- `src/main/resources/application-dev.properties` — configurações locais (DB, mail, jwt).
- `certs/producao-836989-PaymentSystem.p12` — certificado utilizado pela integração Pix.

---

Se quiser, eu posso:

- Gerar exemplos de requests com JSON prontos para o Postman.
- Criar um docker-compose/devcontainer pronto com MySQL e variáveis de ambiente para facilitar testes.
- Restringir endpoints públicos no `SecurityConfig` e adicionar roles/authorities.
