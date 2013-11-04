import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;
import javax.swing.text.DefaultCaret;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	public static final int MAP_WIDTH = 820;
	public static final int MAP_HEIGHT = 580;

	private MapLayer mapLayer;
	private ResultLayer resultLayer;
	private InteractiveLayer interactiveLayer;

	private JLabel lblActiveFeatureType;
	private JLabel lblQuery;

	private JCheckBox cbAS;
	private JCheckBox cbBuilding;
	private JCheckBox cbStudents;

	private ButtonGroup bgpQuery;
	private JRadioButton rbWhole;
	private JRadioButton rbPoint;
	private JRadioButton rbRange;
	private JRadioButton rbSurrounding;
	private JRadioButton rbEmergency;

	private JButton btnSubmitQuery;

	private JScrollPane spQueryLog;
	private JTextArea taQueryLog;

	public MainWindow() {
		super("CSCI 585 HW#2 by Renjie Zhao (2916391724)");

		this.setLayout(null);
		this.setResizable(false);
		this.setBounds(50, 50, 1080, 860);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		initUI();
		initListeners();
	}

	private void initUI() {
		mapLayer = new MapLayer();
		mapLayer.setBounds(10, 10, MAP_WIDTH, MAP_HEIGHT);

		resultLayer = new ResultLayer();
		resultLayer.setBounds(10, 10, MAP_WIDTH, MAP_HEIGHT);

		interactiveLayer = new InteractiveLayer();
		interactiveLayer.setBounds(10, 10, MAP_WIDTH, MAP_HEIGHT);

		this.add(interactiveLayer);
		this.add(resultLayer);
		this.add(mapLayer);

		Font titleFont = new Font(null, Font.PLAIN, 24);
		Font contentFont = new Font(null, Font.PLAIN, 20);

		lblActiveFeatureType = new JLabel("Active Feature Type");
		lblActiveFeatureType.setLocation(MAP_WIDTH + 20, 10);
		lblActiveFeatureType.setFont(titleFont);
		lblActiveFeatureType.setSize(lblActiveFeatureType.getPreferredSize());
		this.add(lblActiveFeatureType);

		cbAS = new JCheckBox("AS");
		cbAS.setLocation(MAP_WIDTH + 17, 50);
		cbAS.setFont(contentFont);
		cbAS.setSize(cbAS.getPreferredSize());
		cbAS.setFocusable(false);
		cbAS.setSelected(true);
		this.add(cbAS);

		cbBuilding = new JCheckBox("Building");
		cbBuilding.setLocation(MAP_WIDTH + 17, 80);
		cbBuilding.setFont(contentFont);
		cbBuilding.setSize(cbBuilding.getPreferredSize());
		cbBuilding.setFocusable(false);
		cbBuilding.setSelected(true);
		this.add(cbBuilding);

		cbStudents = new JCheckBox("Students");
		cbStudents.setLocation(MAP_WIDTH + 17, 110);
		cbStudents.setFont(contentFont);
		cbStudents.setSize(cbStudents.getPreferredSize());
		cbStudents.setFocusable(false);
		cbStudents.setSelected(true);
		this.add(cbStudents);

		lblQuery = new JLabel("Query");
		lblQuery.setLocation(MAP_WIDTH + 20, 190);
		lblQuery.setFont(titleFont);
		lblQuery.setSize(lblQuery.getPreferredSize());
		this.add(lblQuery);

		bgpQuery = new ButtonGroup();

		rbWhole = new JRadioButton("Whole Region");
		rbWhole.setLocation(MAP_WIDTH + 17, 230);
		rbWhole.setFont(contentFont);
		rbWhole.setSize(rbWhole.getPreferredSize());
		rbWhole.setFocusable(false);
		rbWhole.setSelected(true);
		bgpQuery.add(rbWhole);
		this.add(rbWhole);

		rbPoint = new JRadioButton("Point Query");
		rbPoint.setLocation(MAP_WIDTH + 17, 260);
		rbPoint.setFont(contentFont);
		rbPoint.setSize(rbPoint.getPreferredSize());
		rbPoint.setFocusable(false);
		bgpQuery.add(rbPoint);
		this.add(rbPoint);

		rbRange = new JRadioButton("Range Query");
		rbRange.setLocation(MAP_WIDTH + 17, 290);
		rbRange.setFont(contentFont);
		rbRange.setSize(rbRange.getPreferredSize());
		rbRange.setFocusable(false);
		bgpQuery.add(rbRange);
		this.add(rbRange);

		rbSurrounding = new JRadioButton("Surrounding Student");
		rbSurrounding.setLocation(MAP_WIDTH + 17, 320);
		rbSurrounding.setFont(contentFont);
		rbSurrounding.setSize(rbSurrounding.getPreferredSize());
		rbSurrounding.setFocusable(false);
		bgpQuery.add(rbSurrounding);
		this.add(rbSurrounding);

		rbEmergency = new JRadioButton("Emergency Query");
		rbEmergency.setLocation(MAP_WIDTH + 17, 350);
		rbEmergency.setFont(contentFont);
		rbEmergency.setSize(rbEmergency.getPreferredSize());
		rbEmergency.setFocusable(false);
		bgpQuery.add(rbEmergency);
		this.add(rbEmergency);

		btnSubmitQuery = new JButton("Submit Query");
		btnSubmitQuery.setFont(titleFont);
		btnSubmitQuery.setBounds(MAP_WIDTH + 20, 410, 220, 50);
		btnSubmitQuery.setFocusable(false);
		this.add(btnSubmitQuery);

		taQueryLog = new JTextArea();
		taQueryLog.setFocusable(false);
		taQueryLog.setEditable(false);
		taQueryLog.setFont(contentFont);
		taQueryLog.setTabSize(3);
		DefaultCaret caret = (DefaultCaret)taQueryLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		spQueryLog = new JScrollPane(taQueryLog);
		spQueryLog.setBounds(10, MAP_HEIGHT + 23, 1053, 220);
		this.add(spQueryLog);
		

		this.state = new UIState();
	}

	private void initListeners() {
		UIEventListeners = new EventListenerList();

		btnSubmitQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = new UIState();
				dispatchUIEvent(UIEventType.SUBMIT);
			}
		});

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = new UIState();
				dispatchUIEvent(UIEventType.CHANGE_OPTION);

				// UI update
				Boolean featureTypeDisabled = (rbSurrounding.isSelected() || rbEmergency.isSelected());
				cbAS.setEnabled(!featureTypeDisabled);
				cbBuilding.setEnabled(!featureTypeDisabled);
				cbStudents.setEnabled(!featureTypeDisabled);
			}
		};
		rbWhole.addActionListener(actionListener);
		rbPoint.addActionListener(actionListener);
		rbRange.addActionListener(actionListener);
		rbSurrounding.addActionListener(actionListener);
		rbEmergency.addActionListener(actionListener);
	}

	private int sqlCount = 1;
	
	public void pushLog() {
		taQueryLog.setText("\n" + taQueryLog.getText());
	}

	public void pushLog(String log) {
		taQueryLog.setText(sqlCount + ".\t" + log + "\n" + taQueryLog.getText());
		sqlCount++;
	}

	// map layer
	private class MapLayer extends JComponent {
		private BufferedImage image;

		public MapLayer() {
			try {
				image = ImageIO.read(new File("data/map.jpg"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, null);
		}
	}

	// result layer
	private class ResultLayer extends JComponent {
		private ArrayList<Models.AnnouncementSystem> announcementSystems;
		private ArrayList<Models.Building> buildings;
		private ArrayList<Models.Student> students;

		private ArrayList<Color> ASColors;
		private ArrayList<Color> buildingColors;
		private ArrayList<Color> studentColors;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (students != null) {
				if (studentColors == null) {
					g.setColor(Color.GREEN);
					for (Models.Student s : students) {
						g.fillRect(s.pos[0] - 5, s.pos[1] - 5, 10, 10);
					}
				} else {
					for (int i = 0; i < students.size(); i++) {
						g.setColor(studentColors.get(i));
						g.fillRect(students.get(i).pos[0] - 5, students.get(i).pos[1] - 5, 10, 10);
					}
				}
			}

			if (buildings != null) {
				if (buildingColors == null) {
					g.setColor(Color.YELLOW);
					for (Models.Building b : buildings) {
						g.drawPolygon(b.outline);
					}
				} else {
					for (int i = 0; i < buildings.size(); i++) {
						g.setColor(buildingColors.get(i));
						g.drawPolygon(buildings.get(i).outline);
					}
				}
			}

			if (announcementSystems != null) {
				if (ASColors == null) {
					g.setColor(Color.RED);
					for (Models.AnnouncementSystem a : announcementSystems) {
						g.fillRect(a.center[0] - 7, a.center[1] - 7, 15, 15);
						g.drawOval(a.center[0] - a.radius, a.center[1] - a.radius, a.radius * 2, a.radius * 2);
					}
				} else {
					for (int i = 0; i < announcementSystems.size(); i++) {
						g.setColor(ASColors.get(i));
						g.fillRect(announcementSystems.get(i).center[0] - 7, announcementSystems.get(i).center[1] - 7,
							15, 15);
						g.drawOval(announcementSystems.get(i).center[0] - announcementSystems.get(i).radius,
							announcementSystems.get(i).center[1] - announcementSystems.get(i).radius,
							announcementSystems.get(i).radius * 2, announcementSystems.get(i).radius * 2);
					}
				}
			}
		}
	}

	public void drawResult(
		ArrayList<Models.AnnouncementSystem> announcementSystems,
		ArrayList<Models.Building> buildings,
		ArrayList<Models.Student> students) {

		if (announcementSystems != null) {
			resultLayer.announcementSystems = announcementSystems;
		}

		if (buildings != null) {
			resultLayer.buildings = buildings;
		}

		if (students != null) {
			resultLayer.students = students;
		}

		resultLayer.repaint();
	}

	public void drawResult(
		ArrayList<Models.AnnouncementSystem> announcementSystems,
		ArrayList<Models.Building> buildings,
		ArrayList<Models.Student> students,
		ArrayList<Color> ASColors,
		ArrayList<Color> buildingColors,
		ArrayList<Color> studentColors) {
		if (announcementSystems != null) {
			resultLayer.announcementSystems = announcementSystems;
		}

		if (buildings != null) {
			resultLayer.buildings = buildings;
		}

		if (students != null) {
			resultLayer.students = students;
		}

		if (ASColors != null) {
			resultLayer.ASColors = ASColors;
		}

		if (buildingColors != null) {
			resultLayer.buildingColors = buildingColors;
		}

		if (studentColors != null) {
			resultLayer.studentColors = studentColors;
		}

		resultLayer.repaint();
	}

	public void clearResultLayer() {
		resultLayer.announcementSystems = null;
		resultLayer.buildings = null;
		resultLayer.students = null;
		resultLayer.ASColors = null;
		resultLayer.buildingColors = null;
		resultLayer.studentColors = null;
		resultLayer.repaint();
	}

	// interactive layer
	private class InteractiveLayer extends JComponent {
		private int markX;
		private int markY;

		private ArrayList<Integer> polygonX;
		private ArrayList<Integer> polygonY;
		private boolean isDrawingPolygon;

		public InteractiveLayer() {
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					super.mouseReleased(e);

					if (state.queryType == Enums.QueryType.POINT_QUERY) {
						markX = e.getX();
						markY = e.getY();
						repaint();
					} else if (state.queryType == Enums.QueryType.SURROUNDING_STUDENT
						|| state.queryType == Enums.QueryType.EMERGENCY_QUERY) {
						markX = e.getX();
						markY = e.getY();
						dispatchUIEvent(UIEventType.UPDATE);
					} else if (state.queryType == Enums.QueryType.RANGE_QUERY) {
						if (e.getButton() == 1) {
							if (!isDrawingPolygon) {
								polygonX = new ArrayList<Integer>();
								polygonY = new ArrayList<Integer>();
								isDrawingPolygon = true;
								clearResultLayer();
							}

							polygonX.add(e.getX());
							polygonY.add(e.getY());
							repaint();
						} else if (e.getButton() == 3) {
							if (isDrawingPolygon) {
								isDrawingPolygon = false;
								repaint();
							}
						}
					}
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (state.queryType == Enums.QueryType.POINT_QUERY) {
				if (markX > -1 && markY > -1) {
					g.setColor(Color.RED);
					g.fillRect(markX - 2, markY - 2, 5, 5);
					g.setColor(new Color(255, 0, 0, 70));
					g.fillOval(markX - 50, markY - 50, 100, 100);
				}
			} else if (state.queryType == Enums.QueryType.SURROUNDING_STUDENT
				|| state.queryType == Enums.QueryType.EMERGENCY_QUERY) {
				if (markX > -1 && markY > -1) {
					g.setColor(Color.RED);
					g.fillRect(markX - 2, markY - 2, 5, 5);
				}
			} else if (state.queryType == Enums.QueryType.RANGE_QUERY) {
				if (polygonX != null && polygonY != null) {
					if (isDrawingPolygon) {
						g.setColor(Color.RED);
						for (int i = 0; i < polygonX.size(); i++) {
							g.fillRect(polygonX.get(i) - 4, polygonY.get(i) - 4, 8, 8);
							if (i < polygonX.size() - 1) {
								g.drawLine(polygonX.get(i), polygonY.get(i), polygonX.get(i + 1), polygonY.get(i + 1));
							}
						}
					} else {
						g.setColor(new Color(255, 0, 0, 70));
						int[] xpoints = new int[polygonX.size()];
						int[] ypoints = new int[polygonY.size()];
						for (int i = 0; i < polygonX.size(); i++) {
							xpoints[i] = polygonX.get(i);
							ypoints[i] = polygonY.get(i);
						}
						g.fillPolygon(new Polygon(xpoints, ypoints, polygonX.size()));
					}
				}
			}
		}
	}

	public void clearInteractiveLayer() {
		interactiveLayer.markX = -1;
		interactiveLayer.markY = -1;
		interactiveLayer.isDrawingPolygon = false;
		interactiveLayer.polygonX = null;
		interactiveLayer.polygonY = null;
	}

	public int[] getMark() {
		return new int[] { this.interactiveLayer.markX, this.interactiveLayer.markY };
	}

	public String getPolygonOutline() {
		if (this.interactiveLayer.polygonX == null
			|| this.interactiveLayer.polygonY == null
			|| this.interactiveLayer.polygonX.size() == 0
			|| this.interactiveLayer.polygonY.size() == 0) {
			return "";
		}

		String result = "";
		for (int i = 0; i < this.interactiveLayer.polygonX.size(); i++) {
			result += this.interactiveLayer.polygonX.get(i) + "," + this.interactiveLayer.polygonY.get(i) + ",";
		}
		result += this.interactiveLayer.polygonX.get(0) + "," + this.interactiveLayer.polygonY.get(0);
		return result;
	}

	// UI state & event
	private UIState state;

	public UIState getUIState() {
		return state;
	}

	public class UIState {
		private ArrayList<Enums.ActiveFeatureType> activeFeatureTypes;
		private Enums.QueryType queryType;

		public UIState() {
			activeFeatureTypes = new ArrayList<Enums.ActiveFeatureType>();

			if (cbAS.isSelected()) {
				activeFeatureTypes.add(Enums.ActiveFeatureType.AS);
			}
			if (cbBuilding.isSelected()) {
				activeFeatureTypes.add(Enums.ActiveFeatureType.BUILDING);
			}
			if (cbStudents.isSelected()) {
				activeFeatureTypes.add(Enums.ActiveFeatureType.STUDENTS);
			}

			if (rbWhole.isSelected()) {
				queryType = Enums.QueryType.WHOLE_REGION;
			}
			else if (rbPoint.isSelected()) {
				queryType = Enums.QueryType.POINT_QUERY;
			}
			else if (rbRange.isSelected()) {
				queryType = Enums.QueryType.RANGE_QUERY;
			}
			else if (rbSurrounding.isSelected()) {
				queryType = Enums.QueryType.SURROUNDING_STUDENT;
			}
			else if (rbEmergency.isSelected()) {
				queryType = Enums.QueryType.EMERGENCY_QUERY;
			}
		}

		public ArrayList<Enums.ActiveFeatureType> getActiveFeatureTypes() {
			return activeFeatureTypes;
		}

		public Enums.QueryType getQueryType() {
			return queryType;
		}
	}

	private EventListenerList UIEventListeners;

	public void addUIEventListener(UIEventListener l) {
		UIEventListeners.add(UIEventListener.class, l);
	}

	public void removeUIEventListener(UIEventListener l) {
		UIEventListeners.remove(UIEventListener.class, l);
	}

	private void dispatchUIEvent(UIEventType type) {
		Object[] listeners = UIEventListeners.getListenerList();
		UIEvent event = new UIEvent(this, type, -1, -1);

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == UIEventListener.class) {
				((UIEventListener) listeners[i + 1]).onUIEvent(event);
			}
		}
	}

	public class UIEvent extends EventObject {
		private UIEventType type;
		private int mouseX;
		private int mouseY;

		public UIEvent(Object source, UIEventType type, int mouseX, int mouseY) {
			super(source);
			this.type = type;
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}

		public UIEventType getType() {
			return type;
		}

		public int getMouseX() {
			return mouseX;
		}

		public int getMouseY() {
			return mouseY;
		}
	}

	public static interface UIEventListener extends EventListener {
		public void onUIEvent(UIEvent event);
	}

	public static enum UIEventType {
		CHANGE_OPTION, SUBMIT, UPDATE
	}
}
