package br.com.zup.transferencias.transferencia;

import br.com.zup.transferencias.contacorrente.ContaCorrente;
import br.com.zup.transferencias.contacorrente.ContaCorrenteRepository;
import br.com.zup.transferencias.exception.ErroPadronizado;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
class RealizarTransferenciaControllerTest {

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
    void naoDeveCadastrarUmaTransferenciaComDadosNulos() throws Exception {
        // Cenário
        TransferenciaRequest transferenciaRequest = new TransferenciaRequest(
                null,
                null,
                null
        );

        String payloadRequest = objectMapper.writeValueAsString(transferenciaRequest);

        MockHttpServletRequestBuilder request = post("/transferencias")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        String payloadResponse = mockMvc.perform(request).
                andExpect(
                        status().isBadRequest()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ErroPadronizado erroPadronizado = objectMapper.readValue(payloadResponse, ErroPadronizado.class);

        assertThat(erroPadronizado.getMensagens())
                .hasSize(3)
                .contains("valor: não deve ser nulo",
                        "idContaDestino: não deve ser nulo",
                        "idContaOrigem: não deve ser nulo"
                );
    }

    @Test
    void naoDeveCadastrarUmaTransferenciaComDadosInvalidos() throws Exception {
        // Cenário
        TransferenciaRequest transferenciaRequest = new TransferenciaRequest(
                new BigDecimal("-100.0"),
                -1L,
                -4L
        );

        String payloadRequest = objectMapper.writeValueAsString(transferenciaRequest);

        MockHttpServletRequestBuilder request = post("/transferencias")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        String payloadResponse = mockMvc.perform(request).
                andExpect(
                        status().isBadRequest()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ErroPadronizado erroPadronizado = objectMapper.readValue(payloadResponse, ErroPadronizado.class);

        assertThat(erroPadronizado.getMensagens())
                .hasSize(3)
                .contains("valor: deve ser maior que 0",
                        "idContaOrigem: deve ser maior que 0",
                        "idContaDestino: deve ser maior que 0"
                );
    }

    @Test
    void naoDeveCadastrarUmaTransferenciaComContaOrigemNaoCadastrada() throws Exception {
        // Cenário

        ContaCorrente contaDestino = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );

        contaCorrenteRepository.save(contaDestino);

        TransferenciaRequest transferenciaRequest = new TransferenciaRequest(
                new BigDecimal("100.0"),
                Long.MAX_VALUE,
                contaDestino.getId()
        );

        String payloadRequest = objectMapper.writeValueAsString(transferenciaRequest);

        MockHttpServletRequestBuilder request = post("/transferencias")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        String payloadResponse = mockMvc.perform(request).
                andExpect(
                        status().isNotFound()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ErroPadronizado erroPadronizado = objectMapper.readValue(payloadResponse, ErroPadronizado.class);

        assertThat(erroPadronizado.getMensagens())
                .hasSize(1)
                .contains("Conta origem não encontrada.");
    }

    @Test
    void naoDeveCadastrarUmaTransferenciaComContaDestinoNaoCadastrada() throws Exception {
        // Cenário

        ContaCorrente contaOrigem = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );

        contaCorrenteRepository.save(contaOrigem);

        TransferenciaRequest transferenciaRequest = new TransferenciaRequest(
                new BigDecimal("100.0"),
                contaOrigem.getId(),
                Long.MAX_VALUE
        );

        String payloadRequest = objectMapper.writeValueAsString(transferenciaRequest);

        MockHttpServletRequestBuilder request = post("/transferencias")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        String payloadResponse = mockMvc.perform(request).
                andExpect(
                        status().isNotFound()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ErroPadronizado erroPadronizado = objectMapper.readValue(payloadResponse, ErroPadronizado.class);

        assertThat(erroPadronizado.getMensagens())
                .hasSize(1)
                .contains("Conta destino não encontrada.");
    }

    @Test
    void naoDeveCadastrarUmaTransferenciaComContaOrigemComSaldoInsuficiente() throws Exception {
        // Cenário
        ContaCorrente contaOrigem = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );
        contaOrigem.creditar(new BigDecimal("250.0"));
        contaCorrenteRepository.save(contaOrigem);

        ContaCorrente contaDestino = new ContaCorrente(
                "0001",
                "135456",
                "pedro@zup.com.br",
                "735.464.890-63",
                "pedro"
        );

        contaCorrenteRepository.save(contaDestino);

        TransferenciaRequest transferenciaRequest = new TransferenciaRequest(
                new BigDecimal("300.0"),
                contaOrigem.getId(),
                contaDestino.getId()
        );

        String payloadRequest = objectMapper.writeValueAsString(transferenciaRequest);

        MockHttpServletRequestBuilder request = post("/transferencias")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        String payloadResponse = mockMvc.perform(request).
                andExpect(
                        status().isUnprocessableEntity()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ErroPadronizado erroPadronizado = objectMapper.readValue(payloadResponse, ErroPadronizado.class);

        assertThat(erroPadronizado.getMensagens())
                .hasSize(1)
                .contains("Conta com saldo Insuficiente");
    }

    @Test
    void naoDeveCadastrarUmaTransferenciaComContaOrigemEContaDestinoIguais() throws Exception {
        // Cenário
        ContaCorrente contaOrigem = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );
        contaOrigem.creditar(new BigDecimal("250.0"));
        contaCorrenteRepository.save(contaOrigem);

        TransferenciaRequest transferenciaRequest = new TransferenciaRequest(
                new BigDecimal("300.0"),
                contaOrigem.getId(),
                contaOrigem.getId()
        );

        String payloadRequest = objectMapper.writeValueAsString(transferenciaRequest);

        MockHttpServletRequestBuilder request = post("/transferencias")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        String payloadResponse = mockMvc.perform(request).
                andExpect(
                        status().isUnprocessableEntity()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ErroPadronizado erroPadronizado = objectMapper.readValue(payloadResponse, ErroPadronizado.class);

        assertThat(erroPadronizado.getMensagens())
                .hasSize(1)
                .contains("Conta de destino igual a conta de origem.");
    }

    @Test
    void deveCadastrarUmaTransferencia() throws Exception {
        // Cenário
        ContaCorrente contaOrigem = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );
        contaOrigem.creditar(new BigDecimal("300.0"));
        contaCorrenteRepository.save(contaOrigem);

        ContaCorrente contaDestino = new ContaCorrente(
                "0001",
                "135456",
                "pedro@zup.com.br",
                "735.464.890-63",
                "pedro"
        );

        contaCorrenteRepository.save(contaDestino);

        TransferenciaRequest transferenciaRequest = new TransferenciaRequest(
                new BigDecimal("300.0"),
                contaOrigem.getId(),
                contaDestino.getId()
        );

        String payloadRequest = objectMapper.writeValueAsString(transferenciaRequest);

        MockHttpServletRequestBuilder request = post("/transferencias")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        mockMvc.perform(request).
                andExpect(
                        status().isCreated()
                )
                .andExpect(
                        redirectedUrlPattern("http://localhost/transferencias/*")
                );

        List<Transferencia> transferencias =transferenciaRepository.findAll();
        assertEquals(1, transferencias.size());
    }

}