package hr.fer.rasip.http;

import gsn.http.*;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class PowerBoardControllerServlet extends HttpServlet {

   private static transient Logger logger = Logger.getLogger ( PowerBoardControllerServlet.class );

   	/**
    * getting the request from the web and handling it.
    */
   public void doGet ( HttpServletRequest request , HttpServletResponse response ) throws ServletException , IOException {
      
	  response.setContentType ( "text/xml" );
      response.setHeader ( "Expires" , "Sat, 6 May 1995 12:00:00 GMT" );
      response.setHeader ( "Cache-Control" , "no-store, no-cache, must-revalidate" );
      response.addHeader ( "Cache-Control" , "post-check=0, pre-check=0" );
      response.setHeader ( "Pragma" , "no-cache" );

     
	  StringBuilder sb = new StringBuilder ( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" );
      response.getWriter ( ).write ( sb.toString ( ) );
      RequestHandler handler;
      
      handler = new PowerBoardHandler();
      if (handler.isValid(request, response)) handler.handle(request, response);
    
      
   }

   public void doPost ( HttpServletRequest request , HttpServletResponse res ) throws ServletException , IOException {
      doGet ( request , res );
   }

}
