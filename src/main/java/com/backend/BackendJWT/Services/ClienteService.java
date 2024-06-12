package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Repositories.Auth.*;
import com.backend.BackendJWT.Validaciones.ValidacionPorCampo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public List<Medidor> obtenerMedidoresDeCliente(Long clienteId) {
        System.out.println("Se recibe clienteId en service: " + clienteId);
        List<UsuarioMedidor> usuarioMedidores = usuarioMedidorRepository.findByClienteId(clienteId);
        System.out.println("Lista de los medidores del usuario en service, antes del return " + usuarioMedidores.toString());
        System.out.println(usuarioMedidores);

        List<Medidor> medidores = usuarioMedidores.stream()
                .map(UsuarioMedidor::getMedidor)
                .collect(Collectors.toList());
        System.out.println(medidores);
        return medidores;
    }

    public AuthResponseListObj obtenerMedidoresPorCliente(Long clienteId) {
        try {
            System.out.println("Se recibe clienteId en service:  " + clienteId);
            List<UsuarioMedidor> usuarioMedidores = usuarioMedidorRepository.findByClienteId(clienteId);
            System.out.println("Lista de los medidores del usuario en service, antes del return " + usuarioMedidores.toString());
            System.out.println(usuarioMedidores);
            List<Medidor> medidores = usuarioMedidores.stream()
                    .map(UsuarioMedidor::getMedidor)
                    .collect(Collectors.toList());
            //String medidoresString = medidores.toString();
            //System.out.println("lista de medidores: " + medidores.toString());
           //retorna bien el objeto, el problema esta en que si retorno directamente el objeto list de medidor...
            //srping boot automaticamente lo serializa en json.... pero para que lo serialice bien las entidades deben estar correctamente declaradas.
            System.out.println(medidores);
            return AuthResponseListObj.builder()
                    .success(true)
                    .message("peticiion exitosa")
                    .object(medidores)
                    .build();
        } catch (Exception e) {
            System.out.println("error:  " + e.getMessage());
            return AuthResponseListObj.builder()
                    .success(false)
                    .message("peticiion no exitosa")
                    .object(null)
                    .build();
        }
    }


    public AuthResponseObj obtenerObjectCliente(Long clienteId){
        try{
            System.out.println("Se recibe clienteId en service:  " + clienteId);
            List<UsuarioMedidor> usuarioMedidores = usuarioMedidorRepository.findByClienteId(clienteId);
            System.out.println("Lista de los medidores del usuario en service, antes del return " + usuarioMedidores.toString());
            String medidoresString = usuarioMedidores.toString();
            return AuthResponseObj.builder()
                    .success(true)
                    .message("peticiion exitosa")
                    .object(medidoresString)
                    .build();
        }catch (Exception e){
            return AuthResponseObj.builder()
                    .success(false)
                    .message("peticiion exitosa")
                    .object(null)
                    .build();
        }
    }

    public Cliente getClienteByRut(String rut) {

        System.out.println("cliente rut: "+rut);
        return clienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public AuthResponse actualizarClienteParcial(String rut, UpdateClienteRequest updateClienteRequest) {
        Cliente cliente = clienteRepository.getClienteByRut(rut);
        System.out.println("cliente: "+ cliente.toString());

        try{
            ValidationResponse validacionPorCampo = ValidacionPorCampo.validacionPorCampoUpdate(updateClienteRequest);
            if (!validacionPorCampo.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token(""+validacionPorCampo.getMessage())
                        .build();
            }
            cliente.setPassword(passwordEncoder.encode(updateClienteRequest.getPassword()));
            clienteRepository.save(cliente);
            return AuthResponse.builder()
                    .success(true)
                    .token("Datos actualizados exitosamente")
                    .build();
        }
        catch(Exception e){
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al intentar actualizar los datos: "+e.getMessage())
                    .build();
        }
    }


    public AuthResponse registrarMedidor(RegisterMedidorRequest medidorRequest, Cliente cliente) {
        try {
            // Validar campos del medidor
            ValidationResponse validacionPorCampo = ValidacionPorCampo.validacionPorCampoMedidor(medidorRequest);
            if (!validacionPorCampo.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token(validacionPorCampo.getMessage())
                        .build();
            }

            // Crear una instancia de Calendar y establecer la fecha específica
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 2024);
            calendar.set(Calendar.MONTH, Calendar.JUNE); // Calendar.JUNE es 5, ya que los meses se indexan desde 0
            calendar.set(Calendar.DAY_OF_MONTH, 28);

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
                }
            } else {
                // Si el medidor no existe, crearlo y luego la asociación con el cliente
                Medidor nuevoMedidor = new Medidor();
                nuevoMedidor.setNumcliente(medidorRequest.getNumcliente());
                nuevoMedidor.setRegion(medidorRequest.getRegion());
                nuevoMedidor.setComuna(medidorRequest.getComuna());
                nuevoMedidor.setDireccion(medidorRequest.getDireccion());
                nuevoMedidor.setFecha(fechaEspecifica);
                Medidor medidorGuardado = medidorRepository.save(nuevoMedidor);

                UsuarioMedidor usuarioMedidor = new UsuarioMedidor();
                usuarioMedidor.setCliente(cliente);
                usuarioMedidor.setMedidor(medidorGuardado);
                usuarioMedidorRepository.save(usuarioMedidor);
            }

            return AuthResponse.builder()
                    .success(true)
                    .token("Medidor registrado exitosamente")
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al intentar registrar el medidor: " + e.getMessage())
                    .build();
        }
    }


    public GetFechaResponse obtenerFechaConsumo(Long medidorId){
        try{
            Medidor medidor = medidorRepository.findById(medidorId)
                    .orElseThrow(() -> new RuntimeException("Medidor not found"));
            Date fechamedidor = medidor.getFecha();
            return GetFechaResponse.builder()
                    .success(true)
                    .fecha(fechamedidor)
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


    public AuthResponse registrarConsumo(Long medidorId, Consumo consumo) {
        Medidor medidor = medidorRepository.findById(medidorId)
                .orElseThrow(() -> new RuntimeException("Medidor not found"));
        consumo.setMedidor(medidor);
        consumoRepository.save(consumo);
        return AuthResponse.builder()
                .success(false)
                .token("Ocurrio un error al intentar eliminar el medidor de su cuenta")
                .build();
    }


    //public Cliente registrarSuministro(Long medidorId, Suministro suministro){
    //  Medidor medidor = medidorRepository.findById(medidorId)
    //        .orElseThrow(() -> new RuntimeException("Medidor not found"));
    //  suministro.setMedidor(medidor);
    //suministroRepository.save(suministro);
    //  return getClienteByRut(medidor.getCliente().getRut()); // Devolver el cliente actualizado
    // }





}
