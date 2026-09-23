package br.insper.avaliacao.service;

import br.insper.avaliacao.dto.AvaliacaoDto;
import br.insper.avaliacao.entity.Avaliacao;
import br.insper.avaliacao.exception.AvaliacaoNaoEncontradoException;
import br.insper.avaliacao.exception.ValidacaoAvaliacaoException;
import br.insper.avaliacao.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
// criar, listar, buscar por id, excluir

	@Autowired
	private static AvaliacaoRepository avaliacaoRepository;

	public List<Avaliacao> listarTodos() {
		return avaliacaoRepository.findAll();
	}

	public static Optional<Avaliacao> listarPorId(Long id) {
		return avaliacaoRepository.findById(id);
	}

	public Avaliacao criar(AvaliacaoDto dto) {
		if (dto.getAutor() == null) {
			throw new ValidacaoAvaliacaoException("Autor é obrigatório");
		}
		Avaliacao avaliacao = Avaliacao.fromDto(dto);
		return avaliacaoRepository.save(avaliacao);
	}

	public boolean deletar(Long id) {
		if (avaliacaoRepository.existsById(id)) {
			avaliacaoRepository.deleteById(id);
			return true;
		}
		return false;
	}
}