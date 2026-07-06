package com.example.template.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "templates")
@Entity()
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToMany(mappedBy = "template",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
            )
    @Builder.Default
    private List<RecipientId> recipientIds = new ArrayList<>();

    public Template addClient(Long clientId){
        setClientId(clientId);
        return this;
    }

    public Template addRecipient(Long recipientId){
        recipientIds.add(RecipientId.builder()
                .recipientId(recipientId)
                .template(this)
                .build());
        return this;
    }
}
