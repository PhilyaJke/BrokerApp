package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;

@Component
@EnableScheduling
@Slf4j
public class CheckNewPricesAndTradeableFlag {

    private InvestApi investApi;
    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public CheckNewPricesAndTradeableFlag(SecuritiesRepository securitiesRepository,
                                          @Value("${invest.api.secret.token}") String token) {
        this.securitiesRepository = securitiesRepository;
        this.investApi = InvestApi.create(token);
    }


//    @Scheduled(fixedDelay = 1200000)
//    @Async
//    public void updateLastPricesAndGetTradeableFlag() {
//        log.info("Проверка цен акций");
//        int size = securitiesRepository.findAllFigiSecurities().size()/299+1;
//        int count = 0;
//        for(int i = 0; i < 8; i+=1) {
//            var securities = securitiesRepository.findLimitedSecurities(PageRequest.of(i, 299));
//            var lastprices = investApi.getMarketDataService().getLastPricesSync(securities);
//            for (int j = 0; j < lastprices.size(); j++) {
//                if (lastprices.get(j).getFigi().equals(null)) {
//                    continue;
//                } else {
//                    for (int k = 0; k < securities.getNumberOfElements(); k++) {
//                        if (lastprices.get(j).getFigi().equals(securitiesRepository.findSecurityByFigi(securities.get()
//                                .collect(Collectors.toList()).get(k)).getFigi())) {
//                            var sec = securitiesRepository.findSecurityByFigi(lastprices.get(j).getFigi());
//                            sec.setPrice(Double.valueOf(String.valueOf(lastprices.get(j).getPrice().getUnits()).concat(".")
//                                    .concat(String.valueOf(lastprices.get(j).getPrice().getNano()))));
//                            securitiesRepository.save(sec);
//                            count++;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println(count + " " + securitiesRepository.count());
//        log.info("Конец проверки цен акций");
//    }

    @Scheduled(fixedDelay = 10000000l)
    public void addNewSecurities() {
        log.info("Проверка на наличие новых акций");
        List<Share> shares = this.investApi.getInstrumentsService().getTradableSharesSync();
        for (int i = 0; i < shares.size(); i++){
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
