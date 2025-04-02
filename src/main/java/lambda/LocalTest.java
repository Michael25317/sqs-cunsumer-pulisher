package lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;

public class LocalTest {

    public static void main(String[] args) {

        try {

            SqsMessage message = new SqsMessage();
            message.setExternalId("11154b0f-0378-40ac-bc85-7a929c974fea");
            message.setCnpjEmissor("12.345.678/0001-90");
            message.setValorEmissao(new BigDecimal("150.00"));
            message.setNomeRemetente("Empresa Exemplo");
            message.setDataSolicitacao("2025-03-30");
            message.setDescricao("Pagamento de serviços");

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(message);

            ApiGatewayRequest apiGatewayRequest = new ApiGatewayRequest();
            apiGatewayRequest.setBody(jsonBody);

            Handler handler = new Handler();
            String response = handler.handleRequest(apiGatewayRequest, null);

            System.out.println("Resposta da Lambda:");
            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) {
        try {
            SqsMessage message = new SqsMessage();
            message.setExternalId("11154b0f-0378-40ac-bc85-7a929c974fea");
            message.setCnpjEmissor("12.345.678/0001-90");
            message.setValorEmissao(new BigDecimal("150.00"));
            message.setNomeRemetente("Empresa Exemplo");
            message.setDataSolicitacao("2025-03-30");
            message.setDescricao("Pagamento de serviços");

            Handler handler = new Handler();

            String response = handler.handleRequest(message, null);

            System.out.println("Resposta da Lambda:");
            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


}