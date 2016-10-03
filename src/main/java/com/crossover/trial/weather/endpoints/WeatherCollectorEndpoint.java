package com.crossover.trial.weather.endpoints;

import static com.crossover.trial.weather.endpoints.Paths.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.models.Airport;
import com.crossover.trial.weather.models.DataPointType;

/**
 * The interface shared to airport weather collection systems.
 *
 * @author code test administartor
 */
@Path(COLLECT)
public interface WeatherCollectorEndpoint {

    /**
     * A liveliness check for the collection endpoint.
     *
     * @return 1 if the endpoint is alive functioning, 0 otherwise
     */
    @GET
    @Path(PING)
    Response ping();

    /**
     * Update the airports atmospheric information for a particular pointType with
     * json formatted data point information.
     *
     * @param iataCode      the 3 letter airport code
     * @param pointType     the point type, {@link DataPointType} for a complete list
     * @param datapointJson a json dict containing mean, first, second, thrid and count keys
     * @return HTTP Response code
     */
    @POST
    @Path(WEATHER+"/{"+IATA+"}/{"+POINT_TYPE+"}")
    Response updateWeather(@PathParam(IATA) String iataCode,
                           @PathParam(POINT_TYPE) String pointType,
                           String datapointJson);

    /**
     * Return a list of known airports as a json formatted list
     *
     * @return HTTP Response code and a json formatted list of IATA codes
     */
    @GET
    @Path(AIRPORTS)
    @Produces(MediaType.APPLICATION_JSON)
    Response getAirports();

    /**
     * Retrieve airport data, including latitude and longitude for a particular airport
     *
     * @param iata the 3 letter airport code
     * @return an HTTP Response with a json representation of {@link Airport}
     */
    @GET
    @Path(AIRPORT+"/{"+IATA+"}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getAirport(@PathParam(IATA) String iata);

    /**
     * Add a new airport to the known airport list.
     *
     * @param iata       the 3 letter airport code of the new airport
     * @param latString  the airport's latitude in degrees as a string [-90, 90]
     * @param longString the airport's longitude in degrees as a string [-180, 180]
     * @return HTTP Response code for the add operation
     */

    @POST
    @Path(AIRPORT+"/{"+IATA+"}/{"+LAT+"}/{"+LONG+"}")
    Response addAirport(@PathParam(IATA) String iata,
                        @PathParam(LAT) String latString,
                        @PathParam(LONG) String longString);

    /**
     * Remove an airport from the known airport list
     *
     * @param iata the 3 letter airport code
     * @return HTTP Response code for the delete operation
     */
    @DELETE
    @Path(AIRPORT+"/{"+IATA+"}")
    Response deleteAirport(@PathParam(IATA) String iata);

    /**
     * Shutdown airport weather collection system
     *
     * @return HTTP Response code for the exit operation
     * @deprecated it's not possible to shutdown the service
     */
    @GET
    @Path(EXIT)
    @Deprecated
    Response exit();

}
