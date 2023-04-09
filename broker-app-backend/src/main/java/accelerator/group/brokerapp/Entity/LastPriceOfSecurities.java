package accelerator.group.brokerapp.Entity;

import com.google.protobuf.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
@RedisHash(value = "cacheData", timeToLive = 300l)
public class LastPriceOfSecurities implements Serializable {

    @Id
    private String figi;

    @Indexed
    private Double price;

    @Indexed
    @DateTimeFormat
    private Timestamp date;

}
