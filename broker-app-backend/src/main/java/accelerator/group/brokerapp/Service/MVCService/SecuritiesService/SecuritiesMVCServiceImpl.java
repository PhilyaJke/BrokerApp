package accelerator.group.brokerapp.Service.MVCService.SecuritiesService;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.*;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import accelerator.group.brokerapp.Service.DAOService.SecuritiesService.SecuritiesDAOServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService.LastPriceOfSecurityMVCServiceImpl;
import accelerator.group.brokerapp.Service.TinkoffInvestApiService.TinkoffInvestApiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service("SecuritiesMVCService")
public class SecuritiesMVCServiceImpl extends SecuritiesDAOServiceImpl implements SecuritiesMVCService {

    private final SecuritiesDAOServiceImpl securitiesDAOService;
    private final TinkoffInvestApiServiceImpl tinkoffInvestApiService;
    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;
    private final BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService;
    private final LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService;

    @Autowired
    public SecuritiesMVCServiceImpl(@Qualifier("SecuritiesDAOService") SecuritiesDAOServiceImpl securitiesDAOService,
                                    @Qualifier("AdditionalStocksInformationMVCService") AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService,
                                    @Qualifier("BrokeragePortfolioSecuritiesMVCService") BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService,
                                    @Qualifier("LastPriceOfSecurityMVCService") LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService,
                                    SecuritiesRepository securitiesRepository,
                                    TinkoffInvestApiServiceImpl tinkoffInvestApiService) {
        super(securitiesRepository);
        this.securitiesDAOService = securitiesDAOService;
        this.tinkoffInvestApiService = tinkoffInvestApiService;
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
        this.brokeragePortfolioSecuritiesMVCService = brokeragePortfolioSecuritiesMVCService;
        this.lastPriceOfSecurityMVCService = lastPriceOfSecurityMVCService;
    }

    @Override
    public List<HistoricCandle> getSecuritiesInfoFromApiByTicker(String ticker) {
        var security = securitiesDAOService.findSecurityByTicker(ticker);
        return tinkoffInvestApiService.getSecuritiesInfoFromApi(security.getFigi());
    }

    @Override
    public SecuritiesPageResponse showSecurities(int page, int size, String region) {
        SecuritiesPageResponse securitiesPageResponse;
        switch (region){
            case "all" -> {
                log.info("Пришел запрос на все акции");
                securitiesPageResponse = securitiesDAOService.findAllSecuritiesPage(PageRequest.of(page, size));
            }
            case "foreign" ->{
                log.info("Пришел запрос на все иностранные акции");
                securitiesPageResponse = securitiesDAOService.findAllForeignSecuritiesPage(PageRequest.of(page, size));
            }
            default -> {
                log.info("Пришел запрос на все российские акции");
                securitiesPageResponse = securitiesDAOService.findAllRuSecuritiesPage(PageRequest.of(page, size));
            }
        }
        return securitiesPageResponse;
    }

    @Override
    public JSONObject findFullInfo(String ticker) {
//        Optional<User> user = userMVCService.findByUsername(jwtTokenProvider.getUsername(Authorization));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        var securitiesCandles = getSecuritiesInfoFromApiByTicker(ticker);
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ru.tinkoff.piapi.contract.v1.HistoricCandle historicCandle : securitiesCandles) {
            parseToDoublePrice(historicCandle.getHigh().getUnits(), historicCandle.getHigh().getNano());
            Map<String, Object> map = new HashMap();
            map.put("high", parseToDoublePrice(historicCandle.getHigh().getUnits(), historicCandle.getHigh().getNano()));
            map.put("low", parseToDoublePrice(historicCandle.getLow().getUnits(), historicCandle.getLow().getNano()));
            map.put("close", parseToDoublePrice(historicCandle.getClose().getUnits(), historicCandle.getClose().getNano()));
            map.put("open", parseToDoublePrice(historicCandle.getOpen().getUnits(), historicCandle.getOpen().getNano()));
            map.put("date", simpleDateFormat.format(Date.from(Instant.ofEpochSecond(historicCandle.getTime().getSeconds()))));
            list.add(map);
        }
        /*
        var brokeragePortfolioSecurities = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(user.get().getId(), security.getId());
        long count;
        if(brokeragePortfolioSecurities == null){
            count = 0;
        }else{
           count = brokeragePortfolioSecurities.getCount();
        }
         */
        jsonObject.append("candles", list);
        jsonObject.append("lot", additionalStocksInformationMVCService.getLotOfSecurity(ticker));
//        jsonObject.put("count", count);
        return jsonObject;
    }

    @Override
    public JSONObject buySecurity(BuySecurityRequest buySecurityRequest, User user) {
        var portfolio = brokeragePortfolioSecuritiesMVCService.findPortfolioByUserIdAndTicker(user.getId() ,buySecurityRequest.getTicker());
        if(portfolio != null) {
            brokeragePortfolioSecuritiesMVCService.addPurchaseOfSecurityToPortfolio(portfolio.getId(), buySecurityRequest);
        }else{
            brokeragePortfolioSecuritiesMVCService.createPortfolioAndAddPurchaseOfSecurity(user.getId(), buySecurityRequest);
        }
        return buildJson(buySecurityRequest);
    }

    @Override
    public JSONObject sellSecurity(BuySecurityRequest buySecurityRequest, User user) {
        brokeragePortfolioSecuritiesMVCService.sellSecurities(user.getId(), buySecurityRequest);
        JSONObject jsonObject = buildJson(buySecurityRequest);
        return jsonObject;
    }

    @Override
    public JSONObject buildJson(BuySecurityRequest buySecurityRequest) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("count", buySecurityRequest.getValue());
        if(lastPriceOfSecurityMVCService.checkLastPrice(buySecurityRequest).isPresent()){
            jsonObject.append("price", lastPriceOfSecurityMVCService.checkLastPrice(buySecurityRequest).get().getPrice());
            jsonObject.append("sum", lastPriceOfSecurityMVCService.checkLastPrice(buySecurityRequest).get().getPrice() * buySecurityRequest.getValue());
        }else{
            jsonObject.append("price", additionalStocksInformationMVCService.checkAdditionalStockInfo(buySecurityRequest).getPrice());
            jsonObject.append("sum", additionalStocksInformationMVCService.checkAdditionalStockInfo(buySecurityRequest).getPrice() * buySecurityRequest.getValue());
        }
        return jsonObject;
    }

    public Double parseToDoublePrice(long units, int nano) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(units).append(nano).insert(String.valueOf(units).length(), ".");
        return Double.valueOf(String.valueOf(stringBuilder));
    }


}