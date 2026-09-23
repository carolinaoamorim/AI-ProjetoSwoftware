package br.insper.avaliacao.controller;

import br.insper.avaliacao.dto.AvaliacaoDto;
import br.insper.avaliacao.entity.Avaliacao;
import br.insper.avaliacao.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

	@Autowired
	private AvaliacaoService avaliacaoService;

	@GetMapping
	public List<Avaliacao> listar() {
		return avaliacaoService.listarTodos();
	}

	@GetMapping("/{id}")
	public Avaliacao listarPorId(@PathVariable Long id) {
		Optional<Avaliacao> avaliacao = avaliacaoService.listarPorId(id);
        return avaliacao.orElse(null);
    }

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Avaliacao criar(@RequestBody AvaliacaoDto dto) {
		return avaliacaoService.criar(dto);
	}

	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		avaliacaoService.deletar(id);
	}
}