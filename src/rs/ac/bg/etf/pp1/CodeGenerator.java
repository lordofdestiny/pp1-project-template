package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;

import static rs.ac.bg.etf.pp1.util.CounterVisitor.*;

public class CodeGenerator extends VisitorAdaptor {
    private int mainPC;

    public int getMainPC() {
        return mainPC;
    }

    public void visit(PrintStmt printStmt) {
        if (printStmt.getExpr().struct == Tab.intType) {
            Code.loadConst(5);
            Code.put(Code.print);
        } else {
            Code.loadConst(1);
            Code.put(Code.bprint);
        }
    }

    public void visit(Const cnst) {
        Obj con = Tab.insert(Obj.Con, "$", cnst.struct);
        con.setLevel(0);
        con.setAdr(cnst.getN1());

        Code.load(con);
    }

    public void visit(MethodTypeName methodTypeName) {
        if ("main".equalsIgnoreCase(methodTypeName.getMethodName())) {
            mainPC = Code.pc;
        }
        methodTypeName.obj.setAdr(Code.pc);

        SyntaxNode methodNode = methodTypeName.getParent();

        VarCounter varCounter = new VarCounter();
        methodNode.traverseTopDown(varCounter);

        FormParamCounter fpCounter = new FormParamCounter();
        methodNode.traverseTopDown(fpCounter);

        //  Generate the entry instruction
        Code.put(Code.enter);
        Code.put(fpCounter.getCount());
        Code.put(fpCounter.getCount() + varCounter.getCount());
    }

    public void visit(MethodDecl methodDecl) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    public void visit(Assignment assignment) {
        Code.store(assignment.getDesignator().obj);
    }

    public void visit(Designator designator) {
        SyntaxNode parent = designator.getParent();

        if (Assignment.class != parent.getClass() && ProcCall.class != parent.getClass() && FuncCall.class != parent.getClass()) {
            Code.load(designator.obj);
        }
    }

    public void visit(FuncCall funcCall) {
        Obj functionObj = funcCall.getDesignator().obj;
        int offset = functionObj.getAdr() - Code.pc;
        Code.put(Code.call);
        Code.put2(offset);
    }

    public void visit(ProcCall procCall) {
        Obj functionObj = procCall.getDesignator().obj;
        int offset = functionObj.getAdr() - Code.pc;
        Code.put(Code.call);
        Code.put2(offset);
        if (procCall.getDesignator().obj.getType() != Tab.noType) {
            Code.put(Code.pop);
        }
    }

    public void visit(ReturnExpr returnExpr) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    public void visit(ReturnNoExpr returnNoExpr) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    public void visit(AddExpr addExpr) {
        Code.put(Code.add);
    }
}
