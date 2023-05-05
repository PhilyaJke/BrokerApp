package accelerator.group.brokerapp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SecuritiesFullInfoResponse {

    @JsonIgnore
    private long id;
    private String region;
    private Double price;
    private String name;
    private String ticker;
    private String sector;
    private String iconPath;

}
