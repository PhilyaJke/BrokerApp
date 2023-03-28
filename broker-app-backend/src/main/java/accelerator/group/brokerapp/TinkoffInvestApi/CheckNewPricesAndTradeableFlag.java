package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.AdditionalStocksInformationRepository;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.util.List;

@Component
@EnableScheduling
@Slf4j
public class CheckNewPricesAndTradeableFlag {

    private InvestApi investApi;
    private final SecuritiesRepository securitiesRepository;
    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;

    @Autowired
    public CheckNewPricesAndTradeableFlag(SecuritiesRepository securitiesRepository,
                                          @Value("${invest.api.secret.token}") String token,
                                          AdditionalStocksInformationRepository additionalStocksInformationRepository) {
        this.securitiesRepository = securitiesRepository;
        this.investApi = InvestApi.create(token);
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
    }


    @Async
    @Scheduled(fixedDelay = 1200000)
    @Transactional
    public void updateLastPricesAndGetTradeableFlag() {
        log.info("Проверка цен акций");
        for (int i = 0; i < 8; i += 1) {
            var securities = securitiesRepository.findLimitedSecurities(PageRequest.of(i, 299));
            var lastprices = investApi.getMarketDataService().getLastPricesSync(securities);
            for(int j = 0; j < lastprices.size(); j++){
                if(lastprices.get(j).getFigi().isEmpty()){
                    continue;
                }else{
                    for (int k = 0; k < securities.getNumberOfElements(); k++) {
                        if (!securitiesRepository.findSecurityByFigi(lastprices.get(j).getFigi()).equals(null)) {
                            var sec = securitiesRepository.findSecurityByFigi(lastprices.get(j).getFigi()).getAdditionalStocksInformation();
                            sec.setPrice(Double.valueOf(String.valueOf(lastprices.get(j).getPrice().getUnits()).concat(".")
                                    .concat(String.valueOf(lastprices.get(j).getPrice().getNano()))));
                            additionalStocksInformationRepository.save(sec);
                            break;
                        }
                    }
                }
            }
        }
    log.info("Конец проверки цен акций");
}

    @Transactional
    @Scheduled(fixedDelay = 10000000l)
    public void addNewSecurities() {
        log.info("Проверка на наличие новых акций");
        try {
            List<Share> shares = this.investApi.getInstrumentsService().getTradableSharesSync();
            for (int i = 0; i < shares.size(); i++) {
                if (!securitiesRepository.findAllFigiSecurities().contains(shares.get(i).getFigi())) {

                    AdditionalStocksInformation additionalStocksInformation = new AdditionalStocksInformation(
                            shares.get(i).getLot()
                    );
                    additionalStocksInformationRepository.save(additionalStocksInformation);

                    Securities securities = new Securities(
                            shares.get(i).getFigi(),
                            shares.get(i).getName(),
                            shares.get(i).getTicker(),
                            shares.get(i).getCountryOfRisk(),
                            shares.get(i).getSector(),
                            additionalStocksInformation
                    );
                    securitiesRepository.save(securities);
                }
            }
        }catch (ApiRuntimeException exc){
            addNewSecurities();
            log.trace("какая-то ошибка тинькофф апи");
        }
    }
}
