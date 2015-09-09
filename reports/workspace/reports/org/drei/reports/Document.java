package org.drei.reports;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.text.*;

import com.businessobjects.rebean.wi.*; 
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.crystaldecisions.sdk.occa.infostore.*;
import com.businessobjects.rebean.wi.DocumentInstance;
import com.businessobjects.rebean.wi.ReportDictionary;
//import com.businessobjects.rebean.wi.ReportEngine;
import com.businessobjects.rebean.wi.ReportEngines;
import com.businessobjects.rebean.wi.VariableExpression;


public class Document {
	
	private String my_system = null;
	private String my_user = null;
	private String my_password = null;
	private String my_authorisation = null;
	private String my_Report_name = null;
	private String my_Database = null;
	private String my_DB_user = null;
	private String my_DB_password = null;
	private int debug=0;
	
	private static String query_stmt = "SELECT * FROM CI_INFOOBJECTS WHERE SI_KIND='Webi' AND  SI_INSTANCE=0 AND SI_NAME='";
	//private static String[] Report_properties = {"SI_CUID", "docrepoid","name","title","documenttype","keywords","description","createdby","lastsavedby","enhancedViewing","stripquery","reportselected","permanentregionalformatting","repositorytype","locale","refreshonopen","extendmergedimension","querydrill","ispartiallyrefreshed","nbqaawsconnection","inputform","documentversion","lastrefreshduration","documentsize","creationtime","modificationtime","lastrefreshtime"};
	
	private IEnterpriseSession enterpriseSession = null;
	private ReportEngines engines = null;
	private DocumentInstance wiDoc = null;
	private ReportEngine wiRepEngine = null;
	private DocumentOracle myDatabase = null;
	private Properties myProperty = null;

	Document(String system, String user, String password, String authorisation, String Report_name,
			String Database, String DB_user, String DB_password)
	{
		my_system = system;
		my_user = user;
		my_password = password;
		my_authorisation = authorisation;
		my_Report_name = Report_name;
		
		my_Database = Database;
		my_DB_user = DB_user;
		my_DB_password = DB_password;
	}
	
	public void setDebug()
	{
		debug=1;
	}
	public void processDocument() throws Exception
	{
		//Login to the system
  	  	ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
  	  	enterpriseSession = sessionMgr.logon(my_user,my_password, my_system, my_authorisation);

        //Get the Report Engine
	    engines = (ReportEngines) enterpriseSession.getService("ReportEngines");

		wiRepEngine = engines.getService(ReportEngines.ReportEngineType.WI_REPORT_ENGINE);
		int myret = RetriveDocument();
		System.out.println("Return number is "+myret);
		wiDoc = wiRepEngine.openDocument(myret);
		
		myDatabase = new DocumentOracle(my_Database, my_DB_user, my_DB_password);
		
		myProperty=null;
		myProperty=wiDoc.getProperties();
		processProperties(myProperty);
		processDataProvider(wiDoc.getDataProviders());
		wiDoc.closeDocument();
		wiDoc = null;
		wiRepEngine = null;
		enterpriseSession = null;
	}
	
