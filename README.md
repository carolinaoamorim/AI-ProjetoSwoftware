# Avaliação Intermediária - Projeto de Software 2026.2

Por Carolina Oliveira Amorim

## Fluxo da Prova

```bash
git checkout main
git pull
git status   # tem que estar limpo
```

### 1. Rotas na main (GET + POST)
1. Adaptar entity, dto, repository, service e controller pro enunciado
2. Escrever os testes do service (100% de cobertura) e o teste de integração só pro GET e POST
3. Rodar localmente antes de subir:
```bash
mvn clean install
```
4. Subir para a main:
```bash
git add .
git commit -m "Rotas GET e POST"
git push
```

### 2. Rota via Pull Request
```bash
git checkout -b feature/delete
```
Escrever a rota e os testes dela, depois:
```bash
git add .
git commit -m "Adiciona rota DELETE"
git push -u origin feature/delete
```
No GitHub:
1. **Compare & pull request**
2. Conferir: `base: main` ← `compare: feature/delete`
3. **Create pull request**
4. Esperar o check do `tests.yml` ficar verde e o comentário de cobertura do JaCoCo aparecer
5. **Tirar print do PR**
6. **Merge pull request** (dispara o deploy)

### 3. Voltar para a main atualizada
```bash
git checkout main
git pull
```
Conferir em **Actions** se o deploy do merge passou e testar a rota nova com `curl`.

### Comandos na EC2
```bash
ssh -i projsoft26b.pem ubuntu@98.92.208.21
docker ps                             # containers rodando e portas
docker logs AvaliacaoIntermediaria    # erros da aplicação
```

### Se der erro
| Erro | Solução |
|---|---|
| `port is already allocated` | `docker ps`, depois `docker stop NOME` e `docker rm NOME` no container que usa a porta |
| `repository name must be lowercase` | Nome da imagem no `deploy.yml` todo em minúsculo |
| `No such container` | Normal no primeiro deploy, pode ignorar |
| Push da branch pede `--set-upstream` | Usar `git push -u origin nome-da-branch` |
| `tests.yml` não rodou no PR | Conferir se o PR é para a `main` |
| Aplicação não conecta no banco | Conferir os secrets `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD` e o `docker logs` |
| Cobertura abaixo do mínimo | Abrir `target/site/jacoco/index.html` e ver o que está sem teste |

## Como adaptar os testes

"de quantos jeitos diferentes esse método pode terminar?"

### Testes do service (unitários, 100% de cobertura)
1. Trocar os nomes: `CursoService`, `CursoRepository`, `Curso`, `CursoDto` e as exceções.
   
2. **Contar os caminhos do service:** cada `if`, `||`, `.filter` e `orElseThrow` abre um caminho novo.
   - `if (x == null || x.isBlank())` → 3 testes: `null`, em branco e válido
   - `findById(id).orElseThrow(...)` → 2 testes: existe e não existe
   - `.filter(...)` antes do `orElseThrow` → +1 teste: existe, mas não passa no filtro
  
3. **Um teste por caminho**, sempre em 3 partes:
```java
// 1. prepara: o que o repository falso responde
Mockito.when(repo.findById(1L)).thenReturn(Optional.of(obj));
// 2. chama o service
service.metodo(1L);
// 3. confere
Assertions.assertEquals(esperado, obtido);
Mockito.verify(repo).save(obj);
```

**Modelos:**
- Salvar: `Mockito.when(repo.save(Mockito.any(X.class))).thenAnswer(inv -> inv.getArgument(0));`
- Lista: `Mockito.when(repo.metodo()).thenReturn(List.of(new X(), new X()));`
- Não encontrado: `Mockito.when(repo.findById(99L)).thenReturn(Optional.empty());`
- Exceção: `Assertions.assertThrows(MinhaException.class, () -> service.metodo(...));`
- Não salvou: `Mockito.verify(repo, Mockito.never()).save(Mockito.any());`
- Ver o que foi salvo: `ArgumentCaptor<X> captor = ArgumentCaptor.forClass(X.class);`, depois `Mockito.verify(repo).save(captor.capture());` e `captor.getValue()`

