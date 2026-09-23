package br.insper.avaliacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ValidacaoAvaliacaoException extends RuntimeException {
    public ValidacaoAvaliacaoException(String mensagem) {
        super(mensagem);
    }
}