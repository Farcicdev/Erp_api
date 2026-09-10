package farcic.dev.erp_gestao.shared.exeception;

public class RevendaNotFoundException extends RuntimeException {
    public RevendaNotFoundException(String message) {
        super(message);
    }
}
