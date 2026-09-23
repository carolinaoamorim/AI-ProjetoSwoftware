# Aula 09/09

## Adidicionar dependências

```xml
        <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webmvc-test</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>testcontainers</artifactId>
			<version>2.0.5</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>postgresql</artifactId>
			<version>1.21.4</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>junit-jupiter</artifactId>
			<version>1.20.1</version>
			<scope>test</scope>
		</dependency>
```


```java
package br.insper.pagamento.controller;

import br.insper.pagamento.dto.PagamentoDto;
import br.insper.pagamento.entity.Pagamento;
import br.insper.pagamento.entity.TipoPagamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PagamentoControllerTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("pagamento_test")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	

}
```

# Aula 02/09

## Adicionar configuração do JaCoCo

O JaCoCo é a biblioteca que mostra a cobertura de teste que existe no projeto.

```
            <plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.15</version>
				<executions>
					<execution>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>report</id>
						<phase>test</phase>
						<goals>
							<goal>report</goal>
						</goals>
						<configuration>
							<outputDirectory>${project.basedir}/tests</outputDirectory>
						</configuration>
					</execution>
				</executions>
			</plugin>
```

## Substituir bibliotecas de teste

Remover as dependências spring-boot-starter-data-jpa-test e spring-boot-starter-webmvc-test

Adicionar essa:

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
```

## Remover arquivo de teste default do Spring

Remover o arquivo src/test/br.insper.pagameto.PagamentoApplicationTests

O spring sempre adiciona esse arquivo quando o projeto é criado pelo Spring Initializr

## Criar a classe de Teste

Criar o arquivo src/test/br.insper.pagamento.service.PagamentoServiceTest

Copiar o seguinte trecho de código nele
```

package br.insper.pagamento.service;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {

	@InjectMocks
	private PagamentoService pagamentoService;

	@Mock
	private PagamentoRepository pagamentoRepository;

}	
	
```

## Adicionar o primeiro caso de teste 

```
    @Test
	public void testCriarPagamentoComSucesso() {

		PagamentoDto dto = createPagamentoDTO();
		Pagamento entity = createPagamento();
		
		when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(entity);

		Pagamento resultado = pagamentoService.criar(dto);

		assertNotNull(resultado);
		assertEquals(1L, resultado.getId());
		assertEquals(TipoPagamento.PIX, resultado.getTipo());
		assertEquals(new BigDecimal("100.00"), resultado.getValor());
		assertEquals("pendente", resultado.getStatus());
	}
	
	private PagamentoDto createPagamentoDTO() {
		PagamentoDto pagamentoDtoValido = new PagamentoDto();
		pagamentoDtoValido.setTipo(TipoPagamento.PIX);
		pagamentoDtoValido.setValor(new BigDecimal("100.00"));
		pagamentoDtoValido.setParcelas(1);
		pagamentoDtoValido.setDataCompra(LocalDate.now());
		pagamentoDtoValido.setChaveOrigem("chave@pix.com");
		pagamentoDtoValido.setChaveDestino("destino@pix.com");
		return pagamentoDtoValido;
	}
	
	private Pagamento createPagamento() {
		Pagamento pagamentoEsperado = new Pagamento();
		pagamentoEsperado.setId(1L);
		pagamentoEsperado.setTipo(TipoPagamento.PIX);
		pagamentoEsperado.setValor(new BigDecimal("100.00"));
		pagamentoEsperado.setParcelas(1);
		pagamentoEsperado.setDataCompra(LocalDate.now());
		pagamentoEsperado.setChaveOrigem("chave@pix.com");
		pagamentoEsperado.setChaveDestino("destino@pix.com");
		pagamentoEsperado.setStatus("pendente");
		return pagamentoEsperado;
	}
```

## Pipeline de verificação de testes

```
name: Java CI with Maven

permissions:
  contents: read
  pull-requests: write

on:
  pull_request:
    branches: [ "main" ]

jobs:
  build:

    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven
        
    - name: Build with Maven
      run: mvn clean install

    - name: Check JaCoCo coverage
      uses: madrapps/jacoco-report@v1.7.2
      with:
        paths: |
          jacoco/jacoco.xml
        token: ${{ secrets.GITHUB_TOKEN }}
        min-coverage-overall: 80
        min-coverage-changed-files: 80
        fail-build: true
```
