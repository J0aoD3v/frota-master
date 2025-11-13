# Sistema de Gerenciamento de Frota - UENP

## 📋 Especificação do Projeto Final

### Objetivo

Desenvolver um programa orientado a objetos com JavaFX e banco de dados MongoDB para controlar a utilização dos veículos da UENP (campus Bandeirantes).

### Problema

Eventualmente aparecem algumas multas e é necessário saber quem foi o motorista que cometeu a infração.

---

## 🚗 Descrição do Sistema

### Funcionalidades Principais

1. **Cadastro de Veículos**

   - Marca
   - Modelo
   - Placa

2. **Cadastro de Motoristas**

   - Nome
   - Endereço
   - CNH
   - Setor¹

3. **Cadastro de Usuários do Sistema**

   - Código
   - Nome
   - Login
   - Senha

4. **Controle de Utilização**

   - Registro de retirada (motorista, data e horário)
   - Registro de devolução (data e horário)
   - Autenticação por senha obrigatória

5. **Consultas**
   - Consultar quem utilizou um veículo em determinada data
   - Listar todas as utilizações de uma placa por ordem crescente

_¹ Local de trabalho (ex: direção, hospital, fazenda escola)_

---

## � Como Funciona a Autenticação no Sistema

### ⚠️ IMPORTANTE: Fluxo de Autenticação

**O sistema NÃO requer login na tela inicial!**

Veja como funciona:

1. **Tela Inicial (Menu)**: Exibe "Operador: não autenticado" - **ISSO É NORMAL!** ✅

   - Você pode acessar todas as telas de cadastro (Usuários, Motoristas, Veículos, Relatórios)
   - Não precisa estar logado para cadastrar ou consultar

2. **Autenticação é OBRIGATÓRIA apenas para**:

   - ✅ **Registrar RETIRADA de veículo** (tela Utilizações)
   - ✅ **Registrar DEVOLUÇÃO de veículo** (tela Utilizações)

3. **Como funciona na prática**:
   - Entre na tela "Utilizações"
   - Selecione o **Veículo** e o **Motorista** nos ComboBox
   - Ao clicar em "Registrar Retirada" ou "Registrar Devolução"
   - **Um popup de autenticação aparecerá** 🔐
   - Digite login e senha de um operador cadastrado
   - Só com autenticação válida a operação é realizada

### 🎯 Fluxo Visual

```
Tela Utilizações:
  ┌─────────────────────────────────────┐
  │ Veículo:    [ABC-1234 ▼]           │ ← Selecione o carro
  │ Motorista:  [João Silva ▼]         │ ← Selecione o motorista
  │                                     │
  │ [Registrar Retirada] ←─────────────┼─── Clica aqui
  └─────────────────────────────────────┘
                  │
                  ↓
  ┌─────────────────────────────────────┐
  │  🔐 AUTENTICAÇÃO REQUERIDA          │
  │                                     │
  │  Informe suas credenciais de        │
  │  operador para autorizar operação:  │
  │                                     │
  │  Login: [____________]              │ ← Digite login
  │  Senha: [____________]              │ ← Digite senha
  │                                     │
  │  ⚠️ Autenticação obrigatória        │
  │     conforme especificação          │
  │                                     │
  │      [Autenticar] [Cancelar]        │
  └─────────────────────────────────────┘
                  │
                  ↓
         ✅ Operação Autorizada!
```

### 📋 Requisito da Especificação

Conforme item 6 da especificação:

> _"Os registros (retirada e devolução) devem ser feitos por operadores do sistema, mediante autenticação por senha."_

### 🧪 Usuários de Teste no Banco

Se você criou vários usuários de teste e não lembra quais são:

```javascript
// No MongoDB Shell (mongosh)
use veiculos
db.usuarios.find().pretty()

// Ou para ver só login e senha
db.usuarios.find({}, {login: 1, senha: 1, nome: 1, _id: 0})
```

### 💡 Como Testar

1. **Criar um operador** (se ainda não tiver):

   - Vá em "Usuários"
   - Cadastre: Código: 1, Nome: "Admin", Login: "admin", Senha: "1234"

2. **Criar motorista e veículo** (se necessário):

   - Vá em "Motoristas" → Cadastre um motorista
   - Vá em "Veículos" → Cadastre um veículo

3. **Testar autenticação**:

   - Vá em "Utilizações"
   - Selecione um veículo e um motorista
   - Clique em "Registrar Retirada"
   - **Popup de autenticação aparecerá** 🔐
   - Digite: login="admin", senha="1234"
   - Clique em "Autenticar"
   - ✅ Se correto: Retirada registrada!
   - ❌ Se errado: Mensagem de erro e tente novamente

4. **Testar devolução**:
   - Selecione a linha da utilizacao em aberto na tabela
   - Clique em "Registrar Devolução"
   - Autentique novamente
   - ✅ Devolução registrada!

