package Shanks;

public class Token {
    public enum TokenType {
        // Original types (renamed WORD to IDENTIFIER)
        IDENTIFIER,
        NUMBER,
        ENDOFLINE,
        
        // Keywords
        DEFINE,
        VARIABLES,
        CONSTANTS,
        IF,
        THEN,
        ELSIF,
        ELSE,
        FOR,
        FROM,
        TO,
        WHILE,
        REPEAT,
        UNTIL,
        MOD,
        VAR,
        TRUE,
        FALSE,
        ARRAY,
        OF,
        
        // Punctuation
        SEMICOLON,
        COLON,
        COMMA,
        LEFTPAREN,
        RIGHTPAREN,
        LEFTBRACKET,
        RIGHTBRACKET,
        ASSIGNMENT, // :=
        EQUALS,     // =
        NOTEQUALS,  // <>
        LESSTHAN,   // <
        GREATERTHAN, // >
        LESSTHANEQUAL, // <=
        GREATERTHANEQUAL, // >=
        PLUS,       // +
        MINUS,      // -
        MULTIPLY,   // *
        DIVIDE,     // /
        
        // Literals
        STRINGLITERAL,
        CHARACTERLITERAL,
        
        // Indentation
        INDENT,
        DEDENT
    }
    
    private TokenType type;
    private String value;
    private int lineNumber;
    
    public Token(TokenType type, String value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }
    
    public Token(TokenType type, int lineNumber) {
        this.type = type;
        this.value = "";
        this.lineNumber = lineNumber;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public String toString() {
        if (value != null && !value.isEmpty()) {
            return type + " (" + value + ") at line " + lineNumber;
        } else {
            return type + " at line " + lineNumber;
        }
    }
}