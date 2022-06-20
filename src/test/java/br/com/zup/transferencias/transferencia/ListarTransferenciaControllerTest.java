package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;
import br.com.zup.transferencias.contacorrente.ContaCorrenteRepository;
import br.com.zup.transferencias.exception.ErroPadronizado;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.DataInput;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
class ListarTransferenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @BeforeEach
    void setUp() {
        transferenciaRepository.deleteAll();
        contaCorrenteRepository.deleteAll();
    }

    @Test
    void naoDeveListarTransferenciasDeUmaContaQueNaoEstaCadastrado() throws Exception {
        // cenário
        MockHttpServletRequestBuilder request = get(
                "/contas/{id}/transferencias", Long.MAX_VALUE
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

    @Test
    void deveDevolverUmaListaVaziaCasoContaNaoTenhaTransferencias() throws Exception {
        // cenário
        ContaCorrente contaCorrente = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );

        contaCorrenteRepository.save(contaCorrente);

        MockHttpServletRequestBuilder request = get(
                "/contas/{id}/transferencias", contaCorrente.getId()
        ).contentType(APPLICATION_JSON);

        // ação e corretude
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(0)));

    }

    @Test
    void deveDevolverUmaListaDeTransferencias() throws Exception {
        // cenário
        ContaCorrente contaCorrente1 = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );
        contaCorrente1.creditar(new BigDecimal("300.0"));
        contaCorrenteRepository.save(contaCorrente1);

        ContaCorrente contaCorrente2 = new ContaCorrente(
                "0001",
                "009988",
                "fagner@zup.com.br",
                "184.841.590-78",
                "Fagner"
        );

        contaCorrenteRepository.save(contaCorrente2);

        Transferencia transferencia1 = new Transferencia(new BigDecimal("200.0"), contaCorrente1, contaCorrente2);
        Transferencia transferencia2 = new Transferencia(new BigDecimal("50.0"), contaCorrente2, contaCorrente1);

        transferenciaRepository.save(transferencia1);
        transferenciaRepository.save(transferencia2);


        MockHttpServletRequestBuilder request = get(
                "/contas/{id}/transferencias", contaCorrente1.getId()
        ).contentType(APPLICATION_JSON);

        // ação e corretude
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

    }

}