### 🔧 Implementação Técnica

- **Dialog de Autenticação**: `UtilizacoesController.autenticarOperador()`
- **Interface**: `IServicoAutenticacao`
- **Validação**: `ServicoUsuario.autenticar(login, senha)`
- **Exceção**: `AutenticacaoException` (quando login/senha inválidos)
- **Controle**: Operação só prossegue se autenticação for bem-sucedida
- **Feedback**: Mostra nome do operador na mensagem de sucesso

---

## �🗃️ Estrutura de Dados

### Usuários (Operadores do Sistema)

- **Login**
- **Nome**
- **Senha**

### Motoristas

- **Código**
- **Nome**
- **CNH**
- **Setor**

### Veículos

- **Placa**
- **Marca**
- **Modelo**

### Utilização

- **Código**
- **Veículo**
- **Motorista**
- **Data de Retirada**
- **Data de Devolução**

---

## 🛠️ Tecnologias Utilizadas

- **JavaFX** - Interface gráfica
- **MongoDB** - Banco de dados NoSQL
- **Maven** - Gerenciamento de dependências

---

## 📅 Cronograma de Desenvolvimento

| Data                    | Milestone                          |
| ----------------------- | ---------------------------------- |
| 14/10/2025              | Cadastro de Usuários               |
| 21/10/2025              | Cadastro de Motoristas             |
| 28/10/2025              | Cadastro de Veículos               |
| 04/11/2025              | Cadastro de Retiradas e Devoluções |
| 11/11/2025              | Busca de utilização                |
| 25/11/2025 - 04/12/2025 | Apresentação Final                 |

---

## � Estrutura de Arquivos do Projeto

### 🌳 Árvore Completa com Explicações

