package Shanks;

import java.util.List;

public class WhileNode extends StatementNode {
    private Node condition;
    private List<StatementNode> statements;
    
    public WhileNode(Node condition, List<StatementNode> statements) {
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
        sb.append("while ").append(condition).append("\n");
        
        for (StatementNode statement : statements) {
            sb.append("  ").append(statement).append("\n");
        }
        
        return sb.toString();
    }
}