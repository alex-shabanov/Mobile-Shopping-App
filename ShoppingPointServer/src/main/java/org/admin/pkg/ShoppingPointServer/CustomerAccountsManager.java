package org.admin.pkg.ShoppingPointServer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.admin.pkg.ShoppingPointServer.models.CustomerToken;
import org.admin.pkg.ShoppingPointServer.persistence.AesEncryption;
import org.admin.pkg.ShoppingPointServer.models.Customer;
import org.admin.pkg.ShoppingPointServer.models.CustomerAccount;
import org.admin.pkg.ShoppingPointServer.models.CustomerOrders;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerAccountManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerOrdersManager;

@Path("/customeraccount")
public class CustomerAccountsManager {
	
	private final static String FIRST_NAME = "firstname";
    private final static String LAST_NAME = "lastname";
    private final static String ADDRESS = "address";
    private final static String CITY = "city";
    private final static String COUNTRY = "country";
    private final static String AREA_CODE = "areacode";
    private final static String EMAIL = "email";
    private final static String PHONE_NUMBER = "phonenumber";
    private final static String USER_NAME = "username";
    private final static String PASSWORD = "password";
    private final static String INCORRECT_PASSWORD = "incorrectpassword";

	// The @Context annotation allows us to have certain contextual objects
    // injected into this class.
    // UriInfo object allows us to get URI information (no kidding).
    @Context
    UriInfo uriInfo;
 
    // Another "injected" object. This allows us to use the information that's
    // part of any incoming request.
    // We could, for example, get header information, or the requestor's address.
    @Context
    Request request;
    
	@GET
    @Path("getaccountbyusername")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerAccount getCustomerAccountByUsername(@Context UriInfo info) {
		AesEncryption.setKey("salt");
		String username = uriInfo.getQueryParameters().getFirst("username");
		String password = uriInfo.getQueryParameters().getFirst("password");
		CustomerAccount account = null;
		account = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountByUsername(username);
		if(account == null){
			account = new CustomerAccount("none", "none", "none", "none", "none", "none", "none", "none", "accountnotexists", "none", "none", 100);
		}
		else if(account != null){
			String encryptedPswd = account.getPassword();
			AesEncryption.decrypt(encryptedPswd);
	    	String decryptedPswd = AesEncryption.getDecryptedString();
	    	if(password.equalsIgnoreCase(decryptedPswd)){
	    		account.setPassword(decryptedPswd);
				account.setCounter(-5);
	    	}
	    	else {
	    		account = new CustomerAccount("none", "none", "none", "none", "none", "none", "none", "none", "passwordnotmatch", "none", "none", 0);
	    	}
		}
		return account;
	}
	
	/* Creating a new customer account */
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public CustomerAccount createCustomerAccount(MultivaluedMap<String, String> customer) {
		AesEncryption.setKey("salt");
        String userFirstName = customer.getFirst(FIRST_NAME);
		String userLastName = customer.getFirst(LAST_NAME);
		String userAddress = customer.getFirst(ADDRESS);
		String userCity = customer.getFirst(CITY);
		String userCountry = customer.getFirst(COUNTRY);
		String userAreacode = customer.getFirst(AREA_CODE);
		String userEmail = customer.getFirst(EMAIL);
		String userPhoneNumber = customer.getFirst(PHONE_NUMBER);
		String username = customer.getFirst(USER_NAME);
		String password = customer.getFirst(PASSWORD);
		
		CustomerAccount customerAccount = null;
		customerAccount = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountByUsername(username);
		
		/* No accounts with such user name exists, so account is open, create account for the user */
		if(customerAccount == null){
			AesEncryption.encrypt(password);
    		String encryptedPswd = AesEncryption.getEncryptedString();
    		/* Getting the highest id from the accounts */
    		int id = 0;
    		CustomerAccount account = null;
    		account = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountWithHighestId();
    		if(account == null){
    			id = 101;
    		}
    		else{
    			id = Integer.parseInt(account.getCustomerid());
    			id = id + 1;
    		}
			customerAccount = new CustomerAccount(userFirstName, userLastName, userAddress, userCity, userCountry, userAreacode,
					userEmail, userPhoneNumber, username, encryptedPswd, String.valueOf(id), 0);
			HibernateDBCustomerAccountManager.getDefault().add(customerAccount); // adding new customer account to database
		}
		else if(customerAccount != null){
			/* returning null customer account indicating that the customer account user name is taken */
			customerAccount = new CustomerAccount("none", "none", "none", "none", "none", "none", "none", "none", "accountexists", "none", "none", 100);
		}
		return customerAccount;
	}
	
