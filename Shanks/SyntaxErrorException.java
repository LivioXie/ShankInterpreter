package Shanks;

public class SyntaxErrorException extends Exception {
    private Token token;
    private int lineNumber;

    public SyntaxErrorException(String message, Token token, int lineNumber) {
        super(message);
        this.token = token;
        this.lineNumber = lineNumber;
    }

    public SyntaxErrorException(String message, int lineNumber) {
        super(message);
        this.token = null;
        this.lineNumber = lineNumber;
    }

    public Token getToken() {
        return token;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        if (token != null) {
            return "Syntax Error at line " + lineNumber + ": " + getMessage() + " - Token: " + token;
        } else {
            return "Syntax Error at line " + lineNumber + ": " + getMessage();
        }
    }
}