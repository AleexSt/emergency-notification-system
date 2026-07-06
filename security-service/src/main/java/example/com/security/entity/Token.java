package example.com.security.entity;


import example.com.security.model.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "client_id")
    Client client;

    @Enumerated(EnumType.STRING)
    public final TokenType tokenType = TokenType.BEARER;

    @Column(unique = true, nullable = false)
    public String jwt;

    public boolean revoked;
    public boolean expired;

}
