package br.com.thaisrezendeb.pontointeligente.api.controllers;

import br.com.thaisrezendeb.pontointeligente.api.dtos.CadastroPessoaFisicaDto;
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

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPessoaFisicaController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPessoaFisicaController.class);

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private FuncionarioService funcionarioService;

    @PostMapping
    public ResponseEntity<Response<CadastroPessoaFisicaDto>> cadastrar(
            @Valid @RequestBody CadastroPessoaFisicaDto cadastroPessoaFisicaDto,
            BindingResult result)
            throws NoSuchAlgorithmException {
        log.info("Cadastrando PF {}", cadastroPessoaFisicaDto.toString());

        Response<CadastroPessoaFisicaDto> response = new Response<CadastroPessoaFisicaDto>();

        validarDadosExistentes(cadastroPessoaFisicaDto, result);

        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPessoaFisicaDto, result);

        if(result.hasErrors()) {
            log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPessoaFisicaDto.getCnpj());
        empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
        this.funcionarioService.persistir(funcionario);

        return ResponseEntity.ok(response);
    }

    /**
     * Verifica se a empresa esta cadastrada e se o funcionario nao existe na base de dados
     *
     * @param pfDto
     * @param result
     */
    private void validarDadosExistentes(CadastroPessoaFisicaDto pfDto, BindingResult result) {
        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(pfDto.getCnpj());
        if(!empresa.isPresent())
            result.addError(new ObjectError("empresa", "Empresa nao cadastrada"));

        this.funcionarioService.buscarPorCpf(pfDto.getCpf()).ifPresent(func ->
                result.addError(new ObjectError("funcionario", "CPF ja existe")));
        this.funcionarioService.buscarPorEmail(pfDto.getEmail()).ifPresent(func ->
                result.addError(new ObjectError("funcionario", "Email ja existe")));
    }

    /**
     * Converte os dados do DTO para Funcionario
     * @param pfDto
     * @param result
     * @return Funcionario
     * @throws NoSuchAlgorithmException
     */
    private Funcionario converterDtoParaFuncionario(CadastroPessoaFisicaDto pfDto, BindingResult result)
            throws NoSuchAlgorithmException {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(pfDto.getNome());
        funcionario.setEmail(pfDto.getEmail());
        funcionario.setCpf(pfDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.geraBCrypt(pfDto.getSenha()));
        pfDto.getQtdHorasAlmoco().ifPresent(qtdHorasAlmoco ->
                funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
        pfDto.getQtdHorasTrabalhoDia().ifPresent(qtdHorasTrabalhoDia ->
                funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
        pfDto.getValorHora().ifPresent(valorHora ->
                funcionario.setValorHora(new BigDecimal(valorHora)));

        return funcionario;
    }

    /**
     * Popula o DTO de cadastro com os dados do funcionario e empresa
     *
     * @param funcionario
     * @return CadastroPessoaFisicaDto
     */
    private CadastroPessoaFisicaDto converterFuncionarioParaDto(Funcionario funcionario) {
        CadastroPessoaFisicaDto pfDto = new CadastroPessoaFisicaDto();
        pfDto.setId(funcionario.getId());
        pfDto.setNome(funcionario.getNome());
        pfDto.setEmail(funcionario.getEmail());
        pfDto.setCpf(funcionario.getCpf());
        pfDto.setCnpj(funcionario.getEmpresa().getCnpj());
        funcionario.getQtdHorasAlmocoOpt().ifPresent(qtdHorasAlmoco ->
                pfDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
        funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(qtdHorasTrabalhoDia ->
                pfDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));
        funcionario.getValorHoraOpt().ifPresent(valorHora ->
                pfDto.setValorHora(Optional.of(valorHora.toString())));

        return pfDto;
    }
}
