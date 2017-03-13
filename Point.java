import edu.rit.gpu.Struct;
import java.nio.ByteBuffer;
/**
* Class Point encapsulates a 3-D point.
*
* @author  Zach Glassner
* @version 26-Oct-2016
*/
public class Point extends Struct {
  public double x;  // X coordinate
  public double y;  // Y coordinate
  public double z;  // Z coordinate

  public Point() {}
    /**
    *  Constructor for a point.
    *  @param   x   x coordinate value.
    *  @param   y   y coordinate value.
    *  @param   z   z coordinate value.
    */
  public Point(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   *  Returns the size in bytes of the C struct.
   */
  public static long sizeof() {
    return 24;
  }

  /**
   *  Write this Java object to the given byte buffer as a C struct.
   *  @param buf  Buffer to write to.
   */
  public void toStruct(ByteBuffer buf) {
    buf.putDouble (this.x);
    buf.putDouble (this.y);
    buf.putDouble (this.z);
  }

  /**
   *  Read this Java object from the given byte buffer as a C struct.
   *  @param buf  Buffer to read from to.
   */
  public void fromStruct(ByteBuffer buf) {
    this.x = buf.getDouble();
    this.y = buf.getDouble();
    this.z = buf.getDouble();
  }

  public String toString(){
    return "("+this.x+", "+this.y+", "+this.z+")";
  }

  //  city-block distance
  public double distance(Point p) {
    double dX = this.x - p.x;
    if (dX < 0) {
      dX *= -1;
    }
    double dY = this.y - p.y;
    if (dY < 0) {
      dY *= -1;
    }
    double dZ = this.z - p.z;
    if (dZ < 0) {
      dZ *= -1;
    }
    return dX + dY + dZ;
  }
}
