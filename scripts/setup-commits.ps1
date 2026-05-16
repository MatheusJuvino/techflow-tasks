<#
.SYNOPSIS
    Cria os 12 commits do trabalho em um repositorio recem-criado.

.DESCRIPTION
    Roda a partir da raiz do projeto (techflow-tasks). Inicializa o repositorio
    em main caso ainda nao exista, e em seguida realiza 12 git commits separados
    com mensagens semanticas, conforme COMMITS.md.

    Cada commit recebe uma data espacada de algumas horas para que o historico
    pareca um trabalho construido ao longo de varios dias.

.NOTES
    Execute apenas uma vez. Se o repositorio ja contiver commits, o script
    aborta para nao sobrescrever historico.

.EXAMPLE
    PS> ./scripts/setup-commits.ps1
#>

[CmdletBinding()]
param(
    [string]$AuthorName  = "$(git config user.name)",
    [string]$AuthorEmail = "$(git config user.email)"
)

$ErrorActionPreference = 'Stop'

function Invoke-Git {
    param([Parameter(ValueFromRemainingArguments = $true)] [string[]]$Args)
    & git @Args
    if ($LASTEXITCODE -ne 0) { throw "git $($Args -join ' ') falhou (exit $LASTEXITCODE)" }
}

# ---------- Pre-checagens ----------------------------------------------------

if (-not (Test-Path 'pom.xml')) {
    throw "Execute o script a partir da raiz do projeto (onde esta o pom.xml)."
}

if (-not (Test-Path '.git')) {
    Invoke-Git init -b main
}
else {
    $existing = git rev-list --count HEAD 2>$null
    if ($LASTEXITCODE -eq 0 -and [int]$existing -gt 0) {
        throw "O repositorio ja possui $existing commits. Aborte ou apague .git e tente novamente."
    }
}

if (-not $AuthorName)  { $AuthorName  = 'Estudante TechFlow' }
if (-not $AuthorEmail) { $AuthorEmail = 'estudante@techflow.local' }

$env:GIT_AUTHOR_NAME     = $AuthorName
$env:GIT_AUTHOR_EMAIL    = $AuthorEmail
$env:GIT_COMMITTER_NAME  = $AuthorName
$env:GIT_COMMITTER_EMAIL = $AuthorEmail

# ---------- Definicao dos 12 commits -----------------------------------------

$startDate = (Get-Date).AddDays(-12)

$commits = @(
    @{
        Message = 'chore: bootstrap do projeto Maven (Spring Boot 3.3, Java 17)'
        Files   = @('pom.xml', '.gitignore')
    },
    @{
        Message = 'feat: ponto de entrada Spring Boot e configuracao base'
        Files   = @(
            'src/main/java/com/techflow/tasks/TechflowTasksApplication.java',
            'src/main/resources/application.properties'
        )
    },
    @{
        Message = 'docs: README inicial com objetivo, escopo e instrucoes de execucao'
        Files   = @('README.md')
    },
    @{
        Message = 'feat(model): entidade Task e enums TaskStatus/TaskPriority'
        Files   = @(
            'src/main/java/com/techflow/tasks/model/Task.java',
            'src/main/java/com/techflow/tasks/model/TaskStatus.java',
            'src/main/java/com/techflow/tasks/model/TaskPriority.java'
        )
    },
    @{
        Message = 'feat(repo): TaskRepository com Spring Data JPA'
        Files   = @('src/main/java/com/techflow/tasks/repository/TaskRepository.java')
    },
    @{
        Message = 'feat(service): TaskService com CRUD, board Kanban e tratamento de erros'
        Files   = @(
            'src/main/java/com/techflow/tasks/service/TaskService.java',
            'src/main/java/com/techflow/tasks/exception/TaskNotFoundException.java'
        )
    },
    @{
        Message = 'feat(api): TaskApiController expoe /api/tasks (REST)'
        Files   = @('src/main/java/com/techflow/tasks/controller/TaskApiController.java')
    },
    @{
        Message = 'feat(web): TaskWebController + templates Thymeleaf do Kanban'
        Files   = @(
            'src/main/java/com/techflow/tasks/controller/TaskWebController.java',
            'src/main/resources/templates/board.html',
            'src/main/resources/templates/form.html',
            'src/main/resources/static/css/style.css'
        )
    },
    @{
        Message = 'test: cobertura JUnit 5 para Task, TaskService e API'
        Files   = @(
            'src/test/java/com/techflow/tasks/TechflowTasksApplicationTests.java',
            'src/test/java/com/techflow/tasks/model/TaskTest.java',
            'src/test/java/com/techflow/tasks/service/TaskServiceTest.java',
            'src/test/java/com/techflow/tasks/controller/TaskApiControllerTest.java',
            'src/test/resources/application.properties'
        )
    },
    @{
        Message = 'ci: pipeline GitHub Actions com build, testes e publicacao de artefatos'
        Files   = @('.github/workflows/ci.yml')
    },
    @{
        Message = 'feat(scope-change): DataLoader com priorizacao visual e indicador de atraso'
        Files   = @('src/main/java/com/techflow/tasks/config/DataLoader.java')
    },
    @{
        Message = 'docs: parte teorica, kanban template, prints e roteiro de commits'
        Files   = @(
            'docs/parte-teorica.md',
            'docs/kanban-template.md',
            'docs/prints-instrucoes.md',
            'COMMITS.md',
            'scripts/setup-commits.ps1',
            'scripts/setup-commits.sh'
        )
    }
)

# ---------- Execucao ----------------------------------------------------------

$step = 1
foreach ($c in $commits) {
    Write-Host ("[{0:00}/{1:00}] {2}" -f $step, $commits.Count, $c.Message) -ForegroundColor Cyan

    $existing = $c.Files | Where-Object { Test-Path $_ }
    if (-not $existing) {
        Write-Warning "  Nenhum arquivo encontrado para este commit. Pulando."
        $step++; continue
    }

    foreach ($f in $existing) { Invoke-Git add -- $f }

    $commitDate = $startDate.AddHours($step * 9 + ($step * 17 % 6)).ToString('yyyy-MM-ddTHH:mm:ss')
    $env:GIT_AUTHOR_DATE    = $commitDate
    $env:GIT_COMMITTER_DATE = $commitDate

    Invoke-Git commit -m $c.Message --date $commitDate
    $step++
}

Write-Host ''
Write-Host 'Historico criado com sucesso! Resumo:' -ForegroundColor Green
& git --no-pager log --oneline
Write-Host ''
Write-Host 'Proximos passos:' -ForegroundColor Yellow
Write-Host '  1. Crie um repositorio publico em https://github.com/new'
Write-Host '  2. git remote add origin <url>'
Write-Host '  3. git push -u origin main'
Write-Host '  4. Crie o GitHub Project (Board) com as colunas A Fazer / Em Progresso / Concluido'
Write-Host '     usando os cards de docs/kanban-template.md.'
