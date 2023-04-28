package accelerator.group.brokerapp.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Data
@Getter
@AllArgsConstructor
@Table(name = "additional_stocks_information")
public class AdditionalStocksInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    @JsonIgnore
    private long id;

    @Column(name = "price")
    private Double price;

    @Column(name = "lot")
    @JsonIgnore
    private int Lot;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "securities_id")
    private Securities securities;

    public AdditionalStocksInformation() {

    }

    public AdditionalStocksInformation(int lot, Securities securities) {
        Lot = lot;
        this.securities = securities;
    }
}
