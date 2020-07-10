package br.com.thaisrezendeb.pontointeligente.api.controllers;

import br.com.thaisrezendeb.pontointeligente.api.dtos.CadastroPessoaJuridicaDto;
import br.com.thaisrezendeb.pontointeligente.api.entities.Empresa;
import br.com.thaisrezendeb.pontointeligente.api.entities.Funcionario;
import br.com.thaisrezendeb.pontointeligente.api.enums.PerfilEnum;
import br.com.thaisrezendeb.pontointeligente.api.response.Response;
import br.com.thaisrezendeb.pontointeligente.api.services.EmpresaService;
import br.com.thaisrezendeb.pontointeligente.api.services.FuncionarioService;
import br.com.thaisrezendeb.pontointeligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")     // permite requisicoes de qualquer dominio
public class CadastroPessoaJuridicaController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPessoaJuridicaController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private EmpresaService empresaService;

    /**
     * Cadastra uma pessoa juridica no sistema
     * 
     * @param cadastroPessoaJuridicaDto
     * @param result
     * @return ResponseEntity<Response<CadastroPessoaJuridicaDto>>
     * @throws NoSuchAlgorithmException
     */
    @PostMapping
    public ResponseEntity<Response<CadastroPessoaJuridicaDto>> cadastrar(@Valid @RequestBody CadastroPessoaJuridicaDto cadastroPessoaJuridicaDto, BindingResult result) throws NoSuchAlgorithmException {
        log.info("Cadastrando PJ: {}", cadastroPessoaJuridicaDto.toString());

        Response<CadastroPessoaJuridicaDto> response = new Response<CadastroPessoaJuridicaDto>();

        validarDadosExistentes(cadastroPessoaJuridicaDto, result);

        Empresa empresa = this.converterDtoParaEmpresa(cadastroPessoaJuridicaDto);
        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPessoaJuridicaDto, result);

        if(result.hasErrors()) {
            log.error("Erro validando dados de cadastro PJ {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.empresaService.persistir(empresa);
        funcionario.setEmpresa(empresa);
        this.funcionarioService.persistir(funcionario);

        response.setData(this.converterCadastroPessoaJuridicaDto(funcionario));
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica se a empresa ou funcionario ja existe na base de dados
     *
     * @param cadastroPessoaJuridicaDto
     * @param result
     */
    private void validarDadosExistentes(CadastroPessoaJuridicaDto cadastroPessoaJuridicaDto, BindingResult result) {
        this.empresaService.buscarPorCnpj(cadastroPessoaJuridicaDto.getCnpj()).ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa ja existe")));
        this.funcionarioService.buscarPorCpf(cadastroPessoaJuridicaDto.getCpf()).ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF ja existe")));
        this.funcionarioService.buscarPorEmail(cadastroPessoaJuridicaDto.getEmail()).ifPresent(func -> result.addError(new ObjectError("funcionario", "Email ja existe")));
    }

    /**
     * Converte dados do DTO para Empresa
     *
     * @param cadastroPessoaJuridicaDto
     * @return Empresa
     */
    private Empresa converterDtoParaEmpresa(CadastroPessoaJuridicaDto cadastroPessoaJuridicaDto) {
        Empresa empresa = new Empresa();
        empresa.setCnpj(cadastroPessoaJuridicaDto.getCnpj());
        empresa.setRazaoSocial(cadastroPessoaJuridicaDto.getRazaoSocial());
        return empresa;
    }

    /**
     * Converte dados do DTO para Funcionario
     *
     * @param cadastroPessoaJuridicaDto
     * @return Funcionario
     * @throws NoSuchAlgorithmException
     */
    private Funcionario converterDtoParaFuncionario(CadastroPessoaJuridicaDto cadastroPessoaJuridicaDto, BindingResult result) throws NoSuchAlgorithmException {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(cadastroPessoaJuridicaDto.getNome());
        funcionario.setEmail(cadastroPessoaJuridicaDto.getEmail());
        funcionario.setCpf(cadastroPessoaJuridicaDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
        funcionario.setSenha(PasswordUtils.geraBCrypt(cadastroPessoaJuridicaDto.getSenha()));
        return funcionario;
    }

    /**
     * Popula o DTO de cadastro com os dados do funcionario da empresa
     *
     * @param funcionario
     * @return CadastroPessoaJuridicaDto
     */
    private CadastroPessoaJuridicaDto converterCadastroPessoaJuridicaDto(Funcionario funcionario) {
        CadastroPessoaJuridicaDto pjDto = new CadastroPessoaJuridicaDto();
        pjDto.setId(funcionario.getId());
        pjDto.setNome(funcionario.getNome());
        pjDto.setEmail(funcionario.getEmail());
        pjDto.setCpf(funcionario.getCpf());
        pjDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
        pjDto.setCnpj(funcionario.getEmpresa().getCnpj());
        return pjDto;
    }
}