	/* Creating a new customer account */
	@POST
	@Path("updateaccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public CustomerAccount updateCustomerAccount(MultivaluedMap<String, String> customer) {
		AesEncryption.setKey("salt");
        String userFirstName = customer.getFirst(FIRST_NAME);
		String userLastName = customer.getFirst(LAST_NAME);
		String userAddress = customer.getFirst(ADDRESS);
		String userCity = customer.getFirst(CITY);
		String userCountry = customer.getFirst(COUNTRY);
		String userAreacode = customer.getFirst(AREA_CODE);
		String userEmail = customer.getFirst(EMAIL);
		String userPhoneNumber = customer.getFirst(PHONE_NUMBER);
		String username = customer.getFirst(USER_NAME);
		String password = customer.getFirst(PASSWORD);
		
		CustomerAccount customerAccount = null;
		customerAccount = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountByUsername(username);
		
		/* No accounts with such user name exists, so account is open, create account for the user */
		if(customerAccount == null){
			customerAccount = new CustomerAccount("none", "none", "none", "none", "none", "none", "none", "none", "accountnotexists", "none", "none", 0);
		}
		else if(customerAccount != null){
    		String encryptedPswd = customerAccount.getPassword();
    		AesEncryption.decrypt(encryptedPswd);
    		String decryptedPswd = AesEncryption.getDecryptedString();
    		if(decryptedPswd.equalsIgnoreCase(password)){
    			customerAccount.setFirstname(userFirstName);
    			customerAccount.setLastname(userLastName);
    			customerAccount.setAddress(userAddress);
    			customerAccount.setCity(userCity);
    			customerAccount.setCountry(userCountry);
    			customerAccount.setAreacode(userAreacode);
    			customerAccount.setEmail(userEmail);
    			customerAccount.setPhonenumber(userPhoneNumber);
    			customerAccount.setUsername(username);
    			customerAccount.setPassword(encryptedPswd);
    			HibernateDBCustomerAccountManager.getDefault().update(customerAccount); // updating customer account
    			/* Getting the newly updated customer account */
    			customerAccount = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountByUsername(username);
    			customerAccount.setCounter(-7);
    		}
    		else {
    			customerAccount = new CustomerAccount("none", "none", "none", "none", "none", "none", "none", "none",
    					"passwordnotmatch", "none", "none", 0);
    		}		
		}
		return customerAccount;
	}
	
	/* Creating a new customer account */
	@POST
	@Path("customersignin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public CustomerAccount customerSignIn(MultivaluedMap<String, String> customer) {
		AesEncryption.setKey("salt");
		String username = customer.getFirst(USER_NAME);
		String password = customer.getFirst(PASSWORD);
		
		CustomerAccount customerAccount = null;
		customerAccount = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountByUsername(username);
		
		/* No accounts with such user name exists, so account is open, create account for the user */
		if(customerAccount == null){
			customerAccount = new CustomerAccount("none", "none", "none", "none", "none", "none", "none", "none", "usernamenotexists", "none", "none", 0);
		}
		else if(customerAccount != null){
    		String encryptedPswd = customerAccount.getPassword();
    		AesEncryption.decrypt(encryptedPswd);
    		String decryptedPswd = AesEncryption.getDecryptedString();
    		if(decryptedPswd.equalsIgnoreCase(password)){
    			customerAccount.setUsername(username);
    			customerAccount.setPassword(encryptedPswd);
    			HibernateDBCustomerAccountManager.getDefault().update(customerAccount); // updating customer account
    			/* Getting the newly updated customer account */
    			customerAccount = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountByUsername(username);
    			customerAccount.setCounter(100);  // counter set to 100 means login is successful
    		}
    		else {
    			customerAccount = new CustomerAccount("none", "none", "none", "none", "none", "none", "none", "none",
    					INCORRECT_PASSWORD, "none", "none", 0);
    		}		
		}
		return customerAccount;
	}
	
