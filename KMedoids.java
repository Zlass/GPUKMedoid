import edu.rit.util.Instance;
import edu.rit.gpu.CacheConfig;
import edu.rit.gpu.Gpu;
import edu.rit.gpu.GpuDoubleVbl;
import edu.rit.gpu.GpuIntVbl;
import edu.rit.gpu.GpuStructArray;
import edu.rit.gpu.Kernel;
import edu.rit.gpu.Module;
import edu.rit.gpu.Struct;
import edu.rit.gpu.GpuVbl;
import edu.rit.pj2.Task;
import java.util.NoSuchElementException;

/**
 *  This class contains the main task that finds the best 2 medoids of a given
 *  set of points from a PointGroup constructor.
 *  @author   Zachary Glassner
 */
public class KMedoids extends Task {

  //  List of all the generated points
  GpuStructArray<Point> pointList;
  //  Solution Array of solutions for all blocks.
  GpuStructArray<Solution> solutions;
  //  Stores the index of the best solution
  GpuIntVbl bestSolIndex;

  /**
   * Sets up the variables and kernel for the gpu and waits to get the results  * from the gpu.
   */
  public void main(String[] args) throws Exception {
    if (args.length != 1) {
      usage();
      terminate(1);
    }

    PointGroup points = (PointGroup) Instance.newInstance (args[0]);
    // Initialize GPU.
    Gpu gpu = Gpu.gpu();
    gpu.ensureComputeCapability (2, 0);

    // Set up GPU variables.
    Module module = gpu.getModule ("KMedoids.ptx");
    pointList = gpu.getStructArray (Point.class, points.N());
    solutions = gpu.getStructArray (Solution.class, points.N());
    bestSolIndex = module.getIntVbl ("devBestSol");

    Point p = new Point();
    int i = 0;
    while(true){
      try{
        points.nextPoint(p);
        pointList.item[i] = new Point(p.x, p.y, p.z);
        i++;
      }catch (NoSuchElementException e) {
        break;
      }
    }
    pointList.hostToDev();

    for (i = 0; i < points.N(); i++ ) {
      solutions.item[i] = new Solution();
    }
    solutions.hostToDev();



    // Set up GPU kernel.
    KMedoidsKernel kernel = module.getKernel(KMedoidsKernel.class);
    kernel.setBlockDim (1024);
    kernel.setGridDim (points.N()-1);
    kernel.setCacheConfig (CacheConfig.CU_FUNC_CACHE_PREFER_L1);

    //  Find our two medoids
    kernel.computeMedoids(pointList, solutions, points.N());
    bestSolIndex.devToHost();
    solutions.devToHost(0, bestSolIndex.item, 1);
    System.out.printf("%d%n%d%n%.3f%n",solutions.item[0].a, solutions.item[0].b, solutions.item[0].d);
  }

  private static void usage() {
    System.out.println("java pj2 KMedoids \"<constructor>\"");
    System.out.println("\t<constructor> is a constructor expression for a class that implements interface PointGroup");
  }

  /**
   * Specify that this task requires one core.
   */
  protected static int coresRequired() {
    return 1;
  }

  /**
   * Specify that this task requires one GPU accelerator.
   */
  protected static int gpusRequired() {
    return 1;
}

  /**
   *  Interface for the kernel function for computing the medoids.
   */
  private static interface KMedoidsKernel extends Kernel {

    public void computeMedoids(
      GpuStructArray<Point> points,
      GpuStructArray<Solution> solutions,
      int N
    );

  }

}
