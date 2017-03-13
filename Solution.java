import edu.rit.gpu.Struct;
import java.nio.ByteBuffer;
/**
* Class Solution represents a solution for the KMedoids problem.
*
* @author  Zach Glassner
* @version 26-Oct-2016
*/
public class Solution extends Struct {
  public int a;  // X coordinate
  public int b;  // Y coordinate
  public double d;  // Z coordinate

  public Solution() {}
    /**
    *  Constructor for a solution.
    *  @param   a   a index.
    *  @param   b   b index.
    *  @param   d   total distance of medoids A and B.
    */
  public Solution(int a, int b, double d) {
    this.a = a;
    this.b = b;
    this.d = d;
  }

  /**
   *  Returns the size in bytes of the C struct.
   */
  public static long sizeof() {
    return 16;
  }

  /**
   *  Write this Java object to the given byte buffer as a C struct.
   *  @param buf  Buffer to write to.
   */
  public void toStruct(ByteBuffer buf) {
    buf.putInt (this.a);
    buf.putInt (this.b);
    buf.putDouble (this.d);
  }

  /**
   *  Read this Java object from the given byte buffer as a C struct.
   *  @param buf  Buffer to read from to.
   */
  public void fromStruct(ByteBuffer buf) {
    this.a = buf.getInt();
    this.b = buf.getInt();
    this.d = buf.getDouble();
  }

  public String toString(){
    return "Medoids ("+this.a+", "+this.b+"):\t"+this.d+"\n";
  }
}
