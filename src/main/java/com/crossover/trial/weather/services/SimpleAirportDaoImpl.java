package com.crossover.trial.weather.services;

import org.springframework.stereotype.Service;

import com.crossover.trial.weather.models.Airport;
import com.crossover.trial.weather.models.Atmosphereinfo;
import com.crossover.trial.weather.utils.Storage;

import java.util.Map;

/**
 * Implementation of the airport DAO using simple in-memory data storage
 */
@Service
public class SimpleAirportDaoImpl extends AirportDaoImpl {

    @Override
    protected Map<String, Airport> getAirportDataStorage() {
        return Storage.getAIRPORT_DATA();
    }

    @Override
    protected Map<String, Atmosphereinfo> getAtmosphericInformationDataStorage() {
        return Storage.getATMOSPHERIC_INFORMATION();
    }

}
