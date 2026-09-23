package br.insper.avaliacao.observer;

import br.insper.avaliacao.entity.Avaliacao;
import org.springframework.stereotype.Component;

import static br.insper.avaliacao.entity.NotaAvaliacao.DOIS;
import static br.insper.avaliacao.entity.NotaAvaliacao.UM;

@Component
public class ModeracaoObserver implements AvaliacaoObserver {
    @Override
    public void atualizar(Avaliacao avaliacao, String statusAnterior, String statusNovo) {

    }

    @Override
    public void atualizar(Avaliacao avaliacao) {

    }

//    @Override
//    public void atualizar(Avaliacao avaliacao, String evento) {
//        if (avaliacao.getNota() = UM || avaliacao.getNota() = DOIS) {
//            String mensagem = String.format(
//                    "Nota negativa",
//                    avaliacao.getId(),
//                    evento
//                    );
//            System.out.println(mensagem);
//        }
    }
