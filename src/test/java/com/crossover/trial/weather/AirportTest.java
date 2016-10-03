package com.crossover.trial.weather;

import org.junit.After;

import com.crossover.trial.weather.models.Airport;
import com.crossover.trial.weather.models.Atmosphereinfo;
import com.crossover.trial.weather.models.DataPoint;
import com.crossover.trial.weather.models.DataPointType;
import com.crossover.trial.weather.services.AirportDao;
import com.crossover.trial.weather.services.PerformanceDao;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Base class for DAO testing
 */
public class AirportTest {

    @Resource
    private AirportDao airportDao;
    @Resource
    private PerformanceDao performanceDao;

    public void testFindAirportData() {
        Airport ad = new Airport("AAA", 1, 2);
        airportDao.saveAirport(ad);

        ad = airportDao.findAirportData("AAA");
        assertNotNull(ad);
        assertEquals(ad.getIata(), "AAA");
        assertTrue(ad.getLatitude() == 1);
        assertTrue(ad.getLongitude() == 2);
    }

    public void testFindAirportDataNull() {
        Airport ad = airportDao.findAirportData(null);
        assertNull(ad);
    }

    public void testFindAirportDataUnknown() {
        Airport ad = airportDao.findAirportData("");
        assertNull(ad);
    }

    public void testFindNearbyAirports() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("AAA"));

        ad = new Airport("BBB", 2, 2);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("BBB"));

        Set<Airport> result = airportDao.findNearbyAirports("AAA", 1);
        assertTrue(result.size() == 1);
        assertEquals(result.iterator().next().getIata(), "AAA");

        result = airportDao.findNearbyAirports("AAA", 1000d);
        assertTrue(result.size() == 2);
    }

    public void testFindNearbyAirportsNull() {
        Set<Airport> result = airportDao.findNearbyAirports(null, 1);
        assertTrue(0 == result.size());
    }

    public void testFindNearbyAirportsUnknown() {
        Set<Airport> result = airportDao.findNearbyAirports("", 1);
        assertTrue(0 == result.size());
    }

    public void testGetAllAirportCodes() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("AAA"));

        ad = new Airport("BBB", 2, 2);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("BBB"));

        ad = new Airport("CCC", 3, 3);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("CCC"));

        Set<String> result = airportDao.getAllAirportCodes();
        assertTrue(result.size() == 3);
    }

    public void testFindAtmosphericInformation() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);

        Atmosphereinfo result = airportDao.findAtmosphericInformation("AAA");
        assertNull(result);

        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(10, 20, 30, 40, 50));

        result = airportDao.findAtmosphericInformation("AAA");
        assertNotNull(result);
    }

    public void testFindAtmosphericInformationNull() {
        Atmosphereinfo result = airportDao.findAtmosphericInformation(null);
        assertNull(result);
    }

    public void testFindAtmosphericInformationUnknown() {
        Atmosphereinfo result = airportDao.findAtmosphericInformation("");
        assertNull(result);
    }

    public void testFindAtmosphericInformationNearbyAirport() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);

        ad = new Airport("BBB", 2, 2);
        airportDao.saveAirport(ad);

        List<Atmosphereinfo> result = airportDao.findAtmosphericInformationNearbyAirport("AAA", 1);
        assertTrue(result.size() == 0);

        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(10, 20, 30, 40, 50));
        airportDao.updateAtmosphericInformation("BBB", "wind", new DataPoint(50, 40, 30, 20, 10));

        result = airportDao.findAtmosphericInformationNearbyAirport("AAA", 1);
        assertTrue(result.size() == 1);

        result = airportDao.findAtmosphericInformationNearbyAirport("AAA", 1000);
        assertTrue(result.size() == 2);
    }

    public void testFindAtmosphericInformationNearbyAirportNull() {
        List<Atmosphereinfo> result = airportDao.findAtmosphericInformationNearbyAirport(null, 1);
        assertTrue(result.size() == 0);
    }

    public void testFindAtmosphericInformationNearbyAirportUnknown() {
        List<Atmosphereinfo> result = airportDao.findAtmosphericInformationNearbyAirport("", 1);
        assertTrue(result.size() == 0);
    }

    public void testUpdateAtmosphericInformation() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("AAA"));

        airportDao.updateAtmosphericInformation("AAA", DataPointType.WIND.name(), new DataPoint(10, 20, 30, 40, 50));
        assertNotNull(airportDao.findAtmosphericInformation("AAA"));
        Atmosphereinfo ai = airportDao.findAtmosphericInformation("AAA");
        assertTrue(ai.getWind().getFirst() == 10);
        assertTrue(ai.getWind().getSecond() == 20);
        assertTrue(ai.getWind().getMean() == 30);
        assertTrue(ai.getWind().getThird() == 40);
        assertTrue(ai.getWind().getCount() == 50);

        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(50, 40, 30, 20, 10));
        ai = airportDao.findAtmosphericInformation("AAA");
        assertTrue(ai.getWind().getFirst() == 50);
        assertTrue(ai.getWind().getSecond() == 40);
        assertTrue(ai.getWind().getMean() == 30);
        assertTrue(ai.getWind().getThird() == 20);
        assertTrue(ai.getWind().getCount() == 10);
    }

    public void testUpdateAtmosphericInformationNull() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);

        IllegalArgumentException exception = null;
        try {
            airportDao.updateAtmosphericInformation(null, DataPointType.WIND.name(), new DataPoint(10, 20, 30, 40, 50));
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;
        try {
            airportDao.updateAtmosphericInformation("AAA", null, new DataPoint(10, 20, 30, 40, 50));
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;
        try {
            airportDao.updateAtmosphericInformation("AAA", DataPointType.WIND.name(), null);
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    public void testUpdateAtmosphericInformationUnknown() {
        IllegalArgumentException exception = null;
        try {
            airportDao.updateAtmosphericInformation("", DataPointType.WIND.name(), new DataPoint(10, 20, 30, 40, 50));
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    public void testSaveAirport() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("AAA"));
    }

    public void testSaveAirportNull() {
        airportDao.saveAirport(null);
        airportDao.saveAirport(new Airport(null, 1, 1));
    }

    public void testDeleteAirport() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("AAA"));

        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(10, 20, 30, 40, 50));
        assertNotNull(airportDao.findAtmosphericInformation("AAA"));

        airportDao.deleteAirport("AAA");
        assertNull(airportDao.findAirportData("AAA"));
        assertNull(airportDao.findAtmosphericInformation("AAA"));
    }

    public void testDeleteAirportNull() {
        airportDao.deleteAirport(null);
    }

    public void testDeleteAtmosphericInformation() {
        Airport ad = new Airport("AAA", 1, 1);
        airportDao.saveAirport(ad);
        assertNotNull(airportDao.findAirportData("AAA"));

        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(10, 20, 30, 40, 50));
        assertNotNull(airportDao.findAtmosphericInformation("AAA"));

        airportDao.deleteAtmosphericInformation("AAA");
        assertNotNull(airportDao.findAirportData("AAA"));
        assertNull(airportDao.findAtmosphericInformation("AAA"));
    }

    public void testDeleteAtmosphericInformationNull() {
        airportDao.deleteAtmosphericInformation(null);
    }

    public void testDeleteAtmosphericInformationUnknown() {
        airportDao.deleteAtmosphericInformation("");
    }

    @After
    public void cleanup() {
        airportDao.deleteAirport("AAA");
        airportDao.deleteAirport("BBB");
        airportDao.deleteAirport("CCC");
        performanceDao.clear();
    }

}
