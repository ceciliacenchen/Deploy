package smart.data.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

//import edu.uci.ics.jung.algorithms.importance.Ranking;

public class V implements Serializable {
	/**
	 *
	 */
	public int id;
	public double x;
	public double y;

	public V(int n) {
		id = n;
		x = 0.0;
		y = 0.0;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
