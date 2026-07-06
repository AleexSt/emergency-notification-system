package com.example.recipient.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recipient")
public class Recipient {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long clientId;

    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    private String telegramId;

    @OneToMany(
            mappedBy = "recipient",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true

    )
    private List<TemplateId> templateIds = new ArrayList<>();

    public Recipient addClient(Long clientId){
        setClientId(clientId);
        return this;
    }

    public Recipient removeTemplate(Long templateId){
        templateIds.removeIf(
                template -> Objects.equals(template.getTemplateId(), templateId)
        );

        return this;
    }


}
