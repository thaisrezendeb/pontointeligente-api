package br.com.thaisrezendeb.pontointeligente.api.dtos;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class CadastroPessoaJuridicaDto {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String razaoSocial;
    private String cnpj;

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
    @Length(min = 5, max = 200, message = "Email deve conter entre 5 e 200 caracteres")
    @Email(message = "Email invalido")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotEmpty(message = "Senha nao pode ser vazia")
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @NotEmpty(message = "CPF nao pode ser vazio")
    @CPF(message = "CPF invalido")
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @NotEmpty(message = "Razao social nao pode ser vazia")
    @Length(min = 5, max = 200, message = "Razao social deve conter entre 5 e 200 caracteres")
    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    @NotEmpty(message = "CNPJ nao pode ser vazio")
    @CNPJ(message = "CNPJ invalido")
    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @Override
    public String toString() {
        return "CadastroPessoaJuridicaDto [id=" + id
                + ", nome=" + nome
                + ", email=" + email
                + ", senha=" + senha
                + ", cpf=" + cpf
                + ", razaoSocial=" + razaoSocial
                + ", cnpj=" + cnpj + "]";
    }
}
