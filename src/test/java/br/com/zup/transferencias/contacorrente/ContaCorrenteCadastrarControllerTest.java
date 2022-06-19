package br.com.zup.transferencias.contacorrente;

import br.com.zup.transferencias.exception.ErroPadronizado;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
class ContaCorrenteCadastrarControllerTest {

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
    void naoDeveCadastrarUmaContaComDadosNulos() throws Exception {
        // Cenário
        NovaContaCorrenteRequest novaContaCorrenteRequest = new NovaContaCorrenteRequest(
                null,
                null,
                null,
                null,
                null
        );

        String payloadRequest = objectMapper.writeValueAsString(novaContaCorrenteRequest);

        MockHttpServletRequestBuilder request = post("/contas")
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
                .hasSize(5)
                .contains("cpf: não deve estar em branco",
                        "email: não deve estar em branco",
                        "agencia: não deve ser nulo",
                        "titular: não deve estar em branco",
                        "numeroConta: não deve ser nulo"
                );
    }

    @Test
    void naoDeveCadastrarUmaContaComDadosInvalidos() throws Exception {
        // Cenário
        NovaContaCorrenteRequest novaContaCorrenteRequest = new NovaContaCorrenteRequest(
                "000122",
                "12345678",
                "joaoemail.com",
                "231.456.710-51",
                "João batista João batista João batista João batista João batista João batista " +
                        "João batista João batista João batista João batista João batista João batista" +
                        "João batista João batista João batistaJoão batista"
        );

        String payloadRequest = objectMapper.writeValueAsString(novaContaCorrenteRequest);

        MockHttpServletRequestBuilder request = post("/contas")
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
                .hasSize(5)
                .contains("agencia: deve corresponder a \"[\\d]{4}\"",
                "email: deve ser um endereço de e-mail bem formado",
                "numeroConta: deve corresponder a \"[\\d]{6}\"",
                "titular: o comprimento deve ser entre 0 e 120",
                "cpf: número do registro de contribuinte individual brasileiro (CPF) inválido"
                );
    }

    @Test
    void naoDeveCadastrarUmaContaComCpfQueJaFoiCadastrado() throws Exception {
        // Cenário

        ContaCorrente contaCorrente = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );

        contaCorrenteRepository.save(contaCorrente);

        NovaContaCorrenteRequest novaContaCorrenteRequest = new NovaContaCorrenteRequest(
                "0002",
                "123456",
                "jose@zup.com.br",
                "231.256.710-51",
                "José"
        );

        String payloadRequest = objectMapper.writeValueAsString(novaContaCorrenteRequest);

        MockHttpServletRequestBuilder request = post("/contas")
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
                .contains("Cpf já cadastrado na api");
    }

    @Test
    void naoDeveCadastrarUmaContaComEmailQueJaFoiCadastrado() throws Exception {
        // Cenário

        ContaCorrente contaCorrente = new ContaCorrente(
                "0001",
                "889638",
                "joao@zup.com.br",
                "231.256.710-51",
                "João"
        );

        contaCorrenteRepository.save(contaCorrente);

        NovaContaCorrenteRequest novaContaCorrenteRequest = new NovaContaCorrenteRequest(
                "0002",
                "123456",
                "joao@zup.com.br",
                "436.372.510-94",
                "José"
        );

        String payloadRequest = objectMapper.writeValueAsString(novaContaCorrenteRequest);

        MockHttpServletRequestBuilder request = post("/contas")
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
                .contains("Email já cadastrado na api");
    }

    @Test
    void deveCadastrarUmaConta() throws Exception {
        // Cenário

        NovaContaCorrenteRequest novaContaCorrenteRequest = new NovaContaCorrenteRequest(
                "0002",
                "123456",
                "jose@zup.com.br",
                "436.372.510-94",
                "José"
        );

        String payloadRequest = objectMapper.writeValueAsString(novaContaCorrenteRequest);

        MockHttpServletRequestBuilder request = post("/contas")
                .header("Accept-Language", "pt-br")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadRequest);

        // Ação e Corretude
        mockMvc.perform(request).
                andExpect(
                        status().isCreated()
                )
                .andExpect(
                        redirectedUrlPattern("http://localhost/contas/*")
                );

        List<ContaCorrente> contas = contaCorrenteRepository.findAll();
        assertEquals(1, contas.size());
    }
}