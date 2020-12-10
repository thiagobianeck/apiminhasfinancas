package com.bianeck.minhasfinancas.service;

import com.bianeck.minhasfinancas.exceptions.RegraNegocioException;
import com.bianeck.minhasfinancas.model.entity.Lancamento;
import com.bianeck.minhasfinancas.model.entity.Usuario;
import com.bianeck.minhasfinancas.model.enums.StatusLancamento;
import com.bianeck.minhasfinancas.model.repository.LancamentoRepository;
import com.bianeck.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve filtrar lançamentos.")
    public void deveFiltrarLancamentos() {
        Lancamento lancamento = criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);

        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);

    }

    @Test
    @DisplayName("Deve atualizar o Status de um lancamento")
    public void deveAtualizarOStatusDeUmLancamento() {
        Lancamento lancamento = criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;

        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);

    }

    @Test
    @DisplayName("Deve obter um lançamento por id.")
    public void deveObterUmLancamentoPorId() {
        Lancamento lancamento = criarLancamento();
        Long id = 1L;
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.obterPorId(id);

        assertThat(resultado.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Deve retornar vazio quando o lancamento não existe.")
    public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
        Lancamento lancamento = criarLancamento();
        Long id = 1L;
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.obterPorId(id);

        assertThat(resultado.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve lançar erros ao validar um lançamento.")
    public void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        Throwable erro = catchThrowable(() -> service.validar(lancamento));

        testaValidacao(erro, lancamento, "Informe uma Descrição válida.");
        lancamento.setDescricao("");
        testaValidacao(erro, lancamento, "Informe uma Descrição válida.");
        lancamento.setDescricao("Salário");
        testaValidacao(erro, lancamento, "Informe um Mês válido.");
        lancamento.setMes(12);
        testaValidacao(erro, lancamento, "Informe um Ano válido.");
        lancamento.setAno(1984);
        testaValidacao(erro, lancamento, "Informe um Usuário.");
        lancamento.setUsuario(Usuario.builder().id(1L).build());
        testaValidacao(erro, lancamento, "Informe um Valor válido.");
        lancamento.setValor(BigDecimal.TEN);
        testaValidacao(erro, lancamento, "Informe um tipo de lançamento.");
        lancamento.setStatus(StatusLancamento.PENDENTE);

    }

    private void testaValidacao(Throwable erro, Lancamento lancamento, String mensagem) {
        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(mensagem);
    }

//        throw new RegraNegocioException("Informe um Mês válido.");
//        throw new RegraNegocioException("Informe um Ano válido.");
//        throw new RegraNegocioException("Informe um Usuário.");
//        throw new RegraNegocioException("Informe um Valor válido.");
//        throw new RegraNegocioException("Informe um tipo de lançamento.");

}