package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

import java.util.function.Consumer;

public class SemanticPass extends VisitorAdaptor {

    Logger log = Logger.getLogger(getClass());
    boolean hadError = false;
    Obj currentMethod = null;
    boolean returnFound = false;
    int nVars;

    private void report_impl(String message, SyntaxNode info, Consumer<String> logFn) {
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0) {
            msg.append(" on line ").append(line);
        }
        logFn.accept(msg.toString());
    }

    public void report_error(String message, SyntaxNode info) {
        hadError = true;
        report_impl(message, info, log::error);
    }

    public void report_trace(String message, SyntaxNode info) {
        report_impl(message, info, log::trace);
    }

    public void report_info(String message, SyntaxNode info) {
        report_impl(message, info, log::info);
    }

    public void visit(VarDecl varDecl) {
        report_trace("Declared variable '" + varDecl.getVarName() + "' ", varDecl);
        Obj varNode = Tab.insert(Obj.Var, varDecl.getVarName(), varDecl.getType().struct);
    }

    public void visit(ProgName progName) {
        progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
        Tab.openScope();
    }

    public void visit(Program  program) {
        nVars = Tab.currentScope().getnVars();
        Tab.chainLocalSymbols(program.getProgName().obj);
        Tab.closeScope();
    }

    public void visit(Type type) {
        Obj typeNode = Tab.find(type.getTypeName());
        if (typeNode == Tab.noObj) {
            report_error("Type '" + type.getTypeName() + "' not found", type);
        } else if (Obj.Type == typeNode.getKind()) {
            type.struct = typeNode.getType();
        } else {
            report_error("Name '" + type.getTypeName() + "' does not resolve to a type", type);
            type.struct = Tab.noType;
        }

    }

    public void visit(MethodTypeName methodTypeName) {
        currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethodName(), methodTypeName.getType().struct);
        methodTypeName.obj = currentMethod;
        Tab.openScope();
        report_trace("Compiling function '" + methodTypeName.getMethodName() + "'", methodTypeName);
    }

    public void visit(MethodDecl methDecl) {
        if (!returnFound && currentMethod.getType() != Tab.noType) {
            report_error("Semantic error on line " + methDecl.getLine() + ": function '" + currentMethod.getName() + "' doesn't have a return statement", null);
        }
        Tab.chainLocalSymbols(currentMethod);
        Tab.closeScope();

        currentMethod = null;
        returnFound = false;
    }

    public void visit(Designator designator) {
        Obj obj = Tab.find(designator.getName());
        if (obj == Tab.noObj) {
            report_error("Error on line " + designator.getLine() + ": name '" + designator.getName() + "' is not declared in this scope", null);
        }
        designator.obj = obj;
    }

    public void visit(FuncCall funcCall) {
        Obj func = funcCall.getDesignator().obj;
        if (Obj.Meth == func.getKind()) {
            funcCall.struct = func.getType();
        } else {
            report_error("Error on line " + funcCall.getLine() + ": name '" + func.getName() + "' is not a function", null);
            funcCall.struct = Tab.noType;
        }
    }

    public void visit(Term term) {
        term.struct = term.getFactor().struct;
    }

    public void visit(TermExpr termExpr) {
        termExpr.struct = termExpr.getTerm().struct;
    }

    public void visit(AddExpr addExpr) {
        Struct te = addExpr.getExpr().struct;
        Struct t = addExpr.getTerm().struct;

        if (te.equals(t) && te == Tab.intType) {
            addExpr.struct = te;
        } else {
            report_error("Error on line " + addExpr.getLine() + " : incompatible types in addition expression", null);
            addExpr.struct = Tab.noType;
        }
    }

    public void visit(Const cnst) {
        cnst.struct = Tab.intType;
    }

    public void visit(Var var) {
        var.struct = var.getDesignator().obj.getType();

    }

    public void visit(ReturnExpr returnExpr) {
        if (currentMethod == null) {
            report_error("Return statement not allowed outside of function declaration.", null);
            return;
        }
        returnFound = true;
        Struct currMethType = currentMethod.getType();
        if (!currMethType.compatibleWith(returnExpr.getExpr().struct)) {
            report_error("Error on line " + returnExpr.getLine() + ": type of return expression does not match return value type of function '" + currentMethod.getName() + "'", null);
        }
    }

    public void visit(ReturnNoExpr returnNoExpr) {
        if (currentMethod == null) {
            report_error("Return statement not allowed outside of function declaration.", null);
        }
    }

    public void visit(Assignment assignment) {
        if (!assignment.getExpr().struct.assignableTo(assignment.getDesignator().obj.getType())) {
            report_error("Error on line " + assignment.getLine() + " incompatible types in value assignment", null);
        }
    }
}