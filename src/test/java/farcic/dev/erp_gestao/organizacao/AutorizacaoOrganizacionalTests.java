package farcic.dev.erp_gestao.organizacao;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.user.entity.*;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AutorizacaoOrganizacionalTests {
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    Revenda revenda, outraRevenda;
    Cliente cliente, outroCliente, clienteOutraRevenda;
    Loja loja, outraLoja, lojaOutroCliente, lojaOutraRevenda;
    Usuario admin, usuarioCliente;
    UsuarioRevenda vinculoRevenda;
    UsuarioCliente vinculoCliente;
    static final String LOJA_JSON = """
            {"nome":"Nova loja","nomeFantasia":"Fantasia","razaoSocial":"Razão social",
             "cnpj":"99999999000199","inscricaoEstadual":"123","regimeTributario":"SIMPLES_NACIONAL"}
            """;

    @BeforeEach
    void preparar() {
        revenda = revenda("a");
        outraRevenda = revenda("b");
        cliente = cliente(revenda, "Cliente A");
        outroCliente = cliente(revenda, "Cliente B");
        clienteOutraRevenda = cliente(outraRevenda, "Cliente C");
        loja = loja(cliente, "11111111000111");
        outraLoja = loja(cliente, "22222222000122");
        lojaOutroCliente = loja(outroCliente, "33333333000133");
        lojaOutraRevenda = loja(clienteOutraRevenda, "44444444000144");
        admin = new Usuario("admin-revenda"); em.persist(admin);
        usuarioCliente = new Usuario("admin-cliente"); em.persist(usuarioCliente);
        vinculoRevenda = new UsuarioRevenda(admin, revenda); em.persist(vinculoRevenda);
        vinculoCliente = new UsuarioCliente(usuarioCliente, cliente); em.persist(vinculoCliente);
        em.flush();
    }

    @Test void adminRevendaAcessaClienteDaPropriaRevenda() throws Exception {
        mvc.perform(get(clienteUrl(cliente)).with(admin())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()));
    }

    @Test void adminRevendaNaoAcessaOutraRevenda() throws Exception {
        mvc.perform(get(clienteUrl(clienteOutraRevenda)).with(admin())).andExpect(status().isForbidden());
        mvc.perform(get(lojaUrl(lojaOutraRevenda)).with(admin())).andExpect(status().isForbidden());
    }

    @Test void roleSemVinculoNaoAutoriza() throws Exception {
        mvc.perform(get(clienteUrl(cliente)).with(token("sem-vinculo", "ADMIN_REVENDA")))
                .andExpect(status().isForbidden());
        mvc.perform(get(lojaUrl(loja)).with(token("sem-vinculo", "ADMIN_CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest @ValueSource(strings = {"usuario", "vinculo", "revenda"})
    void estadosInativosBloqueiamAdminRevenda(String estado) throws Exception {
        if (estado.equals("usuario")) admin.setAtivo(false);
        if (estado.equals("vinculo")) vinculoRevenda.setAtivo(false);
        if (estado.equals("revenda")) revenda.setAtivo(false);
        em.flush();
        mvc.perform(get(clienteUrl(cliente)).with(admin())).andExpect(status().isForbidden());
        mvc.perform(get(lojaUrl(loja)).with(admin())).andExpect(status().isForbidden());
        mvc.perform(post(lojasUrl(cliente)).with(admin()).contentType(MediaType.APPLICATION_JSON).content(LOJA_JSON))
                .andExpect(status().isForbidden());
    }

    @Test void clienteInativoBloqueiaLojas() throws Exception {
        cliente.setAtivo(false); em.flush();
        mvc.perform(get(lojaUrl(loja)).with(admin())).andExpect(status().isConflict());
        mvc.perform(get(lojasUrl(cliente)).with(admin())).andExpect(status().isConflict());
        mvc.perform(post(lojasUrl(cliente)).with(admin()).contentType(MediaType.APPLICATION_JSON).content(LOJA_JSON))
                .andExpect(status().isConflict());
    }

    @Test void vinculoCriadoRetorna201ERepeticaoRetorna409() throws Exception {
        String url = clienteUrl(outroCliente) + "/usuarios";
        String body = "{\"keycloakSub\":\"novo-usuario\"}";
        mvc.perform(post(url).with(admin()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post(url).with(admin()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test void cnpjDuplicadoRetorna409MesmoEmOutroCliente() throws Exception {
        mvc.perform(post(lojasUrl(outroCliente)).with(admin()).contentType(MediaType.APPLICATION_JSON)
                        .content(LOJA_JSON.replace("99999999000199", loja.getCnpj())))
                .andExpect(status().isConflict());
    }

    @Test void criaLojaComClienteDaUrlEAtivoPorPadrao() throws Exception {
        mvc.perform(post(lojasUrl(cliente)).with(admin()).contentType(MediaType.APPLICATION_JSON).content(LOJA_JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test void meClientesUsaIdDoClienteNaoDoVinculo() throws Exception {
        // Força IDs diferentes, independentemente do estado das sequences.
        em.createNativeQuery("UPDATE usuario_cliente SET id = :id WHERE id = :antigo")
                .setParameter("id", cliente.getId() + 1000000).setParameter("antigo", vinculoCliente.getId()).executeUpdate();
        em.clear();
        mvc.perform(get("/me/clientes").with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test void adminClienteLeTodasAsLojasDoSeuCliente() throws Exception {
        mvc.perform(get("/me/lojas").with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get(lojasUrl(cliente)).with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
        mvc.perform(get(lojaUrl(outraLoja)).with(clienteAuth())).andExpect(status().isOk());
        mvc.perform(get(clienteUrl(cliente)).with(clienteAuth())).andExpect(status().isOk());
    }

    @Test void adminClienteNaoAdministraCadastros() throws Exception {
        mvc.perform(post(lojasUrl(cliente)).with(clienteAuth()).contentType(MediaType.APPLICATION_JSON).content(LOJA_JSON))
                .andExpect(status().isForbidden());
        mvc.perform(patch(lojaUrl(loja) + "/status").with(clienteAuth())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":false}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/me/lojas").with(admin())).andExpect(status().isForbidden());
    }

    @ParameterizedTest @ValueSource(strings = {"usuario", "vinculo", "cliente", "revenda"})
    void meFiltraTodosOsEstadosInativos(String estado) throws Exception {
        if (estado.equals("usuario")) usuarioCliente.setAtivo(false);
        if (estado.equals("vinculo")) vinculoCliente.setAtivo(false);
        if (estado.equals("cliente")) cliente.setAtivo(false);
        if (estado.equals("revenda")) revenda.setAtivo(false);
        em.flush();
        mvc.perform(get("/me/lojas").with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/me/clientes").with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get(lojaUrl(loja)).with(clienteAuth())).andExpect(status().isForbidden());
    }

    @Test void lojaInativaNaoApareceENaoPodeSerConsultada() throws Exception {
        loja.setAtivo(false); em.flush();
        mvc.perform(get("/me/lojas").with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get(lojaUrl(loja)).with(clienteAuth())).andExpect(status().isConflict());
    }

    @Test void trocarQualquerIdNaoPermiteAcessoCruzado() throws Exception {
        mvc.perform(get(clienteUrl(cliente).replace("/clientes/" + cliente.getId(), "/clientes/" + clienteOutraRevenda.getId()))
                .with(admin())).andExpect(status().isNotFound());
        mvc.perform(get(lojasUrl(cliente) + "/" + lojaOutroCliente.getId()).with(admin()))
                .andExpect(status().isNotFound());
        mvc.perform(get(lojaUrl(loja).replace("/revendas/" + revenda.getId(), "/revendas/" + outraRevenda.getId()))
                .with(admin())).andExpect(status().isForbidden());
        mvc.perform(get(lojaUrl(lojaOutroCliente)).with(clienteAuth())).andExpect(status().isForbidden());
        mvc.perform(get(lojasUrl(cliente) + "/" + lojaOutroCliente.getId()).with(clienteAuth()))
                .andExpect(status().isNotFound());
        mvc.perform(patch(lojasUrl(cliente) + "/" + lojaOutroCliente.getId() + "/status").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":false}"))
                .andExpect(status().isNotFound());
        assertThat(lojaOutroCliente.getAtivo()).isTrue();
    }

    @Test void permiteReativarClienteELojaMasExigePaisAtivos() throws Exception {
        loja.setAtivo(false); cliente.setAtivo(false); em.flush();
        mvc.perform(patch(lojaUrl(loja) + "/status").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":true}"))
                .andExpect(status().isConflict());
        mvc.perform(patch(clienteUrl(cliente) + "/status").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(true));
        mvc.perform(patch(lojaUrl(loja) + "/status").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(true));
    }

    @Test void validacaoRetorna400() throws Exception {
        mvc.perform(post(lojasUrl(cliente)).with(admin()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch(lojaUrl(loja) + "/status").with(admin()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post(lojasUrl(cliente)).with(admin()).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(lojasUrl(cliente) + "?size=0").with(admin())).andExpect(status().isBadRequest());
    }

    @Test void meLojasAgrupaSomenteClientesComVinculo() throws Exception {
        em.persist(new UsuarioCliente(usuarioCliente, outroCliente)); em.flush();
        mvc.perform(get("/me/clientes").with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/me/lojas").with(clienteAuth())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        mvc.perform(get(lojaUrl(lojaOutraRevenda)).with(clienteAuth())).andExpect(status().isForbidden());
    }

    @Test void ambasAsRolesPodemUsarVinculoDoCliente() throws Exception {
        mvc.perform(get(lojaUrl(loja)).with(jwt().jwt(jwt -> jwt.subject("admin-cliente"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_CLIENTE"),
                                new SimpleGrantedAuthority("ROLE_ADMIN_REVENDA"))))
                .andExpect(status().isOk());
    }

    @Test void semAutenticacaoNaoAcessaDados() throws Exception {
        mvc.perform(get("/me/lojas")).andExpect(status().isUnauthorized());
        mvc.perform(get(clienteUrl(cliente))).andExpect(status().isUnauthorized());
    }

    @Test void ownerContinuaCriandoVinculosDeRevendaCom201() throws Exception {
        mvc.perform(post("/revendas/" + outraRevenda.getId() + "/usuarios").with(token("owner", "OWNER"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"keycloakSub\":\"novo-admin\"}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/revendas").with(admin())).andExpect(status().isForbidden());
    }

    private RequestPostProcessor admin() { return token("admin-revenda", "ADMIN_REVENDA"); }
    private RequestPostProcessor clienteAuth() { return token("admin-cliente", "ADMIN_CLIENTE"); }
    private RequestPostProcessor token(String sub, String role) {
        return jwt().jwt(jwt -> jwt.subject(sub)).authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
    private String clienteUrl(Cliente c) { return "/revendas/" + c.getRevenda().getId() + "/clientes/" + c.getId(); }
    private String lojasUrl(Cliente c) { return clienteUrl(c) + "/lojas"; }
    private String lojaUrl(Loja l) { return lojasUrl(l.getCliente()) + "/" + l.getId(); }
    private Revenda revenda(String nome) {
        Revenda r = Revenda.builder().nome(nome).emailContato(nome + "@example.test").cnpj(nome).build();
        em.persist(r); return r;
    }
    private Cliente cliente(Revenda r, String nome) {
        Cliente c = Cliente.builder().nome(nome).revenda(r).build(); em.persist(c); return c;
    }
    private Loja loja(Cliente c, String cnpj) {
        Loja l = Loja.builder().nome("Loja").nomeFantasia("Loja").razaoSocial("Loja Ltda").cnpj(cnpj)
                .inscricaoEstadual("123").regimeTributario(RegimeTributario.SIMPLES_NACIONAL).cliente(c).build();
        em.persist(l); return l;
    }
}
