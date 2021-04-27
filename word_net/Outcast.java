/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
  private WordNet wordnet;

  public Outcast(WordNet wn) {
    wordnet = wn;
  }

  public String outcast(String[] nouns) {
    int distances[] = new int[nouns.length];
    for (int i = 0; i < nouns.length - 1; i++) {
      for (int j = i + 1; j < nouns.length; j++) {
        int distance = wordnet.distance(nouns[i], nouns[j]);
        distances[i] += distance;
        distances[j] += distance;
      }
    }

    int maxDistance = 0;
    String outcast = "";
    for (int i = 0; i < nouns.length; i++) {
      if (distances[i] > maxDistance) {
        maxDistance = distances[i];
        outcast = nouns[i];
      }
    }
    return outcast;
  }

  public static void main(String[] args) {
    WordNet wordnet = new WordNet(args[0], args[1]);
    Outcast outcast = new Outcast(wordnet);
    for (int t = 2; t < args.length; t++) {
      In in = new In(args[t]);
      String[] nouns = in.readAllStrings();
      StdOut.println(args[t] + ": " + outcast.outcast(nouns));
    }
  }
}
