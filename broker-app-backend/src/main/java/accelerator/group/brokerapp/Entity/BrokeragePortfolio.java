package accelerator.group.brokerapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "FindUserssSecurities",
                query = "SELECT s.region, s.figi, s.name, s.ticker, s.sector, s.icon_path FROM securities s" +
                        " WHERE s.id IN (SELECT bps.securities_id FROM brokerage_portfolio_securities bps" +
                        " INNER JOIN brokerage_portfolio bp on bp.id = bps.brokerage_portfolio_id" +
                        " WHERE (cast(bp.user_id as varchar(255)) = cast(?1 as varchar(255))))",
                resultSetMapping = "Mapping.FindUsersSecurities"
        )
})


@SqlResultSetMappings({

        @SqlResultSetMapping(name="Mapping.FindUserssSecurities",
                classes = { @ConstructorResult(targetClass = Securities.class,
                        columns = {
                                @ColumnResult(name = "region", type = String.class),
                                @ColumnResult(name = "figi", type = String.class),
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
@AllArgsConstructor
@Table(name = "brokerage_portfolio")
public class BrokeragePortfolio {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    @JsonIgnore
    private UUID id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    public BrokeragePortfolio() {

    }
}
