package br.com.thaisrezendeb.pontointeligente.api.controllers;

import br.com.thaisrezendeb.pontointeligente.api.dtos.EmpresaDto;
import br.com.thaisrezendeb.pontointeligente.api.entities.Empresa;
import br.com.thaisrezendeb.pontointeligente.api.response.Response;
import br.com.thaisrezendeb.pontointeligente.api.services.EmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
//@CrossOrigin(origins = "*")
public class EmpresaController {

    private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

    @Autowired
    private EmpresaService empresaService;

    @GetMapping(value = "/cnpj/{cnpj}")
    public ResponseEntity<Response<EmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
        log.info("Buscando empresas por CNPJ: {}", cnpj);

        Response<EmpresaDto> response = new Response<EmpresaDto>();

        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cnpj);

        if(!empresa.isPresent()) {
            log.info("Empresa nao encontrada para o CNPJ {}", cnpj);
            response.getErrors().add("Empresa nao encontrada para o CNPJ " + cnpj);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(this.converterEmpresaParaDto(empresa.get()));
        return ResponseEntity.ok(response);
    }

    /**
     * Popula um DTO com os dados de uma Empresa
     *
     * @param empresa
     * @return EmpresaDto
     */
    private EmpresaDto converterEmpresaParaDto(Empresa empresa) {
        EmpresaDto empresaDto = new EmpresaDto();
        empresaDto.setId(empresa.getId());
        empresaDto.setRazaoSocial(empresa.getRazaoSocial());
        empresaDto.setCnpj(empresa.getCnpj());
        return empresaDto;
    }

}
