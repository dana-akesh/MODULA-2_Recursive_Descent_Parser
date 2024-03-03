import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
// 1201112 - Dana Akesh
public class Main {
    static public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    static String s, str;
    static Scanner scanner;
    static String[] tokensArray;
    static int index = 0;

    // enum for tokens in the language, including reserved words.
    enum Tokens {
        // Special tokens
        // in bold are reserved words
        // or standard identifiers (library functions or procedures).
        MODULE("module"),
        BEGIN("begin"),
        END("end"),
        CONST("const"),
        VAR("var"),
        INTEGER("integer"),
        REAL("real"),
        CHAR("char"),
        PROCEDURE("procedure"),
        MOD("mod"),
        DIV("div"),
        READINT("readint"),
        READREAL("readreal"),
        READCHAR("readchar"),
        READLN("readln"),
        WRITEINT("writeint"),
        WRITEREAL("writereal"),
        WRITECHAR("writechar"),
        WRITELN("writeln"),
        IF("if"),
        THEN("then"),
        ELSE("else"),
        ELSEIF("elseif"),
        WHILE("while"),
        DO("do"),
        LOOP("loop"),
        UNTIL("until"),
        EXIT("exit"),
        CALL("call"),
        LPAREN("("),
        RPAREN(")"),
        PLUS("+"),
        MINUS("-"),
        ASSIGN(":="),
        MULTIPLY("*"),
        DIVIDE("/"),
        SEMICOLON(";"),
        COLON(":"),
        COMMA(","),
        LARGER_THAN(">"),
        LARGER_THAN_OR_EQUAL(">="),
        SMALLER_THAN("<"),
        SMALLER_THAN_OR_EQUAL("<="),
        EQUALS("="),
        OR_EQUALS("|="),
        DOT(".");

        // String representation of the token
        private final String s;

        // Constructor
        Tokens(String s) {
            this.s = s;
        }

        // toString method
        @Override
        public String toString() {
            return s;
        }
    }

    // Main function
    public static void main(String[] args) throws FileNotFoundException {
        // Read the input file
        System.out.println("**Welcome to the Dana Akesh's parser**");

        System.out.println("**Please enter the file name/path: ");
        Scanner input = new Scanner(System.in);
        String fileName = input.nextLine();
        File file = new File(fileName);

        // Check if the file exists
        if (!file.exists() || !file.isFile() || !file.canRead() || !file.getName().endsWith(".txt")) {
            System.err.println("File not found");
            System.exit(1);
        }
        scanner = new Scanner(file);
        scanner.useDelimiter("(\\s+)|(" + splitWithDelimiter() + ")");

        List<String> tokensList = new ArrayList<>(); // List to store the tokens

        while (scanner.hasNext()) {
            String symbol = nextSymbol();
            if (symbol != null) {
                tokensList.add(symbol); // add each token to the list
            }
        }

        // Convert list to array
        tokensArray = tokensList.toArray(new String[0]);

        // Print the tokens
        /*for (int i = 0; i < tokensArray.length; i++) {
            System.out.println(tokensArray[i]);
        }*/

        try {
            //System.out.println(tokensArray[index]);
            module_decl();
            // check if there are any tokens left
            if (index < tokensArray.length - 1) {
                throw new customParserException("Expected end of file but got " + tokensArray[index + 1].toString() + " instead.");
            }
            System.out.println("Parsing is done successfully");
            scanner.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            scanner.close();
            //System.out.println("Exception caught: " + e.getMessage());
        }

    }

    // Function to split the input string with the delimiters
    public static String splitWithDelimiter() {
        String delimiterPattern = Arrays.stream(Tokens.values())
                .map(Tokens::toString)
                .map(Pattern::quote) // Quote special characters
                .collect(Collectors.joining("|"));

        return String.format(WITH_DELIMITER, delimiterPattern);
    }


