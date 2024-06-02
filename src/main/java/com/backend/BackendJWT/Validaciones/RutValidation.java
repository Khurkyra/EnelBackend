package com.backend.BackendJWT.Validaciones;

import com.backend.BackendJWT.Models.DTO.AuthResponse;
import com.backend.BackendJWT.Models.DTO.ValidationResponse;

public class RutValidation {

    public static String validaFormato(String rut) {
        if(rut.matches("^\\d{7,8}-[0-9Kk]$")){
            return "rut valido";
        //Elimina puntos y guiones del formato original
        //String rutSinFormato = rut.replaceAll("[.-]", "");
        //Verifica si el RUT tiene el formato correcto (7-8 dígitos y un dígito verificador)
        //if (rutSinFormato.matches("\\d{7,9}[0-9kK]")) {

        } else {
            return "El RUT no tiene el formato válido. Debe tener entre 8 a 9 digitos, puntos, guion y digito verificador";
        }
    }


    //Funcion principal que valida y verifica RUT en base al modulo 11.
    public static ValidationResponse validacionModule11(String rut) {
        String validacionFormato = validaFormato(rut);
        if (validacionFormato.equals("rut valido")) {
            String rutSinFormato = rut.replaceAll("[.-]", "");
            //Se separa el RUT.
            //Obtiene RUT.
            String numeroRUT = rutSinFormato.substring(0, rutSinFormato.length() - 1);
            //Obtiene DV
            String dv = rutSinFormato.substring(rutSinFormato.length() - 1);
            //Obtiene DV del Modulo 11.
            String dvEsperado = calcularDigitoVerificadorEsperado(numeroRUT);
            //Compara DV de Modulo 11 con el proporcionado.
            System.out.println("dvEsperado:" + dvEsperado + "dv entregado: " + dv);
            if (dvEsperado.equals("igual")) {
                return ValidationResponse.builder()
                        .success(true)
                        .message("RUT Valido")
                        .build();
            } else if (dvEsperado.equals("0") && dv.equals("0")) {
                return ValidationResponse.builder()
                        .success(true)
                        .message("RUT Valido")
                        .build();
            } else if (dvEsperado.equals("K") && dv.equals("K")) {
                return ValidationResponse.builder()
                        .success(true)
                        .message("RUT Valido")
                        .build();
            } else {
                return ValidationResponse.builder()
                        .success(false)
                        .message("RUT Invalido")
                        .build();
            }
        }
        else{
            return ValidationResponse.builder()
                    .success(false)
                    .message(""+validacionFormato)
                    .build();
        }
    }



    //Calculo DV en base a RUT preparado con modulo 11
    public static String calcularDigitoVerificadorEsperado(String numeroRUT) {
        Object[] array = new Object[]{numeroRUT};

        Object[] rutInvertido = invertirRut(array); //retorna el rut invertido
        String dvEsperado = calculoDV(rutInvertido, array);//retorna el dv del modulo 11

        System.out.println("digito verificador esperado: "+dvEsperado);
        return dvEsperado;
    }

    //Prepara RUT en base a modulo 11, para sacar el DV en base al modulo 11.
    public static Object[] invertirRut(Object[] array) {
        Object[] invertir_int = new Object[array.length];
        int maximo = array.length;

        for (int i = 0; i < array.length; i++) {
            Object j = array[maximo - 1];
            invertir_int[maximo - 1] = array[i];
            maximo--;
        }
        return invertir_int;
    }

    //Calcula el digito verificador con modulo 11
    public static String calculoDV(Object[] rutInvertido, Object[] array) {

        //Calculo para verificar el rut invertido
        int a = 2;
        int rutSumado = 0;
        for (int i = 0; i < rutInvertido.length; i++) {
            rutSumado = 0;
            array[i] = Integer.parseInt((String) array[i]) * a;
            rutSumado += Integer.parseInt(String.valueOf(array[i]));
            if (a == 7) {
                a = 1;
            }
            a++;
        }
        int resto = rutSumado % 11;
        //Digito dará como resultado el digito verificador.
        String Digito = String.valueOf(11 - resto);
        if (Digito.equals("11")) {
            Digito = "0";
        }
        if (Digito.equals("10")) {
            Digito = "K";
        }else{
            Digito = "igual";
        }
        return Digito;
    }

}