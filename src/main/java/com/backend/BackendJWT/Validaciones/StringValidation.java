package com.backend.BackendJWT.Validaciones;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValidation {

    public static Boolean IsEmptyOrNull(String text) {
        if(text == null || text.trim().equals("")) {
            return true;
        } else{
            return false;
        }
    }

    public static Boolean IsOnlyAlphabetic(String text){
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
    public static Boolean IsOnlyNumeric(String text){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }
}