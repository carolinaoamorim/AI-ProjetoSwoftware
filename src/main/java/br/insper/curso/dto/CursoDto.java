package br.insper.curso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDto {
	private String nome;
	private String descricao;
	private Integer cargaHoraria;
}