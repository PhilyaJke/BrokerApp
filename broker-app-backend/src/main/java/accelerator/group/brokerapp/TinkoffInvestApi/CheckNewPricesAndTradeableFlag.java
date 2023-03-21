package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;
import java.util.stream.Collectors;

//@Component
//@EnableScheduling
//@Slf4j
//public class CheckNewPricesAndTradeableFlag {
//
//    @Value("${invest.api.secret.token}")
//    private String token;
//
//    private final SecuritiesRepository securitiesRepository;
//
//    public CheckNewPricesAndTradeableFlag(SecuritiesRepository securitiesRepository) {
//        this.securitiesRepository = securitiesRepository;
//    }
//
//
//    @Scheduled(fixedDelay = 60000)
//    @Async
//    public void updateLastPricesAndGetTradeableFlag() {
//        InvestApi investApi = InvestApi.create(token);
//        int size = securitiesRepository.findAllFigiSecurities().size()/299+1;
//        for(int i = 0; i < size; i+=1) {
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
//                            sec.setLastprice(lastprices.get(j).getPrice().getUnits());
//                            securitiesRepository.save(sec);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
