# CLAUDE.md

API REST (back-end) de um sistema de gestão de associações. O front-end é um projeto Angular separado (CORS liberado para `http://localhost:4200` em `config/CorsConfig.java`).

## Stack
- Java 21, Spring Boot 4.1 (Web MVC + Data JPA / Hibernate 7), Maven
- MariaDB (`jdbc:mariadb://localhost/associacao`)
- `spring-security-crypto` apenas para hash BCrypt de senhas (não há Spring Security / autenticação ainda)

## Comandos
```bash
./mvnw spring-boot:run   # sobe a API na porta 8080
./mvnw test              # testes
```
Observações do ambiente local (Windows):
- O acesso HTTPS ao Maven Central falha com erro de certificado (PKIX), então downloads de novas dependências/plugins podem falhar. Use `mvn -o` (offline) quando as dependências já estiverem em `~/.m2`.
- `AssociacaoApplicationTests.contextLoads` precisa do MariaDB rodando; os testes em `src/test/.../service` usam Mockito e não precisam de banco.

## Configuração
- `application.properties` não contém credenciais. Elas vêm de `src/main/resources/application-local.properties` (ignorado pelo git; modelo em `application-local.properties.example`) ou das variáveis `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
- Não há `ddl-auto` nem migrations: o schema é mantido manualmente. A estrutura atual está documentada em `src/main/resources/db/schema.sql` (não é executado automaticamente). Ao alterar entidades, atualize o banco e esse arquivo.

## Arquitetura
Pacote base `com.projeto.associacao`, em camadas: `controller` → `service` → `repository` (`CrudRepository`) → `model` (entidades JPA).

Entidades: `Pessoa` (central; `tipo` 1 = física, 2 = jurídica), `Usuario` (1:1 com Pessoa, senha em BCrypt), `Membro` (1:1 com Pessoa, N:1 com `StatusMembro`), `StatusMembro`. A tabela `marco_membro` existe no banco mas ainda não tem entidade.

Endpoints (todos com barra final): `/api/pessoa/`, `/api/usuario/`, `/api/membro/`, `/api/status-membro/` com `GET /`, `POST /`, `PUT /`, `DELETE /{id}`.

## Convenções do código
- Nomes em português (classes, métodos, mensagens). Métodos de service: `selecionar`, `cadastrar`, `alterar`, `remover`.
- Regras de negócio ficam no service e lançam `BusinessRuleException` (checked); o `CustomExceptionHandler` converte em HTTP 400 com a mensagem no corpo.
- Validação de texto obrigatório: `(x == null) || x.isBlank()`. No `cadastrar`, o id recebido é zerado; `alterar` e `remover` verificam se o registro existe.
- Entidades com relacionamento recebem ids no request e são expostas via DTOs (`dto/usuario`, `dto/membro`): `XxxRequest` (com `idPessoa`, `idStatus`...) e `XxxResponse` (com os objetos). O service tem `validarXxx(request, edicao)`, que monta a entidade, e `converterParaResponse`. A senha nunca é devolvida.
- Repositórios usam `findById(int)` retornando a entidade ou `null` (não `Optional`), e `findByPessoa_Id` para checar unicidade.
- Colunas do banco em minúsculas sem underscore (`datanascimento`, `idpessoa`, `idstatus`): mapeie com `@Column`/`@JoinColumn` explícitos, pois o padrão do Spring gera `snake_case`.
- Indentação: tabs na maioria dos arquivos (alguns usam 4 espaços; siga o do arquivo editado).
