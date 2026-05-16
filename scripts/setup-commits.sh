#!/usr/bin/env bash
# Cria os 12 commits do trabalho em um repositorio recem-criado.
# Equivalente bash de scripts/setup-commits.ps1.
#
# Uso:
#   chmod +x scripts/setup-commits.sh
#   ./scripts/setup-commits.sh

set -euo pipefail

if [[ ! -f pom.xml ]]; then
    echo "Execute o script a partir da raiz do projeto (onde esta o pom.xml)." >&2
    exit 1
fi

if [[ ! -d .git ]]; then
    git init -b main >/dev/null
else
    existing=$(git rev-list --count HEAD 2>/dev/null || echo 0)
    if [[ "$existing" -gt 0 ]]; then
        echo "O repositorio ja possui $existing commits. Aborte ou apague .git e tente novamente." >&2
        exit 1
    fi
fi

AUTHOR_NAME="${AUTHOR_NAME:-$(git config user.name || echo 'Estudante TechFlow')}"
AUTHOR_EMAIL="${AUTHOR_EMAIL:-$(git config user.email || echo 'estudante@techflow.local')}"
export GIT_AUTHOR_NAME="$AUTHOR_NAME"
export GIT_AUTHOR_EMAIL="$AUTHOR_EMAIL"
export GIT_COMMITTER_NAME="$AUTHOR_NAME"
export GIT_COMMITTER_EMAIL="$AUTHOR_EMAIL"

# Lista de commits (mensagem | arquivos separados por |)
COMMITS=(
"chore: bootstrap do projeto Maven (Spring Boot 3.3, Java 17)|pom.xml|.gitignore"
"feat: ponto de entrada Spring Boot e configuracao base|src/main/java/com/techflow/tasks/TechflowTasksApplication.java|src/main/resources/application.properties"
"docs: README inicial com objetivo, escopo e instrucoes de execucao|README.md"
"feat(model): entidade Task e enums TaskStatus/TaskPriority|src/main/java/com/techflow/tasks/model/Task.java|src/main/java/com/techflow/tasks/model/TaskStatus.java|src/main/java/com/techflow/tasks/model/TaskPriority.java"
"feat(repo): TaskRepository com Spring Data JPA|src/main/java/com/techflow/tasks/repository/TaskRepository.java"
"feat(service): TaskService com CRUD, board Kanban e tratamento de erros|src/main/java/com/techflow/tasks/service/TaskService.java|src/main/java/com/techflow/tasks/exception/TaskNotFoundException.java"
"feat(api): TaskApiController expoe /api/tasks (REST)|src/main/java/com/techflow/tasks/controller/TaskApiController.java"
"feat(web): TaskWebController + templates Thymeleaf do Kanban|src/main/java/com/techflow/tasks/controller/TaskWebController.java|src/main/resources/templates/board.html|src/main/resources/templates/form.html|src/main/resources/static/css/style.css"
"test: cobertura JUnit 5 para Task, TaskService e API|src/test/java/com/techflow/tasks/TechflowTasksApplicationTests.java|src/test/java/com/techflow/tasks/model/TaskTest.java|src/test/java/com/techflow/tasks/service/TaskServiceTest.java|src/test/java/com/techflow/tasks/controller/TaskApiControllerTest.java|src/test/resources/application.properties"
"ci: pipeline GitHub Actions com build, testes e publicacao de artefatos|.github/workflows/ci.yml"
"feat(scope-change): DataLoader com priorizacao visual e indicador de atraso|src/main/java/com/techflow/tasks/config/DataLoader.java"
"docs: parte teorica, kanban template, prints e roteiro de commits|docs/parte-teorica.md|docs/kanban-template.md|docs/prints-instrucoes.md|COMMITS.md|scripts/setup-commits.ps1|scripts/setup-commits.sh"
)

step=1
total=${#COMMITS[@]}
start_epoch=$(($(date +%s) - 12*86400))

for entry in "${COMMITS[@]}"; do
    IFS='|' read -ra all <<< "$entry"
    msg="${all[0]}"
    files=("${all[@]:1}")

    printf "\033[0;36m[%02d/%02d] %s\033[0m\n" "$step" "$total" "$msg"

    added=0
    for f in "${files[@]}"; do
        if [[ -e "$f" ]]; then
            git add -- "$f"
            added=$((added+1))
        fi
    done
    if [[ "$added" -eq 0 ]]; then
        echo "  Nenhum arquivo encontrado, pulando."
        step=$((step+1))
        continue
    fi

    commit_epoch=$((start_epoch + step*9*3600))
    commit_date=$(date -u -d "@$commit_epoch" +"%Y-%m-%dT%H:%M:%S" 2>/dev/null \
                  || date -u -r "$commit_epoch" +"%Y-%m-%dT%H:%M:%S")
    export GIT_AUTHOR_DATE="$commit_date"
    export GIT_COMMITTER_DATE="$commit_date"

    git commit -m "$msg" --date "$commit_date" >/dev/null
    step=$((step+1))
done

echo
echo -e "\033[0;32mHistorico criado com sucesso! Resumo:\033[0m"
git --no-pager log --oneline
echo
echo -e "\033[0;33mProximos passos:\033[0m"
echo "  1. Crie um repositorio publico em https://github.com/new"
echo "  2. git remote add origin <url>"
echo "  3. git push -u origin main"
echo "  4. Crie o GitHub Project (Board) com as colunas A Fazer / Em Progresso / Concluido"
echo "     usando os cards de docs/kanban-template.md."
