
// Number of threads per block.
#define NT 1024

// Structure for a 3-D point.
typedef struct {
  double x;
  double y;
  double z;
}point_t;

// Structure for a solution.
typedef struct {
  int a;
  int b;
  double d;
}solution_t;

// Variables in global memory.
__device__ int devBestSol;

// Per-thread variables in shared memory.
__shared__ solution_t shrSols[NT];
__shared__ int shrBestSolIndex[NT];

/**
 *  Calculates the city-block distance between two point_t structs as defiend
 *  by this function.  distance(P1,P2) = |x1 − x2| + |y1 − y2| + |z1 − z2|
 *  @param    p1    A pointer to the first point.
 *  @param    p2    A pointer to the second point.
 *  @return   The city block distance between p1 and p2.
 */
__device__ double distance(point_t *p1, point_t *p2) {

  double tempX = p1->x - p2->x;
  if (tempX < 0) {
    tempX *= -1;
  }
  double tempY = p1->y - p2->y;
  if (tempY < 0) {
    tempY *= -1;
  }
  double tempZ = p1->z - p2->z;
  if (tempZ < 0) {
    tempZ *= -1;
  }
  return tempX + tempY + tempZ;
}

/**
 * Compares to different solution_t to find the one with the lowest distance then a index and finall b index.
 * @param a   Pointer to first solution.
 * @param b   Pointer to second solution.
 * @return  true if a is the better solution false otherwise.
 */
__device__ bool compareSol(solution_t *a, solution_t *b){
  bool aIsbest = false;
  if(a->d == -1.0){aIsbest = false;}
  else if(b->d == -1.0){aIsbest = true;}
  else if (b->d > a->d) {
    aIsbest = true;
  } else if (b->d == a->d) {
    if (b->a > a->a){
      aIsbest = true;
    }else{
      if (b->b > a->b){
        aIsbest = true;
      }
    }
  }
  return aIsbest;
}

/**
 * Device kernel to calculate the distance for each point to its closest medoid.
 *
 * Called with a one-dimensional grid of one-dimensional blocks, N blocks, NT
 * threads per block. N = number of points. Each block finds the best solution
 * for its given A index. Each thread within a block computes its total
 * distance for its B index(s).
 *
 * @param  pointList   Array of all the points.
 * @param  solutions   Array of all the solutions the gpu finds.
 * @param  N           Total number of points.
 */
extern "C" __global__ void computeMedoids
    (point_t *pointList, solution_t *solutions, int N) {
  int a = blockIdx.x, b; // X index of this block, the A medoids index
  int thrd = threadIdx.x; // Index of this thread within block
  point_t medA = pointList[a]; // Medoid A's point
  // double *solu = &solutions[a + b * N];
  double d = 0.0;
  solution_t sol;
  sol.a = a;
  sol.b = thrd;
  sol.d = -1.0;

  for (b = thrd; b < N; b += NT) {
    d = 0;
    solution_t tempSol;
    for (int p = 0; p < N; p++){
      point_t medB = pointList[b]; // Medoid B's point
      if (p == a || p == b) continue;
      double distA = distance( &medA, &pointList[p]);
      double distB = distance( &medB, &pointList[p]);
      d += (distA <= distB) ? distA : distB;
      tempSol.a = a;
      tempSol.b = b;
      tempSol.d = (b <= a | b >= N) ? -1 : d;
    }
    if(!compareSol(&sol, &tempSol))
      sol = tempSol;
  }
  shrSols[thrd] = sol;
  shrBestSolIndex[thrd] = thrd;
  __syncthreads();

  // Reduction to find the best solution in the current block
  for (int s = NT / 2; s > 0; s >>= 1) {
    if (thrd < s){
      shrBestSolIndex[thrd] = compareSol(&shrSols[shrBestSolIndex[thrd]], &shrSols[shrBestSolIndex[thrd + s]]) ? shrBestSolIndex[thrd] : shrBestSolIndex[thrd + s];

    }
    __syncthreads();
  }
  solutions[a] = shrSols[shrBestSolIndex[0]];

  __threadfence();

  //  Final reduction to find the best solution overall.
  if (thrd == 0) {
    int oldSol;
    int newSol;
    do {
      oldSol = devBestSol;
      newSol = compareSol(&solutions[devBestSol], &solutions[a]) ? devBestSol : a;
    } while (atomicCAS (&devBestSol, oldSol, newSol) != oldSol);
  }
}
