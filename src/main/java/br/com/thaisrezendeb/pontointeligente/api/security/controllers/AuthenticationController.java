package br.com.thaisrezendeb.pontointeligente.api.security.controllers;

import br.com.thaisrezendeb.pontointeligente.api.response.Response;
import br.com.thaisrezendeb.pontointeligente.api.security.dto.JwtAuthenticationDto;
import br.com.thaisrezendeb.pontointeligente.api.security.dto.TokenDto;
import br.com.thaisrezendeb.pontointeligente.api.security.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private static final String TOKEN_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Gera e retorna um novo token JWT
     *
     * @param jwtAuthenticationDto
     * @param result
     * @return ResponseEntity<Response<TokenDto>>
     * @throws AuthenticationException
     */
    @PostMapping
    public ResponseEntity<Response<TokenDto>> generateTokenJwt(
            @Valid @RequestBody JwtAuthenticationDto jwtAuthenticationDto,
            BindingResult result)
            throws AuthenticationException
    {
        Response<TokenDto> response = new Response<TokenDto>();
        if(result.hasErrors()) {
            log.error("Erro validando autenticacao: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        log.info("Gerando token para o email {}", jwtAuthenticationDto.getEmail());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtAuthenticationDto.getEmail(), jwtAuthenticationDto.getSenha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtAuthenticationDto.getEmail());
        String token = jwtTokenUtil.getToken(userDetails);
        response.setData(new TokenDto(token));
        return ResponseEntity.ok(response);
    }

    /**
     * Gera um novo token com uma nova data de expiracao
     *
     * @param request
     * @return ResponseEntity<Response<TokenDto>>
     */
    @PostMapping(value = "/refresh")
    public ResponseEntity<Response<TokenDto>> generateRefreshTokenJwt(HttpServletRequest request) {
        log.info("Gerando refresh token JWT");;
        Response<TokenDto> response = new Response<TokenDto>();
        Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));

        if(token.isPresent() && token.get().startsWith(BEARER_PREFIX)) {
            token = Optional.of(token.get().substring(7));
        }

        if(!token.isPresent()) {
            response.getErrors().add("Token nao informado");
        }
        else if(!jwtTokenUtil.isValidToken(token.get())) {
            response.getErrors().add("Token invalido ou expirado");
        }

        if(!response.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(response);
        }

        String refreshedToken = jwtTokenUtil.refreshToken(token.get());
        response.setData(new TokenDto(refreshedToken));
        return ResponseEntity.ok(response);
    }
}
