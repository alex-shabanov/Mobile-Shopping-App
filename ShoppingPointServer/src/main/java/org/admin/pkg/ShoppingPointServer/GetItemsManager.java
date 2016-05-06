package org.admin.pkg.ShoppingPointServer;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
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

import org.admin.pkg.ShoppingPointServer.models.ConsumerItem;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBConsumerItemManager;
import org.json.JSONArray;

@Path("getitems")
public class GetItemsManager {

	// The @Context annotation allows us to have certain contextual objects
    // injected into this class.
    // UriInfo object allows us to get URI information.
    @Context
    UriInfo uriInfo;
 
    // Another "injected" object. This allows us to use the information that's
    // part of any incoming request.
    // We could, for example, get header information, or the requestor's address.
    @Context
    Request request;
    
	@GET
    @Path("getitembyid")
    @Produces(MediaType.APPLICATION_JSON)
    public ConsumerItem deleteSamplePersonByID(@Context UriInfo info) {

		String productName = uriInfo.getQueryParameters().getFirst("itemname");
		ConsumerItem productItem = null;
		productItem = HibernateDBConsumerItemManager.getDefault().getConsumerItemWithSpecificName(productName);
		if(productItem == null){
			productItem = new ConsumerItem("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", 100);
		}
		return productItem;
	}
	
	@GET
    @Path("getitembycategorytype")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConsumerItem> getItemsByCategoryAndType(@Context UriInfo info) {
		
		System.out.println("GET PRODUCT ITEMS");
		
		JSONArray jsonArray = new JSONArray();
		String productCategory = uriInfo.getQueryParameters().getFirst("itemcategory");
		String productType = uriInfo.getQueryParameters().getFirst("itemtype");
		
		ConsumerItem productItem = null;
		List<ConsumerItem> itemsList = null;
		itemsList = HibernateDBConsumerItemManager.getDefault().getItemsByCategoryAndType(productCategory, productType);
		if(itemsList != null){
			for(int i = 0; i < itemsList.size(); i++){
				productItem = itemsList.get(i);
				jsonArray.put(productItem);
			}
			return itemsList;
		}
		else {
			itemsList = new ArrayList<ConsumerItem>();
			productItem = new ConsumerItem("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", 100);
			itemsList.add(productItem);
		}
		return itemsList;
	}
	
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public ConsumerItem postPerson(MultivaluedMap<String, String> personParams) {
		ConsumerItem item = null;
		return item;
	}
}
