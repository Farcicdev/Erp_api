package farcic.dev.erp_gestao.empresa.service;

import farcic.dev.erp_gestao.empresa.repository.RevendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final RevendaRepository revendaRepository;

}
