package Shanks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private enum State {
        START,
        IN_IDENTIFIER,
        IN_NUMBER,
        IN_DECIMAL_NUMBER,
        IN_STRING,
        IN_CHARACTER,
        IN_COMMENT,
        IN_ASSIGNMENT, // For :=
        IN_LESSTHAN,   // For < or <=
        IN_GREATERTHAN, // For > or >=
        IN_NOTEQUAL    // For <>
    }
    
    private List<Token> tokens;
    private Map<String, Token.TokenType> keywords;
    private int currentLineNumber;
    private int currentIndentLevel;
    private int previousIndentLevel;
    private boolean inMultilineComment;
    
    public Lexer() {
        tokens = new ArrayList<>();
        keywords = new HashMap<>();
        currentLineNumber = 1;
        currentIndentLevel = 0;
        previousIndentLevel = 0;
        inMultilineComment = false;
        
        initializeKeywords();
    }
    
    private void initializeKeywords() {
        // Initialize all keywords
        keywords.put("define", Token.TokenType.DEFINE);
        keywords.put("variables", Token.TokenType.VARIABLES);
        keywords.put("constants", Token.TokenType.CONSTANTS);
        keywords.put("if", Token.TokenType.IF);
        keywords.put("then", Token.TokenType.THEN);
        keywords.put("elsif", Token.TokenType.ELSIF);
        keywords.put("else", Token.TokenType.ELSE);
        keywords.put("for", Token.TokenType.FOR);
        keywords.put("from", Token.TokenType.FROM);
        keywords.put("to", Token.TokenType.TO);
        keywords.put("while", Token.TokenType.WHILE);
        keywords.put("repeat", Token.TokenType.REPEAT);
        keywords.put("until", Token.TokenType.UNTIL);
        keywords.put("mod", Token.TokenType.MOD);
        keywords.put("var", Token.TokenType.VAR);
        keywords.put("true", Token.TokenType.TRUE);
        keywords.put("false", Token.TokenType.FALSE);
        keywords.put("array", Token.TokenType.ARRAY);
        keywords.put("of", Token.TokenType.OF);
    }
    
    public void lex(String line) throws SyntaxErrorException {
        // Skip processing if we're in a multiline comment
        if (inMultilineComment) {
            processCommentLine(line);
            currentLineNumber++;
            return;
        }
        
        // Process indentation
        if (!line.trim().isEmpty()) {
            processIndentation(line);
        }
        
        State currentState = State.START;
        StringBuilder currentToken = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            char nextChar = (i + 1 < line.length()) ? line.charAt(i + 1) : '\0';
            
            switch (currentState) {
                case START:
                    if (Character.isLetter(c)) {
                        currentToken.append(c);
                        currentState = State.IN_IDENTIFIER;
                    } else if (Character.isDigit(c)) {
                        currentToken.append(c);
                        currentState = State.IN_NUMBER;
                    } else if (c == '"') {
                        currentState = State.IN_STRING;
                    } else if (c == '\'') {
                        currentState = State.IN_CHARACTER;
                    } else if (c == '{') {
                        currentState = State.IN_COMMENT;
                        inMultilineComment = true;
                    } else if (c == ':') {
                        if (nextChar == '=') {
                            tokens.add(new Token(Token.TokenType.ASSIGNMENT, currentLineNumber));
                            i++; // Skip the next character
                        } else {
                            tokens.add(new Token(Token.TokenType.COLON, currentLineNumber));
                        }
                    } else if (c == '<') {
                        if (nextChar == '>') {
                            tokens.add(new Token(Token.TokenType.NOTEQUALS, currentLineNumber));
                            i++; // Skip the next character
                        } else if (nextChar == '=') {
                            tokens.add(new Token(Token.TokenType.LESSTHANEQUAL, currentLineNumber));
                            i++; // Skip the next character
                        } else {
                            tokens.add(new Token(Token.TokenType.LESSTHAN, currentLineNumber));
                        }
                    } else if (c == '>') {
                        if (nextChar == '=') {
                            tokens.add(new Token(Token.TokenType.GREATERTHANEQUAL, currentLineNumber));
                            i++; // Skip the next character
                        } else {
                            tokens.add(new Token(Token.TokenType.GREATERTHAN, currentLineNumber));
                        }
                    } else if (c == '=') {
                        tokens.add(new Token(Token.TokenType.EQUALS, currentLineNumber));
                    } else if (c == ';') {
                        tokens.add(new Token(Token.TokenType.SEMICOLON, currentLineNumber));
                    } else if (c == ',') {
                        tokens.add(new Token(Token.TokenType.COMMA, currentLineNumber));
                    } else if (c == '(') {
                        tokens.add(new Token(Token.TokenType.LEFTPAREN, currentLineNumber));
                    } else if (c == ')') {
                        tokens.add(new Token(Token.TokenType.RIGHTPAREN, currentLineNumber));
                    } else if (c == '[') {
                        tokens.add(new Token(Token.TokenType.LEFTBRACKET, currentLineNumber));
                    } else if (c == ']') {
                        tokens.add(new Token(Token.TokenType.RIGHTBRACKET, currentLineNumber));
                    } else if (c == '+') {
                        tokens.add(new Token(Token.TokenType.PLUS, currentLineNumber));
                    } else if (c == '-') {
                        tokens.add(new Token(Token.TokenType.MINUS, currentLineNumber));
                    } else if (c == '*') {
                        tokens.add(new Token(Token.TokenType.MULTIPLY, currentLineNumber));
                    } else if (c == '/') {
                        tokens.add(new Token(Token.TokenType.DIVIDE, currentLineNumber));
                    } else if (!Character.isWhitespace(c)) {
                        throw new SyntaxErrorException("Unexpected character: " + c, currentLineNumber);
                    }
                    break;
                    
                case IN_IDENTIFIER:
                    if (Character.isLetterOrDigit(c)) {
                        currentToken.append(c);
                    } else {
                        // Check if it's a keyword
                        String word = currentToken.toString().toLowerCase();
                        if (keywords.containsKey(word)) {
                            tokens.add(new Token(keywords.get(word), currentLineNumber));
                        } else {
                            tokens.add(new Token(Token.TokenType.IDENTIFIER, currentToken.toString(), currentLineNumber));
                        }
                        currentToken = new StringBuilder();
                        currentState = State.START;
                        
                        // Process the current character again
                        i--;
                    }
                    break;
                    
                case IN_NUMBER:
                    if (Character.isDigit(c)) {
                        currentToken.append(c);
                    } else if (c == '.') {
                        currentToken.append(c);
                        currentState = State.IN_DECIMAL_NUMBER;
                    } else {
                        tokens.add(new Token(Token.TokenType.NUMBER, currentToken.toString(), currentLineNumber));
                        currentToken = new StringBuilder();
                        currentState = State.START;
                        
                        // Process the current character again
                        i--;
                    }
                    break;
                    
                case IN_DECIMAL_NUMBER:
                    if (Character.isDigit(c)) {
                        currentToken.append(c);
                    } else {
                        tokens.add(new Token(Token.TokenType.NUMBER, currentToken.toString(), currentLineNumber));
                        currentToken = new StringBuilder();
                        currentState = State.START;
                        
                        // Process the current character again
                        i--;
                    }
                    break;
                    
                case IN_STRING:
                    if (c == '"') {
                        tokens.add(new Token(Token.TokenType.STRINGLITERAL, currentToken.toString(), currentLineNumber));
                        currentToken = new StringBuilder();
                        currentState = State.START;
                    } else {
                        currentToken.append(c);
                    }
                    break;
                    
                case IN_CHARACTER:
                    if (c == '\'') {
                        if (currentToken.length() != 1) {
                            throw new SyntaxErrorException("Character literal must contain exactly one character", currentLineNumber);
                        }
                        tokens.add(new Token(Token.TokenType.CHARACTERLITERAL, currentToken.toString(), currentLineNumber));
                        currentToken = new StringBuilder();
                        currentState = State.START;
                    } else {
                        currentToken.append(c);
                    }
                    break;
                    
                case IN_COMMENT:
                    if (c == '}') {
                        inMultilineComment = false;
                        currentState = State.START;
                    }
                    break;
            }
        }
        
        // Handle any remaining token at the end of the line
        if (currentToken.length() > 0) {
            switch (currentState) {
                case IN_IDENTIFIER:
                    // Check if it's a keyword
                    String word = currentToken.toString().toLowerCase();
                    if (keywords.containsKey(word)) {
                        tokens.add(new Token(keywords.get(word), currentLineNumber));
                    } else {
                        tokens.add(new Token(Token.TokenType.IDENTIFIER, currentToken.toString(), currentLineNumber));
                    }
                    break;
                case IN_NUMBER:
                case IN_DECIMAL_NUMBER:
                    tokens.add(new Token(Token.TokenType.NUMBER, currentToken.toString(), currentLineNumber));
                    break;
                case IN_STRING:
                    throw new SyntaxErrorException("Unterminated string literal", currentLineNumber);
                case IN_CHARACTER:
                    throw new SyntaxErrorException("Unterminated character literal", currentLineNumber);
            }
        }
        
        // Add ENDOFLINE token
        tokens.add(new Token(Token.TokenType.ENDOFLINE, currentLineNumber));
        
        // Update line number for next call
        currentLineNumber++;
        
        // Update previous indent level
        if (!line.trim().isEmpty() && !inMultilineComment) {
            previousIndentLevel = currentIndentLevel;
        }
    }
    
    private void processCommentLine(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '}') {
                inMultilineComment = false;
                
                // Process the rest of the line if there's anything after the comment
                if (i + 1 < line.length()) {
                    try {
                        lex(line.substring(i + 1));
                    } catch (SyntaxErrorException e) {
                        // Re-throw with updated line number
                        throw new RuntimeException(e);
                    }
                }
                return;
            }
        }
    }
    
    private void processIndentation(String line) {
        int spaces = 0;
        
        // Count leading spaces and tabs
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ' ') {
                spaces++;
            } else if (c == '\t') {
                spaces += 4; // Each tab counts as 4 spaces
            } else {
                break;
            }
        }
        
        // Calculate indent level (each 4 spaces is one level)
        currentIndentLevel = spaces / 4;
        
        // Add INDENT or DEDENT tokens as needed
        if (currentIndentLevel > previousIndentLevel) {
            int indentsToAdd = currentIndentLevel - previousIndentLevel;
            for (int i = 0; i < indentsToAdd; i++) {
                tokens.add(new Token(Token.TokenType.INDENT, currentLineNumber));
            }
        } else if (currentIndentLevel < previousIndentLevel) {
            int dedentsToAdd = previousIndentLevel - currentIndentLevel;
            for (int i = 0; i < dedentsToAdd; i++) {
                tokens.add(new Token(Token.TokenType.DEDENT, currentLineNumber));
            }
        }
    }
    
    public void finishLexing() {
        // Add any remaining DEDENT tokens to get back to level 0
        for (int i = 0; i < currentIndentLevel; i++) {
            tokens.add(new Token(Token.TokenType.DEDENT, currentLineNumber));
        }
    }
    
    public List<Token> getTokens() {
        return tokens;
    }
}