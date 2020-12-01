package com.bianeck.minhasfinancas.model.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "usuario", schema = "financas")
@Data
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

}
