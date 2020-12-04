package com.bianeck.minhasfinancas.model.repository;

import com.bianeck.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UsuarioRepository repository;

    @Test
    @DisplayName("Deve verificar a existência de um e-mail.")
    public void deveVerificarAExistenciaDeUmEmail() {
        // cenário
        String email = "usuario@email.com";
        Usuario usuario = Usuario.builder()
                .nome("usuario")
                .email(email)
                .build();
        entityManager.persist(usuario);
        // ação / execução
        boolean result = repository.existsByEmail(email);

        // verificação
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve Retornar falso quando não houver usuário cadastrado com o e-mail.")
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoCOmOEmail() {

        boolean result = repository.existsByEmail("usuario@email.com");

        Assertions.assertThat(result).isFalse();

    }

    @Test
    @DisplayName("Deve Persistir um usuário na base de dados.")
    public void devePersistirUmUsuarioNaBaseDeDados() {
        Usuario usuario = criarUsuario();

        Usuario usuarioSalvo = repository.save(usuario);

        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

    }

    @Test
    @DisplayName("Deve buscar um usuário por email.")
    public void deveBuscarUmUsuarioPorEmail() {
        Usuario usuario = criarUsuario();

        entityManager.persist(usuario);

        Optional<Usuario> result = repository.findByEmail(criarUsuario().getEmail());

        Assertions.assertThat(result.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar um usuário por email quando não existe na base.")
    public void deveRetornarVazioAoBuscarUmUsuarioPorEmail() {
        Optional<Usuario> result = repository.findByEmail(criarUsuario().getEmail());

        Assertions.assertThat(result.isPresent()).isFalse();

    }

    public static Usuario criarUsuario() {
        return Usuario.builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }

}