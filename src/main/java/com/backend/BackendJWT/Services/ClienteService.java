package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Repositories.Auth.*;
import com.backend.BackendJWT.Validaciones.StringValidation;
import com.backend.BackendJWT.Validaciones.ValidacionPorCampo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private SuministroRepository suministroRepository;

    @Autowired
    private MedidorRepository medidorRepository;


    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private UsuarioMedidorRepository usuarioMedidorRepository;

    //GETS
    public Cliente getClienteByRut(String rut) {
        try{
            Cliente cliente = clienteRepository.getClienteByRut(rut);
            if(cliente != null){
                System.out.println("cliente encontrado en getcliente" + cliente);
                return cliente;
            }else{
                return null;
            }
        }catch(Exception e){
            System.out.println("Error");
            return null;
        }
    }


    public AuthResponseObj obtenerCliente(String rut){
        Cliente cliente = getClienteByRut(rut);
        if (cliente == null){
            return AuthResponseObj.builder()
                    .success(false)
                    .message("El cliente no se encuentra en la base de datos")
                    .object(null)
                    .build();
        }else{
            return AuthResponseObj.builder()
                    .success(true)
                    .message("Peticion GET exitosa")
                    .object(cliente)
                    .build();
        }
    }



    public AuthResponseListObj obtenerMedidoresDeCliente(String rut) {
        try{
            Cliente cliente = getClienteByRut(rut);
            if (cliente == null){
                return AuthResponseListObj.builder()
                        .success(false)
                        .message("El cliente no se encuentra en la base de datos")
                        .object(null)
                        .build();
            }else{
                Long clienteId = cliente.getId();
                List<UsuarioMedidor> usuarioMedidores = usuarioMedidorRepository.findByClienteId(clienteId);

                List<Medidor> medidores = usuarioMedidores.stream()
                        .map(UsuarioMedidor::getMedidor)
                        .collect(Collectors.toList());
                return AuthResponseListObj.builder()
                        .success(true)
                        .message("Peticion GET exitosa")
                        .object(medidores)
                        .build();
            }
        }catch(Exception e){
            return AuthResponseListObj.builder()
                    .success(false)
                    .message("Peticion GET rechazada. Ocurrio un error al intentar obtener el medidor")
                    .object(null)
                    .build();
        }
    }


    public AuthResponseListObj obtenerConsumosDeMedidor(Long medidorId) {
        try{
            List<Consumo> consumos = consumoRepository.findByMedidorId(medidorId);
            return AuthResponseListObj.builder()
                    .success(true)
                    .message("Peticion GET exitosa")
                    .object(consumos)
                    .build();
        }catch(Exception e){
            return AuthResponseListObj.builder()
                    .success(false)
                    .message("Peticion GET rechazada. Ocurrio un error al intentar obtener el consumo del medidor")
                    .object(null)
                    .build();
        }
    }


    public AuthResponseListObj obtenerSuministrosDeMedidor(Long medidorId) {
        try{
            List<Suministro> suministros = suministroRepository.findByMedidorId(medidorId);
            return AuthResponseListObj.builder()
                    .success(true)
                    .message("Peticion GET exitosa")
                    .object(suministros)
                    .build();
        }catch(Exception e){
            return AuthResponseListObj.builder()
                    .success(false)
                    .message("Peticion GET rechazada. Ocurrio un error al intentar obtener el suministro del medidor")
                    .object(null)
                    .build();
        }
    }



    public GetFechaResponse obtenerFechaConsumo(Long medidorId){
        try{
            Medidor medidor = medidorRepository.findById(medidorId)
                    .orElseThrow(() -> new RuntimeException("Medidor not found"));
            Date fechamedidor = medidor.getFecha();
            // Formatear la fecha
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = formatter.format(fechamedidor);
            return GetFechaResponse.builder()
                    .success(true)
                    .fecha(formattedDate)
                    .message("Fecha obtenida exitosamente")
                    .build();
        }catch(RuntimeException e){
            return GetFechaResponse.builder()
                    .success(false)
                    .fecha(null)
                    .message("El medidor seleccionado no se encuentra en nuestra base de datos:  "+e.getMessage())
                    .build();
        }catch(Exception e){
            return GetFechaResponse.builder()
                    .success(false)
                    .fecha(null)
                    .message("Hubo un error al intentar obetener la fecha de consumo"+ e.getMessage())
                    .build();
        }
    }



    //PATCHS
    public AuthResponse actualizarClienteParcial(String rut, UpdateClienteRequest updateClienteRequest) {
        Cliente cliente = clienteRepository.getClienteByRut(rut);

        try {
            ValidationResponse validacionPorCampo = ValidacionPorCampo.validacionPorCampoUpdate(updateClienteRequest);
            if (!validacionPorCampo.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token("" + validacionPorCampo.getMessage())
                        .build();
            }
            if(updateClienteRequest.getEmail() != null){
                cliente.setEmail(updateClienteRequest.getEmail());
            }
            if(updateClienteRequest.getPassword() != null){
                cliente.setPassword(passwordEncoder.encode(updateClienteRequest.getPassword()));
            }
            if(updateClienteRequest.getPhoneNumber() != null){
                cliente.setPhoneNumber(updateClienteRequest.getPhoneNumber());
            }
            clienteRepository.save(cliente);
            return AuthResponse.builder()
                    .success(true)
                    .token("Datos actualizados exitosamente")
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al intentar actualizar los datos: " + e.getMessage())
                    .build();
        }

    }

    //POSTS
    public AuthResponse registrarMedidor(RegisterMedidorRequest medidorRequest, String rut) {
        try {
            // Validar campos del medidor
            ValidationResponse validacionPorCampo = ValidacionPorCampo.validacionPorCampoMedidor(medidorRequest);
            if (!validacionPorCampo.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token(validacionPorCampo.getMessage())
                        .build();
            }
            Cliente cliente = clienteRepository.getClienteByRut(rut);
            // Crear una instancia de Calendar y establecer la fecha específica
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 2024);
            calendar.set(Calendar.MONTH, Calendar.JUNE); // Calendar.JUNE es 5, ya que los meses se indexan desde 0
            calendar.set(Calendar.DAY_OF_MONTH, 06);

            // Obtener la fecha como un objeto Date
            Date fechaEspecifica = calendar.getTime();

            // Buscar el medidor por la dirección
            Optional<Medidor> medidorExistente = medidorRepository.findByDireccion(medidorRequest.getDireccion());
            if (medidorExistente.isPresent()) {
                // Verificar si ya existe una asociación del cliente con este medidor
                boolean asociacionExistente = usuarioMedidorRepository.existsByClienteAndMedidor(cliente, medidorExistente.get());
                if (asociacionExistente) {
                    // Si ya existe una asociación, devolver un mensaje indicando que ya tiene un medidor asociado con esa dirección
                    return AuthResponse.builder()
                            .success(false)
                            .token("Ya tiene un medidor asociado con la dirección proporcionada")
                            .build();
                } else {
                    // Si el medidor existe y no tiene una asociación con el cliente, crear la asociación
                    UsuarioMedidor usuarioMedidor = new UsuarioMedidor();
                    usuarioMedidor.setCliente(cliente);
                    usuarioMedidor.setMedidor(medidorExistente.get());
                    usuarioMedidorRepository.save(usuarioMedidor);
                    return AuthResponse.builder()
                            .success(true)
                            .token("El medidor ya está registrado y ha sido asociado a su cuenta. Puede visualizarlo en la sección de Home")
                            .build();
                }
            } else {
                // Si el medidor no existe, crearlo y luego la asociación con el cliente
                Medidor nuevoMedidor = new Medidor();
                nuevoMedidor.setNumcliente(medidorRequest.getNumcliente());
                nuevoMedidor.setRegion(medidorRequest.getRegion());
                nuevoMedidor.setComuna(medidorRequest.getComuna());
                nuevoMedidor.setDireccion(medidorRequest.getDireccion());
                nuevoMedidor.setFecha(fechaEspecifica);
                nuevoMedidor.setTipoTarifa("BT-1");
                nuevoMedidor.setTarifa(140);
                nuevoMedidor.setCargoFijo(3500);
                Medidor medidorGuardado = medidorRepository.save(nuevoMedidor);

                UsuarioMedidor usuarioMedidor = new UsuarioMedidor();
                usuarioMedidor.setCliente(cliente);
                usuarioMedidor.setMedidor(medidorGuardado);
                usuarioMedidorRepository.save(usuarioMedidor);
                return AuthResponse.builder()
                        .success(true)
                        .token("Medidor registrado exitosamente")
                        .build();
            }
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al intentar registrar el medidor: " + e.getMessage())
                    .build();
        }
    }

    //cotiza el consumo de un medidor, sin guardar el valor.
    public AuthResponseObj cotizarConsumo(Long medidorId, RegisterConsumoRequest consumo) {
        //arreglar validaciones y fecha.
        try{
            Medidor medidor = medidorRepository.findById(medidorId)
                    .orElseThrow(() -> new RuntimeException("Medidor not found"));
            // Si el medidor no existe, crearlo y luego la asociación con el cliente

            //tarifa = 140 kWH para tarifa BT-1
            Integer tarifa = medidor.getTarifa();
            Integer cargoFijo = medidor.getCargoFijo();

            Integer lecturaInteger = Integer.parseInt(consumo.getLectura());
            // Obtener la última lectura del medidor
            Optional<Consumo> ultimoConsumoOpt = consumoRepository.findTopByMedidorIdOrderByFechaDesc(medidorId);

            Integer ultimaLectura = ultimoConsumoOpt.map(Consumo::getLectura).orElse(0);
            Integer consumoCalculado = lecturaInteger - ultimaLectura;
            Integer costoEnergia =consumoCalculado*tarifa;
            Integer subtotalCalculado = costoEnergia+cargoFijo;
            Integer iva = subtotalCalculado*19/100;
            Integer total = subtotalCalculado+iva;

            Consumo nuevoConsumo = new Consumo();
            nuevoConsumo.setFecha(consumo.getFecha());
            nuevoConsumo.setLectura(lecturaInteger);
            nuevoConsumo.setConsumo(consumoCalculado);
            nuevoConsumo.setCostoEnergia(costoEnergia);
            nuevoConsumo.setSubtotal(subtotalCalculado);
            nuevoConsumo.setIva(iva);
            nuevoConsumo.setTotal(total);
            nuevoConsumo.setMedidor(medidor);

            return AuthResponseObj.builder()
                    .success(true)
                    .message("Su cotizacion ha sido realizada con éxito")
                    .object(nuevoConsumo)
                    .build();

        }catch(RuntimeException e){
            return AuthResponseObj.builder()
                    .success(false)
                    .message("El medidor seleccionado no se encuentra en la base de datos")
                    .object(null)
                    .build();

        }catch(Exception e){
            return AuthResponseObj.builder()
                    .success(false)
                    .message("Ocurrio un error al intentar cotizar su consumo")
                    .object(null)
                    .build();
        }
    }


    //calcula si la fecha de registro de consumo esta dentro del rango permitido.
    public static boolean diaDelMesEnRango(LocalDate localDate1, LocalDate localDate2) {
        // Obtener el día del mes de ambas fechas
        int dia1 = localDate1.getDayOfMonth();
        int dia2 = localDate2.getDayOfMonth();
        // Verificar si están en el rango de -1 a 1 días de diferencia
        return Math.abs(dia1 - dia2) <= 1;
    }

    public static boolean consumoExist(LocalDate locaDate3, LocalDate localDate2) {
        int mes1 = locaDate3.getMonthValue();
        int mes2 = localDate2.getMonthValue();

        int year1 = locaDate3.getYear();
        int year2 = localDate2.getYear();

        if(year1 == year2 && mes1 == mes2){
            return true;
        }else{
            return false;
        }

    }
    


    public AuthResponse registrarConsumo(Long medidorId, RegisterConsumoRequest consumo) {
        //arreglar validaciones y fecha.
        try{
            Medidor medidor = medidorRepository.findById(medidorId)
                    .orElseThrow(() -> new RuntimeException("Medidor not found"));
            // Si el medidor no existe, crearlo y luego la asociación con el cliente
            Date fechaLecturaMedidor = medidor.getFecha();
            Date fechaLecturaConsumo = consumo.getFecha();

            LocalDate localDate1 = fechaLecturaMedidor.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDate2 = fechaLecturaConsumo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Obtener la última lectura del medidor
            Optional<Consumo> ultimoConsumoOpt = consumoRepository.findTopByMedidorIdOrderByFechaDesc(medidorId);
            if(ultimoConsumoOpt.isPresent()){
                Date ultimaFechaConsumo = ultimoConsumoOpt.map(Consumo::getFecha).orElse(null);
                LocalDate localDate3 = ultimaFechaConsumo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(consumoExist(localDate3, localDate2)){
                    return AuthResponse.builder()
                            .success(false)
                            .token("El consumo de este mes ya se encuentra registrado.")
                            .build();
                }
            }
            if(diaDelMesEnRango(localDate1, localDate2)){
                Integer tarifa = medidor.getTarifa();
                Integer cargoFijo = medidor.getCargoFijo();

                Integer lecturaInteger = Integer.parseInt(consumo.getLectura());

                Integer ultimaLectura = ultimoConsumoOpt.map(Consumo::getLectura).orElse(0);
                Integer consumoCalculado = lecturaInteger - ultimaLectura;
                Integer costoEnergia =consumoCalculado*tarifa;
                Integer subtotalCalculado = costoEnergia+cargoFijo;
                Integer iva = subtotalCalculado*19/100;
                Integer total = subtotalCalculado+iva;

                Consumo nuevoConsumo = new Consumo();
                nuevoConsumo.setFecha(consumo.getFecha());
                nuevoConsumo.setLectura(lecturaInteger);
                nuevoConsumo.setConsumo(consumoCalculado);
                nuevoConsumo.setCostoEnergia(costoEnergia);
                nuevoConsumo.setSubtotal(subtotalCalculado);
                nuevoConsumo.setIva(iva);
                nuevoConsumo.setTotal(total);
                nuevoConsumo.setMedidor(medidor);

                consumoRepository.save(nuevoConsumo);

                return AuthResponse.builder()
                        .success(true)
                        .token("Su consumo ha sido registrado exitosamente")
                        .build();
            }else{
                return AuthResponse.builder()
                        .success(false)
                        .token("No se encuentra dentro de la fecha permitida. Fecha medidor: "+fechaLecturaMedidor+ "Fecha consumo: "+fechaLecturaConsumo)
                        .build();
            }
        }catch(RuntimeException e){
            return AuthResponse.builder()
                    .success(false)
                    .token("El medidor seleccionado no se encuentra en la base de datos"+e.getMessage())
                    .build();
        }catch(Exception e){
            return AuthResponse.builder()
                    .success(false)
                    .token("Ocurrio un error al intentar registrar su consumo")
                    .build();
        }
    }



    public AuthResponse registrarSuministro(Long medidorId, Suministro suministro) {
        //arreglar validaciones y fecha.
        try{
            Medidor medidor = medidorRepository.findById(medidorId)
                    .orElseThrow(() -> new RuntimeException("Medidor not found"));
            suministro.setMedidor(medidor);
            suministroRepository.save(suministro);
            return AuthResponse.builder()
                    .success(true)
                    .token("Su suministro ha sido registrado exitosamente")
                    .build();
        }catch(RuntimeException e){
            //hacerlo mas especifico, ya que ante cualquier error caera aca, y este debe solo ser para
            //un medidor que no se encuentra en la base de datos/
            return AuthResponse.builder()
                    .success(false)
                    .token("El medidor seleccionado no se encuentra en la base de datos")
                    .build();
        }catch(Exception e){
            return AuthResponse.builder()
                    .success(false)
                    .token("Ocurrio un error al intentar registrar su suministro")
                    .build();
        }
    }



    //DELETES
    public AuthResponse eliminarAsociacionMedidorYObtenerClienteActualizado(Long medidorId, String rut) {
        try{
            Medidor medidor = medidorRepository.findById(medidorId)
                    .orElseThrow(() -> new RuntimeException("Medidor no encontrado"));

            // Eliminar la asociación entre el cliente y el medidor
            usuarioMedidorRepository.deleteByMedidorAndClienteRut(medidor, rut);

            // Devolver el cliente actualizado
            return AuthResponse.builder()
                    .success(true)
                    .token("El medidor ya no esta asociado a su cuenta")
                    .build();
        }catch(Exception e){
            return AuthResponse.builder()
                    .success(false)
                    .token("Ocurrio un error al intentar eliminar el medidor de su cuenta")
                    .build();
        }
    }



    public boolean eliminarUsuario(String rut) {
        Cliente cliente = getClienteByRut(rut);

        clienteRepository.delete(cliente);
        return true; // Usuario eliminado con éxito
    }

}