**Pegadinha:** `UnnecessaryStubbingException` → tem um `when(...)` que o service não usou naquele teste. É só apagar.

### Teste de integração (controller)
1. Trocar a rota (`/cursos`), a entidade e o repository.
2. Manter o `@BeforeEach` com `repository.deleteAll()`.
3. Criar dados direto pelo repository com o helper `salvarX(...)`.
4. **Um teste por rota**, conferindo o status:
   - POST → `status().isCreated()` e `jsonPath("$.id").exists()`
   - GET → `status().isOk()` e `jsonPath("$.length()").value(N)`
   - DELETE → `status().isNoContent()`, e confirmar no banco com `repository.findById(...)`
   - Não encontrado → `status().isNotFound()`

### Conferir antes de subir
```bash
mvn clean install
```
Abrir `target/site/jacoco/index.html`: o service tem que estar em 100% (tudo verde).

## Padrões: quando usar Validator, Processor e Observer

| Padrão | Sinal no enunciado | Exemplo |
|---|---|---|
| **Validator** | "validar", "campos obrigatórios", regras que mudam por tipo | PIX exige chave, cartão exige número e CVC |
| **Processor** (Strategy) | um **enum de tipos** em que cada tipo faz algo **diferente** | processar PIX, cartão e boleto de jeitos diferentes |
| **Observer** | "notificar", "registrar log", "quando X acontecer, fazer Y" | ao mudar status, enviar e-mail e gravar auditoria |

Se nada disso aparecer, as regras ficam direto no service.

### Validator: tira as regras de validação do service
```java
public class ValidadorCurso {
    public void validar(CursoDto dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new ValidacaoCursoException("Nome obrigatório");
        }
    }
}
```
- **No service:** `new ValidadorCurso().validar(dto);` antes de salvar.
- **Teste:** os testes do service já cobrem, com um teste por regra (válido e cada caso inválido).

### Processor: um comportamento para cada tipo
```java
public interface Processador {
    boolean processar(Pagamento pagamento);
}

@Component("PIX")   // o nome do bean tem que ser IGUAL ao valor do enum
public class ProcessadorPix implements Processador {
    public boolean processar(Pagamento p) { /* regra do PIX */ return true; }
}
```
- **Tem que ter:** a interface, uma classe `@Component("TIPO")` por valor do enum e o enum.
- **No service:** o Spring monta um mapa "nome do bean → classe" sozinho:
```java
@Autowired
private Map<String, Processador> processadores;

Processador processador = processadores.get(pagamento.getTipo().toString());
boolean sucesso = processador.processar(pagamento);
```
- **Teste do service:** mockar o mapa e o processador:
```java
@Mock private Map<String, Processador> processadores;
@Mock private Processador processador;

Mockito.when(processadores.get(Mockito.any())).thenReturn(processador);
Mockito.when(processador.processar(Mockito.any())).thenReturn(true);  // e outro teste com false
```
- **Cada processador** precisa de um teste próprio (é classe nova, conta na cobertura).

### Observer: avisar outras classes quando algo acontece
```java
public interface CursoObserver {
    void atualizar(Curso avaliacao, String evento);
}

@Component
public class LogObserver implements CursoObserver {
    public void atualizar(Curso avaliacao, String evento) {
        System.out.println("Curso " + avaliacao.getId() + ": " + evento);
    }
}
```
- **Tem que ter:** a interface e uma classe `@Component` para cada reação (log, e-mail...).
- **No service:** o Spring injeta todos os observers numa lista:
```java
@Autowired(required = false)
private List<CursoObserver> observers;

private void notificar(Curso avaliacao, String evento) {
    if (observers != null) {
        for (CursoObserver o : observers) {
            o.atualizar(avaliacao, evento);
        }
    }
}
```
Chamar `notificar(salvo, "CRIADO")` depois do `save`.
- **Teste do service:** colocar um observer falso na lista:
```java
@Mock private CursoObserver observer;

ReflectionTestUtils.setField(cursoService, "observers", List.of(observer));
cursoService.criar(dto);
Mockito.verify(observer).atualizar(Mockito.any(), Mockito.eq("CRIADO"));
```
O `if (observers != null)` também é um caminho: fazer um teste **sem** setar a lista (fica `null`).
