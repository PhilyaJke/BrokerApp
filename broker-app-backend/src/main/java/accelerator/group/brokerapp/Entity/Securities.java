package accelerator.group.brokerapp.Entity;

import accelerator.group.brokerapp.Responses.StocksResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;



@NamedNativeQuery(
        name = "GetAllStocksInfoForSearch",
        query = "SELECT s.region, asi.price, s.name, s.ticker, s.sector FROM Securities s INNER JOIN additional_stocks_info asi ON asi.id = s.add_info_id",
        resultSetMapping = "Mapping.StocksResponseForSearch")

@SqlResultSetMapping(name = "Mapping.StocksResponseForSearch",
        classes = @ConstructorResult(targetClass = StocksResponse.class,
        columns = {@ColumnResult(name = "region"),
                @ColumnResult(name = "price"),
                @ColumnResult(name = "name"),
                @ColumnResult(name = "ticker"),
                @ColumnResult(name = "sector")}
        ))

@Entity
@Data
@AllArgsConstructor
@Table(name = "Securities")
public class Securities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private long id;

    @JsonIgnore
    @Column(name = "figi")
    private String Figi;

    @Column(name = "name")
    private String Name;

    @Column(name = "ticker")
    private String Ticker;

    @Column(name = "region")
    private String region;

    @Column(name = "sector")
    private String Sector;

    @OneToOne
    @JoinColumn(name = "add_info_id")
    private AdditionalStocksInfo additionalStocksInfo;

    public Securities() {

    }

    public Securities(String figi, String name, String ticker, String region, String sector, AdditionalStocksInfo additionalStocksInfo) {
        Figi = figi;
        Name = name;
        Ticker = ticker;
        this.region = region;
        Sector = sector;
        this.additionalStocksInfo = additionalStocksInfo;
    }
}
