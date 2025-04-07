package Shanks;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends Node {
    private String name;
    private List<VariableNode> parameters;
    private List<VariableNode> constants;
    private List<VariableNode> variables;
    private List<StatementNode> statements;
    
    public FunctionNode(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
        this.constants = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.statements = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public List<VariableNode> getParameters() {
        return parameters;
    }
    
    public void addParameter(VariableNode parameter) {
        parameters.add(parameter);
    }
    
    public List<VariableNode> getConstants() {
        return constants;
    }
    
    public void addConstant(VariableNode constant) {
        constants.add(constant);
    }
    
    public List<VariableNode> getVariables() {
        return variables;
    }
    
    public void addVariable(VariableNode variable) {
        variables.add(variable);
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
    
    public void addStatement(StatementNode statement) {
        statements.add(statement);
    }
    
    // New method for variadic functions
    public boolean isVariadic() {
        return false;
    }
    
    // Method for built-in functions to execute
    public void execute(List<InterpreterDataType> parameters) {
        throw new UnsupportedOperationException("Execute not implemented for this function");
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define ").append(name).append("(");
        
        // Add parameters
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameters.get(i));
        }
        
        sb.append(")\n");
        
        // Add constants
        if (!constants.isEmpty()) {
            sb.append("constants\n");
            for (VariableNode constant : constants) {
                sb.append("  ").append(constant).append("\n");
            }
        }
        
        // Add variables
        if (!variables.isEmpty()) {
            sb.append("variables\n");
            for (VariableNode variable : variables) {
                sb.append("  ").append(variable).append("\n");
            }
        }
        
        // Add statements
        sb.append("begin\n");
        for (StatementNode statement : statements) {
            sb.append("  ").append(statement).append("\n");
        }
        sb.append("end");
        
        return sb.toString();
    }
}