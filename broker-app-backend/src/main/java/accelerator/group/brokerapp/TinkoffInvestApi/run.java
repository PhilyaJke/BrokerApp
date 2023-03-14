package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import javax.annotation.Nonnull;
import javax.persistence.PrePersist;
import java.util.List;

@Component
public class run {
    String token = "t.2lxQV0lbcELMPVCsITrhbjldejNU7_MIxjW-7nTMaD5hEI4rLQdwF5nuZAiAolpuGKdShnQ6cyaMA1BeyAJ0BA";
    InvestApi investApi = InvestApi.create(token);
    private final SecuritiesRepository securitiesRepository;


    public run(SecuritiesRepository securitiesRepository) {
        this.securitiesRepository = securitiesRepository;
    }

    @PrePersist
    public void init(){
        add(api.findAllSharesSecurities(investApi));
    }

    public void add(List<Share> shares) {
        for (int i = 0; i < shares.size(); i++) {
            if (!securitiesRepository.findAllFigiSecurities().contains(shares.get(i).getFigi())) {
                Securities securities = new Securities();
                securities.setLot(shares.get(i).getLot());
                securities.setRegion(shares.get(i).getCountryOfRisk());
                securities.setName(shares.get(i).getName());
                securities.setFigi(shares.get(i).getFigi());
                securities.setTicker(shares.get(i).getTicker());
                securities.setDate(String.valueOf(shares.get(i).getFirst1DayCandleDate()));
                securities.setSector(shares.get(i).getSector());
                securitiesRepository.save(securities);

            }
        }
    }

}
