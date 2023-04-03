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
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.contract.v1.SubscriptionStatus;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class CheckNewPricesAndTradeableFlag {

    private final SecuritiesRepository securitiesRepository;
    private final SecuritiesServiceImpl securitiesService;
    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;

    @Autowired
    public CheckNewPricesAndTradeableFlag(SecuritiesRepository securitiesRepository,
                                          SecuritiesServiceImpl securitiesService, AdditionalStocksInformationRepository additionalStocksInformationRepository) {
        this.securitiesRepository = securitiesRepository;
        this.securitiesService = securitiesService;
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
    }


    @Async
    @Scheduled(fixedDelay = 1200000)
    @Transactional
    public void updateLastPricesAndGetTradeableFlag() {
        log.info("Проверка цен акций");
        for (int i = 0; i < 8; i += 1) {
            var securities = securitiesRepository.findLimitedSecurities(PageRequest.of(i, 299));
            var lastprices = securitiesService.returnInvestApiConnection().getMarketDataService().getLastPricesSync(securities);
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


//Обновить и сделать автоматическое добавление иконок
    @Transactional
    @Scheduled(fixedDelay = 10000000l)
    @Async
    public void addNewSecurities() {
        log.info("Проверка на наличие новых акций");
        int cpunt = 0;

        StreamProcessor<MarketDataResponse> processor = response -> {
            System.out.println(response.getLastPrice().getPrice() + " " + response.getLastPrice().getFigi());
            if (response.hasTradingStatus()) {
                log.info("Новые данные по статусам: {}", response);
            } else if (response.hasPing()) {
                log.info("пинг сообщение");
            } else if (response.hasCandle()) {
                log.info("Новые данные по свечам: {}", response);
            } else if (response.hasOrderbook()) {
                log.info("Новые данные по стакану: {}", response);
            } else if (response.hasTrade()) {
                log.info("Новые данные по сделкам: {}", response);
            } else if (response.hasSubscribeCandlesResponse()) {
                var successCount = response.getSubscribeCandlesResponse().getCandlesSubscriptionsList().stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                var errorCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList().stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                log.info("удачных подписок на свечи: {}", successCount);
                log.info("неудачных подписок на свечи: {}", errorCount);
            } else if (response.hasSubscribeInfoResponse()) {
                var successCount = response.getSubscribeInfoResponse().getInfoSubscriptionsList().stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                var errorCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList().stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                log.info("удачных подписок на статусы: {}", successCount);
                log.info("неудачных подписок на статусы: {}", errorCount);
            } else if (response.hasSubscribeOrderBookResponse()) {
                var successCount = response.getSubscribeOrderBookResponse().getOrderBookSubscriptionsList().stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                var errorCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList().stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                log.info("удачных подписок на стакан: {}", successCount);
                log.info("неудачных подписок на стакан: {}", errorCount);
            } else if (response.hasSubscribeTradesResponse()) {
                var successCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList().stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                var errorCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList().stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                log.info("удачных подписок на сделки: {}", successCount);
                log.info("неудачных подписок на сделки: {}", errorCount);
            } else if (response.hasSubscribeLastPriceResponse()) {
                var successCount = response.getSubscribeLastPriceResponse().getLastPriceSubscriptionsList().stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                var errorCount = response.getSubscribeLastPriceResponse().getLastPriceSubscriptionsList().stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                log.info("удачных подписок на последние цены: {}", successCount);
                log.info("неудачных подписок на последние цены: {}", errorCount);
                var k = response.getSubscribeLastPriceResponse().getLastPriceSubscriptionsList().stream().collect(Collectors.toList());
                System.out.println(k.get(0).getFigi() + " " + response.hasLastPrice());
            }
        };
        Consumer<Throwable> onErrorCallback = error -> log.error("хуй");

//        while (true){
        securitiesService.returnInvestApiConnection().getMarketDataStreamService().newStream("last_prices_stream", processor, onErrorCallback).subscribeLastPrices(new ArrayList<>(List.of("BBG000BN56Q9", "BBG000KCZPC3")));
//            securitiesService.returnInvestApiConnection().getMarketDataStreamService().getStreamById("new_stream").unsubscribeCandles(Collections.singletonList("BBG000BN56Q9"));
//            securitiesService.returnInvestApiConnection().getMarketDataStreamService().newStream("new_stream", processor, onErrorCallback).subscribeCandles(Collections.singletonList("BBG000BN56Q9"));
//        }
//            List<Share> shares = securitiesService.returnInvestApiConnection().getInstrumentsService().getTradableSharesSync();
//            for (int i = 0; i < shares.size(); i++) {
//                if (!securitiesRepository.findAllFigiSecurities().contains(shares.get(i).getFigi())) {
//
//                    AdditionalStocksInformation additionalStocksInformation = new AdditionalStocksInformation(
//                            shares.get(i).getLot()
//                    );
//                    additionalStocksInformationRepository.save(additionalStocksInformation);
//
//                    Securities securities = new Securities(
//                            shares.get(i).getFigi(),
//                            shares.get(i).getName(),
//                            shares.get(i).getTicker(),
//                            shares.get(i).getCountryOfRisk(),
//                            shares.get(i).getSector(),
//                            additionalStocksInformation
//                    );
//                    securitiesRepository.save(securities);
//                }
//            }
//        }catch (ApiRuntimeException exc){
//            addNewSecurities();
//            log.trace("какая-то ошибка тинькофф апи");
//        }
    }
}
