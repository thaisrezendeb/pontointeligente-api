package br.com.thaisrezendeb.pontointeligente.api.repositories;

import br.com.thaisrezendeb.pontointeligente.api.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true) // Não faz lock no banco de dados
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Funcionario findByCpf(String cpf);
    Funcionario findByEmail(String email);
    Funcionario findByCpfOrEmail(String cpf, String email);

}
