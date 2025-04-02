projeto para publicar mensagens em um fila do sqs e consumi-las,
A ideia é que o publisher seja um lambda , e ele acionado publica na fila, onde sera consumido por uma aplicação

Para o projeto rodar, é necessario configurar duas variaveis de ambiente: 

QUEUE_URL=https://sqs.us-east-2.minha-fila.fifo;
AWS_REGION=us-east-2

Para rodar local é necessario estar logado no CLI da aws, e o lambda remoto precisa de permissao para
publicar no sqs