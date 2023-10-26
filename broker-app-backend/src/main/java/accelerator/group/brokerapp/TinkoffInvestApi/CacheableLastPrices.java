package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import accelerator.group.brokerapp.Service.DAOService.SecuritiesService.SecuritiesDAOServiceImpl;
import accelerator.group.brokerapp.Service.TinkoffInvestApiService.TinkoffInvestApiServiceImpl;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.contract.v1.SubscriptionStatus;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

@Slf4j
@Component
public class CacheableLastPrices{

    private final SecuritiesRepository securitiesRepository;
    private final SecuritiesDAOServiceImpl securitiesService;
    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;

    private final TinkoffInvestApiServiceImpl tinkoffInvestApiService;

    @Autowired
    public CacheableLastPrices(SecuritiesRepository securitiesRepository,
                               SecuritiesDAOServiceImpl securitiesService,
                               LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository,
                               TinkoffInvestApiServiceImpl tinkoffInvestApiService) {
        this.securitiesRepository = securitiesRepository;
        this.securitiesService = securitiesService;
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.tinkoffInvestApiService = tinkoffInvestApiService;
    }

    @Cacheable(value = "LastPrice")
    @PostConstruct
    public void SecuritiesStreamInfo(){
        try {
            StreamProcessor<MarketDataResponse> processor = response -> {
                if (response.hasTradingStatus()) {
                    log.info("Новые данные по статусам: {}", response);
                } else if (response.hasPing()) {
                    log.info("пинг сообщение");
                } else if (response.hasCandle()) {
                    log.info("Новые данные по свечам: {}", response);
                } else if (response.hasTrade()) {
                    log.info("Новые данные по сделкам: {}", response);
                }else if(response.hasLastPrice()){
                    if(!lastPriceOfSecuritiesRepository.existsById(response.getLastPrice().getFigi())) {
                        LastPriceOfSecurities lastPriceOfSecurities = new LastPriceOfSecurities(
                                response.getLastPrice().getFigi(),
                                parseToDoublePrice(response.getLastPrice().getPrice().getUnits(), response.getLastPrice().getPrice().getNano()
                                ), response.getLastPrice().getTime()
                        );
                        lastPriceOfSecuritiesRepository.save(lastPriceOfSecurities);
                    }else {
                        System.out.println(lastPriceOfSecuritiesRepository.findById(response.getLastPrice().getFigi()).get().getPrice());
                        lastPriceOfSecuritiesRepository.deleteById(response.getLastPrice().getFigi());
                        LastPriceOfSecurities lastPriceOfSecurities = new LastPriceOfSecurities(
                                response.getLastPrice().getFigi(),
                                parseToDoublePrice(response.getLastPrice().getPrice().getUnits(), response.getLastPrice().getPrice().getNano()
                                ), response.getLastPrice().getTime()
                        );
                        lastPriceOfSecuritiesRepository.save(lastPriceOfSecurities);
                    }
                } else if (response.hasSubscribeCandlesResponse()) {
                    var successCount = response.getSubscribeCandlesResponse().getCandlesSubscriptionsList()
                            .stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                    var errorCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList()
                            .stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                    log.info("удачных подписок на свечи: {}", successCount);
                    log.info("неудачных подписок на свечи: {}", errorCount);
                } else if (response.hasSubscribeLastPriceResponse()) {
                    var successCount = response.getSubscribeLastPriceResponse().getLastPriceSubscriptionsList()
                            .stream().filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                    var errorCount = response.getSubscribeLastPriceResponse().getLastPriceSubscriptionsList()
                            .stream().filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
                    log.info("удачных подписок на последние цены: {}", successCount);
                    log.info("неудачных подписок на последние цены: {}", errorCount);
                }
            };
            Consumer<Throwable> onErrorCallback = Throwable::printStackTrace;

            for (int i = 1; i < 3; i += 1) {
                tinkoffInvestApiService.returnInvestApiConnection().getMarketDataStreamService().newStream("new_stream", processor, onErrorCallback).subscribeLastPrices(securitiesRepository.findLimitedSecurities(PageRequest.of(i, 299)));
            }

        }catch (ApiRuntimeException | StatusRuntimeException exc){
            log.info("Какая-то ошибка тинькофф банка. Callback");
            SecuritiesStreamInfo();
        }
    }

    public Double parseToDoublePrice(long units, int nano){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(units).append(nano).insert(String.valueOf(units).length(), ".");
        return Double.valueOf(String.valueOf(stringBuilder));
    }
}

