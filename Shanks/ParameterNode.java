package Shanks;

public class ParameterNode extends Node {
    private VariableReferenceNode variableReference;
    private Node expression;
    private boolean isVar;
    
    public ParameterNode(VariableReferenceNode variableReference) {
        this.variableReference = variableReference;
        this.expression = null;
        this.isVar = true;
    }
    
    public ParameterNode(Node expression) {
        this.variableReference = null;
        this.expression = expression;
        this.isVar = false;
    }
    
    public VariableReferenceNode getVariableReference() {
        return variableReference;
    }
    
    public Node getExpression() {
        return expression;
    }
    
    public boolean isVar() {
        return isVar;
    }
    
    @Override
    public String toString() {
        if (isVar) {
            return "var " + variableReference;
        } else {
            return expression.toString();
        }
    }
}