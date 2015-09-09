package org.drei.reports;

import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class DocumentOracle{
	
	private String my_db = null;
	private String my_user = null;
	private String my_passwd = null;
	private Connection conn = null;
	
	DocumentOracle(String db, String user, String password) throws Exception
	{
		my_db = db;
		my_user = user;
		my_passwd = password;
		
		Class.forName ("oracle.jdbc.OracleDriver");
		conn = DriverManager.getConnection("jdbc:oracle:thin:@"+my_db, my_user, my_passwd);
	}
	
	public boolean insertBoxiQuery(String reportname, String dpname, String uniclass, String uniobject) throws Exception
	{
		boolean ret_code = false;
		String mergeSql = "MERGE INTO admin.boxi_query b"
						+ " USING ("
						+ " SELECT ? report_name,"
						+ "        ? dataprovider_name,"
						+ "        ? uni_class,"
						+ "        ? uni_object,"
						+ "        to_date(?,\'dd.mm.yyyy\') update_date"
						+ " FROM dual) e"
						+ " ON (b.report_name = e.report_name and"
						+ "    b.dataprovider_name = e.dataprovider_name and"
						+ "    b.uni_class = e.uni_class and"
						+ "    b.uni_object = e.uni_object)"
						+ " WHEN MATCHED THEN"
						+ "   UPDATE SET "
						+ "         b.update_date=e.update_date"
						+ " WHEN NOT MATCHED THEN"
						+ "  INSERT (b.report_name,"
						+ "          b.dataprovider_name,"
						+ "          b.uni_class,"
						+ "          b.uni_object,"
						+ "          b.update_date"
						+ "         )"
						+ "  VALUES (e.report_name,"
						+ "          e.dataprovider_name,"
						+ "          e.uni_class,"
						+ "          e.uni_object,"
						+ "          e.update_date)";
		
		PreparedStatement prep = conn.prepareStatement(mergeSql);
		prep.setString(1, reportname);
		prep.setString(2, dpname);
		prep.setString(3, uniclass);
		prep.setString(4, uniobject);
		prep.setString(5, getCurrentDate());
		ret_code = prep.execute();
	
		prep.close();
		
		return ret_code;
	}
	
	public boolean insertBoxiReports(String cuid, int id, String report_name, String title, String document_type,
			String keywords, String description, String createdby, String modifiedby, int enhancedviewing,
			int stripquery, int reportselected, int permanentregionalformatting, String repositorytype,
			String locale, int refreshonopen, int extendmergedimension, int querydrill, int ispartiallyrefreshed,
			int nbqaawsconnection, String inputform, String documentversion, int lastrefreshduration,
			int documentsize, Calendar creationtime, Calendar modificationtime, Calendar lastrefreshtime) throws Exception
	{
		boolean ret_code = false;
		
		SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String creation = date_format.format(creationtime.getTime());
		String modification = date_format.format(modificationtime.getTime());
		String lastrefresh = date_format.format(lastrefreshtime.getTime());
		
		String mergeSQL = "MERGE INTO admin.boxi_reports b"
				+ " USING ("
				+ "  SELECT ? cuid,"
				+ "         ? id,"
				+ "         ? report_name,"
				+ "         ? title,"
				+ "         ? document_type,"
				+ "         ? keywords,"
				+ "         ? description,"
				+ "         ? createdby,"
				+ "         ? modifiedby,"
				+ "         ? enhancedviewing,"
				+ "         ? stripquery,"
				+ "         ? reportselected,"
				+ "         ? permanentregionalformatting,"
				+ "         ? repositorytype,"
				+ "         ? locale,"
				+ "         ? refreshonopen,"
				+ "         ? extendmergedimension,"
				+ "         ? querydrill,"
				+ "         ? ispartiallyrefreshed,"
				+ "         ? nbqaawsconnection,"
				+ "         ? inputform,"
				+ "         ? documentversion,"
				+ "         ? lastrefreshduration,"
				+ "         ? documentsize,"
				+ "         to_date(?,\'dd.mm.yyyy HH24:MI:SS\') creationtime,"
				+ "         to_date(?,\'dd.mm.yyyy HH24:MI:SS\') modificationtime,"
				+ "         to_date(?,\'dd.mm.yyyy HH24:MI:SS\') lastrefreshtime,"
				+ "         to_date(?,\'dd.mm.yyyy\') update_date"
				+ "  FROM dual) e"
				+ " ON (b.report_name=e.report_name"
				+ "     AND b.id=e.id)"
				+ " WHEN MATCHED THEN"
				+ "  UPDATE SET "
				+ "         b.cuid=e.cuid,"
				+ "         b.title=e.title,"
				+ "         b.document_type=e.document_type,"
				+ "         b.keywords=e.keywords,"
				+ "         b.description=e.description,"
				+ "         b.createdby=e.createdby,"
				+ "         b.modifiedby=e.modifiedby,"
				+ "         b.enhancedviewing=e.enhancedviewing,"
				+ "         b.stripquery=e.stripquery,"
				+ "         b.reportselected=e.reportselected,"
				+ "         b.permanentregionalformatting=e.permanentregionalformatting,"
				+ "         b.repositorytype=e.repositorytype,"
				+ "         b.locale=e.locale,"
				+ "         b.refreshonopen=e.refreshonopen,"
				+ "         b.extendmergedimension=e.extendmergedimension,"
				+ "         b.querydrill=e.querydrill,"
				+ "         b.ispartiallyrefreshed=e.ispartiallyrefreshed,"
				+ "         b.nbqaawsconnection=e.nbqaawsconnection,"
				+ "         b.inputform=e.inputform,"
				+ "         b.documentversion=e.documentversion,"
				+ "         b.lastrefreshduration=e.lastrefreshduration,"
				+ "         b.documentsize=e.documentsize,"
				+ "         b.creationtime=e.creationtime,"
				+ "         b.modificationtime=e.modificationtime,"
				+ "         b.lastrefreshtime=e.lastrefreshtime,"
				+ "         b.update_date=e.update_date"
				+ " WHEN NOT MATCHED THEN"
				+ "  INSERT (b.report_name,"
				+ "          b.id,"
				+ "          b.cuid,"
				+ "          b.title,"
				+ "          b.document_type,"
				+ "          b.keywords,"
				+ "          b.description,"
				+ "          b.createdby,"
				+ "          b.modifiedby,"
				+ "          b.enhancedviewing,"
				+ "          b.stripquery,"
				+ "          b.reportselected,"
				+ "          b.permanentregionalformatting,"
				+ "          b.repositorytype,"
				+ "          b.locale,"
				+ "          b.refreshonopen,"
				+ "          b.extendmergedimension,"
				+ "          b.querydrill,"
				+ "          b.ispartiallyrefreshed,"
				+ "          b.nbqaawsconnection,"
				+ "          b.inputform,"
				+ "          b.documentversion,"
				+ "          b.lastrefreshduration,"
				+ "          b.documentsize,"
				+ "          b.creationtime,"
				+ "          b.modificationtime,"
				+ "          b.lastrefreshtime,"
				+ "          b.update_date"
				+ "         )"
				+ "  VALUES (e.report_name,"
				+ "          e.id,"
				+ "          e.cuid,"
				+ "          e.title,"
				+ "          e.document_type,"
				+ "          e.keywords,"
				+ "          e.description,"
				+ "          e.createdby,"
				+ "          e.modifiedby,"
				+ "          e.enhancedviewing,"
				+ "          e.stripquery,"
				+ "          e.reportselected,"
				+ "          e.permanentregionalformatting,"
				+ "          e.repositorytype,"
				+ "          e.locale,"
				+ "          e.refreshonopen,"
				+ "          e.extendmergedimension,"
				+ "          e.querydrill,"
				+ "          e.ispartiallyrefreshed,"
				+ "          e.nbqaawsconnection,"
				+ "          e.inputform,"
				+ "          e.documentversion,"
				+ "          e.lastrefreshduration,"
				+ "          e.documentsize,"
				+ "          e.creationtime,"
				+ "          e.modificationtime,"
				+ "          e.lastrefreshtime,"
				+ "          e.update_date"
				+ "         )";
		
		PreparedStatement prep = conn.prepareStatement(mergeSQL);
		prep.setString(1, cuid);
		prep.setInt   (2, id);
		prep.setString(3, report_name);
		prep.setString(4, title);
		prep.setString(5, document_type);
		prep.setString(6, keywords);
		prep.setString(7, description);
		prep.setString(8, createdby);
		prep.setString(9, modifiedby);
		prep.setInt   (10, enhancedviewing);
		prep.setInt   (11, stripquery);
		prep.setInt   (12, reportselected);
		prep.setInt   (13, permanentregionalformatting);
		prep.setString(14, repositorytype);
		prep.setString(15, locale);
		prep.setInt   (16, refreshonopen);
		prep.setInt   (17, extendmergedimension);
		prep.setInt   (18, querydrill);
		prep.setInt   (19, ispartiallyrefreshed);
		prep.setInt   (20, nbqaawsconnection);
		prep.setString(21, inputform);
		prep.setString(22, documentversion);
		prep.setInt   (23, lastrefreshduration);
		prep.setInt   (24, documentsize);
		prep.setString(25, creation);
		prep.setString(26, modification);
		prep.setString(27, lastrefresh);
		prep.setString(28, getCurrentDate());
		ret_code = prep.execute();
		
		prep.close();
		
		return ret_code;
	}
	
	public boolean insertBoxiDataProvider(String reportname, String dpname, 
			String editable, String refreshable, Calendar lastexecutiontime,
			String maxduration, String maxnblines, String nbcubes, String nbrowsfetched,
			String partialrefresh, String universename, String stmtsql) throws Exception
	{
		boolean ret_code = false;
		
		SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String lastexecution = date_format.format(lastexecutiontime.getTime());
		 
		String mergeSql = "MERGE INTO admin.boxi_dataprovider b"
				+ " USING ("
				+ "  SELECT ? report_name,"
				+ "         ? dataprovider_name,"
				+ "         ? editable,"
				+ "         ? refreshable,"
				+ "         to_date(?,\'dd.mm.yyyy HH24:MI:SS\') lastexecutiontime,"
				+ "         ? maxduration,"
				+ "         ? maxnblines,"
				+ "         ? nbcubes,"
				+ "         ? nbrowsfetched,"
				+ "         ? partialrefresh,"
				+ "         ? universe_name,"
				+ "         ? stmt_sql,"
				+ "         to_date(?,\'dd.mm.yyyy\') update_date"
				+ "  FROM dual) e"
				+ " ON (b.report_name = e.report_name and"
				+ "    b.dataprovider_name = e.dataprovider_name)"
				+ " WHEN MATCHED THEN"
				+ "  UPDATE SET "
				+ "         b.editable=e.editable,"
				+ "         b.refreshable=e.refreshable,"
				+ "         b.lastexecutiontime=e.lastexecutiontime,"
				+ "         b.maxduration=e.maxduration,"
				+ "         b.maxnblines=e.maxnblines,"
				+ "         b.nbcubes=e.nbcubes,"
				+ "         b.nbrowsfetched=e.nbrowsfetched,"
				+ "         b.partialrefresh=e.partialrefresh,"
				+ "         b.universe_name=e.universe_name,"
				+ "         b.stmt_sql=e.stmt_sql,"
				+ "         b.update_date=e.update_date"
				+ " WHEN NOT MATCHED THEN"
				+ "  INSERT (b.report_name,"
				+ "          b.dataprovider_name,"
				+ "          b.editable,"
				+ "          b.refreshable,"
				+ "          b.lastexecutiontime,"
				+ "          b.maxduration,"
				+ "          b.maxnblines,"
				+ "          b.nbcubes,"
				+ "          b.nbrowsfetched,"
				+ "          b.partialrefresh,"
				+ "          b.universe_name,"
				+ "          b.stmt_sql,"
				+ "          b.update_date"
				+ "         )"
				+ "  VALUES (e.report_name,"
				+ "          e.dataprovider_name,"
				+ "          e.editable,"
				+ "          e.refreshable,"
				+ "          e.lastexecutiontime,"
				+ "          e.maxduration,"
				+ "          e.maxnblines,"
				+ "          e.nbcubes,"
				+ "          e.nbrowsfetched,"
				+ "          e.partialrefresh,"
				+ "          e.universe_name,"
				+ "          e.stmt_sql,"
				+ "          e.update_date)";
						
		PreparedStatement prep = conn.prepareStatement(mergeSql);
		prep.setString(1, reportname);
		prep.setString(2, dpname);
		prep.setString(3, editable);
		prep.setString(4, refreshable);
		prep.setString(5, lastexecution);
		prep.setString(6, maxduration);
		prep.setString(7, maxnblines);
		prep.setString(8, nbcubes);
		prep.setString(9, nbrowsfetched);
		prep.setString(10, partialrefresh);
		prep.setString(11, universename);
		prep.setString(12, stmtsql);
		prep.setString(13, getCurrentDate());
		ret_code = prep.execute();
	
		prep.close();
		
		return ret_code;
	}
	
	public boolean insertUniverse(String name, String longname, 
			String description, String connection, String author,
			String creation, String revision, String comments, String fullname) throws Exception
	{
		boolean ret_code = false;
		
		String mergeSql = "MERGE INTO admin.boxi_universe b"
				+ " USING ("
				+ "  SELECT ? name,"
				+ "         ? longname,"
				+ "         ? description,"
				+ "         ? connection,"
				+ "         ? author,"
				+ "         to_date(?,\'dd.mm.yyyy\') creation_date,"
				+ "         ? revision,"
				+ "         ? comments,"
				+ "         ? fullname,"
				+ "         to_date(?,\'dd.mm.yyyy\') update_date"
				+ "  FROM dual) e"
				+ " ON (b.name = e.name)"
				+ " WHEN MATCHED THEN"
				+ "  UPDATE SET b.longname=e.longname,"
				+ "         b.description=e.description,"
				+ "         b.connection=e.connection,"
				+ "         b.author=e.author,"
				+ "         b.creation_date=e.creation_date,"
				+ "         b.revision=e.revision,"
				+ "         b.comments=e.comments,"
				+ "         b.fullname=e.fullname,"
				+ "         b.update_date=e.update_date"
				+ " WHEN NOT MATCHED THEN"
				+ "  INSERT (b.name,"
				+ "          b.longname,"
				+ "          b.description,"
				+ "          b.connection,"
				+ "          b.author,"
				+ "          b.creation_date,"
				+ "          b.revision,"
				+ "          b.comments,"
				+ "          b.fullname,"
				+ "          b.update_date"
				+ "         )"
				+ "  VALUES (e.name,"
				+ "          e.longname,"
				+ "          e.description,"
				+ "          e.connection,"
				+ "          e.author,"
				+ "          e.creation_date,"
				+ "          e.revision,"
				+ "          e.comments,"
				+ "          e.fullname,"
				+ "          e.update_date)";
						
		PreparedStatement prep = conn.prepareStatement(mergeSql);
		prep.setString(1, name);
		prep.setString(2, longname);
		prep.setString(3, description);
		prep.setString(4, connection);
		prep.setString(5, author);
		prep.setString(6, creation);
		prep.setString(7, revision);
		prep.setString(8, comments);
		prep.setString(9, fullname);
		prep.setString(10, getCurrentDate());
		ret_code = prep.execute();
		
		prep.close();
		
		return ret_code;
	}
	
	public boolean insertTable(String universe, String name, String weight, 
			String alias_flag, String alias_table) throws Exception
	{
		boolean ret_code = false;
		
		String mergeSql = "MERGE INTO admin.boxi_tables b"
				+ "   USING ("
				+ "    SELECT ? universe,"
				+ "         ? name,"
				+ "         ? weight,"
				+ "         ? alias_flag,"
				+ "         ? alias_table,"
				+ "         to_date(?,\'dd.mm.yyyy\') update_date"
				+ "    FROM dual) e"
				+ "   ON (b.universe=e.universe and b.name = e.name)"
				+ "   WHEN MATCHED THEN"
				+ "   UPDATE SET b.weight=e.weight,"
				+ "         b.alias_flag=e.alias_flag,"
				+ "         b.alias_table=e.alias_table,"
				+ "         b.update_date=e.update_date"
				+ "   WHEN NOT MATCHED THEN"
				+ "    INSERT (b.universe,"
				+ "          b.name,"
				+ "          b.weight,"
				+ "          b.alias_flag,"
				+ "          b.alias_table,"
				+ "          b.update_date"
				+ "         )"
				+ "    VALUES (e.universe,"
				+ "          e.name,"
				+ "          e.weight,"
				+ "          e.alias_flag,"
				+ "          e.alias_table,"
				+ "          e.update_date)";
						
		PreparedStatement prep = conn.prepareStatement(mergeSql);
		prep.setString(1, universe);
		prep.setString(2, name);
		prep.setString(3, weight);
		prep.setString(4, alias_flag);
		prep.setString(5, alias_table);
		prep.setString(6, getCurrentDate());
		ret_code = prep.execute();
		
		prep.close();
		
		return ret_code;
	}
	
	public boolean insertObjects(String universe, String parent, String name,  
			String description, String show_flag, String stmt_select, String stmt_where,
			String stmt_format, String lov_refresh_flag, String lov_export_flag,
			String lov_flag) throws Exception
	{
		boolean ret_code = false;
		
		String mergeSql = "MERGE INTO admin.boxi_objects b"
				+ "   USING ("
				+ "    SELECT ? universe,"
				+ "         ? parent,"
				+ "         ? name,"
				+ "         ? description,"
				+ "         ? show_flag,"
				+ "         ? stmt_select,"
				+ "         ? stmt_where,"
				+ "         ? stmt_format,"
				+ "         ? lov_refresh_flag,"
				+ "         ? lov_export_flag,"
				+ "         ? lov_flag,"
				+ "         to_date(?,\'dd.mm.yyyy\') update_date"
				+ "    FROM dual) e"
				+ "   ON (b.universe=e.universe and b.parent=e.parent and b.name = e.name)"
				+ "   WHEN MATCHED THEN"
				+ "   UPDATE SET b.description=e.description,"
				+ "         b.show_flag=e.show_flag,"
				+ "         b.stmt_select=e.stmt_select,"
				+ "         b.stmt_where=e.stmt_where,"
				+ "         b.stmt_format=e.stmt_format,"
				+ "         b.lov_refresh_flag=e.lov_refresh_flag,"
				+ "         b.lov_export_flag=e.lov_export_flag,"
				+ "         b.lov_flag=e.lov_flag,"
				+ "         b.update_date=e.update_date"
				+ "   WHEN NOT MATCHED THEN"
				+ "    INSERT (b.universe,"
				+ "          b.parent,"
				+ "          b.name,"
				+ "          b.description,"
				+ "          b.show_flag,"
				+ "          b.stmt_select,"
				+ "          b.stmt_where,"
				+ "          b.stmt_format,"
				+ "          b.lov_refresh_flag,"
				+ "          b.lov_export_flag,"
				+ "          b.lov_flag,"
				+ "          b.update_date"
				+ "         )"
				+ "    VALUES (e.universe,"
				+ "          e.parent,"
				+ "          e.name,"
				+ "          e.description,"
				+ "          e.show_flag,"
				+ "          e.stmt_select,"
				+ "          e.stmt_where,"
				+ "          e.stmt_format,"
				+ "          e.lov_refresh_flag,"
				+ "          e.lov_export_flag,"
				+ "          e.lov_flag,"
				+ "          e.update_date)";
						
		PreparedStatement prep = conn.prepareStatement(mergeSql);
		prep.setString(1, universe);
		prep.setString(2, parent);
		prep.setString(3, name);
		prep.setString(4, description);
		prep.setString(5, show_flag);
		prep.setString(6, stmt_select);
		prep.setString(7, stmt_where);
		prep.setString(8, stmt_format);
		prep.setString(9, lov_refresh_flag);
		prep.setString(10, lov_export_flag);
		prep.setString(11, lov_flag);
		prep.setString(12, getCurrentDate());
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
