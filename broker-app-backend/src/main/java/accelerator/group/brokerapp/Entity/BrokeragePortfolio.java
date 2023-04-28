package accelerator.group.brokerapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "FindUsersSecurities",
                query = " SELECT s.figi, s.name, s.ticker, s.region, s.sector FROM securities s WHERE s.id IN (SELECT bpbps.brokerage_portfolio_securities_id FROM brokerage_portfolio b" +
                        " INNER JOIN" +
                        " brokerage_portfolio_brokerage_portfolio_securities bpbps on b.id = bpbps.brokerage_portfolio_id " +
                        "WHERE cast(b.user_id as varchar(255)) LIKE cast(?1 as varchar(255)))",
                resultSetMapping = "Mapping.FindUsersSecurities"
        )
})


@SqlResultSetMapping(name="Mapping.FindUsersSecurities",
        classes = { @ConstructorResult(targetClass = Securities.class,
                columns = {
                        @ColumnResult(name = "figi"),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "ticker"),
                        @ColumnResult(name = "region"),
                        @ColumnResult(name = "sector")
                })
        }
)

@Entity
@Data
@Getter
@AllArgsConstructor
@Table(name = "brokerage_portfolio")
public class BrokeragePortfolio {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    @JsonIgnore
    private UUID id;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<BrokeragePortfolioSecurities> BrokeragePortfolioSecurities;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    public BrokeragePortfolio() {

    }
}
