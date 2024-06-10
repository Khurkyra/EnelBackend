package com.backend.BackendJWT.Validaciones;

import com.backend.BackendJWT.Models.DTO.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidacionPorCampo {
    public static ValidationResponse validacionPorCampo(RegisterRequest request) {

        if (!StringValidation.IsOnlyAlphabetic(request.getFirstname()) || request.getFirstname().contains(" ")) {
            return new ValidationResponse(false, "El campo nombre solo puede contener letras y una longitud entre 2 y 40 caracteres. No puede tener espacios vacios.");
        }
        if (!StringValidation.IsOnlyAlphabetic(request.getLastname()) || request.getLastname().contains(" ")) {
            return new ValidationResponse(false, "El campo apellido solo puede contener letras y una longitud entre 2 y 40 caracteres. No puede tener espacios vacios.");
        }
        if(!StringValidation.validatePassword(request.getPassword()) || request.getPassword().contains(" ")){
            return new ValidationResponse(false, "El campo password debe tener minimo de ocho caracteres y maximo 15, con al menos una letra mayuscula, una letra minuscula y un numero.");
        }
        if (request.getRut() == null || request.getRut().isEmpty() || request.getRut().trim().isEmpty() || request.getRut().contains(" ")){
            return new ValidationResponse(false, "El campo rut no puede estar vacio, ni tener espacios vacios.");
        }
        ValidationResponse rutvalidation = RutValidation.validacionModule11(request.getRut());
        if(!rutvalidation.isSuccess()){
            return new ValidationResponse(false, ""+rutvalidation.getMessage());
        }
        if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().trim().isEmpty() || request.getPassword().contains(" ")) {
            return new ValidationResponse(false, "El password rut no puede estar vacio, ni tener espacios vacios.");
        }
        if(!isValidEmail(request.getEmail()) || request.getEmail().contains(" ")){
            return new ValidationResponse(false, "El campo email es invalido. Debe tener una longitud entre 4 y 50 caracteres, un @ y un dominio. No puede tener espacios vacios.");
        }
        if(!isValidPhoneNumber(request.getPhoneNumber()) || request.getPhoneNumber().contains(" ")){
            return new ValidationResponse(false, "El campo celular es invalido. Debe tener solo numeros con una longitud de 8 digitos, sin el prefijo +569. No puede tener espacios vacios.");
        }
        return new ValidationResponse(true, "Todos los campos son válidos");
    }

    public static ValidationResponse validacionPorCampoUpdate(UpdateClienteRequest request) {
        if(!isValidEmail(request.getEmail()) || request.getEmail().contains(" ")){
            return new ValidationResponse(false, "El campo email es invalido. Debe tener una longitud entre 4 y 50 caracteres, un @ y un dominio. No puede tener espacios vacios.");
        }
        if(!isValidPhoneNumber(request.getPhoneNumber()) || request.getPhoneNumber().contains(" ")){
            return new ValidationResponse(false, "El campo celular es invalido. Debe tener solo numeros con una longitud de 8 digitos, sin el prefijo +569. No puede tener espacios vacios.");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().trim().isEmpty() || request.getPassword().contains(" ")) {
            return new ValidationResponse(false, "El password rut no puede estar vacio, ni tener espacios vacios.");
        }
        if(!StringValidation.validatePassword(request.getPassword()) || request.getPassword().contains(" ")){
            return new ValidationResponse(false, "El campo password debe tener minimo de ocho caracteres y maximo 15, con al menos una letra mayuscula, una letra minuscula y un numero.");
        }
        return new ValidationResponse(true, "Todos los campos son válidos");
    }
    public static ValidationResponse validacionPorCampoConsumo(RegisterConsumoRequest request) {
        if (request.getLectura() == null || request.getLectura().isEmpty() || request.getLectura().trim().isEmpty() || request.getLectura().contains(" ")){
            return new ValidationResponse(false, "La cantidad de consumo es obligatoria, no puede ser nula, vacia o tener espacios vacios.");
        }
        if(!StringValidation.IsOnlyNumeric(request.getLectura()) || request.getLectura().length() <=4 || request.getLectura().length()>=30){
            return new ValidationResponse(false, "El campo comuna debe tener solo numeros, un mínimo de 2 caracteres y un maximo de 20 caracteres.");
        }
        return new ValidationResponse(true, "Todos los campos son válidos");
    }


    public static ValidationResponse validacionPorCampoMedidor(RegisterMedidorRequest request) {
        if(request.getRegion() == null || request.getRegion().isEmpty() || request.getRegion().trim().isEmpty()){
            return new ValidationResponse(false, "El campo region es obligatorio, no puede ser nulo o vacio.");
        }
        if(!StringValidation.IsOnlyAlphabeticAndBlank(request.getRegion()) || request.getRegion().length() <=4 || request.getRegion().length()>=30){
            return new ValidationResponse(false, "El campo region debe tener solo letras, un mínimo de 4 caracteres y un maximo de 30 caracteres.");
        }
        if(request.getComuna() == null || request.getComuna().isEmpty() || request.getComuna().trim().isEmpty()){
            return new ValidationResponse(false, "El campo comuna es obligatorio, no puede ser nulo o vacio.");
        }
        if(!StringValidation.IsOnlyAlphabeticAndBlank(request.getComuna()) || request.getComuna().length() <=4 || request.getComuna().length()>=30){
            return new ValidationResponse(false, "El campo comuna debe tener solo letras, un mínimo de 4 caracteres y un maximo de 30 caracteres.");
        }
        if(request.getDireccion() == null || request.getDireccion().isEmpty() || request.getDireccion().trim().isEmpty()){
            return new ValidationResponse(false, "El campo dirección es obligatorio, no puede ser nulo o vacio.");
        }
        if(request.getDireccion().length() <=4 || request.getDireccion().length()>=60){
            return new ValidationResponse(false, "El campo dirección debe tener un mínimo de 4 caracteres y un maximo de 60 caracteres.");
        }
        if(!StringValidation.IsOnlyAlphaNumeric(request.getDireccion())){
            return new ValidationResponse(false, "El campo dirección debe tener el nombre de la calle y enumeración");
        }
        if (request.getNumcliente() == null || request.getNumcliente().isEmpty() || request.getNumcliente().trim().isEmpty() || request.getNumcliente().contains(" ")){
            return new ValidationResponse(false, "El campo numero de cliente es obligatorio, no puede ser nulo, vacio o tener espacios vacios.");
        }
        if(!StringValidation.IsOnlyNumeric(request.getNumcliente()) || request.getNumcliente().length() <=2 || request.getNumcliente().length()>=20){
            return new ValidationResponse(false, "El campo numero de cliente debe tener solo numeros, un mínimo de 4 caracteres y un maximo de 20 caracteres.");
        }
        return new ValidationResponse(true, "Todos los campos son validos");
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