```
frota-master/
│
├── 📄 EspecificacaoProjetoFinal.txt
│   └── Documento original com os requisitos do sistema
│       • Descreve o problema: controle de veículos da UENP
│       • Define as funcionalidades obrigatórias
│       • Lista os critérios de avaliação
│
├── 📄 InstrucoesProjetoFinal.txt
│   └── Instruções complementares sobre o desenvolvimento
│       • Orientações metodológicas
│       • Dicas de implementação
│
├── 📄 nbactions.xml
│   └── Configurações de ações do NetBeans
│       • Define comandos de run, debug e profile
│       • Integração com JavaFX Maven Plugin
│       • Automatiza a execução do projeto
│
├── 📄 pom.xml
│   └── Arquivo de configuração do Maven (Project Object Model)
│       • Gerencia todas as dependências do projeto
│       • Define versões: Java 17, JavaFX 21, MongoDB 5.2
│       • Configura plugins de build e execução
│       • Bibliotecas principais:
│         - javafx-controls (interface gráfica)
│         - javafx-fxml (carregamento de telas)
│         - mongodb-driver-sync (conexão com banco)
│         - bson (formato de dados do MongoDB)
│
├── 📄 README.md
│   └── Documentação completa do projeto
│       • Tutorial de instalação do MongoDB
│       • Especificação técnica do sistema
│       • Guia de configuração passo a passo
│       • Solução de problemas comuns
│
├── 📂 src/
│   │
│   ├── 📂 main/
│   │   │
│   │   ├── 📂 java/
│   │   │   │
│   │   │   ├── 📂 com/
│   │   │   │   └── 📄 App.java
│   │   │   │       └── Classe principal da aplicação JavaFX
│   │   │   │           • Ponto de entrada do programa (main)
│   │   │   │           • Carrega a tela inicial (Menu.fxml)
│   │   │   │           • Gerencia troca entre telas
│   │   │   │           • Configura a janela principal (800x520px)
│   │   │   │
│   │   │   ├── 📂 controllers/
│   │   │   │   ├── 📄 MenuController.java
│   │   │   │   │   └── Controlador da tela de menu principal
│   │   │   │   │       • Gerencia navegação entre módulos
│   │   │   │   │       • Exibe status do operador logado
│   │   │   │   │       • Botões para acessar cada funcionalidade
│   │   │   │   │
│   │   │   │   ├── 📄 UsuariosController.java
│   │   │   │   │   └── Controlador da tela de cadastro de usuários
│   │   │   │   │       • CRUD de operadores do sistema
│   │   │   │   │       • Validação de login/senha
│   │   │   │   │       • Interface com ServicoUsuario
│   │   │   │   │
│   │   │   │   ├── 📄 MotoristasController.java
│   │   │   │   │   └── Controlador da tela de cadastro de motoristas
│   │   │   │   │       • CRUD de motoristas (usuários dos veículos)
│   │   │   │   │       • Validação de CNH
│   │   │   │   │       • Gerencia informações de setor
│   │   │   │   │
│   │   │   │   ├── 📄 VeiculosController.java
│   │   │   │   │   └── Controlador da tela de cadastro de veículos
│   │   │   │   │       • CRUD da frota (marca, modelo, placa)
│   │   │   │   │       • Validação de placa única
│   │   │   │   │       • Interface com ServicoVeiculo
│   │   │   │   │
│   │   │   │   ├── 📄 UtilizacoesController.java
│   │   │   │   │   └── Controlador da tela de retirada/devolução
│   │   │   │   │       • Registra retirada de veículos
│   │   │   │   │       • Registra devolução de veículos
│   │   │   │   │       • Requer autenticação do operador
│   │   │   │   │       • Valida datas/horários
│   │   │   │   │
│   │   │   │   └── 📄 RelatoriosController.java
│   │   │   │       └── Controlador da tela de relatórios
│   │   │   │           • Consulta utilizações por data
│   │   │   │           • Lista histórico por placa
│   │   │   │           • Exibe resultados em tabelas
│   │   │   │
│   │   │   ├── 📂 dao/
│   │   │   │   └── 📄 Dao.java
│   │   │   │       └── Data Access Object genérico
│   │   │   │           • Camada de persistência com MongoDB
│   │   │   │           • Implementa padrão DAO e programação genérica
│   │   │   │           • Métodos CRUD reutilizáveis:
│   │   │   │             - inserir(T objeto)
│   │   │   │             - listar()
│   │   │   │             - alterar(T objeto)
│   │   │   │             - consultar(filtros)
│   │   │   │             - excluir(filtros)
│   │   │   │           • Funciona com qualquer classe de modelo
│   │   │   │           • URI: mongodb://admin:senha123@localhost:27017/
│   │   │   │           • Database: veiculos
│   │   │   │
│   │   │   ├── 📂 excecoes/
│   │   │   │   ├── 📄 AutenticacaoException.java
│   │   │   │   │   └── Exceção para falhas de autenticação
│   │   │   │   │       • Lançada quando login/senha inválidos
│   │   │   │   │       • Tratada pelos controllers
│   │   │   │   │
│   │   │   │   ├── 📄 UsuarioDuplicadoException.java
│   │   │   │   │   └── Exceção para login duplicado
│   │   │   │   │       • Impede cadastro de mesmo login
│   │   │   │   │       • Garante unicidade
│   │   │   │   │
│   │   │   │   ├── 📄 VeiculoDuplicadoException.java
│   │   │   │   │   └── Exceção para placa duplicada
│   │   │   │   │       • Impede cadastro de mesma placa
│   │   │   │   │       • Validação de negócio
│   │   │   │   │
│   │   │   │   ├── 📄 MotoristaInvalidoException.java
│   │   │   │   │   └── Exceção para dados inválidos de motorista
│   │   │   │   │       • CNH inválida
│   │   │   │   │       • Dados obrigatórios ausentes
│   │   │   │   │
│   │   │   │   └── 📄 UtilizacaoException.java
│   │   │   │       └── Exceção para erros em utilizações
│   │   │   │           • Devolução sem retirada
│   │   │   │           • Veículo já em uso
│   │   │   │           • Datas inválidas
│   │   │   │
│   │   │   ├── 📂 interfaces/
│   │   │   │   ├── 📄 IRepositorio.java
│   │   │   │   │   └── Interface genérica para repositórios
│   │   │   │   │       • Define contrato de persistência
│   │   │   │   │       • Implementada pela classe Dao
│   │   │   │   │       • Permite polimorfismo
│   │   │   │   │
│   │   │   │   └── 📄 IServicoAutenticacao.java
│   │   │   │       └── Interface para serviços de autenticação
│   │   │   │           • Define métodos de login
│   │   │   │           • Implementada por ServicoUsuario
│   │   │   │           • Garante padronização
│   │   │   │
│   │   │   ├── 📂 modelo/
│   │   │   │   ├── 📄 Usuario.java
│   │   │   │   │   └── Modelo de operador do sistema
│   │   │   │   │       • Atributos: codigo, nome, login, senha
│   │   │   │   │       • Responsável por autenticação
│   │   │   │   │       • Getters, setters, toString, equals
│   │   │   │   │
│   │   │   │   ├── 📄 Motorista.java
│   │   │   │   │   └── Modelo de motorista (usuário dos veículos)
│   │   │   │   │       • Atributos: codigo, nome, cnh, setor
│   │   │   │   │       • Representa quem dirige os veículos
│   │   │   │   │       • Setor: local de trabalho (direção, fazenda, etc)
│   │   │   │   │
│   │   │   │   ├── 📄 Veiculo.java
│   │   │   │   │   └── Modelo de veículo da frota
│   │   │   │   │       • Atributos: placa, marca, modelo
│   │   │   │   │       • Representa os carros da UENP
│   │   │   │   │       • Placa é identificador único
│   │   │   │   │
│   │   │   │   └── 📄 Utilizacao.java
│   │   │   │       └── Modelo de registro de uso de veículo
│   │   │   │           • Atributos:
│   │   │   │             - codigo (identificador)
│   │   │   │             - veiculo (objeto Veiculo)
│   │   │   │             - motorista (objeto Motorista)
│   │   │   │             - dataRetirada (LocalDateTime)
│   │   │   │             - dataDevolucao (LocalDateTime)
│   │   │   │           • Registra histórico de utilizações
│   │   │   │           • Permite rastrear infrações
│   │   │   │
│   │   │   └── 📂 servico/
│   │   │       ├── 📄 ServicoUsuario.java
│   │   │       │   └── Regras de negócio para usuários
│   │   │       │       • Valida cadastro (login único)
│   │   │       │       • Implementa autenticação
│   │   │       │       • Criptografia de senha (se implementado)
│   │   │       │       • Camada entre controller e DAO
│   │   │       │
│   │   │       ├── 📄 ServicoMotorista.java
│   │   │       │   └── Regras de negócio para motoristas
│   │   │       │       • Valida CNH
│   │   │       │       • Verifica duplicidade
│   │   │       │       • Valida dados obrigatórios
│   │   │       │
│   │   │       ├── 📄 ServicoVeiculo.java
│   │   │       │   └── Regras de negócio para veículos
│   │   │       │       • Valida placa única
│   │   │       │       • Formata dados
│   │   │       │       • Verifica disponibilidade
│   │   │       │
│   │   │       └── 📄 ServicoUtilizacao.java
│   │   │           └── Regras de negócio para utilizações
│   │   │               • Valida datas (retirada < devolução)
│   │   │               • Verifica se veículo está disponível
│   │   │               • Gera relatórios
│   │   │               • Consultas por data e placa
│   │   │
│   │   └── 📂 resources/
│   │       └── 📂 fxml/
│   │           ├── 📄 Menu.fxml
│   │           │   └── Layout da tela de menu principal
│   │           │       • Tela inicial do sistema
│   │           │       • Botões de navegação
│   │           │       • Status do operador
│   │           │
│   │           ├── 📄 Usuarios.fxml
│   │           │   └── Layout da tela de cadastro de usuários
│   │           │       • Formulário de operadores
│   │           │       • Tabela de listagem
│   │           │       • Botões de ação (salvar, editar, excluir)
│   │           │
│   │           ├── 📄 Motoristas.fxml
│   │           │   └── Layout da tela de cadastro de motoristas
│   │           │       • Campos: nome, CNH, setor
│   │           │       • Grid de motoristas cadastrados
│   │           │
│   │           ├── 📄 Veiculos.fxml
│   │           │   └── Layout da tela de cadastro de veículos
│   │           │       • Campos: placa, marca, modelo
│   │           │       • Listagem da frota
│   │           │
│   │           ├── 📄 Utilizacoes.fxml
│   │           │   └── Layout da tela de retirada/devolução
│   │           │       • ComboBox de veículos e motoristas
│   │           │       • DatePicker para datas
│   │           │       • Autenticação de operador
│   │           │
│   │           └── 📄 Relatorios.fxml
│   │               └── Layout da tela de relatórios
│   │                   • Filtros de consulta
│   │                   • TableView para resultados
│   │                   • Exportação de dados
│   │
│   └── 📂 test/
│       └── 📂 java/
│           ├── 📄 ExemploUsoCompleto.java
│           │   └── Exemplo de uso completo do sistema
│           │       • Demonstra fluxo de cadastros
│           │       • Testa integrações
│           │       • Serve como documentação viva
│           │
│           ├── 📄 TesteAlteracoesBD.java
│           │   └── Testa operações de UPDATE e DELETE
│           │       • Valida alterações no MongoDB
│           │       • Verifica integridade dos dados
│           │
│           ├── 📄 TesteConexaoMongoDB.java
│           │   └── Testa conexão com o banco de dados
│           │       • Primeiro teste a ser executado
│           │       • Valida URI e credenciais
│           │       • Verifica se MongoDB está rodando
│           │
│           ├── 📄 TesteUtilizacao.java
│           │   └── Testa regras de negócio de utilizações
│           │       • Valida retirada/devolução
│           │       • Testa consultas por data
│           │       • Verifica ordenação por placa
│           │
│           └── 📄 VerificarDados.java
│               └── Consulta dados cadastrados no banco
│                   • Lista todos os registros
│                   • Útil para debug
│                   • Valida persistência
│
└── 📂 target/
    ├── 📂 classes/
    │   └── Bytecode compilado (.class) de todas as classes Java
    │       • Arquivos .class gerados pelo Maven
    │       • Espelho da estrutura de src/main/java
    │       • Recursos copiados de src/main/resources
    │
    ├── 📂 generated-sources/
    │   └── 📂 annotations/
    │       └── Código gerado automaticamente por processadores
    │           • Gerado durante compilação
    │           • Normalmente vazio neste projeto
    │
    └── 📂 maven-status/
        └── 📂 maven-compiler-plugin/
            └── 📂 compile/
                └── 📂 default-compile/
                    ├── 📄 createdFiles.lst
                    │   └── Lista de arquivos .class criados
                    │
                    └── 📄 inputFiles.lst
                        └── Lista de arquivos .java compilados
```

