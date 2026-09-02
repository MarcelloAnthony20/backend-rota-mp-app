package br.mp.mpms.exemploservico.infra.exception;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message);
    }
}
