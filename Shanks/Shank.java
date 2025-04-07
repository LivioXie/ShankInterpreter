package Shanks;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Shank {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Error: Please provide exactly one file name as an argument.");
            System.exit(1);
        }
        
        String filename = args[0];
        
        try {
            Path filePath = Paths.get(filename);
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            
            Lexer lexer = new Lexer();
            
            for (String line : lines) {
                try {
                    lexer.lex(line);
                } catch (SyntaxErrorException e) {
                    System.out.println(e);
                    System.exit(1);
                }
            }
            
            // Finish lexing to add any remaining DEDENT tokens
            lexer.finishLexing();
            
            // Print all tokens
            for (Token token : lexer.getTokens()) {
                System.out.println(token);
            }
            
            // Create a parser and parse the tokens
            Parser parser = new Parser(lexer.getTokens());
            try {
                ProgramNode program = parser.parse();
                System.out.println(program);
                
                // Perform semantic analysis
                SemanticAnalysis semanticAnalysis = new SemanticAnalysis();
                try {
                    semanticAnalysis.analyze(program);
                    System.out.println("Semantic analysis completed successfully.");
                } catch (RuntimeException e) {
                    System.out.println("Semantic error: " + e.getMessage());
                    System.exit(1);
                }
                
                // Create an interpreter and add built-in functions
                Interpreter interpreter = new Interpreter(program);
                
                // Print available functions
                System.out.println("\nAvailable functions:");
                for (String functionName : interpreter.getFunctions().keySet()) {
                    System.out.println("- " + functionName);
                }
                
                // Interpret the program
                interpreter.interpret();
                
            } catch (SyntaxErrorException e) {
                System.out.println("Parsing error: " + e.getMessage());
                System.exit(1);
            } catch (RuntimeException e) {
                System.out.println("Runtime error during parsing: " + e.getMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }
}