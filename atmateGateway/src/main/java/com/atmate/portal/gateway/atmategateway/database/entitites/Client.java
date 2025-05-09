package com.atmate.portal.gateway.atmategateway.database.entitites;

import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String name;

    @Column(nullable = false, unique = true)
    private Integer nif;

    @ManyToOne
    @JoinColumn(name = "client_type", nullable = false)
    private ClientType clientType;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 1)
    @Pattern(regexp = "^[MF]$", message = "O gênero deve ser 'M' ou 'F'")
    private String gender;

    @Column(length = 50)
    private String nationality;

    @Column(name = "associated_colaborator", length = 50)
    private String associatedColaborator;

    @Column(name = "last_refresh_date")
    private LocalDateTime lastRefreshDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getGender() {
        return gender != null ? switch (gender) {
            case "M" -> "Masculino";
            case "F" -> "Feminino";
            default -> "Outro";
        } : "Outro";
    }

    public String getAssociatedColaborator(){
        return associatedColaborator != null ? associatedColaborator : "Não associado";
    }

    public Client(Integer nif, ClientType clientType){
        this.nif = nif;
        this.name = "A obter dados de novo cliente...";
        this.clientType = clientType;
    }
}
