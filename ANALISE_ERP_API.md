# Análise do ERP API — início do desenvolvimento

**Você está começando o projeto e está trabalhando na entidade Cliente. Revenda tem uma primeira implementação e Estabelecimento é apenas um esboço da etapa seguinte. A próxima implementação deve continuar Cliente e seu relacionamento com Revenda, em um passo pequeno e explicado.**

A análise anterior cobrou várias etapas futuras como se o sistema já estivesse pronto para uso. Esta revisão considera o momento real do desenvolvimento: aprender, construir a base por partes e só depois avançar para outros módulos.

Esta análise se baseia na leitura do código e das configurações disponíveis. A aplicação e os testes não foram executados. A revisão altera apenas este documento.

## 1. O objetivo que orienta o projeto

O sistema será um ERP em nuvem com PDV local. A hierarquia desejada é:

```text
OWNER — cadastra → Revenda
                    │
                    └── Empresa/Cliente
                          │
                          └── Loja/Estabelecimento
```

- OWNER cadastra revendas.
- Cada revenda administra suas empresas/clientes.
- Cada empresa pode ter várias lojas.
- Keycloak cuida de identidade, senha, token e roles.
- PostgreSQL guarda as organizações, os vínculos dos usuários e os dados de negócio.
- Será usado um único realm `erp`, com os clients `erp-web` e `erp-api`.
- Produto e estoque ficam para depois de fechar a base de revenda, empresa e loja.

Esses pontos orientam as decisões atuais, mas não precisam ser implementados todos de uma vez.

## 2. Onde o código está agora

| Parte | Estado real | Como interpretar |
|---|---|---|
| Revenda | Entidade, repository, service, controller, DTOs e mapper | Primeira implementação de cadastro e consultas, ainda sujeita a ajustes |
| Cliente | Entidade em construção, com campos e relacionamento com Revenda | É o trabalho atual |
| Estabelecimento | Classe com uma referência para Cliente | Esboço para uma etapa ainda não desenvolvida |
| Keycloak | Dependências, serviço no Compose e issuer nos profiles | Preparação da integração |
| Vínculos de usuários e autorização por organização | Ainda não implementados | Etapa futura necessária para o uso por várias organizações |
| Produto, estoque e PDV | Ainda não implementados | Fora do escopo atual, conforme planejado |

A ausência de funcionalidades futuras não é, por si só, um defeito nesta fase.

### Revenda

A entidade possui ID, nome, e-mail de contato, CNPJ, indicador de ativo e coleção de clientes.

O código já oferece criação, listagem paginada, busca por ID e exclusão física. Isso fornece um primeiro exemplo do caminho da requisição até o banco. Não significa que o cadastro esteja finalizado ou validado em execução.

### Cliente — o ponto atual

[Cliente.java](src/main/java/farcic/dev/erp_gestao/cliente/entity/Cliente.java) já contém:

- ID e configuração de geração por sequence.
- Nome obrigatório.
- E-mail de contato e telefone.
- Relacionamento com Revenda, por `revenda_id` obrigatório.
- Uma coleção de estabelecimentos, antecipando a relação futura.

O relacionamento com Revenda corresponde à regra desejada: vários clientes podem pertencer a uma revenda, e cada cliente aponta para uma revenda.

### Estabelecimento — etapa seguinte

[Estabelecimento.java](src/main/java/farcic/dev/erp_gestao/estabelecimento/entity/Estabelecimento.java) ainda não representa um cadastro desenvolvido. Não faz sentido avaliar a falta de campos, endpoints ou regras como se esse trabalho já tivesse sido concluído.

Existe somente um detalhe técnico imediato: como a classe já está anotada com `@Entity`, o JPA tentará mapeá-la ao iniciar a aplicação. Sem um identificador, esse mapeamento é inválido. Isso só precisa entrar no passo atual se você quiser executar a aplicação antes de desenvolver Estabelecimento.

