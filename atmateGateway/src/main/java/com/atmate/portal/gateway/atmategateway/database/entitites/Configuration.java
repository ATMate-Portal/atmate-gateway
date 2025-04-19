package com.atmate.portal.gateway.atmategateway.database.entitites;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "O nome da configuração é obrigatório")
    @Size(max = 50, message = "O nome da configuração deve ter no máximo 50 caracteres")
    private String varname;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "O valor da configuração é obrigatório")
    @Size(max = 255, message = "O valor da configuração deve ter no máximo 255 caracteres")
    private String varvalue;

    @Column(length = 255)
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String description;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}