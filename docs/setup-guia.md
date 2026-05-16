# Guia passo-a-passo — do zero ao GitHub

Este guia leva o projeto de **agora** (criado em `~/.cursor/projects/techflow-tasks/`)
até **público no GitHub com 12 commits, CI rodando e Project Board pronto**.

> Tudo é PowerShell. Copie cada bloco no terminal do Windows (Powershell ou Windows Terminal).

---

## 0. Pré-requisitos

Confira o que você já tem:

```powershell
java -version          # precisa ser >= 17
mvn -version           # precisa ser >= 3.9
git --version
gh --version           # GitHub CLI; se faltar, instale com: winget install GitHub.cli
```

Se faltar algo:

| Falta | Comando |
|---|---|
| Java 17 | `winget install EclipseAdoptium.Temurin.17.JDK` |
| Maven | `winget install Apache.Maven` |
| Git | `winget install Git.Git` |
| GitHub CLI | `winget install GitHub.cli` |

> Após instalar, **feche e reabra o terminal** para o `PATH` atualizar.

---

## 1. Mover o projeto para o Desktop

```powershell
$src = "$env:USERPROFILE\.cursor\projects\techflow-tasks"
$dst = "$env:USERPROFILE\Desktop\techflow-tasks"

if (Test-Path $dst) { Write-Error "Ja existe $dst, mova manualmente" } else {
    Move-Item $src $dst
}
cd $dst
```

---

## 2. (Opcional, mas recomendado) testar build local antes de commitar

```powershell
mvn test
```

Esperado: `BUILD SUCCESS` com **15 testes**, 0 falhas.

```powershell
mvn spring-boot:run
# Abra http://localhost:8080/board no navegador
# Ctrl+C no terminal para parar
```

---

## 3. Autenticar o GitHub CLI

Você escolheu **GitHub CLI** (mais simples). Rode:

```powershell
gh auth login
```

Responda as perguntas assim:

1. *What account do you want to log into?* → **GitHub.com**
2. *What is your preferred protocol for Git operations?* → **HTTPS**
3. *Authenticate Git with your GitHub credentials?* → **Y**
4. *How would you like to authenticate GitHub CLI?* → **Login with a web browser**

Ele mostrará um código de 8 caracteres (ex.: `ABCD-1234`) e abrirá o navegador.
Cole o código, autorize o acesso. Volte ao terminal.

Confirme:

```powershell
gh auth status
```

Deve mostrar `Logged in to github.com as <seu-usuario>`.

---

## 4. Inicializar o repositório local e gerar os 12 commits

> O script faz `git init -b main` automaticamente se ainda não existir `.git`.

```powershell
# Garanta que o git knows seu nome/email (uma vez na vida)
git config --global user.name  "Seu Nome"
git config --global user.email "voce@exemplo.com"

# Rode o script - ele cria os 12 commits com mensagens semanticas e datas espacadas
.\scripts\setup-commits.ps1
```

Esperado: ao final, `git log --oneline` lista 12 commits do tipo
`chore: bootstrap...`, `feat(model)...`, `test:...`, `ci:...`, `feat(scope-change):...`, etc.

