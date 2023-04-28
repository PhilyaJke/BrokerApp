package accelerator.group.brokerapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Getter
@AllArgsConstructor
@Table(name = "brokerage_portfolio_securities")
public class BrokeragePortfolioSecurities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Securities securities;

    private long count;

    public BrokeragePortfolioSecurities(Securities securities, long count) {
        this.securities = securities;
        this.count = count;
    }

    public BrokeragePortfolioSecurities() {

    }
}
