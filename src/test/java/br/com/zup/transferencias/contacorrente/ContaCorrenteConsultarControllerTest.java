package br.com.zup.transferencias.contacorrente;

import br.com.zup.transferencias.exception.ErroPadronizado;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
class ContaCorrenteConsultarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    @BeforeEach
    void setUp() {
        contaCorrenteRepository.deleteAll();
    }

    @Test
    void deveConsultarUmaConta() throws Exception {
        // cenário
        ContaCorrente contaCorrente = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );

        contaCorrenteRepository.save(contaCorrente);

        MockHttpServletRequestBuilder request = get("/contas/{id}", contaCorrente.getId()).contentType(
                APPLICATION_JSON
        );

        // ação e corretude
        String payload = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ContaCorrenteResponse contaCorrenteResponse = objectMapper.readValue(payload, ContaCorrenteResponse.class);

        Assertions.assertThat(contaCorrenteResponse)
                .extracting("agencia", "numeroConta", "saldo")
                .contains(contaCorrente.getAgencia(), contaCorrente.getNumeroConta(), contaCorrente.getSaldo());
    }

    @Test
    void naoDeveConsultarUmaContaQueNaoEstaCadastrado() throws Exception {
        // cenário
        MockHttpServletRequestBuilder request = get(
                "/contas/{id}", Long.MAX_VALUE
        ).contentType(APPLICATION_JSON);

        // ação e corretude
        String response = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ErroPadronizado erroPadronizado = objectMapper.readValue(response, ErroPadronizado.class);
        List<String> mensagens = erroPadronizado.getMensagens();

        assertEquals(1, mensagens.size());
        assertThat(mensagens, containsInAnyOrder("Conta Corrente não encontrada"));
    }
}