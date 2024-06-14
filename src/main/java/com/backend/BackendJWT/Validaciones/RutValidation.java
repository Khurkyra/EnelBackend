package com.backend.BackendJWT.Validaciones;

import com.backend.BackendJWT.Models.DTO.ValidationResponse;

public class RutValidation {

    public static String validaFormato(String rut) {
        if (rut.matches("^\\d{7,8}-[0-9Kk]$")) {
            return "rut valido";
        } else {
            return "El RUT no tiene el formato válido. Debe tener entre 8 a 9 dígitos, guion y dígito verificador";
        }
    }

    // Función principal que valida y verifica RUT en base al modulo 11.
    public static ValidationResponse validacionModule11(String rut) {
        String validacionFormato = validaFormato(rut);
        if (validacionFormato.equals("rut valido")) {
            String rutSinFormato = rut.replaceAll("[.-]", "");
            // Se separa el RUT.
            // Obtiene RUT.
            String numeroRUT = rutSinFormato.substring(0, rutSinFormato.length() - 1);
            // Obtiene DV
            String dv = rutSinFormato.substring(rutSinFormato.length() - 1).toUpperCase();
            // Obtiene DV del Modulo 11.
            String dvEsperado = calcularDigitoVerificadorEsperado(numeroRUT);
            // Compara DV de Modulo 11 con el proporcionado.
            if (dvEsperado.equals(dv)) {
                return ValidationResponse.builder()
                        .success(true)
                        .message("RUT Válido")
                        .build();
            } else {
                return ValidationResponse.builder()
                        .success(false)
                        .message("RUT Inválido")
                        .build();
            }
        } else {
            return ValidationResponse.builder()
                    .success(false)
                    .message(validacionFormato)
                    .build();
        }
    }

    // Calculo DV en base a RUT preparado con modulo 11
    public static String calcularDigitoVerificadorEsperado(String numeroRUT) {
        int rut = Integer.parseInt(numeroRUT);
        int m = 0, s = 1;
        while (rut != 0) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
            rut /= 10;
        }
        return (s > 0) ? String.valueOf((char) (s + 47)) : "K";
    }

}
