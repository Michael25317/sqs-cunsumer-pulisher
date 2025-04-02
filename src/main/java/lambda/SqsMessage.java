package lambda;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class SqsMessage {

    @NotBlank(message = "O ID externo não pode estar em branco") private String externalId;

    @NotNull(message = "O CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "CNPJ inválido") private String cnpjEmissor;

    @NotNull(message = "O valor de emissão é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor de emissão deve ser maior que 0.01") private BigDecimal valorEmissao;

    @NotBlank(message = "O nome do remetente é obrigatório") private String nomeRemetente;

    @NotNull(message = "A data de solicitação é obrigatória")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") private String dataSolicitacao;

    @NotBlank(message = "A descrição não pode estar vazia") private String descricao;

    public SqsMessage() {

    }

    public SqsMessage(String externalId,
                      String cnpjEmissor,
                      BigDecimal valorEmissao,
                      String nomeRemetente,
                      String dataSolicitacao,
                      String descricao) {

        this.externalId = externalId;
        this.cnpjEmissor = cnpjEmissor;
        this.valorEmissao = valorEmissao;
        this.nomeRemetente = nomeRemetente;
        this.dataSolicitacao = dataSolicitacao;
        this.descricao = descricao;
    }

    public String getExternalId() {

        return externalId;
    }

    public void setExternalId(String externalId) {

        this.externalId = externalId;
    }

    public String getCnpjEmissor() {

        return cnpjEmissor;
    }

    public void setCnpjEmissor(String cnpjEmissor) {

        this.cnpjEmissor = cnpjEmissor;
    }

    public BigDecimal getValorEmissao() {

        return valorEmissao;
    }

    public void setValorEmissao(BigDecimal valorEmissao) {

        this.valorEmissao = valorEmissao;
    }

    public String getNomeRemetente() {

        return nomeRemetente;
    }

    public void setNomeRemetente(String nomeRemetente) {

        this.nomeRemetente = nomeRemetente;
    }

    public String getDataSolicitacao() {

        return dataSolicitacao;
    }

    public void setDataSolicitacao(String dataSolicitacao) {

        this.dataSolicitacao = dataSolicitacao;
    }

    public String getDescricao() {

        return descricao;
    }

    public void setDescricao(String descricao) {

        this.descricao = descricao;
    }
}