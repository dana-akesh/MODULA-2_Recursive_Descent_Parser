import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    static public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    static String s;
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
        scanner = new Scanner(new File("input.txt"));
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
            System.out.println("Parsing is done successfully");
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception caught: " + e.getMessage());
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

    public static void module_decl() {
        module_heading();
        declarations();
        procedure_decl();
        block();
        name();

        // check if the last token is a dot
        if (tokensArray[index].toString().equals(Tokens.DOT.toString())) {
            //System.out.println("Status: code parsed successfully");
        } else {
            System.out.println("Expected dot at the end of the file but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // module_heading function
    // module-heading → module name ;
    public static void module_heading() {
        String module = tokensArray[index].toString() + tokensArray[index + 1].toString();
        /*
        System.out.println("***********************");
        System.out.println(module);
        System.out.println("***********************");*/

        if (module.equals(Tokens.MODULE.toString())) {
            index = index + 2;
            System.out.println("Module Name: " + tokensArray[index].toString());

            // check if the next token is a valid name
            if (is_valid_name(tokensArray[index].toString()))
                name();
            else {
                System.out.println("Invalid name " + tokensArray[index].toString());
                System.exit(1);
            }

            // check if the next token is a semicolon
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
            } else {
                System.out.println("Expected semicolon after the module name but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected module keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // declarations function
    public static void declarations() {
        if (tokensArray[index].toString().equals(Tokens.CONST.toString())) {
            const_decl();
        }
        if (tokensArray[index].toString().equals(Tokens.VAR.toString())) {
            var_decl();
        }
    }

    // procedure_decl function
    public static void procedure_decl() {
        if (tokensArray[index].toString().equals(Tokens.PROCEDURE.toString())) {
            procedure_heading();
            declarations();
            block();
            name();
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
            } else {
                System.out.println("Expected semicolon after the procedure name but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected procedure keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // procedure_heading function
    // procedure-heading → procedure name ;
    public static void procedure_heading() {
        if (tokensArray[index].toString().equals(Tokens.PROCEDURE.toString())) {
            index++;
            if (is_valid_name(tokensArray[index].toString())) {
                name();
            } else {
                System.out.println("Invalid name " + tokensArray[index].toString());
                System.exit(1);
            }
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
            } else {
                System.out.println("Expected semicolon after the procedure name but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Invalid name " + tokensArray[index].toString());
            System.exit(1);
        }
    }

    // block function
    // block → begin stmt-list end
    public static void block() {
        if (tokensArray[index].toString().equals(Tokens.BEGIN.toString())) {
            index++;
            stmt_list();
            if (tokensArray[index].toString().equals(Tokens.END.toString())) {
                index++;
            } else {
                System.out.println("Expected end keyword after the begin keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected begin keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // var_decl function
    // var-decl → var var-list | lambda
    public static void var_decl() {
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
                System.out.println("Expected var or procedure or begin keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        }
    }

    // var_list function
    // var-list → ( var-item ;)*
    public static void var_list() {
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
    public static void var_item() {
        name_list();
        if (tokensArray[index].toString().equals(Tokens.COLON.toString())) {
            index++;
            data_type();
        } else {
            System.out.println("Expected colon after the name-list but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // stmt_list function
    // stmt-list → statement (; statement)*
    public static void stmt_list() {
        statement();
        while (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
            index++;
            statement();
        }
    }

    // statement function
    // statement → if-stmt | while-stmt | repeat-stmt | read-stmt | write-stmt | ass-stmt | call-stmt | exit-stmt | lambda
    public static void statement() {
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
    public static void ass_stmt() {
        if (is_valid_name(tokensArray[index].toString())) {
            name();
            String assign = tokensArray[index].toString() + tokensArray[index + 1].toString();
            if (assign.equals(Tokens.ASSIGN.toString())) {
                index = index + 2;
                exp();
            } else {
                System.out.println("Expected := but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected name but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // exp function
    // exp → term ( add-oper term )*
    public static void exp() {
        term();
        while (tokensArray[index].toString().equals(Tokens.PLUS.toString()) || tokensArray[index].toString().equals(Tokens.MINUS.toString())) {
            index++;
            term();
        }
    }

    // term function
    // term → factor (mul-oper factor)*
    public static void term() {
        factor();
        while (tokensArray[index].toString().equals(Tokens.MULTIPLY.toString()) || tokensArray[index].toString().equals(Tokens.DIVIDE.toString())
                || tokensArray[index].toString().equals(Tokens.DIV.toString()) || tokensArray[index].toString().equals(Tokens.MOD.toString())) {
            index++;
            factor();
        }
    }

    // factor function
    // factor → ( exp ) | name | value
    public static void factor() {
        if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
            index++;
            exp();
            if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                index++;
            } else {
                System.out.println("Expected right parenthesis after the expression but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else if (is_valid_name(tokensArray[index].toString()) && tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            name();
        } else if (tokensArray[index].toString().matches("[0-9]+")) {
            integer_value();
        } else if (tokensArray[index].toString().matches("[0-9]+.[0-9]+")) {
            real_value();
        } else {
            System.out.println("Expected name or value or expression but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // name function
    // letter ( letter | digit )*
    public static void name() {
        if (tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*") && is_valid_name(tokensArray[index].toString())) {
            index++;
        } else {
            System.out.println("Expected name but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // data_type function
    // data-type → integer | real | char
    public static void data_type() {
        if (tokensArray[index].toString().equals(Tokens.INTEGER.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.REAL.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.CHAR.toString())) {
            index++;
        } else {
            System.out.println("Expected data type but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // const_decl function
    // const-decl → const const-list | lambda
    public static void const_decl() {
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
                //todo
                procedure_decl();
            }
            // case3: follow of const_decl function is the first of block function
            else if (tokensArray[index].toString().equals(Tokens.BEGIN.toString())) {
                index++;
                block();
            } else {
                System.out.println("Expected const or var or procedure or begin keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        }
    }


    // const_list function
    // const-list → (name = value ;)*
    public static void const_list() {
        name();
        if (tokensArray[index].toString().equals(Tokens.EQUALS.toString())) {
            index++;
            value();
            if (tokensArray[index].toString().equals(Tokens.SEMICOLON.toString())) {
                index++;
                if (tokensArray[index].toString().equals(Tokens.VAR.toString())) {
                    return;
                }
                const_list();
            } else {
                System.out.println("Expected semicolon after the value but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected assign operator after the name but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // value function
    // value → integer-value | real-value
    public static void value() {
        if (tokensArray[index].toString().matches("[0-9]+")) {
            integer_value();
        } else if (tokensArray[index].toString().matches("[0-9]+.[0-9]+")) {
            index++;
            real_value();
        } else {
            System.out.println("Expected integer or real value but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // integer_value function
    // integer-value → digit (digit)*
    public static void integer_value() {
        if (tokensArray[index].toString().matches("[0-9]+")) {
            index++;
        } else {
            System.out.println("Expected integer value but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // real_value function
    // real-value → digit (digit)* . digit (digit)*
    public static void real_value() {
        if (tokensArray[index].toString().matches("[0-9]+.[0-9]+")) {
            index++;
        } else {
            System.out.println("Expected real value but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // while_stmt function
    // while-stmt → while condition do stmt-list end
    public static void while_stmt() {
        if (tokensArray[index].toString().equals(Tokens.WHILE.toString())) {
            index++;
            condition();
            if (tokensArray[index].toString().equals(Tokens.DO.toString())) {
                index++;
                stmt_list();
                if (tokensArray[index].toString().equals(Tokens.END.toString())) {
                    index++;
                } else {
                    System.out.println("Expected end keyword after the while statement but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected do keyword after the condition but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected while keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // repeat_stmt function
    // repeat-stmt → loop stmt-list until condition
    public static void repeat_stmt() {
        if (tokensArray[index].toString().equals(Tokens.LOOP.toString())) {
            index++;
            stmt_list();
            if (tokensArray[index].toString().equals(Tokens.UNTIL.toString())) {
                index++;
                condition();
            } else {
                System.out.println("Expected until keyword after the stmt-list but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected loop keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // read_stmt function
    // read-stmt → readint ( name-list )  | readreal ( name-list ) | readchar ( name-list ) | readln
    public static void read_stmt() {
        if (tokensArray[index].toString().equals(Tokens.READINT.toString())) {
            index++;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                name_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    System.out.println("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected left parenthesis after the readint keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.READREAL.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                name_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    System.out.println("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected left parenthesis after the readreal keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.READCHAR.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                name_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    System.out.println("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected left parenthesis after the readchar keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else if ((tokensArray[index].toString()).equals(Tokens.READLN.toString())) {
            index++;
        } else {
            System.out.println("Expected readint or readreal or readchar or readln keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // write_stmt function
    // write-stmt → writeint ( name_list ) | writereal ( name_list ) | writechar ( name_list ) | writeln
    public static void write_stmt() {
        if ((tokensArray[index].toString()).equals(Tokens.WRITEINT.toString())) {
            index++;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                write_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    System.out.println("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected left parenthesis after the writeint keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.WRITEREAL.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                write_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    System.out.println("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected left parenthesis after the writereal keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.WRITECHAR.toString())) {
            index = index + 2;
            if (tokensArray[index].toString().equals(Tokens.LPAREN.toString())) {
                index++;
                write_list();
                if (tokensArray[index].toString().equals(Tokens.RPAREN.toString())) {
                    index++;
                } else {
                    System.out.println("Expected right parenthesis after the namelist but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected left parenthesis after the writechar keyword but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else if (tokensArray[index].toString().equals(Tokens.WRITELN.toString())) {
            index++;
        } else {
            System.out.println("Expected writeint or writereal or writechar or writeln keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // name_list function
    // name-list → name (, name)*
    public static void name_list() {
        name();
        while (tokensArray[index].toString().equals(Tokens.COMMA.toString())) {
            index++;
            name();
        }
    }

    // write_list function
    // write-list → write-item (, write-item)*
    public static void write_list() {
        write_item();
        while (tokensArray[index].toString().equals(Tokens.COMMA.toString())) {
            index++;
            write_item();
        }
    }

    // write_item function
    // write-item → name | value
    public static void write_item() {
        if (is_valid_name(tokensArray[index].toString()) && tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            name();
        } else if (tokensArray[index].toString().matches("[0-9]+")) {
            integer_value();
        } else if (tokensArray[index].toString().matches("[0-9]+.[0-9]+")) {
            real_value();
        } else {
            System.out.println("Expected name or value but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // relational_oper function
    // relational-oper → < | > | = | <= | >= | |=
    public static void relational_oper() {
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
            System.out.println("Expected relational operator but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // name-value function
    // name-value → name | value
    public static void name_value() {
        if (tokensArray[index].toString().matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            index++;
        } else if (tokensArray[index].toString().matches("[0-9]+")) {
            index++;
        } else if (tokensArray[index].toString().matches("[0-9]+.[0-9]+")) {
            index++;
        } else {
            System.out.println("Expected name or value but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // condition function
    // condition → name-value relational-oper name-value
    public static void condition() {
        name_value();
        relational_oper();
        name_value();
    }

    // call_stmt function
    // call-stmt → call name
    // name is procedure name
    public static void call_stmt() {
        if (tokensArray[index].toString().equals(Tokens.CALL.toString())) {
            index++;
            if (is_valid_name(tokensArray[index].toString())) {
                name();
                // todo: check is the procedure is declared before or not
                // function to check if the procedure is declared before or not
            } else {
                System.out.println("Invalid name " + tokensArray[index].toString());
                System.exit(1);
            }


        } else {
            System.out.println("Expected call keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // exit_stmt function
    // exit-stmt → exit
    public static void exit_stmt() {
        if (tokensArray[index].toString().equals(Tokens.EXIT.toString())) {
            index++;
        } else {
            System.out.println("Expected exit keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // add_oper function
    // add-oper → + | -
    public static void add_oper() {
        if (tokensArray[index].toString().equals(Tokens.PLUS.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.MINUS.toString())) {
            index++;
        } else {
            System.out.println("Expected add operator but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // mul_oper function
    // mul-oper → * | / | mod | div
    public static void mul_oper() {
        if (tokensArray[index].toString().equals(Tokens.MULTIPLY.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.DIVIDE.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.MOD.toString())) {
            index++;
        } else if (tokensArray[index].toString().equals(Tokens.DIV.toString())) {
            index++;
        } else {
            System.out.println("Expected mul operator but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // if_stmt function
    // if-stmt → if condition then stmt-list elseif-part else-part end
    public static void if_stmt() {
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
                    System.out.println("Expected end keyword after the if statement but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            } else {
                System.out.println("Expected then keyword after the condition but got " + tokensArray[index].toString() + " instead.");
                System.exit(1);
            }
        } else {
            System.out.println("Expected if keyword but got " + tokensArray[index].toString() + " instead.");
            System.exit(1);
        }
    }

    // elseif_part function
    // elseif-part → (elseif condition then stmt-list)*
    public static void elseif_part() {
        if ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.ELSEIF.toString())) {
            while ((tokensArray[index].toString() + tokensArray[index + 1].toString()).equals(Tokens.ELSEIF.toString())) {
                index = index + 2;
                condition();
                if (tokensArray[index].toString().equals(Tokens.THEN.toString())) {
                    index++;
                    stmt_list();
                } else {
                    System.out.println("Expected then keyword after the condition but got " + tokensArray[index].toString() + " instead.");
                    System.exit(1);
                }
            }
        }
    }

    // else_part function
    // else-part → else stmt-list | lambda
    public static void else_part() {
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
}




