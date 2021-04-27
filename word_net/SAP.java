/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
  private Digraph graph;

  public SAP(Digraph G) {
    validateInput(G);

    graph = new Digraph(G.V());
    for (int vertex = 0; vertex < G.V(); vertex++) {
      for (int neighbor : G.adj(vertex))
        graph.addEdge(vertex, neighbor);
    }
  }

  private void validateInput(Object input) {
    if (input == null)
      throw new IllegalArgumentException();
  }

  public int length(int v, int w) {
    validateVertex(v);
    validateVertex(w);

    BreadthFirstDirectedPaths pathsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths pathsW = new BreadthFirstDirectedPaths(graph, w);
    return length(pathsV, pathsW);
  }

  public int length(Iterable<Integer> v, Iterable<Integer> w) {
    validateVertexIterable(v);
    validateVertexIterable(w);

    int countV = 0;
    for (int vertex : v)
      countV++;
    int countW = 0;
    for (int vertex : w)
      countW++;
    if (countV == 0 || countW == 0)
      return -1;

    BreadthFirstDirectedPaths pathsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths pathsW = new BreadthFirstDirectedPaths(graph, w);
    return length(pathsV, pathsW);
  }

  private int length(BreadthFirstDirectedPaths pathsV, BreadthFirstDirectedPaths pathsW) {
    int minLength = graph.V();
    for (int vertex = 0; vertex < graph.V(); vertex++)
      if (pathsV.hasPathTo(vertex) && pathsW.hasPathTo(vertex))
        minLength = Math.min(minLength, pathsV.distTo(vertex) + pathsW.distTo(vertex));
    return minLength < graph.V() ? minLength : -1;
  }

  public int ancestor(int v, int w) {
    validateVertex(v);
    validateVertex(w);

    BreadthFirstDirectedPaths pathsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths pathsW = new BreadthFirstDirectedPaths(graph, w);
    return ancestor(pathsV, pathsW);
  }

  public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
    validateVertexIterable(v);
    validateVertexIterable(w);

    int countV = 0;
    for (int vertex : v)
      countV++;
    int countW = 0;
    for (int vertex : w)
      countW++;
    if (countV == 0 || countW == 0)
      return -1;

    BreadthFirstDirectedPaths pathsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths pathsW = new BreadthFirstDirectedPaths(graph, w);
    return ancestor(pathsV, pathsW);
  }

  private int ancestor(BreadthFirstDirectedPaths pathsV, BreadthFirstDirectedPaths pathsW) {
    int minLength = graph.V();
    int minAncestor = -1;
    for (int vertex = 0; vertex < graph.V(); vertex++) {
      if (pathsV.hasPathTo(vertex) && pathsW.hasPathTo(vertex)) {
        int distance = pathsV.distTo(vertex) + pathsW.distTo(vertex);
        if (distance < minLength) {
          minLength = distance;
          minAncestor = vertex;
        }
      }
    }
    return minAncestor;
  }

  private void validateVertex(int vertex) {
    if (vertex < 0 || vertex > graph.V())
      throw new IllegalArgumentException();
  }

  private void validateVertexIterable(Iterable<Integer> vertices) {
    if (vertices == null)
      throw new IllegalArgumentException();

    for (Integer vertex : vertices) {
      if (vertex == null)
        throw new IllegalArgumentException();
      validateVertex(vertex);
    }
  }

  public static void main(String[] args) {
    try {
      new SAP(null);
    } catch (IllegalArgumentException err) {
      StdOut.println("Caught null constructor argument");
    }

    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    SAP sap = new SAP(G);

    try {
      sap.length(0, -1);
    } catch (IllegalArgumentException err) {
      StdOut.println("Caught vertex out of range: length");
    }
    try {
      sap.ancestor(-1, 0);
    } catch (IllegalArgumentException err) {
      StdOut.println("Caught vertex out of range: ancestor");
    }
    try {
      sap.ancestor(null, new Bag<Integer>());
    } catch (IllegalArgumentException err) {
      StdOut.println("Caught null Iterable argument: length");
    }
    try {
      sap.ancestor(null, new Bag<Integer>());
    } catch (IllegalArgumentException err) {
      StdOut.println("Caught null Iterable argument: ancestor");
    }
    try {
      Bag<Integer> bagV = new Bag<Integer>();
      Bag<Integer> bagW = new Bag<Integer>();
      bagV.add(null);
      sap.length(bagV, bagW);
    } catch (IllegalArgumentException err) {
      StdOut.println("Caught Iterable with null element: length");
    }
    try {
      Bag<Integer> bagV = new Bag<Integer>();
      Bag<Integer> bagW = new Bag<Integer>();
      bagV.add(null);
      sap.length(bagV, bagW);
    } catch (IllegalArgumentException err) {
      StdOut.println("Caught Iterable with null element: ancestor");
    }

    Bag<Integer> bagV = new Bag<Integer>();
    Bag<Integer> bagW = new Bag<Integer>();
    StdOut.println(sap.length(bagV, bagW));
    StdOut.println(sap.ancestor(bagV, bagW));

    while (!StdIn.isEmpty()) {
      int v = StdIn.readInt();
      int w = StdIn.readInt();
      int length = sap.length(v, w);
      int ancestor = sap.ancestor(v, w);
      StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
  }
}
