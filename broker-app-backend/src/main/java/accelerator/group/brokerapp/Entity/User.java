package accelerator.group.brokerapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import javax.persistence.GeneratedValue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Getter
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    @JsonIgnore
    private UUID id;

    @Column(name = "username", columnDefinition = "VARCHAR(255)")
    private String username;

    @Column(name = "password", columnDefinition = "VARCHAR(255)")
    private String password;

    @Column(name = "date_of_regestration", columnDefinition = "VARCHAR(255)")
    private String date;

    @Column(name = "age", columnDefinition = "VARCHAR(255)")
    private String age;

    @Column(name = "email", columnDefinition = "VARCHAR(255)", unique = true)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", columnDefinition = "VARCHAR(255)")
    @JsonIgnore
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(255)")
    @JsonIgnore
    private Status status;

    public User(){

    }

    public User(String username, String password, String age, String email, Role role, Status status) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    @PrePersist
    public void init(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        date = LocalDateTime.now().format(formatter);
    }
}
