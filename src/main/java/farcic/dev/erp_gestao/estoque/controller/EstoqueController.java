package farcic.dev.erp_gestao.estoque.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lojas/{lojaId}/estoques")
@RequiredArgsConstructor
public class EstoqueController {
}
