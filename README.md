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