### 🎯 Organização por Camadas (Arquitetura)

#### 📊 Camada de Apresentação (View/Controller)

- **FXML**: Arquivos de interface gráfica
- **Controllers**: Lógica de controle das telas

#### 🧠 Camada de Negócio (Business Logic)

- **Servico**: Regras de negócio e validações
- **Interfaces**: Contratos de comportamento

#### 💾 Camada de Dados (Data Access)

- **Dao**: Acesso ao banco de dados
- **Modelo**: Entidades do sistema

#### ⚠️ Camada de Exceções

- **Excecoes**: Tratamento de erros específicos

### 📦 Padrões de Projeto Utilizados

1. **DAO (Data Access Object)**: Isolamento da lógica de persistência
2. **MVC (Model-View-Controller)**: Separação de responsabilidades
3. **Generic Programming**: Reutilização com tipos parametrizados
4. **Repository Pattern**: Interface genérica IRepositorio
5. **Service Layer**: Camada de serviços entre controller e DAO

### 🔄 Fluxo de Dados

```
[FXML] → [Controller] → [Servico] → [Dao] → [MongoDB]
   ↓           ↓            ↓          ↓
[JavaFX]  [Validação]  [Regras]  [CRUD]
```

### 🗄️ Estrutura do Banco de Dados MongoDB

