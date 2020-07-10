package br.com.thaisrezendeb.pontointeligente.api.services.impl;

import br.com.thaisrezendeb.pontointeligente.api.entities.Lancamento;
import br.com.thaisrezendeb.pontointeligente.api.repositories.LancamentoRepository;
import br.com.thaisrezendeb.pontointeligente.api.services.LancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Override
    public Page<Lancamento> bucarLancamentoPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
        log.info("Buscando lancamento pelo ID do funcionario {}");
        return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
    }

    @Override
    public Optional<Lancamento> buscalancamentoPorId(Long id) {
        log.info("Buscando lancamento pelo ID {}", id);
        return this.lancamentoRepository.findById(id);
    }

    @Override
    public Lancamento persistir(Lancamento lancamento) {
        log.info("Persistindo lancamento: {}", lancamento);
        return this.lancamentoRepository.save(lancamento);
    }

    @Override
    public void remover(Long id) {
        log.info("Removendo o lancamento de ID {}", id);
        this.lancamentoRepository.deleteById(id);
    }
}
