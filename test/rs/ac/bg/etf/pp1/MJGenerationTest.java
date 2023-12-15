package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.exception.UnexpectedSymbolException;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;

import java.io.*;

public class MJGenerationTest {
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
        Logger log = Logger.getLogger(MJGenerationTest.class);

        Reader br = null;
        try {
            File sourceCode = new File("test/programs/program.mj");
            log.info("Compiling source file: " + sourceCode.getAbsolutePath());

            br = new BufferedReader(new FileReader(sourceCode));
            Yylex lexer = new Yylex(br);

            MJParser parser = new MJParser(lexer);
            Program prog = parser.parse_or_throw();

            log.info(prog.toString(""));
            log.info("=".repeat(30));

            Tab.init();
            SemanticPass v = new SemanticPass();
            prog.traverseBottomUp(v);

            log.info("=".repeat(30));
            Tab.dump();

            if (v.hadError) {
                log.error("Semantic errors detected...");
                return;
            }
            log.info("All semantic checks passed");

            File objFile = new File("test/obj/program.obj");

            CodeGenerator codeGenerator = new CodeGenerator();
            prog.traverseBottomUp(codeGenerator);
            Code.dataSize = v.nVars;
            Code.mainPc = codeGenerator.getMainPC();
            Code.write(new FileOutputStream(objFile));
            log.info("Object file generated.");
        } catch (rs.ac.bg.etf.pp1.exception.ParseException pe) {
            System.exit(20);
        } catch (UnexpectedSymbolException us) {
            System.exit(10);
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