	private void processProperties (Properties myProp) throws Exception
	{
		if (debug==1)
		{
			myProp.list(System.out);
		}
		
		Calendar mycal = Calendar.getInstance(); //TimeZone.getTimeZone("Europe/Vienna"));
		mycal.setTimeInMillis(Long.parseLong(myProp.getProperty("creationtime"))*1000);
		Calendar creationdt = (Calendar)mycal.clone();
		mycal.setTimeInMillis(Long.parseLong(myProp.getProperty("modificationtime"))*1000);
		Calendar modificationdt = (Calendar)mycal.clone();
		mycal.setTimeInMillis(Long.parseLong(myProp.getProperty("lastrefreshtime"))*1000);
		Calendar lastrefreshdt = (Calendar)mycal.clone();
		
		myDatabase.insertBoxiReports(myProp.getProperty("SI_CUID"),
				Integer.parseInt(myProp.getProperty("docrepoid")),
				myProp.getProperty("name"),
				myProp.getProperty("title"), 
				myProp.getProperty("document_type"),
				myProp.getProperty("keywords"),
				myProp.getProperty("description"),
				myProp.getProperty("createdby"),
				myProp.getProperty("modifiedby"),
				(Boolean.getBoolean(myProp.getProperty("enchancedviewing"))?1:0), 
				(Boolean.getBoolean(myProp.getProperty("stripquery"))?1:0),
				Integer.parseInt(myProp.getProperty("reportselected")),
				(Boolean.getBoolean(myProp.getProperty("permanentregionalformatting"))?1:0),
				myProp.getProperty("repositorytype"),
				myProp.getProperty("locale"), 
				(Boolean.getBoolean(myProp.getProperty("refreshonopen"))?1:0),
				(Boolean.getBoolean(myProp.getProperty("extendmergedimension"))?1:0),
				(Boolean.getBoolean(myProp.getProperty("querydrill"))?1:0),
				(Boolean.getBoolean(myProp.getProperty("ispartiallyrefreshed"))?1:0),
				(Boolean.getBoolean(myProp.getProperty("nbqaawsconnection"))?1:0),
				myProp.getProperty("inputform"),
				myProp.getProperty("documentversion"),
				Integer.parseInt(myProp.getProperty("lastrefreshduration")),
				Integer.parseInt(myProp.getProperty("documentsize")),
				creationdt,
				modificationdt,
				lastrefreshdt
				);
		
	}
	private int RetriveDocument() throws Exception
	{	
	    //Get a Session to query the repository
        IInfoStore boInfoStore = (IInfoStore) enterpriseSession.getService("InfoStore");  
        //Get and open the WebI report
        String query = query_stmt + my_Report_name +"'";
        
        //Execute the query
        IInfoObjects boInfoObjects = (IInfoObjects) boInfoStore.query(query);
        if (boInfoObjects.isEmpty() ) {
           System.out.println( "ERROR: report "+my_Report_name+" not found" ) ;
           throw( null ) ; // bail
        }
        //Retrieve the first InfoObject instance of the Web Intelligence document
        IInfoObject infoObject = (IInfoObject) boInfoObjects.get(0);
	             
	    return infoObject.getID();
	}
	private void processDataProvider(DataProviders wiDPs)
	{
		for(int dp_position=0;dp_position<wiDPs.getCount();dp_position++)
        {
			if(wiDPs.getItem(dp_position) instanceof SQLDataProvider)
			{
				processSQLDataProvider((SQLDataProvider)wiDPs.getItem(dp_position));
				/*try {
					//processSQLDP((SQLDataProvider)wiDPs.getItem(dp_position));
					System.out.println("Testing only");
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				*/
			}
        }
	}
	
	private void processSQLDP (SQLDataProvider my_sqldp) throws Exception
	{
		Calendar mycal = Calendar.getInstance(); //TimeZone.getTimeZone("Europe/Vienna"));
		
		SQLContainer oSQLContainer_root = my_sqldp.getSQLContainer();
		if (oSQLContainer_root != null) 
		{     
			for (int j=0; j<oSQLContainer_root.getChildCount(); j++) 
			{   
				SQLNode oSQLNode = (SQLNode) oSQLContainer_root.getChildAt(j);
				SQLSelectStatement oSQLSelectStatement = (SQLSelectStatement) oSQLNode;              
				String sqlStatement = oSQLSelectStatement.getSQL();
				//System.out.println("Results for query ");
				//System.out.println(sqlStatement);
				
				String DPName = my_sqldp.getName();
			   	String DPUniverse = my_sqldp.getSource();
			  	int DPpartialrefresh = my_sqldp.isPartialResult()?1:0;
			   	 
			   	int DProws = my_sqldp.getRowCount();
			   	int DPnbflow = my_sqldp.getFlowCount();
			   	int DPrefreshable = my_sqldp.isIgnoredForRefresh()?0:1;
			   	Calendar DPDate = Calendar.getInstance();
			   	DPDate.setTime(my_sqldp.getDate());
			   	Calendar lastrefreshtime = (Calendar)DPDate.clone();
				myDatabase.insertBoxiDataProvider(
						my_Report_name, 
						DPName,
						1,
						DPrefreshable,
						lastrefreshtime,
						my_sqldp.getDuration(),
						1,
						DProws, 
						DPnbflow,
						DPpartialrefresh,
						DPUniverse,  
						sqlStatement);
			}      
		} 
		else 
		{    
			System.out.println("Data Provider is not a SQLDataProvider.  SQL Statement can not be retrieved.");  
		}

	}
	
	private int tryParse(String myInput)
	{
		try {
			return Integer.parseInt(myInput);
		} catch (Exception e){
			return 0;
		}
	}
	
