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
            return new ValidationResponse(false, "El campo nombre solo puede contener letras y una longitud entre 2 y 40 caracteres");
        }

        // Validar campo apellido
        if (!isValidNombreOrApellido(request.getLastname())) {
            return new ValidationResponse(false, "El campo apellido solo puede contener letras y una longitud entre 2 y 40 caracteres");
        }
        if(!StringValidation.validatePassword(request.getPassword())){
            return new ValidationResponse(false, "El campo password debe tener minimo de ocho caracteres y maximo 15, con al menos una letra mayuscula, una letra minuscula y un numero");
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
            return new ValidationResponse(false, "El campo email es invalido. Debe tener una longitud entre 4 y 50 caracteres, un @ y un dominio");
        }
        if(!isValidPhoneNumber(request.getPhoneNumber())){
            return new ValidationResponse(false, "El campo celular es invalido. Debe tener solo numeros con una longitud de 8 digitos, sin el prefijo +569");
        }
        return new ValidationResponse(true, "Todos los campos son válidos");
    }

    public static boolean isValidNombreOrApellido(String text) {
        if (text == null || text.isEmpty() || text.trim().isEmpty()) {
            return false;
        }
        if(text.length()>2 && text.length()<40){
            Pattern pattern = Pattern.compile("[a-zA-Z]+");
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        }else{
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty() || email.trim().isEmpty()) {
            return false;
        }
        if (email.length()>4 &&email.length()<50){
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$");
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }else{
            return false;
        }
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty() || phone.trim().isEmpty()) {
            return false;
        }
        if(phone.length()==8){
            Pattern pattern = Pattern.compile("^(\\+569|569|9)?\\d{8}$");
            Matcher matcher = pattern.matcher(phone);
            return matcher.matches();
        }else{
            return false;
        }
    }

}