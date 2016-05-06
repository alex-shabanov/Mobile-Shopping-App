package org.admin.pkg.ShoppingPointServer;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.server.mvc.Viewable;

import org.admin.pkg.ShoppingPointServer.models.AdminAccounts;
import org.admin.pkg.ShoppingPointServer.persistence.AesEncryption;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBAdminAccountsManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBAdminRegistrationManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBConsumerItemManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerAccountManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerOrdersManager;
import org.admin.pkg.ShoppingPointServer.persistence.VerifyRecaptcha;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("login")
public class MyResource {
    
	/**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
   
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getIt() {
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "mustsignin");
    	return Response.ok(new Viewable("/login", map)).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response getLoginStatus(@FormParam("username") String name, @FormParam("password") String userpswd,
    		MultivaluedMap<String, String> formParams) throws IOException {
        
    	/* UNCOMMENT THIS FOR DATABASE SET UP AND COMMENT OUT AGAIN SO THAT TABLES ARE NOT DROPPED AND RECREATED EACH TIME */
    	
		//HibernateDBAdminAccountsManager.getDefault().setupTable();     // setting up AdminAccounts database table
		//HibernateDBAdminRegistrationManager.getDefault().setupTable(); // setting up AdminRegistration database table
		//HibernateDBConsumerItemManager.getDefault().setupTable();      // setting up ConsumerItem database table
		//HibernateDBCustomerAccountManager.getDefault().setupTable();   // setting up CustomerAccount database table
		//HibernateDBCustomerManager.getDefault().setupTable();          // setting up Customer database table 
		//HibernateDBCustomerOrdersManager.getDefault().setupTable();    // setting up CustomerOrders database table
    	
    	Map<String, Object> hmap = new HashMap<String, Object>();
    	/*Iterator<String> it = formParams.keySet().iterator();
        while(it.hasNext()){
        	String theKey = (String)it.next();
        	System.out.println("FORM PARAMETERS: " + theKey);
        }*/
		String gRecaptchaResponse = formParams.getFirst("g-recaptcha-response");
        boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
        if(!verify){
        	hmap.put("user", "invalidrecaptcha");
            return Response.ok(new Viewable("/login", hmap)).build();
        }
    	
    	if(name.trim().length() == 0 && userpswd.trim().length() == 0){
    		Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", "emptyusernamepassword");
            return Response.ok(new Viewable("/login", map)).build();
    	}
    	else if(name.trim().length() == 0){
    		Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", "emptyusername");
            return Response.ok(new Viewable("/login", map)).build();
    	}
    	else if(userpswd.trim().length() == 0){
    		Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", "emptypassword");
            return Response.ok(new Viewable("/login", map)).build();
    	}
    	AdminAccounts adminAccount = null;
    	adminAccount = HibernateDBAdminAccountsManager.getDefault().getAdmninAccountWithName(name);
    	if(adminAccount == null){
    		Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", "mustregister");
            return Response.ok(new Viewable("/login", map)).build();
    	}
    	AesEncryption.setKey("salt");
    	String str = adminAccount.getAdminpassword();
    	AesEncryption.decrypt(str);
    	String encryptedPswd = AesEncryption.getDecryptedString();
    	if(userpswd.equalsIgnoreCase(encryptedPswd)){
    		String loginStatus = adminAccount.getLoginstatus();
    		if(loginStatus.equalsIgnoreCase("logged in")){
    			
    		}
    		Calendar calendar = Calendar.getInstance();
    		//Timestamp lastLogInTime = adminAccount.getResetTime();
    		Timestamp currentTime = new Timestamp(calendar.getTimeInMillis());
    		
    		/*long timeIn = lastLogInTime.getTime();
    		long currTime = currentTime.getTime();
    		System.out.println("LAST LOGIN TIME: " + String.valueOf(timeIn));
    		System.out.println("CURRENT TIME: " + String.valueOf(currTime));
    		long difference = Math.abs(timeIn - currTime);
    		if(difference > 1 * 60 * 1000){
    			System.out.println("LOGIN TIME IS GREATER THAN LOGOUT TIME BY ONE MINUTE");
    		}*/
    		
    		HibernateDBAdminAccountsManager.getDefault().incrementHitCounterByName(name);
    		adminAccount.setResetTime(currentTime);
    		adminAccount.setLoginstatus("logged in");
    		HibernateDBAdminAccountsManager.getDefault().update(adminAccount);
    		Map<String, Object> map = new HashMap<String, Object>();
            map.put("status", "passwordok");
            map.put("username", name);
            map.put("password", userpswd);
            return Response.ok(new Viewable("/shoppingpoint", map)).build();
    	}
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "invalidpswd");
        return Response.ok(new Viewable("/login", map)).build();          
    }  
}
