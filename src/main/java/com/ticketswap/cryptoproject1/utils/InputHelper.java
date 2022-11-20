package com.ticketswap.cryptoproject1.utils;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHelper {
    // declare array of commands to check against, can add to this easily
    public static final String[] stringsToCheck = { "select", "drop", "from",
            "exec", "exists", "update", "delete", "insert", "cast", "http",
            "sql", "null", "like", "mysql", "()", "information_schema",
            "sleep", "version", "join", "declare", "having", "signed", "alter",
            "union", "where", "create", "shutdown", "grant", "privileges" };

    public static RegexObj regex1 = new RegexObj("(/\\*).*(\\*/)",
            "Found /* and */");

    // -- at the end
    public static RegexObj regex2 = new RegexObj("(--.*)$", "-- at end of sql");

    // ; and at least one " or '
    public static RegexObj regex3 = new RegexObj(";+\"+'",
            "One or more ; and at least one \" or '");

    // two or more "
    public static RegexObj regex4 = new RegexObj("\"{2,}+", "Two or more \"");

    // two or more '
    public static RegexObj regex5 = new RegexObj("'{2,}+", "Two or more '");

    // anydigit=anydigit
    public static RegexObj regex6 = new RegexObj("\\d=\\d", "anydigit=anydigit");

    // two or more white spaces in a row
    public static RegexObj regex7 = new RegexObj("(\\s\\s)+",
            "two or more white spaces in a row");

    // # at the end
    public static RegexObj regex8 = new RegexObj("(#.*)$", "# at end of sql");

    // two or more %
    public static RegexObj regex9 = new RegexObj("%{2,}+",
            "Two or more \\% signs");

    // admin and one of [; ' " =] before or after admin
    public static RegexObj regex10 = new RegexObj(
            "([;'\"\\=]+.*(admin.*))|((admin.*).*[;'\"\\=]+)",
            "admin (and variations like administrator) and one of [; ' \" =] before or after admin");

    // ASCII in hex
    public static RegexObj regex11 = new RegexObj("%+[0-7]+[0-9|A-F]+",
            "ASCII Hex");

    // declare array to hold each regex, can add to this easily
    public static final RegexObj[] regexes = { regex1, regex2, regex3, regex4,
            regex5, regex6, regex7, regex8, regex9, regex10, regex11 };


    public static boolean filterSqlInjection(String input) {
        return !sqlRegexChecker(input) && !sqlStringChecker(input);
    }

    public static boolean sqlStringChecker(String sqlToCheck) {
        boolean pass = false;
        // convert to lower case to handle obfuscation with mixed upper and
        // lower case
        sqlToCheck = sqlToCheck.toLowerCase();
        // for each string in stringsToCheck
        for (String command : stringsToCheck) {
            if (sqlToCheck.contains(command)) {
                System.out.printf("SQL string found (%s), predicted label = 1\n",
                                command);
                if (!pass) {
                    pass = true;
                }
            }
        }
        return pass;
    }


    public static boolean validateEmail(final String email){

        boolean isValid = false;

        try{
            Pattern p = Pattern.compile(RegexObj.EMAIL_REGEX);
            Matcher m = p.matcher(email);
            isValid = m.find();
        } catch (NullPointerException e){
            System.out.println("Email is null");
        }
        return isValid;
    }
    public static boolean sqlRegexChecker(String sqlToCheck) {
        // bool for each regex
        boolean pass;
        // bool to return overall
        boolean overall = false;
        Matcher matcher;
        // convert to lower case to handle obfuscation with mixed upper and
        // lower case
        sqlToCheck = sqlToCheck.toLowerCase();
        // regex checking
        for (RegexObj regex : regexes) {
            // check sqlToCheck vs regex, if pattern returns i.e. regex returns
            // true
            matcher = regex.getRegexPattern().matcher(sqlToCheck);
            pass = matcher.find();
            if (pass) {
                System.out.printf("Malicious input found via regex (%s), predicted label = 1\n",
                                regex.getDescription());
            }
            // if a regex returns true for the first time (i.e. overall is still
            // false), then make overall true
            if ((pass) && (!overall)) {
                overall = true;
            }
        }
        return overall;
    }
    public static String getStringInput(String message) {
        System.out.print(message);
        Scanner scanner = new Scanner(System.in);
        try {
            String inputString = scanner.nextLine();
            boolean valid = filterSqlInjection(inputString);
            if (valid) {
                return inputString;
            } else {
                System.out.println("Invalid input");
                return getStringInput(message);
            }
        } catch (Exception e) {
            System.out.println("Invalid input, please try again");
            return getStringInput(message);
        }
    }

    public static int getIntInput(String input) {
        System.out.print(input);
        Scanner scanner = new Scanner(System.in);
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input, please try again");
            return getIntInput(input);
        }
    }

    public static LocalDate getLocalDateInput(String input) {
        System.out.print(input);
        Scanner scanner = new Scanner(System.in);
        try {
            return LocalDate.parse(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input, please try again");
            return getLocalDateInput(input);
        }
    }

    public static double getDoubleInput(String input) {
        System.out.print(input);
        Scanner scanner = new Scanner(System.in);
        try {
            return scanner.nextDouble();
        } catch (Exception e) {
            System.out.println("Invalid input, please try again");
            return getDoubleInput(input);
        }
    }
}
