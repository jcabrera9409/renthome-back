package com.surrender.repo;

import com.surrender.model.Token;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TokenRepo extends IGenericRepo<Token, Integer> {
    Page<Token> findAll(Pageable pageable);
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);
    List<Token> findByUsuarioIdAndLoggedOutFalse(Integer idUsuario);
}
