package br.insper.avaliacao.controller;

import br.insper.avaliacao.dto.AvaliacaoDto;
import br.insper.avaliacao.entity.Avaliacao;
import br.insper.avaliacao.entity.NotaAvaliacao;
import br.insper.avaliacao.repository.AvaliacaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;

import static br.insper.avaliacao.entity.NotaAvaliacao.CINCO;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AvaliacaoControllerTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("avaliacao_test")
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

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @BeforeEach
    public void limparBanco() {
        avaliacaoRepository.deleteAll();
    }

    private Avaliacao salvarCurso(String autor, String conteudo, NotaAvaliacao nota, LocalDate dataAvaliacao) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAutor(autor);
        avaliacao.setConteudo(conteudo);
        avaliacao.setNota(nota);
        avaliacao.setDataAvaliacao(dataAvaliacao);
        return avaliacaoRepository.save(avaliacao);
    }

    // POST /avaliacao

    @Test
    public void test_shouldCreateAvaliacao() throws Exception {

        AvaliacaoDto dto = new AvaliacaoDto();
        dto.setAutor("Carolina");
        dto.setConteudo("Java");
        dto.setNota(NotaAvaliacao.CINCO);
        dto.setDataAvaliacao(LocalDate.now());

        // chamada
        MvcResult result = mockMvc.perform(
                        post("/api/avaliacao")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        // asserts

        Avaliacao avaliacao = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Avaliacao.class);
        Assertions.assertNotNull(avaliacao.getId());
        Assertions.assertEquals("Carolina", avaliacao.getAutor());
        Assertions.assertEquals("Java", avaliacao.getConteudo());
        Assertions.assertEquals(NotaAvaliacao.CINCO, avaliacao.getNota());
        Assertions.assertEquals(LocalDate.now(), avaliacao.getDataAvaliacao());

    }

}