package com.backend.BackendJWT.Validaciones;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValidation {

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
    //con espacios blancos opcionales
    public static Boolean IsOnlyAlphaNumeric(String text){
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9\\s]+$");

        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }
    //con espacios blancos opcionales
    public static Boolean IsOnlyAlphabeticAndBlank(String text){
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z\\s]+$");

        Matcher matcher = pattern.matcher(text);
        if(matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }

    public static Boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}$");
        Matcher matcher = pattern.matcher(password);
        if(matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }
    }
