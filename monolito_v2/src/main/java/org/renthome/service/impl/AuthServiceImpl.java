package org.renthome.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.renthome.dto.AuthResponse;
import org.renthome.dto.LoginRequest;
import org.renthome.dto.RegisterRequest;
import org.renthome.model.Usuario;
import org.renthome.repository.UsuarioRepository;
import org.renthome.service.AuthService;
import org.renthome.service.JwtService;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = Logger.getLogger(AuthServiceImpl.class);

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        LOG.infof("Iniciando registro para usuario: %s", request.getEmail());

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + request.getEmail());
        }

        try {
            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.nombre = request.getNombre();
            usuario.email = request.getEmail();
            usuario.password = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

            // Guardar usuario
            usuarioRepository.persist(usuario);
            LOG.infof("Usuario registrado exitosamente: %s", usuario.email);

            // Generar token JWT
            String token = jwtService.generateToken(usuario);

            return new AuthResponse(token, usuario.email, usuario.nombre, 3600L);

        } catch (Exception e) {
            LOG.errorf(e, "Error registrando usuario: %s", request.getEmail());
            throw new RuntimeException("Error interno del servidor", e);
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        LOG.infof("Iniciando login para usuario: %s", request.getEmail());

        try {
            // Buscar usuario por email
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

            // Verificar que esté activo
            if (!usuario.activo) {
                throw new RuntimeException("Usuario inactivo");
            }

            // Verificar contraseña
            if (!BCrypt.checkpw(request.getPassword(), usuario.password)) {
                throw new RuntimeException("Credenciales inválidas");
            }

            LOG.infof("Login exitoso para usuario: %s", usuario.email);

            // Generar token JWT
            String token = jwtService.generateToken(usuario);

            return new AuthResponse(token, usuario.email, usuario.nombre, 3600L);

        } catch (Exception e) {
            LOG.errorf(e, "Error en login para usuario: %s", request.getEmail());
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
