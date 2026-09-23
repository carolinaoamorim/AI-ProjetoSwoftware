package br.insper.avaliacao.observer;

import br.insper.avaliacao.entity.Avaliacao;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuditLoggerObserver implements AvaliacaoObserver {
    @Override
    public void atualizar(Avaliacao avaliacao, String statusAnterior, String statusNovo) {

    }

    @Override
    public void atualizar(Avaliacao avaliacao) {

    }

//    private static final Logger logger = LoggerFactory.getLogger(AuditLoggerObserver.class);
//
//    @Override
//    public void atualizar(Avaliacao avaliacao, String statusAnterior, String statusNovo) {
//        String mensagem = String.format(
//                "AUDITORIA - Pagamento ID: %d | Status: %s → %s | Valor: %s",
//                avaliacao.getId(),
//                statusAnterior,
//                statusNovo,
//                avaliacao.getValor()
//        );
//        logger.info(mensagem);
//    }

}