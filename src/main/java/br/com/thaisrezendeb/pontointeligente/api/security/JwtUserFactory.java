package br.com.thaisrezendeb.pontointeligente.api.security;

import br.com.thaisrezendeb.pontointeligente.api.entities.Funcionario;
import br.com.thaisrezendeb.pontointeligente.api.enums.PerfilEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Converte o perfil do usuario para o formato utilizado pelo Spring Security
     *
     * @param perfilEnum
     * @return List<GrantedAuthority>
     */
    private static List<GrantedAuthority> mapToGrantedAuthorities(PerfilEnum perfilEnum) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(perfilEnum.toString()));
        return authorities;
    }
}
