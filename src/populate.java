import java.io.FileReader;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class populate {
	private static PreparedStatement insertBuildingStmt;
	private static PreparedStatement insertStudentStmt;
	private static PreparedStatement insertASStmt;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Error: 3 params are required for input.");
			return;
		}

		DB.init();
		prepareStatements();
		clearTables();
		loadFiles(args);

		System.exit(0);
	}
	
	private static void prepareStatements() {
		insertBuildingStmt = DB.prepareStatement("INSERT INTO BUILDINGS VALUES (?, ?, ?)");
		insertStudentStmt = DB.prepareStatement("INSERT INTO STUDENTS VALUES (?, ?)");
		insertASStmt = DB.prepareStatement("INSERT INTO ANNSYS VALUES (?, ?, ?, ?)");
	}
	
	private static void clearTables() {
		try {
			DB.statement.execute("DELETE FROM BUILDINGS");
			DB.statement.execute("DELETE FROM STUDENTS");
			DB.statement.execute("DELETE FROM ANNSYS");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadFiles(String[] args) {
		try {
			Scanner in;

			// buildings
			in = new Scanner(new FileReader(args[0]));
			while (in.hasNextLine()) {
				String line = in.nextLine();
				parseBuilding(line);
			}
			in.close();

			// students
			in = new Scanner(new FileReader(args[1]));
			while (in.hasNextLine()) {
				String line = in.nextLine();
				parseStudent(line);
			}
			in.close();

			// announcementsSystems
			in = new Scanner(new FileReader(args[2]));
			while (in.hasNextLine()) {
				String line = in.nextLine();
				parseAS(line);
			}
			in.close();
		} catch (Exception e) {
			System.out.println("Error while loading files: " + e.toString());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static void parseBuilding(String line) {
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter(", ");
		
		String id = scanner.next();
		String name = scanner.next();
		int pointCount = scanner.nextInt();
		double[] pointArray = new double[pointCount * 2];
		for (int i = 0; i < pointCount; i++) {
			pointArray[i * 2] = scanner.nextDouble();
			pointArray[i * 2 + 1] = scanner.nextDouble();
		}
		
		try {
			insertBuildingStmt.setString(1, id);
			insertBuildingStmt.setString(2, name);
			insertBuildingStmt.setObject(3, Geom.createPolygon(pointArray));
			insertBuildingStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scanner.close();
	}

	private static void parseStudent(String line) {
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter(", ");
		
		String id = scanner.next();
		double x = scanner.nextDouble();
		double y = scanner.nextDouble();
		
		try {
			insertStudentStmt.setString(1, id);
			insertStudentStmt.setObject(2, Geom.createPoint(x, y));
			insertStudentStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scanner.close();
	}

	private static void parseAS(String line) {
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter(", ");
		
		String id = scanner.next();
		double x = scanner.nextDouble();
		double y = scanner.nextDouble();
		double r = scanner.nextDouble();
		
		try {
			insertASStmt.setString(1, id);
			insertASStmt.setObject(2, Geom.createPoint(x, y));
			insertASStmt.setDouble(3, r);
			insertASStmt.setObject(4, Geom.createCircle(x, y, r));
			insertASStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scanner.close();
	}
}
