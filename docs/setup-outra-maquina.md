# Rodar o TechFlow Tasks em outra máquina

Guia completo para clonar e executar o projeto em qualquer computador.
**Só é necessário Java 17 + Git.** O Maven baixa sozinho via Maven Wrapper.

---

## 1. Pré-requisitos (instalar apenas o que faltar)

### 1.1 Java 17 (obrigatório)

Verifique se já existe:

```bash
java -version
```

Se aparecer `openjdk version "17..."` ou superior, está OK. Caso contrário:

#### Windows (PowerShell como administrador)

```powershell
winget install --id EclipseAdoptium.Temurin.17.JDK -e
```

Feche e reabra o PowerShell. Confirme com `java -version`.

#### macOS

```bash
brew install --cask temurin@17
```

#### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
```

#### Linux (Fedora/RHEL)

```bash
sudo dnf install -y java-17-openjdk-devel
```

### 1.2 Git (obrigatório só para clonar — opcional se baixar ZIP)

- **Windows:** `winget install --id Git.Git -e`
- **macOS:** `brew install git` (ou já vem com Xcode Command Line Tools)
- **Linux:** `sudo apt install -y git` ou `sudo dnf install -y git`

---

## 2. Baixar o projeto

### Opção A — Clonar com Git (recomendado)

```bash
git clone https://github.com/MatheusJuvino/techflow-tasks.git
cd techflow-tasks
```

### Opção B — Baixar ZIP

1. Acesse <https://github.com/MatheusJuvino/techflow-tasks>
2. Clique em **Code → Download ZIP**
3. Extraia e abra um terminal dentro da pasta extraída

---

## 3. Executar a aplicação

### Windows (PowerShell ou cmd)

```powershell
.\mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
chmod +x mvnw        # primeira vez apenas
./mvnw spring-boot:run
```

A primeira execução demora alguns minutos porque o wrapper:

1. baixa o Maven 3.9.9 para `~/.m2/wrapper/` (~10 MB);
2. baixa todas as dependências do Spring Boot (~150 MB) para `~/.m2/repository/`;
3. compila o projeto e sobe a aplicação.

Quando aparecer a linha:

```
Started TechflowTasksApplication in X.XXX seconds (process running for ...)
```

abra no navegador: **<http://localhost:8080/board>**

Para parar o servidor: `Ctrl + C` no terminal.

---

## 4. Rodar os testes (opcional, para demonstrar CI local)

```bash
./mvnw test            # Linux/macOS
.\mvnw.cmd test        # Windows
```

Saída esperada: `Tests run: 25, Failures: 0, Errors: 0, Skipped: 0`.

---

## 5. Empacotar como JAR executável (opcional)

```bash
./mvnw -DskipTests package
java -jar target/techflow-tasks-1.0.0.jar
```

Útil se quiser copiar **apenas** o `.jar` (~30 MB) para outra máquina e rodar
sem Git/Maven — basta ter Java 17.

---

## 6. Solução de problemas

### `JAVA_HOME environment variable is not defined correctly`

O Java 17 não está instalado ou `JAVA_HOME` aponta para uma versão antiga.

**Windows:**

```powershell
[System.Environment]::SetEnvironmentVariable(
  'JAVA_HOME',
  'C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot',
  'User'
)
```

(Ajuste o caminho conforme a versão instalada — veja `C:\Program Files\Eclipse Adoptium\`.)

Feche e reabra o terminal.

**macOS/Linux (bash/zsh):**

```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc   # macOS
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc  # Ubuntu
source ~/.zshrc   # ou ~/.bashrc
```

### `Port 8080 was already in use`

Outra aplicação está usando a porta 8080. Suba o app em outra porta:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

E acesse `http://localhost:9090/board`.

### Wrapper não baixa o Maven (sem internet ou proxy bloqueado)

Defina um espelho via variável de ambiente antes de rodar:

```bash
export MVNW_REPOURL=https://repo1.maven.org/maven2
./mvnw spring-boot:run
```

Ou instale o Maven manualmente e use `mvn` direto.

### `permission denied: ./mvnw` (Linux/macOS)

```bash
chmod +x mvnw
```

---

## 7. Versões dos componentes

| Componente | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.4 |
| Maven (via wrapper) | 3.9.9 |
| Banco de dados | H2 em memória |
| Build | Maven Wrapper 3.3.2 |

---

## 8. Resumo (TL;DR)

```bash
# Instale Java 17 (uma vez)
# Depois:
git clone https://github.com/MatheusJuvino/techflow-tasks.git
cd techflow-tasks
./mvnw spring-boot:run         # Linux/macOS
.\mvnw.cmd spring-boot:run     # Windows
# Abra http://localhost:8080/board
```
