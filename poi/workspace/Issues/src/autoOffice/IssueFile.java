package autoOffice;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.*;


public class IssueFile {

	/**
	 * @param args
	 */
	
	private String IssueFile;
	private static String TemplateDir = "Y:/CTO/IT/BI/05Public/_PM/020 Issues & Risks/Demandrelated Issues & Logs";
	private static String TemplateFile = TemplateDir + "/DXXXXX_Logs_TEMPLATE.xlsm";
	
	public IssueFile (String FileName){
		// do nothing now
		IssueFile = FileName;
	}
	
	public void readIssues () throws FileNotFoundException, Exception{
	    InputStream inp = new FileInputStream(IssueFile);

	    Workbook wb = WorkbookFactory.create(inp);

	    Sheet sheet = wb.getSheetAt(0);
	    
	    //Row row = sheet.getRow(14);
	    
	    Row row = sheet.getRow(10);
	    Cell cell = row.getCell(0);
	    if (cell == null)
	        cell = row.createCell(3);
	    cell.setCellType(Cell.CELL_TYPE_STRING);
	    cell.setCellValue("Das ist eine neuer Versuch - Eintrage");

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream(IssueFile);
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	public void findIssueFiles() {
		File dir = new File(TemplateDir);

		File[] matches = dir.listFiles(new FilenameFilter()
		{
		  public boolean accept(File dir, String name)
		  {
		     return name.startsWith("temp") && name.endsWith(".txt");
		  }
		});
	}
	
	public void createIssues() throws Exception{
		InputStream inp = new FileInputStream(TemplateFile);

	    Workbook wb = WorkbookFactory.create(inp);
	    // set formula recalculation to true
	    wb.setForceFormulaRecalculation(true);

	    Sheet sheet = wb.getSheetAt(0);
	    
	    // Delete sample rows in template
	    sheet.shiftRows(11, 15, -1);
	    sheet.shiftRows(11, 14, -1);
	    sheet.shiftRows(11, 13, -1);
	    sheet.shiftRows(11, 12, -1);
	    sheet.shiftRows(11, 11, -1);
	    
	    Row row = sheet.getRow(10);
	    Cell cell = row.getCell(3);
	    if (cell == null)
	        cell = row.createCell(3);
	    cell.setCellType(Cell.CELL_TYPE_STRING);
	    cell.setCellValue("Das ist eine nexter TEST Eintrage");
	    ((sheet.getRow(0)).getCell(2)).setCellValue("D123456789");
	    ((sheet.getRow(1)).getCell(2)).setCellValue("My Dem Title");
	    ((sheet.getRow(3)).getCell(2)).setCellValue("Dem Owner");
	    ((sheet.getRow(4)).getCell(2)).setCellValue("BA An");
	    ((sheet.getRow(5)).getCell(2)).setCellValue("SUP An");

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream(IssueFile);
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		IssueFile myFile = new IssueFile("Y:/CTO/IT/BI/05Public/_PM/020 Issues & Risks/Demandrelated Issues & Logs/abcdefg.xlsm");
		
		System.out.println("Starting fileread");
		try{
			myFile.readIssues();
		} catch (FileNotFoundException fe){
			try{
				myFile.createIssues();
			} catch (Exception es){
				System.out.println("File Creation was not successful");
				es.printStackTrace();				
			}
		
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("End fileread");

	}

}
