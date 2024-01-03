import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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
        READIN("readin"),
        WRITEINT("writeint"),
        WRITEREAL("writereal"),
        WRITECHAR("writechar"),
        WRITEIN("writein"),
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
        scanner.useDelimiter("(\\s+)|(" + splitWithDelimiter() + ")"); // split the input with the delimiters and the white spaces between them
        // and remove the white spaces from the tokens array (the tokens array will contain only the tokens)
        // (\\s+) is for the white spaces and (splitWithDelimiter()) is for the delimiters (the special characters)
        // and the | is for the OR operator between them (\\s+)|(" + splitWithDelimiter() + ") is for the white spaces and the delimiters between them (\\s+)|(" + splitWithDelimiter() + ") is for the white spaces and the delimiters between them

        List<String> tokensList = new ArrayList<>(); // List to store the tokens

        while (scanner.hasNext()) {
            String symbol = nextSymbol();
            if (symbol != null) {
                tokensList.add(symbol); // add each token to the list
            }
        }

        // Convert list to array
        tokensArray = tokensList.toArray(new String[0]);
        /*اللي رح نشتغل فيها هي tokensArray*/
        for (String token : tokensList) {
            System.out.println(token);
        }
        try {
            System.out.println(tokensArray[index]);
            /*هون بتحطي اول فنكشن رح يبلش فيه البرنامج عندك*/
            // project_declaration();
            System.out.println("Parsing is done successfully");
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to split the input string with the delimiters
    private static String splitWithDelimiter() {
        return String.format(WITH_DELIMITER, esCharacters(
                        Arrays.stream(Tokens.values())
                                .map(Tokens::toString)
                                .collect(Collectors.joining("|"))
                )
        );
    }

    // Function to escape the special characters
    private static String esCharacters(String inputString) {
        final String[] metaCharacters = {"\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "<", ">", "-", "&", "%"};

        for (String metaCharacter : metaCharacters) {
            if (inputString.contains(metaCharacter)) {
                inputString = inputString.replace(metaCharacter, "\\" + metaCharacter);
            }
        }
        return inputString;
    }

    // Function to get the next symbol from the input
    private static String nextSymbol() {
        String symbol = "";
        if (scanner.hasNext()) {
            symbol = scanner.next();
            s = symbol;
//            System.out.println(symbol + ", this symbol was in next");
            while (symbol.trim().length() < 1) {
                symbol = scanner.next();
            }
        }

        return symbol;

    }



    /*  مثال عن كيف رح تكون الفنكشنز*/
    //    public static void project_declaration() throws Exception {

//        project_def();  >>استدعاء فنكشن تاني
//        if (tokensArray[index].toString().equals(Tokens.DOT.toString())) {   >>النقطة بتعبر عن نهاية الكود وهي الجملة بتتطبق في نهاية البرنامج
//            System.out.println("project_declaration parsed successfully");
//        } else {
//            System.out.println("Expected dot at the end of the file but got " + tokensArray[index].toString()+" instead.");
//        }
//    }


//    public static void project_def() throws Exception {
//        project_heading();
//        declarations();
//        compound_stmt();
//    }


    //    public static void project_heading() throws Exception {
    //لفحص أول كلمة في البرنامج وهي project
    // عندك بدها تكون module
//        if (tokensArray[index].equals(Tokens.PROJECT.toString())) {
//            index++; // للإنتقال للكلمة التانية وبنفحصها//
    //               هون المفروض نستدعي فنكشن لفحص الإسم إزا هو صحيح. بحيث ما يكون الإسم كلمة من الكلمات المعرفة مسبقا مثلا
//            } else {
//                System.out.println("project name isn't a valid name");
//            }
//
//        } else {
//            System.out.println("Expected project at the beginning of the file");
//        }
//
//    }


    /* لفحص إزا كان الأسماء والأرقام صحيحة*/
//    public static boolean is_valid_name(String s) {
//        if (!tokensArray[index].equals(Tokens.PROJECT.toString()) && !tokensArray[index].equals(Tokens.CONST.toString()) && !tokensArray[index].equals(Tokens.VAR.toString()) && !tokensArray[index].equals(Tokens.INT.toString()) && !tokensArray[index].equals(Tokens.ROUTINE.toString()) && !tokensArray[index].equals(Tokens.START.toString()) && !tokensArray[index].equals(Tokens.END.toString()) && !tokensArray[index].equals(Tokens.INPUT.toString()) && !tokensArray[index].equals(Tokens.OUTPUT.toString()) && !tokensArray[index].equals(Tokens.IF.toString()) && !tokensArray[index].equals(Tokens.THEN.toString()) && !tokensArray[index].equals(Tokens.ENDIF.toString()) && !tokensArray[index].equals(Tokens.ELSE.toString()) && !tokensArray[index].equals(Tokens.LOOP.toString()) && !tokensArray[index].equals(Tokens.DO.toString()) && !tokensArray[index].equals(Tokens.RPAREN.toString()) && !tokensArray[index].equals(Tokens.LPAREN.toString()) && !tokensArray[index].equals(Tokens.PLUS.toString()) && !tokensArray[index].equals(Tokens.MINUS.toString()) && !tokensArray[index].equals(Tokens.ASSIGN.toString()) && !tokensArray[index].equals(Tokens.MULTIPLY.toString()) && !tokensArray[index].equals(Tokens.DIVIDE.toString()) && !tokensArray[index].equals(Tokens.MOD.toString()) && !tokensArray[index].equals(Tokens.COMMA.toString()) && !tokensArray[index].equals(Tokens.COLON.toString()) && !tokensArray[index].equals(Tokens.SEMICOLON.toString()) && !tokensArray[index].equals(Tokens.SMALLER_THAN.toString()) && !tokensArray[index].equals(Tokens.LARGER_THAN.toString()) && !tokensArray[index].equals(Tokens.EQUALS.toString()) && !tokensArray[index].equals(Tokens.DOT.toString()) && !tokensArray[index].equals(Tokens.NOT.toString())) {
//            isValid = true;
//        } else {
//            isValid = false;
//        }
//        return isValid;
//
//    }
//
//    public static boolean is_valid_integer(String str) {
//        return str.matches("-?\\d+"); //return true is an integer
//    }
}


