package lambda;

import com.amazonaws.services.lambda.runtime.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import com.amazonaws.services.lambda.runtime.Context;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

public class Handler implements RequestHandler<ApiGatewayRequest, String> {

    private final Validator validator;
    private final SqsPublisher publisher = new SqsPublisher();
    private static final String QUEUE_URL = System.getenv("QUEUE_URL");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Handler() {

        ValidatorFactory factory = Validation.byDefaultProvider()
                                             .configure()
                                             .messageInterpolator(new ParameterMessageInterpolator())
                                             .buildValidatorFactory();

        this.validator = factory.getValidator();
    }

    @Override
    public String handleRequest(ApiGatewayRequest request, Context context) {

        try {

            String rawBody = request.getBody();
            System.out.println("Corpo recebido: " + rawBody);

            SqsMessage message = objectMapper.readValue(rawBody, SqsMessage.class);

            System.out.println("Conteúdo recebido na request:");
            System.out.println("externalId:        " + message.getExternalId());
            System.out.println("cnpjEmissor:       " + message.getCnpjEmissor());
            System.out.println("valorEmissao:      " + message.getValorEmissao());
            System.out.println("nomeRemetente:     " + message.getNomeRemetente());
            System.out.println("dataSolicitacao:   " + message.getDataSolicitacao());
            System.out.println("descricao:         " + message.getDescricao());

            Set<ConstraintViolation<SqsMessage>> violations = validator.validate(message);

            System.out.println("Iniciando validaçoes!");

            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder("Erros de validação:\n");
                for (ConstraintViolation<SqsMessage> v : violations) {
                    sb.append("- ").append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("\n");
                }
                return sb.toString();
            }

            System.out.println("Passou nas validaçoes!");

            String json = objectMapper.writeValueAsString(message);

            publisher.send(QUEUE_URL, json, message.getExternalId());

            return "Mensagem enviada com sucesso!";

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Erro ao processar JSON: " + e.getMessage();
        } catch (Exception e) {

            System.err.println("Erro ao processar mensagem:");
            e.printStackTrace();
            return "Erro interno: " + e.getMessage();
        }

    }

}