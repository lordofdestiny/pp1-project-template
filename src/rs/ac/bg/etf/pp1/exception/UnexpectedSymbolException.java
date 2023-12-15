package rs.ac.bg.etf.pp1.exception;

public class UnexpectedSymbolException extends Exception {

    public UnexpectedSymbolException() {
        super("Unexpected symbol");
    }

    public UnexpectedSymbolException(String message) {
        super(message);
    }
}
