package Shanks;

public class MathOpNode extends Node {
    public enum Operation {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        MOD
    }
    
    private Operation operation;
    private Node left;
    private Node right;
    
    public MathOpNode(Operation operation, Node left, Node right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }
    
    public Operation getOperation() {
        return operation;
    }
    
    public Node getLeft() {
        return left;
    }
    
    public Node getRight() {
        return right;
    }
    
    @Override
    public String toString() {
        return "MathOpNode(" + operation + ", " + left + ", " + right + ")";
    }
}