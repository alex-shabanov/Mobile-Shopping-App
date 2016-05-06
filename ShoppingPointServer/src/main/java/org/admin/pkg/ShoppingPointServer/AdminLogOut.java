package org.admin.pkg.ShoppingPointServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.admin.pkg.ShoppingPointServer.models.AdminAccounts;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBAdminAccountsManager;
import org.glassfish.jersey.server.mvc.Viewable;


@Path("signout")
public class AdminLogOut {
	
	@GET
    @Produces(MediaType.TEXT_HTML)
    public Response adminLogOut(@Context ServletContext context, @Context HttpServletRequest req) throws URISyntaxException {
        URI uri = new URI("/ShoppingPointServer");
        req.getSession().invalidate(); // invalidating session upon logout
        return Response.temporaryRedirect(uri).build();
    }
	
	@GET
	@Path("/sameadmin")
    @Produces(MediaType.TEXT_HTML)
    public Response sameAdminLogIn() throws URISyntaxException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "alreadyloggedin");
        return Response.ok(new Viewable("/login", map)).build();
    }
	
	@GET
	@Path("/timeout")
    @Produces(MediaType.TEXT_HTML)
    public Response sessionTimeOut(@Context HttpServletRequest req) throws URISyntaxException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "sessiontimeout");
        req.getSession().invalidate(); // invalidating session upon session time out
        return Response.ok(new Viewable("/login", map)).build();
    }
}