Nesse caso, é possível deixar o esboço temporariamente fora do mapeamento, ajustando também a associação em Cliente, ou definir a identidade mínima da entidade. A escolha deve ser explicada antes de alterar o código; ela não exige desenvolver todo o cadastro de loja agora.

## 3. A arquitetura escolhida até aqui

O [pom.xml](pom.xml) declara Java 21, Spring Boot 4.1.1, Spring MVC, JPA, Validation, Security, OAuth2 Resource Server, Flyway e PostgreSQL.

A organização visível em Revenda é por domínio, com camadas dentro dele:

```text
Controller → Service → Repository → PostgreSQL
     DTOs ↔ Mapper ↔ Entity
```

Cada parte tem um papel:

| Parte | Responsabilidade |
|---|---|
| Entity | Representar os dados persistidos e seus relacionamentos |
| Repository | Consultar e persistir entidades |
| Service | Executar as regras e coordenar uma operação de negócio |
| DTO | Definir os dados recebidos e devolvidos pela API |
| Mapper | Converter DTOs e entidades |
| Controller | Receber a requisição HTTP e encaminhar a operação |

Essa organização pode continuar sendo usada. Um monólito organizado por domínio é uma escolha adequada para aprender e desenvolver essa base gradualmente.

O projeto também já configura `open-in-view: false`, Flyway habilitado e `ddl-auto: validate`. A intenção é manter a criação do esquema em migrations e usar o Hibernate para conferir se o banco corresponde às entidades. As migrations ainda serão necessárias quando você for validar a persistência em um banco vazio.

## 4. O que decidir agora em Cliente

### O significado de Cliente

Pelo objetivo descrito, Cliente parece representar a empresa que contrata o ERP por meio de uma revenda.

Vale definir esse significado antes de expandir a classe, porque futuramente também existirá o cliente que compra no PDV.

Minha sugestão é usar `Empresa` para a organização que utiliza o ERP. Entretanto, `Cliente` não é automaticamente um nome errado: ele pode ser mantido se o domínio deixar claro que significa cliente da revenda. A renomeação é uma decisão de linguagem do projeto, não uma exigência técnica.

Da mesma forma, `Estabelecimento` pode representar uma loja. Não é necessário trocar esse nome apenas para seguir uma preferência estética.

### O vínculo com Revenda

Este é o ponto principal para aprender agora:

```text
Revenda 1 ─── N Cliente
```

Cliente contém a chave estrangeira `revenda_id`; por isso, seu lado controla a associação no banco. A coleção em Revenda, com `mappedBy = "revenda"`, representa o outro lado da mesma relação. Não é uma segunda associação nem exige uma tabela intermediária.

`nullable = false` expressa que a coluna não deve aceitar nulo. A migration precisa criar a restrição correspondente, e o serviço futuramente deverá verificar a revenda informada para devolver um erro compreensível.

### Os campos mínimos

Nome, e-mail e telefone já permitem começar a discutir o cadastro. Antes de acrescentar campos, defina quais são obrigatórios e o que representam.

O indicador de ativo é uma possibilidade útil, mas depende da regra de inativação que você quiser adotar. A localização do CNPJ também merece uma conversa quando for distinguir empresa, matriz e filiais. Não há necessidade de acrescentar todos os campos de um ERP agora.

## 5. Próxima implementação exata

**Concluir o mapeamento mínimo de Cliente e sua associação obrigatória com Revenda.**

O próximo passo deve ficar limitado a:

1. Definir se a entidade continuará chamada Cliente ou se representará explicitamente Empresa.
2. Revisar os campos já existentes e sua obrigatoriedade.
3. Conferir o identificador e ligar explicitamente `@GeneratedValue` ao gerador de sequence declarado.
4. Conferir os dois lados do relacionamento Revenda–Cliente e explicar quem guarda a chave estrangeira.
5. Decidir se a coleção de estabelecimentos deve permanecer neste momento ou entrar quando Estabelecimento for desenvolvido.

