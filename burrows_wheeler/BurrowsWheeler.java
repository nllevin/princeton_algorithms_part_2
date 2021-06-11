/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;

public class BurrowsWheeler {
  private static final int R = 256;

  public static void transform() {
    String s = BinaryStdIn.readString();
    CircularSuffixArray csa = new CircularSuffixArray(s);
    int n = s.length();

    for (int i = 0; i < n; i++) {
      if (csa.index(i) == 0) {
        BinaryStdOut.write(i);
        break;
      }
    }

    for (int i = 0; i < n; i++) {
      int j = csa.index(i);
      char c = s.charAt((j + n - 1) % n);
      BinaryStdOut.write(c);
    }

    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  public static void inverseTransform() {
    int first = BinaryStdIn.readInt();

    ArrayList<Character> t = new ArrayList<>();
    while (!BinaryStdIn.isEmpty())
      t.add(BinaryStdIn.readChar());

    int n = t.size();
    int[] count = new int[R];
    int[] cumulates = new int[R];
    char[] s = new char[n];
    int[] next = new int[n];

    for (int i = 0; i < n; i++)
      count[t.get(i)]++;

    for (int i = 1; i < R; i++)
      cumulates[i] = cumulates[i - 1] + count[i - 1];

    for (int i = 0; i < n; i++) {
      char c = t.get(i);
      next[cumulates[c]] = i;
      s[cumulates[c]] = c;
      cumulates[c]++;
    }

    int current = first;
    for (int i = 0; i < n; i++) {
      BinaryStdOut.write(s[current]);
      current = next[current];
    }

    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  public static void main(String[] args) {
    if (args[0].equals("-"))
      transform();
    else if (args[0].equals("+"))
      inverseTransform();
  }
}
