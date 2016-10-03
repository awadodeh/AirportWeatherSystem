package com.crossover.trial.weather.services;

import com.crossover.trial.weather.models.Airport;
import com.crossover.trial.weather.models.Atmosphereinfo;
import com.crossover.trial.weather.models.RequestFrequencyData;
import com.crossover.trial.weather.utils.Storage;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of the performance DAO using simple in-memory data storage
 */
@Service
@Deprecated
public class SimplePerformanceDaoImpl implements PerformanceDao {

    @Resource
    private AirportDao airportDao;

    @Override
    public void updateRequestFrequency(String iata, Double radius) {
        Airport airportData = airportDao.findAirportData(iata);

        if (airportData != null) {
            AtomicInteger i = Storage.getREQUEST_FREQUENCY().get(airportData);
            if (i == null) {
                Storage.getREQUEST_FREQUENCY().putIfAbsent(airportData, new AtomicInteger(0));
                i = Storage.getREQUEST_FREQUENCY().get(airportData);

            }
            i.incrementAndGet();

            // radius cannot be less than zero
            if (radius < 0) radius = 0d;
            // all request with radius above 1000 to one counter. It's unusual to request radius > 1000.
            if (radius > 1000) radius = 1000d;

            i = Storage.getRADIUS_FREQUENCY().get(radius);
            if (i == null) {
                Storage.getRADIUS_FREQUENCY().putIfAbsent(radius, new AtomicInteger(0));
                i = Storage.getRADIUS_FREQUENCY().get(radius);
            }
            i.incrementAndGet();
        }
    }

    @Override
    public RequestFrequencyData getPerformanceData() {
        RequestFrequencyData data = new RequestFrequencyData();

        int dataSize = 0;
        for (Atmosphereinfo ai : Storage.getATMOSPHERIC_INFORMATION().values()) {
            // we only count recent readings
            if (ai.getCloudCover() != null
                    || ai.getHumidity() != null
                    || ai.getPressure() != null
                    || ai.getPrecipitation() != null
                    || ai.getTemperature() != null
                    || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    dataSize++;
                }
            }
        }

        data.setDataSize(dataSize);

        Map<String, Double> freq = new HashMap<>();

        int count = 0;
        for (AtomicInteger i : Storage.getREQUEST_FREQUENCY().values()) {
            count += i.get();
        }

        if (count > 0) {
            for (Airport ad : Storage.getAIRPORT_DATA().values()) {
                double frac = (double) Storage.getREQUEST_FREQUENCY().getOrDefault(ad, new AtomicInteger(0)).intValue() / count;
                freq.put(ad.getIata(), frac);
            }
        }

        data.setAirportFrequency(freq);

        int m = Storage.getRADIUS_FREQUENCY().keySet().stream()
                .max(Double::compare)
                .orElse(0d).intValue();

        if (m < 0) m = 0;
        if (m > 1000) m = 1000;

        int[] hist = new int[m + 1];

        for (Map.Entry<Double, AtomicInteger> e : Storage.getRADIUS_FREQUENCY().entrySet()) {
            int i = e.getKey().intValue();
            hist[i] = e.getValue().intValue();
        }

        data.setRadiusFrequency(hist);

        return data;
    }

    @Override
    public void clear() {
        Storage.getRADIUS_FREQUENCY().clear();
        Storage.getREQUEST_FREQUENCY().clear();
    }

}
