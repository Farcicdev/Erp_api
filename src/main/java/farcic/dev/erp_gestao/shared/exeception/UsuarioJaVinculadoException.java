package farcic.dev.erp_gestao.shared.exeception;

public class UsuarioJaVinculadoException extends RuntimeException {
    public UsuarioJaVinculadoException(String message) {
        super(message);
    }
}
