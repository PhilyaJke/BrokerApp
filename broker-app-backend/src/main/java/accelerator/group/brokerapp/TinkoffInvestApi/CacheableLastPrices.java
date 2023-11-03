package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService.LastPriceOfSecurityMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.TinkoffInvestApiService.TinkoffInvestApiServiceImpl;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final TinkoffInvestApiServiceImpl tinkoffInvestApiService;
    private final LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService;

    @Autowired
    public CacheableLastPrices(
            @Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
            @Qualifier("LastPriceOfSecuritiesMVCService") LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService,
            TinkoffInvestApiServiceImpl tinkoffInvestApiService) {
        this.securitiesMVCService = securitiesMVCService;
        this.tinkoffInvestApiService = tinkoffInvestApiService;
        this.lastPriceOfSecurityMVCService = lastPriceOfSecurityMVCService;
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
                    if(!lastPriceOfSecurityMVCService.existById(response.getLastPrice().getFigi())) {
                        LastPriceOfSecurities lastPriceOfSecurities = new LastPriceOfSecurities(
                                response.getLastPrice().getFigi(),
                                parseToDoublePrice(response.getLastPrice().getPrice().getUnits(), response.getLastPrice().getPrice().getNano()
                                ), response.getLastPrice().getTime()
                        );
                        lastPriceOfSecurityMVCService.save(lastPriceOfSecurities);
                    }else {
                        System.out.println(lastPriceOfSecurityMVCService.findById(response.getLastPrice().getFigi()).get().getPrice() + " " + securitiesMVCService.findSecurityByFigi(lastPriceOfSecurityMVCService.findById(response.getLastPrice().getFigi()).get().getFigi()).getName());
                        lastPriceOfSecurityMVCService.deleteById(response.getLastPrice().getFigi());
                        LastPriceOfSecurities lastPriceOfSecurities = new LastPriceOfSecurities(
                                response.getLastPrice().getFigi(),
                                parseToDoublePrice(response.getLastPrice().getPrice().getUnits(), response.getLastPrice().getPrice().getNano()
                                ), response.getLastPrice().getTime()
                        );
                        lastPriceOfSecurityMVCService.save(lastPriceOfSecurities);
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
                tinkoffInvestApiService.returnInvestApiConnection().getMarketDataStreamService().newStream("new_stream", processor, onErrorCallback).subscribeLastPrices(securitiesMVCService.findLimitedSecurities(PageRequest.of(i, 299)));
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

