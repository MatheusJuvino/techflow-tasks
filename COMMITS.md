# Roteiro de commits — TechFlow Tasks

O trabalho exige **no mínimo 10 commits** com mensagens descritivas. Este roteiro define
**12 commits** distribuídos ao longo do desenvolvimento, no estilo
[Conventional Commits](https://www.conventionalcommits.org/), o que ajuda na avaliação e
demonstra boas práticas reais de mercado.

> Para gerar o histórico **automaticamente**, rode `scripts/setup-commits.ps1` (PowerShell)
> ou `scripts/setup-commits.sh` (bash). O script reorganiza os arquivos e faz cada commit
> separadamente. Veja a seção *"Modo automático"* ao final.

## Lista dos 12 commits (em ordem)

| # | Mensagem | O que adiciona |
|---|---|---|
| 01 | `chore: bootstrap do projeto Maven (Spring Boot 3.3, Java 17)` | `pom.xml`, `.gitignore` |
| 02 | `docs: README inicial com objetivo, escopo e como executar` | `README.md` (versão inicial) |
| 03 | `feat(model): entidade Task e enum TaskStatus para o Kanban` | `Task.java`, `TaskStatus.java` |
| 04 | `feat(repo): TaskRepository com Spring Data JPA` | `TaskRepository.java` |
| 05 | `feat(service): TaskService com CRUD + montagem do board Kanban` | `TaskService.java`, `TaskNotFoundException.java` |
| 06 | `feat(api): TaskApiController expõe /api/tasks (REST)` | `TaskApiController.java` |
| 07 | `feat(web): TaskWebController + templates Thymeleaf do Kanban` | `TaskWebController.java`, `board.html`, `form.html`, `style.css` |
| 08 | `test: cobertura JUnit 5 para Task, TaskService e API` | `TaskTest.java`, `TaskServiceTest.java`, `TaskApiControllerTest.java`, `application.properties` (test) |
| 09 | `ci: pipeline GitHub Actions com build, testes e artefatos` | `.github/workflows/ci.yml` |
| 10 | `feat: carga de dados inicial com 10+ tarefas para demo` | `DataLoader.java`, `application.properties` |
| 11 | `feat(scope-change): adiciona prioridade, prazo e indicador de atraso` | `TaskPriority.java`, alterações em `Task.java`, ajustes UI/CSS, novos testes de prioridade e overdue |
| 12 | `docs: parte teórica com UML, justificativa de mudança de escopo e instruções de prints` | `docs/parte-teorica.md`, `docs/kanban-template.md`, `docs/prints-instrucoes.md`, atualização do README |

> Os commits 11 e 12 são especialmente importantes porque demonstram a **gestão de mudanças**
> exigida pelo trabalho.

## Modo manual

Se preferir fazer os commits "à mão" (recomendado para realmente entender o fluxo):

1. Comece com o repositório vazio + `git init -b main`.
2. Para cada linha da tabela acima, copie os arquivos correspondentes para a árvore de
   trabalho, rode `git add <arquivos>` e
   `git commit -m "tipo: mensagem"` usando exatamente a mensagem da tabela.
3. Ao fim, `git log --oneline` deve listar os 12 commits.

## Modo automático (recomendado para reprodutibilidade)

> Atenção: o script **só deve ser executado uma vez**, em um repositório recém-criado e vazio
> (ou apenas com o conteúdo deste projeto **fora** do git ainda).

### Windows (PowerShell)

```powershell
# A partir da pasta do projeto (techflow-tasks):
./scripts/setup-commits.ps1
```

### Linux/macOS (bash)

```bash
chmod +x scripts/setup-commits.sh
./scripts/setup-commits.sh
```

O script:

1. Move temporariamente toda a árvore para `.staging/`.
2. Inicializa o repositório em `main`.
3. Restaura os arquivos por etapa, fazendo um `git commit` por etapa
   com a mensagem da tabela acima (e datas espaçadas, para parecer um histórico real).
4. Remove `.staging/` ao final.

Depois é só `git remote add origin <url-do-seu-repo>` e `git push -u origin main`.
