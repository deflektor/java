package org.drei.reports;

//import com.businessobjects.rebean.wi.DocumentInstance;
//import com.businessobjects.rebean.wi.ReportDictionary;
//import com.businessobjects.rebean.wi.ReportEngine;
import com.businessobjects.rebean.wi.ReportEngines;
//import com.businessobjects.rebean.wi.VariableExpression;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.crystaldecisions.sdk.occa.infostore.*;
import com.crystaldecisions.sdk.plugin.desktop.user.*;
//import com.crystaldecisions.sdk.properties.*;
//import com.crystaldecisions.sdk.properties.internal.SDKPropertyBag;
//import com.crystaldecisions.sdk.properties.internal.SDKPropertyBagHelper;

//import java.util.*;
//import java.util.Map.Entry;

public class deleteUser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	      IEnterpriseSession enterpriseSession = null;
	      ReportEngines reportEngines = null;
	      if (args.length != 5)
	      {
		      System.out.println("deleteUser.jar");
		      System.out.println("---------------------------------------------------------------------------------------");
		      System.out.println("usage: java -jar deleteUser.jar <system> <user> <password> <authent> <user to delete>");
		      System.out.println(" ");
		      System.out.println(" authent: secEnterprise secWinAd");
		      System.out.println("---------------------------------------------------------------------------------------");
		      
	    	  System.exit(1);
	      }
	      String my_system = args[0];
	      String my_user = args[1];
	      String my_password = args[2];
	      String my_authorisation = args[3];
	      String my_delete_user = args[4];
	      try {
	         ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
	         enterpriseSession = sessionMgr.logon(my_user,
	               my_password, my_system, my_authorisation);
	         reportEngines = (ReportEngines) enterpriseSession
	               .getService("ReportEngines");

	         IInfoStore infoStore =
	               (IInfoStore) enterpriseSession.getService("InfoStore");
	         //String query = "select SI_NAME, SI_ID from CI_Infoobjects where si_parent_folder=11877 and si_name like 'BI%' and SI_INSTANCE_OBJECT = 0 and si_kind='FullClient' order by Si_name ";
	         String query = "SELECT * FROM CI_SYSTEMOBJECTS WHERE SI_NAME='" + my_delete_user + "'";
	         IInfoObjects infoObjects = (IInfoObjects) infoStore.query(query);

	         for (Object object : infoObjects) {
	            IInfoObject infoObject = (IInfoObject) object;
	            IUser act_user = (IUser) infoObject;
	            String objTitle = infoObject.getTitle();
	            
	            //System.out.println(repTitle + " Title "+infoObject.toString());
	            if (!objTitle.equals("Administrator") && !objTitle.equals("Guest"))
	            {    
	            	// adding the alias of disabled and setting it to true
	                //IUserAliases aliases = act_user.getAliases();
	                // geting the alias out of the collection of aliases
	                //IUserAlias alias = (IUserAlias)aliases.get("winAD:"+my_delete_user);
	                // setting the secEnterprise alias to disabled which makes the whole account disabled
	                //alias.();
	                
	            	infoObject.deleteNow();
	            	//infoObjects.delete(infoObject);
	            	infoStore.commit(infoObjects);
	        	    System.out.println("User " + objTitle + " removed");	        	    
	        	} else {
	        		System.out.println("It is not recommended to remove the Administrator or Guest user accounts");
	        	}
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

	
	}