package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.AdditionalStocksInformationRepository;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import accelerator.group.brokerapp.Service.SecuritiesService.SecuritiesServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import ru.tinkoff.piapi.contract.v1.Share;

import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.util.List;

@Component
@EnableScheduling
@Slf4j
public class CheckNewPricesAndTradeableFlag{

    private final SecuritiesRepository securitiesRepository;
    private final SecuritiesServiceImpl securitiesService;
    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;

    @Autowired
    public CheckNewPricesAndTradeableFlag(SecuritiesRepository securitiesRepository,
                                          SecuritiesServiceImpl securitiesService,
                                          AdditionalStocksInformationRepository additionalStocksInformationRepository) {
        this.securitiesRepository = securitiesRepository;
        this.securitiesService = securitiesService;
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
    }

//Обновить и сделать автоматическое добавление иконок
    @Async
    @Transactional
    @Scheduled(fixedDelay = 10000000l)
    protected void addNewSecurities() {
        log.info("Проверка на наличие новых акций");
        try {
            List<Share> shares = securitiesService.returnInvestApiConnection().getInstrumentsService().getTradableSharesSync();
            for (int i = 0; i < shares.size(); i++) {
                if (!securitiesRepository.findAllFigiSecurities().contains(shares.get(i).getFigi())) {

                    Securities securities = new Securities(
                            shares.get(i).getFigi(),
                            shares.get(i).getName(),
                            shares.get(i).getTicker(),
                            shares.get(i).getCountryOfRisk(),
                            shares.get(i).getSector()
                    );

                    AdditionalStocksInformation additionalStocksInformation = new AdditionalStocksInformation(
                            shares.get(i).getLot(),
                            securities
                    );

                    additionalStocksInformationRepository.save(additionalStocksInformation);
                    securitiesRepository.save(securities);
                }
            }
        }catch (ApiRuntimeException exc){
            addNewSecurities();
            log.trace("какая-то ошибка тинькофф апи");
        }
    }

    @Async
    @Scheduled(fixedDelay = 2000000)
    protected void updateLastPricesAndGetTradeableFlag() {
        log.info("Проверка цен акций");
        for (int i = 1; i < 8; i += 1) {
            var securities = securitiesRepository.findLimitedSecurities(PageRequest.of(i, 299));
            var lastprices = securitiesService.returnInvestApiConnection().getMarketDataService().getLastPricesSync(securities);
            for (ru.tinkoff.piapi.contract.v1.LastPrice lastprice : lastprices) {
                if (!lastprice.getFigi().isEmpty()) {
                    for (int k = 0; k < securities.size(); k++) {
                        if (securitiesRepository.findSecurityByFigi(lastprice.getFigi()) != null) {
                            var additionalStocksInformation = additionalStocksInformationRepository
                                    .findAddStocksInfoById(securitiesRepository.findSecurityByFigi(lastprice.getFigi()).getId());
                            additionalStocksInformation.setPrice(Double.valueOf(String.valueOf(lastprice.getPrice().getUnits()).concat(".")
                                    .concat(String.valueOf(lastprice.getPrice().getNano()))));
                            additionalStocksInformationRepository.save(additionalStocksInformation);
                            break;
                        }
                    }
                }
            }
        }
        log.info("Конец проверки цен акций");
    }

}
