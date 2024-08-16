# Botão de Ação para Envio de Relatório Formatado na Sankhya


Este botão de ação, desenvolvido para o sistema Sankhya, tem como finalidade automatizar o envio de um relatório de processamento por e-mail para destinatários definidos. O relatório é gerado utilizando o iReport e é enviado em formato PDF.

 ## Funcionalidades:
> Geração de Relatório:

O botão utiliza o iReport para gerar um relatório específico, identificado pelo número único do anexo (nuanexo).
O relatório é formatado e convertido para PDF, sendo armazenado temporariamente na sessão com o nome "Relatorio.pdf".
> Consulta de Dados:

## Consulta ao Banco de Dados:
O sistema busca informações do parceiro (produtor), variedade do produto, e-mail do parceiro e e-mail do usuário logado.
Os dados são recuperados a partir do número único do registro (NROUNICO) e outros parâmetros do banco de dados.
Envio de E-mail:

O relatório gerado é enviado para os e-mails associados ao parceiro, além do e-mail do usuário logado, se configurado.
A mensagem do e-mail é personalizada com a data de colheita, nome do produtor, variedade e uma saudação apropriada com base na hora do dia.
## Saudação Dinâmica:

O sistema automaticamente seleciona a saudação "Bom dia", "Boa tarde", ou "Boa noite" com base na hora atual do servidor.
## Mecanismo de Falhas:

Caso ocorra algum erro durante a execução, o sistema captura a exceção e retorna uma mensagem de erro apropriada no contexto da ação.
## Como Funciona:
> Invocação:

- O botão é acionado a partir da interface da Sankhya.
- O número único (NROUNICO) do registro é utilizado para buscar os dados relevantes.
## Geração e Envio:

- Um relatório é gerado para o número do anexo especificado.
- O relatório é então anexado a um e-mail, que é enviado para os destinatários configurados.
## Código Principal:

- O método buscarRelatorio é o núcleo da operação, onde ocorre a consulta ao banco de dados, geração do relatório, e o envio do e-mail.
- Estrutura do Código:
- buscarRelatorio(ContextoAcao ctx, BigDecimal nuanexo, Registro registro):
- Gera o relatório e envia o e-mail.
- obterSaudacao():
- Retorna a saudação adequada com base na hora do dia.
  # Dependências:
## iReport: Utilizado para geração do relatório.
Sankhya: Ambiente onde o botão de ação é implementado.
## Java: Linguagem utilizada para o desenvolvimento do código.
- Banco de Dados JDBC: Para consultas e operações com dados do sistema.
- Este botão de ação simplifica o processo de envio de relatórios para os parceiros e usuários, garantindo que todos os envolvidos recebam as informações necessárias de maneira eficiente e automatizada.
