package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public class MJParserTest {
    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    static class ParseException extends Exception {
        ParseException() {
            super("Input program had syntax errors.");
        }
    }

    public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger(MJParserTest.class);

        Reader br = null;
        try {
            File sourceCode = new File("test/program.mj");
            log.info("Compiling source file: " + sourceCode.getAbsolutePath());

            br = new BufferedReader(new FileReader(sourceCode));
            Yylex lexer = new Yylex(br);

            MJParser parser = new MJParser(lexer);
            Symbol ast = parser.parse_safe().orElseThrow(
                    () -> new ParseException()
            );

            Program prog = (Program) (ast.value);

            log.info(prog.toString(""));
            log.info("=".repeat(30));

            RuleVisitor v = new RuleVisitor();
            prog.traverseBottomUp(v);

            log.info("Print calls count: " + v.printCallCount);
            log.info("Declared variables count: " + v.varDeclCount);
        } catch (ParseException pe) {
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
