# Modelo organizacional

Revenda → Cliente → Loja.

- Revenda comercializa e administra o ERP.
- Cliente é a conta contratante: agrupamento, contrato e vínculos de usuários.
- Loja é o estabelecimento fiscal e operacional, com CNPJ, inscrição estadual e regime tributário.
- As lojas do mesmo cliente permanecem separadas. Futuros produtos, preços, estoques,
  vendas, caixas e documentos fiscais devem ter Loja como limite de dados.
  Esses módulos não são implementados nesta refatoração.

## Rotas e permissões

| Rotas | Permissão |
| --- | --- |
| `/revendas` e administração de revendas | OWNER (mantido) |
| POST/GET `/revendas/{revendaId}/clientes` | ADMIN_REVENDA vinculado |
| GET `/revendas/{revendaId}/clientes/{clienteId}` | ADMIN_REVENDA vinculado à revenda ou ADMIN_CLIENTE vinculado ao cliente |
| PATCH `/revendas/{revendaId}/clientes/{clienteId}/status` | ADMIN_REVENDA vinculado |
| POST `/revendas/{revendaId}/clientes/{clienteId}/usuarios` | ADMIN_REVENDA vinculado; resposta 201 |
| POST `/revendas/{revendaId}/clientes/{clienteId}/lojas` | ADMIN_REVENDA vinculado; resposta 201 |
| GET `/revendas/{revendaId}/clientes/{clienteId}/lojas` e `/{lojaId}` | ADMIN_REVENDA vinculado à revenda ou ADMIN_CLIENTE vinculado ao cliente |
| PATCH `/revendas/{revendaId}/clientes/{clienteId}/lojas/{lojaId}/status` | ADMIN_REVENDA vinculado |
| GET `/me/clientes` e `/me/lojas` | ADMIN_CLIENTE; somente dados ativos acessíveis pelo sub do JWT |

O papel vem do JWT; o banco comprova vínculo e estados ativos. Ter papel não basta.
AcessoOrganizacionalService seleciona a regra de leitura conforme as roles autenticadas,
sem receber permissões do body ou da URL. Um usuário com ambas as roles pode ler por
qualquer um dos vínculos válidos. AcessoRevendaService concentra a consulta de autorização
à revenda. ConsultaClienteService concentra a busca do cliente ativo dentro da revenda.

## Decisões adotadas

- As listagens retornam registros ativos. `/me` retorna lista vazia quando não há
  vínculos ativos elegíveis; o acesso direto sem vínculo válido retorna 403.
- POST/PATCH são administrativos: ADMIN_CLIENTE tem acesso de leitura nesta tarefa.
- PATCH de status pode reativar o próprio cliente/loja inativo, mas exige administrador,
  usuário, vínculo e pais ativos. Essa é a única exceção ao bloqueio do registro inativo.
- Vínculo já existente, inclusive inativo, retorna 409. Não foi criado fluxo de reativação de vínculos.
- CNPJ de loja continua único globalmente, inclusive para lojas inativas.
- As rotas antigas e a role antiga não têm aliases de compatibilidade.
- O cadastro de vínculo usa o sub informado; não cria usuário nem atribui roles no Keycloak.

## Migração e implantação

V1–V3 permanecem intactas. V4 renomeia tabelas, colunas, sequences, constraints e índices,
sem copiar ou excluir registros. Os IDs, contadores e relacionamentos existentes são mantidos.

Antes de implantar:

1. Fazer backup e validar V4 numa cópia do banco.
2. Criar a client role `ADMIN_CLIENTE` no client `erp-api` do Keycloak.
3. Atribuir a nova role aos usuários/grupos que usavam a role anterior e conferir
   sua presença em `resource_access.erp-api.roles` nos novos tokens.
4. Atualizar consumidores/Postman para `/clientes`, `/me/clientes` e `clienteId`.
5. Coordenar a parada da versão antiga e a migração/subida da nova versão.
6. Renovar tokens; remover atribuições antigas quando a migração dos usuários estiver concluída.

Renomeações exigem locks no PostgreSQL e podem aguardar transações abertas. A versão
antiga não funciona sobre o schema novo. A V4 pressupõe o schema criado por V1–V3,
sem renomeações manuais anteriores. Views, consultas SQL externas e integrações que
referenciem os nomes antigos precisam ser revisadas. Não aplicar rollback somente do
código após a V4: planejar migração reversa ou restauração coordenada.

## Testes

Os testes Spring usam o profile `test` e PostgreSQL separado; não usam `.env` nem o banco local do ERP.

```bash
docker run --rm -d --name erp-cliente-refactor-test \
  -e POSTGRES_DB=erp_refactor_test -e POSTGRES_USER=erp_test -e POSTGRES_PASSWORD=erp_test \
  -p 127.0.0.1:55439:5432 postgres:17-alpine
./mvnw clean test
docker stop erp-cliente-refactor-test
```

É possível configurar `ERP_TEST_DB_URL`, `ERP_TEST_DB_USERNAME` e `ERP_TEST_DB_PASSWORD`
para outro banco **exclusivo de testes**. O teste da migration cria e remove seu próprio
schema e requer permissão para criar schemas. Os testes HTTP simulam o JWT e exercitam
os controllers, a autorização, os services e as consultas no PostgreSQL. Não validam
um servidor Keycloak real.

MigrationClienteTests começa em V3 com dados, aplica V4, valida preservação de IDs,
sequences, FKs, unicidade e índices. Referências aos nomes antigos permanecem apenas
nas migrations históricas, na V4 e nesse teste de transição. ANALISE_ERP_API.md é uma
análise histórica anterior a esta refatoração.

## Resultado da validação desta refatoração

- Base inspecionada: commit `7a7ac32`, correspondente ao HEAD remoto na consulta inicial.
- `./mvnw -o -DskipTests compile`: sucesso.
- `./mvnw -o clean test`: **43 testes, 0 falhas, 0 erros, 0 ignorados**.
- PostgreSQL 17 temporário, separado do banco da aplicação; V4 testada em schema novo
  e na transição de V3 com dados. Hibernate validou o schema resultante.
- `git diff --check`: sem problemas; V1–V3 sem alterações.
- Busca final: sem referências antigas no Java da aplicação. Nomes anteriores
  aparecem no teste de migração por necessidade de preparar o schema V3.
- Nenhum commit, push ou alteração no Keycloak foi realizado.

Pontos de negócio para confirmar na revisão: a exceção de reativação pelo PATCH de
status e ADMIN_CLIENTE somente com leitura dos cadastros organizacionais. As decisões
adotadas estão descritas acima e cobertas pelos testes.
