import edu.rit.util.Random;
import java.util.NoSuchElementException;
import static java.lang.Math.*;

/**
 * Class ClusterPointGroup provides a group of 3-D points at random coordinates
 * centered around one or more clusters.
 *
 * @author  Alan Kaminsky
 * @version 31-Oct-2016
 */
public class ClusterPointGroup
	implements PointGroup
	{

	private int N;
	private Random prng;
	private int C;
	private double[] xcenter;
	private double[] ycenter;
	private double[] zcenter;
	private double[] radius;
	private int generated;

	/**
	 * Construct a new random point group. The points' coordinates are chosen at
	 * random in one or more clusters. The first cluster is defined by X center
	 * = param[0], Y center = param[1], Z center = param[2], radius = param[3];
	 * the second cluster is defined similarly by param[4..7]; and so on.
	 *
	 * @param  N      Number of points.
	 * @param  seed   Seed for pseudorandom number generator.
	 * @param  param  Cluster parameters.
	 */
	public ClusterPointGroup
		(int N,
		 long seed,
		 double... param)
		{
		if (param.length < 4 || param.length % 4 != 0)
			throw new IllegalArgumentException
				("ClusterPointGroup(): param illegal");
		this.N = N;
		prng = new Random (seed);
		C = param.length/4;
		xcenter = new double [C];
		ycenter = new double [C];
		zcenter = new double [C];
		radius  = new double [C];
		for (int i = 0; i < C; ++ i)
			{
			xcenter[i] = param[4*i];
			ycenter[i] = param[4*i+1];
			zcenter[i] = param[4*i+2];
			radius[i]  = param[4*i+3];
			}
		}

	/**
	 * Returns the number of points in this group, N.
	 */
	public int N()
		{
		return N;
		}

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
		(Point point)
		{
		if (generated == N)
			throw new NoSuchElementException
				("RandomPointGroup.nextPoint(): Too many points generated");
		int c = generated % C;
		double r = prng.nextDouble()*radius[c];
		double theta = prng.nextDouble()*2*PI;
		double phi = prng.nextDouble()*PI;
		point.x = xcenter[c] + r*cos(theta)*sin(phi);
		point.y = ycenter[c] + r*sin(theta)*sin(phi);
		point.z = zcenter[c] + r*cos(phi);
		++ generated;
		}

	}
