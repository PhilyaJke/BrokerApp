package accelerator.group.brokerapp.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@Table(name = "additional_stocks_information")
public class AdditionalStocksInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private long id;

    @Column(name = "price")
    private Double price;

    @Column(name = "lot")
    @JsonIgnore
    private int Lot;

    public AdditionalStocksInformation() {

    }

    public AdditionalStocksInformation(int lot) {
        Lot = lot;
    }
}
