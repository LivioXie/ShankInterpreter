package Shanks;

import java.util.List;

public class ForNode extends StatementNode {
    private VariableReferenceNode variable;
    private Node fromExpr;
    private Node toExpr;
    private List<StatementNode> statements;
    
    public ForNode(VariableReferenceNode variable, Node fromExpr, Node toExpr, List<StatementNode> statements) {
        this.variable = variable;
        this.fromExpr = fromExpr;
        this.toExpr = toExpr;
        this.statements = statements;
    }
    
    public VariableReferenceNode getVariable() {
        return variable;
    }
    
    public Node getFromExpr() {
        return fromExpr;
    }
    
    public Node getToExpr() {
        return toExpr;
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("for ").append(variable).append(" from ").append(fromExpr)
          .append(" to ").append(toExpr).append("\n");
        
        for (StatementNode statement : statements) {
            sb.append("  ").append(statement).append("\n");
        }
        
        return sb.toString();
    }
}