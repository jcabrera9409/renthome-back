package com.surrender.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.surrender.dto.AuthenticationResponse;
import com.surrender.model.Token;
import com.surrender.model.Usuario;
import com.surrender.repo.TokenRepo;
import com.surrender.repo.UsuarioRepo;

@Service
public class AuthenticationService {
    @Autowired
	private UsuarioRepo repository;
    
	@Autowired
	private PasswordEncoder passwordEncoder;
    
	@Autowired
	private JwtService jwtService;

	@Autowired
    private TokenRepo tokenRepository;

	@Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(Usuario request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Usuario usuario = repository.findByEmail(request.getUsername()).orElseThrow();
        
        if(usuario.isActivo()) {
        	String accessToken = jwtService.generateAccessToken(usuario);
            String refreshToken = jwtService.generateRefreshToken(usuario);

            revokeAllTokenByVendedor(usuario);
            saveUserToken(accessToken, refreshToken, usuario);

            return new AuthenticationResponse(accessToken, refreshToken, "Usuario autenticado correctamente");
        } else {
        	return new AuthenticationResponse(null, null, "Su cuenta esta desactivada.");
        }
    }

    public void revokeAllTokenByVendedor(Usuario usuario) {
        List<Token> validTokens = tokenRepository.findByUsuarioIdAndLoggedOutFalse(usuario.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, Usuario usuario) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUsuario(usuario);
        tokenRepository.save(token);
    }

    public int modificarPasswordPorId(Integer id, String password) {
		int filasActualizadas = repository.updatePasswordById(id, passwordEncoder.encode(password));
		return filasActualizadas;
	}
}
