package rs.ac.bg.etf.pp1.exception;

public class ParseException extends Exception {
    public ParseException() {
        super("Input program had syntax errors.");
    }
}