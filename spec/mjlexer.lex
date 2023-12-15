package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;

%%

%{
    Logger log = Logger.getLogger(getClass());

    private Symbol new_symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn);
    }

    private Symbol new_symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn, value);
    }
%}

%cup
%line
%column

%xstate COMMENT

%eofval{
    return new_symbol(sym.EOF);
%eofval}

%%

" " { }
"\b" { }
"\t" { }
"\n" { }
"\r" { }
"\f" { }

"program"       { return new_symbol(sym.PROG, yytext()); }
"print"         { return new_symbol(sym.PRINT, yytext()); }
"return"        { return new_symbol(sym.RETURN, yytext()); }
"void"          { return new_symbol(sym.VOID, yytext()); }
"if"            { return new_symbol(sym.IF, yytext()); }
"else"          { return new_symbol(sym.ELSE, yytext()); }
"+"             { return new_symbol(sym.PLUS, yytext()); }
"="             { return new_symbol(sym.EQUAL, yytext()); }
";"             { return new_symbol(sym.SEMI, yytext()); }
","             { return new_symbol(sym.COMMA, yytext()); }
"("             { return new_symbol(sym.LPAREN, yytext()); }
")"             { return new_symbol(sym.RPAREN, yytext()); }
"{"             { return new_symbol(sym.LBRACE, yytext()); }
"}"             { return new_symbol(sym.RBRACE, yytext()); }

"//"            { yybegin(COMMENT); }

<COMMENT>.      { yybegin(COMMENT); }
<COMMENT>"\r\n"   { yybegin(YYINITIAL); }

[0-9]+          {return new_symbol(sym.NUMBER, Integer.valueOf(yytext())); }
[a-zA-Z][_a-zA-Z0-9]*      { return new_symbol(sym.IDENT, yytext()); }

.   {
        log.error("Unexpected symbol (" + yytext() + ") on line " + (yyline+1));
        return new_symbol(sym.error, yytext());
    }