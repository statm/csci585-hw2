import oracle.jdbc.driver.OracleConnection;
import oracle.sdoapi.OraSpatialManager;
import oracle.sdoapi.adapter.GeometryAdapter;
import oracle.sdoapi.geom.CoordPoint;
import oracle.sdoapi.geom.Geometry;
import oracle.sdoapi.geom.GeometryFactory;
import oracle.sdoapi.geom.Point;
import oracle.sdoapi.geom.Polygon;
import oracle.sql.STRUCT;

public class Geom {
	private static GeometryAdapter sdoAdapter;
	private static GeometryFactory geomFactory;

	public static void init(OracleConnection conn) {
		sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9", STRUCT.class, STRUCT.class, null, conn);
		geomFactory = OraSpatialManager.getGeometryFactory();
	}

	// create
	public static Object createPolygon(double[] outerOrdinateArray) {
		try {
			return sdoAdapter.exportGeometry(STRUCT.class,
				geomFactory.createPolygon(geomFactory.createLineString(outerOrdinateArray), null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object createPoint(double x, double y) {
		try {
			return sdoAdapter.exportGeometry(STRUCT.class, geomFactory.createPoint(x, y));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object createCircle(double x, double y, double r) {
		try {
			return sdoAdapter.exportGeometry(STRUCT.class, geomFactory.createCircle(x, y, r));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	// read
	public static int[] readPolygon(Object data) {
		try {
			Geometry g = sdoAdapter.importGeometry((STRUCT) data);
			if (g instanceof Polygon) {
				Polygon polygon = (Polygon) g;
				return coordPointArray2Ints(polygon.getExteriorRing().getPointArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int[] readPoint(Object data) {
		try {
			Geometry g = sdoAdapter.importGeometry((STRUCT) data);
			if (g instanceof Point) {
				Point point = (Point) g;
				return new int[] {(int)point.getX(), (int)point.getY()};
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// utils
	private static int[] coordPointArray2Ints(CoordPoint[] points) {
		int[] result = new int[points.length * 2];
		for (int i = 0; i < points.length; i++) {
			result[2 * i] = (int)points[i].getX();
			result[2 * i + 1] = (int)points[i].getY();
		}
		return result;
	}
}
