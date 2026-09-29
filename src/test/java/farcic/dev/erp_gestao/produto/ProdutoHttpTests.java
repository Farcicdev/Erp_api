package farcic.dev.erp_gestao.produto;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import farcic.dev.erp_gestao.produto.repository.ProdutoRepository;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.user.entity.Usuario;
import farcic.dev.erp_gestao.user.entity.UsuarioCliente;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProdutoHttpTests {
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @Autowired
    ProdutoRepository produtos;
    Revenda revenda;
    Cliente cliente;
    Loja loja, outraLoja, lojaOutroCliente, lojaOutraRevenda;
    Usuario usuario;
    UsuarioCliente vinculo;
    long quantidadeInicial;

    private static final String JSON = """
            {"codigoInterno":" A ","descricao":" Produto ","gtin":" 12345678 ",
             "unidade":"UN","ncm":"12345678","cest":"1234567"}
            """;

    @BeforeEach
    void preparar() {
        revenda = revenda("produto-a");
        cliente = cliente(revenda);
        loja = loja(cliente, "11111111000111");
        outraLoja = loja(cliente, "22222222000122");
        lojaOutroCliente = loja(cliente(revenda), "33333333000133");
        lojaOutraRevenda = loja(cliente(revenda("produto-b")), "44444444000144");
        usuario = new Usuario("admin-produto");
        em.persist(usuario);
        vinculo = new UsuarioCliente(usuario, cliente);
        em.persist(vinculo);
        em.flush();
        quantidadeInicial = produtos.count();
    }

    @Test
    void criaProdutoNormalizadoNaLojaDaUrlEPersisteDados() throws Exception {
        criar(loja, JSON).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.lojaId").value(loja.getId()))
                .andExpect(jsonPath("$.codigoInterno").value("A"))
                .andExpect(jsonPath("$.descricao").value("Produto"))
                .andExpect(jsonPath("$.gtin").value("12345678"))
                .andExpect(jsonPath("$.unidade").value("UN"))
                .andExpect(jsonPath("$.ncm").value("12345678"))
                .andExpect(jsonPath("$.cest").value("1234567"))
                .andExpect(jsonPath("$.ativo").value(true));
        em.flush();
        em.clear();
        assertThat(produtos.count()).isEqualTo(quantidadeInicial + 1);
        assertThat(produtos.existsByLoja_IdAndCodigoInterno(loja.getId(), "A")).isTrue();
        assertThat(produtos.existsByLoja_IdAndGtin(loja.getId(), "12345678")).isTrue();
    }

    @Test
    void semAutenticacaoRetorna401() throws Exception {
        mvc.perform(post(url(loja)).contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isUnauthorized());
        naoGravou();
    }

    @ParameterizedTest
    @ValueSource(strings = {"OWNER", "ADMIN_REVENDA", "SEM_PERMISSAO"})
    void vinculoSemRoleCorretaRetorna403(String role) throws Exception {
        mvc.perform(post(url(loja)).with(jwt().jwt(token -> token.subject(usuario.getKeycloakSub()))
                        .authorities(new SimpleGrantedAuthority("ROLE_" + role)))
                .contentType(MediaType.APPLICATION_JSON).content(JSON)).andExpect(status().isForbidden());
        naoGravou();
    }

    @Test
    void roleSemVinculoRetorna403MesmoInformandoSubNoBody() throws Exception {
        mvc.perform(post(url(loja)).with(jwt().jwt(token -> token.subject("sem-vinculo"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_CLIENTE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.replace("{", "{\"keycloakSub\":\"admin-produto\",")))
                .andExpect(status().isForbidden());
        naoGravou();
    }

    @Test
    void trocarLojaParaOutroClienteOuRevendaRetorna403() throws Exception {
        criar(lojaOutroCliente, JSON).andExpect(status().isForbidden());
        criar(lojaOutraRevenda, JSON).andExpect(status().isForbidden());
        naoGravou();
    }

    @ParameterizedTest
    @ValueSource(strings = {"usuario", "vinculo", "cliente", "revenda", "loja"})
    void inativosBloqueiamCadastro(String estado) throws Exception {
        switch (estado) {
            case "usuario" -> usuario.setAtivo(false);
            case "vinculo" -> vinculo.setAtivo(false);
            case "cliente" -> cliente.setAtivo(false);
            case "revenda" -> revenda.setAtivo(false);
            case "loja" -> loja.setAtivo(false);
        }
        em.flush();
        criar(loja, JSON).andExpect(status().is(estado.equals("loja") ? 409 : 403));
        naoGravou();
    }

    @Test
    void lojaInexistenteRetorna404() throws Exception {
        criar(Loja.builder().id(-1L).build(), JSON).andExpect(status().isNotFound());
        naoGravou();
    }

    @Test
    void codigoDuplicadoRetorna409InclusiveQuandoProdutoInativo() throws Exception {
        criar(loja, JSON).andExpect(status().isCreated());
        em.createQuery("update Produto p set p.ativo = false where p.loja.id = :lojaId")
                .setParameter("lojaId", loja.getId()).executeUpdate();
        em.clear();
        criar(loja, JSON.replace("12345678 ", "87654321 "))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Código interno já cadastrado nesta loja"));
        assertThat(produtos.count()).isEqualTo(quantidadeInicial + 1);
    }

    @Test
    void gtinDuplicadoRetorna409() throws Exception {
        criar(loja, JSON).andExpect(status().isCreated());
        criar(loja, JSON.replace(" A ", "B")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("GTIN já cadastrado nesta loja"));
        assertThat(produtos.count()).isEqualTo(quantidadeInicial + 1);
    }

    @Test
    void mesmoCodigoEGtinEmLojasDiferentesRetorna201() throws Exception {
        criar(loja, JSON).andExpect(status().isCreated());
        criar(outraLoja, JSON).andExpect(status().isCreated())
                .andExpect(jsonPath("$.lojaId").value(outraLoja.getId()));
        assertThat(produtos.count()).isEqualTo(quantidadeInicial + 2);
    }

    @Test
    void permiteMultiplosProdutosSemGtinECest() throws Exception {
        String semOpcionais = """
                {"codigoInterno":"A","descricao":"Produto","unidade":"UN","ncm":"12345678"}
                """;
        criar(loja, semOpcionais).andExpect(status().isCreated())
                .andExpect(jsonPath("$.gtin").isEmpty()).andExpect(jsonPath("$.cest").isEmpty());
        criar(loja, semOpcionais.replace("\"A\"", "\"B\"")
                .replace("{", "{\"gtin\":\"  \",\"cest\":\"  \","))
                .andExpect(status().isCreated());
        em.flush();
        assertThat(produtos.count()).isEqualTo(quantidadeInicial + 2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"codigoVazio", "codigoLongo", "descricaoVazia", "descricaoLonga",
            "unidadeAusente", "unidadeInvalida", "ncmAusente", "ncmInvalido", "gtinInvalido", "cestInvalido",
            "jsonInvalido", "objetoVazio"})
    void dadosInvalidosRetornam400SemGravar(String caso) throws Exception {
        String body = switch (caso) {
            case "codigoVazio" -> JSON.replace(" A ", " ");
            case "codigoLongo" -> JSON.replace(" A ", "A".repeat(51));
            case "descricaoVazia" -> JSON.replace(" Produto ", " ");
            case "descricaoLonga" -> JSON.replace(" Produto ", "A".repeat(256));
            case "unidadeAusente" -> JSON.replace("\"UN\"", "null");
            case "unidadeInvalida" -> JSON.replace("\"UN\"", "\"INVALIDA\"");
            case "ncmAusente" -> JSON.replace("\"ncm\":\"12345678\"", "\"ncm\":null");
            case "ncmInvalido" -> JSON.replace("\"ncm\":\"12345678\"", "\"ncm\":\"abcdefgh\"");
            case "gtinInvalido" -> JSON.replace(" 12345678 ", "123");
            case "cestInvalido" -> JSON.replace("\"cest\":\"1234567\"", "\"cest\":\"123\"");
            case "jsonInvalido" -> "{";
            default -> "{}";
        };
        criar(loja, body).andExpect(status().isBadRequest());
        naoGravou();
    }

    @Test
    void listaSomenteAtivosDaLojaComPaginacaoETotalCorretos() throws Exception {
        criar(loja, JSON).andExpect(status().isCreated());
        em.createQuery("update Produto p set p.ativo = false where p.loja.id = :lojaId")
                .setParameter("lojaId", loja.getId()).executeUpdate();
        em.clear();
        criar(loja, JSON.replace(" A ", "B").replace("12345678 ", "87654321 "))
                .andExpect(status().isCreated());
        criar(loja, JSON.replace(" A ", "C").replace("12345678 ", "11111111 "))
                .andExpect(status().isCreated());
        criar(outraLoja, JSON).andExpect(status().isCreated());

        for (int page = 0; page < 2; page++) {
            listar(loja, page, 1).andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].ativo").value(true))
                    .andExpect(jsonPath("$.content[0].lojaId").value(loja.getId()))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.number").value(page));
        }
        listar(loja, 2, 1).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void lojaSomenteComInativosRetornaPaginaVazia() throws Exception {
        criar(loja, JSON).andExpect(status().isCreated());
        em.createQuery("update Produto p set p.ativo = false where p.loja.id = :lojaId")
                .setParameter("lojaId", loja.getId()).executeUpdate();
        em.clear();
        listar(loja, 0, 10).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listagemExigeAutenticacaoRoleEVinculoComCliente() throws Exception {
        mvc.perform(get(url(loja))).andExpect(status().isUnauthorized());
        mvc.perform(get(url(loja)).with(jwt().jwt(token -> token.subject("admin-produto"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_REVENDA"))))
                .andExpect(status().isForbidden());
        listar(lojaOutroCliente, 0, 10).andExpect(status().isForbidden());
        listar(lojaOutraRevenda, 0, 10).andExpect(status().isForbidden());
    }

    @Test
    void listagemValidaParametrosDePaginacao() throws Exception {
        listar(loja, -1, 10).andExpect(status().isBadRequest());
        listar(loja, 0, 0).andExpect(status().isBadRequest());
        listar(loja, 0, 101).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\u2003"})
    void edicaoRejeitaCodigoEDescricaoEmBranco(String valor) throws Exception {
        Long id = criarProdutoParaAlterar();
        String jsonValor = valor.replace("\t", "\\t");
        for (String campo : new String[]{"codigoInterno", "descricao"}) {
            alterar(loja, id, "/alterar", "{\"" + campo + "\":\"" + jsonValor + "\"}")
                    .andExpect(status().isBadRequest());
        }
        assertThat(produtos.findById(id).orElseThrow().getCodigoInterno()).isEqualTo("A");
        assertThat(produtos.findById(id).orElseThrow().getDescricao()).isEqualTo("Produto");
    }

    @Test
    void edicaoParcialNormalizaTextoEPreservaCamposOmitidos() throws Exception {
        Long id = criarProdutoParaAlterar();
        alterar(loja, id, "/alterar", "{\"descricao\":\" Nova descrição \"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Nova descrição"))
                .andExpect(jsonPath("$.codigoInterno").value("A"));
        em.flush();
        em.clear();
        assertThat(produtos.findById(id).orElseThrow().getDescricao()).isEqualTo("Nova descrição");
    }

    @Test
    void edicaoRejeitaCodigoEGtinDuplicados() throws Exception {
        Long id = criarProdutoParaAlterar();
        criar(loja, JSON.replace(" A ", "B").replace("12345678 ", "87654321 "))
                .andExpect(status().isCreated());
        alterar(loja, id, "/alterar", "{\"codigoInterno\":\" B \"}").andExpect(status().isConflict());
        alterar(loja, id, "/alterar", "{\"gtin\":\"87654321\"}").andExpect(status().isConflict());
    }

    @Test
    void statusExplicitoPermiteRepeticaoListagemInativaEReativacao() throws Exception {
        Long id = criarProdutoParaAlterar();
        for (int i = 0; i < 2; i++) {
            alterar(loja, id, "/status", "{\"ativo\":false}").andExpect(status().isOk());
        }
        listar(loja, 0, 10).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get(url(loja)).param("ativo", "false").with(auth()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].ativo").value(false));
        for (int i = 0; i < 2; i++) {
            alterar(loja, id, "/status", "{\"ativo\":true}").andExpect(status().isOk());
        }
        em.flush();
        em.clear();
        assertThat(produtos.findById(id).orElseThrow().getAtivo()).isTrue();
    }

    @Test
    void statusExigeValorEProdutoExistente() throws Exception {
        Long id = criarProdutoParaAlterar();
        alterar(loja, id, "/status", "{}").andExpect(status().isBadRequest());
        alterar(loja, id, "/status", "{\"ativo\":null}").andExpect(status().isBadRequest());
        alterar(loja, -1L, "/status", "{\"ativo\":false}").andExpect(status().isNotFound());
        assertThat(produtos.findById(id).orElseThrow().getAtivo()).isTrue();
    }

    @Test
    void edicaoEStatusNaoAlteramProdutoDeOutraLoja() throws Exception {
        Long id = criarProdutoParaAlterar();
        alterar(outraLoja, id, "/alterar", "{\"descricao\":\"Indevida\"}")
                .andExpect(status().isBadRequest());
        alterar(outraLoja, id, "/status", "{\"ativo\":false}").andExpect(status().isNotFound());
        for (Loja destino : new Loja[]{lojaOutroCliente, lojaOutraRevenda}) {
            alterar(destino, id, "/alterar", "{\"descricao\":\"Indevida\"}").andExpect(status().isForbidden());
            alterar(destino, id, "/status", "{\"ativo\":false}").andExpect(status().isForbidden());
        }
        assertThat(produtos.findById(id).orElseThrow().getDescricao()).isEqualTo("Produto");
        assertThat(produtos.findById(id).orElseThrow().getAtivo()).isTrue();
    }

    @Test
    void edicaoEStatusExigemAutenticacaoRoleEVinculo() throws Exception {
        Long id = criarProdutoParaAlterar();
        for (String operacao : new String[]{"/alterar", "/status"}) {
            String body = operacao.equals("/status") ? "{\"ativo\":false}" : "{\"descricao\":\"Indevida\"}";
            String caminho = url(loja) + "/" + id + operacao;
            mvc.perform(patch(caminho).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isUnauthorized());
            mvc.perform(patch(caminho).with(jwt().jwt(token -> token.subject("admin-produto"))
                            .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_REVENDA")))
                    .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
            mvc.perform(patch(caminho).with(jwt().jwt(token -> token.subject("sem-vinculo"))
                            .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_CLIENTE")))
                    .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        }
        assertThat(produtos.findById(id).orElseThrow().getDescricao()).isEqualTo("Produto");
        assertThat(produtos.findById(id).orElseThrow().getAtivo()).isTrue();
    }

    @Test
    void buscaFiltraTextoCodigoGtinLojaEStatus() throws Exception {
        Long id = criarProdutoParaAlterar();
        criar(outraLoja, JSON).andExpect(status().isCreated());
        for (String busca : new String[]{"pRoDu", "A", "12345678"}) {
            mvc.perform(get(url(loja) + "/buscar").param("busca", busca).with(auth()))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(id));
        }
        alterar(loja, id, "/status", "{\"ativo\":false}").andExpect(status().isOk());
        mvc.perform(get(url(loja) + "/buscar").param("busca", "A").with(auth()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get(url(lojaOutroCliente) + "/buscar").with(auth()))
                .andExpect(status().isForbidden());
    }

    private Long criarProdutoParaAlterar() throws Exception {
        criar(loja, JSON).andExpect(status().isCreated());
        return produtos.findAllByLoja_IdAndAtivoTrue(loja.getId(), org.springframework.data.domain.Pageable.unpaged())
                .getContent().getFirst().getId();
    }

    private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor auth() {
        return jwt().jwt(token -> token.subject("admin-produto"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_CLIENTE"));
    }

    private ResultActions alterar(Loja destino, Long id, String operacao, String body) throws Exception {
        return mvc.perform(patch(url(destino) + "/" + id + operacao).with(auth())
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private ResultActions listar(Loja destino, int page, int size) throws Exception {
        return mvc.perform(get(url(destino)).param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(jwt().jwt(token -> token.subject("admin-produto"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_CLIENTE"))));
    }

    private ResultActions criar(Loja destino, String body) throws Exception {
        return mvc.perform(post(url(destino)).with(jwt().jwt(token -> token.subject("admin-produto"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_CLIENTE")))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private String url(Loja destino) { return "/lojas/" + destino.getId() + "/produtos"; }
    private void naoGravou() { assertThat(produtos.count()).isEqualTo(quantidadeInicial); }

    private Revenda revenda(String nome) {
        Revenda entity = Revenda.builder().nome(nome).emailContato(nome + "@teste.local").cnpj(nome).build();
        em.persist(entity);
        return entity;
    }

    private Cliente cliente(Revenda pai) {
        Cliente entity = Cliente.builder().nome("Cliente").revenda(pai).build();
        em.persist(entity);
        return entity;
    }

    private Loja loja(Cliente pai, String cnpj) {
        Loja entity = Loja.builder().nome("Loja").nomeFantasia("Loja").razaoSocial("Loja")
                .cnpj(cnpj).inscricaoEstadual("123").regimeTributario(RegimeTributario.SIMPLES_NACIONAL)
                .cliente(pai).build();
        em.persist(entity);
        return entity;
    }
}
