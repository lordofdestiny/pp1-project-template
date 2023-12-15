# PP1 project template

This repository is a project template repository for the homework for the subject "Programski prevodioci 1" on the Faculty of electrical enigneering in Belgrade. 

This template is a reworked version of the template provided in class materials, but was adapted for use with IntelliJ IDEA code editor. It has additional Ant targets and Run targets added with the intent of making the development process more streamlined.

In order to use Ant targets, it is recommended to install IntelliJ IDEA [Ant Plugin](https://plugins.jetbrains.com/plugin/23025-ant?_ga=2.245260870.238897384.1702670743-556405651.1695306650&_gac=1.196130014.1699039179.Cj0KCQjwtJKqBhCaARIsAN_yS_kG83icSxF51QENNtogRzS6sDU1zCOHYArIcpIG2onsTaDbCnpd9ncaAvuxEALw_wcB&_gl=1%2Apppaah%2A_ga%2ANTU2NDA1NjUxLjE2OTUzMDY2NTA.%2A_ga_9J976DJZ68%2AMTcwMjY3MDc0Mi4zMy4wLjE3MDI2NzA3NDMuNTkuMC4w). 
There is no need to install Appache Ant separately, since it comes bundled with IntelliJ IDEA. This was atleast the case for with my version IntelliJ IDEA Ultimate 2023.3.

Default impelenations of for any of four main steps are not complete and do not perform all required checks/requirements

# Project targets
There several IntelliJ IDEA targets, including 4 Application targets and 4 Ant targets. Those targets use Ant targets defined in `build.xml` and IntelliJ IDEA build step for easier development.

## IDEA targets

### Application targets
All aplication targets first run Ant **compile-src** target

- **Lexer** - Run the `MJLexerTest` program that runs the lexer step on the `program.mj`
- **Parser** - Run the `MJParserTest` program that runs the lexer and parser steps on the `program.mj`
- **Semantic** - Runs the `MJSemanticTest` program that runs lexer, parser and semantic analysis steps on the `program.mj`
- **Generation** - Runs the `MJGenerationTest` program that runs the whole compiler pipeline on `program.mj` and produces `program.obj` in the `tests/obj` folder

### Ant IDEA targets
- **Clean** - Directly calls only `clean` Ant target
- **Run** - Builds the complete application, runs the **Generate** target, and then runs Ant `run-program` target that runs `program.obj` using MicroJava VM
- **Debug** - Exactly the same as **Run**, but adds passes `-d` flag to MicroJava VM to trigger the debug mode
- **Dissasemble** - Builds the complete application, runs the **Generate** target and then runs Ant `dissasemble-program` target that uses `disasm` classt to dump the contents of the `program.obj` 

## *Ant targets*
- **clean** - Remove all generated `*.java` files and compiled `*.class` files as well as any other execution results
- **clean-logs** - Delete all log files that were produced
- **compile-src** - Generates parser and lexer classes from the `spec` files and then compiles all classes in the `src` directory. Depends on *clean*, *parserGen* and *lexerGen*
- **debug-program** - Runs MicroJava VM with `-d` flag for debugging on `program.obj`
- **disassemble-program** - Runs `disasm` class on `program.obj`
- **lexerGen** - Generates lexer implementation files from `mjlexer.lex` specification file. It should be run after *parserGen*
- **parserGen** - Generates parser implementation files from `mjparser.cup` specification file
- **run-program** - Runs MicroJava VM on `program.obj`

## Project structure

-  Folders
    - `.idea` - IntelliJ IDEA configuration files. It contains all the settings and runnable targets
    - `config` - Configuration files, mainly for `log4j` configurations
    - `lib` - Library folder. Ant targets add libraries to the classpath automatically. If additional libraries are needed, they should be added to this folder and then registered in **Project Structure** settings by doing `File > Project Structure... > Project Settings > Libraries`, and then registering a new Java library
    - `logs` - Initially not present. No source control. All log files are dumped here. 
    - `out` - Initially not present. No source control. All compiled `*.class` files are dumped here, for both Ant *compile-src* and IntelliJ IDEA builtin *Build* tasks
    - `spec` - Lexer and parser specification files
    - `src` - Application root source directory
    - `test` - Testing root source directory
        - `test/obj` - Compiled object files are saved here
        - `test/programs` - You can put your `*.mj` test programs here
-  Files
    - `build.xml` - Ant build target definitions
    - `ujavac.iml` - Java module description

## Java source packages

-  `rc.ac.bg.etf.pp1` - Main project package. Classes generated from parser and lexer specifications are placed here. Main visitor classes for syntactic and semantic pass should be placed here as well. Classes for testing also live in this package, but are placed in the `test` folder. This is done in order to separate the code meant for different purposes, but to still enable the use of generated classes that have package visibility
-  `rs.ac.bg.etf.pp1.ast` - Classes generated for AST description by the parser generator are placed here. This package is not present initially, before the `compile-src` is run
-  `rs.ac.bg.etf.pp1.exception` - Any exception classes required by the implementation are meant to be placed here
-  `rs.ac.bg.etf.pp1.util` - Utility package. Any utility classes are meant to be placed here
