package farcic.dev.erp_gestao.produto.service;

import farcic.dev.erp_gestao.produto.dto.respose.ProdutosResponse;
import farcic.dev.erp_gestao.produto.mapper.ProdutosMapper;
import farcic.dev.erp_gestao.produto.repository.ProdutosRepository;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListarProdutosService {

    private final ProdutosRepository produtosRepository;
    private final AcessoLojaService acessoLojaService;
    private final ProdutosMapper produtosMapper;

    @Transactional(readOnly = true)
    public Page<ProdutosResponse> listarProdutos(Pageable pageable, Long lojaId, String keycloakSub){
        acessoLojaService.buscarLojaAutorizada(keycloakSub, lojaId);

        return produtosRepository.findAllByLoja_Id(lojaId, pageable)
                .map(produtosMapper::toResponse);
    }

}
