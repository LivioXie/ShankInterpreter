package Shanks;

import java.util.List;

public class IfNode extends StatementNode {
    private Node condition;
    private List<StatementNode> statements;
    private IfNode nextIf; // For elsif or else
    
    public IfNode(Node condition, List<StatementNode> statements) {
        this.condition = condition;
        this.statements = statements;
        this.nextIf = null;
    }
    
    public IfNode(List<StatementNode> statements) {
        // For else blocks (no condition)
        this.condition = null;
        this.statements = statements;
        this.nextIf = null;
    }
    
    public Node getCondition() {
        return condition;
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
    
    public IfNode getNextIf() {
        return nextIf;
    }
    
    public void setNextIf(IfNode nextIf) {
        this.nextIf = nextIf;
    }
    
    public boolean isElse() {
        return condition == null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (condition == null) {
            sb.append("else\n");
        } else {
            sb.append("if ").append(condition).append(" then\n");
        }
        
        for (StatementNode statement : statements) {
            sb.append("  ").append(statement).append("\n");
        }
        
        if (nextIf != null) {
            sb.append(nextIf.toString());
        }
        
        return sb.toString();
    }
}