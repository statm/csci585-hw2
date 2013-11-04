import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class hw2 {
	private MainWindow window;

	private void go() {
		window = new MainWindow();
		window.setVisible(true);

		DB.init();

		window.addUIEventListener(new MainWindow.UIEventListener() {
			@Override
			public void onUIEvent(MainWindow.UIEvent event) {
				if (event.getType() == MainWindow.UIEventType.SUBMIT) {
					switch (window.getUIState().getQueryType()) {
						case WHOLE_REGION:
							queryWholeRegion(window.getUIState().getActiveFeatureTypes());
							break;
						case POINT_QUERY:
							queryPointRange(window.getUIState().getActiveFeatureTypes());
							break;
						case RANGE_QUERY:
							queryPolygonRange(window.getUIState().getActiveFeatureTypes());
							break;
						case SURROUNDING_STUDENT:
							querySurroundingStudents();
							break;
						case EMERGENCY_QUERY:
							queryEmergencyAS();
							break;
					}
				} else if (event.getType() == MainWindow.UIEventType.CHANGE_OPTION) {
					window.clearResultLayer();
					window.clearInteractiveLayer();
				} else if (event.getType() == MainWindow.UIEventType.UPDATE) {
					switch (window.getUIState().getQueryType()) {
						case SURROUNDING_STUDENT:
						case EMERGENCY_QUERY:
							window.clearResultLayer();
							queryNearestAS();
							break;
						case RANGE_QUERY:
						case WHOLE_REGION:
						case POINT_QUERY:
						default:
							break;
					}
				}
			}
		});
	}

	private void queryWholeRegion(ArrayList<Enums.ActiveFeatureType> activeFeatureTypes) {
		ArrayList<Models.AnnouncementSystem> announcementSystems = new ArrayList<Models.AnnouncementSystem>();
		ArrayList<Models.Building> buildings = new ArrayList<Models.Building>();
		ArrayList<Models.Student> students = new ArrayList<Models.Student>();

		window.pushLog();
		
		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.AS)) {
			String sql = "SELECT * FROM ANNSYS";
			announcementSystems = Models.AnnouncementSystem.find(sql);
			window.pushLog(sql);
		}

		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.BUILDING)) {
			String sql = "SELECT * FROM BUILDINGS";
			buildings = Models.Building.find(sql);
			window.pushLog(sql);
		}

		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.STUDENTS)) {
			String sql = "SELECT * FROM STUDENTS";
			students = Models.Student.find(sql);
			window.pushLog(sql);
		}
		
		window.drawResult(announcementSystems, buildings, students);
	}

	private void queryPointRange(ArrayList<Enums.ActiveFeatureType> activeFeatureTypes) {
		ArrayList<Models.AnnouncementSystem> announcementSystems = new ArrayList<Models.AnnouncementSystem>();
		ArrayList<Models.Building> buildings = new ArrayList<Models.Building>();
		ArrayList<Models.Student> students = new ArrayList<Models.Student>();
		ArrayList<Color> ASColors = new ArrayList<Color>();
		ArrayList<Color> buildingColors = new ArrayList<Color>();
		ArrayList<Color> studentColors = new ArrayList<Color>();

		int[] mark = window.getMark();

		if (mark[0] < 0 || mark[1] < 0) {
			window.clearResultLayer();
			return;
		}
		
		window.pushLog();

		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.AS)) {
			String sql = "SELECT * FROM ANNSYS a "
				+ "WHERE SDO_WITHIN_DISTANCE(a.center, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
				+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 'distance='||TO_CHAR(a.radius + 50)) = 'TRUE' "
				+ "ORDER BY SDO_GEOM.SDO_DISTANCE(a.area, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
				+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 1)";
			announcementSystems = Models.AnnouncementSystem.find(sql);
			window.pushLog(sql);
		}

		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.BUILDING)) {
			String sql = "SELECT * FROM BUILDINGS b "
				+ "WHERE SDO_WITHIN_DISTANCE(b.outline, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
				+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 'distance=50') = 'TRUE' "
				+ "ORDER BY SDO_GEOM.SDO_DISTANCE(b.outline,  SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
				+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 1)";
			buildings = Models.Building.find(sql);
			window.pushLog(sql);
		}

		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.STUDENTS)) {
			String sql = "SELECT * FROM STUDENTS s "
				+ "WHERE SDO_WITHIN_DISTANCE(s.location, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
				+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 'distance=50') = 'TRUE' "
				+ "ORDER BY SDO_GEOM.SDO_DISTANCE(s.location,  SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
				+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 1)";
			students = Models.Student.find(sql);
			window.pushLog(sql);
		}

		for (int i = 0; i < announcementSystems.size(); i++) {
			ASColors.add(i == 0 ? Color.YELLOW : Color.GREEN);
		}

		for (int i = 0; i < buildings.size(); i++) {
			buildingColors.add(i == 0 ? Color.YELLOW : Color.GREEN);
		}

		for (int i = 0; i < students.size(); i++) {
			studentColors.add(i == 0 ? Color.YELLOW : Color.GREEN);
		}

		window.drawResult(announcementSystems, buildings, students, ASColors, buildingColors, studentColors);
	}
	
	private void queryPolygonRange(ArrayList<Enums.ActiveFeatureType> activeFeatureTypes) {
		ArrayList<Models.AnnouncementSystem> announcementSystems = new ArrayList<Models.AnnouncementSystem>();
		ArrayList<Models.Building> buildings = new ArrayList<Models.Building>();
		ArrayList<Models.Student> students = new ArrayList<Models.Student>();
		
		window.pushLog();
		
		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.AS)) {
			String sql = "SELECT * FROM ANNSYS a "
				+ "WHERE SDO_ANYINTERACT(a.area, SDO_GEOMETRY(2003, NULL, NULL, "
				+ "SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + window.getPolygonOutline() + "))) = 'TRUE'";
			announcementSystems = Models.AnnouncementSystem.find(sql);
			window.pushLog(sql);
		}
		
		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.BUILDING)) {
			String sql = "SELECT * FROM BUILDINGS b "
				+ "WHERE SDO_ANYINTERACT(b.outline, SDO_GEOMETRY(2003, NULL, NULL, "
				+ "SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + window.getPolygonOutline() + "))) = 'TRUE'";
			buildings = Models.Building.find(sql);
			window.pushLog(sql);
		}

		if (activeFeatureTypes.contains(Enums.ActiveFeatureType.STUDENTS)) {
			String sql = "SELECT * FROM STUDENTS s "
				+ "WHERE SDO_INSIDE(s.location, SDO_GEOMETRY(2003, NULL, NULL, "
				+ "SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + window.getPolygonOutline() + "))) = 'TRUE'";
			students = Models.Student.find(sql);
			window.pushLog(sql);
		}

		window.drawResult(announcementSystems, buildings, students);
	}

	private void queryNearestAS() {
		ArrayList<Models.AnnouncementSystem> announcementSystems = new ArrayList<Models.AnnouncementSystem>();

		int[] mark = window.getMark();

		if (mark[0] < 0 || mark[1] < 0) {
			return;
		}
		
		window.pushLog();

		String sql = "SELECT * FROM ANNSYS a "
			+ "WHERE SDO_NN(a.center, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
			+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE'";
		announcementSystems = Models.AnnouncementSystem.find(sql);
		window.pushLog(sql);

		window.drawResult(announcementSystems, null, null);
	}

	private void querySurroundingStudents() {
		ArrayList<Models.AnnouncementSystem> announcementSystems = new ArrayList<Models.AnnouncementSystem>();
		ArrayList<Models.Student> students = new ArrayList<Models.Student>();

		int[] mark = window.getMark();

		if (mark[0] < 0 || mark[1] < 0) {
			return;
		}
		
		window.pushLog();

		String sql = "SELECT * FROM ANNSYS a "
			+ "WHERE SDO_NN(a.center, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
			+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE'";
		announcementSystems = Models.AnnouncementSystem.find(sql);
		window.pushLog(sql);

		sql = "SELECT * FROM STUDENTS s, ANNSYS a "
			+ "WHERE SDO_NN(a.center, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
			+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE' "
			+ "AND SDO_WITHIN_DISTANCE(s.location, a.center, 'distance='||TO_CHAR(a.radius)) = 'TRUE'";
		students = Models.Student.find(sql);
		window.pushLog(sql);

		window.drawResult(announcementSystems, null, students);
	}

	private static final Color[] AS_COLOR_TABLE = { Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA };

	private void queryEmergencyAS() {
		Models.AnnouncementSystem nearestAS;
		ArrayList<Models.AnnouncementSystem> announcementSystems;
		ArrayList<Models.Student> students;
		HashMap<String, Color> ASColorMap = new HashMap<String, Color>();
		ArrayList<Color> ASColors = new ArrayList<Color>();
		ArrayList<Color> studentColors = new ArrayList<Color>();

		int[] mark = window.getMark();

		if (mark[0] < 0 || mark[1] < 0) {
			return;
		}
		
		window.pushLog();

		String sql = "SELECT * FROM ANNSYS a "
			+ "WHERE SDO_NN(a.center, SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE("
			+ mark[0] + ", " + mark[1] + ", NULL), NULL, NULL), 'sdo_num_res=1') = 'TRUE'";
		nearestAS = Models.AnnouncementSystem.find(sql).get(0);

		sql = "SELECT a2.* FROM STUDENTS s, ANNSYS a1, ANNSYS a2 "
			+ "WHERE a1.AS_ID = '" + nearestAS.id + "' "
			+ "AND SDO_WITHIN_DISTANCE(s.location, a1.center, 'distance='||TO_CHAR(a1.radius)) = 'TRUE' "
			+ "AND a2.AS_ID <> a1.AS_ID "
			+ "AND SDO_NN(a2.area, s.location, 'sdo_num_res=2') = 'TRUE'";
		announcementSystems = Models.AnnouncementSystem.find(sql);
		window.pushLog(sql);

		sql = "SELECT s.* FROM STUDENTS s, ANNSYS a1, ANNSYS a2 "
			+ "WHERE a1.AS_ID = '" + nearestAS.id + "' "
			+ "AND SDO_WITHIN_DISTANCE(s.location, a1.center, 'distance='||TO_CHAR(a1.radius)) = 'TRUE' "
			+ "AND a2.AS_ID <> a1.AS_ID "
			+ "AND SDO_NN(a2.area, s.location, 'sdo_num_res=2') = 'TRUE'";
		students = Models.Student.find(sql);
		window.pushLog(sql);

		for (int i = 0; i < announcementSystems.size(); i++) {
			Models.AnnouncementSystem AS = announcementSystems.get(i);

			if (!ASColorMap.containsKey(AS.id)) {
				ASColorMap.put(AS.id, AS_COLOR_TABLE[ASColorMap.size() % 4]);
			}

			ASColors.add(ASColorMap.get(AS.id));
			studentColors.add(ASColorMap.get(AS.id));
		}

		window.drawResult(announcementSystems, null, students, ASColors, null, studentColors);
	}

	// program entrance
	public static void main(String[] args) {
		new hw2().go();
	}
}
