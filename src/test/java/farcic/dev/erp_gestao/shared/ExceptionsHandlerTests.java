package farcic.dev.erp_gestao.shared;

import farcic.dev.erp_gestao.shared.config.ExceptionsHandler;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsHandlerTests {
    @Test void conflitoNaoExpoeDetalhesDoBanco() {
        var response = new ExceptionsHandler().handleIntegridade(
                new DataIntegrityViolationException("SQL interno, constraint e dados sensíveis"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).doesNotContain("SQL interno", "constraint", "sensíveis");
    }
}
