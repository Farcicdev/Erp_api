package farcic.dev.erp_gestao.produto;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import farcic.dev.erp_gestao.produto.repository.ProdutosRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProdutoHttpTests {
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @Autowired
    ProdutosRepository produtos;
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
        em.createQuery("update Produtos p set p.ativo = false where p.loja.id = :lojaId")
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
