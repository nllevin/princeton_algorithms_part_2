/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;

public class WordNet {
  private ArrayList<String> idIndexedSynsets;
  private HashMap<String, Bag<Integer>> nounSynsetsTable;
  private Digraph net;
  private SAP shortestAncestorPaths;

  public WordNet(String synsets, String hypernyms) {
    validateNonNullInput(synsets);
    validateNonNullInput(hypernyms);

    processSynsets(synsets);
    buildWordNetGraph(hypernyms);
    validateRootedDAG();

    shortestAncestorPaths = new SAP(net);
  }

  private void validateNonNullInput(String input) {
    if (input == null)
      throw new IllegalArgumentException();
  }

  private void processSynsets(String synsets) {
    idIndexedSynsets = new ArrayList<String>();
    nounSynsetsTable = new HashMap<String, Bag<Integer>>();
    In inSynsets = new In(synsets);

    while (inSynsets.hasNextLine()) {
      String[] entry = inSynsets.readLine().split(",");
      int synsetId = Integer.parseInt(entry[0]);
      String[] nouns = entry[1].split(" ");

      idIndexedSynsets.add(entry[1]);
      for (String noun : nouns) {
        if (!nounSynsetsTable.containsKey(noun))
          nounSynsetsTable.put(noun, new Bag<Integer>());

        nounSynsetsTable.get(noun).add(synsetId);
      }
    }
  }

  private void buildWordNetGraph(String hypernyms) {
    net = new Digraph(idIndexedSynsets.size());
    In inHypernyms = new In(hypernyms);
    while (inHypernyms.hasNextLine()) {
      String[] entry = inHypernyms.readLine().split(",");
      int hyponymId = Integer.parseInt(entry[0]);
      for (int i = 1; i < entry.length; i++)
        net.addEdge(hyponymId, Integer.parseInt(entry[i]));
    }
  }

  private void validateRootedDAG() {
    validateAcyclic();
    validateRooted();
  }

  private void validateAcyclic() {
    boolean[] visited = new boolean[net.V()];
    boolean[] onStack = new boolean[net.V()];
    for (int vertex = 0; vertex < net.V(); vertex++) {
      visited[vertex] = false;
      onStack[vertex] = false;
    }

    for (int vertex = 0; vertex < net.V(); vertex++)
      if (!visited[vertex])
        validateAcyclicDFS(vertex, visited, onStack);
  }

  private void validateAcyclicDFS(int vertex, boolean[] visited, boolean[] onStack) {
    visited[vertex] = true;
    onStack[vertex] = true;
    for (int neighbor : net.adj(vertex)) {
      if (!visited[neighbor]) {
        validateAcyclicDFS(neighbor, visited, onStack);
      } else if (onStack[neighbor]) {
        throw new IllegalArgumentException();
      }
    }
    onStack[vertex] = false;
  }

  private void validateRooted() {
    boolean foundRoot = false;
    for (int vertex = 0; vertex < net.V(); vertex++) {
      int outDegree = 0;
      for (int neighbor : net.adj(vertex))
        outDegree++;

      if (outDegree == 0) {
        if (foundRoot)
          throw new IllegalArgumentException();

        foundRoot = true;
      }
    }
  }

  public Iterable<String> nouns() {
    return nounSynsetsTable.keySet();
  }

  public boolean isNoun(String word) {
    validateNonNullInput(word);
    return nounSynsetsTable.containsKey(word);
  }

  public int distance(String nounA, String nounB) {
    validateNoun(nounA);
    validateNoun(nounB);

    Bag<Integer> synsetsA = nounSynsetsTable.get(nounA);
    Bag<Integer> synsetsB = nounSynsetsTable.get(nounB);
    return shortestAncestorPaths.length(synsetsA, synsetsB);
  }

  public String sap(String nounA, String nounB) {
    validateNoun(nounA);
    validateNoun(nounB);

    Bag<Integer> synsetsA = nounSynsetsTable.get(nounA);
    Bag<Integer> synsetsB = nounSynsetsTable.get(nounB);
    int synsetId = shortestAncestorPaths.ancestor(synsetsA, synsetsB);
    return idIndexedSynsets.get(synsetId);
  }

  private void validateNoun(String word) {
    if (!isNoun(word))
      throw new IllegalArgumentException();
  }

  public static void main(String[] args) {
    try {
      new WordNet(args[0], null);
    } catch (IllegalArgumentException err) {
      System.out.println("Caught null constructor argument.");
    }

    WordNet wn = new WordNet(args[0], args[1]);

    Iterable<String> nouns = wn.nouns();
    int count = 0;
    for (String noun : nouns) {
      count++;
      System.out.println(noun);
      if (count == 10)
        break;
    }

    try {
      wn.isNoun(null);
    } catch (IllegalArgumentException err) {
      System.out.println("Caught null isNoun argument.");
    }

    System.out.println(wn.isNoun("Jupiter's_beard"));
    System.out.println(wn.isNoun("bloop_bloop"));

    try {
      wn.distance("cat", null);
    } catch (IllegalArgumentException err) {
      System.out.println("Caught null distance argument.");
    }
    try {
      wn.distance("cat", "bloop_bloop");
    } catch (IllegalArgumentException err) {
      System.out.println("Caught excluded word argument: distance.");
    }
    try {
      wn.sap("cat", null);
    } catch (IllegalArgumentException err) {
      System.out.println("Caught null sap argument.");
    }
    try {
      wn.sap("cat", "bloop_bloop");
    } catch (IllegalArgumentException err) {
      System.out.println("Caught excluded word argument: sap.");
    }

    System.out.println(wn.distance("George_W._Bush", "President_John_F._Kennedy"));
    System.out.println(wn.sap("George_W._Bush", "President_John_F._Kennedy"));
    System.out.println(wn.distance("George_W._Bush", "chimpanzee"));
    System.out.println(wn.sap("George_W._Bush", "chimpanzee"));
    System.out.println(wn.distance("George_W._Bush", "Eric_Arthur_Blair"));
    System.out.println(wn.sap("George_W._Bush", "Eric_Arthur_Blair"));
    System.out.println(wn.distance("apple", "gravity"));
    System.out.println(wn.sap("apple", "gravity"));
  }
}
