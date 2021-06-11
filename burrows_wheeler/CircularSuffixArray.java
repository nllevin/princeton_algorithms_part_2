/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class CircularSuffixArray {
  private int[] suffixArray;

  public CircularSuffixArray(String s) {
    if (s == null)
      throw new IllegalArgumentException();

    int n = s.length();
    suffixArray = new int[n];
    for (int i = 0; i < n; i++)
      suffixArray[i] = i;

    radixQuicksort(s, 0, n - 1, 0);
  }

  private void radixQuicksort(String s, int left, int right, int d) {
    int lo = left;
    int hi = right;
    int n = s.length();

    if (hi <= lo || d == n)
      return;

    char pivot = s.charAt((suffixArray[lo] + d) % n);
    for (int i = lo + 1; i <= hi; i++) {
      char c = s.charAt((suffixArray[i] + d) % n);
      if (c < pivot) {
        int temp = suffixArray[lo];
        suffixArray[lo++] = suffixArray[i];
        suffixArray[i] = temp;
      } else if (c > pivot) {
        int temp = suffixArray[hi];
        suffixArray[hi--] = suffixArray[i];
        suffixArray[i--] = temp;
      }
    }
    radixQuicksort(s, left, lo - 1, d);
    radixQuicksort(s, lo, hi, d + 1);
    radixQuicksort(s, hi + 1, right, d);
  }

  public int length() {
    return suffixArray.length;
  }

  public int index(int i) {
    if (i < 0 || i >= length())
      throw new IllegalArgumentException();

    return suffixArray[i];
  }

  public static void main(String[] args) {
    String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder sb = new StringBuilder(1000);
    for (int i = 0; i < 1000; i++)
      sb.append(alpha.charAt((int) Math.floor(Math.random() * 26)));
    String s = sb.toString();

    Stopwatch watch = new Stopwatch();
    CircularSuffixArray csa = new CircularSuffixArray(s);
    StdOut.println(watch.elapsedTime());

    StdOut.println(csa.length());
    for (int i = 0; i < 12; i++)
      StdOut.println(csa.index(i));
  }
}
