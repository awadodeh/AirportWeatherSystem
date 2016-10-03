package com.crossover.trial.weather.services;

import com.crossover.trial.weather.models.Airport;
import com.crossover.trial.weather.models.Atmosphereinfo;
import com.hazelcast.core.HazelcastInstance;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Implementation of the airport DAO using Hazelcast framework.
 * <p>
 * It's an example of the implementation that can be used in production.
 */
public class HazelcastAirportDaoImpl extends AirportDaoImpl {

    @Resource
    private HazelcastInstance hazelcastInstance;

    @Override
    protected Map<String, Airport> getAirportDataStorage() {
        return hazelcastInstance.getMap("AIRPORT_DATA");
    }

    @Override
    protected Map<String, Atmosphereinfo> getAtmosphericInformationDataStorage() {
        return hazelcastInstance.getMap("ATMOSPHERIC_INFORMATION");
    }

}
