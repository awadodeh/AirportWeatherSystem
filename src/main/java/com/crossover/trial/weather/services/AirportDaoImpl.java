package com.crossover.trial.weather.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crossover.trial.weather.models.Airport;
import com.crossover.trial.weather.models.Atmosphereinfo;
import com.crossover.trial.weather.models.DataPoint;
import com.crossover.trial.weather.models.DataPointType;

/**
 * General implementation of the DAO that can be used for any key-value storage.
 *
 * For example:
 * Simple in-memory storage based on ConcurrentHashMap
 * @see SimpleAirportDaoImpl
 * Clusterable storage based on Hazelcast framework
 * @see HazelcastAirportDaoImpl
 *
 * It also can be used with MongoDB or Cassandra.
 *
 */
public abstract class AirportDaoImpl implements AirportDao {

    private static final Logger LOGGER = Logger.getLogger(AirportDaoImpl.class.getName());

    protected abstract Map<String, Airport> getAirportDataStorage();

    protected abstract Map<String, Atmosphereinfo> getAtmosphericInformationDataStorage();

    @Override
    public Airport findAirportData(String iataCode) {
        if (iataCode == null) {
            LOGGER.severe("iataCode is null");
            return null;
        }
        return getAirportDataStorage().get(iataCode);
    }

    @Override
    public Set<Airport> findNearbyAirports(String iataCode, double radius) {
        Set<Airport> result = new HashSet<>();
        if (iataCode == null) {
            LOGGER.severe("iataCode is null");
            return result;
        }

        Airport ad = getAirportDataStorage().get(iataCode);
        if (ad == null) {
            LOGGER.severe("Cannot find airport iataCode = " + iataCode);
            return result;
        }

        for (Airport a : getAirportDataStorage().values()) {
            if (calculateDistance(ad, a) <= radius) {
                result.add(a);
            }
        }

        return result;
    }

    @Override
    public Set<String> getAllAirportCodes() {
        return getAirportDataStorage().keySet();
    }

    @Override
    public Atmosphereinfo findAtmosphericInformation(String iataCode) {
        if (iataCode == null) return null;
        return getAtmosphericInformationDataStorage().get(iataCode);
    }

    @Override
    public List<Atmosphereinfo> findAtmosphericInformationNearbyAirport(String iataCode, double radius) {
        List<Atmosphereinfo> result = new ArrayList<>();
        if (iataCode == null) return result;

        for (Airport ad : findNearbyAirports(iataCode, radius)) {
            Atmosphereinfo ai = getAtmosphericInformationDataStorage().get(ad.getIata());
            if (ai != null) {
                result.add(ai);
            }
        }

        return result;
    }

    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    private double calculateDistance(Airport ad1, Airport ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
        double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));

        return 6372.8 * c;
    }

    public void updateAtmosphericInformation(String iataCode, String pointType, DataPoint dp) {
        if (iataCode == null) throw new IllegalArgumentException("IATA code is null");
        if (getAirportDataStorage().get(iataCode) == null) throw new IllegalArgumentException("Unknown IATA code");
        if (pointType == null) throw new IllegalArgumentException("pointType is null");
        if (dp == null) throw new IllegalArgumentException("Data point is null");

        Atmosphereinfo oldValue = getAtmosphericInformationDataStorage().get(iataCode);
        if (oldValue == null) {
            oldValue = new Atmosphereinfo();
            getAtmosphericInformationDataStorage().putIfAbsent(iataCode, oldValue);
            oldValue = getAtmosphericInformationDataStorage().get(iataCode);
        }

        while (true) {
            Atmosphereinfo newValue = new Atmosphereinfo(oldValue.getTemperature(), oldValue.getHumidity(), oldValue.getWind(), oldValue.getPrecipitation(), oldValue.getPressure(), oldValue.getCloudCover());
            updateAtmosphericInformation(newValue, pointType, dp);
            if (getAtmosphericInformationDataStorage().replace(iataCode, oldValue, newValue)) break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "updateAtmosphericInformation", e);
            }
        }
    }

    protected void updateAtmosphericInformation(Atmosphereinfo ai, String pointType, DataPoint dp) {
        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
            } else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
            } else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.HUMIDTY.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
            } else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
            } else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
            } else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
            } else throw new IllegalArgumentException("");
        }
        ai.setLastUpdateTime(System.currentTimeMillis());
    }

    @Override
    public void saveAirport(Airport ad) {
        if (ad == null || ad.getIata() == null) {
            LOGGER.severe("Cannot save airport");
            return;
        }
        getAirportDataStorage().put(ad.getIata(), ad);
    }

    @Override
    public void deleteAirport(String iataCode) {
        if (iataCode == null) {
            LOGGER.severe("Cannot delete airport");
            return;
        }
        deleteAtmosphericInformation(iataCode);
        getAirportDataStorage().remove(iataCode);
    }

    @Override
    public void deleteAtmosphericInformation(String iataCode) {
        if (iataCode == null) {
            LOGGER.severe("Cannot delete atmospheric information");
            return;
        }
        getAtmosphericInformationDataStorage().remove(iataCode);
    }

}