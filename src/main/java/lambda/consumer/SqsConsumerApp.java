package lambda.consumer;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lambda.SqsMessage;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.List;
import java.util.Set;

public class SqsConsumerApp {

    private static final String QUEUE_URL = System.getenv("QUEUE_URL");

    private static final ObjectMapper objectMapper = new ObjectMapper();


    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.byDefaultProvider()
                                             .configure()
                                             .messageInterpolator(new ParameterMessageInterpolator())
                                             .buildValidatorFactory();
        validator = factory.getValidator();
    }


    public static void main(String[] args) {

        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                                              .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                                              .withRegion(System.getenv("AWS_REGION"))
                                              .build();
        System.out.println("📡 Iniciando consumo da fila FIFO com SDK v1...");

        while (true) {

            try {

                ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest()
                        .withQueueUrl(QUEUE_URL)
                        .withMaxNumberOfMessages(5)
                        .withWaitTimeSeconds(20)
                        .withMessageAttributeNames("All");

                List<Message> messages = sqs.receiveMessage(receiveRequest).getMessages();

                if (messages.isEmpty()) {
                    System.out.println("Nenhuma mensagem recebida...");
                    continue;
                }

                for (Message message : messages) {

                    System.out.println("Mensagem recebida: " + message.getBody());

                    try {

                        SqsMessage parsed = objectMapper.readValue(message.getBody(), SqsMessage.class);

                        Set<ConstraintViolation<SqsMessage>> violations = validator.validate(parsed);

                        if (!violations.isEmpty()) {

                            System.out.println("Mensagem inválida:");

                            for (ConstraintViolation<?> v : violations) {

                                System.out.println("- " + v.getPropertyPath() + ": " + v.getMessage());
                            }

                            continue;
                        }


                        System.out.println("Processando:");
                        System.out.println("External ID: " + parsed.getExternalId());
                        System.out.println("Descrição:   " + parsed.getDescricao());

                        sqs.deleteMessage(new DeleteMessageRequest()
                                                  .withQueueUrl(QUEUE_URL)
                                                  .withReceiptHandle(message.getReceiptHandle()));

                        System.out.println("Mensagem deletada!");

                    } catch (Exception e) {

                        System.err.println("Erro ao processar/parsing da mensagem:");
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {

                System.err.println("Erro na leitura da fila:");
                e.printStackTrace();
            }
        }

    }

}
