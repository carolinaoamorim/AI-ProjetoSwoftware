package br.insper.pagamento.service;

import br.insper.pagamento.dto.PagamentoDto;
import br.insper.pagamento.entity.Pagamento;
import br.insper.pagamento.entity.TipoPagamento;
import br.insper.pagamento.processor.Processador;
import br.insper.pagamento.repository.PagamentoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTests {

    @InjectMocks
    private PagamentoService pagamentoService;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private Map<String, Processador> processadores;

    @Mock
    private Processador processador;


    @Test
    public void test_shouldReturnPagamentoWhenCallObterPorId() {

        PagamentoDto dto = new PagamentoDto();
        dto.setTipo(TipoPagamento.PIX);
        dto.setValor(new BigDecimal(1000));
        dto.setDataCompra(LocalDate.now());
        dto.setChaveOrigem("123");
        dto.setChaveDestino("234");

        Pagamento pagamento = Pagamento.fromDto(dto);

        // mocks
        Mockito.when(pagamentoRepository.findById(1L))
                .thenReturn(Optional.of(pagamento));

        // chamada
        Optional<Pagamento> op = pagamentoService.obterPorId(1L);

        // asserts
        Assertions.assertTrue(op.isPresent());
        Assertions.assertEquals("123", op.get().getChaveOrigem());
        Assertions.assertEquals("234", op.get().getChaveDestino());
        Assertions.assertEquals(TipoPagamento.PIX, op.get().getTipo());
    }

    @Test
    public void test_shouldReturnSuccessWhenCallProcessador() {

        PagamentoDto dto = new PagamentoDto();
        dto.setTipo(TipoPagamento.PIX);
        dto.setValor(new BigDecimal(1000));
        dto.setDataCompra(LocalDate.now());
        dto.setChaveOrigem("123");
        dto.setChaveDestino("234");

        Pagamento pagamento = Pagamento.fromDto(dto);

        // mocks
        Mockito.when(pagamentoRepository.findById(1L))
                .thenReturn(Optional.of(pagamento));
        Mockito.when(processadores.get(Mockito.any()))
                .thenReturn(processador);
        Mockito.when(processador.processar(Mockito.any()))
                .thenReturn(true);
        Mockito.when(pagamentoRepository.save(Mockito.any()))
                .thenReturn(pagamento);

        // chamada
        boolean response = pagamentoService.processar(1L);

        // asserts
        Assertions.assertTrue(response);
    }


    @Test
    public void test_shouldReturnFalseWhenCallProcessador() {

        PagamentoDto dto = new PagamentoDto();
        dto.setTipo(TipoPagamento.PIX);
        dto.setValor(new BigDecimal(1000));
        dto.setDataCompra(LocalDate.now());
        dto.setChaveOrigem("123");
        dto.setChaveDestino("234");

        Pagamento pagamento = Pagamento.fromDto(dto);

        // mocks
        Mockito.when(pagamentoRepository.findById(1L))
                .thenReturn(Optional.of(pagamento));
        Mockito.when(processadores.get(Mockito.any()))
                .thenReturn(processador);
        Mockito.when(processador.processar(Mockito.any()))
                .thenReturn(false);
        Mockito.when(pagamentoRepository.save(Mockito.any()))
                .thenReturn(pagamento);

        // chamada
        boolean response = pagamentoService.processar(1L);

        // asserts
        Assertions.assertFalse(response);
    }


    @Test
    public void test_shouldReturnTwoPaymentsWhenListarTodos() {
        List<Pagamento> pagamentos = new ArrayList<>();
        pagamentos.add(new Pagamento());
        pagamentos.add(new Pagamento());

        // cria os mocks
        Mockito.when(pagamentoRepository.findAll())
                .thenReturn(pagamentos);

        // chama o metodo testado
        List<Pagamento> response = pagamentoService.listarTodos();

        // asserts
        Assertions.assertEquals(2, response.size());
    }



    @Test
    public void test_shouldCreatePaymentWhenTipoPagamentoIsPix() {
        // mocks
        PagamentoDto dto = new PagamentoDto();
        dto.setTipo(TipoPagamento.PIX);
        dto.setValor(new BigDecimal(1000));
        dto.setDataCompra(LocalDate.now());
        dto.setChaveOrigem("123");
        dto.setChaveDestino("234");

        Pagamento pagamento = Pagamento.fromDto(dto);

        Mockito.when(pagamentoRepository.save(Mockito.any()))
                .thenReturn(pagamento);

        // chamada
        Pagamento response = pagamentoService.criar(dto);

        // asserts
        Assertions.assertEquals(TipoPagamento.PIX, response.getTipo());
        Assertions.assertEquals("123", response.getChaveOrigem());
    }


}