package autoOffice;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;


public class DocumentDB {
	
		private String my_db = null;
		private String my_user = null;
		private String my_passwd = null;
		private Connection conn = null;
		
		DocumentDB(String db, String user, String password) throws Exception
		{
			my_db = db;
			my_user = user;
			my_passwd = password;
			
			Class.forName ("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+my_db, my_user, my_passwd);
		}
		
		public boolean insertCpIndent(String db_demand_nr, String db_incident, String db_creation_date, String db_creation_user,
				String db_title, String db_description, String db_impact, String db_assigned_user, String db_classifciation,
				String db_priority, String db_status, String db_target_date, String db_notes
				) throws Exception
		{
			boolean ret_code = false;
			String mergeSql = "MERGE INTO edwro.cp_incident b"
							+ " USING ("
							+ " SELECT ? demand_nr"      
							+ "        ,? incident"       
							+ "        ,to_date(?,\'dd.mm.yyyy\') creation_date" 
							+ "        ,? creation_user" 
							+ "        ,? title"
							+ "        ,? description"
							+ "        ,? impact"         
							+ "        ,? assigned_user"  
							+ "        ,? classifciation" 
							+ "        ,? priority"       
							+ "        ,? status"         
							+ "        ,to_date(?,\'dd.mm.yyyy\') target_date"    
							+ "        ,? notes"          
							+ " FROM dual) e"
							+ " ON (b.demand_nr = e.demand_nr and"
							+ "    b.incident = e.incident)"
							+ " WHEN MATCHED THEN"
							+ "   UPDATE SET "
							+ "         b.title=e.title"
							+ "         b.description=e.description"
							+ "         b.impact=e.impact"
							+ "         b.assigned_user=e.assigned_user"
							+ "         b.classification=e.classification"
							+ "         b.priority=e.priority"
							+ "         b.status=e.status"
							+ "         b.target_date=e.target_date"
							+ "         b.notes=e.notes"
							+ " WHEN NOT MATCHED THEN"
							+ "  INSERT ( b.demand_nr      "
							+ "          ,b.incident       "
							+ "          ,b.creation_date  "
							+ "          ,b.creation_user  "
							+ "          ,b.title          "
							+ "          ,b.description    "
							+ "          ,b.impact         "
							+ "          ,b.assigned_user  "
							+ "          ,b.classifciation "
							+ "          ,b.priority       "
							+ "          ,b.status         "
							+ "          ,b.target_date    "
							+ "          ,b.notes          "
							+ "          ,b.status_open    " 
							+ "          ,b.status_closed )"  
							+ "  VALUES ( e.demand_nr      "
							+ "          ,e.incident       "
							+ "          ,e.creation_date  "
							+ "          ,e.creation_user  "
							+ "          ,e.title          "
							+ "          ,e.description    "
							+ "          ,e.impact         "
							+ "          ,e.assigned_user  "
							+ "          ,e.classifciation "
							+ "          ,e.priority       "
							+ "          ,e.status         "
							+ "          ,e.target_date    "
							+ "          ,e.notes          "
							+ "          ,1    "
							+ "          ,0  )";

			
			PreparedStatement prep = conn.prepareStatement(mergeSql);
			prep.setString(1, db_demand_nr      );
			prep.setString(2, db_incident       );
			prep.setString(3, db_creation_date  );
			prep.setString(4, db_creation_user  );
			prep.setString(5, db_title          );
			prep.setString(6, db_description    );
			prep.setString(7, db_impact         );
			prep.setString(8, db_assigned_user  );
			prep.setString(9, db_classifciation );
			prep.setString(10, db_priority       );
			prep.setString(11, db_status         );
			prep.setString(12, db_target_date    );
			prep.setString(13, db_notes          );
			
			ret_code = prep.execute();
		
			prep.close();
			
			return ret_code;
		}
				
		private static String getCurrentDate() {
			 
			Calendar today = Calendar.getInstance();
			SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
			return date_format.format(today.getTime());
	 
		}


		protected void finalize() throws Throwable
		{
			//do finalization here
			conn.close();
			super.finalize(); //not necessary if extending Object.
		} 
	
}
