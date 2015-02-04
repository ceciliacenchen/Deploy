package smart.data.objects;

import java.io.Serializable;


public class E implements Serializable {

	public int id;
	public V v1;
	public V v2;
	public double dis;

	public E(int n) {
		id = n++;
		dis= 0;
	}

	/**
	 * @return the v1
	 */
	public V getV1() {
		return v1;
	}

	/**
	 * @param v1 the v1 to set
	 */
	public void setV1(V v1) {
		this.v1 = v1;
	}

	/**
	 * @return the v2
	 */
	public V getV2() {
		return v2;
	}

	/**
	 * @param v2 the v2 to set
	 */
	public void setV2(V v2) {
		this.v2 = v2;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the dis
	 */
	public double getDis() {
		return dis;
	}

	/**
	 * @param dis the dis to set
	 */
	public void setDis(double dis) {
		this.dis = dis;
	}



}
