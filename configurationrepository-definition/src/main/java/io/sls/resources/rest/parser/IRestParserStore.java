package io.sls.resources.rest.parser;

import io.sls.resources.rest.IRestVersionInfo;
import io.sls.resources.rest.documentdescriptor.model.DocumentDescriptor;
import io.sls.resources.rest.parser.model.ParserConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * @author ginccc
 */

@Api
@Path("/parserstore/parsers")
public interface IRestParserStore extends IRestVersionInfo {
    String resourceURI = "eddi://ai.labs.parser/parserstore/parsers/";


    @GET
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", format = "string", example = "<name_of_parser>"),
            @ApiImplicitParam(name = "index", format = "integer", example = "<at what position should the paging start>"),
            @ApiImplicitParam(name = "limit", format = "integer", example = "<how many results should be returned>")})
    @ApiResponse(code = 200, response = DocumentDescriptor.class, responseContainer = "List",
            message = "Array of DocumentDescriptors")
    List<DocumentDescriptor> readParserDescriptors(@QueryParam("filter") @DefaultValue("") String filter,
                                                   @QueryParam("index") @DefaultValue("0") Integer index,
                                                   @QueryParam("limit") @DefaultValue("20") Integer limit);


    @GET
    @GZIP
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(code = 200, response = ParserConfiguration.class, message = "configuration of parser")
    ParserConfiguration readParser(@PathParam("id") String id, @QueryParam("version") @DefaultValue("-1") Integer version) throws Exception;

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    URI updateParser(@PathParam("id") String id, @QueryParam("version") @DefaultValue("-1") Integer version,
                     @GZIP ParserConfiguration parserConfiguration);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createParser(@GZIP ParserConfiguration parserConfiguration);

    @DELETE
    @Path("/{id}")
    void deleteParser(@PathParam("id") String id, @QueryParam("version") @DefaultValue("-1") Integer version);
}
