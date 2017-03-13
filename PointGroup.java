/**
 * Interface PointGroup specifies the interface for a group of 3-D points.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2016
 */
public interface PointGroup
	{

	/**
	 * Returns the number of points in this group, N.
	 */
	public int N();

	/**
	 * Obtain the next point in this point group. This method must be called
	 * repeatedly, N times, to obtain all the points. Each time this method is
	 * called, it stores, in the fields of object point, the coordinates of the
	 * next point.
	 *
	 * @param  point  Point object in which to store the coordinates.
	 *
	 * @exception  NoSuchElementException
	 *     (unchecked exception) Thrown if this method is called more than N
	 *     times.
	 */
	public void nextPoint
		(Point point);

	}
