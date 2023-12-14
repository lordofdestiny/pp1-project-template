package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.symboltable.Tab;

import java.io.*;

public class MJSemanticTest {
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
        Logger log = Logger.getLogger(MJSemanticTest.class);

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

            Tab.init();
            SemanticPass v = new SemanticPass();
            prog.traverseBottomUp(v);

            log.info("=".repeat(30));
            Tab.dump();

            if (v.hadError) {
                log.error("Semantic errors detected...");
            } else {
                log.info("All semantic checks passed");
            }
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
