import org.sqlite.SQLiteException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.text.*;
import java.sql.*;
import java.util.Date;

public class WebReader {

	static Connection c = null;
	static Statement stmt = null;
	
	public static void parseWebPages(Document doc, int i) {
		String fullTitle = doc.title().replace("'", "");
		String titleID = fullTitle.substring(0, fullTitle.length() - " - Canadian Food Inspection Agency".length());
		//System.out.println("Processing " + titleID);
		Elements recallSummary = doc.getElementsByClass("mrgn-bttm-sm");
		Elements recallProduct = doc.getElementsByTag("th");
		try {
			String date = recallSummary.first().text().replace("'", "");
			recallSummary.remove(0);
			String reason = recallSummary.first().text().replace("'", "");
			recallSummary.remove(0);
			String classification = recallSummary.first().text().replace("'", "");
			recallSummary.remove(0);
			String firm = recallSummary.first().text().replace("'", "");
			recallSummary.remove(0);
			String distribution = recallSummary.first().text().replace("'", "");
			recallSummary.remove(0);
			String extentDistribution = recallSummary.first().text().replace("'", "");
			recallSummary.remove(0);
			String referenceNumber;
			if(recallSummary.size() > 0) {
				referenceNumber = recallSummary.first().text().replace("'", "");
				recallSummary.remove(0);
			} else {
				referenceNumber = "N\\A";
			}
			String company = recallProduct.get(5).text().toString().replace("'", "");
			recallProduct = doc.getElementsByTag("td");
			String product = recallProduct.first().text().replace("'", "");
			recallProduct.remove(0);
			String size = recallProduct.first().text().replace("'", "");
			recallProduct.remove(0);
			String code = recallProduct.first().text().replace("'", "");
			recallProduct.remove(0);
			String upc = recallProduct.first().text().replace("'", "");
			recallProduct.remove(0);

			String[] details = {titleID, date, reason, classification, firm,
				distribution, extentDistribution, referenceNumber, 
				company, product, size, code, upc};
			populateDB(details, i);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Problem encountered with " + titleID + ". Not enough data, skipping entry");
			System.out.println("recallSummary = " + recallSummary);
			System.out.println("recallProduct = " + recallProduct);
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		} catch (Exception e) {
			System.out.println("Problem encountered with " + titleID + ". Unknown error, skipping entry");
			System.out.println("recallSummary = " + recallSummary);
			System.out.println("recallProduct = " + recallProduct);
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public static void populateDB(String[] details, int i) {
		//System.out.println("Attempting to populate database");
		try {
			String dateStr = details[1];
			String fromFormat = "MMMM dd, yyyy";
			String toFormat = "yy-MM-dd";
			
			DateFormat fromFormatter = new SimpleDateFormat(fromFormat);
			Date formatDate = (Date) fromFormatter.parse(dateStr);
			
	        DateFormat toformatter = new SimpleDateFormat(toFormat);
	        String shortDate = toformatter.format(formatDate);
	        
			String ID = shortDate + "_" + details[0];
			ID = ID.replace(" ", "");
			if(ID.length() > 50) {
				ID = ID.substring(0,  50);
			}
			String sql = "INSERT INTO RECALL " +
						"(ID, TITLE, DATE, REASON, CLASSIFICATION, FIRM, DISTRIBUTION, EXTENT, REFERENCE, COMPANY, PRODUCT, SIZE, CODE, UPC)" +
						"VALUES " +
						"('" + ID + "', '" + details[0] + "', '" + details[1] + 
						"', '" + details[2] + "', '" + details[3] + 
						"', '" + details[4] + "', '" + details[5] + 
						"', '" + details[6] + 
						"', '" + details[7] + "', '" + details[8] + 
						"', '" + details[9] + "', '" + details[10] + 
						"', '" + details[11] + "', '" + details[12] + "');";
			stmt.executeUpdate(sql);
		} catch (SQLiteException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		//System.out.println("Database entry " + i + " populated.");
	}
	
	public static void readWebsite() {
		try {
			System.out.println("Reading website...");
			//Document doc = Jsoup.connect("http://www.inspection.gc.ca/about-the-cfia/newsroom/food-recall-warnings/complete-listing/eng/1351519587174/1351519588221?ay=0&fr=0&fc=0&fd=0&ft=2").get();
			//Document doc = Jsoup.connect("http://www.inspection.gc.ca/about-the-cfia/newsroom/food-recall-warnings/complete-listing/eng/1351519587174/1351519588221?ay=2017&fr=0&fc=0&fd=0&ft=2").get();
			//Document doc = Jsoup.connect("http://www.inspection.gc.ca/about-the-cfia/newsroom/food-recall-warnings/complete-listing/eng/1351519587174/1351519588221?ay=2016&fr=0&fc=0&fd=0&ft=2").get();
			//Document doc = Jsoup.connect("http://www.inspection.gc.ca/about-the-cfia/newsroom/food-recall-warnings/complete-listing/eng/1351519587174/1351519588221?ay=2015&fr=0&fc=0&fd=0&ft=2").get();
			//Document doc = Jsoup.connect("http://www.inspection.gc.ca/about-the-cfia/newsroom/food-recall-warnings/complete-listing/eng/1351519587174/1351519588221?ay=2014&fr=0&fc=0&fd=0&ft=2").get();
			//Document doc = Jsoup.connect("http://www.inspection.gc.ca/about-the-cfia/newsroom/food-recall-warnings/complete-listing/eng/1351519587174/1351519588221?ay=2013&fr=0&fc=0&fd=0&ft=2").get();
			Document doc = Jsoup.connect("http://www.inspection.gc.ca/about-the-cfia/newsroom/food-recall-warnings/complete-listing/eng/1351519587174/1351519588221?ay=2012&fr=0&fc=0&fd=0&ft=2").get();
			
			Elements recalls = doc.select("TR").select("TD").select("A");

			System.out.println("Found " + recalls.size() + " recalls.");
			for(int i = 0; i < recalls.size(); i++) {
				Element recall = recalls.get(i);
				String href = recall.toString().substring(9, recall.toString().length() - 8);
				int hrefEnd = href.indexOf('"');
				href = "http://www.inspection.gc.ca" + href.substring(0,hrefEnd);
				Document link = Jsoup.connect(href).get();
				parseWebPages(link, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public static void main(String[] args) {
		try {
			Class.forName("org.sqlite.JDBC");
		    c = DriverManager.getConnection("jdbc:sqlite:CFIArecalls.db");
		    //System.out.println("Opened database successfully");
		    
			stmt = c.createStatement();
			String sql;
			//System.out.println("Dropping existing table");
			//sql = "DROP TABLE RECALL"; 
			//stmt.executeUpdate(sql);
			
			//System.out.println("Creating table");
			sql = "CREATE TABLE IF NOT EXISTS RECALL " +
					"(ID				VARCHAR(50) PRIMARY KEY     NOT NULL," +
					"TITLE				TEXT	NOT NULL," +
					" DATE				TEXT," +
					" REASON			TEXT," +
					" CLASSIFICATION	TEXT," +
					" FIRM				TEXT," +
					" DISTRIBUTION		TEXT," +
					" EXTENT			TEXT," +
					" REFERENCE			TEXT," +
					" COMPANY			TEXT," +
					" PRODUCT			TEXT," +
					" SIZE				TEXT," +
					" CODE				TEXT," +
					" UPC				TEXT)";
			stmt.executeUpdate(sql);
			//System.out.println("Created table successfully");
			readWebsite();
			stmt.close();
			c.close();
			System.out.println("Finished");
		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

}
