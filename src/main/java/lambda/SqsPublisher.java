package lambda;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.*;

public class SqsPublisher {

    private final AmazonSQSClient client;

    public SqsPublisher() {


        this.client = (AmazonSQSClient) AmazonSQSClient.builder()
                                                       .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                                                       .withRegion(System.getenv("AWS_REGION"))
                                                       .build();
    }

    public void send(String queueUrl, String body, String externalId ) {

        try {
            System.out.println("Iniciando chamada para publicar na fila!");

            SendMessageRequest request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(body)
                    .withMessageGroupId("emissao-nf")
                    .withMessageDeduplicationId(externalId);

            client.sendMessage(request);

            System.out.println("Mensagem enviada com sucesso");

        } catch (AmazonServiceException ase) {

            System.err.println("AWS Service Exception:");
            System.err.println("Mensagem: " + ase.getMessage());
            System.err.println("Status Code: " + ase.getStatusCode());
            System.err.println("Error Code: " + ase.getErrorCode());
            System.err.println("Request ID: " + ase.getRequestId());
            throw ase;

        } catch (AmazonClientException ace) {

            System.err.println("AWS Client Exception:");
            System.err.println("Mensagem: " + ace.getMessage());
            throw ace;
        }

    }

}