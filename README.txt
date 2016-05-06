Android application intendet to run in latest version of android studio (1.5.1) with JRE 1.8,
on Android 6.0 device system version (i.e. SDK Platform 6.0 or 5.1.0 or 5.0), and using SDK 24.4.1 or 23.0.

Instructions:

1) DATABASE SET UP
   	When running the project for the first time, inside MyResource class located inside ShoppingPointServer project,
   in the getLoginState method, UNCOMMENT the six lines that will be responsible for creating necessary tables requred for this project.
   Then run the ShoppingPointServer on the Tomcat server. First press the SIGIN BUTTON (no username and password necessary). This will create
   the six database tables for persistant data storage. Then go back MyREsource class getLoginState() method and COMMENT OUT those six lines
   again so that each time you log in the database tables are not dropped and reacreated each time.

  2) IMAGE FILE SYSTEM
	The images are located inside the project folder named webapps inside images sub-folder.
     In ShoppingPointServer project, inside ShoppingPointManager class, inside addProductItem(), checkItemsExists(),
     and convertImageFileToBase64() methods change the project file path to what ever the project path you currently have.
     The current path in my project was "F:/temp/Workspace/ShoppingPointServer/src/main/webapp/images", however, "F:/temp/Workspace/"
     portion of the path might be different for different users, depending on particular users' file system.
     Also, do not change the file name of image files, as queryies will not be done properly.

  3) 	When adding items to database, because I only have a limited selection of product images and the way the product images are found,
     the GENERAL PRODUCT NAME can be one of the following KEY words: bread, cheese, apple, plum, grape, pants, sweater, mixer. These are the only product images
     project contains so far. 
     So, when selecting images for bread for example, CATEGORY TYPE sould be selected to GROCERIES and GENERAL PRODUCT NAME should be bread. Then the list of
     all the available bread products will be displayed to the admin user.
     Similarly, if user wants to see kitechen appliances, GENERAL CATEGORY NAME should be Appliances and GENERAL PRODUCT NAME should be mixer, because the only
     kitchen appliances' image files that project contains so far are images for blenders and mixers.

  4) FINDING PRODUCTS ANDROID CLIENT SIDE
     	Note, PODUCTTYPE attribute field of the ConsumerItem table is the one used for searching items in the database from the Andoid client side. So when storing
     the product item on the server side, the PRODUCTTYPE name given for the particular product item is the one that should be use when searching for this product
     item on the Android client side.
     So, for example, for different Blenders and Mixers, the PRODUCT TYPE can be mixer (for example). Then on the Andoid side when quering for different Blender,
     the user can select CATEGORY Appliances and type in mixer for product type. The list of all possible added Blender Mixers will then be displayed to the user.
     The point being is that the PRODUCTYPE name under which the item is stored in the database should be used on the Android side to get diffent products of the
     same type.  
