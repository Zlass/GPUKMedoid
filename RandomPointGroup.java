import edu.rit.util.Random;
import java.util.NoSuchElementException;

/**
 * Class RandomPointGroup provides a group of 3-D points at random coordinates.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2016
 */
public class RandomPointGroup
  implements PointGroup
  {

  private int N;
  private double max;
  private Random prng;
  private int generated;

  /**
   * Construct a new random point group. The points' coordinates are chosen at
   * random in the range -max .. +max.
   *
   * @param  N  Number of points.
   * @param  max  Maximum absolute coordinate value.
   * @param  seed  Seed for pseudorandom number generator.
   */
  public RandomPointGroup
    (int N,
     int max,
     long seed)
    {
    this.N = N;
    this.max = max;
    this.prng = new Random (seed);
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
   * repeatedly, N times, to obtain all the edges. Each time this method is
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
    point.x = randomCoordinate();
    point.y = randomCoordinate();
    point.z = randomCoordinate();
    ++ generated;
    }

  /**
   * Returns a random coordinate.
   */
  private double randomCoordinate()
    {
    return (prng.nextDouble()*2.0 - 1.0)*max;
    }

  }
