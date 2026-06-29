# Code Review Lab. 5 - Lumen Coin
**Grupo BrainCoins**

Review do código do projeto de moeda estudantil desenvolvido pelo grupo Lumen Coin para a disciplina de **Laboratório de Desenvolvimento de Software**

No geral, o projeto está seguindo muito bem as boas práticas aprendidas na disciplina de Projeto de Software, porém com alguns detalhes importantes que foram destacados nessa pull request para serem considerados pelo grupo.

## 📁🧍 User

### 📁⚙️ Service
- ℹ️ Usar construtor com parâmetros para criação de objetos. 
  - ✅ Aumento de legibilidade
  - ✅ Redução do número de linhas
  
- ℹ️ Eliminar verificações de atributos obrigatórios nulos em DTOs
  - ✅ Aumento da coesão
  - ✅ Aumento da legibilidade

- ℹ️ Padronizar o tratamento de avatar nulo

## 📁📧 Email

### 📁🔃 DTO

- ℹ️ Adicionar anotações como as que foram feitas para os DTOs de Usuario.

## 📁🎓 Course

### 📁⚙️ Service

- ℹ️ Usar construtor com parâmetros para criação de objetos.
    - ✅ Aumento de legibilidade
    - ✅ Redução do número de linhas

## 📁🛍️ Benefit

### 📁⚙️ Service

- ℹ️ Usar construtor com parâmetros nas operações de CREATE.
    - ✅ Redução da repetição de código (DRY)
    - ✅ Legibilidade
