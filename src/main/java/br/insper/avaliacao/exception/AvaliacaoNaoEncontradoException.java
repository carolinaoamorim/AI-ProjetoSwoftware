package br.insper.avaliacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvaliacaoNaoEncontradoException extends RuntimeException {
	public AvaliacaoNaoEncontradoException(String mensagem) {
		super(mensagem);
	}
}