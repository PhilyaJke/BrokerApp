package accelerator.group.brokerapp.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SecuritiesFullInfoResponse {

    private String region;
    private String figi;
    private Double price;
    private String name;
    private String ticker;
    private String sector;
    private String icon_path;

}
