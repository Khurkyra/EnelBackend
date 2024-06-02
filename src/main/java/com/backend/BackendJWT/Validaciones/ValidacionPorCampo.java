package com.backend.BackendJWT.Validaciones;

import com.backend.BackendJWT.Models.DTO.AuthResponse;
import com.backend.BackendJWT.Models.DTO.RegisterRequest;
import com.backend.BackendJWT.Models.DTO.ValidationResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidacionPorCampo {
    public static ValidationResponse validacionPorCampo(RegisterRequest request) {

        // Validar campo nombre
        if (!isValidNombreOrApellido(request.getFirstname())) {
            return new ValidationResponse(false, "El campo nombre solo puede contener letras");
        }

        // Validar campo apellido
        if (!isValidNombreOrApellido(request.getLastname())) {
            return new ValidationResponse(false, "El campo apellido solo puede contener letras");
        }
        if(!StringValidation.validatePassword(request.getPassword())){
            return new ValidationResponse(false, "El campo password debe tener minima de ocho caracteres, que combine mayusculas, minusculas y numeros");
        }
        ValidationResponse rutvalidation = RutValidation.validacionModule11(request.getRut());
        if(!rutvalidation.isSuccess()){
            return new ValidationResponse(false, ""+rutvalidation.getMessage());
        }
        if (request.getRut() == null || request.getRut().isEmpty() || request.getRut().trim().isEmpty()){
            return new ValidationResponse(false, "El campo rut no puede estar vacio");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().trim().isEmpty()) {
            return new ValidationResponse(false, "El password rut no puede estar vacio");
        }
        if(!isValidEmail(request.getEmail())){
            return new ValidationResponse(false, "El campo email es invalido");
        }
        if(!isValidPhoneNumber(request.getPhoneNumber())){
            return new ValidationResponse(false, "El campo celular es invalido");
        }
        return new ValidationResponse(true, "Todos los campos son válidos");
    }

    private static boolean isValidNombreOrApellido(String text) {
        if (text == null || text.isEmpty() || text.trim().isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty() || email.trim().isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty() || phone.trim().isEmpty()) {
            return false;
        }
//"^(\\+)?(569|9)?\\s?\\d{8}"
        Pattern pattern = Pattern.compile("^(\\+569|569|9)?\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

}