**Critério de conclusão deste passo:** a entidade tem um significado claro, campos mínimos definidos e um relacionamento coerente com Revenda; você consegue explicar como esse relacionamento será armazenado no banco.

Essa conclusão é sobre o mapeamento. Para comprovar a persistência funcionando, o passo seguinte precisa incluir migrations, banco e resolução do esboço JPA de Estabelecimento.

Depois, construir uma operação de criação de Cliente, uma camada por vez:

1. Migration correspondente ao esquema necessário, considerando se já existe banco com dados.
2. Repository de Cliente.
3. DTO de criação com os dados mínimos e identificação da revenda.
4. Service que busca a revenda, valida a operação e salva o cliente.
5. DTO de resposta e controller.
6. Verificação de criação válida, revenda inexistente e campos obrigatórios.

Receber um ID de revenda no request não concede permissão sobre ela. Quando a autorização entrar nesse fluxo, o serviço precisará verificar também o vínculo do usuário autenticado.

O desenvolvimento local pode avançar por esses passos; disponibilizar a operação para usuários de organizações diferentes depende da autorização descrita adiante.

## 6. Ajustes na primeira implementação de Revenda

Estes são pontos concretos do código já escrito para revisar conforme o cadastro for exercitado. Eles não exigem interromper Cliente para refazer todo o projeto.

| Ponto | Por que revisar | Momento adequado |
|---|---|---|
| Criação sem `@RequestBody` | O parâmetro não está declarado para receber JSON pelo mecanismo esperado | Ao testar a criação pela API |
| DTO sem validações e controller sem `@Valid` | Campos inválidos precisam de uma resposta compreensível | Ao fechar o contrato de criação |
| Mapper copia `ativo` do request | Um valor omitido pode resultar em nulo | Ao definir o estado inicial da revenda |
| Valores iniciais com Lombok `@Builder` | Inicializadores de campos não se tornam automaticamente defaults do builder | Ao revisar a construção das entidades |
| Gerador de sequence sem referência explícita em `@GeneratedValue` | É melhor alinhar o gerador utilizado com a migration | Ao preparar a persistência |
| Consultas devolvem entidades JPA | Relações lazy e bidirecionais podem complicar a serialização | Ao testar listagem e busca |
| Exceção genérica para revenda inexistente | A API precisa distinguir ausência de registro de erro interno | Ao implementar respostas de erro |
| Exclusão física | Será necessário definir o comportamento quando houver empresas vinculadas | Antes de usar exclusão com dados relacionados |

Não há migrations versionadas na leitura realizada. Com `ddl-auto: validate`, um banco vazio não será criado pelo Hibernate. Isso é uma tarefa de preparação da primeira execução persistente, não evidência de que um sistema pronto perdeu seu esquema.

O único teste atual é `contextLoads()`. Testes de negócio podem acompanhar cada operação implementada. A ausência de testes para módulos ainda não desenvolvidos é esperada.

## 7. Keycloak: o que já está preparado e o que vem depois

O [docker-compose.yaml](docker-compose.yaml) prepara um Keycloak com banco próprio, separado do banco do ERP. Os profiles da API possuem configuração de `issuer-uri`.

Há um alinhamento simples a fazer quando você trabalhar nessa integração: o profile local usa `erp-realm` como padrão, enquanto o realm desejado é `erp`. A variável `KEYCLOAK_REALM` também não aparece no `.env.example`.

O repositório não demonstra a criação dos clients e das roles. Eles podem ter sido configurados manualmente; a leitura dos arquivos não permite confirmar isso.

A divisão planejada permanece:

| Elemento | Responsabilidade |
|---|---|
| Realm `erp` | Reunir as identidades do ERP |
| Client `erp-web` | Participar do login da aplicação web |
| Client `erp-api` | Representar a API e suas roles, conforme a configuração escolhida |
| Keycloak | Identidade, senha, emissão de tokens e roles |
| PostgreSQL do ERP | Organizações, vínculos e dados de negócio |

