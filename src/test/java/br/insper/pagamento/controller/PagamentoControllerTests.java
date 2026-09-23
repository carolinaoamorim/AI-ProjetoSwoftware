package br.insper.pagamento.controller;

import br.insper.pagamento.dto.PagamentoDto;
import br.insper.pagamento.entity.Pagamento;
import br.insper.pagamento.entity.TipoPagamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PagamentoControllerTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("pagamento_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_shouldCreatePayment() throws Exception {

        PagamentoDto dto = new PagamentoDto();
        dto.setTipo(TipoPagamento.PIX);
        dto.setValor(new BigDecimal(1000));
        dto.setDataCompra(LocalDate.now());
        dto.setChaveOrigem("123");
        dto.setChaveDestino("234");
        dto.setParcelas(10);

        // chamada

        MvcResult result = mockMvc.perform(
                post("/api/pagamentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andReturn();

        // asserts

        Pagamento pagamento = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Pagamento.class);
        Assertions.assertNotNull(pagamento.getId());
        Assertions.assertEquals(TipoPagamento.PIX, pagamento.getTipo());
        Assertions.assertEquals(10, pagamento.getParcelas());

    }






}