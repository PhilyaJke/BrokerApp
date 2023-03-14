package accelerator.group.brokerapp.TinkoffInvestApi;

import com.google.protobuf.Timestamp;
import lombok.Data;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.core.utils.DateUtils.epochMillisToString;
import static ru.tinkoff.piapi.core.utils.MapperUtils.mapUnitsAndNanos;

public class api {
//многопоточкой брать цены акций
    public static void getCandle(InvestApi api){
        String figi = "BBG004730N88";
        LocalDateTime date = LocalDateTime.ofEpochSecond(946969200,0, ZoneOffset.UTC);
        System.out.println(date);
        var candlesHour = api.getMarketDataService()
                .getCandlesSync(figi, Instant.now().minus(1, ChronoUnit.MONTHS), Instant.now(), CandleInterval.CANDLE_INTERVAL_15_MIN);
        for (HistoricCandle candle : candlesHour) {
            printCandle(candle);
        }
    }




    public static List<Share> findAllSharesSecurities(InvestApi api){
       return api.getInstrumentsService().getTradableSharesSync();
    }


    public static void shedule1(InvestApi api){
        var tradingSchedules =
                api.getInstrumentsService().getTradingScheduleSync("spb", Instant.now(), Instant.now().plus(5, ChronoUnit.DAYS));
        for (TradingDay tradingDay : tradingSchedules.getDaysList()) {
            var date = timestampToString(tradingDay.getDate());
            var startDate = timestampToString(tradingDay.getStartTime());
            var endDate = timestampToString(tradingDay.getEndTime());
            if (tradingDay.getIsTradingDay()) {
                System.out.println(" дата " + date + " начало " + startDate + " конец " + endDate);
            } else {
                System.out.println( " Выходной день дата " + date);
            }
        }
    }

    public static void shedule(InvestApi api) throws ExecutionException, InterruptedException {
        var inst = api.getInstrumentsService().getInstrumentByFigi("BBG000B9XRY4");
        System.out.println(
                inst.get().getFigi() + " figi " +
                        inst.get().getLot() + " lot " +
                        inst.get().getTradingStatus().name() + " status " +
                        inst.get().getOtcFlag() + " flag "
        );
    }

    public static void getCandlesExample(InvestApi api) {
        var figi = "BBG004S684M6";
        var s = String.valueOf(Instant.ofEpochSecond(9365));
//        Instant.parse("2022-006-06T00:00:00Z");
        System.out.println(s);
        var candles1min = api.getMarketDataService()
                .getCandlesSync(figi, Instant.parse("2022-06-06T00:00:00Z"), Instant.now(),
                        CandleInterval.CANDLE_INTERVAL_DAY);
        for (HistoricCandle candle : candles1min) {
            printCandle(candle);
        }
    }

    private static void printCandle(HistoricCandle candle) {
        var open = quotationToBigDecimal(candle.getOpen());
        var close = quotationToBigDecimal(candle.getClose());
        var high = quotationToBigDecimal(candle.getHigh());
        var low = quotationToBigDecimal(candle.getLow());
        var volume = candle.getVolume();
        var time = timestampToString(candle.getTime());
        System.out.println("open " + open + " close " + close + " low " + low + " hight" + high + " volume " + volume + " time " +time);
    }




    public static void marketdataServiceExample(InvestApi api) {
       getLastPricesExample(api);
   }

    private static void getLastPricesExample(InvestApi api) {
        //Получаем и печатаем последнюю цену по инструменту
        var randomFigi = randomFigi(api, 5);
        var lastPrices = api.getMarketDataService().getLastPricesSync(randomFigi);
        for (LastPrice lastPrice : lastPrices) {
            var figi = lastPrice.getFigi();
            var price = quotationToBigDecimal(lastPrice.getPrice());
            var time = timestampToString(lastPrice.getTime());
            System.out.println(figi + " " + price + " " + time);
        }
    }

    public static void instrumentsServiceExample(InvestApi api) {
        //Получаем базовые списки инструментов и печатаем их
        var shares = api.getInstrumentsService().getTradableSharesSync();
        for(int i = 0; i < shares.size(); i++){
            System.out.println(shares.get(i).getName() + " " + shares.get(i).getTicker() +
                    " " + api.getMarketDataService().getLastPricesSync(List.of(shares.get(i).getFigi())).get(0).getPrice());
        }
    }








    public static String timestampToString(Timestamp timestamp) {
        return epochMillisToString(timestamp.getSeconds() * 1_000);
    }

    public static BigDecimal quotationToBigDecimal(Quotation value) {
        if (value == null) {
            return null;
        }
        return mapUnitsAndNanos(value.getUnits(), value.getNano());
    }

    private static List<String> randomFigi(InvestApi api, int count) {
        return api.getInstrumentsService().getTradableSharesSync()
                .stream()
                .filter(el -> Boolean.TRUE.equals(el.getApiTradeAvailableFlag()))
                .map(Share::getFigi)
                .limit(count)
                .collect(Collectors.toList());
    }
}
