package com.backend.BackendJWT.Validaciones;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class CorreoValidation {
    public static boolean validacionCorreo(String correo) {
        // Expresión regex que verifica el formato del correo electrónico
        String regx = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$";
        Pattern pattern = Pattern.compile(regx);

        // Create instance of matcher
        Matcher matcher = pattern.matcher(correo);

        // Imprimir resultado de la validación
        System.out.println(correo + " : " + matcher.matches());

        return matcher.matches();
    }
}
//Pruebas de validacion:
//example@domain.com : true
//
//exampletwo@domain.com : true
//
//12@domain.com : true
//
//@helloworld.com : false