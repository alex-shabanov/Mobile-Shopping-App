package org.admin.pkg.ShoppingPointServer.persistence;

import org.admin.pkg.ShoppingPointServer.MyResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

public class MyApplicationResourseConfig extends ResourceConfig {
	
	public MyApplicationResourseConfig(){
		packages("src.main.webapp.WEB-INF.jsp");
		property("jersey.config.server.mvc.templateBasePath.jsp", "/WEB-INF/jsp");
		register(org.glassfish.jersey.server.mvc.MvcFeature.class);
		register(JspMvcFeature.class);
		register(MyResource.class);
	}
}
