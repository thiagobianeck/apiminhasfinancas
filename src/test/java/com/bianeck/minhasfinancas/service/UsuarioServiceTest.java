package com.bianeck.minhasfinancas.service;

import com.bianeck.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.bianeck.minhasfinancas.exceptions.RegraNegocioException;
import com.bianeck.minhasfinancas.model.entity.Usuario;
import com.bianeck.minhasfinancas.model.repository.UsuarioRepository;
import com.bianeck.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test
    @DisplayName("Deve salvar um usuario.")
    public void deveSalvarUmUsuario() {
        Mockito.doNothing().when(service).validarEmail(anyString());
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();

        when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioSalvo = Assertions.assertDoesNotThrow(() ->
                service.salvarUsuario(new Usuario()));

        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");


    }

    @Test
    @DisplayName("Não deve salvar um usuario com email já cadastrado.")
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        String email = "email@email.com";
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email(email)
                .build();

        Mockito.doThrow(RegraNegocioException.class)
                .when(service).validarEmail(email);

        Assertions.assertThrows(RegraNegocioException.class, () ->
            service.salvarUsuario(usuario));

        Mockito.verify(repository, Mockito.never())
                .save(usuario);

    }

    @Test
    @DisplayName("Deve validar email.")
    public void deveValidarEmail() {

        when(repository.existsByEmail(anyString())).thenReturn(false);

        Assertions.assertDoesNotThrow(() ->
                service.validarEmail("email@email.com"));

    }

    @Test
    @DisplayName("Deve lançar erro ao validar e-mail quando existir email cadastrado.")
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

        when(repository.existsByEmail(anyString())).thenReturn(true);

        Assertions.assertThrows(RegraNegocioException.class, () ->
                service.validarEmail("email@email.com"));
    }

    @Test
    @DisplayName("Deve autenticar um usuário com sucesso.")
    public void deveAutenticarUmUsuarioComSucesso() {

        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder()
                .email(email)
                .senha(senha)
                .id(1L)
                .build();

        when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario resultado = Assertions.assertDoesNotThrow(() ->
                service.autenticar(email, senha));

        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar erro quando não encontrar usuário cadastrado com o e-mail informado.")
    public void deveLancarErroQuandoEncontrarUsuarioCadastradoComOEmailInformado() {

        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        Throwable exception = Assertions.assertThrows(ErroAutenticacaoException.class, () ->
                service.autenticar("email@email.com", "senha"));

        assertThat(exception).isInstanceOf(ErroAutenticacaoException.class)
                .hasMessage("usuário não encontrado para o e-mail informado.");

    }

    @Test
    @DisplayName("Deve lançar erro quando senha não bater.")
    public void deveLancarErroQuandoSenhaNaoBater() {

        String senha = "senha";
        Usuario usuario = Usuario.builder()
                .email("email@email.com")
                .senha(senha)
                .build();

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        Throwable exception = Assertions.assertThrows(ErroAutenticacaoException.class, () ->
                service.autenticar("email@email.com", "123"));

        assertThat(exception).isInstanceOf(ErroAutenticacaoException.class)
                .hasMessage("Senha inválida.");

    }

}