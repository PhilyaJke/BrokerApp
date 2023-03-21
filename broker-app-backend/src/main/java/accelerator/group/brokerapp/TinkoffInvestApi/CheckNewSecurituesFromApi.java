//package accelerator.group.brokerapp.TinkoffInvestApi;
//
//import accelerator.group.brokerapp.Entity.Securities;
//import accelerator.group.brokerapp.Repository.SecuritiesRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import ru.tinkoff.piapi.contract.v1.Share;
//import ru.tinkoff.piapi.core.InvestApi;
//
//import java.util.List;
//
//@Component
//@EnableScheduling
//@Slf4j
//public class CheckNewSecurituesFromApi {
//
//    @Value("${invest.api.secret.token}")
//    private String token;
//
//    private final SecuritiesRepository securitiesRepository;
//
//    public CheckNewSecurituesFromApi(SecuritiesRepository securitiesRepository) {
//        this.securitiesRepository = securitiesRepository;
//    }
//
//
//    @Scheduled(fixedDelay = 1000l)
//    public void addNewSecurities() {
//        System.out.println("dfvdfvdfsvsdfv");
//        InvestApi investApi = InvestApi.create(token);
//        log.info("Проверка на наличие новых акций");
//        List<Share> shares = investApi.getInstrumentsService().getTradableSharesSync();
//        for (int i = 0; i < shares.size(); i++){
//            if (!securitiesRepository.findAllFigiSecurities().contains(shares.get(i).getFigi())) {
//                Securities securities = new Securities();
//                securities.setLot(shares.get(i).getLot());
//                securities.setRegion(shares.get(i).getCountryOfRisk());
//                securities.setName(shares.get(i).getName());
//                securities.setFigi(shares.get(i).getFigi());
//                securities.setTicker(shares.get(i).getTicker());
//                securities.setDate(String.valueOf(shares.get(i).getFirst1DayCandleDate()));
//                securities.setSector(shares.get(i).getSector());
//                securitiesRepository.save(securities);
//
//            }
//        }
//    }
//}
