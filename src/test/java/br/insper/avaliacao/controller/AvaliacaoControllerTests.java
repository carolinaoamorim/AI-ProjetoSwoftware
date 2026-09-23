package br.insper.avaliacao.controller;

import br.insper.avaliacao.dto.AvaliacaoDto;
import br.insper.avaliacao.entity.Avaliacao;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AvaliacaoControllerTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("curso_test")
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

    private Avaliacao salvarCurso(String nome, boolean deletado) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNome(nome);
        avaliacao.setDescricao("Descrição de " + nome);
        avaliacao.setCargaHoraria(40);
        avaliacao.setDeletado(deletado);
        return avaliacaoRepository.save(avaliacao);
    }

    // POST /cursos

    @Test
    public void deveCriarCurso() throws Exception {
        AvaliacaoDto dto = new AvaliacaoDto("Java Básico", "Introdução à linguagem", 40);

        mockMvc.perform(post("/cursos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Java Básico"))
                .andExpect(jsonPath("$.cargaHoraria").value(40))
                .andExpect(jsonPath("$.deletado").value(false));

        Assertions.assertEquals(1, avaliacaoRepository.count());
    }

    // GET /cursos

    @Test
    public void deveListarApenasCursosNaoDeletados() throws Exception {
        salvarCurso("Java Básico", false);
        salvarCurso("Python", false);
        salvarCurso("Curso Antigo", true);

        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].nome").value(containsInAnyOrder("Java Básico", "Python")));
    }

    @Test
    public void deveFiltrarCursosPeloInicioDoNome() throws Exception {
        salvarCurso("Java Básico", false);
        salvarCurso("JavaScript", false);
        salvarCurso("Aprenda Java", false);
        salvarCurso("Java Deletado", true);

        mockMvc.perform(get("/cursos").param("nome", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].nome").value(containsInAnyOrder("Java Básico", "JavaScript")));
    }

    // DELETE /cursos/{id}

    @Test
    public void deveDeletarCursoLogicamente() throws Exception {
        Avaliacao avaliacao = salvarCurso("Java Básico", false);

        mockMvc.perform(delete("/cursos/" + avaliacao.getId()))
                .andExpect(status().isNoContent());

        Avaliacao noBanco = avaliacaoRepository.findById(avaliacao.getId()).orElseThrow();
        Assertions.assertTrue(noBanco.isDeletado());

        mockMvc.perform(get("/cursos"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void deveRetornar404AoDeletarCursoInexistente() throws Exception {
        mockMvc.perform(delete("/cursos/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar404AoDeletarCursoJaDeletado() throws Exception {
        Avaliacao avaliacao = salvarCurso("Java Básico", true);

        mockMvc.perform(delete("/cursos/" + avaliacao.getId()))
                .andExpect(status().isNotFound());
    }

}