	private void processSQLDataProvider (SQLDataProvider my_sqldp)
	{
		SQLDataProvider wiSDP = my_sqldp;
   	 
   	 	String DPName = wiSDP.getName();
	   	String DPUniverse = wiSDP.getSource();
	   	int DPpartialrefresh = wiSDP.isPartialResult()?1:0;
   	 
	   	int DProws = wiSDP.getRowCount();
	   	String DPnbflow = ((Integer)wiSDP.getFlowCount()).toString();
	   	int DPrefreshable = wiSDP.isIgnoredForRefresh()?0:1;
	   	Calendar DPDate = Calendar.getInstance();
	   	try {
	   		DPDate.setTime(wiSDP.getDate());
	   	} catch (Exception e)
	   	{
	   		DPDate = new GregorianCalendar(2001, 0, 1);
	   	}
	   	Calendar lastrefreshtime = (Calendar)DPDate.clone();
	   	
	   	if (debug==1)
	   	{
		   	System.out.println("Report_name "+my_Report_name);
		   	System.out.println("Dataprovider_name "+DPName);
	
		   	System.out.println("Refreshable "+DPrefreshable+" ");
		   	
		   	SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			String lastexecution = date_format.format(DPDate.getTime());
		   	System.out.println("Lastexecutiontime " +  lastexecution);
	   	 
		   	System.out.println("Maxduration " + wiSDP.getDuration());
	   	 
		   	System.out.println("NBCubes "+DPnbflow+" ");
		   	System.out.println("NBRowsfetched "+DProws);
		   	System.out.println("PartialRefresh "+DPpartialrefresh);
		   	System.out.println("Universe_name "+DPUniverse);
	   	}
	   	String sql_output = null;
   	 
	   	if(! wiSDP.isSupported(DataProviderFeature.VIEW_SQL))
	   	{
	   		System.out.println("SQL not supported");
	   	}
	   	if ( wiSDP.hasCombinedQueries() ) 
	   	{
	   		QueryContainer my_Queryblock = wiSDP.getCombinedQueries();
            sql_output = ((Query)my_Queryblock.getChildAt(0)).getSQL();

            processResultObject(((Query)my_Queryblock.getChildAt(0)), DPName);
        } else 
        {
       	 	boolean resolvePrompts = true;
       	 	wiSDP.resetSQL();
            sql_output = processSQLContainer(wiSDP.getSQLContainer(resolvePrompts)) ;
            if (debug==1)
            {
            	System.out.println(sql_output);
            }
    	   	//BOXI_Query
    	   	processResultObject(wiSDP.getQuery(), DPName);
        }
	   	
	   	try {
	   		//System.out.println("My Report Name "+myProperty.getProperty("name"));
	   		myDatabase.insertBoxiDataProvider(
	   				my_Report_name, 
					DPName,
					1,
					DPrefreshable,
					lastrefreshtime,
					wiSDP.getDuration(),
					1,
					0, 
					0,
					DPpartialrefresh,
					DPUniverse, 
					sql_output);
		} catch (Exception e)
		{
			System.out.println("Cannot insert into Database");
			e.printStackTrace();
		}
	}
	protected void finalize() throws Throwable
	{
		//do finalization here
   	 	engines.close();
        enterpriseSession.logoff();
		super.finalize(); //not necessary if extending Object.
	} 
	public static void main(String[] args) {
	      if (args.length != 8)
	      {
		      System.out.println("document.jar");
		      System.out.println("----------------------------------------------------------------------------");
		      System.out.println("usage: java -jar document.jar <system> <user> <password> <authent> <report name> <database> <db_user> <db_password>");
		      System.out.println(" ");
		      System.out.println(" java -jar document.jar bo automate_bi <password> secEnterprise \"BI026_ORS Daily Gross Adds\" edwp06 admin <db_password>");
		      System.out.println(" authent: secEnterprise secWinAd");
		      System.out.println(" Database: <server>:1521/<tns service>");
		      System.out.println("----------------------------------------------------------------------------");
		      
	    	  System.exit(1);
	      }
	      String my_system = args[0];
	      String my_user = args[1];
	      String my_password = args[2];
	      String my_authorisation = args[3];
	      String strDoc = args[4];
	      System.out.println("Parameter 4 "+ strDoc);
	      String my_Database = args[5];
	      String my_DB_user = args[6];
	      String my_DB_password = args[7];
	      
	      Document myDoc = new Document(my_system, my_user, my_password, my_authorisation, strDoc,
	    		  my_Database, my_DB_user, my_DB_password);
	      
	      //myDoc.setDebug(); // set if Debug is necesary
	      try {
	    	  myDoc.processDocument();
	      } catch (Exception e)
	      {
	    	  e.printStackTrace();
	      }
	      
	}
	private void processResultObject (Query inQuery, String DP_name)
	//Traverse Result objects
	// Input for: BOXI_QUERY (Report_name, Dataprovider_name, Uni_Class, uni_object, update_date)
	{
		for (int x=0; x<inQuery.getResultObjectCount();x++)
		{
			DataSourceObject inDSO = inQuery.getResultObject(x);
			String ResObj = inDSO.getName();
			DataSourceObject ParentDSO = (DataSourceObject) inDSO.getParent();
			String ParentObj = ParentDSO.getName();
			if (debug==1)
			{
				System.out.println("Report_Name "+my_Report_name);
				System.out.println("Dataprovider_name "+DP_name);
				System.out.println("Uni_Class "+ParentObj);
				System.out.println("uni_object "+ResObj);
			}
			try {
				myDatabase.insertBoxiQuery(my_Report_name, DP_name, ParentObj, ResObj);
			} catch (Exception e)
			{
				System.out.println("Cannot insert into Database");
				e.printStackTrace();
			}
		}
	}
	public static void universe_processDataprovider (DataSourceObjects inDSO)
	//Inactive code for Universe objects description
	//Traverse the to whole Universe
	{
		for (int x=0;x<inDSO.getChildCount();x++)
		{
			DataSourceObject prDSO = inDSO.getChildAt(x);
			System.out.println("Object Name :"+prDSO.getName());
			System.out.println("Object Description :"+prDSO.getDescription());
			System.out.println("Object Childs :"+prDSO.getChildCount());
			if (prDSO.getChildCount()>0)
			{
				processDataobjects(prDSO);
			}
		}
	}
	public static void processDataobjects (DataSourceObject inDSO)
	// Inactive code part for universe object description
	{
		for (int x=0;x<inDSO.getChildCount();x++)
		{
			DataSourceObject prDSO = (DataSourceObject) inDSO.getChildAt(x);
			String parent_name = ((DataSourceObject)prDSO.getParent()).getName();
			String childs = "";
			if (prDSO.getChildCount()> 0)
			{
				childs = " Childs "+prDSO.getChildCount();
			}
			System.out.print("Parent "+parent_name);
			
			ObjectQualification OQ = prDSO.getQualification();
			String OQuali = " Qualification "+OQ.toString()+ " ";
			ObjectType OT = prDSO.getType();
			String OType = " Type "+OT.toString()+" ";
			DataSourceObjectPropertiesType DSOP = (DataSourceObjectPropertiesType)prDSO.getProperties();
			String OProp = " ";
			try 
			{
				OProp =" Properties "+DSOP.toString()+ " ";
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			System.out.println(" Name "+prDSO.getName()+" Description "+prDSO.getDescription()+OProp+OType+OQuali+childs+" has_LOV "+prDSO.hasLOV()+" is_Indexed "+prDSO.isLeaf());
			DataSource prDS = prDSO.getDataSource();
			if (prDS != null)
			{
				System.out.println(" Universe "+prDS.getName()+
						" " + prDS.getUniverseID());
			}
			
			//processDataProvider(prDSO);
		}
	}
	public static String processSQLContainer( SQLContainer sqlContainer )
	// Recursive method
	// Allows viewing of prompt definitions / values
	{
	   String   ret_val ;

	   // safety check
	   if ( sqlContainer == null ) {
	      return ( "ERROR: Could not find SQL Container" ) ;
	   }
	   ret_val  = "";
	   for ( int i=0; i < sqlContainer.getChildCount(); i++ ) 
	   {
	      SQLNode childNode = (SQLNode) sqlContainer.getChildAt( i ) ;
	      if ( i > 0 ) 
	      {
	         ret_val += " " + sqlContainer.getOperator() + " " ;
	      }
	      if ( childNode instanceof SQLSelectStatement ) 
	      {
	         /** recursion base **/
	         SQLSelectStatement   sqlStatement = (SQLSelectStatement) childNode ;
	         ret_val += sqlStatement.getSQL() + " " ;
	      } else 
	      { // SQLContainer
	         ret_val += processSQLContainer( (SQLContainer) childNode ) ;
	      }
	   }
	   return ret_val ;
	}
	public static void printDocumentVariables(DocumentInstance widoc ) 
	{
		ReportDictionary dic = widoc.getDictionary();
		VariableExpression[] variables = dic.getVariables();
		for (VariableExpression e : variables) {
			String name = e.getFormulaLanguageID();
			String expression = e.getFormula().getValue();
			System.out.println(" " + name + " " + expression);
		}
	}        
	public static String getInfoObjectPath(IInfoObject infoObject)
		                                        throws SDKException 
	{
		String path = "";
		while (infoObject.getParentID() != 0) {
			infoObject = infoObject.getParent();
			path = "/" + infoObject.getTitle() + path;
		}
		return path;
	}

}
