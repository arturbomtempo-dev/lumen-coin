# Code Review Lab. 5 - Lumen Coin
**Grupo BrainCoins**

Review do código do projeto de moeda estudantil desenvolvido pelo grupo Lumen Coin para a disciplina de **Laboratório de Desenvolvimento de Software**

No geral, o projeto está seguindo muito bem as boas práticas aprendidas na disciplina de Projeto de Software, porém com alguns detalhes importantes que foram destacados nessa pull request para serem considerados pelo grupo.

### Camada Model
OK.

### Repositories

### DTOs

### Camada Service
- Usar construtor com parâmetros para criação de objetos. 
  - Aumento de legibilidade
  - Redução do número de linhas
  
- Eliminar verificações de atributos obrigatórios nulos em DTOs
  - Aumento da coesão
  - Aumento da legibilidade

- Padronizar o tratamento de avatar nulo
### Camada Controller
OK.

### Exceptions
- Criar novas exceptions lidando com atributos nulos antes que passem para os DTOs. Elimina necessidade de verificar nulos constantemente nos Services

## Conclusão