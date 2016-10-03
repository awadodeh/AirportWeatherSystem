package com.crossover.trial.weather.models;

import java.io.Serializable;

/**
 * encapsulates sensor information for a particular location
 */
public class Atmosphereinfo implements Serializable {

    private static final long serialVersionUID = -5317972341273507458L;

    /**
     * temperature in degrees celsius
     */
    private DataPoint temperature;

    /**
     * wind speed in km/h
     */
    private DataPoint wind;

    /**
     * humidity in percent
     */
    private DataPoint humidity;

    /**
     * precipitation in cm
     */
    private DataPoint precipitation;

    /**
     * pressure in mmHg
     */
    private DataPoint pressure;

    /**
     * cloud cover percent from 0 - 100 (integer)
     */
    private DataPoint cloudCover;

    /**
     * the last time this data was updated, in milliseconds since UTC epoch
     */
    private long lastUpdateTime;

    public Atmosphereinfo() {
    }

    public Atmosphereinfo(DataPoint temperature, DataPoint wind, DataPoint humidity, DataPoint percipitation, DataPoint pressure, DataPoint cloudCover) {
        this.temperature = temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.precipitation = percipitation;
        this.pressure = pressure;
        this.cloudCover = cloudCover;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public DataPoint getTemperature() {
        return temperature;
    }

    public void setTemperature(DataPoint temperature) {
        this.temperature = temperature;
    }

    public DataPoint getWind() {
        return wind;
    }

    public void setWind(DataPoint wind) {
        this.wind = wind;
    }

    public DataPoint getHumidity() {
        return humidity;
    }

    public void setHumidity(DataPoint humidity) {
        this.humidity = humidity;
    }

    public DataPoint getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(DataPoint precipitation) {
        this.precipitation = precipitation;
    }

    public DataPoint getPressure() {
        return pressure;
    }

    public void setPressure(DataPoint pressure) {
        this.pressure = pressure;
    }

    public DataPoint getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(DataPoint cloudCover) {
        this.cloudCover = cloudCover;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public int hashCode() {
        int result = temperature != null ? temperature.hashCode() : 0;
        result = 31 * result + (wind != null ? wind.hashCode() : 0);
        result = 31 * result + (humidity != null ? humidity.hashCode() : 0);
        result = 31 * result + (precipitation != null ? precipitation.hashCode() : 0);
        result = 31 * result + (pressure != null ? pressure.hashCode() : 0);
        result = 31 * result + (cloudCover != null ? cloudCover.hashCode() : 0);
        result = 31 * result + (int) (lastUpdateTime ^ (lastUpdateTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Atmosphereinfo that = (Atmosphereinfo) o;

        if (lastUpdateTime != that.lastUpdateTime) return false;
        if (temperature != null ? !temperature.equals(that.temperature) : that.temperature != null) return false;
        if (wind != null ? !wind.equals(that.wind) : that.wind != null) return false;
        if (humidity != null ? !humidity.equals(that.humidity) : that.humidity != null) return false;
        if (precipitation != null ? !precipitation.equals(that.precipitation) : that.precipitation != null)
            return false;
        if (pressure != null ? !pressure.equals(that.pressure) : that.pressure != null) return false;
        return cloudCover != null ? cloudCover.equals(that.cloudCover) : that.cloudCover == null;

    }

    @Override
    public String toString() {
        return "AtmosphericInformation{" +
                "temperature=" + temperature +
                ", wind=" + wind +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", pressure=" + pressure +
                ", cloudCover=" + cloudCover +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }

}
