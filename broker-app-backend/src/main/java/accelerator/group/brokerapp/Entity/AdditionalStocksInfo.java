package accelerator.group.brokerapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@Table(name = "AdditionalStocksInfo")
public class AdditionalStocksInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "lot")
    @JsonIgnore
    private int Lot;

    @Column(name = "price")
    private Double price;

    public AdditionalStocksInfo() {

    }

    public AdditionalStocksInfo(int lot) {
        Lot = lot;
    }
}
