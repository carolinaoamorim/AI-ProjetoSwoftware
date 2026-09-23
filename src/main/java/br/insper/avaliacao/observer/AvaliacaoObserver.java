package br.insper.avaliacao.observer;

import br.insper.avaliacao.entity.Avaliacao;

public interface AvaliacaoObserver {
    void atualizar(Avaliacao avaliacao, String statusAnterior, String statusNovo);

    void atualizar(Avaliacao avaliacao);
}
