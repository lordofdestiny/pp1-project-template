package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.exception.ParseException;
import rs.ac.bg.etf.pp1.exception.UnexpectedSymbolException;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public class MJParserTest {
    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger(MJParserTest.class);

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

            RuleVisitor v = new RuleVisitor();
            prog.traverseBottomUp(v);

            log.info("Print calls count: " + v.printCallCount);
            log.info("Declared variables count: " + v.varDeclCount);
        } catch (ParseException pe) {
            System.exit(20);
        } catch (UnexpectedSymbolException us) {
            System.exit(10);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    System.exit(2);
                }
            }
        }
    }
}
