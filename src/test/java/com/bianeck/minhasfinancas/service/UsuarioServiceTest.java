package com.bianeck.minhasfinancas.service;

import com.bianeck.minhasfinancas.exceptions.RegraNegocioException;
import com.bianeck.minhasfinancas.model.entity.Usuario;
import com.bianeck.minhasfinancas.model.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @Autowired
    UsuarioService service;

    @Autowired
    UsuarioRepository repository;

    @Test
    @DisplayName("Deve validar email.")
    public void deveValidarEmail() {

        repository.deleteAll();

        Assertions.assertDoesNotThrow(() ->
                service.validarEmail("email@email.com"));

    }

    @Test
    @DisplayName("Deve lançar erro ao validar e-mail quando existir email cadastrado.")
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

        Usuario usuario = Usuario.builder()
                .nome("usuario")
                .email("email@email.com")
                .build();

        repository.save(usuario);

        Assertions.assertThrows(RegraNegocioException.class, () ->
                service.validarEmail("email@email.com"));
    }

}