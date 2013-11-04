import java.awt.Polygon;
import java.sql.ResultSet;
import java.util.ArrayList;


public abstract class Models {
	public static abstract class Model {
	}
	
	public static class AnnouncementSystem extends Model {
		public String id;
		public int[] center;
		public int radius;
		
		@Override
		public String toString() {
			return "[AS] id=" + id + ", center=[" + center[0] + "," + center[1] + "], radius=" + radius;
		}
		
		private static AnnouncementSystem create(ResultSet queryResult) {
			try {
				AnnouncementSystem AS = new AnnouncementSystem();
				AS.id = queryResult.getString("AS_ID");
				AS.center = Geom.readPoint(queryResult.getObject("CENTER"));
				AS.radius = queryResult.getInt("RADIUS");
				return AS;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static ArrayList<AnnouncementSystem> find(String sql) {
			ArrayList<AnnouncementSystem> result = new ArrayList<AnnouncementSystem>();
			try {
				ResultSet queryResult = DB.statement.executeQuery(sql);
				while (queryResult.next()) {
					result.add(AnnouncementSystem.create(queryResult));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
	public static class Building extends Model {
		public String id;
		public String name;
		public Polygon outline;
		
		@Override
		public String toString() {
			return "[Building] id=" + id + ", name=" + name + ", outline=[" + outline + "]";
		}
		
		private static Building create(ResultSet queryResult) {
			try {
				Building building = new Building();
				building.id = queryResult.getString("BUILDING_ID");
				building.name = queryResult.getString("NAME");
				
				int[] points = Geom.readPolygon(queryResult.getObject("OUTLINE"));
				int npoints = points.length / 2;
				int[] xpoints = new int[npoints];
				int[] ypoints = new int[npoints];
				for (int i = 0; i < npoints; i++) {
					xpoints[i] = points[i * 2];
					ypoints[i] = points[i * 2 + 1];
				}
				building.outline = new Polygon(xpoints, ypoints, npoints);
				
				return building;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static ArrayList<Building> find(String sql) {
			ArrayList<Building> result = new ArrayList<Building>();
			try {
				ResultSet queryResult = DB.statement.executeQuery(sql);
				while (queryResult.next()) {
					result.add(Building.create(queryResult));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
	public static class Student extends Model {
		public String id;
		public int[] pos;
		
		@Override
		public String toString() {
			return "[Student] id=" + id + ", pos=[" + pos[0] + "," + pos[1] + "]";
		}

		private static Student create(ResultSet queryResult) {
			try {
				Student student = new Student();
				student.id = queryResult.getString("STUDENT_ID");
				student.pos = Geom.readPoint(queryResult.getObject("LOCATION"));
				return student;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static ArrayList<Student> find(String sql) {
			ArrayList<Student> result = new ArrayList<Student>();
			try {
				ResultSet queryResult = DB.statement.executeQuery(sql);
				while (queryResult.next()) {
					result.add(Student.create(queryResult));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}
}
