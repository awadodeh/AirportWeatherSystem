package com.crossover.trial.weather.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.models.Airport;
import com.crossover.trial.weather.models.Atmosphereinfo;

/**
 * Simple in-memory data storage. Production system has to implement more reliable storage.
 */
public class Storage {

    /**
     * All known airports
     */
    private static Map<String, Airport> AIRPORT_DATA = new ConcurrentHashMap<>();

    /**
     * Atmospheric information for each airport
     */
    private static Map<String, Atmosphereinfo> ATMOSPHERIC_INFORMATION = new ConcurrentHashMap<>();

    /**
     * Internal performance counters to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link com.crossover.trial.weather.RestWeatherQueryEndpoint}
     */
    private static Map<Airport, AtomicInteger> REQUEST_FREQUENCY = new ConcurrentHashMap<>();
    private static Map<Double, AtomicInteger> RADIUS_FREQUENCY = new ConcurrentHashMap<>();
	public synchronized static Map<Airport, AtomicInteger> getREQUEST_FREQUENCY() {
		return REQUEST_FREQUENCY;
	}
	public synchronized static void setREQUEST_FREQUENCY(Map<Airport, AtomicInteger> rEQUEST_FREQUENCY) {
		REQUEST_FREQUENCY = rEQUEST_FREQUENCY;
	}
	public synchronized static Map<Double, AtomicInteger> getRADIUS_FREQUENCY() {
		return RADIUS_FREQUENCY;
	}
	public synchronized static void setRADIUS_FREQUENCY(Map<Double, AtomicInteger> rADIUS_FREQUENCY) {
		RADIUS_FREQUENCY = rADIUS_FREQUENCY;
	}
	public synchronized static Map<String, Atmosphereinfo> getATMOSPHERIC_INFORMATION() {
		return ATMOSPHERIC_INFORMATION;
	}
	public synchronized static void setATMOSPHERIC_INFORMATION(Map<String, Atmosphereinfo> aTMOSPHERIC_INFORMATION) {
		ATMOSPHERIC_INFORMATION = aTMOSPHERIC_INFORMATION;
	}
	public synchronized static Map<String, Airport> getAIRPORT_DATA() {
		return AIRPORT_DATA;
	}
	public synchronized static void setAIRPORT_DATA(Map<String, Airport> aIRPORT_DATA) {
		AIRPORT_DATA = aIRPORT_DATA;
	}

}
