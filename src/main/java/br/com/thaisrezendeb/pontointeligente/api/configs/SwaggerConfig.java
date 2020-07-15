package br.com.thaisrezendeb.pontointeligente.api.configs;

import br.com.thaisrezendeb.pontointeligente.api.security.services.JwtUserDetailsServiceImpl;
import br.com.thaisrezendeb.pontointeligente.api.security.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Profile("dev")
@EnableSwagger2
public class SwaggerConfig {

    /*
        http://localhost:8080/v2/api-docs
        http://localhost:8080/swagger-ui.html
     */
    private static final Logger log = LoggerFactory.getLogger(SwaggerConfig.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("br.com.thaisrezendeb.pontointeligente.api.controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public SecurityConfiguration security() {
        String token;
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername("thaisrezendeb@gmail.com");
            token = this.jwtTokenUtil.getToken(userDetails);
            log.info("Token gerado: {}", token);
        }
        catch (Exception e) {
            token = "";
            log.error("Falha ao obter token");
        }
        return new SecurityConfiguration(null,
                null,
                null,
                null,
                "Bearer " + token,
                ApiKeyVehicle.HEADER,
                "Authorization",
                ",");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Ponto Inteligente API")
                .description("Documentacao da API de acesso aos endpoints do Ponto Inteligente")
                .version("1.0")
                .build();
    }
}
