package Shanks;

public class VariableReferenceNode extends Node {
    private String name;
    private Node indexExpression; // Optional for array access
    
    // Constructor for simple variable reference (no array index)
    public VariableReferenceNode(String name) {
        this.name = name;
        this.indexExpression = null;
    }
    
    // Constructor for array variable reference
    public VariableReferenceNode(String name, Node indexExpression) {
        this.name = name;
        this.indexExpression = indexExpression;
    }
    
    public String getName() {
        return name;
    }
    
    public Node getIndexExpression() {
        return indexExpression;
    }
    
    public boolean isArray() {
        return indexExpression != null;
    }
    
    @Override
    public String toString() {
        if (isArray()) {
            return name + "[" + indexExpression + "]";
        } else {
            return name;
        }
    }
}