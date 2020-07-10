package br.com.thaisrezendeb.pontointeligente.api.services;

import br.com.thaisrezendeb.pontointeligente.api.entities.Empresa;
import br.com.thaisrezendeb.pontointeligente.api.repositories.EmpresaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class EmpresaServiceTest {

    @MockBean
    private EmpresaRepository empresaRepository;

    @Autowired
    private EmpresaService empresaService;

    private static final String CNPJ = "23355544000171";

    @BeforeEach
    public void setup() throws Exception {
        BDDMockito.given(this.empresaRepository.findByCnpj(Mockito.anyString())).willReturn(new Empresa());
        BDDMockito.given(this.empresaRepository.save(Mockito.any(Empresa.class))).willReturn(new Empresa());
    }

    @Test
    public void testBuscarEmpresaPorCnpj() {
        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(CNPJ);
        Assertions.assertTrue(empresa.isPresent());
    }

    @Test
    public void testPersistirEmpresa() {
        Empresa empresa = this.empresaService.persistir(new Empresa());
        Assertions.assertNotNull(empresa);
    }
}