Na etapa de segurança, será necessário configurar a validação do token, mapear as roles para as authorities do Spring e proteger as operações. A autoconfiguração de JWT não implementa sozinha a regra de OWNER. O Spring usa scopes como authorities por padrão; o mapeamento das roles deve ser definido explicitamente. A API também deverá validar a audience esperada. [Documentação do Spring Security](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html).

Se `erp-web` for uma SPA, o desenho previsto é um client público com Authorization Code e PKCE, sem segredo no navegador. Essa decisão será aplicada quando o login web for desenvolvido. [Documentação do Keycloak](https://www.keycloak.org/docs/latest/server_admin/).

O primeiro exercício de autorização pode ser pequeno: demonstrar que um usuário com OWNER consegue cadastrar revenda e um usuário sem essa role não consegue.

## 8. Vínculos e isolamento: requisito futuro da base

**Role responde o que o usuário pode fazer; vínculo responde em qual organização ele pode fazer.**

Exemplo: um administrador da revenda A não deve administrar uma empresa da revenda B, mesmo que seu token seja válido.

Quando essa etapa chegar, o PostgreSQL precisará associar a identidade do Keycloak, usando o `sub` do token, aos escopos permitidos. E-mail não deve ser usado como identificador estável desse vínculo. Não é necessário guardar senhas ou tokens nas tabelas de negócio.

Ainda não é preciso fixar todas as tabelas de vínculo. Primeiro devem ser definidas regras como:

- Um usuário pode trabalhar em mais de uma revenda ou empresa?
- O acesso a uma empresa inclui todas as suas lojas?
- Haverá usuários restritos a lojas específicas?
- OWNER administra apenas revendas ou também acessa dados operacionais das empresas?

Essas decisões determinarão os relacionamentos necessários. Não devem virar implementação automática nesta etapa de Cliente.

Uma chave estrangeira protege a integridade da relação, mas não verifica a permissão de quem fez a requisição. A API deverá aplicar o escopo autorizado nas operações, e os testes deverão comprovar que dados de uma organização não são acessíveis por outra indevidamente.

## 9. Sequência de desenvolvimento, sem pular o aprendizado

| Etapa | Foco | Resultado esperado |
|---|---|---|
| Agora | Cliente e vínculo com Revenda | Entidade mínima compreendida e modelada |
| Em seguida | Persistência e criação de Cliente | Primeiro fluxo de Cliente funcionando localmente |
| Depois | Estabelecimento/Loja | Identidade, campos mínimos e vínculo obrigatório com Cliente/Empresa |
| Próxima parte da base | Keycloak e OWNER | Cadastro de revenda protegido por role |
| Continuação da base | Vínculos e escopo de acesso | Operações limitadas às organizações autorizadas |
| Fechamento | Validação dos fluxos da hierarquia | Cadastros, integridade e isolamento verificados |
| Somente depois | Produto e estoque | Desenvolvimento sobre a base organizacional definida |

A segurança precisa estar concluída antes do uso compartilhado por organizações reais. Isso não impede aprender os mapeamentos e os cadastros localmente em etapas menores.

## 10. PDV local neste momento

A loja será a referência organizacional do futuro PDV. Por enquanto, essa informação serve para orientar o relacionamento Cliente/Empresa → Estabelecimento/Loja.

Operação offline, sincronização, identificação de terminais e conflitos serão assuntos de uma etapa própria. “PDV local” ainda não define, por si só, como funcionará sem conexão.

## 11. Como conduzir os próximos passos

Cada alteração deve começar com a regra que será representada, seguida da explicação do código e de uma verificação proporcional ao que foi construído.

Para o momento atual, a conversa deve se concentrar em Cliente: o que ele representa, quais dados precisa ter e por que contém a referência para Revenda. Depois que isso estiver claro, avançar para a persistência e para a primeira operação do cadastro.

**A próxima implementação é terminar o passo atual de Cliente. Estabelecimento, integração completa de segurança e os demais módulos entram progressivamente, sem exigir que você construa toda a base de uma vez.**
