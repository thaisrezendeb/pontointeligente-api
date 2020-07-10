package br.com.thaisrezendeb.pontointeligente.api.utils;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

public class PasswordUtilsTest {

    private static final String SENHA = "123456";
    private final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

    @Test
    public void testSenhaNula() throws Exception {
        Assertions.assertNull(PasswordUtils.geraBCrypt(null));
    }

    @Test
    public void testGeraHashsenha() throws Exception {
        String hash = PasswordUtils.geraBCrypt(SENHA);
        Assertions.assertTrue(bCryptEncoder.matches(SENHA, hash));
    }
}
