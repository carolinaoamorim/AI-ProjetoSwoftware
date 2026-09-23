package br.insper.avaliacao.observer;

import br.insper.avaliacao.entity.Avaliacao;

public interface AvaliacaoObservable {
    void notificarObservadores(Avaliacao Avaliacao, String statusAnterior);
}
