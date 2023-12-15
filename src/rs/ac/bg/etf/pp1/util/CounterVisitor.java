package rs.ac.bg.etf.pp1.util;

import rs.ac.bg.etf.pp1.ast.*;

public abstract class CounterVisitor extends VisitorAdaptor {
    protected int count;

    public int getCount() {
        return count;
    }

    public static class FormParamCounter extends CounterVisitor {
        public void visit(FormalParamDecl formalParamDecl) {
            count++;
        }
    }

    public static class VarCounter extends CounterVisitor {
        public void visit(VarDecl varDecl) {
            count++;
        }
    }
}
