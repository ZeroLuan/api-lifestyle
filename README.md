## 📋 Requisitos Mínimos

Antes de começar, você precisará ter instalado em sua máquina:
*   [Git](https://git-scm.com/) (para clonar o projeto)
*   [Docker](https://www.docker.com/products/docker-desktop/) (com Docker Compose)
*   *Opcional*: [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou VS Code (para edição de código)

---

## ⚡ Como Rodar o Projeto (Instalação Rápida)

O projeto está totalmente "containerizado" via Docker. Você não precisa instalar Java ou PostgreSQL localmente.

### 1. Clonar e Iniciar
Abra o terminal na pasta raiz do projeto e execute:
```bash
docker-compose up --build
```

### 2. Aguardar a Inicialização
Aguarde até que a mensagem abaixo apareça no log do terminal:
`bootcamp-backend  | Started BackendApplication in ... seconds`

### 3. Acessar a Documentação e Testar (Swagger)
Toda a documentação dos endpoints e a interface para testes (Swagger) ficam disponíveis em:
🔗 **[http://localhost:8080/sysmap-docs.html](http://localhost:8080/sysmap-docs.html)**

---

## 💻 Alternativa: Rodando para Desenvolvimento (IDE + Docker Infra)

Se você deseja debugar o código ou fazer alterações rápidas, pode subir apenas a infraestrutura no Docker e rodar o Java diretamente na sua IDE.

### 1. Subir a Infraestrutura (Postgres + LocalStack)
No terminal, execute:
```bash
docker-compose up -d postgres localstack
```
*Isso iniciará o banco de dados na porta `5433` e o LocalStack na porta `4566` em segundo plano.*

### 2. Rodar o Backend na IDE
1. Abra o projeto na sua IDE favorita (IntelliJ, VS Code, etc).
2. Certifique-se de ter o **Java 25** configurado.
3. Execute a classe principal: `br.com.sysmap.backend.BackendApplication`.
4. O backend estará acessível em **[http://localhost:8080](http://localhost:8080)**.

---

## 🛠 Principais Tecnologias
- **Linguagem**: Java 25
- **Framework**: Spring Boot 4.0.6 (Spring Security + JWT)
- **Banco de Dados**: PostgreSQL (Migrations via Liquibase)
- **Storage**: LocalStack (Simulando AWS S3 para fotos)
- **Testes**: JUnit 5 & Mockito


---

## 🧪 Rodando os Testes Unitários
Para validar a integridade do código e as regras de negócio:
```bash
./gradlew test
```

---

**Candidato:** Luan Alves de Souza
**Bootcamp:** 2026-05-05
