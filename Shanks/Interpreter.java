package Shanks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
    private ProgramNode program;
    private Map<String, FunctionNode> functions;
    
    public Interpreter(ProgramNode program) {
        this.program = program;
        this.functions = new HashMap<>();
        
        // Add all user-defined functions to the map
        for (FunctionNode function : program.getFunctions().values()) {
            functions.put(function.getName().toLowerCase(), function);
        }
        
        // Add built-in functions
        addBuiltInFunctions();
    }
    
    private void addBuiltInFunctions() {
        // Add all built-in functions to the map
        functions.put("read", new BuiltInRead());
        functions.put("write", new BuiltInWrite());
        functions.put("left", new BuiltInLeft());
        functions.put("right", new BuiltInRight());
        functions.put("substring", new BuiltInSubstring());
        functions.put("squareroot", new BuiltInSquareRoot());
        functions.put("getrandom", new BuiltInGetRandom());
        functions.put("integertoreal", new BuiltInIntegerToReal());
        functions.put("realtointeger", new BuiltInRealToInteger());
        functions.put("start", new BuiltInStart());
        functions.put("end", new BuiltInEnd());
    }
    
    public void interpret() {
        // Find the "main" function and execute it
        FunctionNode mainFunction = functions.get("main");
        if (mainFunction == null) {
            throw new RuntimeException("No 'main' function found");
        }
        
        // Execute the main function
        interpretFunction(mainFunction);
    }
    
    public Map<String, FunctionNode> getFunctions() {
        return functions;
    }
    
    // New methods for Assignment 8
    
    /**
     * Interprets a function by creating local variables and executing statements
     * @param function The function to interpret
     */
    private void interpretFunction(FunctionNode function) {
        // Create a map for local variables
        Map<String, InterpreterDataType> variables = new HashMap<>();
        
        // Add constants to the variables map
        for (VariableNode constant : function.getConstants()) {
            String name = constant.getName().toLowerCase();
            InterpreterDataType value = createIDTFromVariableNode(constant);
            variables.put(name, value);
        }
        
        // Add local variables to the variables map
        for (VariableNode variable : function.getVariables()) {
            String name = variable.getName().toLowerCase();
            InterpreterDataType value = createIDTFromVariableNode(variable);
            variables.put(name, value);
        }
        
        // Interpret the function's statements
        interpretBlock(function.getStatements(), variables);
    }
    
    /**
     * Creates an InterpreterDataType from a VariableNode
     * @param variable The variable node
     * @return The corresponding InterpreterDataType
     */
    private InterpreterDataType createIDTFromVariableNode(VariableNode variable) {
        String type = variable.getType().toLowerCase();
        
        switch (type) {
            case "integer":
                return new IntegerDataType();
            case "real":
                return new RealDataType();
            case "string":
                return new StringDataType();
            case "character":
                return new CharacterDataType();
            case "boolean":
                return new BooleanDataType();
            default:
                if (type.startsWith("array")) {
                    // Handle array type
                    // This is a placeholder - you'll need to implement array handling
                    return new ArrayDataType();
                }
                throw new RuntimeException("Unsupported variable type: " + type);
        }
    }
    
    /**
     * Interprets a block of statements
     * @param statements The statements to interpret
     * @param variables The current variable scope
     */
    private void interpretBlock(List<StatementNode> statements, Map<String, InterpreterDataType> variables) {
        for (StatementNode statement : statements) {
            if (statement instanceof AssignmentNode) {
                handleAssignmentNode((AssignmentNode) statement, variables);
            } else if (statement instanceof IfNode) {
                handleIfNode((IfNode) statement, variables);
            } else if (statement instanceof WhileNode) {
                handleWhileNode((WhileNode) statement, variables);
            } else if (statement instanceof RepeatNode) {
                handleRepeatNode((RepeatNode) statement, variables);
            } else if (statement instanceof ForNode) {
                handleForNode((ForNode) statement, variables);
            } else if (statement instanceof FunctionCallNode) {
                handleFunctionCallNode((FunctionCallNode) statement, variables);
            } else {
                throw new RuntimeException("Unsupported statement type: " + statement.getClass().getName());
            }
        }
    }
    
    /**
     * Evaluates an expression and returns the resulting value
     * @param node The node to evaluate
     * @param variables The current variable scope
     * @return The resulting InterpreterDataType
     */
    private InterpreterDataType expression(Node node, Map<String, InterpreterDataType> variables) {
        if (node instanceof VariableReferenceNode) {
            return handleVariableReferenceNode((VariableReferenceNode) node, variables);
        } else if (node instanceof IntegerNode) {
            return new IntegerDataType(((IntegerNode) node).getValue());
        } else if (node instanceof RealNode) {
            return new RealDataType(((RealNode) node).getValue());
        } else if (node instanceof StringNode) {
            return new StringDataType(((StringNode) node).getValue());
        } else if (node instanceof BooleanNode) {
            return new BooleanDataType(((BooleanNode) node).getValue());
        } else if (node instanceof CharacterNode) {
            return new CharacterDataType(((CharacterNode) node).getValue());
        } else if (node instanceof MathOpNode) {
            return handleMathOpNode((MathOpNode) node, variables);
        } else if (node instanceof FunctionCallNode) {
            // This is a placeholder - function calls that return values will need special handling
            handleFunctionCallNode((FunctionCallNode) node, variables);
            return null; // This should be replaced with the actual return value
        } else {
            throw new RuntimeException("Unsupported expression type: " + node.getClass().getName());
        }
    }
    
    /**
     * Handles a variable reference node
     * @param node The variable reference node
     * @param variables The current variable scope
     * @return The variable's value
     */
    private InterpreterDataType handleVariableReferenceNode(VariableReferenceNode node, Map<String, InterpreterDataType> variables) {
        String name = node.getName().toLowerCase();
        InterpreterDataType value = variables.get(name);
        
        if (value == null) {
            throw new RuntimeException("Variable not found: " + name);
        }
        
        return value;
    }
    
    /**
     * Handles a math operation node
     * @param node The math operation node
     * @param variables The current variable scope
     * @return The result of the operation
     */
    private InterpreterDataType handleMathOpNode(MathOpNode node, Map<String, InterpreterDataType> variables) {
        InterpreterDataType left = expression(node.getLeft(), variables);
        InterpreterDataType right = expression(node.getRight(), variables);
        
        // Ensure both sides are of the same type
        if (left.getClass() != right.getClass()) {
            throw new RuntimeException("Type mismatch in math operation");
        }
        
        // Handle different types of math operations
        if (left instanceof IntegerDataType && right instanceof IntegerDataType) {
            int leftValue = ((IntegerDataType) left).getValue();
            int rightValue = ((IntegerDataType) right).getValue();
            
            switch (node.getOperation()) {
                case ADD:
                    return new IntegerDataType(leftValue + rightValue);
                case SUBTRACT:
                    return new IntegerDataType(leftValue - rightValue);
                case MULTIPLY:
                    return new IntegerDataType(leftValue * rightValue);
                case DIVIDE:
                    if (rightValue == 0) {
                        throw new RuntimeException("Division by zero");
                    }
                    return new IntegerDataType(leftValue / rightValue);
                case MOD:
                    if (rightValue == 0) {
                        throw new RuntimeException("Modulo by zero");
                    }
                    return new IntegerDataType(leftValue % rightValue);
                default:
                    throw new RuntimeException("Unsupported integer operation: " + node.getOperation());
            }
        } else if (left instanceof RealDataType && right instanceof RealDataType) {
            float leftValue = ((RealDataType) left).getValue();
            float rightValue = ((RealDataType) right).getValue();
            
            switch (node.getOperation()) {
                case ADD:
                    return new RealDataType(leftValue + rightValue);
                case SUBTRACT:
                    return new RealDataType(leftValue - rightValue);
                case MULTIPLY:
                    return new RealDataType(leftValue * rightValue);
                case DIVIDE:
                    if (rightValue == 0) {
                        throw new RuntimeException("Division by zero");
                    }
                    return new RealDataType(leftValue / rightValue);
                default:
                    throw new RuntimeException("Unsupported real operation: " + node.getOperation());
            }
        } else if (left instanceof StringDataType && right instanceof StringDataType) {
            String leftValue = ((StringDataType) left).getValue();
            String rightValue = ((StringDataType) right).getValue();
            
            if (node.getOperation() == MathOpNode.Operation.ADD) {
                return new StringDataType(leftValue + rightValue);
            } else {
                throw new RuntimeException("Only addition is supported for strings");
            }
        } else {
            throw new RuntimeException("Unsupported operand types for math operation");
        }
    }
    
    /**
     * Evaluates a boolean comparison
     * @param node The boolean comparison node
     * @param variables The current variable scope
     * @return The result of the comparison
     */
    private boolean evaluateBooleanCompare(BooleanCompareNode node, Map<String, InterpreterDataType> variables) {
        InterpreterDataType left = expression(node.getLeftSide(), variables);
        InterpreterDataType right = expression(node.getRightSide(), variables);
        
        // Handle different types of comparisons
        if (left instanceof IntegerDataType && right instanceof IntegerDataType) {
            int leftValue = ((IntegerDataType) left).getValue();
            int rightValue = ((IntegerDataType) right).getValue();
            
            switch (node.getOperator()) {
                case EQUAL:
                    return leftValue == rightValue;
                case NOT_EQUAL:
                    return leftValue != rightValue;
                case GREATER_THAN:
                    return leftValue > rightValue;
                case GREATER_EQUAL:
                    return leftValue >= rightValue;
                case LESS_THAN:
                    return leftValue < rightValue;
                case LESS_EQUAL:
                    return leftValue <= rightValue;
                default:
                    throw new RuntimeException("Unsupported comparison: " + node.getOperator());
            }
        } else if (left instanceof RealDataType && right instanceof RealDataType) {
            float leftValue = ((RealDataType) left).getValue();
            float rightValue = ((RealDataType) right).getValue();
            
            switch (node.getOperator()) {
                case EQUAL:
                    return leftValue == rightValue;
                case NOT_EQUAL:
                    return leftValue != rightValue;
                case GREATER_THAN:
                    return leftValue > rightValue;
                case GREATER_EQUAL:
                    return leftValue >= rightValue;
                case LESS_THAN:
                    return leftValue < rightValue;
                case LESS_EQUAL:
                    return leftValue <= rightValue;
                default:
                    throw new RuntimeException("Unsupported comparison: " + node.getOperator());
            }
        } else if (left instanceof StringDataType && right instanceof StringDataType) {
            String leftValue = ((StringDataType) left).getValue();
            String rightValue = ((StringDataType) right).getValue();
            
            switch (node.getOperator()) {
                case EQUAL:
                    return leftValue.equals(rightValue);
                case NOT_EQUAL:
                    return !leftValue.equals(rightValue);
                default:
                    throw new RuntimeException("Only equality comparisons are supported for strings");
            }
        } else if (left instanceof BooleanDataType && right instanceof BooleanDataType) {
            boolean leftValue = ((BooleanDataType) left).getValue();
            boolean rightValue = ((BooleanDataType) right).getValue();
            
            switch (node.getOperator()) {
                case EQUAL:
                    return leftValue == rightValue;
                case NOT_EQUAL:
                    return leftValue != rightValue;
                default:
                    throw new RuntimeException("Only equality comparisons are supported for booleans");
            }
        } else {
            throw new RuntimeException("Unsupported operand types for comparison");
        }
    }
    
    /**
     * Handles an assignment node
     * @param node The assignment node
     * @param variables The current variable scope
     */
    private void handleAssignmentNode(AssignmentNode node, Map<String, InterpreterDataType> variables) {
        String name = node.getTarget().getName().toLowerCase();
        InterpreterDataType value = expression(node.getValue(), variables);
        
        // Check if the variable exists
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not found: " + name);
        }
        
        // Check if the types are compatible
        InterpreterDataType currentValue = variables.get(name);
        if (currentValue.getClass() != value.getClass()) {
            throw new RuntimeException("Type mismatch in assignment");
        }
        
        // Update the variable's value
        variables.put(name, value);
    }
    
    /**
     * Handles an if node
     * @param node The if node
     * @param variables The current variable scope
     */
    private void handleIfNode(IfNode node, Map<String, InterpreterDataType> variables) {
        boolean condition = evaluateBooleanCompare((BooleanCompareNode)node.getCondition(), variables);
        
        if (condition) {
            // Execute the if block
            interpretBlock(node.getStatements(), variables);
        } else {
            // Check for else-if or else blocks
            IfNode elseIfNode = node.getNextIf();
            while (elseIfNode != null) {
                if (elseIfNode.getCondition() == null) {
                    // This is an else block (no condition)
                    interpretBlock(elseIfNode.getStatements(), variables);
                    break;
                } else {
                    // This is an else-if block
                    condition = evaluateBooleanCompare((BooleanCompareNode)elseIfNode.getCondition(), variables);
                    if (condition) {
                        interpretBlock(elseIfNode.getStatements(), variables);
                        break;
                    }
                }
                elseIfNode = elseIfNode.getNextIf();
            }
        }
    }
    
    /**
     * Handles a while node
     * @param node The while node
     * @param variables The current variable scope
     */
    private void handleWhileNode(WhileNode node, Map<String, InterpreterDataType> variables) {
        while (evaluateBooleanCompare((BooleanCompareNode)node.getCondition(), variables)) {
            interpretBlock(node.getStatements(), variables);
        }
    }
    
    /**
     * Handles a repeat node
     * @param node The repeat node
     * @param variables The current variable scope
     */
    private void handleRepeatNode(RepeatNode node, Map<String, InterpreterDataType> variables) {
        do {
            interpretBlock(node.getStatements(), variables);
        } while (!evaluateBooleanCompare((BooleanCompareNode)node.getCondition(), variables));
    }
    
    /**
     * Handles a for node
     * @param node The for node
     * @param variables The current variable scope
     */
    private void handleForNode(ForNode node, Map<String, InterpreterDataType> variables) {
        String varName = node.getVariable().getName().toLowerCase();
        
        // Check if the variable exists
        if (!variables.containsKey(varName)) {
            throw new RuntimeException("Variable not found: " + varName);
        }
        
        // Get the start and end values
        InterpreterDataType startIDT = expression(node.getFromExpr(), variables);
        InterpreterDataType endIDT = expression(node.getToExpr(), variables);
        
        // Ensure both are integers
        if (!(startIDT instanceof IntegerDataType) || !(endIDT instanceof IntegerDataType)) {
            throw new RuntimeException("For loop range must be integers");
        }
        
        int start = ((IntegerDataType) startIDT).getValue();
        int end = ((IntegerDataType) endIDT).getValue();
        
        // Set the loop variable to the start value
        variables.put(varName, new IntegerDataType(start));
        
        // Determine if we're counting up or down
        if (start <= end) {
            // Count up
            for (int i = start; i <= end; i++) {
                variables.put(varName, new IntegerDataType(i));
                interpretBlock(node.getStatements(), variables);
            }
        } else {
            // Count down
            for (int i = start; i >= end; i--) {
                variables.put(varName, new IntegerDataType(i));
                interpretBlock(node.getStatements(), variables);
            }
        }
    }
    
    /**
     * Handles a function call node
     * @param node The function call node
     * @param variables The current variable scope
     */
    private void handleFunctionCallNode(FunctionCallNode node, Map<String, InterpreterDataType> variables) {
        String functionName = node.getName().toLowerCase();
        FunctionNode function = functions.get(functionName);
        
        if (function == null) {
            throw new RuntimeException("Function not found: " + functionName);
        }
        
        // Check parameter count
        List<ParameterNode> callParameters = node.getParameters();
        List<VariableNode> functionParameters = function.getParameters();
        
        // If the function is not variadic, check that parameter counts match
        if (!function.isVariadic() && callParameters.size() != functionParameters.size()) {
            throw new RuntimeException("Function " + functionName + " expects " + 
                                      functionParameters.size() + " parameters, but got " + 
                                      callParameters.size());
        }
        
        // Create a new collection of IDTs for the function call
        List<InterpreterDataType> parameterValues = new ArrayList<>();
        
        // Evaluate each parameter and add to the collection
        for (int i = 0; i < callParameters.size(); i++) {
            ParameterNode callParam = callParameters.get(i);
            
            // Evaluate the parameter expression
            InterpreterDataType value;
            if (callParam.getExpression() instanceof VariableReferenceNode && callParam.isVar()) {
                // For var parameters, we need to get the variable reference
                VariableReferenceNode varRef = (VariableReferenceNode) callParam.getExpression();
                String varName = varRef.getName().toLowerCase();
                value = variables.get(varName);
                
                if (value == null) {
                    throw new RuntimeException("Variable not found: " + varName);
                }
                
                // Create a copy of the value for the function to modify
                parameterValues.add(value);
            } else {
                // For non-var parameters, evaluate the expression
                value = expression(callParam.getExpression(), variables);
                
                // Create a copy of the value
                InterpreterDataType copy = createCopyOfIDT(value);
                parameterValues.add(copy);
            }
        }
        
        // Execute the function
        if (function instanceof BuiltInRead || 
            function instanceof BuiltInWrite || 
            function instanceof BuiltInLeft || 
            function instanceof BuiltInRight || 
            function instanceof BuiltInSubstring || 
            function instanceof BuiltInSquareRoot || 
            function instanceof BuiltInGetRandom || 
            function instanceof BuiltInIntegerToReal || 
            function instanceof BuiltInRealToInteger || 
            function instanceof BuiltInStart || 
            function instanceof BuiltInEnd) {
            
            // Execute built-in function
            function.execute(parameterValues);
        } else {
            // Create a new scope for the function
            Map<String, InterpreterDataType> functionVariables = new HashMap<>();
            
            // Add parameters to the function's variable scope
            for (int i = 0; i < functionParameters.size(); i++) {
                VariableNode param = functionParameters.get(i);
                String paramName = param.getName().toLowerCase();
                
                // Use the evaluated parameter value
                InterpreterDataType paramValue = parameterValues.get(i);
                functionVariables.put(paramName, paramValue);
            }
            
            // Add constants to the function's variable scope
            for (VariableNode constant : function.getConstants()) {
                String name = constant.getName().toLowerCase();
                InterpreterDataType value = createIDTFromVariableNode(constant);
                functionVariables.put(name, value);
            }
            
            // Add local variables to the function's variable scope
            for (VariableNode variable : function.getVariables()) {
                String name = variable.getName().toLowerCase();
                InterpreterDataType value = createIDTFromVariableNode(variable);
                functionVariables.put(name, value);
            }
            
            // Interpret the function's statements
            interpretBlock(function.getStatements(), functionVariables);
        }
        
        // Copy values back for var parameters
        for (int i = 0; i < callParameters.size(); i++) {
            ParameterNode callParam = callParameters.get(i);
            
            // Only copy back for var parameters
            if (callParam.isVar()) {
                // Check if the function parameter is also var
                boolean isFunctionParamVar = false;
                
                if (function.isVariadic()) {
                    // For variadic functions, all parameters can be var
                    isFunctionParamVar = true;
                } else if (i < functionParameters.size()) {
                    VariableNode functionParam = functionParameters.get(i);
                    isFunctionParamVar = functionParam.isVar();
                }
                
                if (isFunctionParamVar) {
                    // Get the variable reference
                    VariableReferenceNode varRef = (VariableReferenceNode) callParam.getExpression();
                    String varName = varRef.getName().toLowerCase();
                    
                    // Update the variable in the caller's scope
                    variables.put(varName, parameterValues.get(i));
                }
            }
        }
    }
    
    /**
     * Creates a copy of an InterpreterDataType
     * @param original The original IDT
     * @return A copy of the IDT
     */
    private InterpreterDataType createCopyOfIDT(InterpreterDataType original) {
        if (original instanceof IntegerDataType) {
            return new IntegerDataType(((IntegerDataType) original).getValue());
        } else if (original instanceof RealDataType) {
            return new RealDataType(((RealDataType) original).getValue());
        } else if (original instanceof StringDataType) {
            return new StringDataType(((StringDataType) original).getValue());
        } else if (original instanceof BooleanDataType) {
            return new BooleanDataType(((BooleanDataType) original).getValue());
        } else if (original instanceof CharacterDataType) {
            return new CharacterDataType(((CharacterDataType) original).getValue());
        } else if (original instanceof ArrayDataType) {
            // This is a placeholder - you'll need to implement deep copying for arrays
            return new ArrayDataType();
        } else {
            throw new RuntimeException("Unsupported data type for copying: " + original.getClass().getName());
        }
    }
}