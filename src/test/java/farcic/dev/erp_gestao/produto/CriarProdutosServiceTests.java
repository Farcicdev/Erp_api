package farcic.dev.erp_gestao.produto;

import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.produto.dto.request.ProdutosRequest;
import farcic.dev.erp_gestao.produto.entity.Produtos;
import farcic.dev.erp_gestao.produto.entity.UnidadeComercial;
import farcic.dev.erp_gestao.produto.mapper.ProdutosMapper;
import farcic.dev.erp_gestao.produto.repository.ProdutosRepository;
import farcic.dev.erp_gestao.produto.service.CriarProdutosService;
import farcic.dev.erp_gestao.shared.exeception.AcessoClienteNegadoException;
import farcic.dev.erp_gestao.shared.exeception.ProdutoJaCadastradoException;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriarProdutosServiceTests {
    private final ProdutosRepository produtos = mock(ProdutosRepository.class);
    private final AcessoLojaService acesso = mock(AcessoLojaService.class);
    private final CriarProdutosService service = new CriarProdutosService(produtos, acesso, new ProdutosMapper());

    @Test
    void acessoNegadoNaoConsultaNemGravaProdutos() {
        when(acesso.buscarLojaAutorizada("usuario", 2L))
                .thenThrow(new AcessoClienteNegadoException("Acesso negado"));

        assertThatThrownBy(() -> service.cadastrarProdutos(request(null), 2L, "usuario"))
                .isInstanceOf(AcessoClienteNegadoException.class);
        verifyNoInteractions(produtos);
    }

    @Test
    void codigoDuplicadoNaLojaNaoGravaProduto() {
        permitirAcesso();
        when(produtos.existsByLoja_IdAndCodigoInterno(2L, "A")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarProdutos(request(null), 2L, "usuario"))
                .isInstanceOf(ProdutoJaCadastradoException.class).hasMessageContaining("Código interno");
        verify(produtos, never()).save(any());
    }

    @Test
    void gtinDuplicadoNaLojaNaoGravaProduto() {
        permitirAcesso();
        when(produtos.existsByLoja_IdAndGtin(2L, "12345678")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarProdutos(request(" 12345678 "), 2L, "usuario"))
                .isInstanceOf(ProdutoJaCadastradoException.class).hasMessageContaining("GTIN");
        verify(produtos, never()).save(any());
    }

    @Test
    void criaProdutoAtivoNaLojaAutorizadaERetornaDadosPersistidos() {
        Loja loja = permitirAcesso();
        when(produtos.save(any(Produtos.class))).thenAnswer(invocation -> {
            Produtos produto = invocation.getArgument(0);
            assertThat(produto.getLoja()).isSameAs(loja);
            produto.setId(9L);
            return produto;
        });
        ProdutosRequest request = request(" 12345678 ");

        var response = service.cadastrarProdutos(request, 2L, "usuario");

        assertThat(response.id()).isEqualTo(9L);
        assertThat(response.lojaId()).isEqualTo(2L);
        assertThat(response.ativo()).isTrue();
        assertThat(response).usingRecursiveComparison().comparingOnlyFields(
                "codigoInterno", "descricao", "gtin", "unidade", "ncm", "cest").isEqualTo(request);
        verify(produtos).existsByLoja_IdAndCodigoInterno(2L, "A");
        verify(produtos).existsByLoja_IdAndGtin(2L, "12345678");
    }

    @Test
    void gtinAusenteNaoConsultaDuplicidadeDeGtin() {
        permitirAcesso();
        when(produtos.save(any(Produtos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.cadastrarProdutos(request("  "), 2L, "usuario");

        assertThat(response.gtin()).isNull();
        verify(produtos, never()).existsByLoja_IdAndGtin(any(), any());
        verify(produtos).save(any(Produtos.class));
    }

    private Loja permitirAcesso() {
        Loja loja = Loja.builder().id(2L).build();
        when(acesso.buscarLojaAutorizada("usuario", 2L)).thenReturn(loja);
        return loja;
    }

    private ProdutosRequest request(String gtin) {
        return new ProdutosRequest(" A ", "Produto", gtin, UnidadeComercial.UN, "12345678", "1234567");
    }
}
