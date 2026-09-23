package br.insper.curso.service;

import br.insper.curso.dto.CursoDto;
import br.insper.curso.entity.Curso;
import br.insper.curso.exception.CursoNaoEncontradoException;
import br.insper.curso.exception.ValidacaoCursoException;
import br.insper.curso.repository.CursoRepository;
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
public class CursoServiceTests {

    @InjectMocks
    private CursoService cursoService;

    @Mock
    private CursoRepository cursoRepository;

    // ---------- listar ----------

    @Test
    public void deveListarTodosNaoDeletadosQuandoNomeForNull() {
        Mockito.when(cursoRepository.findByDeletadoFalse())
                .thenReturn(List.of(new Curso(), new Curso()));

        List<Curso> response = cursoService.listar(null);

        Assertions.assertEquals(2, response.size());
        Mockito.verify(cursoRepository).findByDeletadoFalse();
        Mockito.verify(cursoRepository, Mockito.never())
                .findByNomeStartingWithAndDeletadoFalse(Mockito.any());
    }

    @Test
    public void deveListarTodosNaoDeletadosQuandoNomeEstiverEmBranco() {
        Mockito.when(cursoRepository.findByDeletadoFalse())
                .thenReturn(List.of(new Curso()));

        List<Curso> response = cursoService.listar("   ");

        Assertions.assertEquals(1, response.size());
        Mockito.verify(cursoRepository).findByDeletadoFalse();
        Mockito.verify(cursoRepository, Mockito.never())
                .findByNomeStartingWithAndDeletadoFalse(Mockito.any());
    }

    @Test
    public void deveFiltrarPeloInicioDoNomeQuandoNomeForInformado() {
        Curso curso = new Curso();
        curso.setNome("Java Básico");

        Mockito.when(cursoRepository.findByNomeStartingWithAndDeletadoFalse("Java"))
                .thenReturn(List.of(curso));

        List<Curso> response = cursoService.listar("Java");

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Java Básico", response.get(0).getNome());
        Mockito.verify(cursoRepository, Mockito.never()).findByDeletadoFalse();
    }

    // ---------- criar ----------

    @Test
    public void deveCriarCursoQuandoNomeForValido() {
        CursoDto dto = new CursoDto("Java Básico", "Introdução à linguagem", 40);

        Mockito.when(cursoRepository.save(Mockito.any(Curso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Curso response = cursoService.criar(dto);

        Assertions.assertEquals("Java Básico", response.getNome());
        Assertions.assertEquals("Introdução à linguagem", response.getDescricao());
        Assertions.assertEquals(40, response.getCargaHoraria());
        Assertions.assertFalse(response.isDeletado());
    }

    @Test
    public void deveMapearDtoAntesDeSalvar() {
        CursoDto dto = new CursoDto("Python", "Curso de Python", 30);

        Mockito.when(cursoRepository.save(Mockito.any(Curso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        cursoService.criar(dto);

        ArgumentCaptor<Curso> captor = ArgumentCaptor.forClass(Curso.class);
        Mockito.verify(cursoRepository).save(captor.capture());

        Curso cursoSalvo = captor.getValue();
        Assertions.assertEquals("Python", cursoSalvo.getNome());
        Assertions.assertEquals("Curso de Python", cursoSalvo.getDescricao());
        Assertions.assertEquals(30, cursoSalvo.getCargaHoraria());
        Assertions.assertFalse(cursoSalvo.isDeletado());
    }

    @Test
    public void deveLancarExcecaoAoCriarQuandoNomeForNull() {
        CursoDto dto = new CursoDto(null, "Descrição", 40);

        Assertions.assertThrows(ValidacaoCursoException.class,
                () -> cursoService.criar(dto));

        Mockito.verify(cursoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void deveLancarExcecaoAoCriarQuandoNomeEstiverEmBranco() {
        CursoDto dto = new CursoDto("   ", "Descrição", 40);

        Assertions.assertThrows(ValidacaoCursoException.class,
                () -> cursoService.criar(dto));

        Mockito.verify(cursoRepository, Mockito.never()).save(Mockito.any());
    }

    // ---------- deletar ----------

    @Test
    public void deveMarcarCursoComoDeletadoQuandoExistir() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setNome("Java Básico");
        curso.setDeletado(false);

        Mockito.when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));

        cursoService.deletar(1L);

        Assertions.assertTrue(curso.isDeletado());
        Mockito.verify(cursoRepository).save(curso);
        Mockito.verify(cursoRepository, Mockito.never()).deleteById(Mockito.any());
        Mockito.verify(cursoRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoCursoNaoExistir() {
        Mockito.when(cursoRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CursoNaoEncontradoException.class,
                () -> cursoService.deletar(99L));

        Mockito.verify(cursoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void deveLancarExcecaoAoDeletarCursoJaDeletado() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setNome("Java Básico");
        curso.setDeletado(true);

        Mockito.when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));

        Assertions.assertThrows(CursoNaoEncontradoException.class,
                () -> cursoService.deletar(1L));

        Mockito.verify(cursoRepository, Mockito.never()).save(Mockito.any());
    }
}