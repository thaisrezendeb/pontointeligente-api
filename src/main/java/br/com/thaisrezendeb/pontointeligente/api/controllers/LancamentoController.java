package br.com.thaisrezendeb.pontointeligente.api.controllers;

import br.com.thaisrezendeb.pontointeligente.api.dtos.LancamentoDto;
import br.com.thaisrezendeb.pontointeligente.api.entities.Funcionario;
import br.com.thaisrezendeb.pontointeligente.api.entities.Lancamento;
import br.com.thaisrezendeb.pontointeligente.api.enums.TipoEnum;
import br.com.thaisrezendeb.pontointeligente.api.response.Response;
import br.com.thaisrezendeb.pontointeligente.api.services.FuncionarioService;
import br.com.thaisrezendeb.pontointeligente.api.services.LancamentoService;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

    private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
    private final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Value("${paginacao.qtd_por_pagina}")
    private int qtdPorPagina;

    /**
     * Retorna a listagem de lancamentos de um funcionario
     *
     * @param funcionarioId
     * @param pag
     * @param ord
     * @param dir
     * @return ResponseEntity<Response<Page<LancamentoDto>>>
     */
    @GetMapping(value = "/funcionario/{funcionarioId}")
    public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
            @PathVariable("funcionarioId") Long funcionarioId,
            @RequestParam(value = "pag", defaultValue = "0") int pag,
            @RequestParam(value = "ord", defaultValue = "id") String ord,
            @RequestParam(value = "dir", defaultValue = "DESC") String dir)
    {
        log.info("Buscando lancamento por ID do funcionario: {}, pagina: {}", funcionarioId, pag);
        Response<Page<LancamentoDto>> response = new Response<Page<LancamentoDto>>();
        PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Sort.Direction.valueOf(dir), ord);
        Page<Lancamento> lancamentos = this.lancamentoService
                .bucarLancamentoPorFuncionarioId(funcionarioId, pageRequest);
        Page<LancamentoDto> lancamentosDto = lancamentos.map(lancamento ->
                this.converterLancamentoParaDto(lancamento));

        response.setData(lancamentosDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna um lancamento por ID
     *
     * @param id
     * @return ResponseEntity<Response<LancamentoDto>>
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id) {
        log.info("Buscando lancamento por ID: {}", id);
        Response<LancamentoDto> response = new Response<LancamentoDto>();
        Optional<Lancamento> lancamento = this.lancamentoService.buscalancamentoPorId(id);

        if(!lancamento.isPresent()) {
            log.info("Lancamento nao encontrado para o ID: {}");
            response.getErrors().add("Lancamento nao encontrado para o id " + id);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(this.converterLancamentoParaDto(lancamento.get()));
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona um novo lancamento
     *
     * @param lancamentoDto
     * @param result
     * @return ResponseEntity<Response<LancamentoDto>>
     * @throws ParseException
     */
    @PostMapping
    public ResponseEntity<Response<LancamentoDto>> adicionar(
            @Valid @RequestBody LancamentoDto lancamentoDto,
            BindingResult result)
            throws ParseException
    {
        log.info("Adicionando lancamento: {}", lancamentoDto.toString());
        Response<LancamentoDto> response = new Response<LancamentoDto>();
        validarFuncionario(lancamentoDto, result);
        Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);

        if(result.hasErrors()) {
            log.error("Erro validando lancamento: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        lancamento = this.lancamentoService.persistir(lancamento);
        response.setData(this.converterLancamentoParaDto(lancamento));
        return ResponseEntity.ok(response);
    }

    /**
     * Remove um lancamento dado um ID
     *
     * @param id
     * @return ResponseEntity<Response<String>>
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
        log.info("Removendo lancamento: {}", id);
        Response<String> response = new Response<String>();
        Optional<Lancamento> lancamento = this.lancamentoService.buscalancamentoPorId(id);

        if(!lancamento.isPresent()) {
            log.info("Erro ao remover, lancamento {} invalido", id);
            response.getErrors().add("Erro ao remover lancamento. Registro nao encontrado para id " + id);
            return ResponseEntity.badRequest().body(response);
        }

        this.lancamentoService.remover(id);
        return ResponseEntity.ok(new Response<String>());
    }

    /**
     * Atualiza dados de um lancamento
     *
     * @param id
     * @param landDto
     * @param result
     * @return ResponseEntity<Response<LancamentoDto>>
     * @throws ParseException
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<LancamentoDto>> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody LancamentoDto landDto,
            BindingResult result)
            throws ParseException
    {
        log.info("Atualizando lancamento: {}", landDto.toString());
        Response<LancamentoDto> response = new Response<LancamentoDto>();
        validarFuncionario(landDto, result);
        landDto.setId(Optional.of(id));
        Lancamento lancamento = this.converterDtoParaLancamento(landDto, result);

        if(result.hasErrors()) {
            log.error("Erro validando lancamento: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        lancamento = this.lancamentoService.persistir(lancamento);
        response.setData(this.converterLancamentoParaDto(lancamento));
        return ResponseEntity.ok(response);
    }

    /**
     * Converte uma entidade Lancamento para seu respectivo DTO
     * @param lancamento
     * @return LancamentoDto
     */
    private LancamentoDto converterLancamentoParaDto(Lancamento lancamento) {
        LancamentoDto lancDto = new LancamentoDto();
        lancDto.setId(Optional.of(lancamento.getId()));
        lancDto.setData(this.dataFormat.format(lancamento.getData()));
        lancDto.setTipo(lancamento.getTipo().toString());
        lancDto.setDescricao(lancamento.getDescricao());
        lancDto.setLocalizacao(lancamento.getLocalizacao());
        lancDto.setFuncionarioId(lancamento.getFuncionario().getId());
        return lancDto;
    }

    /**
     * Converte um lancamento DTO para uma entidade Lancamento
     *
     * @param lancDto
     * @return Lancamento
     */
    private Lancamento converterDtoParaLancamento(
            LancamentoDto lancDto,
            BindingResult result)
            throws ParseException
    {
        Lancamento lancamento = new Lancamento();

        if(lancDto.getId().isPresent()) {
            Optional<Lancamento> lanc = this.lancamentoService.buscalancamentoPorId(lancDto.getId().get());
            if(lanc.isPresent()) {
                lancamento = lanc.get();
            }
            else {
                result.addError(new ObjectError("lancamento",
                        "Lancamento nao encontrado"));
            }
        }
        else {
            lancamento.setFuncionario(new Funcionario());
            lancamento.getFuncionario().setId(lancDto.getFuncionarioId());
        }
        lancamento.setDescricao(lancDto.getDescricao());
        lancamento.setLocalizacao(lancDto.getLocalizacao());
        lancamento.setData(this.dataFormat.parse(lancDto.getData()));
        if(EnumUtils.isValidEnum(TipoEnum.class, lancDto.getTipo())) {
            lancamento.setTipo(TipoEnum.valueOf(lancDto.getTipo()));
        }
        else {
            result.addError(new ObjectError("tipo", "Tipo invalido"));
        }
        return lancamento;
    }

    /**
     * Valida um funcionario verificando se ele e' existente e valido no sistema
     *
     * @param lancDto
     * @param result
     */
    private void validarFuncionario(LancamentoDto lancDto, BindingResult result) {
        if(lancDto.getFuncionarioId() == null) {
            result.addError(new ObjectError("funcionario",
                    "Funcionario nao informado"));
            return;
        }

        log.info("Validando funcionario ID: {}", lancDto.getFuncionarioId());
        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancDto.getFuncionarioId());
        if(!funcionario.isPresent()) {
            result.addError(new ObjectError("funcionario", "Funcionario nao encontrado. ID inexistente"));
        }
    }
}
