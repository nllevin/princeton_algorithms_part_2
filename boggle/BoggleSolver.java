/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashSet;

public class BoggleSolver {
  private Node dictionary;
  private int uid = 0;

  private static class Node {
    private Node[] next = new Node[26];
    private String word;
    private int lastUID = 0;
  }

  public BoggleSolver(String[] dictionaryArray) {
    for (String word : dictionaryArray)
      addToDictionary(word);
  }

  private void addToDictionary(String word) {
    dictionary = addToDictionary(dictionary, word, 0);
  }

  private Node addToDictionary(Node node, String word, int d) {
    if (d > word.length())
      return node;
    else if (node == null)
      node = new Node();

    if (d == word.length()) {
      node.word = word;
      return node;
    } else {
      int c = word.charAt(d);
      int offset = c == 'Q' ? 2 : 1;
      if (c == 'Q' && (d == word.length() - 1 || word.charAt(d + 1) != 'U'))
        return node;
      node.next[c - 'A'] = addToDictionary(node.next[c - 'A'], word, d + offset);
    }

    return node;
  }

  public Iterable<String> getAllValidWords(BoggleBoard board) {
    uid++;
    HashSet<String> words = new HashSet<>();
    boolean[][] visited = new boolean[board.rows()][board.cols()];
    for (int row = 0; row < board.rows(); row++) {
      for (int col = 0; col < board.cols(); col++) {
        dfs(row, col, board, visited, words, dictionary);
      }
    }

    return words;
  }

  private void dfs(int row, int col, BoggleBoard board, boolean[][] visited, HashSet<String> words, Node node) {
    char nextChar = board.getLetter(row, col);
    Node nextNode = node.next[nextChar - 'A'];

    if (nextNode == null)
      return;

    if (nextNode.word != null && nextNode.lastUID != uid && nextNode.word.length() > 2) {
      words.add(nextNode.word);
      nextNode.lastUID = uid;
    }

    visited[row][col] = true;
    for (int dRow = -1; dRow <= 1; dRow++) {
      int nextRow = row + dRow;
      if (nextRow < 0 || nextRow == board.rows())
        continue;

      for (int dCol = -1; dCol <= 1; dCol++) {
        int nextCol = col + dCol;
        if (nextCol < 0 || nextCol == board.cols())
          continue;

        if (!visited[nextRow][nextCol])
          dfs(nextRow, nextCol, board, visited, words, nextNode);
      }
    }
    visited[row][col] = false;
  }

  public int scoreOf(String word) {
    if (word.length() < 3)
      return 0;

    Node node = dictionary;
    for (int i = 0; i < word.length(); i++) {
      if (node == null)
        return 0;

      char nextChar = word.charAt(i);
      if (nextChar == 'Q')
        i++;

      node = node.next[nextChar - 'A'];
    }
    if (node == null || node.word == null) {
      return 0;
    }

    int len = word.length();
    if (len < 5)
      return 1;
    else if (len == 5)
      return 2;
    else if (len == 6)
      return 3;
    else if (len == 7)
      return 5;
    else
      return 11;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard(args[1]);
    int score = 0;
    for (String word : solver.getAllValidWords(board)) {
      StdOut.println(word);
      score += solver.scoreOf(word);
    }
    StdOut.println("Score = " + score);

    Stopwatch timer = new Stopwatch();
    for (int i = 0; i < Integer.parseInt(args[2]); i++) {
      BoggleBoard randBoard = new BoggleBoard();
      solver.getAllValidWords(randBoard);
    }
    StdOut.println("Time elapsed for " + args[2] + " random boards: " + timer.elapsedTime() + "s");
  }
}
