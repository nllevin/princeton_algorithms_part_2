/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
  private static final int R = 256;
  private static final int W = 8;

  private static class Node {
    private final char c;
    private Node prev;
    private Node next;

    public Node(char ch) {
      c = ch;
    }
  }

  public static void encode() {
    Node head = initializeAlphabet();

    while (!BinaryStdIn.isEmpty()) {
      int c = BinaryStdIn.readInt(W);
      int rank = 0;

      Node current = head.next;
      while (current.c != c) {
        rank++;
        current = current.next;
      }

      BinaryStdOut.write(rank, W);

      if (rank == 0)
        continue;

      moveToFront(current, head);
    }

    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  public static void decode() {
    Node head = initializeAlphabet();

    while (!BinaryStdIn.isEmpty()) {
      int code = BinaryStdIn.readInt(W);
      int rank = 0;

      Node current = head.next;
      while (rank < code) {
        rank++;
        current = current.next;
      }
      BinaryStdOut.write(current.c);

      if (rank == 0)
        continue;

      moveToFront(current, head);
    }

    BinaryStdIn.close();
    BinaryStdOut.close();
  }

  private static Node initializeAlphabet() {
    Node head = new Node('\0');
    Node tail = new Node('\0');

    Node current = head;
    for (char i = 0; i < R; i++) {
      Node n = new Node(i);
      n.prev = current;
      current.next = n;
      current = n;
    }

    current.next = tail;
    tail.prev = current;

    return head;
  }

  private static void moveToFront(Node current, Node head) {
    current.prev.next = current.next;
    current.next.prev = current.prev;
    current.prev = head;
    current.next = head.next;
    head.next.prev = current;
    head.next = current;
  }

  public static void main(String[] args) {
    if (args[0].equals("-"))
      encode();
    else if (args[0].equals("+"))
      decode();
  }
}
