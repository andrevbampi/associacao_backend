# CLAUDE.md

API REST (back-end) de um sistema de gestão de associações. O front-end é o repositório React separado `associacao_react` (Vite, dev server fixado em `localhost:4200`; ver seu próprio `CLAUDE.md`/`README.md`).

## Stack
- Java 21, Spring Boot 4.1 (Web MVC + Data JPA / Hibernate 7), Maven
- MariaDB (`jdbc:mariadb://localhost/associacao`)
- Spring Security + JWT (`io.jsonwebtoken`/jjwt) para autenticação. `spring-security-crypto` (BCrypt) para hash de senha.

## Comandos
```bash
./mvnw spring-boot:run   # sobe a API na porta 8080
./mvnw test              # testes
```
Observações do ambiente local (Windows):
- O acesso HTTPS ao Maven Central falha com erro de certificado (PKIX) especificamente para `mvn dependency:*` e para o plugin do Surefire quando ele ainda não está em cache; `mvn compile`/`mvn test-compile` funcionam normalmente online. Use `mvn -o` (offline) quando as dependências já estiverem em `~/.m2`.
- `AssociacaoApplicationTests.contextLoads` precisa do MariaDB rodando; os testes em `src/test/.../service` e `.../security` usam Mockito e não precisam de banco.

## Configuração
- `application.properties` não contém credenciais. Elas vêm de `src/main/resources/application-local.properties` (ignorado pelo git; modelo em `application-local.properties.example`) ou das variáveis `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
- `jwt.secret` (>= 32 bytes, HS256) e `jwt.expiration-ms` também são configuráveis via `JWT_SECRET`/`JWT_EXPIRATION_MS`; o valor padrão em `application.properties` é só para dev.
- Não há `ddl-auto` nem migrations: o schema é mantido manualmente. A estrutura atual está documentada em `src/main/resources/db/schema.sql` (não é executado automaticamente). Ao alterar entidades, atualize o banco e esse arquivo.

## Autenticação (JWT)
- `security/`: `SecurityConfig` (filter chain stateless, CORS, libera só `POST /api/auth/login` e preflight `OPTIONS`; todo o resto exige token), `JwtService` (gera/valida o token), `JwtAuthenticationFilter` (lê o header `Authorization`), `JwtAuthenticationEntryPoint` (401 em texto puro nas rotas protegidas sem token válido, mesmo formato do `CustomExceptionHandler`).
- `AuthController`/`AuthService`: `POST /api/auth/login` (valida com `passwordEncoder.matches`, devolve token + `UsuarioResponse`) e `GET /api/auth/me` (usado pelo front para restaurar sessão).
- Em qualquer service que precise saber "quem está fazendo a ação" (ex.: `HistoricoMembroService`), o controller pega o login via `Authentication.getName()` (injetado pelo Spring Security a partir do JWT) e passa para o service — nunca confie em um campo tipo `idUsuarioRegistro` vindo do corpo da requisição.

## Arquitetura
Pacote base `com.projeto.associacao`, em camadas: `controller` → `service` → `repository` (`CrudRepository`) → `model` (entidades JPA).

Entidades: `Pessoa` (central; `tipo` 1 = física, 2 = jurídica), `Usuario` (1:1 com Pessoa, senha em BCrypt), `Membro` (1:1 com Pessoa, N:1 com `StatusMembro`), `StatusMembro`, `TipoEvento`, `HistoricoMembro` (N:1 com Membro/TipoEvento/Usuario — linha do tempo de eventos de um membro), `Produto` (preço normal + preço membro), `Comanda` (N:1 opcional com Pessoa; `nomeTemporario` para visitante sem cadastro; `status` é o enum `StatusComanda` — ABERTA/FECHADA/CANCELADA), `ItemComanda` (N:1 com Comanda/Produto). A tabela `marco_membro` existe no banco mas ainda não tem entidade.

Endpoints (todos com barra final, exceto os de ação/nested):
- `/api/pessoa/`, `/api/usuario/`, `/api/membro/`, `/api/status-membro/`, `/api/tipo-evento/`, `/api/produto/`: `GET /`, `POST /`, `PUT /`, `DELETE /{id}`.
- `/api/historico-membro/`: além do CRUD padrão, `GET /membro/{idMembro}` lista o histórico de um membro específico.
- `/api/comanda/`: `GET /` (aceita `?status=ABERTA|FECHADA|CANCELADA`), `GET /{id}` (única entidade com GET por id — inclui os itens), `POST /` (abre), `POST /{id}/itens`, `PUT /{id}/itens/{idItem}`, `DELETE /{id}/itens/{idItem}`, `PUT /{id}/fechar`, `PUT /{id}/cancelar`. Não tem `DELETE /{id}` (comanda se cancela, não se apaga).
- `/api/auth/login` (público), `/api/auth/me` (protegida).

### Regra de precificação da Comanda
Ao adicionar um item, o service (`ComandaService.precoParaComanda`) usa `produto.precoMembro` se a pessoa da comanda tiver um registro em `Membro` com `ativo = true` (via `MembroRepository.findByPessoa_Id`); caso contrário (visitante ou pessoa sem membresia ativa), usa `produto.preco`. O preço é sempre calculado no servidor, nunca aceito do cliente.

## Convenções do código
- Nomes em português (classes, métodos, mensagens). Métodos de service: `selecionar`, `cadastrar`, `alterar`, `remover` (Comanda foge um pouco disso por ter um fluxo de estados: `abrir`, `adicionarItem`, `fechar`, `cancelar`).
- Regras de negócio ficam no service e lançam `BusinessRuleException` (checked); o `CustomExceptionHandler` converte em HTTP 400 com a mensagem no corpo. Falha de login/token usa `CredenciaisInvalidasException` → HTTP 401, mesmo formato de corpo (texto puro).
- Validação de texto obrigatório: `(x == null) || x.isBlank()`. No `cadastrar`, o id recebido é zerado; `alterar` e `remover` verificam se o registro existe.
- Entidades com relacionamento recebem ids no request e são expostas via DTOs (`dto/usuario`, `dto/membro`, `dto/historicoMembro`, `dto/comanda`): `XxxRequest` (com `idPessoa`, `idStatus`...) e `XxxResponse` (com os objetos). O service tem `validarXxx(request, edicao)`, que monta a entidade, e `converterParaResponse` — este último é `public` nos services que outros services reaproveitam (ex.: `MembroService`, `UsuarioService`) para não duplicar a montagem do DTO. A senha nunca é devolvida.
- Repositórios usam `findById(int)` retornando a entidade ou `null` (não `Optional`; cuidado com overload ambíguo ao passar um `Integer` boxed — use `.intValue()`), e `findByXxx_Id`/`existsByXxx_Id` para checar unicidade e vínculos antes de excluir.
- Colunas do banco em minúsculas sem underscore (`datanascimento`, `idpessoa`, `idstatus`, `nometemporario`, `datainclusao`...): mapeie com `@Column`/`@JoinColumn` explícitos, pois o padrão do Spring gera `snake_case`.
- Dinheiro é `BigDecimal` (nunca `double`/`float`), coluna `DECIMAL(10,2)`.
- Indentação: tabs na maioria dos arquivos (alguns arquivos antigos usam 4 espaços; siga o do arquivo editado — os arquivos criados a partir da feature de autenticação em diante usam tabs).
