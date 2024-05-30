package com.backend.BackendJWT.Validaciones;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CelularValidation {
    //validacion de numero de celular chileno
    public static Boolean ValidateCelular(String celular){
        String regex = "^(\\+)?(569|9)?\\s?\\d{8}";
        Pattern pattern = Pattern.compile(regex);
        //compara patron de regex con el celular recibido
        Matcher matcher = pattern.matcher(celular);
        System.out.println(celular + " : " + matcher.matches());

        return matcher.matches();

    }
}
//EXPLICACION REGEX:

//^: indicador para que la regla o patron que le sigue, se aplique solo al principio de la cadena regex.
// (\\+569|9) :
// Primero, los () son indicadores de un patron, es decir: (ab) busca lo que contenga ab como: ababab, ab1da, etc.
// Segundo, en java el \\ es indicador de que lo que le sigue, se debe tomar literal.
// Tercero, el operador | indica que puede ser el valor anterior o posterior a el.
// ? : indica que la regla anterior a este simbolo, es opcional. Puede estar como puede no estar.
// s : refiere a un espacio en blanco.
// d : refiere a un digito.
// {n} refiere a la cantidad de n veces que repite el patron que le antepone.