    // Function to escape the special characters
    public static String esCharacters(String inputString) {
        final String[] metaCharacters = {"\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "<", ">", "-", "&", "%"};

        for (String metaCharacter : metaCharacters) {
            if (inputString.contains(metaCharacter)) {
                inputString = inputString.replace(metaCharacter, "\\" + metaCharacter);
            }
        }
        return inputString;
    }

    // Function to get the next symbol from the input
    public static String nextSymbol() {
        String symbol = "";
        if (scanner.hasNext()) {
            symbol = scanner.next();
            s = symbol;
            //System.out.println(symbol + ", this symbol was in next");
            while (symbol.trim().length() < 1) {
                symbol = scanner.next();
            }
        }
        return symbol;
    }
// the first function to start with
// module-decl → module-heading declarations procedure-decl block name .

    public static void module_decl() throws customParserException {
        module_heading();
        declarations();
        procedure_decl();
        block();
        name();

        // check if the last token is a dot
        if (tokensArray[index].toString().equals(Tokens.DOT.toString())) {
            //System.out.println("Status: code parsed successfully");
        } else {
            throw new customParserException("Expected dot at the end of the file but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // module_heading function
    // module-heading → module name ;
    public static void module_heading() throws customParserException {
        String module = tokensArray[index].toString() + tokensArray[index + 1].toString();
        /*
        System.out.println("***********************");
        System.out.println(module);
        System.out.println("***********************");*/

        if (module.equals(Tokens.MODULE.toString())) {
            index = index + 2;
            System.out.println("Module Name: " + tokensArray[index].toString());

            // check if the next token is a valid name
            if (is_valid_name(tokensArray[index].toString())) {
                name();
            } else {
                throw new customParserException("Invalid name " + tokensArray[index].toString());
            }

            // check if the next token is a semicolon
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
            } else {
                throw new customParserException("Expected semicolon after the module name but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected module keyword at the beginning of the file but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // declarations function
    public static void declarations() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.CONST.toString())) {
            const_decl();
        }
        if (tokensArray[index].toString().equals(Tokens.VAR.toString())) {
            var_decl();
        }
    }

    // procedure_decl function
    public static void procedure_decl() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.PROCEDURE.toString())) {
            procedure_heading();
            declarations();
            block();
            name();
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
            } else {
                throw new customParserException("Expected semicolon after the procedure name but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected procedure keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // procedure_heading function
    // procedure-heading → procedure name ;
    public static void procedure_heading() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.PROCEDURE.toString())) {
            index++;
            if (is_valid_name(tokensArray[index].toString())) {
                name();
            } else {
                throw new customParserException("Invalid name " + tokensArray[index].toString());
            }
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
            } else {
                throw new customParserException("Expected semicolon after the procedure name but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected procedure keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // block function
    // block → begin stmt-list end
    public static void block() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.BEGIN.toString())) {
            index++;
            stmt_list();
            if (tokensArray[index].toString().equals(Tokens.END.toString())) {
                index++;
            } else {
                throw new customParserException("Expected end keyword after the begin keyword but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected begin keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // var_decl function
    // var-decl → var var-list | lambda
    public static void var_decl() throws customParserException {
        // first case: var var-list
        if (tokensArray[index].toString().equals(Tokens.VAR.toString())) {
            index++;
            var_list();
        }
        // second case: lambda, take follow
        else {
            // follow of var_decl function
            // case1: follow of var_decl function is the first of procedure_decl function
            if (tokensArray[index].toString().equals(Tokens.PROCEDURE.toString())) {
                index++;
                procedure_decl();
            }
            // case2: follow of var_decl function is the first of procedure_decl function
            else if (tokensArray[index].toString().equals(Tokens.BEGIN.toString())) {
                index++;
                block();
            } else {
                throw new customParserException("Expected var or procedure or begin keyword but got " + tokensArray[index].toString() + " instead.");
            }
        }
    }

    // var_list function
    // var-list → ( var-item ;)*
    public static void var_list() throws customParserException {
        if (is_valid_name(tokensArray[index].toString())) {
            var_item();
            while (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
                var_list();
            }
        }
    }

    // var_item function
    // var-item → name-list : data-type
    public static void var_item() throws customParserException {
        name_list();
        if (tokensArray[index].toString().equals(Tokens.COLON.toString())) {
            index++;
            data_type();
        } else {
            throw new customParserException("Expected colon after the name-list but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // stmt_list function
    // stmt-list → statement (; statement)*
    public static void stmt_list() throws customParserException {
        statement();
        while (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
            index++;
            statement();
        }
    }

    // statement function
    // statement → if-stmt | while-stmt | repeat-stmt | read-stmt | write-stmt | ass-stmt | call-stmt | exit-stmt | lambda
    public static void statement() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.IF.toString())) {
            if_stmt();
        } else if (tokensArray[index].toString().equals(Tokens.WHILE.toString())) {
            while_stmt();
        } else if (tokensArray[index].toString().equals(Tokens.LOOP.toString())) {
            repeat_stmt();
        } else if (tokensArray[index].toString().equals(Tokens.READINT.toString()) || (tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.READREAL.toString())
                || (tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.READCHAR.toString()) || (tokensArray[index].toString()).equals(Tokens.READLN.toString())) {
            read_stmt();
        } else if (tokensArray[index].toString().equals(Tokens.WRITEINT.toString()) || (tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.WRITEREAL.toString())
                || (tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.WRITECHAR.toString()) || (tokensArray[index].toString()).equals(Tokens.WRITELN.toString())) {
            write_stmt();
        } else if (tokensArray[index].toString().equals(Tokens.CALL.toString())) {
            call_stmt();
        } else if (tokensArray[index].toString().equals(Tokens.EXIT.toString())) {
            exit_stmt();
        } else if (is_valid_name(tokensArray[index].toString()) && tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            ass_stmt();
        } else {

        }
    }

    // ass-stmt function
    // ass-stmt → name := exp
    public static void ass_stmt() throws customParserException {
        if (is_valid_name(tokensArray[index].toString())) {
            name();
            String assign = tokensArray[index].toString() + tokensArray[index + 1].toString();
            if (assign.equals(Tokens.ASSIGN.toString())) {
                index = index + 2;
                exp();
            } else {
                throw new customParserException("Expected := but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected name but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // exp function
    // exp → term ( add-oper term )*
    public static void exp() throws customParserException {
        term();
        while (tokensArray[index].toString().equals(Tokens.PLUS.toString()) || tokensArray[index].toString().equals(Tokens.MINUS.toString())) {
            index++;
            term();
        }
    }

    // term function
    // term → factor (mul-oper factor)*
    public static void term() throws customParserException {
        factor();
        while (tokensArray[index].toString().equals(Tokens.MULTIPLY.toString()) || tokensArray[index].toString().equals(Tokens.DIVIDE.toString())
                || tokensArray[index].toString().equals(Tokens.DIV.toString()) || tokensArray[index].toString().equals(Tokens.MOD.toString())) {
            index++;
            factor();
        }
    }

    // factor function
    // factor → ( exp ) | name | value
    public static void factor() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
            index++;
            exp();
            if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                index++;
            } else {
                throw new customParserException("Expected right parenthesis after the expression but got " + tokensArray[index].toString() + " instead.");
            }
        } else if (is_valid_name(tokensArray[index].toString()) && tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            name();
        } else if (tokensArray[index].toString().matches("-?\\d+")) {
            integer_value();
        } else if (tokensArray[index].toString().matches("-?\\d+(\\.\\d+)?")) {
            real_value();
        } else {
            throw new customParserException("Expected name or value or expression but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // name function
    // letter ( letter | digit )*
    public static void name() throws customParserException {
        if (is_valid_name(tokensArray[index].toString())) {
            if (tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
                index++;
            } else {
                throw new customParserException("Invalid name " + tokensArray[index].toString());
            }
        }
    }

    // data_type function
    // data-type → integer | real | char
    public static void data_type() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.INTEGER.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.REAL.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.CHAR.toString())) {
            index++;
        } else {
            throw new customParserException("Expected data type but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // const_decl function
    // const-decl → const const-list | lambda
    public static void const_decl() throws customParserException {
        // first case: const const-list aka not lambda
        if (tokensArray[index].toString().equals(Tokens.CONST.toString())) {
            index++;
            const_list();
        }
        // second case: lambda, take follow
        else {
            // todo follow of const_decl function
            // case1: follow of const_decl function is the first of var_decl function
            if (tokensArray[index].toString().equals(Tokens.VAR.toString())) {
                index++;
                var_decl();
            }
            // case2: follow of const_decl function is the first of procedure_decl function if there is no var_decl function
            else if (tokensArray[index].toString().equals(Tokens.PROCEDURE.toString())) {
                index++;
                procedure_decl();
            }
            // case3: follow of const_decl function is the first of block function
            else if (tokensArray[index].toString().equals(Tokens.BEGIN.toString())) {
                index++;
                block();
            } else {
                throw new customParserException("Expected const or var or procedure or begin keyword but got " + tokensArray[index].toString() + " instead.");
            }
        }
    }


    // const_list function
    // const-list → (name = value ;)*
    public static void const_list() throws customParserException {
        name();
        if (tokensArray[index].toString().equals(Tokens.EQUALS.toString())) { // =
            index++;
            if (tokensArray[index + 1].toString().equals(Tokens.DOT.toString())) {
                str = tokensArray[index].toString() + tokensArray[index + 1].toString() + tokensArray[index + 2].toString();
                index = index + 2;
                value();
            } else {
                value();
            }
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
                if (tokensArray[index].toString().equals(Tokens.VAR.toString()) || tokensArray[index].toString().equals(Tokens.PROCEDURE.toString())) {
                    return;
                }
                const_list();
            } else {
                throw new customParserException("Expected semicolon after the value but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected equal sign after the name but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // value function
    // value → integer-value | real-value
    public static void value() throws customParserException {
        if (tokensArray[index].toString().matches("-?\\d+")) {
            integer_value();
        } else if (str.matches("-?\\d+(\\.\\d+)?")) {
            index++;
            real_value();
        } else {
            throw new customParserException("Expected integer or real value but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // integer_value function
    // integer-value → digit (digit)*
    public static void integer_value() throws customParserException {
        if (tokensArray[index].toString().matches("-?\\d+")) {
            index++;
        } else {
            throw new customParserException("Expected integer value but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // real_value function
    // real-value → digit (digit)* . digit (digit)*
    public static void real_value() throws customParserException {
        if (tokensArray[index].toString().matches("-?\\d+(\\.\\d+)?")) {
            index++;
        } else {
            throw new customParserException("Expected real value but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // while_stmt function
    // while-stmt → while condition do stmt-list end
    public static void while_stmt() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.WHILE.toString())) {
            index++;
            condition();
            if (tokensArray[index].toString().equals(Tokens.DO.toString())) {
                index++;
                stmt_list();
                if (tokensArray[index].toString().equals(Tokens.END.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected end keyword after the while statement but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected do keyword after the condition but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected while keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // repeat_stmt function
    // repeat-stmt → loop stmt-list until condition
    public static void repeat_stmt() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.LOOP.toString())) {
            index++;
            stmt_list();
            if (tokensArray[index].toString().equals(Tokens.UNTIL.toString())) {
                index++;
                condition();
            } else {
                throw new customParserException("Expected until keyword after the stmt-list but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected loop keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // read_stmt function
    // read-stmt → readint ( name-list )  | readreal ( name-list ) | readchar ( name-list ) | readln
    public static void read_stmt() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.READINT.toString())) {
            index++;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                name_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected left parenthesis after the readint keyword but got " + tokensArray[index].toString() + " instead.");
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.READREAL.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                name_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected left parenthesis after the readreal keyword but got " + tokensArray[index].toString() + " instead.");
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.READCHAR.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                name_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected left parenthesis after the readchar keyword but got " + tokensArray[index].toString() + " instead.");
            }
        } else if ((tokensArray[index].toString()).equals(Tokens.READLN.toString())) {
            index++;
        } else {
            throw new customParserException("Expected readint or readreal or readchar or readln keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // write_stmt function
    // write-stmt → writeint ( name_list ) | writereal ( name_list ) | writechar ( name_list ) | writeln
    public static void write_stmt() throws customParserException {
        if ((tokensArray[index].toString()).equals(Tokens.WRITEINT.toString())) {
            index++;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                write_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected left parenthesis after the writeint keyword but got " + tokensArray[index].toString() + " instead.");
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.WRITEREAL.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                write_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected left parenthesis after the writereal keyword but got " + tokensArray[index].toString() + " instead.");
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.WRITECHAR.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                write_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected left parenthesis after the writechar keyword but got " + tokensArray[index].toString() + " instead.");
            }
        } else if (tokensArray[index].toString().equals(Tokens.WRITELN.toString())) {
            index++;
        } else {
            throw new customParserException("Expected writeint or writereal or writechar or writeln keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // name_list function
    // name-list → name (, name)*
    public static void name_list() throws customParserException {
        name();
        while (tokensArray[index].toString().equals(Tokens.COMMA.toString())) {
            index++;
            name();
        }
    }

    // write_list function
    // write-list → write-item (, write-item)*
    public static void write_list() throws customParserException {
        write_item();
        while (tokensArray[index].toString().equals(Tokens.COMMA.toString())) {
            index++;
            write_item();
        }
    }

    // write_item function
    // write-item → name | value
    public static void write_item() throws customParserException {
        if (is_valid_name(tokensArray[index].toString()) && tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            name();
        } else if (tokensArray[index].toString().matches("-?\\d+")) {
            integer_value();
        } else if (tokensArray[index].toString().matches("-?\\d+(\\.\\d+)?")) {
            real_value();
        } else {
            throw new customParserException("Expected name or value but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // relational_oper function
    // relational-oper → < | > | = | <= | >= | |=
    public static void relational_oper() throws customParserException {
        if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.OR_EQUALS.toString())) { // |=
            index = index + 2;
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.SMALLER_THAN_OR_EQUAL.toString())) { // <=
            index = index + 2;
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.LARGER_THAN_OR_EQUAL.toString())) { // >=
            index = index + 2;
        } else if (tokensArray[index].toString().equals(Tokens.LARGER_THAN.toString())) { // >
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.SMALLER_THAN.toString())) { // <
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.EQUALS.toString())) { // =
            index++;
        } else { // error
            throw new customParserException("Expected relational operator but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // name-value function
    // name-value → name | value
    public static void name_value() throws customParserException {
        if (tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            index++;
        } else if (tokensArray[index].toString().matches("-?\\d+")) {
            index++;
        } else if (tokensArray[index].toString().matches("-?\\d+(\\.\\d+)?")) {
            index++;
        } else {
            throw new customParserException("Expected name or value but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // condition function
    // condition → name-value relational-oper name-value
    public static void condition() throws customParserException {
        name_value();
        relational_oper();
        name_value();
    }

    // call_stmt function
    // call-stmt → call name
    // name is procedure name
    public static void call_stmt() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.CALL.toString())) {
            index++;
            if (is_valid_name(tokensArray[index].toString())) {
                name();
                // todo: check is the procedure is declared before or not
                // function to check if the procedure is declared before or not
            } else {
                throw new customParserException("Invalid name " + tokensArray[index].toString());
            }


        } else {
            throw new customParserException("Expected call keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // exit_stmt function
    // exit-stmt → exit
    public static void exit_stmt() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.EXIT.toString())) {
            index++;
        } else {
            throw new customParserException("Expected exit keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // add_oper function
    // add-oper → + | -
    public static void add_oper() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.PLUS.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.MINUS.toString())) {
            index++;
        } else {
            throw new customParserException("Expected add operator but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // mul_oper function
    // mul-oper → * | / | mod | div
    public static void mul_oper() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.MULTIPLY.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.DIVIDE.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.MOD.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.DIV.toString())) {
            index++;
        } else {
            throw new customParserException("Expected mul operator but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // if_stmt function
    // if-stmt → if condition then stmt-list elseif-part else-part end
    public static void if_stmt() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.IF.toString())) {
            index++;
            condition();
            if (tokensArray[index].toString().equals(Tokens.THEN.toString())) {
                index++;
                stmt_list();
                elseif_part();
                else_part(); // lambda or else-part todo ask
                if (tokensArray[index].toString().equals(Tokens.END.toString())) {
                    index++;
                } else {
                    throw new customParserException("Expected end keyword after the if statement but got " + tokensArray[index].toString() + " instead.");
                }
            } else {
                throw new customParserException("Expected then keyword after the condition but got " + tokensArray[index].toString() + " instead.");
            }
        } else {
            throw new customParserException("Expected if keyword but got " + tokensArray[index].toString() + " instead.");
        }
    }

    // elseif_part function
    // elseif-part → (elseif condition then stmt-list)*
    public static void elseif_part() throws customParserException {
        if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.ELSEIF.toString())) {
            while ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.ELSEIF.toString())) {
                index = index + 2;
                condition();
                if (tokensArray[index].toString().equals(Tokens.THEN.toString())) {
                    index++;
                    stmt_list();
                } else {
                    throw new customParserException("Expected then keyword after the condition but got " + tokensArray[index].toString() + " instead.");
                }
            }
        }
    }

    // else_part function
    // else-part → else stmt-list | lambda
    public static void else_part() throws customParserException {
        if (tokensArray[index].toString().equals(Tokens.ELSE.toString())) {
            index++;
            stmt_list();
        }
        // lambda
        // follow of else_part function is the first of end keyword
    }

    // is_valid_name function, to check if the name is valid or not or if it's a reserved word
    public static boolean is_valid_name(String s) {
        boolean isValid = false;
        if (index >= tokensArray.length - 1) {
            return false;
        }
        if (!(tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.MODULE.toString()) && !tokensArray[index].equals(Tokens.CONST.toString()) &&
                !tokensArray[index].equals(Tokens.VAR.toString()) && !tokensArray[index].equals(Tokens.INTEGER.toString()) &&
                !tokensArray[index].equals(Tokens.BEGIN.toString()) && !tokensArray[index].equals(Tokens.DIV.toString()) &&
                !tokensArray[index].equals(Tokens.END.toString()) && !tokensArray[index].equals(Tokens.REAL.toString()) &&
                !tokensArray[index].equals(Tokens.CHAR.toString()) && !tokensArray[index].equals(Tokens.IF.toString()) &&
                !tokensArray[index].equals(Tokens.THEN.toString()) && !tokensArray[index].equals(Tokens.PROCEDURE.toString()) &&
                !tokensArray[index].equals(Tokens.ELSE.toString()) && !tokensArray[index].equals(Tokens.LOOP.toString()) &&
                !tokensArray[index].equals(Tokens.DO.toString()) && !tokensArray[index].equals(Tokens.RPAREN.toString()) &&
                !tokensArray[index].equals(Tokens.LPAREN.toString()) && !tokensArray[index].equals(Tokens.PLUS.toString()) &&
                !tokensArray[index].equals(Tokens.MINUS.toString()) && !tokensArray[index].equals(Tokens.ASSIGN.toString()) &&
                !tokensArray[index].equals(Tokens.MULTIPLY.toString()) && !tokensArray[index].equals(Tokens.DIVIDE.toString()) &&
                !tokensArray[index].equals(Tokens.MOD.toString()) && !tokensArray[index].equals(Tokens.COMMA.toString()) &&
                !tokensArray[index].equals(Tokens.COLON.toString()) && !tokensArray[index].equals(Tokens.SEMICOLON.toString()) &&
                !tokensArray[index].equals(Tokens.SMALLER_THAN.toString()) && !tokensArray[index].equals(Tokens.LARGER_THAN.toString()) &&
                !tokensArray[index].equals(Tokens.EQUALS.toString()) && !tokensArray[index].equals(Tokens.DOT.toString()) &&
                !tokensArray[index].equals(Tokens.OR_EQUALS.toString()) && !tokensArray[index].equals(Tokens.READINT.toString()) &&
                !(tokensArray[index].toString()).equals(Tokens.READREAL.toString()) && !(tokensArray[index].toString()).equals(Tokens.READCHAR.toString()) &&
                !(tokensArray[index].toString()).equals(Tokens.READLN.toString()) && !tokensArray[index].equals(Tokens.WRITEINT.toString()) &&
                !(tokensArray[index].toString()).equals(Tokens.WRITEREAL.toString()) && !(tokensArray[index].toString()).equals(Tokens.WRITECHAR.toString()) &&
                !(tokensArray[index].toString()).equals(Tokens.WRITELN.toString()) && !tokensArray[index].equals(Tokens.THEN.toString()) &&
                !tokensArray[index].equals(Tokens.ELSE.toString()) && !(tokensArray[index].toString()).equals(Tokens.ELSEIF.toString()) &&
                !tokensArray[index].equals(Tokens.WHILE.toString()) && !tokensArray[index].equals(Tokens.UNTIL.toString()) &&
                !tokensArray[index].equals(Tokens.EXIT.toString()) && !tokensArray[index].equals(Tokens.CALL.toString()) &&
                !tokensArray[index].equals(Tokens.LARGER_THAN_OR_EQUAL.toString()) && !tokensArray[index].equals(Tokens.SMALLER_THAN_OR_EQUAL.toString())) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    private static class customParserException extends Exception {
        public customParserException(String message) {
            super(message);
        }
    }
}




