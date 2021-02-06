import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import com.opencsv.CSVWriter;

import java.sql.Statement;
import java.sql.Timestamp;

public class TimerTesting extends java.util.TimerTask {
	static Connection con;
	static Statement stmt;
	static ResultSet rs;

	static String pathToCSV = "studentlist.csv";
	static PreparedStatement statement;
	static String sql;

	public static void main(String[] args) throws Exception {

		Class.forName("com.mysql.jdbc.Driver");

		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/classonedata", "root", "mecks@123");

		sql = "INSERT INTO csvtesting (course_name, student_name, timestamp, rating, comment) VALUES (?, ?, ?, ?, ?)";
		statement = con.prepareStatement(sql);

		Timer timer = new Timer(); // Instantiates a timer to schedule tasks

		TimerTesting task1 = new TimerTesting(); // Task 1 Instantiation

		timer.schedule(task1, 86400000); // Schedules task 1 for execution after the specified delay of 24 hours

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date date = sdf.parse("04-09-2019 01:36:00");

		TimerTesting task2 = new TimerTesting(); // Task 2 Instantiation

		timer.schedule(task2, date); // Schedules task 2 for execution at the particular time defined by date

		// timer.cancel(); // Terminates the Timer and cancels all the scheduled tasks
	}

	@Override
	public void run() {
		System.out.println("Timer task executed :: " + new Date() + " :: " + Thread.currentThread().getName());

		//Getting the data and writing it to csv file
		File file = new File(pathToCSV);
		FileWriter outFileWriter = null;
		try {
			outFileWriter = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CSVWriter writer = new CSVWriter(outFileWriter);
		String[] header = {"Course Name","Student Name","Timestamp","Rating","Comment" };
		writer.writeNext(header);

		// add data to csv
		String[] data1 = {"Java Servlet JSP and Hibernate12345","Praveen Gurram12345","2019-07-31 19:10:12","5.0","excellent teaching12345"};
		writer.writeNext(data1);
		String[] data2 = {"Java Servlet JSP and Hibernate125","Praveen Gurram125","2019-07-31 19:10:12","4.0","excellent teaching125"};
		writer.writeNext(data2);
		try {
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//Writing to mysql database using JDBC
		try {
			BufferedReader lineReader = new BufferedReader(new FileReader(pathToCSV));
			String lineText = null;
			int count = 0;
			int batchSize = 20;
			lineReader.readLine();
			while ((lineText = lineReader.readLine()) != null) {
				String[] data = lineText.split(",");
				String course_name = data[0];
				String student_name = data[1];
				String timestamp = "2019-07-31 19:10:12";
				String rating = "5.0";
				String comment = data.length == 5 ? data[4] : "";

				statement.setString(1, course_name);
				statement.setString(2, student_name);

				Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);

				statement.setTimestamp(3, sqlTimestamp);

				Float fRating = Float.parseFloat(rating);
				statement.setFloat(4, fRating);

				statement.setString(5, comment);

				statement.addBatch();

				if (count % batchSize == 0) {
					statement.executeBatch();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
