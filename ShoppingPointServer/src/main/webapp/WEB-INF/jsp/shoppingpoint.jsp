<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
     <%@page import='java.util.LinkedList' %>
     <%@page import='java.util.ArrayList' %>
     <%@page import='java.util.Set' %>
     <%@page import='org.admin.pkg.ShoppingPointServer.models.ConsumerItem' %>
     <%@page import='org.admin.pkg.ShoppingPointServer.models.CustomerOrders' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Shopping Point</title>
	</head>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles4.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/updatestyles.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewitemstyle.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
	
	<body>		
		<div id="nav" class = "navbar navbar-inverse navbar-static-top">
			<a href="#" class="navbar-brand">Shopping Point</a>
		</div>
		<div id="column2">
			<p><b><a href="/ShoppingPointServer/webapi/productmanagement/addproduct" id="addItemLink">Add Product Item</a></b></p>
			<p><b><a href="/ShoppingPointServer/webapi/productmanagement/deleteproduct" id="deleteItemLink">Delete Product Item</a></b></p>
			<p><b><a href="/ShoppingPointServer/webapi/productmanagement/updateproduct" id="updateItemLink">Update Product Item</a></b></p>
			<p><b><a href="/ShoppingPointServer/webapi/productmanagement/viewitems" id="viewItemsLink">View Products</a></b></p>
			<p><b><a href="/ShoppingPointServer/webapi/productmanagement/vieworders" id="viewOrdersLink">View Customer Orders</a></b></p>
			<p><b><a href="/ShoppingPointServer/webapi/productmanagement/viewstats" id="viewStatsLink">View Stats</a></b></p>
			<a href="/ShoppingPointServer/webapi/signout" id="signoutLink"><input type="submit" value="Sign Out" id="signoutBtn" name="submit"></a>
		</div>
			
		<div id="column3">
			<%
				if(request.getHeader("Referer") == null){
					response.sendRedirect("http://localhost:8080/ShoppingPointServer/webapi/login");
				}
			   int timeout = session.getMaxInactiveInterval();
			   response.setHeader("Refresh", timeout + "; URL = /ShoppingPointServer/webapi/signout/timeout");
			%>
			<c:choose>
	   			<c:when test="${it.status == 'passwordok'}">
					<c:set var="pswd" value="${it.password}"/>
					<c:set var="name" value="${it.username}"/>
					<%
					    session = request.getSession();
						session.setMaxInactiveInterval(5 * 60 * 1000);
						String username = "none";
						String userpassword= (String) pageContext.getAttribute("Password", PageContext.SESSION_SCOPE);
						if(userpassword == null) {
							pageContext.setAttribute("Username", (String)pageContext.getAttribute("name"), PageContext.SESSION_SCOPE);
							pageContext.setAttribute("Password", (String)pageContext.getAttribute("pswd"), PageContext.SESSION_SCOPE);
						}
						else if(userpassword != null) {
							response.sendRedirect("/ShoppingPointServer/webapi/signout/sameadmin");
						}
					%>
					</c:when>
				<c:otherwise>
				</c:otherwise>
	    	</c:choose>
	    	
			<c:choose>
  				<c:when test="${it.additem == 'success'}">
    				<div id="alertSuccess" class="alert alert-success">
      					<strong>Success!</strong> Product item is added to the database.
    				</div>
 				 </c:when>
  				<c:when test="${it.additem == 'failure'}">
  					<div id="alertWarning" class="alert alert-warning">
  						<strong>Warning!</strong> Product item already exists in the database.
  					</div>
  				</c:when>
  				<c:when test="${it.additem == 'imageselect'}">
  					<div id="alertInfo" class="alert alert-info">
  						<strong>Warning!</strong> Product image must be selected.
  					</div>
  				</c:when>
 				<c:otherwise>
  				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${it.task == 'additem'}">
					<form id="selectItemForm" name="selectItemForm" action="/ShoppingPointServer/webapi/productmanagement/additem" method="POST">
						<table border="0">
							<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
							<tr>
								<td><p id="addItemCategoryText">Select Product Category</p></td>
							</tr>
							<tr>
							<td>
								<select id="dropDownMenu" name="category">
  								<option value="appliances">Appliances</option>
  								<option value="food">Groceries</option>
  								<option value="clothes">Clothes</option>
  		      					<option value="audio">Audio</option>
								</select>
							</td>
							</tr>
							<tr>
								<td><p id="addGeneralNameText">General Product Name</p></td>
								<td><input type="text" name="productname" placeholder="Enter Product Name"  id="productname" size="50" /></td>
								<td><input type="submit" value="Select Product Image" id="choosePicBtn" name="submit" /></td>
							</tr>
						</table>
					</form>
					<div>
					<% ArrayList<String> list= new ArrayList<String>(); %>
   	    				<c:choose>
							<c:when test="${it.productItem == 'yesitems'}">
								<ul class="galary">
				   					<c:forEach var="item" items="${it.items}">
  										<li>
  											<img id='${item}' onclick="changeImage('${item}')" src="${pageContext.request.contextPath}/images/${item}" width="140" height="140" alt="Image" />
  										</li>
  										<c:set var="lname" value="${item}"/>
  										<% list.add((String)pageContext.getAttribute("lname")); %>
    								</c:forEach>
  								</ul>
							</c:when>
    						<c:otherwise>
   							</c:otherwise>
		    			</c:choose>	    			
		    			<c:choose>
		    				<c:when test="${it.itemCategory == 'appliances'}">
		    					<c:set var="productCategory" value="${it.itemCategory}"/>
								<c:set var="productName" value="${it.itemName}"/>
								<% if(list.size() != 0){
								        list.add((String)pageContext.getAttribute("productCategory"));
  								        list.add((String)pageContext.getAttribute("productName"));
								      }
  								%>
							</c:when>
							<c:when test="${it.itemCategory == 'food'}">
								<c:set var="productCategory" value="${it.itemCategory}"/>
								<c:set var="productName" value="${it.itemName}"/>
								<% if(list.size() != 0){
								        list.add((String)pageContext.getAttribute("productCategory"));
  								        list.add((String)pageContext.getAttribute("productName"));
								      }
  								%>
							</c:when>
							<c:when test="${it.itemCategory == 'clothes'}">
								<c:set var="productCategory" value="${it.itemCategory}"/>
								<c:set var="productName" value="${it.itemName}"/>
								<% if(list.size() != 0){
								        list.add((String)pageContext.getAttribute("productCategory"));
  								        list.add((String)pageContext.getAttribute("productName"));
								      }
  								%>
							</c:when>
							<c:when test="${it.itemCategory == 'audio'}">
								<c:set var="productCategory" value="${it.itemCategory}"/>
								<c:set var="productName" value="${it.itemName}"/>
								<% if(list.size() != 0){
								        list.add((String)pageContext.getAttribute("productCategory"));
  								        list.add((String)pageContext.getAttribute("productName"));
								      }
  								%>
							</c:when>
							<c:otherwise>
   							</c:otherwise>
		    			</c:choose>
  					</div>
					<!-- % 
  						for(int i = 0; i < list.size(); i++){
  							out.println(list.get(i));
  						}
  					% -->
  					<div class="imageWrapper" id="picture">
 						<img id="myImage" src="${pageContext.request.contextPath}/images/penguin.png" width="140" height="140" title="title1"/>
 					</div>
  					<script>
  						function changeImage(value) {
  		    				var image = document.getElementById(value);
  		    				var text = "";
               				var lst = '<%= list %>';
                			var array = new Array();
                			for(var i = 0; i < lst.length; i++){
               	  				text += lst[i];
                			}
                			var a = text.match(/\[(.*?)\]/);  // this removes the square brackets
                			var str = a[1];
                			var array = str.split(', ');
                			for(var i = 0; i < array.length - 2; i++){
            	  				var arrayItem = array[i];
            	  				if (image.src.match(arrayItem)) {
            		  				var image2 = document.getElementById('myImage');
            		  				var inputField = document.getElementById('productImage');
            		  				image2.src = "${pageContext.request.contextPath}/images/" + arrayItem;
            	      				//image2.style.visibility = 'visible';
            	     	 			image2.title = arrayItem;
            	      				inputField.value = arrayItem;
            		  				document.getElementById('myImage').style.visibility='visible';
      		      				}
               				}
                			var categoryInputField = document.getElementById('itemCategory');
                			var nameInputField = document.getElementById('itemName');
                			categoryInputField.value = array[array.length - 2];
                			nameInputField.value = array[array.length - 1];
  						}
  					</script>	
					<form id="addItemForm" name="addItemForm" action="/ShoppingPointServer/webapi/addproductitem/addconsumeritem" method="POST">
						<table border="0">
							<tr>
								<td><p id="addItemProductTypeNameText">Product Type</p></td>
								<td><input type="text" name="productType" placeholder="Enter Product Type" value="" id="productType" size="50" required  /></td>
							</tr>
							<tr>
								<td><p id="addItemSpecificNameText">Specific Product Name</p></td>
								<td><input type="text" name="specificName" placeholder="Enter Specific Product Name" value="" id="specificName" size="50" required  /></td>
							</tr>
							<tr>
								<td><p id="addItemProductNameText">Product Number</p></td>
								<td><input type="text" name="itemNumber" placeholder="Enter Product Number" value="" id="itemNumber" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="addItemProductDescrText">Product Description</p></td>
								<td><input type="text" name="itemDescr" placeholder="Enter Product Description" value="" id="itemDescr" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="addItemProductDetailText">Product Details</p></td>
								<td><input type="text" name="itemDetail" placeholder="Enter Product Details" value="" id="itemDetail" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="addItemProductQuantityText">Product Quantity</p></td>
								<td><input type="text" name="itemQuantity" placeholder="Enter Product Quantity" value="" id="itemQuantity" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="addItemProductPriceText">Product Price</p></td>
								<td><input type="text" name="itemPrice" placeholder="Enter Product Price" value="" id="itemPrice" size="50" required /></td>
								<td><input type="hidden" name="productImage" value="abc" id="productImage"/></td>
								<td><input type="hidden" name="itemName"  value="abcd" id="itemName"/></td>
								<td><input type="hidden" name="itemCategory"  value="none" id="itemCategory"/></td>
								<td><input type="hidden" name="itemImageName"  value="none" id="itemImageName"/></td>
								<td><input type="submit" value="Add Item to Database" id="addItemBtn" name="submit" /></td>
							</tr>
						</table>
					</form>	
				</c:when >
				<c:when test="${it.task == 'deleteitem'}">
					<c:choose>
						<c:when test="${it.taskDelete == 'deletesuccess'}">
							<div id="alertSuccess" class="alert alert-success">
      							<strong>Success!</strong> Product item deleted successfully.
    						</div>
						</c:when>
						<c:when test="${it.taskDelete == 'deletefailure'}">
							<div id="alertWarning" class="alert alert-warning">
  								<strong>Info!</strong> Product hasn't been deleted.
  							</div>
						</c:when>
						<c:when test="${it.taskDelete == 'itemnotexists'}">
							<div id="alertWarning" class="alert alert-warning">
  								<strong>Info!</strong> Product doesn't exist.
  							</div>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
					<form id="deleteItemForm" name="deleteItemForm" action="/ShoppingPointServer/webapi/productmanagement/deleteitem" method="POST">
						<table border="0">
							<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
							<tr>
								<td><p id="deltemCategoryText">Select Product Category</p></td>
							</tr>
							<tr>
							<td>
								<select id="deleteCategoryMenu" name="deleteCategory">
  								<option value="appliances">Appliances</option>
  								<option value="food">Groceries</option>
  								<option value="clothes">Clothes</option>
  		      					<option value="audio">Audio</option>
								</select>
							</td>
							</tr>
							<tr>
								<td><p id="delItemNameText">Specific Product Name</p></td>
								<td><input type="text" name="deleteItemName" placeholder="Enter Product Name" value="" id="deleteItemName" size="50" required  /></td>
							</tr>
							<tr>
								<td><p id="delItemNumberText">Specific Product Number</p></td>
								<td><input type="text" name="deleteItemNumber" placeholder="Enter Product Number" value="" id="deleteItemNumber" size="50" required /></td>
							</tr>
							<tr><td><input type="submit" value="Delete Item From Database" id="deleteItemBtn" name="submit" /></td></tr>
						</table>
					</form>	
				</c:when>
				<c:when test="${it.task == 'updateitem'}">
					<c:choose>
						<c:when test="${it.taskUpdate == 'updatesuccess'}">
							<div id="alertSuccess" class="alert alert-success">
      							<strong>Success!</strong> Product item updated successfully.
    						</div>
						</c:when>
						<c:when test="${it.taskUpdate == 'updatefailure'}">
						<div id="alertWarning" class="alert alert-warning">
  								<strong>Warning!</strong> Product hasn't been updated.
  							</div>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
					<form id="updateItemForm" name="updateItemForm" action="/ShoppingPointServer/webapi/productmanagement/checkitem" method="POST">
						<table border="0">
							<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
							<tr>
								<td><p id="updateCategoryText">Select Product Category</p></td>
							</tr>
							<tr>
							<td>
								<select id="updateCategoryMenu" name="updateCategory">
  								<option value="appliances">Appliances</option>
  								<option value="food">Groceries</option>
  								<option value="clothes">Clothes</option>
  		      					<option value="audio">Audio</option>
								</select>
							</td>
							</tr>
							<tr>
								<td><p id="updateItemNameText">Enter Product Name</p></td>
								<td><input type="text" name="itemNameUpdate" placeholder="Enter Product Name" value="" id="itemNameUpdate" size="50" required  /></td>
							</tr>
							<tr>
								<td><p id="updateItemNumberText">Enter Product Number</p></td>
								<td><input type="text" name="itemNumberUpdate" placeholder="Enter Product Number" value="" id="itemNumberUpdate" size="50" required /></td>
							</tr>
							<tr>
								<td><input type="submit" value="View Product Item" id="viewItemBtn" name="submit" /></td>
							</tr>
						</table>
					</form>
				</c:when>
				<c:when test="${it.task == 'itemnotfound'}">
					<div id="itemNotFoundWarning" class="alert alert-warning">
  						<strong>Warning!</strong> Product item doesn't exist.
  					</div>
					<form id="updateItemForm" name="updateItemForm" action="/ShoppingPointServer/webapi/productmanagement/checkitem" method="POST">
						<table border="0">
							<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
							<tr>
								<td><p id="updateCategoryText">Select Product Category</p></td>
							</tr>
							<tr>
							<td>
								<select id="updateCategoryMenu" name="updateCategory">
  								<option value="appliances">Appliances</option>
  								<option value="food">Groceries</option>
  								<option value="clothes">Clothes</option>
  		      					<option value="audio">Audio</option>
								</select>
							</td>
							</tr>
							<tr>
								<td><p id="updateItemNameText">Enter Product Name</p></td>
								<td><input type="text" name="itemNameUpdate" placeholder="Enter Product Name" value="" id="itemNameUpdate" size="50" required  /></td>
							</tr>
							<tr>
								<td><p id="updateItemNumberText">Enter Product Number</p></td>
								<td><input type="text" name="itemNumberUpdate" placeholder="Enter Product Number" value="" id="itemNumberUpdate" size="50" required /></td>
							</tr>
							<tr>
								<td><input type="submit" value="View Product Item" id="viewItemBtn" name="submit" /></td>
							</tr>
						</table>
					</form>
				</c:when>
				<c:when test="${it.task == 'itemfound'}">
					<div>
					<% ArrayList<String> list= new ArrayList<String>(); %>
   	    				<c:choose>
							<c:when test="${it.productItem == 'yesitems'}">
								<ul class="updateGalary">
				   					<c:forEach var="item" items="${it.items}">
  										<li>
  											<img id='${item}' onclick="updateImage('${item}')" src="${pageContext.request.contextPath}/images/${item}" width="140" height="140" alt="Image" />
  										</li>
  										<c:set var="lname" value="${item}"/>
  											<% list.add((String)pageContext.getAttribute("lname")); %>
    								</c:forEach>
  								</ul>
							</c:when>
    						<c:otherwise>
   							</c:otherwise>
		    			</c:choose>	    			
		    			<c:choose>
		    				<c:when test="${it.itemCategory == 'appliances'}">
		    					<c:set var="productCategory" value="${it.itemCategory}"/>
								<c:set var="productName" value="${it.itemName}"/>
								<c:set var="productNumber" value="${it.itemNumber}"/>
								<% if(list.size() != 0){
								        list.add((String)pageContext.getAttribute("productCategory"));
  								        list.add((String)pageContext.getAttribute("productName"));
  								        list.add((String)pageContext.getAttribute("productNumber"));
								      }
  								%>
							</c:when>
							<c:when test="${it.itemCategory == 'food'}">
								<c:set var="productCategory" value="${it.itemCategory}"/>
								<c:set var="productName" value="${it.itemName}"/>
								<c:set var="productNumber" value="${it.itemNumber}"/>
								<% if(list.size() != 0){
								        list.add((String)pageContext.getAttribute("productCategory"));
  								        list.add((String)pageContext.getAttribute("productName"));
  								        list.add((String)pageContext.getAttribute("productNumber"));
								      }
  								%>
							</c:when>
							<c:when test="${it.itemCategory == 'clothes'}">
								<c:set var="productCategory" value="${it.itemCategory}"/>
								<c:set var="productName" value="${it.itemName}"/>
								<c:set var="productNumber" value="${it.itemNumber}"/>
								<% if(list.size() != 0){
								        list.add((String)pageContext.getAttribute("productCategory"));
  								        list.add((String)pageContext.getAttribute("productName"));
  								        list.add((String)pageContext.getAttribute("productNumber"));
								      }
  								%>
							</c:when>
							<c:otherwise>
   							</c:otherwise>
		    			</c:choose>
  					</div>
  					<div class="imageWrapper" id="currentPicture">
 						<img id="myCurrentImage" src="${pageContext.request.contextPath}/images/${it.itemImage}" title="title1"/><p id="currentImageText">Current Image</p>
 						<!-- img id="myCurrentImage" src="data:image/png;base64, ${it.itemImage }" width="140" height="140" alt="Image"/><p id="currentImageText">Current Image</p -->
 					</div>
					<form id="updateProductForm" name="updateProductForm" action="/ShoppingPointServer/webapi/productmanagement/updateitem" method="POST">
						<table border="0">
							<tr>
								<td><p id="updateCategoryText">Product Category</p></td>
							</tr>
							<tr>
							<td>
								<select id="foundUpdateCategoryMenu" name="foundUpdateCategoryMenu">
  								<option value="appliances">Appliances</option>
  								<option value="food">Groceries</option>
  								<option value="clothes">Clothes</option>
  		      					<option value="audio">Audio</option>
								</select>
							</td>
							</tr>
							<tr>
								<td><p id="updateItemTypeText">Product Type</p></td>
								<td><input type="text" name="itemTypeUpdate" placeholder="Enter Product Type" value="${it.itemType}" id="itemTypeUpdate" size="50" /></td>
							</tr>
							<tr>
								<td><p id="updateItemGeneralNameText">Product General Name</p></td>
								<td><input type="text" name="itemGeneralNameUpdate" placeholder="Enter Product Name" value="${it.itemGeneralName}" id="itemGeneralNameUpdate" size="50" /></td>
							</tr>
							<tr>
								<td><p id="updateItemNameText">Product Specific Name</p></td>
								<td><input type="text" name="itemNameUpdate" placeholder="Enter Product Name" value="${it.itemName}" id="itemNameUpdate" size="50" required  /></td>
							</tr>
							<tr>
								<td><p id="updateItemNumberText">Product Number</p></td>
								<td><input type="text" name="itemNumberUpdate" placeholder="Enter Product Number" value="${it.itemNumber}" id="itemNumberUpdate" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="updateProduceDescrText">Product Description</p></td>
								<td><input type="text" name="itemDescrUpdate" placeholder="Enter Product Description" value="${it.itemLongDescr}" id="itemDescrUpdate" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="updateProduceDetailsText">Product Details</p></td>
								<td><input type="text" name="itemDetailUpdate" placeholder="Enter Product Details" value="${it.itemShortDescr}" id="itemDetailUpdate" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="updateProductQuantityText">Product Quantity</p></td>
								<td><input type="text" name="itemQuantityUpdate" placeholder="Enter Product Quantity" value="${it.itemQuantity}" id="itemQuantityUpdate" size="50" required /></td>
							</tr>
							<tr>
								<td><p id ="updateProductPriceText">Product Price</p></td>
								<td><input type="text" name="itemPriceUpdate" placeholder="Enter Product Price" value="${it.itemPrice}" id="itemPriceUpdate" size="50" required /></td>
								<td><input type="hidden" name="updateItemCategory"  value="${it.itemCategory}" id="updateItemCategory"/></td>
								<td><input type="hidden" name="updateItemName"  value="${it.itemName}" id="updateItemName"/></td>
								<td><input type="hidden" name="updateItemNumber"  value="${it.itemNumber}" id="updateItemNumber"/></td>
								<td><input type="hidden" name="updateItemImage" value="${it.itemImage}" id="updateItemImage"/></td>
							<tr><td><input type="submit" value="Update Product Item" id="updateItemBtn" name="submit" /></td></tr>
						</table>
					</form>
 					<script>
  						function updateImage(value) {
  		    				var image = document.getElementById(value);
  		    				var text = "";
               				var lst = '<%= list %>';
                			var array = new Array();
                			for(var i = 0; i < lst.length; i++){ text += lst[i]; }
                			var a = text.match(/\[(.*?)\]/);  // this removes the square brackets
                			var str = a[1];
                			var array = str.split(', ');
                			for(var i = 0; i < array.length - 3; i++){
            	  				var arrayItem = array[i];
            	  				if (image.src.match(arrayItem)) {
            		  				var image2 = document.getElementById('myCurrentImage');
            		  				var inputField = document.getElementById('updateItemImage');
            		  				image2.src = "${pageContext.request.contextPath}/images/" + arrayItem;
            	     	 			image2.title = arrayItem;
            	      				inputField.value = arrayItem;
      		      				}
               				}
                			var categoryInputField = document.getElementById('updateItemCategory');
                			var nameInputField = document.getElementById('updateItemName');
                			var numberInputField = document.getElementById('updateItemNumber');
                			categoryInputField.value = array[array.length - 3];
                			nameInputField.value = array[array.length - 2];
                			numberInputField.value = array[array.length - 1];
  						}
  				   </script>	
				</c:when>
				<c:when test="${it.task == 'viewproducts'}">
					<c:choose>
						<c:when test="${it.viewtask == 'viewitemsbycategorytype'}">
							<c:choose>
								<c:when test="${it.viewtasklist == 'itemsfound'}">
									<div id="viewItemListScreen">
										<table id="productItemsTableViewList">
											<col width="180">
                                            <col width="180">
                                            <col width="180">
                                            <col width="180">
                                            <col width="180">
                                            <col width="180">
                                            <tr  height="70">
    											<th><font size="3">Category</font></th>
    											<th><font size="3">Product Type</font></th>
    											<th><font size="3">Product Name</font></th>
    											<th><font size="3">Product Number</font></th>
    											<th><font size="3">Product Price</font></th>
    											<th><font size="3">Product Image</font></th>
 											 </tr>
   											<c:forEach var="item" items="${it.items}">
    											<tr id="viewProductItemsList">
        											<td id="viewItemListCategory" height="100"><font size="3">${item.category}</font></td>
        											<td id="viewItemListType" height="100"><font size="3">${item.producttype}</font></td>
        											<td id="viewItemListSpecificName" height="100"><font size="3">${item.specificname}</font></td>
        											<td id="viewItemListNumber" height="100"><font size="3">${item.productnumber}</font></td>
        											<td id="viewItemListPrice" height="100"><font size="3">${item.price}</font></td>
        											<td id="viewItemListImage" height="100">
        												<img id="tableImageListView" src="data:image/png;base64, ${item.productimage}" width="90" height="90" alt="Image"/>
        											</td>
    											</tr>
  											</c:forEach>
										</table>
									</div>
								</c:when>
								<c:when test="${it.viewtasklist == 'noitemsfound'}">
									<div id="alertInfo" class="alert alert-info">
  										<strong>Info!</strong>  No products to display.
  									</div>
								</c:when>
								<c:otherwise>
								</c:otherwise>
						    </c:choose>
							<form id="viewProductForm" name="viewProductForm" action="/ShoppingPointServer/webapi/productmanagement/viewitemlist" method="POST">
								<table border="0">
									<tr><td><p id="viewItemCategoryText">Select Product Category</p></td></tr>
									<tr>
										<td>
											<select id="viewItemCategoryMenu" name="viewItemCategoryMenu">
  												<option value="appliances">Appliances</option>
  												<option value="food">Groceries</option>
  												<option value="clothes">Clothes</option>
  		      									<option value="audio">Audio</option>
											</select>
										</td>
									</tr>
									<tr>
										<td><p id="viewItemTypeNameText">Product Type</p></td>
										<td><input type="text" name="viewItemsType" placeholder="Enter Product Type" value="" id="viewItemsType" size="50" required /></td>
									</tr>
									<tr><td><input type="submit" value="View Items" id="viewItemCategoryBtn" name="submit" /></td></tr>
								</table>
						</form>
					</c:when>
					<c:when test="${it.viewtask == 'viewsingleitem'}">
						<c:choose>
							<c:when test="${it.viewtasksingleitem == 'itemfound'}">
								<div id="viewSingleItemScreen">
									<p id="itemSingleCategoryText">Product Category:  ${it.category}</p>
									<p id="itemSingleTypeText">Product Type:  ${it.type}</p>
									<p id="itemSingleGeneralNameText">Product General Name:  ${it.genname}</p>
									<p id="itemSingleSpecificNameText">Product Specific Name:  ${it.specname}</p>
									<p id="itemSingleNumberText">Product Number:  ${it.itemnum}</p>
									<p id="itemSingleShortDescrText">Short Description:  ${it.shortdescr}</p>
									<p id="itemSingleLongDescrText">Long Description:  ${it.longdescr}</p>
									<p id="itemSingleQuantityText">Product Quantity:  ${it.quantity}</p>
									<p id="itemSinglePriseText">Product Price:  ${it.price}</p>
									<p>
										<img id="databaseImage" src="data:image/png;base64, ${it.image }" width="140" height="140" alt="Image"/>
									</p>
								</div>
							</c:when>
							<c:when test="${it.viewtasksingleitem == 'noitemfound'}">
								<div id="alertInfo" class="alert alert-info">
  									<strong>Info!</strong>  Product item doesn't exist.
  								</div>
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
						<form id="viewProductForm" name="viewProductForm" action="/ShoppingPointServer/webapi/productmanagement/viewsingleitem" method="POST">
							<table border="0">
								<tr><td><p id="viewItemCategoryText">Product Category</p></td></tr>
								<tr>
									<td>
										<select id="viewItemCategoryMenu" name="viewItemCategoryMenu">
  											<option value="appliances">Appliances</option>
  											<option value="food">Groceries</option>
  											<option value="clothes">Clothes</option>
  		      								<option value="audio">Audio</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><p id="viewItemTypeNameText">Product Type</p></td>
								<td><input type="text" name="viewItemType" placeholder="Enter Product Type" value="" id="viewItemType" size="50" required /></td>
							</tr>
							<tr>
								<td><p id="viewItemSpecificNameText">Product Specific Name</p></td>
								<td><input type="text" name="viewItemSpecificName" placeholder="Enter Product Name" value="" id="viewItemSpecificName" size="50" required  /></td>
							</tr>
							<tr>
								<td><p id="viewItemNumberText">Product Number</p></td>
								<td><input type="text" name="viewItemNumber" placeholder="Enter Product Number" value="" id="viewItemNumber" size="50" required  /></td>
							</tr>
							<tr><td><input type="submit" value="View Item" id="viewItemBtn" name="submit" /></td></tr>
						</table>
					</form>
					</c:when>
					<c:otherwise>
						<form id="viewItemsByCategoryTypeForm" name="viewItemsByCategoryTypeForm" action="/ShoppingPointServer/webapi/productmanagement/viewitemsbycategory" method="POST">
							<button id="viewItemsCategoryLink" class="btn btn-link" type="submit" value="View Products bt Category and Type"><b>View Products by Category and Type</b></button>
						</form>
						<form id="viewSpecificItemForm" name="viewSpecificItemForm" action="/ShoppingPointServer/webapi/productmanagement/viewitem" method="POST">
							<button id="viewSpecificItemLink" class="btn btn-link" type="submit" value="View Specific Product"><b>View Specific Product</b></button>
						</form>
					</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${it.task == 'vieworders'}">
					<c:choose>
						<c:when test="${it.customer == 'yescustomers'}">
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
					<div id="viewOrdersListScreen">
						<table id="orderItemsTableViewList">
							<col width="180">
                            <col width="180">
                            <col width="180">
                            <col width="180">
                            <col width="180">
                           <col width="180">
                           <tr  height="20">
    							<th><font size="3">First Name</font></th>
    							<th><font size="3">Last Name</font></th>
    							<th><font size="3">User Name</font></th>
    							<th><font size="3">Product Name</font></th>
    							<th><font size="3">Product Number</font></th>
    							<th><font size="3">Product Price</font></th>
 							</tr>
   						    <c:forEach var="item" items="${it.customerorderslist}">
   								<tr id="viewOrderItemsList">
  								<td id="viewOrdersListFirstName" height="30"><font size="3">${item.customer.firstname}</font></td>
   								<td id="viewOrdersListLastName" height="30"><font size="3">${item.customer.lastname}</font></td>
   								<td id="viewOrdersListUsername" height="30"><font size="3">${item.customer.username}</font></td>
   								<td id="viewProductName" height="30"><font size="3">${item.specificname}</font></td>
   								<td id="viewProductNumber" height="30"><font size="3">${item.productnumber}</font></td>
   								<td id="viewProductPrice" height="30"><font size="3">${item.price}</font></td>
   								</tr>
							</c:forEach>
						</table>
					</div>
				</c:when>
				<c:when test="${it.task == 'viewstats'}">
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
		</div>	
	</body>
</html>