package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Models.Auth.Consumo;
import com.backend.BackendJWT.Models.Auth.Suministro;
import com.backend.BackendJWT.Models.DTO.AuthResponseListObj;
import com.backend.BackendJWT.Models.DTO.ConsumoConComuna;
import com.backend.BackendJWT.Repositories.Auth.ConsumoRepository;
import com.backend.BackendJWT.Repositories.Auth.SuministroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ConsumoRepository consumoRepository;
    private final SuministroRepository suministroRepository;


    public AuthResponseListObj obtenerTodosLosConsumos() {
        try{
            List<Consumo> response = consumoRepository.findAll();
            return AuthResponseListObj.builder()
                    .success(true)
                    .message("Peticion GET exitosa")
                    .object(response)
                    .build();

        }catch(Exception e){
            return AuthResponseListObj.builder()
                    .success(false)
                    .message("Peticion GET rechazada. Ocurrio un error al intentar obtener todos los consumos")
                    .object(null)
                    .build();
        }
    }

    public AuthResponseListObj obtenerTodosLosSuministros() {
        try{
            List<Suministro> response = suministroRepository.findAll();
            return AuthResponseListObj.builder()
                    .success(true)
                    .message("Peticion GET exitosa")
                    .object(response)
                    .build();

        }catch(Exception e){
            return AuthResponseListObj.builder()
                    .success(false)
                    .message("Peticion GET rechazada. Ocurrio un error al intentar obtener todos los consumos")
                    .object(null)
                    .build();
        }
    }

    public AuthResponseListObj obtenerTodosLosConsumosConMedidores() {
        try {
            List<ConsumoConComuna> consumosConComuna = new ArrayList<>();

            List<Consumo> consumos = consumoRepository.findAll();  // Obtener todos los consumos

            // Iterar sobre los consumos y crear un DTO que contenga la comuna y el consumo
            for (Consumo consumo : consumos) {
                String comuna = consumo.getMedidor().getComuna();// Obtener la comuna del medidor asociado
                String region = consumo.getMedidor().getRegion();
                ConsumoConComuna consumoConComuna = new ConsumoConComuna(consumo, comuna,region);
                consumosConComuna.add(consumoConComuna);
            }

            return AuthResponseListObj.builder()
                    .success(true)
                    .message("Peticion GET exitosa")
                    .object(consumosConComuna)
                    .build();

        } catch (Exception e) {
            return AuthResponseListObj.builder()
                    .success(false)
                    .message("Peticion GET rechazada. Ocurrio un error al intentar obtener todos los consumos")
                    .object(null)
                    .build();
        }
    }

}
