package org.drei.reports;

import com.businessobjects.rebean.wi.DocumentInstance;
import com.businessobjects.rebean.wi.ReportDictionary;
//import com.businessobjects.rebean.wi.ReportEngine;
import com.businessobjects.rebean.wi.ReportEngines;
import com.businessobjects.rebean.wi.VariableExpression;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.crystaldecisions.sdk.occa.infostore.*;
import com.crystaldecisions.sdk.properties.*;
import com.crystaldecisions.sdk.properties.internal.SDKPropertyBag;
import com.crystaldecisions.sdk.properties.internal.SDKPropertyBagHelper;

import java.util.*;
import java.util.Map.Entry;

public class Reports {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	      IEnterpriseSession enterpriseSession = null;
	      ReportEngines reportEngines = null;
	      if (args.length != 5)
	      {
		      System.out.println("reporting.jar");
		      System.out.println("----------------------------------------------------------------------------");
		      System.out.println("usage: java -jar reporting.jar <system> <user> <password> <authent> <query>");
		      System.out.println(" ");
		      System.out.println(" authent: secEnterprise secWinAd");
		      System.out.println("----------------------------------------------------------------------------");
		      
	    	  System.exit(1);
	      }
	      String my_system = args[0];
	      String my_user = args[1];
	      String my_password = args[2];
	      String my_authorisation = args[3];
	      String my_sql = args[4];
	      try {
	         ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
	         enterpriseSession = sessionMgr.logon(my_user,
	               my_password, my_system, my_authorisation);
	         reportEngines = (ReportEngines) enterpriseSession
	               .getService("ReportEngines");

	         IInfoStore infoStore =
	               (IInfoStore) enterpriseSession.getService("InfoStore");
	         //String query = "select SI_NAME, SI_ID from CI_Infoobjects where si_parent_folder=11877 and si_name like 'BI%' and SI_INSTANCE_OBJECT = 0 and si_kind='FullClient' order by Si_name ";
	         String query = my_sql;
	         IInfoObjects infoObjects = (IInfoObjects) infoStore.query(query);
	         int runing_count = 0;
	         for (Object object : infoObjects) {
	            IInfoObject infoObject = (IInfoObject) object;
	            String repTitle = infoObject.getTitle();
	            
	            //System.out.println(repTitle + " Title "+infoObject.toString());
	            if (runing_count==0)
	            {
		            printPropertiesHeader((infoObject.properties()).toString());
	            }
	            printProperties(infoObject.properties());

	            runing_count++;
	         }
	      }
	      catch (SDKException ex) {
	         ex.printStackTrace();
	      }
	      finally {
	         if (reportEngines != null)
	            reportEngines.close();
	         if (enterpriseSession != null)
	            enterpriseSession.logoff();
	      }
	      //System.out.println("Finished!");
	   }

	   public static void printPropertiesHeader(String myString)
	   {
		   String my_header = myString;
           
           String regex_pattern = "\\)\\s*?\\(";
           String regex_pattern_openbracket = "\\(";
           String regex_pattern_grap = "\\s";
           my_header = my_header.substring(1, my_header.length()-1);
           //System.out.println("Property to String : "+my_header);
           String[] my_header_split = my_header.split(regex_pattern);

	       for (int my_int = 0 ; my_int<my_header_split.length; my_int++)
	       {
	        	String my_working_split = my_header_split[my_int];
	        	if (my_int==0)
	        	{
	        		my_working_split = my_working_split.substring(1, my_working_split.length());
	        	}
	        	if (my_int>0 && my_working_split.matches(regex_pattern_openbracket))
	        	{
	        		System.out.println("Additional objects found ");
	        	}
	        	//System.out.println("Match number "+my_int+" : "+my_working_split);
	        	String[] my_object_split = my_working_split.split(regex_pattern_grap);
	        	System.out.print(my_object_split[0]);
	        	if(my_int<my_header_split.length-1)
	        	{
	        		System.out.print("|");
	        	}
	       }
	       System.out.println("");
	   }
	   public static void printProperties(IProperties myProps)
	   {
		   Set s = myProps.entrySet();
           Iterator it = s.iterator();
           while (it.hasNext())
           {
        	Map.Entry m = (Map.Entry)it.next();
        	String value = (m.getValue()).toString();
        	
           	System.out.print(value.trim());
           	
           	if (!it.hasNext())
           	{
           		System.out.println("");
           	} else
           	{
           		System.out.print("|");
           	}
           }
	   }
	   public static void printSchedule(ISchedulingInfo objectSchedule)
	   {
		   if (objectSchedule != null)
           {
           	//String progress = objectSchedule.getPROGRESS"].ToString();
	            //String outcome  = infoObject.SchedulingInfo().Properties["SI_OUTCOME"].ToString();
	            String insterror= objectSchedule.getErrorMessage();
	            
	            System.out.println("Error in Instance: " + insterror);
           }
	   }
	   public static void printDocumentVariables(DocumentInstance widoc ) {
	      ReportDictionary dic = widoc.getDictionary();
	      VariableExpression[] variables = dic.getVariables();
	      for (VariableExpression e : variables) {
	         String name = e.getFormulaLanguageID();
	         String expression = e.getFormula().getValue();
	         System.out.println(" " + name + " " + expression);
	      }
	   }        

	   public static String getInfoObjectPath(IInfoObject infoObject)
	                                        throws SDKException {
	      String path = "";
	      while (infoObject.getParentID() != 0) {
	         infoObject = infoObject.getParent();
	         path = "/" + infoObject.getTitle() + path;
	      }
	      return path;
	   }
	}
