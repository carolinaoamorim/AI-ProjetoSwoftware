package br.insper.curso.service;

import br.insper.curso.dto.CursoDto;
import br.insper.curso.entity.Curso;
import br.insper.curso.exception.CursoNaoEncontradoException;
import br.insper.curso.exception.ValidacaoCursoException;
import br.insper.curso.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

	@Autowired
	private CursoRepository cursoRepository;

	public List<Curso> listar(String nome) {
		if (nome == null || nome.isBlank()) {
			return cursoRepository.findByDeletadoFalse();
		}
		return cursoRepository.findByNomeStartingWithAndDeletadoFalse(nome);
	}

	public Curso criar(CursoDto dto) {
		if (dto.getNome() == null || dto.getNome().isBlank()) {
			throw new ValidacaoCursoException("Nome do curso é obrigatório");
		}
		Curso curso = Curso.fromDto(dto);
		return cursoRepository.save(curso);
	}

	public void deletar(Long id) {
		Curso curso = cursoRepository.findById(id)
				.filter(c -> !c.isDeletado())
				.orElseThrow(() -> new CursoNaoEncontradoException("Curso com ID " + id + " não encontrado"));
		curso.setDeletado(true);
		cursoRepository.save(curso);
	}
}