Se o PowerShell reclamar de **Execution Policy**, rode antes:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\setup-commits.ps1
```

---

## 5. Criar o repositório PÚBLICO no GitHub e fazer o push

Um único comando faz tudo:

```powershell
gh repo create techflow-tasks --public --source=. --remote=origin --push --description "Sistema agil de gerenciamento de tarefas - trabalho de Engenharia de Software"
```

Esse comando:

- cria o repo `techflow-tasks` público na sua conta
- adiciona o remoto `origin` apontando para ele
- dá `git push -u origin main`

Confira:

```powershell
gh repo view --web
```

(abre o repositório no navegador).

---

## 6. Verificar que o GitHub Actions rodou

```powershell
gh run list --limit 3
```

Deve aparecer um run com nome `CI` e status `completed` (e `success` se o build passou).
Pra ver no navegador:

```powershell
gh run watch
```

---

## 7. Criar o GitHub Project (Board) com as 3 colunas

> O `gh` CLI cria *Projects v2* (a versão nova). O fluxo abaixo cria um Board associado
> ao seu **usuário** (não ao repositório), o que é o padrão atual do GitHub.

```powershell
# Cria o projeto e captura o numero
$proj = gh project create --owner "@me" --title "TechFlow Tasks - Kanban" --format json | ConvertFrom-Json
$num = $proj.number
Write-Host "Projeto #$num criado: $($proj.url)"
```

### Adicionar a coluna correta de status (se necessario)

O Project v2 vem com um campo *Status* default que tem `Todo / In Progress / Done`.
Você pode renomeá-las pela UI depois (Project → Settings → fields → Status → renomear
opções para **A Fazer / Em Progresso / Concluído**).

### Linkar o repositório ao projeto

```powershell
gh project link $num --owner "@me" --repo "$(gh repo view --json nameWithOwner -q .nameWithOwner)"
```

### Adicionar os 14 cards via comando

Cada card aqui é um **draft item** no projeto (não precisa virar issue do repo).
Cole o bloco abaixo inteiro:

```powershell
$cards = @(
    @{ Title = "Configurar dashboard operacional"; Body = "Tela inicial com KPIs de entregas em tempo real. Prioridade Alta. Responsavel: UX." },
    @{ Title = "Integrar API de rastreamento"; Body = "Consumir webhook da transportadora parceira. Prioridade Critica. Responsavel: Backend." },
    @{ Title = "Documentar API publica"; Body = "Gerar contrato OpenAPI 3.1 para clientes B2B. Prioridade Media. Responsavel: Backend." },
    @{ Title = "[Mudanca de escopo] Filtros por prioridade"; Body = "Permitir filtrar o Kanban por prioridade. Prioridade Alta. Responsavel: Frontend." },
    @{ Title = "Notificacao por e-mail em tarefas atrasadas"; Body = "Disparar e-mail diario com cards vencidos. Prioridade Media. Responsavel: Backend." },
    @{ Title = "Implementar CRUD de tarefas"; Body = "CRUD completo no backend com validacoes. Prioridade Alta. Responsavel: Backend." },
    @{ Title = "Criar quadro Kanban responsivo"; Body = "Layout em colunas com drag-and-drop futuramente. Prioridade Media. Responsavel: Frontend." },
    @{ Title = "Definir politica de SLA"; Body = "Acordo de nivel de servico para entregas urgentes. Prioridade Baixa. Responsavel: Produto." },
    @{ Title = "[Mudanca de escopo] Indicador de atraso"; Body = "Destacar visualmente tarefas com prazo vencido. Prioridade Critica. Responsavel: Frontend." },
    @{ Title = "Levantar requisitos com cliente"; Body = "Reuniao inicial e mapa de personas. Prioridade Media. Responsavel: Produto." },
    @{ Title = "Configurar repositorio GitHub"; Body = "Repositorio publico com Projects e protecao de branch. Prioridade Media. Responsavel: DevOps." },
    @{ Title = "Pipeline de CI inicial"; Body = "GitHub Actions executando build e testes a cada PR. Prioridade Alta. Responsavel: DevOps." },
    @{ Title = "Definir arquitetura inicial"; Body = "Stack Java + Spring Boot + JPA escolhida. Prioridade Alta. Responsavel: Arquiteto." },
    @{ Title = "Criar README com instrucoes de execucao"; Body = "Documentacao inicial do repositorio. Prioridade Media. Responsavel: Tech Writer." }
)

foreach ($c in $cards) {
    gh project item-create $num --owner "@me" --title $c.Title --body $c.Body | Out-Null
    Write-Host "  + $($c.Title)"
}
Write-Host "Cards adicionados!"
```

> Após criar, abra o projeto: `gh project view $num --web` e arraste cada card para a
> coluna correta (A Fazer / Em Progresso / Concluído) seguindo a tabela em
> `docs/kanban-template.md`.

---

## 8. Tirar os 3 prints obrigatórios

Ver `docs/prints-instrucoes.md`.

---

## 9. Exportar a parte teórica para PDF

```powershell
code docs/parte-teorica.md
```

No VS Code: **Ctrl+Shift+P** → *Markdown: Open Preview to the Side* → no preview, **Ctrl+P**
→ *Save as PDF*. Depois insira os prints na seção 7.

> Alternativa: rodar `pandoc docs/parte-teorica.md -o parte-teorica.pdf` se tiver Pandoc.

---

## 10. Gravar o vídeo pitch (até 4 min)

Roteiro sugerido (em ordem):

1. (~30s) Apresentação do projeto e do escopo
2. (~30s) Metodologia híbrida Scrum + Kanban
3. (~60s) Demo: criar tarefa → mover entre colunas → editar prioridade → ver atraso
4. (~30s) Mostrar `mvn test` rodando localmente
5. (~30s) Mostrar a aba Actions do GitHub com o badge verde
6. (~30s) Comentar a mudança de escopo e os cards `[Mudança de escopo]` no Projects
7. (~30s) Reflexão final sobre Engenharia de Software

Publique no YouTube como *não-listado* ou no Drive com **link compartilhável**.

---

## Quebra-galho rápido

| Problema | Solução |
|---|---|
| `setup-commits.ps1 nao pode ser carregado` | `Set-ExecutionPolicy -Scope Process Bypass` antes |
| `gh: command not found` | `winget install GitHub.cli`, reabra o terminal |
| `mvn test` falha sem internet | A primeira execução baixa dependências; precisa de internet |
| Push pede senha | Você não está autenticado: `gh auth login` ou use SSH |
| CI vermelho no GitHub | Veja o log: `gh run view --log-failed` |

---

Boa entrega! Qualquer erro durante a execução, cole a mensagem aqui que eu te ajudo a resolver.
