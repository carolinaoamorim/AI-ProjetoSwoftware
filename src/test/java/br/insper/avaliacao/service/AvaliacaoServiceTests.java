package br.insper.avaliacao.service;

import br.insper.avaliacao.dto.AvaliacaoDto;
import br.insper.avaliacao.entity.Avaliacao;
import br.insper.avaliacao.entity.NotaAvaliacao;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AvaliacaoServiceTests {

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    // listar

    @Test
    public void test_shouldReturnAvaliacaoWhenCallObterPorId() {

        AvaliacaoDto dto = new AvaliacaoDto();

        dto.setAutor("Carolina");
        dto.setConteudo("Java");
        dto.setNota(NotaAvaliacao.CINCO);
        dto.setDataAvaliacao(LocalDate.now());

        Avaliacao avaliacao = Avaliacao.fromDto(dto);

        // mocks
        Mockito.when(avaliacaoRepository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        // chamada
        Optional<Avaliacao> op = AvaliacaoService.listarPorId(1L);

        // asserts
        Assertions.assertTrue(op.isPresent());
        Assertions.assertEquals("Carolina", op.get().getAutor());
        Assertions.assertEquals("Java", op.get().getConteudo());
        Assertions.assertEquals(NotaAvaliacao.CINCO, op.get().getNota());
    }

    @Test
    public void test_shouldReturnTwoAvaliacoesWhenListarTodos() {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        avaliacoes.add(new Avaliacao());
        avaliacoes.add(new Avaliacao());

        // cria os mocks
        Mockito.when(avaliacaoRepository.findAll())
                .thenReturn(avaliacoes);

        // chama o metodo testado
        List<Avaliacao> response = avaliacaoService.listarTodos();

        // asserts
        Assertions.assertEquals(2, response.size());
    }


    // criar

    @Test
    public void test_deveCriarCursoQuandoAutorForValido() {
        AvaliacaoDto dto = new AvaliacaoDto("Carolina", "Java", NotaAvaliacao.CINCO, LocalDate.now());

        Mockito.when(avaliacaoRepository.save(Mockito.any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Avaliacao response = avaliacaoService.criar(dto);

        Assertions.assertEquals("Carolina", response.getAutor());
        Assertions.assertEquals("Java", response.getConteudo());
        Assertions.assertEquals(NotaAvaliacao.CINCO, response.getNota());
    }

    @Test
    public void test_deveLancarExcecaoAoCriarQuandoAutorForNull() {
        AvaliacaoDto dto = new AvaliacaoDto(null, "Java", NotaAvaliacao.CINCO, LocalDate.now());

        Assertions.assertThrows(ValidacaoAvaliacaoException.class,
                () -> avaliacaoService.criar(dto));

        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }

    // deletar

    @Test
    public void test_deveLancarExcecaoAoDeletarQuandoCursoNaoExistir() {
        Mockito.when(avaliacaoRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(AvaliacaoNaoEncontradoException.class,
                () -> avaliacaoService.deletar(99L));

        Mockito.verify(avaliacaoRepository, Mockito.never()).save(Mockito.any());
    }

}