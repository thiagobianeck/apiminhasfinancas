package com.bianeck.minhasfinancas.service;

import com.bianeck.minhasfinancas.exceptions.RegraNegocioException;
import com.bianeck.minhasfinancas.model.entity.Lancamento;
import com.bianeck.minhasfinancas.model.entity.Usuario;
import com.bianeck.minhasfinancas.model.enums.StatusLancamento;
import com.bianeck.minhasfinancas.model.repository.LancamentoRepository;
import com.bianeck.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.bianeck.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.bianeck.minhasfinancas.model.repository.LancamentoRepositoryTest.criarLancamento;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    @DisplayName("Deve salvar um Lançamento")
    public void deveSalvarumLancamento(){
        Lancamento lancamentoASalvar = criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    @DisplayName("Não deve salvar um lançamento quando houver erro de validação")
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        Lancamento lancamentoASalvar = criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    @DisplayName("Deve atualizar um Lançamento")
    public void deveAtualizarumLancamento(){

        Lancamento lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);
        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.atualizar(lancamentoSalvo);

        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar um lançamento que ainda não foi salvo.")
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamento = criarLancamento();

        catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);

        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }

    @Test
    @DisplayName("Deve deletar um lançamento.")
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = criarLancamento();
        lancamento.setId(1L);

        service.deletar(lancamento);

        Mockito.verify(repository).delete(lancamento);

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar um lançamento que ainda não foi salvo.")
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamento = criarLancamento();

        catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

}