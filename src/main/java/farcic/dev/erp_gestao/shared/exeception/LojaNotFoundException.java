package farcic.dev.erp_gestao.shared.exeception;

public class LojaNotFoundException extends RuntimeException {
    public LojaNotFoundException(String message) {
        super(message);
    }
}
