package org.admin.pkg.ShoppingPointServer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.admin.pkg.ShoppingPointServer.models.ConsumerItem;
import org.admin.pkg.ShoppingPointServer.models.Customer;
import org.admin.pkg.ShoppingPointServer.models.CustomerOrders;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBConsumerItemManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerManager;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBCustomerOrdersManager;
import org.glassfish.jersey.server.mvc.Viewable;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

@Path("productmanagement")
public class ShoppingPointManager {
	
	@GET
    @Produces(MediaType.TEXT_HTML)
    public Response selectProductItem() {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("productItem", "yesitems");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
    }
	
	@GET
	@Path("/additem")
    @Produces(MediaType.TEXT_HTML)
    public Response addItemRedirect() {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "mustsignin");
    	return Response.ok(new Viewable("/login", map)).build();
    }
	
	@GET
	@Path("/addproduct")
    @Produces(MediaType.TEXT_HTML)
    public Response addProduct(){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("task", "additem");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@GET
	@Path("/deleteproduct")
    @Produces(MediaType.TEXT_HTML)
    public Response deleteProduct(){		
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("task", "deleteitem");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}

	@GET
	@Path("/updateproduct")
    @Produces(MediaType.TEXT_HTML)
    public Response updateProduct(){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("task", "updateitem");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@GET
	@Path("/viewitems")
    @Produces(MediaType.TEXT_HTML)
    public Response viewItems(){	
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("task", "viewproducts");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@GET
	@Path("/vieworders")
    @Produces(MediaType.TEXT_HTML)
    public Response viewCustomerOrders(){
		Map<String, Object> map = new HashMap<String, Object>();
    	map.put("task", "vieworders");
    	Customer customer;
		CustomerOrders customerOrder;
		List<CustomerOrders> ordersList;
		ordersList = HibernateDBCustomerOrdersManager.getDefault().getAllCustomerOrders();
		if(ordersList == null){
			map.put("customer", "nocustomers");
		}
		else if(ordersList != null){
			map.put("customer", "yescustomers");
			map.put("customerorderslist", ordersList);
		}	
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@GET
	@Path("/viewstats")
    @Produces(MediaType.TEXT_HTML)
    public Response viewStatistic(){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("task", "viewstats");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/additem")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response addProductItem(@FormParam("category") String categoryItem, @FormParam("productname") String productName){
		List<String> list = new ArrayList<String>();
		List<String> selectedItemList = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		File folder = new File("F:/temp/Workspace/ShoppingPointServer/src/main/webapp/images"); // path must be exact
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < listOfFiles.length; i++){
			if(listOfFiles[i].isFile()){
				String fileName = listOfFiles[i].getName();
				if(fileName.contains(categoryItem)){
					selectedItemList.add(fileName);
				}
			}
			else if(listOfFiles[i].isDirectory()){
		    }
		}
		for(int i = 0; i < selectedItemList.size(); i++){
			String fileName = selectedItemList.get(i);
			if(fileName.contains(productName) && !productName.equalsIgnoreCase("")){
				list.add(fileName);
			}
		}
		if(list.size() != 0){
			map.put("task", "additem");
			map.put("items", list);
	        map.put("productItem", "yesitems");
	        map.put("itemCategory", categoryItem);
	        map.put("itemName", productName);
		}
		else if(list.size() == 0){
			map.put("task", "additem");
			map.put("items", list);
	        map.put("productItem", "noitems");
	        map.put("itemCategory", categoryItem);
	        map.put("itemName", productName);
		}
        return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/deleteitem")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response deleteProduct(@FormParam("deleteCategory") String itemCategory, @FormParam("deleteItemName") String productName,
    		@FormParam("deleteItemNumber") String productNumber){
		boolean deleteItem = false;
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	ConsumerItem consumerItem = null;
		consumerItem = HibernateDBConsumerItemManager.getDefault().getConsumerItemWithSpecificCategoryNameNumber(itemCategory,
				productName, productNumber);
		if(consumerItem == null){
			map.put("task", "deleteitem");
			map.put("taskDelete", "itemnotexists");
			return Response.ok(new Viewable("/shoppingpoint", map)).build();
		}
		else if (consumerItem != null){
			deleteItem = HibernateDBConsumerItemManager.getDefault().deleteItemByCategoryNameNumber(itemCategory, productName, productNumber);
		}
		if(deleteItem == true){
			map.put("taskDelete", "deletesuccess");
		}
		else {
			map.put("taskDelete", "deletefailure");
		}
		map.put("task", "deleteitem");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/checkitem")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
	public Response checkItemExists(@FormParam("updateCategory") String itemCategory, @FormParam("itemNameUpdate") String itemName,
    		@FormParam("itemNumberUpdate") String itemNumber) throws IOException{
		
		Map<String, Object> map = new HashMap<String, Object>();
		ConsumerItem consumerItem = null;
		consumerItem = HibernateDBConsumerItemManager.getDefault().getConsumerItemWithSpecificCategoryNameNumber(itemCategory, itemName, itemNumber);
		
		if(consumerItem == null){
			map.put("task", "itemnotfound");
	    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
		}
		else if(consumerItem != null){
			map.put("task", "itemfound");
			map.put("itemCategory", consumerItem.getCategory());
			map.put("itemType", consumerItem.getProducttype());
			map.put("itemGeneralName", consumerItem.getGeneralname());
	        map.put("itemName", consumerItem.getSpecificname());
	        map.put("itemNumber", consumerItem.getProductnumber());
	        map.put("itemLongDescr", consumerItem.getLongdescription());
	        map.put("itemShortDescr", consumerItem.getShortdescription());
	        map.put("itemQuantity", consumerItem.getQuantity());
	        map.put("itemPrice", consumerItem.getPrice());
	        map.put("itemImage", consumerItem.getImagename());
		}
		String itemGeneralName = consumerItem.getGeneralname();
		List<String> list = new ArrayList<String>();
		List<String> selectedItemList = new ArrayList<String>();
		/* CHANGE THIS TO WHAT EVER PROJECT PATH YOU CURRENTLY HAVE */
		File folder = new File("F:/temp/Workspace/ShoppingPointServer/src/main/webapp/images"); // path must be exact
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < listOfFiles.length; i++){
			if(listOfFiles[i].isFile()){
				String fileName = listOfFiles[i].getName();
				if(fileName.contains(itemCategory)){
					selectedItemList.add(fileName);
				}
			}
			else if(listOfFiles[i].isDirectory()){
		    }
		}
		for(int i = 0; i < selectedItemList.size(); i++){
			String fileName = selectedItemList.get(i);
			if(fileName.contains(itemGeneralName)){
				list.add(fileName);
			}
		}
		if(list.size() != 0){
			map.put("items", list);
	        map.put("productItem", "yesitems");
		}
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/updateitem")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
	public Response updateItem(@FormParam("foundUpdateCategoryMenu") String newItemCategory, @FormParam("itemTypeUpdate") String newItemType,
			@FormParam("itemGeneralNameUpdate") String generalName,
			@FormParam("itemNameUpdate") String specificName, @FormParam("itemNumberUpdate") String productNum,
    		@FormParam("itemDescrUpdate") String description, @FormParam("itemDetailUpdate") String detail,
    		@FormParam("itemQuantityUpdate") String quantity, @FormParam("itemPriceUpdate") String price,
    		@FormParam("updateItemName") String prevSpecificName,@FormParam("updateItemCategory") String prevItemCategory,
    		@FormParam("updateItemNumber") String prevItemNumber, @FormParam("updateItemImage") String imageName) throws IOException{	
		boolean itemUpdate = false;
		Map<String, Object> map = new HashMap<String, Object>();
				
		ConsumerItem consumerItem = null;
		consumerItem = HibernateDBConsumerItemManager.getDefault().getConsumerItemWithSpecificCategoryNameNumber(prevItemCategory,
				prevSpecificName, prevItemNumber);
		if(consumerItem == null){
			map.put("task", "itemnotfound");
	    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
		}
		else if(consumerItem != null){
			if(newItemCategory.equalsIgnoreCase("appliances")){
				consumerItem.setCategory(prevItemCategory);
			}
			else{
				consumerItem.setCategory(newItemCategory);
			}
			consumerItem.setProducttype(newItemType);
			consumerItem.setGeneralname(generalName);
			consumerItem.setSpecificname(specificName);
			consumerItem.setProductnumber(productNum);
			consumerItem.setShortdescription(description);
			consumerItem.setLongdescription(detail);
			consumerItem.setQuantity(quantity);
			consumerItem.setPrice(price);
			String prevImageName = consumerItem.getImagename();
			if(!prevImageName.equalsIgnoreCase(imageName)){
				consumerItem.setImagename(imageName);
				String newItemImage = convertImageFileToBase64(imageName);
				consumerItem.setProductimage(newItemImage);
			}
			itemUpdate = HibernateDBConsumerItemManager.getDefault().update(consumerItem);
		}
		if(itemUpdate == true){
			map.put("taskUpdate", "updatesuccess");
		}
		else {
			map.put("taskUpdate", "updatefailure");
		}
    	map.put("task", "updateitem");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/viewitemsbycategory")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response viewItemsByCategoryAndType(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("task", "viewproducts");
		map.put("viewtask", "viewitemsbycategorytype");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/viewitem")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response viewSpecificItem(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("task", "viewproducts");
		map.put("viewtask", "viewsingleitem");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/viewitemlist")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response viewProducts(@FormParam("viewItemCategoryMenu") String itemCategory,
    		@FormParam("viewItemsType") String itemType){
		Map<String, Object> map = new HashMap<String, Object>();
		List<ConsumerItem> itemList = null;
		ConsumerItem productItem = null;
		itemList = HibernateDBConsumerItemManager.getDefault().getItemsByCategoryAndType(itemCategory, itemType);
		
		if(itemList == null){
			map.put("task", "viewproducts");
			map.put("viewtask", "viewitemsbycategorytype");
			map.put("viewtasklist", "noitemsfound");
	    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
		}
		if(itemList != null){
			if(itemList.size() == 0){
				map.put("task", "viewproducts");
				map.put("viewtask", "viewitemsbycategorytype");
				map.put("viewtasklist", "noitemsfound");
		    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
			}
		}
		map.put("items", itemList);
		map.put("task", "viewproducts");
		map.put("viewtask", "viewitemsbycategorytype");
		map.put("viewtasklist", "itemsfound");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	@POST
	@Path("/viewsingleitem")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response viewSingleProduct(@FormParam("viewItemCategoryMenu") String itemCategory,
    		@FormParam("viewItemType") String itemType, @FormParam("viewItemSpecificName") String itemName,
    		@FormParam("viewItemNumber") String itemNumber){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		ConsumerItem productItem = null;
		productItem = HibernateDBConsumerItemManager.getDefault().getItemByCategoryTypeNameNumber(itemCategory,
				itemType, itemName, itemNumber);
		
		if(productItem == null){
			map.put("task", "viewproducts");
			map.put("viewtask", "viewsingleitem");
			map.put("viewtasksingleitem", "noitemfound");
	    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
		}
		
		map.put("category", productItem.getCategory());
		map.put("type", productItem.getProducttype());
		map.put("genname", productItem.getGeneralname());
		map.put("specname", productItem.getSpecificname());
		map.put("itemnum", productItem.getProductnumber());
		map.put("shortdescr", productItem.getShortdescription());
		map.put("longdescr", productItem.getLongdescription());
		map.put("quantity", productItem.getQuantity());
		map.put("price", productItem.getPrice());
		map.put("image", productItem.getProductimage());
		
		map.put("task", "viewproducts");
		map.put("viewtask", "viewsingleitem");
		map.put("viewtasksingleitem", "itemfound");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	public String convertImageFileToBase64(String image) throws IOException{
		String base64Str = "";
		String[] strArray = image.split("\\.");
		String fileExtension = strArray[1];
		/* CHANGE THIS FILE PATH TO WHAT EVER PROJECT PATH YOU CURRENTLY HAVE */
		String directoryName = "F:/temp/Workspace/ShoppingPointServer/src/main/webapp/images";
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		BufferedImage img = ImageIO.read(new File(directoryName, image));
		ImageIO.write(img, fileExtension, baos);
		base64Str = Base64.encode(baos.toByteArray());
		baos.close();
		return base64Str;
	}
	
	public BufferedImage getImageFromDB(String base64Str) throws IOException{
		byte[] bytearray = Base64.decode(base64Str);
		BufferedImage imag = ImageIO.read(new ByteArrayInputStream(bytearray));
		return imag;
	}
}
