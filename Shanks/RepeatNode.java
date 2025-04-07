package Shanks;

import java.util.List;

public class RepeatNode extends StatementNode {
    private Node condition;
    private List<StatementNode> statements;
    
    public RepeatNode(Node condition, List<StatementNode> statements) {
        this.condition = condition;
        this.statements = statements;
    }
    
    public Node getCondition() {
        return condition;
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("repeat\n");
        
        for (StatementNode statement : statements) {
            sb.append("  ").append(statement).append("\n");
        }
        
        sb.append("until ").append(condition);
        
        return sb.toString();
    }
}