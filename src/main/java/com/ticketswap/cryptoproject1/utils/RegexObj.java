package com.ticketswap.cryptoproject1.utils;

import java.util.regex.Pattern;

public class RegexObj {
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public RegexObj(String regexPattern, String description){
        this.regexPattern = Pattern.compile(regexPattern);
        this.description = description;
    }
    // the pattern to compile
    private Pattern regexPattern;
    /**
     * @return the regexPattern
     */
    public Pattern getRegexPattern() {
        return regexPattern;
    }
    /**
     * @param regexPattern the regexPattern to set
     */
    public void setRegexPattern(Pattern regexPattern) {
        this.regexPattern = regexPattern;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    // more human readable description / explanation
    private String description;
}
