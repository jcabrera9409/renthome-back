package com.surrender.repo;

import com.surrender.model.Usuario;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepo extends IGenericRepo<Usuario, Integer> {
    // Puedes agregar métodos personalizados aquí si es necesario
    Page<Usuario> findAll(Pageable pageable);
    Optional<Usuario> findByEmail(String email);

    @Transactional
	@Modifying
	@Query("UPDATE Usuario u SET u.password = :password WHERE u.id = :id")
	int updatePasswordById(@Param("id") Integer id, @Param("password") String password);
}
