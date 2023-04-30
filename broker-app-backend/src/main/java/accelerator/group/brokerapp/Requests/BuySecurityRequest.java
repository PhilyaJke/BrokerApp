package accelerator.group.brokerapp.Requests;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class BuySecurityRequest {

    private String ticker;
    private long value;

}
