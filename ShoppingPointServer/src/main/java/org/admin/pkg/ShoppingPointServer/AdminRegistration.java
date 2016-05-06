package org.admin.pkg.ShoppingPointServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.mvc.Viewable;

import org.admin.pkg.ShoppingPointServer.persistence.AesEncryption;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBAdminAccountsManager;
import org.admin.pkg.ShoppingPointServer.models.AdminAccounts;
import org.admin.pkg.ShoppingPointServer.models.AdminAccountRegistration;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBAdminRegistrationManager;
import org.admin.pkg.ShoppingPointServer.persistence.VerifyRecaptcha;

@Path("registration")
public class AdminRegistration {
	
	@GET
    @Produces(MediaType.TEXT_HTML)
    public Response adminRegistration() {
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "loginnotsuccessful");
        return Response.ok(new Viewable("/adminregistration", map)).build();
    }
	
	@POST
	@Path("{adminregistration}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
	public Response adminRegistrationForm(){
		return Response.ok(new Viewable("/adminregistration")).build();
	}
    
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response getRegistrationStatus(@FormParam("firstname") String firstname, @FormParam("lastname") String lastname,
    		@FormParam("employeeId") String employeeId, @FormParam("username") String username,
    		@FormParam("password") String password, MultivaluedMap<String, String> formParams) throws IOException{
		
		Map<String, Object> hmap = new HashMap<String, Object>();
		String gRecaptchaResponse = formParams.getFirst("g-recaptcha-response");
        boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
        if(!verify){
        	hmap.put("user", "invalidrecaptcha");
            return Response.ok(new Viewable("/adminregistration", hmap)).build();
        }
    	
    	AesEncryption.setKey("salt");
    	String firstName = "", lastName = "", userName = "", pswd = "";
    	String encryptedEmpId = "";
    	String decryptedEmpId = "";
    	AdminAccountRegistration account = null;
    	List<AdminAccountRegistration> list = null;
    	list = HibernateDBAdminRegistrationManager.getDefault().getAllAdminRegistrations();

    	for(int i = 0; i < list.size(); i++){
    		account = list.get(i);
    		firstName = account.getAdminfirstname();
    		lastName = account.getAdminlastname();
    		userName = account.getAdminname();
    		encryptedEmpId = account.getEmployeeid();
    		pswd = account.getAdminpassword();
    		AesEncryption.decrypt(encryptedEmpId);
    		decryptedEmpId = AesEncryption.getDecryptedString();
    		if(decryptedEmpId.equalsIgnoreCase(employeeId)){
    			if(!firstName.equalsIgnoreCase("none") && !lastName.equalsIgnoreCase("none") &&
    					!userName.equalsIgnoreCase("none") && !pswd.equalsIgnoreCase("none")){
    				Map<String, Object> map = new HashMap<String, Object>();
    				map.put("user", "accountexists");
    				return Response.ok(new Viewable("/adminregistration", map)).build();
    			}
    			account.setAdminfirstname(firstname);
    			account.setAdminlastname(lastname);
    			account.setAdminname(username);
    			AesEncryption.encrypt(password);
    			String encryptedPswd = AesEncryption.getEncryptedString();
    			account.setAdminpassword(encryptedPswd);
    			account.setCounter(0);
    			HibernateDBAdminRegistrationManager.getDefault().update(account);

    			AdminAccounts adminAccount = new AdminAccounts(username, encryptedPswd, "no", 0);
    			HibernateDBAdminAccountsManager.getDefault().add(adminAccount);
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("user", "yesregistration");
    			return Response.ok(new Viewable("/login", map)).build();
    		}
    	}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", "noregistration");
		return Response.ok(new Viewable("/adminregistration", map)).build();
	}
}
