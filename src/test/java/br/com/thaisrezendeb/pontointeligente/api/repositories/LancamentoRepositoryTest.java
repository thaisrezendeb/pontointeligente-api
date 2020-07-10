package br.com.thaisrezendeb.pontointeligente.api.repositories;

import br.com.thaisrezendeb.pontointeligente.api.entities.Empresa;
import br.com.thaisrezendeb.pontointeligente.api.entities.Funcionario;
import br.com.thaisrezendeb.pontointeligente.api.entities.Lancamento;
import br.com.thaisrezendeb.pontointeligente.api.enums.PerfilEnum;
import br.com.thaisrezendeb.pontointeligente.api.enums.TipoEnum;
import br.com.thaisrezendeb.pontointeligente.api.utils.PasswordUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private Long funcionarioId;

    @BeforeEach
    public void setup() throws Exception {
        Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());
        Funcionario funcionario = this.funcionarioRepository.save(obterDadosFuncionario(empresa));
        this.funcionarioId = funcionario.getId();
        this.lancamentoRepository.save(obterDadosLancamentos(funcionario));
        this.lancamentoRepository.save(obterDadosLancamentos(funcionario));
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.empresaRepository.deleteAll();
    }

    @Test
    public void testBuscarLancamentoPorFuncionarioId() {
        List<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId);

        Assertions.assertEquals(2, lancamentos.size());
    }

    @Test
    public void testBuscarLancamentoPorFuncionarioIdPaginado() {
        PageRequest page = PageRequest.of(0, 10);
        Page<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId, page);

        Assertions.assertEquals(2, lancamentos.getTotalElements());
    }

    private Lancamento obterDadosLancamentos(Funcionario funcionario) {
        Lancamento lancamento = new Lancamento();
        lancamento.setData(new Date());
        lancamento.setTipo(TipoEnum.INICIO_ALMOCO);
        lancamento.setFuncionario(funcionario);
        return lancamento;
    }

    private Funcionario obterDadosFuncionario(Empresa empresa) throws NoSuchAlgorithmException {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Fulano de Tal");
        funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.geraBCrypt("123456"));
        funcionario.setCpf("12345678901");
        funcionario.setEmail("email@dominio.com");
        funcionario.setEmpresa(empresa);
        return funcionario;
    }

    private Empresa obterDadosEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Empresa exemplo");
        empresa.setCnpj("233555444000171");
        return empresa;
    }
}
