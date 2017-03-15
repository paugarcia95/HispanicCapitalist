/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import XML.Services;
import com.google.gson.Gson;
import generated.World;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author killian
 */
@Path("generic")
public class GenericResource {

    private Services service;
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
        this.service = new Services();
    }

    /**
     * Retrieves representation of an instance of Rest.GenericResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Path("World")
    @Produces(MediaType.APPLICATION_XML)
    public World getXml() {
        return service.readWorldFromXml();
    }

    @GET
    @Path("World")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJSon() {
        new Gson().toJson(service.readWorldFromXml());
        return new Gson().toJson(service.readWorldFromXml());
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
