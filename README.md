# TechFlow Tasks

Sistema web de **gerenciamento de tarefas em fluxo Kanban** desenvolvido pela TechFlow Solutions
para uma startup fictícia de logística. O projeto demonstra, na prática, conceitos de **Engenharia
de Software**: metodologia ágil, versionamento com Git/GitHub, integração contínua, testes
automatizados e gestão de mudanças.

> Trabalho da disciplina **Engenharia de Software** — produto entregável a partir do desafio
> "Construindo um Projeto Ágil no GitHub: Da Gestão ao Controle de Qualidade".

---

## 1. Objetivo

Construir um sistema realista (CRUD + quadro Kanban) que permita à equipe da startup:

- acompanhar o fluxo de trabalho em tempo real (colunas **A Fazer / Em Progresso / Concluído**);
- priorizar tarefas críticas para a operação de logística;
- monitorar o desempenho da equipe (contagem por coluna, prazos, responsáveis);
- evoluir o produto com **mudanças de escopo controladas**, refletidas no Kanban e no código.

## 2. Escopo

### Escopo inicial

- CRUD de tarefas (criar, listar, atualizar, mover, excluir).
- Quadro Kanban com três colunas obrigatórias: **A Fazer**, **Em Progresso**, **Concluído**.
- Persistência em banco H2 em memória (suficiente para a demonstração acadêmica).
- API REST `/api/tasks` consumida pelos testes automatizados e por integrações externas.
- UI web em Thymeleaf com botões para criar, editar, mover e excluir tarefas.

### Mudança de escopo (entregue nesta versão)

Após uma reunião de revisão, o cliente pediu **priorização visual de tarefas críticas** e
**indicador de atraso** para cards com prazo vencido. As alterações foram absorvidas dentro do
mesmo sprint:

- Novo campo **prioridade** (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) com badge colorido.
- Novo campo **prazo** (`dueDate`) e marcação automática **ATRASADA** quando vencido e a tarefa
  ainda não está concluída.
- Atribuição de **responsável** (`assignee`) por card.
- Ordenação automática dos cards por prioridade decrescente dentro de cada coluna.

> A justificativa completa, o impacto no Kanban e a forma de gestão dessa mudança estão
> documentados em `docs/parte-teorica.md` e refletidos nos novos cards do GitHub Projects
> (ver seção 7).

## 3. Metodologia

Foi adotado um **híbrido Scrum + Kanban**:

- **Scrum** para o ciclo (sprint de duas semanas, planejamento, revisão e retrospectiva).
- **Kanban** para o fluxo diário e visualização contínua de WIP.

A divisão das tarefas, o backlog e a movimentação dos cards estão materializados na aba
**Projects** do repositório GitHub.

## 4. Arquitetura

```
[Browser] --HTTP--> [TaskWebController] --> [TaskService] --> [TaskRepository (JPA)] --> [H2]
                          ^                                          ^
                          |                                          |
                    [TaskApiController] (REST /api/tasks) -----------+
```

Camadas:

- **Controller (web + REST)** — entrada HTTP, validação, mapeamento.
- **Service** — regras de negócio (defaults, transições de coluna, montagem do board).
- **Repository (Spring Data JPA)** — persistência.
- **Model** — entidade `Task` + enums `TaskStatus`, `TaskPriority`.

Os diagramas UML obrigatórios (Casos de Uso e Classes) estão em `docs/parte-teorica.md`
no formato Mermaid (renderizado nativamente pelo GitHub).

## 5. Como executar

### Pré-requisitos

- **Java 17+**
- **Maven 3.9+** (ou usar `./mvnw` se preferir adicionar o wrapper)

### Rodar localmente

```bash
mvn spring-boot:run
```

Acesse:

- Quadro Kanban: <http://localhost:8080/board>
- Console H2 (dev): <http://localhost:8080/h2-console>
  - JDBC URL: `jdbc:h2:mem:techflowdb`  &nbsp;Usuário: `sa`  &nbsp;Senha: *(em branco)*

### Empacotar

```bash
mvn -DskipTests package
java -jar target/techflow-tasks-1.0.0.jar
```

### Rodar os testes

```bash
mvn test
```

A bateria atual cobre:

- regras de negócio do `TaskService` (CRUD, board, contagens, ordenação por prioridade);
- regras da entidade `Task` (defaults, `isOverdue`, `touch`);
- API REST (`/api/tasks`) via `MockMvc` (criação, validação, 404, mover, excluir, listar).

## 6. Estrutura de pastas

```
techflow-tasks/
├── .github/workflows/ci.yml        # Pipeline de Integração Contínua (GitHub Actions)
├── docs/
│   └── parte-teorica.md            # Documento da Parte 1 (escopo, metodologia, UML)
├── src/main/java/com/techflow/tasks/
│   ├── TechflowTasksApplication.java
│   ├── config/DataLoader.java      # Carga inicial (12 tarefas-exemplo)
│   ├── controller/                 # Web (Thymeleaf) + REST API
│   ├── exception/
│   ├── model/                      # Task, TaskStatus, TaskPriority
│   ├── repository/
│   └── service/
├── src/main/resources/
│   ├── application.properties
│   ├── static/css/style.css
│   └── templates/                  # board.html, form.html
├── src/test/java/com/techflow/tasks/...
├── pom.xml
└── README.md
```

## 7. Quadro Kanban (GitHub Projects)

O quadro vinculado a este repositório está disponível em
**[github.com/users/MatheusJuvino/projects/2](https://github.com/users/MatheusJuvino/projects/2)**,
com 14 cards distribuídos nas três colunas obrigatórias:

1. **A Fazer** — 5 cards
2. **Em Progresso** — 4 cards
3. **Concluído** — 5 cards

Dois desses cards estão marcados como `[Mudança de escopo]` para representar a
solicitação de priorização visual e indicador de atraso (ver seção 2).

## 8. Controle de qualidade — GitHub Actions

A pipeline em `.github/workflows/ci.yml` é disparada em cada `push` e `pull request` na branch
`main`. Ela executa:

1. checkout do código;
2. setup do JDK 17 (Temurin) com cache do Maven;
3. `mvn validate` → `compile` → `test` → `package`;
4. publicação dos relatórios do Surefire e do JAR como artefatos do workflow.

Bastam dois commits errados para o badge ficar vermelho — ótimo gatilho pedagógico para a
reflexão sobre **integração contínua**.

## 9. Histórico de commits

O repositório contém **16 commits semânticos** no padrão
[Conventional Commits](https://www.conventionalcommits.org/), cobrindo desde o
bootstrap do projeto Maven até o último ajuste no template Thymeleaf. Veja o
histórico completo em
[github.com/MatheusJuvino/techflow-tasks/commits/main](https://github.com/MatheusJuvino/techflow-tasks/commits/main).

Destaques:

- `chore: bootstrap do projeto Maven...` — base do Spring Boot
- `feat(model): entidade Task e enums...` — modelagem do domínio
- `test: cobertura JUnit 5 para Task, TaskService e API` — 25 testes
- `ci: pipeline GitHub Actions com build, testes e publicacao de artefatos`
- `feat(scope-change): DataLoader com priorizacao visual e indicador de atraso`
  — **a mudança de escopo descrita na seção 2**
- `fix(model): ordenar prioridade por peso semantico (ORDINAL)` — bug fix coberto pelos testes
- `fix(web): corrige acesso a Map com chave enum no template Thymeleaf`

## 10. Licença

Projeto acadêmico — sem licença comercial. Uso livre para fins educacionais.