**Database**: `veiculos`

**Coleções (Collections)**:

1. **usuarios**: Operadores do sistema
2. **motoristas**: Motoristas dos veículos
3. **veiculos**: Frota da UENP
4. **utilizacoes**: Histórico de uso

_Cada coleção é criada automaticamente pela classe `Dao` baseado no nome da classe do modelo (em lowercase)._

---

## �📦 Tutorial Completo - Configuração MongoDB do Zero

### 🔧 Pré-requisitos

- Windows 10/11
- Acesso de administrador
- Conexão com internet
- Pelo menos 2GB de espaço livre em disco

---

### 📥 Passo 1: Download do MongoDB Community Edition

1. **Acesse o site oficial:**

   - Vá para: [https://www.mongodb.com/products/self-managed/community-edition](https://www.mongodb.com/products/self-managed/community-edition)

2. **Faça o download:**
   - Clique em "Download"
   - Selecione a versão: **Windows x64**
   - Formato: **MSI**
   - Versão recomendada: **8.2.0** ou mais recente
   - Link direto: [mongodb-windows-x86_64-8.2.0-signed.msi](https://fastdl.mongodb.org/windows/mongodb-windows-x86_64-8.2.0-signed.msi)

---

### 🛠️ Passo 2: Instalação do MongoDB

1. **Execute o instalador:**

   - Clique duas vezes no arquivo `.msi` baixado
   - **Execute como administrador** (clique direito → "Executar como administrador")

2. **Configuração da instalação:**

   - **Setup Type**: Selecione "Complete" (Completa)
   - **Service Configuration**:
     - ✅ Marque "Install MongoDB as a Service"
     - **Service Name**: `MongoDB`
     - **Data Directory**: `C:\Program Files\MongoDB\Server\8.0\data\`
     - **Log Directory**: `C:\Program Files\MongoDB\Server\8.0\log\`
     - ✅ Marque "Run service as Network Service user"

3. **MongoDB Compass:**

   - ✅ Deixe marcado "Install MongoDB Compass" (recomendado)
   - Se não instalar agora, pode instalar depois

4. **Finalize a instalação:**
   - Clique em "Install"
   - Aguarde o processo (pode demorar alguns minutos)
   - Clique em "Finish"

---

### 🚀 Passo 3: Verificação da Instalação

#### 3.1 Verificar o Serviço do MongoDB

1. **Abra o Gerenciador de Serviços:**

   ```powershell
   # No PowerShell como administrador:
   services.msc
   ```

2. **Procure pelo serviço "MongoDB":**
   - Status deve estar: **Executando (Running)**
   - Tipo de inicialização: **Automático**

#### 3.2 Instalar MongoDB Shell (mongosh) - OBRIGATÓRIO

⚠️ **IMPORTANTE**: O MongoDB Server não inclui o `mongosh` por padrão. É necessário instalá-lo separadamente.

1. **Download do MongoDB Shell:**

   - Acesse: [https://www.mongodb.com/try/download/shell](https://www.mongodb.com/try/download/shell)
   - Selecione: **Windows x64**
   - Formato: **msi**
   - Baixe a versão mais recente

2. **Instalar o MongoDB Shell:**
   - Execute o arquivo `.msi` baixado como administrador
   - Siga o assistente de instalação padrão
   - Clique em "Install" e "Finish"

#### 3.3 Testar Conexão via MongoDB Shell

1. **Abra um novo PowerShell como administrador**

2. **Teste se o mongosh foi instalado:**

   ```powershell
   mongosh --version
   ```

3. **Se não funcionar, navegue até o diretório do mongosh:**

   ```powershell
   cd "C:\Users\$env:USERNAME\AppData\Local\Programs\mongosh"
   .\mongosh.exe --version
   ```

4. **Conectar ao MongoDB:**

   ```powershell
   mongosh
   # ou se precisar especificar o caminho:
   # "C:\Users\$env:USERNAME\AppData\Local\Programs\mongosh\mongosh.exe"
   ```

5. **Você deve ver algo como:**

   ```
   Current Mongosh Log ID: [ID]
   Connecting to: mongodb://127.0.0.1:27017/?directConnection=true
   Using MongoDB: 8.2.0
   Using Mongosh: 2.x.x

   test>
   ```

6. **Teste comandos básicos:**

   ```javascript
   // Listar bancos de dados
   show dbs

   // Criar e usar o banco do projeto
   use veiculos

   // Verificar banco atual
   db.getName()

   // Sair
   exit
   ```

#### 3.4 Alternativa: Usar MongoDB Compass (Mais Fácil)

Se o `mongosh` não funcionar, você pode usar o MongoDB Compass que já foi instalado:

1. **Abra o MongoDB Compass**
2. **Conecte em**: `mongodb://localhost:27017`
3. **Use a aba "MongoSH" no bottom** para executar comandos

---

### 🔧 Passo 4: Configuração das Variáveis de Ambiente (Recomendado)

Para usar o MongoDB e mongosh de qualquer lugar no terminal:

1. **Abra as Configurações do Sistema:**

   - `Windows + R` → digite `sysdm.cpl` → Enter
   - Clique em "Variáveis de Ambiente"

2. **Edite a variável PATH:**

   - Em "Variáveis do sistema", encontre "Path"
   - Clique em "Editar"
   - Clique em "Novo"
   - Adicione: `C:\Program Files\MongoDB\Server\8.2\bin`
   - Clique em "Novo" novamente
   - Adicione: `C:\Users\%USERNAME%\AppData\Local\Programs\mongosh`
   - Clique "OK" em todas as janelas

3. **Teste a configuração:**
   ```powershell
   # Abra um novo PowerShell
   mongosh --version
   mongod --version
   ```

---

### 🖥️ Passo 5: Instalação do MongoDB Compass (Interface Gráfica)

Se não instalou durante a instalação do MongoDB:

1. **Download:**

   - Acesse: [https://www.mongodb.com/products/compass](https://www.mongodb.com/products/compass)
   - Baixe a versão para Windows

2. **Instalação:**

   - Execute o instalador
   - Siga o assistente padrão

3. **Primeira conexão:**
   - Abra o MongoDB Compass
   - **Connection String**: `mongodb://localhost:27017`
   - Clique em "Connect"

---

### 📊 Passo 6: Configuração Inicial do Banco de Dados

#### 6.1 Criar o Banco do Projeto

1. **Via MongoDB Compass:**

   - Clique em "Create Database"
   - **Database Name**: `veiculos`
   - **Collection Name**: `usuarios`
   - Clique "Create Database"

2. **Via Command Line:**

   ```javascript
   // Conectar ao MongoDB
   mongosh

   // Criar e usar o banco
   use veiculos

   // Criar as coleções
   db.createCollection("usuarios")
   db.createCollection("motoristas")
   db.createCollection("veiculos")
   db.createCollection("utilizacoes")

   // Verificar coleções criadas
   show collections
   ```

#### 6.2 Inserir Dados de Teste

```javascript
// Inserir um usuário de teste
db.usuarios.insertOne({
  codigo: 1,
  nome: "Administrador",
  login: "admin",
  senha: "123456",
});

// Inserir um motorista de teste
db.motoristas.insertOne({
  codigo: 1,
  nome: "João Silva",
  cnh: "12345678901",
  setor: "Direção",
});

// Inserir um veículo de teste
db.veiculos.insertOne({
  placa: "ABC-1234",
  marca: "Toyota",
  modelo: "Corolla",
});

// Verificar os dados
db.usuarios.find();
db.motoristas.find();
db.veiculos.find();
```

---

### 🔐 Passo 7: Configuração de Segurança (Opcional - Recomendado)

#### 7.1 Habilitar Autenticação

1. **Criar usuário administrador:**

   ```javascript
   mongosh

   use admin

   db.createUser({
     user: "admin",
     pwd: "senha123",
     roles: ["userAdminAnyDatabase", "dbAdminAnyDatabase", "readWriteAnyDatabase"]
   })
   ```

2. **Editar arquivo de configuração:**

   - Arquivo: `C:\Program Files\MongoDB\Server\8.0\bin\mongod.cfg`
   - Adicionar:

   ```yaml
   security:
     authorization: enabled
   ```

3. **Reiniciar o serviço MongoDB:**
   ```powershell
   # Como administrador
   net stop MongoDB
   net start MongoDB
   ```

#### 7.2 Conectar com Autenticação

```powershell
# Via mongosh
mongosh --username admin --password senha123 --authenticationDatabase admin

# Via aplicação Java
mongodb://admin:senha123@localhost:27017/veiculos?authSource=admin
```

---

### ⚙️ Passo 8: Configuração para o Projeto Java

#### 8.1 Dependências Maven (pom.xml)

Adicione as seguintes dependências ao seu `pom.xml`:

```xml
<dependencies>
    <!-- MongoDB Driver -->
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.11.1</version>
    </dependency>

    <!-- BSON -->
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>bson</artifactId>
        <version>4.11.1</version>
    </dependency>

    <!-- JavaFX Controls -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.1</version>
    </dependency>

    <!-- JavaFX FXML -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.1</version>
    </dependency>
</dependencies>
```

#### 8.2 Configurações da Aplicação

**Arquivo de propriedades** (`application.properties`):

```properties
# MongoDB Configuration
mongodb.uri=mongodb://localhost:27017
mongodb.database=veiculos

# Com autenticação (se configurada)
# mongodb.uri=mongodb://admin:senha123@localhost:27017/veiculos?authSource=admin
```

**Classe de Configuração** (`MongoConfig.java`):

```java
public class MongoConfig {
    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE = "veiculos";

    public static MongoDatabase getDatabase() {
        MongoClient mongoClient = MongoClients.create(URI);
        return mongoClient.getDatabase(DATABASE);
    }
}
```

---

### 🧪 Passo 9: Teste Final da Configuração

#### 9.1 Teste de Conectividade

Crie um arquivo de teste Java:

```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class TesteConexaoMongoDB {
    public static void main(String[] args) {
        try {
            // Conectar ao MongoDB
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("veiculos");

            // Teste de inserção
            Document doc = new Document("teste", "conexão funcionando")
                    .append("timestamp", new java.util.Date());

            database.getCollection("teste").insertOne(doc);

            System.out.println("✅ Conexão com MongoDB estabelecida com sucesso!");
            System.out.println("✅ Banco de dados 'veiculos' acessível!");

            mongoClient.close();

        } catch (Exception e) {
            System.err.println("❌ Erro na conexão com MongoDB:");
            e.printStackTrace();
        }
    }
}
```

#### 9.2 Verificação no MongoDB Compass

1. Abra o MongoDB Compass
2. Conecte em `mongodb://localhost:27017`
3. Verifique se o banco `veiculos` foi criado
4. Verifique se a coleção `teste` contém o documento inserido

---

### 🔍 Solução de Problemas Comuns

#### Problema: "mongosh não é reconhecido"

**Causa**: MongoDB Shell não foi instalado ou não está no PATH.

**Soluções:**

1. **Instalar MongoDB Shell separadamente:**

   ```powershell
   # Baixar e instalar de: https://www.mongodb.com/try/download/shell
   ```

2. **Usar caminho completo:**

   ```powershell
   "C:\Users\$env:USERNAME\AppData\Local\Programs\mongosh\mongosh.exe"
   ```

3. **Adicionar ao PATH** (veja Passo 4 acima)

4. **Alternativa - usar MongoDB Compass:**
   - Abra MongoDB Compass
   - Use a aba "MongoSH" no bottom da interface

#### Problema: "MongoDB service failed to start"

**Solução:**

```powershell
# 1. Verificar se as pastas existem
mkdir "C:\data\db" -Force

# 2. Dar permissões para o serviço
icacls "C:\data\db" /grant "Network Service":(OI)(CI)F

# 3. Reiniciar o serviço
net stop MongoDB
net start MongoDB
```

#### Problema: "Connection refused"

**Verificações:**

1. Serviço MongoDB está rodando?
2. Porta 27017 está bloqueada no firewall?
3. Arquivo de configuração está correto?

```powershell
# Verificar porta
netstat -an | findstr 27017

# Verificar serviço
sc query MongoDB

# Verificar se mongod está rodando
tasklist /fi "imagename eq mongod.exe"
```

#### Problema: "Authentication failed"

**Solução:**

1. Verifique usuário e senha
2. Verifique banco de autenticação
3. Use connection string correta:
   ```
   mongodb://usuario:senha@localhost:27017/veiculos?authSource=admin
   ```

#### Problema: Versão 8.2 vs 8.0 nos caminhos

**Ajuste os caminhos conforme sua versão:**

```powershell
# Para MongoDB 8.2:
cd "C:\Program Files\MongoDB\Server\8.2\bin"

# Para MongoDB 8.0:
cd "C:\Program Files\MongoDB\Server\8.0\bin"
```

---

### 📚 Comandos Úteis para o Dia a Dia

```javascript
// Conectar ao MongoDB
mongosh

// Listar bancos
show dbs

// Usar banco do projeto
use veiculos

// Listar coleções
show collections

// Contar documentos
db.usuarios.countDocuments()

// Buscar todos os documentos
db.usuarios.find().pretty()

// Buscar com filtro
db.veiculos.find({marca: "Toyota"})

// Atualizar documento
db.usuarios.updateOne(
   {login: "admin"},
   {$set: {nome: "Super Admin"}}
)

// Deletar documento
db.teste.deleteMany({})

// Criar índice
db.usuarios.createIndex({login: 1}, {unique: true})

// Verificar índices
db.usuarios.getIndexes()

// Fazer backup
mongodump --db veiculos --out C:\backup\

// Restaurar backup
mongorestore --db veiculos C:\backup\veiculos\
```

---

### ✅ Checklist Final

- [ ] MongoDB Community Edition instalado
- [ ] **MongoDB Shell (mongosh) instalado separadamente**
- [ ] Serviço MongoDB executando
- [ ] **Conexão mongosh funcionando**
- [ ] MongoDB Compass instalado (opcional)
- [ ] Banco `veiculos` criado
- [ ] Coleções básicas criadas
- [ ] Teste de conexão Java funcionando
- [ ] Dependências Maven configuradas
- [ ] Variáveis de ambiente configuradas (recomendado)
- [ ] Autenticação configurada (opcional)

### 🎯 Configuração para o Projeto

- **URI de Conexão**: `mongodb://localhost:27017`
- **Nome do Banco**: `veiculos`
- **Coleções**:
  - `usuarios` - Operadores do sistema
  - `motoristas` - Motoristas cadastrados
  - `veiculos` - Frota de veículos
  - `utilizacoes` - Registros de uso

---

## 🏗️ Arquitetura do Sistema

### Padrão DAO (Data Access Object)

```java
// Exemplo de uso
Dao<Veiculo> daoVeiculo = new Dao(Veiculo.class);
Dao<Usuario> daoUsuario = new Dao(Usuario.class);
```

### Estrutura da Classe Dao

```java
public class Dao<T> {
    private final Class<T> classe;
    private final String URI = "mongodb://localhost:27017";
    private final String DATABASE = "veiculos";
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    // Construtor recebe a classe dos objetos a serem persistidos
    // Métodos: inserir, listar, alterar, consultar, excluir
}
```

---

## ✅ Conceitos Obrigatórios

1. **Pacotes** - Organização das classes por natureza
2. **Classes e métodos**
3. **Sobrecarga**
4. **Encapsulamento**
5. **Programação genérica**
6. **Tratamento de exceções**
7. **Polimorfismo** (se possível)
8. **Reutilização**
9. **Interfaces**
10. **Princípios SOLID e padrões de projeto** (se possível)

---

## 🎯 Critérios de Avaliação

### Organização do Código

- **Empacotamento**: Classes de mesma natureza no mesmo pacote
- **Padronização**: Seguir convenções Java
  - Classes/interfaces: PascalCase
  - Métodos/variáveis/pacotes: camelCase
- **Identificadores significativos**: Nomes descritivos

### Interface Gráfica

- **JavaFX obrigatório**
- Todas as entradas/saídas via interface gráfica
- CSS apenas após funcionalidade completa

### Boas Práticas

- **Objetividade**
- **Baixo acoplamento**
- **Desenvolvimento incremental**

---

## 📊 Avaliação

### Formato

- **Avaliação contínua** durante as aulas
- **Apresentação final** obrigatória

### Observações

- Desenvolvimento em sala de aula (complementar fora se necessário)
- Apresentação pode ser antecipada
- Repositório GitHub deve ser **privado**

---

## 🔗 Recursos

- **Esqueleto do projeto**: [https://github.com/merlinuenp/frota](https://github.com/merlinuenp/frota)

---

## 📝 Notas Importantes

### MongoDB

- Formato de armazenamento: **BSON** (Binary JSON)
- Estrutura: chave + valor
- Não é necessário conhecimento profundo, apenas para persistência de objetos

### Desenvolvimento

- Classe Dao não trata exceções inicialmente
- Podem ser necessários métodos adicionais na Dao
- Foco na funcionalidade antes da estética
- Implementar testes unitários primeiro, depois interface gráfica

---

_Projeto desenvolvido para a disciplina de Programação II - UENP Campus Bandeirantes_
