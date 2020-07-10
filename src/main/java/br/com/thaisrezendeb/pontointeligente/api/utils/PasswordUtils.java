package br.com.thaisrezendeb.pontointeligente.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordUtils.class);

    /**
     * Gera um hash utilizando o BCrypt.
     *
     * @param senha
     * @return String
     */
    public static String geraBCrypt(String senha) {
        if(senha == null)
            return senha;

        log.info("Gerando hash com BCrypt");
        BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
        return bCryptEncoder.encode(senha);
    }
}
