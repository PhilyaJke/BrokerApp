package accelerator.group.brokerapp.TinkoffInvestApi;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.TinkoffInvestApiService.TinkoffInvestApiServiceImpl;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import ru.tinkoff.piapi.contract.v1.Share;

import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@EnableScheduling
public class CheckNewPricesAndTradeableFlag{
    private final TinkoffInvestApiServiceImpl tinkoffInvestApiService;
    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;

    @Autowired
    public CheckNewPricesAndTradeableFlag(@Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
                                          @Qualifier("AdditionalStocksInformationMVCService") AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService,
                                          TinkoffInvestApiServiceImpl tinkoffInvestApiService) {
        this.tinkoffInvestApiService = tinkoffInvestApiService;
        this.securitiesMVCService = securitiesMVCService;
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
    }

    @Async
    @Transactional
    @Scheduled(fixedDelay = 1000000L)
    protected void addNewSecurities() throws ExecutionException, InterruptedException {
        log.info("Проверка на наличие новых акций");
        String sl = null;
        try {
            List<Share> shares = tinkoffInvestApiService.returnInvestApiConnection().getInstrumentsService().getTradableSharesSync();
            for (Share share : shares) {
                if (!securitiesMVCService.findAllFigiSecurities().contains(share.getFigi())) {
                    var listFiles = new File("/Users/philyaborozdin/Desktop/icons").listFiles();
                    for (int i = 0; i < Objects.requireNonNull(listFiles).length; i++) {
                        try {
                            if (listFiles[i].getName().substring(0, listFiles[i].getName().length() - 3).equals(share.getTicker())) {
                                var s = getAwsCredentials().getObject("securitiesicons", listFiles[i].getName().substring(0, listFiles[i].getName().length() - 3));
                                if (s != null) {
                                    sl = s.getObjectContent().getHttpRequest().getURI().toString();
                                    break;
                                }
                            }
                        } catch (SdkClientException ignored) {

                        }


                        Securities securities = new Securities(
                                share.getFigi(),
                                share.getName(),
                                share.getTicker(),
                                share.getCountryOfRisk(),
                                share.getSector(),
                                sl
                        );

                        AdditionalStocksInformation additionalStocksInformation = new AdditionalStocksInformation(
                                share.getLot(),
                                securities
                        );

                        additionalStocksInformationMVCService.save(additionalStocksInformation);
                        securitiesMVCService.save(securities);
                    }
                }
            }
        }catch (ApiRuntimeException exc){
            addNewSecurities();
            log.trace("какая-то ошибка тинькофф апи");
        }
    }

    @Async
    @Scheduled(fixedDelay = 2000000L)
    protected void updateLastPricesAndGetTradeableFlag() {
        log.info("Проверка цен акций");
        for (int i = 1; i < 8; i += 1) {
            var securities = securitiesMVCService.findLimitedSecurities(PageRequest.of(i, 299));
            var lastprices = tinkoffInvestApiService.returnInvestApiConnection().getMarketDataService().getLastPricesSync(securities);
            for (ru.tinkoff.piapi.contract.v1.LastPrice lastprice : lastprices) {
                if (!lastprice.getFigi().isEmpty()) {
                    for (int k = 0; k < securities.size(); k++) {
                        if (securitiesMVCService.findSecurityByFigi(lastprice.getFigi()) != null) {
                            var additionalStocksInformation = additionalStocksInformationMVCService
                                    .findAddStocksInfoById(securitiesMVCService.findSecurityByFigi(lastprice.getFigi()).getId());
                            additionalStocksInformation.setPrice(parseToDoublePrice(lastprice.getPrice().getUnits(), lastprice.getPrice().getNano()));
                            additionalStocksInformationMVCService.save(additionalStocksInformation);
                            break;
                        }
                    }
                }
            }
        }
        log.info("Конец проверки цен акций");
    }

    public AmazonS3 getAwsCredentials(){

        AWSCredentials awsCredentials = new AWSCredentials(){
            @Override
            public String getAWSAccessKeyId() {
                return "YCAJEsTENnTdsKsgUimR5RBMO";
            }

            @Override
            public String getAWSSecretKey() {
                return "YCPMp-e69gbk6szjYV_NIfWDfqqxqS6u8Ah2WNXT";
            }
        };

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net","ru-central1"
                        )
                )
                .build();
    }

    public Double parseToDoublePrice(long units, int nano){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(units).append(nano).insert(String.valueOf(units).length(), ".");
        return Double.valueOf(String.valueOf(stringBuilder));
    }

}
