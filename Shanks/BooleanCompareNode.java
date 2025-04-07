package Shanks;

public class BooleanCompareNode extends Node {
    public enum ComparisonOperator {
        LESS_THAN,        // <
        GREATER_THAN,     // >
        LESS_EQUAL,       // <=
        GREATER_EQUAL,    // >=
        EQUAL,            // =
        NOT_EQUAL         // <>
    }
    
    private ComparisonOperator operator;
    private Node leftSide;
    private Node rightSide;
    
    public BooleanCompareNode(ComparisonOperator operator, Node leftSide, Node rightSide) {
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }
    
    public ComparisonOperator getOperator() {
        return operator;
    }
    
    public Node getLeftSide() {
        return leftSide;
    }
    
    public Node getRightSide() {
        return rightSide;
    }
    
    @Override
    public String toString() {
        String opString;
        switch (operator) {
            case LESS_THAN:
                opString = "<";
                break;
            case GREATER_THAN:
                opString = ">";
                break;
            case LESS_EQUAL:
                opString = "<=";
                break;
            case GREATER_EQUAL:
                opString = ">=";
                break;
            case EQUAL:
                opString = "=";
                break;
            case NOT_EQUAL:
                opString = "<>";
                break;
            default:
                opString = "?";
        }
        
        return leftSide + " " + opString + " " + rightSide;
    }
}