package accelerator.group.brokerapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@Table(name = "Securities")
public class Securities {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonIgnore
    private long id;

    @JsonIgnore
    @Column(name = "figi")
    private String Figi;

    @Column(name = "name")
    private String name;

    @Column(name = "ticker")
    private String Ticker;

    @Column(name = "region")
    private String region;

    @Column(name = "lot")
    @JsonIgnore
    private int lot;

    @Column(name = "first_candle_day")
    @JsonIgnore
    private String date;

    @Column(name = "sector")
    private String sector;

    public Securities() {

    }
}
