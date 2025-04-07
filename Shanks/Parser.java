package Shanks;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int currentPosition;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentPosition = 0;
    }
    
    // Helper method to match and remove a token of a specific type
    private Token matchAndRemove(Token.TokenType expectedType) {
        if (currentPosition >= tokens.size()) {
            return null;
        }
        
        Token currentToken = tokens.get(currentPosition);
        if (currentToken.getType() == expectedType) {
            currentPosition++;
            return currentToken;
        }
        
        return null;
    }
    
    // Helper method to expect and remove one or more end of line tokens
    private void expectEndsOfLine() throws SyntaxErrorException {
        Token eol = matchAndRemove(Token.TokenType.ENDOFLINE);
        if (eol == null) {
            throw new SyntaxErrorException("Expected end of line", 
                currentPosition < tokens.size() ? tokens.get(currentPosition).getLineNumber() : -1);
        }
        
        // Consume any additional end of line tokens
        while (matchAndRemove(Token.TokenType.ENDOFLINE) != null) {
            // Just keep consuming
        }
    }
    
    // Helper method to peek ahead in the token stream
    private Token peek(int ahead) {
        int position = currentPosition + ahead;
        if (position >= 0 && position < tokens.size()) {
            return tokens.get(position);
        }
        return null;
    }
    
    // Main parse method - now returns a ProgramNode
    public ProgramNode parse() throws SyntaxErrorException {
        ProgramNode program = new ProgramNode();
        
        while (currentPosition < tokens.size()) {
            FunctionNode functionNode = function();
            if (functionNode != null) {
                program.addFunction(functionNode);
            } else {
                // If we can't parse a function but there are still tokens,
                // try to consume an end of line and continue
                if (matchAndRemove(Token.TokenType.ENDOFLINE) == null) {
                    // If it's not an end of line, we have a syntax error
                    if (currentPosition < tokens.size()) {
                        throw new SyntaxErrorException("Unexpected token: " + tokens.get(currentPosition).getType(),
                            tokens.get(currentPosition).getLineNumber());
                    }
                    break;
                }
            }
        }
        
        return program;
    }
    
    // Parse a function definition
    private FunctionNode function() throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Check for 'define' keyword
        if (matchAndRemove(Token.TokenType.DEFINE) == null) {
            // Not a function definition
            currentPosition = startPosition;
            return null;
        }
        
        // Get function name
        Token nameToken = matchAndRemove(Token.TokenType.IDENTIFIER);
        if (nameToken == null) {
            throw new SyntaxErrorException("Expected function name after 'define'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Create function node
        FunctionNode function = new FunctionNode(nameToken.getValue());
        
        // Check for left parenthesis
        if (matchAndRemove(Token.TokenType.LEFTPAREN) == null) {
            throw new SyntaxErrorException("Expected '(' after function name",
                                         nameToken.getLineNumber());
        }
        
        // Parse parameters
        List<VariableNode> parameters = parameterDeclarations();
        for (VariableNode param : parameters) {
            function.addParameter(param);
        }
        
        // Check for right parenthesis
        if (matchAndRemove(Token.TokenType.RIGHTPAREN) == null) {
            throw new SyntaxErrorException("Expected ')' after parameters",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Expect end of line
        expectEndsOfLine();
        
        // Parse constants and variables
        boolean foundConstantsOrVariables = true;
        while (foundConstantsOrVariables) {
            int posBeforeCheck = currentPosition;
            
            // Try to parse constants
            if (parseConstants(function)) {
                continue;
            }
            
            // Try to parse variables
            if (parseVariables(function)) {
                continue;
            }
            
            // If we didn't find constants or variables, break the loop
            if (currentPosition == posBeforeCheck) {
                foundConstantsOrVariables = false;
            }
        }
        
        // Check for INDENT
        if (matchAndRemove(Token.TokenType.INDENT) == null) {
            throw new SyntaxErrorException("Expected indented block for function body",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Parse expressions (temporary, just to make sure indentation works)
        while (true) {
            Node expressionNode = expression();
            if (expressionNode != null) {
                System.out.println("Expression in function " + nameToken.getValue() + ": " + expressionNode);
                expectEndsOfLine();
            } else {
                break;
            }
        }
        
        // Check for DEDENT
        if (matchAndRemove(Token.TokenType.DEDENT) == null) {
            throw new SyntaxErrorException("Expected dedent at end of function body",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        return function;
    }

     // Parse statements block
     private List<StatementNode> statements() throws SyntaxErrorException {
        // Check for INDENT
        if (matchAndRemove(Token.TokenType.INDENT) == null) {
            return null; // No statements block
        }
        
        List<StatementNode> statementsList = new ArrayList<>();
        
        // Parse statements until we find a DEDENT
        while (true) {
            // Check for DEDENT (end of statements block)
            if (matchAndRemove(Token.TokenType.DEDENT) != null) {
                break;
            }
            
            // Try to parse a statement
            StatementNode statement = statement();
            if (statement != null) {
                statementsList.add(statement);
                expectEndsOfLine(); // Expect end of line after each statement
            } else {
                // If we can't parse a statement but haven't found a DEDENT, that's an error
                throw new SyntaxErrorException("Expected statement or dedent",
                                             currentPosition < tokens.size() ? tokens.get(currentPosition).getLineNumber() : -1);
            }
        }
        
        return statementsList;
    }
    
    // Parse a single statement
    private StatementNode statement() throws SyntaxErrorException {
        // Try to parse each type of statement
        StatementNode result;
        
        // Try if statement
        result = parseIf();
        if (result != null) return result;
        
        // Try while statement
        result = parseWhile();
        if (result != null) return result;
        
        // Try repeat statement
        result = parseRepeat();
        if (result != null) return result;
        
        // Try for statement
        result = parseFor();
        if (result != null) return result;
        
        // Try function call
        result = parseFunctionCall();
        if (result != null) return result;
        
        // Try assignment statement
        result = assignment();
        if (result != null) return result;
        
        // No statement found
        return null;
    }

    // Parse an if statement
    private IfNode parseIf() throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Check for 'if' keyword
        if (matchAndRemove(Token.TokenType.IF) == null) {
            // Not an if statement
            currentPosition = startPosition;
            return null;
        }
        
        // Parse condition
        Node condition = boolCompare();
        if (condition == null) {
            throw new SyntaxErrorException("Expected condition after 'if'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Check for 'then' keyword
        if (matchAndRemove(Token.TokenType.THEN) == null) {
            throw new SyntaxErrorException("Expected 'then' after condition",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        expectEndsOfLine();
        
        // Parse statements
        List<StatementNode> ifStatements = statements();
        if (ifStatements == null) {
            throw new SyntaxErrorException("Expected statements after 'then'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Create if node
        IfNode ifNode = new IfNode(condition, ifStatements);
        
        // Check for 'elsif' or 'else'
        IfNode currentIf = ifNode;
        
        while (true) {
            // Check for 'elsif'
            if (matchAndRemove(Token.TokenType.ELSIF) != null) {
                // Parse condition
                Node elsifCondition = boolCompare();
                if (elsifCondition == null) {
                    throw new SyntaxErrorException("Expected condition after 'elsif'",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
                
                // Check for 'then' keyword
                if (matchAndRemove(Token.TokenType.THEN) == null) {
                    throw new SyntaxErrorException("Expected 'then' after condition",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
                
                expectEndsOfLine();
                
                // Parse statements
                List<StatementNode> elsifStatements = statements();
                if (elsifStatements == null) {
                    throw new SyntaxErrorException("Expected statements after 'then'",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
                
                // Create elsif node and link it
                IfNode elsifNode = new IfNode(elsifCondition, elsifStatements);
                currentIf.setNextIf(elsifNode);
                currentIf = elsifNode;
                
                continue;
            }
            
            // Check for 'else'
            if (matchAndRemove(Token.TokenType.ELSE) != null) {
                expectEndsOfLine();
                
                // Parse statements
                List<StatementNode> elseStatements = statements();
                if (elseStatements == null) {
                    throw new SyntaxErrorException("Expected statements after 'else'",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
                
                // Create else node and link it
                IfNode elseNode = new IfNode(elseStatements);
                currentIf.setNextIf(elseNode);
            }
            
            break;
        }
        
        return ifNode;
    }
    
    // Parse a while statement
    private WhileNode parseWhile() throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Check for 'while' keyword
        if (matchAndRemove(Token.TokenType.WHILE) == null) {
            // Not a while statement
            currentPosition = startPosition;
            return null;
        }
        
        // Parse condition
        Node condition = boolCompare();
        if (condition == null) {
            throw new SyntaxErrorException("Expected condition after 'while'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        expectEndsOfLine();
        
        // Parse statements
        List<StatementNode> whileStatements = statements();
        if (whileStatements == null) {
            throw new SyntaxErrorException("Expected statements in while loop",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        return new WhileNode(condition, whileStatements);
    }
    
    // Parse a repeat statement
    private RepeatNode parseRepeat() throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Check for 'repeat' keyword
        if (matchAndRemove(Token.TokenType.REPEAT) == null) {
            // Not a repeat statement
            currentPosition = startPosition;
            return null;
        }
        
        expectEndsOfLine();
        
        // Parse statements
        List<StatementNode> repeatStatements = statements();
        if (repeatStatements == null) {
            throw new SyntaxErrorException("Expected statements in repeat loop",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Check for 'until' keyword
        if (matchAndRemove(Token.TokenType.UNTIL) == null) {
            throw new SyntaxErrorException("Expected 'until' after repeat statements",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Parse condition
        Node condition = boolCompare();
        if (condition == null) {
            throw new SyntaxErrorException("Expected condition after 'until'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        expectEndsOfLine();
        
        return new RepeatNode(condition, repeatStatements);
    }
    
    // Parse a for statement
    private ForNode parseFor() throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Check for 'for' keyword
        if (matchAndRemove(Token.TokenType.FOR) == null) {
            // Not a for statement
            currentPosition = startPosition;
            return null;
        }
        
        // Parse variable
        VariableReferenceNode variable = variableReference();
        if (variable == null) {
            throw new SyntaxErrorException("Expected variable after 'for'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Check for 'from' keyword
        if (matchAndRemove(Token.TokenType.FROM) == null) {
            throw new SyntaxErrorException("Expected 'from' after variable",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Parse from expression
        Node fromExpr = boolCompare();
        if (fromExpr == null) {
            throw new SyntaxErrorException("Expected expression after 'from'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Check for 'to' keyword
        if (matchAndRemove(Token.TokenType.TO) == null) {
            throw new SyntaxErrorException("Expected 'to' after from expression",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Parse to expression
        Node toExpr = boolCompare();
        if (toExpr == null) {
            throw new SyntaxErrorException("Expected expression after 'to'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        expectEndsOfLine();
        
        // Parse statements
        List<StatementNode> forStatements = statements();
        if (forStatements == null) {
            throw new SyntaxErrorException("Expected statements in for loop",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        return new ForNode(variable, fromExpr, toExpr, forStatements);
    }
    
    // Parse a function call
    private FunctionCallNode parseFunctionCall() throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Get function name
        Token nameToken = matchAndRemove(Token.TokenType.IDENTIFIER);
        if (nameToken == null) {
            // Not a function call
            currentPosition = startPosition;
            return null;
        }
        
        // Check for left parenthesis
        if (matchAndRemove(Token.TokenType.LEFTPAREN) == null) {
            // Not a function call
            currentPosition = startPosition;
            return null;
        }
        
        // Create function call node
        FunctionCallNode functionCall = new FunctionCallNode(nameToken.getValue());
        
        // Check if there are any parameters
        if (matchAndRemove(Token.TokenType.RIGHTPAREN) == null) {
            // Parse parameters
            boolean moreParameters = true;
            while (moreParameters) {
                // Check for 'var' keyword
                boolean isVar = false;
                if (matchAndRemove(Token.TokenType.VAR) != null) {
                    isVar = true;
                }
                
                if (isVar) {
                    // Parse variable reference
                    VariableReferenceNode varRef = variableReference();
                    if (varRef == null) {
                        throw new SyntaxErrorException("Expected variable reference after 'var'",
                                                     currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                    }
                    
                    // Add parameter
                    functionCall.addParameter(new ParameterNode(varRef));
                } else {
                    // Parse expression
                    Node expr = boolCompare();
                    if (expr == null) {
                        throw new SyntaxErrorException("Expected expression for parameter",
                                                     currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                    }
                    
                    // Add parameter
                    functionCall.addParameter(new ParameterNode(expr));
                }
                
                // Check for comma (more parameters) or right parenthesis (end of parameters)
                if (matchAndRemove(Token.TokenType.COMMA) != null) {
                    // More parameters
                } else if (matchAndRemove(Token.TokenType.RIGHTPAREN) != null) {
                    // End of parameters
                    moreParameters = false;
                } else {
                    throw new SyntaxErrorException("Expected ',' or ')' after parameter",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
            }
        }
        
        return functionCall;
    }
    
    // Parse an assignment statement
    private AssignmentNode assignment() throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Try to parse a variable reference
        VariableReferenceNode target = variableReference();
        if (target == null) {
            // Not an assignment statement
            currentPosition = startPosition;
            return null;
        }
        
        // Check for assignment operator
        if (matchAndRemove(Token.TokenType.ASSIGNMENT) == null) {
            // Not an assignment statement
            currentPosition = startPosition;
            return null;
        }
        
        // Parse the right side (value)
        Node value = boolCompare();
        if (value == null) {
            throw new SyntaxErrorException("Expected expression after ':='", 
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        return new AssignmentNode(target, value);
    }
    
    // Parse a variable reference (identifier with optional array index)
    private VariableReferenceNode variableReference() throws SyntaxErrorException {
        // Get the variable name
        Token nameToken = matchAndRemove(Token.TokenType.IDENTIFIER);
        if (nameToken == null) {
            return null;
        }
        
        // Check for array index
        if (matchAndRemove(Token.TokenType.LEFTBRACKET) != null) {
            // Parse the index expression
            Node indexExpression = expression();
            if (indexExpression == null) {
                throw new SyntaxErrorException("Expected expression for array index", 
                                             nameToken.getLineNumber());
            }
            
            // Check for closing bracket
            if (matchAndRemove(Token.TokenType.RIGHTBRACKET) == null) {
                throw new SyntaxErrorException("Expected ']' after array index", 
                                             nameToken.getLineNumber());
            }
            
            return new VariableReferenceNode(nameToken.getValue(), indexExpression);
        }
        
        // Simple variable reference (no array index)
        return new VariableReferenceNode(nameToken.getValue());
    }
    
    // Parse a boolean comparison
    private Node boolCompare() throws SyntaxErrorException {
        // Parse the left side expression
        Node leftSide = expression();
        if (leftSide == null) {
            return null;
        }
        
        // Check for comparison operator
        Token.TokenType tokenType = null;
        BooleanCompareNode.ComparisonOperator operator = null;
        
        if (matchAndRemove(Token.TokenType.LESSTHAN) != null) {
            operator = BooleanCompareNode.ComparisonOperator.LESS_THAN;
        } else if (matchAndRemove(Token.TokenType.GREATERTHAN) != null) {
            operator = BooleanCompareNode.ComparisonOperator.GREATER_THAN;
        } else if (matchAndRemove(Token.TokenType.LESSTHANEQUAL) != null) {
            operator = BooleanCompareNode.ComparisonOperator.LESS_EQUAL;
        } else if (matchAndRemove(Token.TokenType.GREATERTHANEQUAL) != null) {
            operator = BooleanCompareNode.ComparisonOperator.GREATER_EQUAL;
        } else if (matchAndRemove(Token.TokenType.EQUALS) != null) {
            operator = BooleanCompareNode.ComparisonOperator.EQUAL;
        } else if (matchAndRemove(Token.TokenType.NOTEQUALS) != null) {
            operator = BooleanCompareNode.ComparisonOperator.NOT_EQUAL;
        }
        
        // If no comparison operator, just return the expression
        if (operator == null) {
            return leftSide;
        }
        
        // Parse the right side expression
        Node rightSide = expression();
        if (rightSide == null) {
            throw new SyntaxErrorException("Expected expression after comparison operator",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        return new BooleanCompareNode(operator, leftSide, rightSide);
    }
    
    // Parse an expression (term +/- term)
    private Node expression() throws SyntaxErrorException {
        Node left = term();
        if (left == null) {
            return null;
        }
        
        while (true) {
            MathOpNode.Operation operation = null;
            
            if (matchAndRemove(Token.TokenType.PLUS) != null) {
                operation = MathOpNode.Operation.ADD;
            } else if (matchAndRemove(Token.TokenType.MINUS) != null) {
                operation = MathOpNode.Operation.SUBTRACT;
            } else {
                break; // No more operations
            }
            
            Node right = term();
            if (right == null) {
                throw new SyntaxErrorException("Expected term after operator",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            left = new MathOpNode(operation, left, right);
        }
        
        return left;
    }
    
    // Parse a term (factor */ factor)
    private Node term() throws SyntaxErrorException {
        Node left = factor();
        if (left == null) {
            return null;
        }
        
        while (true) {
            MathOpNode.Operation operation = null;
            
            if (matchAndRemove(Token.TokenType.MULTIPLY) != null) {
                operation = MathOpNode.Operation.MULTIPLY;
            } else if (matchAndRemove(Token.TokenType.DIVIDE) != null) {
                operation = MathOpNode.Operation.DIVIDE;
            } else if (matchAndRemove(Token.TokenType.MOD) != null) {
                operation = MathOpNode.Operation.MOD;
            } else {
                break; // No more operations
            }
            
            Node right = factor();
            if (right == null) {
                throw new SyntaxErrorException("Expected factor after operator",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            left = new MathOpNode(operation, left, right);
        }
        
        return left;
    }
    
    // Parse a factor (number, parenthesized expression, or variable reference)
    private Node factor() throws SyntaxErrorException {
        // Check for negative sign
        boolean isNegative = matchAndRemove(Token.TokenType.MINUS) != null;
        
        // Try to match a number
        Token number = matchAndRemove(Token.TokenType.NUMBER);
        if (number != null) {
            String value = number.getValue();
            try {
                if (value.contains(".")) {
                    float floatValue = Float.parseFloat(value);
                    return new RealNode(isNegative ? -floatValue : floatValue);
                } else {
                    int intValue = Integer.parseInt(value);
                    return new IntegerNode(isNegative ? -intValue : intValue);
                }
            } catch (NumberFormatException e) {
                throw new SyntaxErrorException("Invalid number format: " + value,
                                             number.getLineNumber());
            }
        }
        
        // If not a number, try parenthesized expression
        if (matchAndRemove(Token.TokenType.LEFTPAREN) != null) {
            Node expr = boolCompare(); // Use boolCompare to allow boolean expressions in parentheses
            if (expr == null) {
                throw new SyntaxErrorException("Expected expression after '('",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            if (matchAndRemove(Token.TokenType.RIGHTPAREN) == null) {
                throw new SyntaxErrorException("Expected ')' after expression",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            // Apply negative sign if needed
            if (isNegative) {
                // Create a MathOpNode for negation (0 - expr)
                return new MathOpNode(MathOpNode.Operation.SUBTRACT, new IntegerNode(0), expr);
            }
            
            return expr;
        }
        
        // Try to match a variable reference
        int startPosition = currentPosition;
        VariableReferenceNode varRef = variableReference();
        if (varRef != null) {
            // Apply negative sign if needed
            if (isNegative) {
                // Create a MathOpNode for negation (0 - varRef)
                return new MathOpNode(MathOpNode.Operation.SUBTRACT, new IntegerNode(0), varRef);
            }
            
            return varRef;
        }
        
        // If we had a negative sign but no number, parenthesized expression, or variable reference, that's an error
        if (isNegative) {
            throw new SyntaxErrorException("Expected number, expression, or variable after '-'",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // We couldn't parse a factor
        return null;
    }
    
    // Parse parameter declarations
    private List<VariableNode> parameterDeclarations() throws SyntaxErrorException {
        List<VariableNode> parameters = new ArrayList<>();
        
        // Check if there are any parameters
        if (peek(0) != null && peek(0).getType() == Token.TokenType.RIGHTPAREN) {
            return parameters; // No parameters
        }
        
        boolean moreParameters = true;
        while (moreParameters) {
            // Check for 'var' keyword
            boolean isVar = false;
            if (matchAndRemove(Token.TokenType.VAR) != null) {
                isVar = true;
            }
            
            // Get parameter name
            Token nameToken = matchAndRemove(Token.TokenType.IDENTIFIER);
            if (nameToken == null) {
                throw new SyntaxErrorException("Expected parameter name",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            // Check for colon
            if (matchAndRemove(Token.TokenType.COLON) == null) {
                throw new SyntaxErrorException("Expected ':' after parameter name",
                                             nameToken.getLineNumber());
            }
            
            // Get parameter type
            String type = parseType();
            if (type == null) {
                throw new SyntaxErrorException("Expected type for parameter",
                                             nameToken.getLineNumber());
            }
            
            // Create parameter node
            VariableNode parameter = new VariableNode(nameToken.getValue(), type, isVar);
            parameters.add(parameter);
            
            // Check for semicolon (more parameters) or right parenthesis (end of parameters)
            if (matchAndRemove(Token.TokenType.SEMICOLON) != null) {
                // More parameters
            } else {
                moreParameters = false;
            }
        }
        
        return parameters;
    }
    
    // Parse type
    private String parseType() {
        // Check for basic types
        if (matchAndRemove(Token.TokenType.IDENTIFIER) != null) {
            // Go back one token to re-read the identifier
            currentPosition--;
            
            Token typeToken = tokens.get(currentPosition);
            String type = typeToken.getValue().toLowerCase();
            
            if (type.equals("integer") || type.equals("real") || 
                type.equals("string") || type.equals("boolean") || 
                type.equals("character")) {
                currentPosition++; // Consume the token
                return type;
            }
        }
        
        // Check for array type
        if (matchAndRemove(Token.TokenType.ARRAY) != null) {
            if (matchAndRemove(Token.TokenType.OF) != null) {
                String elementType = parseType();
                if (elementType != null) {
                    return "array of " + elementType;
                }
            }
        }
        
        return null;
    }
    
    // Parse constants
    private boolean parseConstants(FunctionNode function) throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Check for 'constants' keyword
        if (matchAndRemove(Token.TokenType.CONSTANTS) == null) {
            // Not a constants declaration
            currentPosition = startPosition;
            return false;
        }
        
        // Parse constant declarations
        boolean firstConstant = true;
        
        while (true) {
            // Parse constant name
            Token nameToken = matchAndRemove(Token.TokenType.IDENTIFIER);
            if (nameToken == null) {
                if (firstConstant) {
                    throw new SyntaxErrorException("Expected constant name",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                } else {
                    break;
                }
            }
            
            firstConstant = false;
            
            // Check for equals sign
            if (matchAndRemove(Token.TokenType.EQUALS) == null) {
                throw new SyntaxErrorException("Expected '=' after constant name",
                                             nameToken.getLineNumber());
            }
            
            // Parse constant value
            Node value = parseConstantValue();
            if (value == null) {
                throw new SyntaxErrorException("Expected constant value",
                                             nameToken.getLineNumber());
            }
            
            // Determine type from value
            String type = determineTypeFromValue(value);
            
            // Create constant node
            VariableNode constant = new VariableNode(nameToken.getValue(), type, value);
            function.addConstant(constant);
            
            // Check for comma (more constants) or end of line
            if (matchAndRemove(Token.TokenType.COMMA) != null) {
                // More constants
            } else {
                break;
            }
        }
        
        // Expect end of line
        expectEndsOfLine();
        
        return true;
    }
    
    // Parse variables
    private boolean parseVariables(FunctionNode function) throws SyntaxErrorException {
        // Save current position in case we need to backtrack
        int startPosition = currentPosition;
        
        // Check for 'variables' keyword
        if (matchAndRemove(Token.TokenType.VARIABLES) == null) {
            // Not a variables declaration
            currentPosition = startPosition;
            return false;
        }
        
        List<String> variableNames = new ArrayList<>();
        
        // Parse variable names
        boolean moreNames = true;
        while (moreNames) {
            Token nameToken = matchAndRemove(Token.TokenType.IDENTIFIER);
            if (nameToken == null) {
                throw new SyntaxErrorException("Expected variable name",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            variableNames.add(nameToken.getValue());
            
            // Check if there are more names
            if (matchAndRemove(Token.TokenType.COMMA) != null) {
                // More names
            } else {
                moreNames = false;
            }
        }
        
        // Parse type
        if (matchAndRemove(Token.TokenType.COLON) == null) {
            throw new SyntaxErrorException("Expected ':' after variable names",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        String type = parseType();
        if (type == null) {
            throw new SyntaxErrorException("Expected type for variables",
                                         currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
        }
        
        // Check for range constraints
        Integer intFrom = null;
        Integer intTo = null;
        Float realFrom = null;
        Float realTo = null;
        
        if (matchAndRemove(Token.TokenType.FROM) != null) {
            // Parse from value
            Node fromExpr = parseConstantValue();
            if (fromExpr == null) {
                throw new SyntaxErrorException("Expected constant value after 'from'",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            // Check for 'to' keyword
            if (matchAndRemove(Token.TokenType.TO) == null) {
                throw new SyntaxErrorException("Expected 'to' after from value",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            // Parse to value
            Node toExpr = parseConstantValue();
            if (toExpr == null) {
                throw new SyntaxErrorException("Expected constant value after 'to'",
                                             currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
            }
            
            // Set range values based on type
            if (type.equals("integer")) {
                if (fromExpr instanceof IntegerNode && toExpr instanceof IntegerNode) {
                    intFrom = ((IntegerNode) fromExpr).getValue();
                    intTo = ((IntegerNode) toExpr).getValue();
                } else {
                    throw new SyntaxErrorException("Range values for integer must be integers",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
            } else if (type.equals("real")) {
                if (fromExpr instanceof RealNode && toExpr instanceof RealNode) {
                    realFrom = ((RealNode) fromExpr).getValue();
                    realTo = ((RealNode) toExpr).getValue();
                } else if (fromExpr instanceof IntegerNode && toExpr instanceof IntegerNode) {
                    // Allow integer values for real ranges
                    realFrom = (float) ((IntegerNode) fromExpr).getValue();
                    realTo = (float) ((IntegerNode) toExpr).getValue();
                } else if (fromExpr instanceof IntegerNode && toExpr instanceof RealNode) {
                    realFrom = (float) ((IntegerNode) fromExpr).getValue();
                    realTo = ((RealNode) toExpr).getValue();
                } else if (fromExpr instanceof RealNode && toExpr instanceof IntegerNode) {
                    realFrom = ((RealNode) fromExpr).getValue();
                    realTo = (float) ((IntegerNode) toExpr).getValue();
                } else {
                    throw new SyntaxErrorException("Range values for real must be numbers",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
            } else if (type.equals("string")) {
                if (fromExpr instanceof IntegerNode && toExpr instanceof IntegerNode) {
                    intFrom = ((IntegerNode) fromExpr).getValue();
                    intTo = ((IntegerNode) toExpr).getValue();
                } else {
                    throw new SyntaxErrorException("Range values for string must be integers",
                                                 currentPosition > 0 ? tokens.get(currentPosition - 1).getLineNumber() : -1);
                }
            }
        }
        
        // Create variable nodes with range constraints
        for (String name : variableNames) {
            VariableNode variable = new VariableNode(name, type, true);
            
            // Set range constraints if applicable
            if (type.equals("integer") && intFrom != null && intTo != null) {
                variable.setIntegerRange(intFrom, intTo);
            } else if (type.equals("real") && realFrom != null && realTo != null) {
                variable.setRealRange(realFrom, realTo);
            } else if (type.equals("string") && intFrom != null && intTo != null) {
                variable.setStringRange(intFrom, intTo);
            }
            
            function.addVariable(variable);
        }
        
        expectEndsOfLine();
        
        return true;
    }
    
    // Parse a constant value (number, string, character, boolean)
    private Node parseConstantValue() {
        // Try to parse a number
        boolean isNegative = false;
        if (matchAndRemove(Token.TokenType.MINUS) != null) {
            isNegative = true;
        }
        
        Token number = matchAndRemove(Token.TokenType.NUMBER);
        if (number != null) {
            String value = number.getValue();
            if (value.contains(".")) {
                // It's a real number
                float realValue = Float.parseFloat(value);
                if (isNegative) {
                    realValue = -realValue;
                }
                return new RealNode(realValue);
            } else {
                // It's an integer
                int intValue = Integer.parseInt(value);
                if (isNegative) {
                    intValue = -intValue;
                }
                return new IntegerNode(intValue);
            }
        } else if (isNegative) {
            // If we had a negative sign but no number, restore position
            currentPosition--;
        }
        
        // Try to parse a string
        Token string = matchAndRemove(Token.TokenType.STRINGLITERAL);
        if (string != null) {
            return new StringNode(string.getValue());
        }
        
        // Try to parse a character
        Token character = matchAndRemove(Token.TokenType.CHARACTERLITERAL);
        if (character != null) {
            return new CharacterNode(character.getValue().charAt(0));
        }
        
        // Try to parse a boolean
        Token boolTrue = matchAndRemove(Token.TokenType.TRUE);
        if (boolTrue != null) {
            return new BooleanNode(true);
        }
        
        Token boolFalse = matchAndRemove(Token.TokenType.FALSE);
        if (boolFalse != null) {
            return new BooleanNode(false);
        }
        
        return null;
    }
    
    // Determine type from a value node
    private String determineTypeFromValue(Node value) {
        if (value instanceof IntegerNode) {
            return "integer";
        } else if (value instanceof RealNode) {
            return "real";
        } else if (value instanceof StringNode) {
            return "string";
        } else if (value instanceof CharacterNode) {
            return "character";
        } else if (value instanceof BooleanNode) {
            return "boolean";
        } else {
            throw new RuntimeException("Unknown value type: " + value.getClass().getName());
        }
    }
    
}