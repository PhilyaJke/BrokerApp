package accelerator.group.brokerapp.Entity;

import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;


@NamedNativeQueries({
        @NamedNativeQuery(
                name = "FindFullStocksInfo",
                query = "SELECT s.region, s.figi, asi.price, s.name, s.ticker, s.sector, s.icon_path" +
                        " FROM securities s" +
                        " INNER JOIN additional_stocks_information asi on asi.id = s.additional_info_id",
                resultSetMapping = "Mapping.SecuritiesFullInfoResponse"
        ),

        @NamedNativeQuery(
                name = "FindRUStocksInfo",
                query = "SELECT s.region, s.figi, asi.price, s.name, s.ticker, s.sector, s.icon_path" +
                        " FROM securities s" +
                        " INNER JOIN additional_stocks_information asi on asi.id = s.additional_info_id WHERE s.region = 'RU'",
                resultSetMapping = "Mapping.SecuritiesFullInfoResponse"
        ),

        @NamedNativeQuery(
                name = "FindForeignStocksInfo",
                query = "SELECT s.region, s.figi, asi.price, s.name, s.ticker, s.sector, s.icon_path" +
                        " FROM securities s" +
                        " INNER JOIN additional_stocks_information asi on asi.id = s.additional_info_id WHERE s.region <> 'RU'",
                resultSetMapping = "Mapping.SecuritiesFullInfoResponse"
        ),

        @NamedNativeQuery(
                name = "CountSecurities",
                query = "SELECT count(*) FROM securities"
        )
})

@SqlResultSetMapping(name="Mapping.SecuritiesFullInfoResponse",
        classes = { @ConstructorResult(targetClass = SecuritiesFullInfoResponse.class,
                columns = {
                        @ColumnResult(name = "region"),
                        @ColumnResult(name = "figi"),
                        @ColumnResult(name = "price"),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "ticker"),
                        @ColumnResult(name = "sector"),
                        @ColumnResult(name = "icon_path")
                })
        }
)


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

    @Column(name = "icon_path")
    private String iconPath;

    @OneToOne
    @MapsId
    @JoinColumn(name = "additional_info_id")
    private AdditionalStocksInformation additionalStocksInformation;

    public Securities() {

    }

    public Securities(String figi, String name, String ticker, String region, String sector, AdditionalStocksInformation additionalStocksInformation) {
        Figi = figi;
        Name = name;
        Ticker = ticker;
        this.region = region;
        Sector = sector;
        this.additionalStocksInformation = additionalStocksInformation;
    }
}
