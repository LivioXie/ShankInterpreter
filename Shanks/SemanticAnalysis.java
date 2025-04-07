package Shanks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalysis {
    
    /**
     * Performs semantic analysis on a program
     * @param program The program to analyze
     */
    public void analyze(ProgramNode program) {
        // Check assignments in all functions
        for (FunctionNode function : program.getFunctions().values()) {
            // Create a map of variable types for this function
            Map<String, String> variableTypes = new HashMap<>();
            
            // Add parameters to the variable types map
            for (VariableNode param : function.getParameters()) {
                variableTypes.put(param.getName().toLowerCase(), param.getType().toLowerCase());
            }
            
            // Add constants to the variable types map
            for (VariableNode constant : function.getConstants()) {
                variableTypes.put(constant.getName().toLowerCase(), constant.getType().toLowerCase());
            }
            
            // Add local variables to the variable types map
            for (VariableNode variable : function.getVariables()) {
                variableTypes.put(variable.getName().toLowerCase(), variable.getType().toLowerCase());
            }
            
            // Check assignments in the function's statements
            checkAssignments(function.getStatements(), variableTypes, function.getName());
        }
    }
    
    /**
     * Checks assignments in a list of statements
     * @param statements The statements to check
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     */
    private void checkAssignments(List<StatementNode> statements, Map<String, String> variableTypes, String functionName) {
        for (StatementNode statement : statements) {
            if (statement instanceof AssignmentNode) {
                checkAssignmentNode((AssignmentNode) statement, variableTypes, functionName);
            } else if (statement instanceof IfNode) {
                checkIfNode((IfNode) statement, variableTypes, functionName);
            } else if (statement instanceof WhileNode) {
                checkWhileNode((WhileNode) statement, variableTypes, functionName);
            } else if (statement instanceof RepeatNode) {
                checkRepeatNode((RepeatNode) statement, variableTypes, functionName);
            } else if (statement instanceof ForNode) {
                checkForNode((ForNode) statement, variableTypes, functionName);
            }
            // Function calls don't need type checking for assignments
        }
    }
    
    /**
     * Checks an assignment node for type consistency
     * @param node The assignment node to check
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     */
    private void checkAssignmentNode(AssignmentNode node, Map<String, String> variableTypes, String functionName) {
        String targetName = node.getTarget().getName().toLowerCase();
        String targetType = variableTypes.get(targetName);
        
        if (targetType == null) {
            throw new RuntimeException("In function '" + functionName + "': Variable '" + 
                                      targetName + "' not declared");
        }
        
        // Check the expression type
        String expressionType = getExpressionType(node.getValue(), variableTypes, functionName);
        
        // Check if types are compatible
        if (!areTypesCompatible(targetType, expressionType)) {
            throw new RuntimeException("In function '" + functionName + "': Type mismatch in assignment to '" + 
                                      targetName + "'. Expected '" + targetType + "' but got '" + 
                                      expressionType + "'");
        }
    }
    
    /**
     * Checks if node for type consistency in its condition and statements
     * @param node The if node to check
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     */
    private void checkIfNode(IfNode node, Map<String, String> variableTypes, String functionName) {
        // Check the condition
        String conditionType = getExpressionType(node.getCondition(), variableTypes, functionName);
        if (!conditionType.equals("boolean")) {
            throw new RuntimeException("In function '" + functionName + "': If condition must be boolean, but got '" + 
                                      conditionType + "'");
        }
        
        // Check the statements in the if block
        checkAssignments(node.getStatements(), variableTypes, functionName);
        
        // Check else-if and else blocks
        IfNode elseIfNode = node.getNextIf();
        while (elseIfNode != null) {
            if (elseIfNode.getCondition() != null) {
                // This is an else-if block
                String elseIfConditionType = getExpressionType(elseIfNode.getCondition(), variableTypes, functionName);
                if (!elseIfConditionType.equals("boolean")) {
                    throw new RuntimeException("In function '" + functionName + "': Else-if condition must be boolean, but got '" + 
                                              elseIfConditionType + "'");
                }
            }
            
            // Check the statements in the else-if/else block
            checkAssignments(elseIfNode.getStatements(), variableTypes, functionName);
            
            elseIfNode = elseIfNode.getNextIf();
        }
    }
    
    /**
     * Checks while node for type consistency in its condition and statements
     * @param node The while node to check
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     */
    private void checkWhileNode(WhileNode node, Map<String, String> variableTypes, String functionName) {
        // Check the condition
        String conditionType = getExpressionType(node.getCondition(), variableTypes, functionName);
        if (!conditionType.equals("boolean")) {
            throw new RuntimeException("In function '" + functionName + "': While condition must be boolean, but got '" + 
                                      conditionType + "'");
        }
        
        // Check the statements in the while block
        checkAssignments(node.getStatements(), variableTypes, functionName);
    }
    
    /**
     * Checks repeat node for type consistency in its condition and statements
     * @param node The repeat node to check
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     */
    private void checkRepeatNode(RepeatNode node, Map<String, String> variableTypes, String functionName) {
        // Check the condition
        String conditionType = getExpressionType(node.getCondition(), variableTypes, functionName);
        if (!conditionType.equals("boolean")) {
            throw new RuntimeException("In function '" + functionName + "': Repeat-until condition must be boolean, but got '" + 
                                      conditionType + "'");
        }
        
        // Check the statements in the repeat block
        checkAssignments(node.getStatements(), variableTypes, functionName);
    }
    
    /**
     * Checks for node for type consistency in its range and statements
     * @param node The for node to check
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     */
    private void checkForNode(ForNode node, Map<String, String> variableTypes, String functionName) {
        // Check the variable
        String varName = node.getVariable().getName().toLowerCase();
        String varType = variableTypes.get(varName);
        
        if (varType == null) {
            throw new RuntimeException("In function '" + functionName + "': For loop variable '" + 
                                      varName + "' not declared");
        }
        
        if (!varType.equals("integer")) {
            throw new RuntimeException("In function '" + functionName + "': For loop variable '" + 
                                      varName + "' must be integer, but is '" + varType + "'");
        }
        
        // Check the start and end expressions
        String startType = getExpressionType(node.getFromExpr(), variableTypes, functionName);
        String endType = getExpressionType(node.getToExpr(), variableTypes, functionName);
        
        if (!startType.equals("integer")) {
            throw new RuntimeException("In function '" + functionName + "': For loop start value must be integer, but got '" + 
                                      startType + "'");
        }
        
        if (!endType.equals("integer")) {
            throw new RuntimeException("In function '" + functionName + "': For loop end value must be integer, but got '" + 
                                      endType + "'");
        }
        
        // Check the statements in the for block
        checkAssignments(node.getStatements(), variableTypes, functionName);
    }
    
    /**
     * Gets the type of an expression
     * @param node The expression node
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     * @return The type of the expression
     */
    private String getExpressionType(Node node, Map<String, String> variableTypes, String functionName) {
        if (node instanceof VariableReferenceNode) {
            String varName = ((VariableReferenceNode) node).getName().toLowerCase();
            String varType = variableTypes.get(varName);
            
            if (varType == null) {
                throw new RuntimeException("In function '" + functionName + "': Variable '" + 
                                          varName + "' not declared");
            }
            
            return varType;
        } else if (node instanceof IntegerNode) {
            return "integer";
        } else if (node instanceof RealNode) {
            return "real";
        } else if (node instanceof StringNode) {
            return "string";
        } else if (node instanceof BooleanNode) {
            return "boolean";
        } else if (node instanceof CharacterNode) {
            return "character";
        } else if (node instanceof MathOpNode) {
            return getMathOpNodeType((MathOpNode) node, variableTypes, functionName);
        } else if (node instanceof BooleanCompareNode) {
            // Boolean comparisons always result in boolean
            checkBooleanCompareNode((BooleanCompareNode) node, variableTypes, functionName);
            return "boolean";
        } else if (node instanceof FunctionCallNode) {
            // For now, we'll assume function calls return void
            // In a more complete implementation, you would need to track function return types
            return "void";
        } else {
            throw new RuntimeException("In function '" + functionName + "': Unsupported expression type: " + 
                                      node.getClass().getName());
        }
    }
    
    /**
     * Gets the type of a math operation node
     * @param node The math operation node
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     * @return The type of the math operation
     */
    private String getMathOpNodeType(MathOpNode node, Map<String, String> variableTypes, String functionName) {
        String leftType = getExpressionType(node.getLeft(), variableTypes, functionName);
        String rightType = getExpressionType(node.getRight(), variableTypes, functionName);
        
        // Check if types are compatible for the operation
        if (!areTypesCompatibleForMathOp(leftType, rightType, node.getOperation())) {
            throw new RuntimeException("In function '" + functionName + "': Incompatible types for math operation. " + 
                                      "Left: '" + leftType + "', Right: '" + rightType + "', Operation: " + 
                                      node.getOperation());
        }
        
        // Determine the result type
        if (leftType.equals("integer") && rightType.equals("integer")) {
            return "integer";
        } else if ((leftType.equals("integer") || leftType.equals("real")) && 
                  (rightType.equals("integer") || rightType.equals("real"))) {
            return "real";
        } else if (leftType.equals("string") && rightType.equals("string") && 
                  node.getOperation() == MathOpNode.Operation.ADD) {
            return "string";
        } else {
            throw new RuntimeException("In function '" + functionName + "': Unsupported types for math operation. " + 
                                      "Left: '" + leftType + "', Right: '" + rightType + "', Operation: " + 
                                      node.getOperation());
        }
    }
    
    /**
     * Checks a boolean compare node for type consistency
     * @param node The boolean compare node
     * @param variableTypes Map of variable names to their types
     * @param functionName The name of the current function (for error messages)
     */
    private void checkBooleanCompareNode(BooleanCompareNode node, Map<String, String> variableTypes, String functionName) {
        String leftType = getExpressionType(node.getLeftSide(), variableTypes, functionName);
        String rightType = getExpressionType(node.getRightSide(), variableTypes, functionName);
        
        // Check if types are compatible for comparison
        if (!areTypesCompatibleForComparison(leftType, rightType)) {
            throw new RuntimeException("In function '" + functionName + "': Incompatible types for comparison. " + 
                                      "Left: '" + leftType + "', Right: '" + rightType + "', Comparison: " + 
                                      node.getOperator());
        }
    }
    
    /**
     * Checks if two types are compatible for assignment
     * @param targetType The target type
     * @param expressionType The expression type
     * @return True if the types are compatible, false otherwise
     */
    private boolean areTypesCompatible(String targetType, String expressionType) {
        // Same types are always compatible
        if (targetType.equals(expressionType)) {
            return true;
        }
        
        // Integer can be assigned to real
        if (targetType.equals("real") && expressionType.equals("integer")) {
            return true;
        }
        
        // All other type combinations are incompatible
        return false;
    }
    
    /**
     * Checks if two types are compatible for a math operation
     * @param leftType The left operand type
     * @param rightType The right operand type
     * @param operation The math operation
     * @return True if the types are compatible, false otherwise
     */
    private boolean areTypesCompatibleForMathOp(String leftType, String rightType, MathOpNode.Operation operation) {
        // Integer and integer are compatible for all operations
        if (leftType.equals("integer") && rightType.equals("integer")) {
            return true;
        }
        
        // Real and real, or real and integer, or integer and real are compatible for all operations except MOD
        if ((leftType.equals("real") || leftType.equals("integer")) && 
            (rightType.equals("real") || rightType.equals("integer"))) {
            return operation != MathOpNode.Operation.MOD; // MOD only works with integers
        }
        
        // String and string are compatible only for ADD
        if (leftType.equals("string") && rightType.equals("string")) {
            return operation == MathOpNode.Operation.ADD;
        }
        
        // All other type combinations are incompatible
        return false;
    }
    
    /**
     * Checks if two types are compatible for comparison
     * @param leftType The left operand type
     * @param rightType The right operand type
     * @return True if the types are compatible, false otherwise
     */
    private boolean areTypesCompatibleForComparison(String leftType, String rightType) {
        // Same types are always compatible
        if (leftType.equals(rightType)) {
            return true;
        }
        
        // Integer and real are compatible
        if ((leftType.equals("integer") && rightType.equals("real")) || 
            (leftType.equals("real") && rightType.equals("integer"))) {
            return true;
        }
        
        // All other type combinations are incompatible
        return false;
    }
}