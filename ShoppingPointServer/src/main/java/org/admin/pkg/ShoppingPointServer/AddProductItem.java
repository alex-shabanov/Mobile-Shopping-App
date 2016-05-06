package org.admin.pkg.ShoppingPointServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.ws.rs.core.Response;

import org.admin.pkg.ShoppingPointServer.models.ConsumerItem;
import org.admin.pkg.ShoppingPointServer.persistence.HibernateDBConsumerItemManager;
import org.glassfish.jersey.server.mvc.Viewable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
 


@Path("addproductitem")
public class AddProductItem {
	
	@GET
	@Path("/addconsumeritem")
    @Produces(MediaType.TEXT_HTML)
    public Response addConsumerItemRedirect() {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "mustsignin");
    	return Response.ok(new Viewable("/login", map)).build();
    }
	
	@POST
	@Path("/addconsumeritem")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response productAddItem(@FormParam("productType") String productType, @FormParam("specificName") String specificName,
    		@FormParam("itemNumber") String productNum, @FormParam("itemDescr") String description, @FormParam("itemDetail") String detail,
    		@FormParam("itemQuantity") String quantity, @FormParam("itemPrice") String price,
    		@FormParam("itemName") String generalName,@FormParam("itemCategory") String productCategory,
    		@FormParam("productImage") String imageName) throws IOException{
		
		boolean addItem;
		Map<String, Object> map = new HashMap<String, Object>();
		if(imageName.equalsIgnoreCase("abc")){
	    	map.put("additem", "imageselect");
	    	map.put("task", "additem");
	    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
		}
		
		String imageStr = convertImageFileToBase64(imageName);
		ConsumerItem productItem = new ConsumerItem(productCategory, productType, generalName, specificName, productNum, description,
				detail, quantity, price, imageName, imageStr, 0);
		addItem = HibernateDBConsumerItemManager.getDefault().add(productItem);
		
		if(addItem == true){
			map.put("additem", "success");
		}
		else if(addItem == false){
			map.put("additem", "failure");
		}
    	map.put("task", "additem");
    	map.put("productItem", "noitems");
    	return Response.ok(new Viewable("/shoppingpoint", map)).build();
	}
	
	public String convertImageFileToBase64(String image) throws IOException{
		String base64Str = "";
		String[] strArray = image.split("\\.");
		String fileExtension = strArray[1];
		/*for(int i = 0; i < strArray.length; i++){
			System.out.println("strArray[" + String.valueOf(i) + "] = " + strArray[i]);
		}*/
		String directoryName = "F:/temp/Workspace/ShoppingPointServer/src/main/webapp/images";
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		BufferedImage img = ImageIO.read(new File(directoryName, image));
		ImageIO.write(img, fileExtension, baos);
 
		base64Str = Base64.encode(baos.toByteArray());
		baos.close();
		return base64Str;
		
		//System.out.println("BASE64 IMAGE STRING: " + base64Str);
		/*byte[] bytearray = Base64.decode(base64Str);
		BufferedImage imag = ImageIO.read(new ByteArrayInputStream(bytearray));
		ImageIO.write(imag, fileExtension, new File(directoryName,"newimage." + fileExtension));*/
	}
}
