package accelerator.group.brokerapp.Entity;

import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "FindCurrentUsersSecurities",
                query = "SELECT bps.id, bps.securities_id, bps.brokerage_portfolio_id, bps.count FROM brokerage_portfolio_securities bps INNER JOIN brokerage_portfolio bp on bp.id = bps.brokerage_portfolio_id" +
                        " WHERE (cast(bp.user_id as varchar(255)) = cast(?1 as varchar(255)) AND (bps.securities_id = ?2))",
                resultSetMapping = "Mapping.FindCurrentUsersSecurities"
        ),
        @NamedNativeQuery(
                name = "FindUsersSecurities",
                query = "SELECT s.region, asi.price, s.name, s.ticker, s.sector, s.icon_path FROM securities s" +
                        " INNER JOIN additional_stocks_information asi on s.id = asi.securities_id" +
                        " WHERE s.id IN (SELECT bps.securities_id FROM brokerage_portfolio_securities bps" +
                        " INNER JOIN brokerage_portfolio bp on bp.id = bps.brokerage_portfolio_id" +
                        " WHERE (cast(bp.user_id as varchar(255)) = cast(?1 as varchar(255))))",
                resultSetMapping = "Mapping.FindUsersSecurities"
        )
})


@SqlResultSetMappings({

        @SqlResultSetMapping(name="Mapping.FindCurrentUsersSecurities",
                classes = { @ConstructorResult(targetClass = BrokeragePortfolioSecurities.class,
                        columns = {
                        @ColumnResult(name = "id", type = long.class),
                        })
        }),

        @SqlResultSetMapping(name="Mapping.FindUsersSecurities",
        classes = { @ConstructorResult(targetClass = SecuritiesFullInfoResponse.class,
                columns = {
                        @ColumnResult(name = "region", type = String.class),
                        @ColumnResult(name = "price", type = Double.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "ticker", type = String.class),
                        @ColumnResult(name = "sector", type = String.class),
                        @ColumnResult(name = "icon_path", type = String.class)
                })
        })
})


@Entity
@Data
@Getter
@Table(name = "brokerage_portfolio_securities")
public class BrokeragePortfolioSecurities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Securities securities;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private BrokeragePortfolio brokeragePortfolio;

    private long count;

    public BrokeragePortfolioSecurities(Securities securities, BrokeragePortfolio brokeragePortfolio, long count) {
        this.securities = securities;
        this.brokeragePortfolio = brokeragePortfolio;
        this.count = count;
    }

    public BrokeragePortfolioSecurities(long id) {
        this.id = id;
    }

    public BrokeragePortfolioSecurities() {

    }
}
