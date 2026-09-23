package br.insper.curso.entity;

import br.insper.curso.dto.CursoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column
	private String descricao;

	@Column
	private Integer cargaHoraria;

	@Column(nullable = false)
	private boolean deletado = false;

	public static Curso fromDto(CursoDto dto) {
		Curso curso = new Curso();
		curso.setNome(dto.getNome());
		curso.setDescricao(dto.getDescricao());
		curso.setCargaHoraria(dto.getCargaHoraria());
		return curso;
	}
}