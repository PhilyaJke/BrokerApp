package accelerator.group.brokerapp.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SecuritiesPageResponse {

    private List<SecuritiesFullInfoResponse> securitiesList;
}
