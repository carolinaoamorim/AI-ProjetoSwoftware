package br.insper.avaliacao.service;

import br.insper.avaliacao.dto.AvaliacaoDto;
import br.insper.avaliacao.entity.Avaliacao;
import br.insper.avaliacao.exception.AvaliacaoNaoEncontradoException;
import br.insper.avaliacao.exception.ValidacaoAvaliacaoException;
import br.insper.avaliacao.repository.AvaliacaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AvaliacaoServiceTests {

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    // ---------- listar ----------

    @Test
    public void deveListarTodosNaoDeletadosQuandoNomeForNull() {
        Mockito.when(avaliacaoRepository.findByDeletadoFalse())
                .thenReturn(List.of(new Avaliacao(), new Avaliacao()));

        List<Avaliacao> response = avaliacaoService.listarPorId(null);

        Assertions.assertEquals(2, response.size());
        Mockito.verify(avaliacaoRepository).findByDeletadoFalse();
        Mockito.verify(avaliacaoRepository, Mockito.never())
                .findByNomeStartingWithAndDeletadoFalse(Mockito.any());
    }

    @Test
    public void deveListarTodosNaoDeletadosQuandoNomeEstiverEmBranco() {
        Mockito.when(avaliacaoRepository.findByDeletadoFalse())
                .thenReturn(List.of(new Avaliacao()));

        List<Avaliacao> response = avaliacaoService.listarPorId("   ");

        Assertions.assertEquals(1, response.size());
        Mockito.verify(avaliacaoRepository).findByDeletadoFalse();
        Mockito.verify(avaliacaoRepository, Mockito.never())
                .findByNomeStartingWithAndDeletadoFalse(Mockito.any());
    }

    @Test
    public void deveFiltrarPeloInicioDoNomeQuandoNomeForInformado() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNome("Java Básico");

        Mockito.when(avaliacaoRepository.findByNomeStartingWithAndDeletadoFalse("Java"))
                .thenReturn(List.of(avaliacao));

        List<Avaliacao> response = avaliacaoService.listarPorId("Java");

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Java Básico", response.get(0).getNome());
        Mockito.verify(avaliacaoRepository, Mockito.never()).findByDeletadoFalse();
    }

    // ---------- criar ----------

    @Test
    public void deveCriarCursoQuandoNomeForValido() {
        AvaliacaoDto dto = new AvaliacaoDto("Java Básico", "Introdução à linguagem", 40);

        Mockito.when(avaliacaoRepository.save(Mockito.any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Avaliacao response = avaliacaoService.criar(dto);

        Assertions.assertEquals("Java Básico", response.getNome());
        Assertions.assertEquals("Introdução à linguagem", response.getDescricao());
        Assertions.assertEquals(40, response.getCargaHoraria());
        Assertions.assertFalse(response.isDeletado());
    }

    @Test
    public void deveMapearDtoAntesDeSalvar() {
        AvaliacaoDto dto = new AvaliacaoDto("Python", "Curso de Python", 30);

        Mockito.when(avaliacaoRepository.save(Mockito.any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        avaliacaoService.criar(dto);

        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        Mockito.verify(avaliacaoRepository).save(captor.capture());

        Avaliacao avaliacaoSalvo = captor.getValue();
        Assertions.assertEquals("Python", avaliacaoSalvo.getNome());
        Assertions.assertEquals("Curso de Python", avaliacaoSalvo.getDescricao());
        Assertions.assertEquals(30, avaliacaoSalvo.getCargaHoraria());
        Assertions.assertFalse(avaliacaoSalvo.isDeletado());
    }

    @Test
    public void deveLancarExcecaoAoCriarQuandoNomeForNull() {
        AvaliacaoDto dto = new AvaliacaoDto(null, "Descrição", 40);

        Assertions.assertThrows(ValidacaoAvaliacaoException.class,
                () -> avaliacaoService.criar(dto));

        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void deveLancarExcecaoAoCriarQuandoNomeEstiverEmBranco() {
        AvaliacaoDto dto = new AvaliacaoDto("   ", "Descrição", 40);

        Assertions.assertThrows(ValidacaoAvaliacaoException.class,
                () -> avaliacaoService.criar(dto));

        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }

    // ---------- deletar ----------

    @Test
    public void deveMarcarCursoComoDeletadoQuandoExistir() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        avaliacao.setNome("Java Básico");
        avaliacao.setDeletado(false);

        Mockito.when(avaliacaoRepository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        avaliacaoService.deletar(1L);

        Assertions.assertTrue(avaliacao.isDeletado());
        Mockito.verify(avaliacaoRepository).save(avaliacao);
        Mockito.verify(avaliacaoRepository, Mockito.never()).deleteById(Mockito.any());
        Mockito.verify(avaliacaoRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoCursoNaoExistir() {
        Mockito.when(avaliacaoRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(AvaliacaoNaoEncontradoException.class,
                () -> avaliacaoService.deletar(99L));

        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void deveLancarExcecaoAoDeletarCursoJaDeletado() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        avaliacao.setNome("Java Básico");
        avaliacao.setDeletado(true);

        Mockito.when(avaliacaoRepository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        Assertions.assertThrows(AvaliacaoNaoEncontradoException.class,
                () -> avaliacaoService.deletar(1L));

        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }
}