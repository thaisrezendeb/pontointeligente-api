package br.com.thaisrezendeb.pontointeligente.api.dtos;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

public class FuncionarioDto {

    private Long id;
    private String nome;
    private String email;
    private Optional<String> senha = Optional.empty();
    private Optional<String> valorHora = Optional.empty();
    private Optional<String> qtdHorasTrabalhoDia = Optional.empty();
    private Optional<String> qtdHorasAlmoco = Optional.empty();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotEmpty(message = "Nome nao pode ser vazio")
    @Length(min = 3, max = 200, message = "Nome deve conter entre 3 e 200 caracteres")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @NotEmpty(message = "Email nao pode ser vazio")
    @Length(min = 5, max = 200, message = "Nome deve conter entre 5 e 200 caracteres")
    @Email(message = "Email invalido")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Optional<String> getSenha() {
        return senha;
    }

    public void setSenha(Optional<String> senha) {
        this.senha = senha;
    }

    public Optional<String> getValorHora() {
        return valorHora;
    }

    public void setValorHora(Optional<String> valorHora) {
        this.valorHora = valorHora;
    }

    public Optional<String> getQtdHorasTrabalhoDia() {
        return qtdHorasTrabalhoDia;
    }

    public void setQtdHorasTrabalhoDia(Optional<String> qtdHorasTrabalhoDia) {
        this.qtdHorasTrabalhoDia = qtdHorasTrabalhoDia;
    }

    public Optional<String> getQtdHorasAlmoco() {
        return qtdHorasAlmoco;
    }

    public void setQtdHorasAlmoco(Optional<String> qtdHorasAlmoco) {
        this.qtdHorasAlmoco = qtdHorasAlmoco;
    }

    @Override
    public String toString() {
        return "FuncionarioDto [id=" + this.id
                + ", nome=" + this.nome
                + ", email=" + this.email
                + ", senha=" + this.senha
                + ", valorHora=" + this.valorHora
                + ", qtdHorasTrabalhoDia=" + this.qtdHorasTrabalhoDia
                + ", qtdHorasAlmoco=" + this.qtdHorasAlmoco + "]";
    }
}
