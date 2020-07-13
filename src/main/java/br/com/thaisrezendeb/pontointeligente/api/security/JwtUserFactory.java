package br.com.thaisrezendeb.pontointeligente.api.security;

import br.com.thaisrezendeb.pontointeligente.api.entities.Funcionario;

public class JwtUserFactory {

    /**
     * Converte e gera um JWT user baseado em um Funcionario
     *
     * @param funcionario
     * @return JwtUser
     */
    public static JwtUser create(Funcionario funcionario) {
        return new JwtUser(funcionario.getId(),
                funcionario.getEmail(),
                funcionario.getSenha(),
                mapToGrantedAuthorities(funcionario.getPerfil()));
    }

    private static 
}
