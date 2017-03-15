/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import XML.Services;
import com.google.gson.Gson;
import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

    public GenericResource() {
        this.service = new Services();
    }

    @GET
    @Path("World")
    @Produces(MediaType.APPLICATION_XML)
    public World getXml(/*@Context HttpServletRequest request*/) {
        //String username = request.getHeader("X-user");
        return service.readWorldFromXml();
    }

    @GET
    @Path("World")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(/*@Context HttpServletRequest request*/) {
        // String username = request.getHeader("X-user");
        return new Gson().toJson(service.readWorldFromXml());
    }

    @PUT
    @Path("Product")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putProduct(String json) {
        ProductType produit = new Gson().fromJson(json, ProductType.class);
        service.updateProduct("Michel", produit);
    }

    @PUT
    @Path("Manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putManager(String json) {
        PallierType pallier = new Gson().fromJson(json, PallierType.class);
        service.updateManager("Michel", pallier);
    }

    @PUT
    @Path("Upgrade")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putUpgrade(String json) {
        PallierType pallier = new Gson().fromJson(json, PallierType.class);
        service.updateUpgrade("Michel",pallier);
    }

    @PUT
    @Path("AngelUpgrade")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putAngelUpgrade(String json) {
        PallierType pallier = new Gson().fromJson(json, PallierType.class);
    }

    @DELETE
    @Path("World")
    public void deleteWorld() {
        
    }
}
