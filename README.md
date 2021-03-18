Servidor, feito em Java, usando o Ratpack

- Para buildar, crie um projeto gradle, com Java 15, clone o repositório para a pasta raiz do projeto e aplique as dependências no arquivo "build.gradle'.
- Para rodar o servidor, execute o método "main" da classe "Main"
- Para popular o banco de dados com alguns exemplos de benefícios e empresas, execute o método "main" da classe "localOps.LocalMain"

Variáveis de ambiente necessárias:  
(todas as variáveis de ambiente são definidas na classe "utils.EnvUtils")
- PORT  
Porta usada pelo servidor (inteiro)
- DB  
String de conexão usada pelo cliente MongoDB (string)  
https://docs.mongodb.com/manual/reference/connection-string/#connections-standard-connection-string-format
- database_name  
Nome do banco de dados utilizado (string)

Links úteis:
- Notion
https://www.notion.so/Projeto-2020-1-1e74d4e1cf524523b4883c3245ef9cf9
- Trello
https://trello.com/b/OSyYbTwP
- Repositório do cliente
https://github.com/CosmicAnemone/Projeto-2021-1-client
