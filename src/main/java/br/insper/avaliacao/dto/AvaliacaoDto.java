package br.insper.avaliacao.dto;

import br.insper.avaliacao.entity.NotaAvaliacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDto {
	private String autor;
	private String conteudo;
	private Integer cargaHoraria;
	private NotaAvaliacao nota;
	private LocalDate dataAvaliacao;
}