	/* Credit Card Validation Class */
	@POST
	@Path("creditcardvalidation")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public CustomerToken creditCartValidation(MultivaluedMap<String, String> info) {
		int lstSize = 0;
		long creditCardNumber = 0;
		String productType, productGeneralName, productSpecificName;
		String productNumber, productQuantity, productPrice;
		CustomerToken customerToken = null;
		String userName = info.getFirst(Constants.USER_NAME);
		String cardName = info.getFirst(Constants.CREDIT_CARD_NAME);
		String cardNumber = info.getFirst(Constants.CREDIT_CARD_NUMBER);
		String cardExpiryDate = info.getFirst(Constants.CREDIT_CARD_EXPIRY_DATE);
		String cardType = info.getFirst(Constants.CREDIT_CARD_TYPE);
		String cardCvvNumber = info.getFirst(Constants.CREDIT_CARD_CVV_NUMBER);
		String listSize = info.getFirst(Constants.ORDERED_LIST_SIZE);
		
		lstSize = Integer.parseInt(listSize);	
		try {
		      creditCardNumber = Long.parseLong(cardNumber);
		} catch (NumberFormatException e) {
			return customerToken = new CustomerToken(false, Constants.INVALID_CREDIT_CARD_FORMAT_NUMBER);
		}
		int k = 15;
		long[] cardNumArray = new long[16];
		while(creditCardNumber > 0) {
		    cardNumArray[k] = creditCardNumber % 10 ;
		    creditCardNumber = creditCardNumber / 10;
		    k -= 1;
		}
		/* VALIDATING CREDIT CARD NUMBER */
		boolean validCreditCardNumber = checkCreditCardNumber(cardNumArray);
		if(validCreditCardNumber == true){
			CustomerAccount customerAccount = null;
			CustomerOrders customerOrder = null;
			Customer customer = null;
			customerAccount = HibernateDBCustomerAccountManager.getDefault().getCustomerAccountByUsername(userName);
			if(customerAccount != null){
				String firstName = customerAccount.getFirstname();
				String lastName = customerAccount.getLastname();
				String address = customerAccount.getAddress();
				String city = customerAccount.getCity();
				String country = customerAccount.getCountry();
				String areaCode = customerAccount.getAreacode();
				String email = customerAccount.getEmail();
				String phoneNum = customerAccount.getPhonenumber();
				String username = customerAccount.getUsername();
				String customerId = customerAccount.getCustomerid();
				customer = new Customer(firstName, lastName, address, city, country, areaCode, email,
						phoneNum, username, customerId, 0);
				HibernateDBCustomerManager.getDefault().add(customer);
			}
			for(int i = 0; i < lstSize; i++){
	            String str = String.valueOf(i);
	            productType = info.getFirst(Constants.PRODUCT_PRODUCT_TYPE + str);
	            productGeneralName = info.getFirst(Constants.PRODUCT_GENERAL_NAME + str);
	            productSpecificName = info.getFirst(Constants.PRODUCT_SPECIFIC_NAME + str);
	            productNumber = info.getFirst(Constants.PRODUCT_NUMBER + str);
	            productQuantity = info.getFirst(Constants.PRODUCT_QUANTITY_ORDERED + str);
	            productPrice = info.getFirst(Constants.PRODUCT_PRICE + str);
	            
	            int id = 0;
	    		CustomerOrders order = null;
	    		order = HibernateDBCustomerOrdersManager.getDefault().getCustomerOrderWithHighestId();
	    		if(order == null){
	    			id = 101;
	    		}
	    		else{
	    			id = Integer.parseInt(order.getOrdernumber());
	    			id = id + 1;
	    		}
	            customerOrder = new CustomerOrders(String.valueOf(id), productType, productGeneralName, productSpecificName,
	            		productNumber, productQuantity, productPrice, 0);
	            
	            customer.addCustomerOrders(customerOrder);
	            customerOrder.setCustomer(customer);
	            HibernateDBCustomerOrdersManager.getDefault().add(customerOrder);          
	            HibernateDBCustomerManager.getDefault().update(customer);
	        }	
			customerToken = new CustomerToken(true, Constants.TRANSACTION_SUCCESSFUL);
		}
		else {
			customerToken = new CustomerToken(false, Constants.INVALID_CREDIT_CARD_NUMBER);
		}
		return customerToken;
	}
	
	/* Luhn Algorithm */
	public boolean checkCreditCardNumber(long[] digits){
		int sum = 0;
	      int length = digits.length;
	      for(int i = 0; i < length; i++){
	    	  int digit = (int) digits[length - i - 1];
	          if(i % 2 == 1){
	        	  digit *= 2;
	          }
	          sum += digit > 9 ? digit - 9 : digit;
	      }
	      return sum % 10 == 0;
	}
}
