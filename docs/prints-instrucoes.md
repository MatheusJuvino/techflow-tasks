# Instruções para os prints obrigatórios

A *Parte Teórica* do trabalho exige três prints comentados do GitHub:
**Kanban**, **Commits** e **CI**. Aqui está o passo a passo, na ordem exata.

---

## 1. Print do Kanban

1. Abra o repositório no GitHub.
2. Vá em **Projects** → clique no projeto criado (Board view).
3. Garanta que existem **mais de 10 cards** distribuídos nas colunas
   *A Fazer / Em Progresso / Concluído* (use `docs/kanban-template.md`).
4. Aplique zoom-out do navegador para 80–90% se precisar caber tudo na tela.
5. Print: `Win + Shift + S` (Windows) → recorte da janela do projeto.
6. Salve como `docs/screenshots/01-kanban.png`.
7. **Comentário sugerido**:
   > *"Quadro com 14 cards organizados nas três colunas. Os dois cards marcados como
   > '[Mudança de escopo]' representam a inclusão de prioridades visuais e indicador de
   > atraso, absorvidos no sprint corrente sem replanejamento."*

## 2. Print dos Commits

1. No repositório → aba **Code** → clique em **commits** (ícone do relógio acima da árvore).
2. Confirme que há **≥ 10 commits** com mensagens **semânticas**
   (`feat:`, `fix:`, `test:`, `chore:`, `docs:`...).
3. Print da listagem (mostrando autor, mensagem e data).
4. Salve como `docs/screenshots/02-commits.png`.
5. **Comentário sugerido**:
   > *"Histórico semântico cobrindo 12 commits, do bootstrap do projeto à mudança de escopo.
   > A convenção `tipo: descrição` permite leitura rápida e habilita automações
   > (semantic-release, geração de changelog)."*

## 3. Print do CI funcionando (GitHub Actions)

1. No repositório → aba **Actions**.
2. Clique no workflow `CI` → escolha uma execução com **bolinha verde** (✓).
3. Print da página de detalhes (com a árvore de jobs/steps visível).
4. Salve como `docs/screenshots/03-actions.png`.
5. **Bônus** (recomendado): clique no step
   *"Rodar testes automatizados (JUnit 5)"* → expanda o log → print dos resultados
   (`Tests run: X, Failures: 0`). Salve como `docs/screenshots/04-tests.png`.
6. **Comentário sugerido**:
   > *"Pipeline executa em cada push/PR sobre `main`: build com Maven, testes JUnit 5 e
   > empacotamento. Os relatórios do Surefire são publicados como artefato e o JAR final
   > é anexado ao workflow run, facilitando a entrega contínua."*

---

## Estrutura final esperada da pasta de prints

```
docs/screenshots/
├── 01-kanban.png
├── 02-commits.png
├── 03-actions.png
└── 04-tests.png   (opcional)
```

Depois disso é só inseri-los no documento de parte teórica
(`docs/parte-teorica.md`, seção 7) e exportar para PDF.
