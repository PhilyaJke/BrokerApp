package accelerator.group.brokerapp.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class RefreshTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;

    @Column(name = "userUUID")
    private UUID userUUID;

    @Column(name = "token")
    private String token;
}
