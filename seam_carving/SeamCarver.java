/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
  private int[][] red;
  private int[][] green;
  private int[][] blue;

  public SeamCarver(Picture picture) {
    validateNonNullInput(picture);

    red = new int[picture.height()][picture.width()];
    green = new int[picture.height()][picture.width()];
    blue = new int[picture.height()][picture.width()];

    for (int y = 0; y < picture.height(); y++) {
      for (int x = 0; x < picture.width(); x++) {
        Color color = picture.get(x, y);
        red[y][x] = color.getRed();
        green[y][x] = color.getGreen();
        blue[y][x] = color.getBlue();
      }
    }
  }

  public Picture picture() {
    Picture pic = new Picture(width(), height());
    for (int y = 0; y < height(); y++)
      for (int x = 0; x < width(); x++)
        pic.set(x, y, new Color(red[y][x], green[y][x], blue[y][x]));

    return pic;
  }

  public int width() {
    return red[0].length;
  }

  public int height() {
    return red.length;
  }

  public double energy(int x, int y) {
    validateCoordinates(x, y);

    if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1)
      return 1000;

    double xGradientSquared = gradientSquared(x - 1, y, x + 1, y);
    double yGradientSquared = gradientSquared(x, y - 1, x, y + 1);

    return Math.sqrt(xGradientSquared + yGradientSquared);
  }

  private double gradientSquared(int x1, int y1, int x2, int y2) {
    int deltaR = red[y2][x2] - red[y1][x1];
    int deltaB = blue[y2][x2] - blue[y1][x1];
    int deltaG = green[y2][x2] - green[y1][x1];

    return Math.pow(deltaR, 2) + Math.pow(deltaB, 2) + Math.pow(deltaG, 2);
  }

  public int[] findHorizontalSeam() {
    int rowFrom[][] = new int[height()][width()];
    double[][] pathWeights = new double[height()][width()];
    for (int x = 0; x < width(); x++)
      for (int y = 0; y < height(); y++)
        pathWeights[y][x] = (x == 0) ? 1000 : Double.POSITIVE_INFINITY;

    int endRow = 0;
    double minPathWeight = Double.POSITIVE_INFINITY;

    for (int x = 0; x < width() - 1; x++) {
      int nextX = x + 1;
      for (int y = 0; y < height(); y++) {
        double currWeight = pathWeights[y][x];
        for (int deltaY = -1; deltaY <= 1; deltaY++) {
          int nextY = y + deltaY;
          if (nextY < 0 || nextY >= height())
            continue;

          double nextWeight = currWeight + energy(nextX, nextY);
          if (nextWeight < pathWeights[nextY][nextX]) {
            rowFrom[nextY][nextX] = y;
            pathWeights[nextY][nextX] = nextWeight;

            if (nextX == width() - 1 && nextWeight < minPathWeight) {
              minPathWeight = nextWeight;
              endRow = nextY;
            }
          }
        }
      }
    }

    int[] seam = new int[width()];
    seam[width() - 1] = endRow;
    for (int x = width() - 1; x > 0; x--)
      seam[x - 1] = rowFrom[seam[x]][x];

    return seam;
  }

  public int[] findVerticalSeam() {
    int columnFrom[][] = new int[height()][width()];
    double[][] pathWeights = new double[height()][width()];
    for (int x = 0; x < width(); x++)
      for (int y = 0; y < height(); y++)
        pathWeights[y][x] = (y == 0) ? 1000 : Double.POSITIVE_INFINITY;

    int endColumn = 0;
    double minPathWeight = Double.POSITIVE_INFINITY;
    for (int y = 0; y < height() - 1; y++) {
      int nextY = y + 1;
      for (int x = 0; x < width(); x++) {
        double currWeight = pathWeights[y][x];
        for (int deltaX = -1; deltaX <= 1; deltaX++) {
          int nextX = x + deltaX;
          if (nextX < 0 || nextX >= width())
            continue;

          double nextWeight = currWeight + energy(nextX, nextY);
          if (nextWeight < pathWeights[nextY][nextX]) {
            columnFrom[nextY][nextX] = x;
            pathWeights[nextY][nextX] = nextWeight;

            if (nextY == height() - 1 && nextWeight < minPathWeight) {
              minPathWeight = nextWeight;
              endColumn = nextX;
            }
          }
        }
      }
    }

    int[] seam = new int[height()];
    seam[height() - 1] = endColumn;
    for (int y = height() - 1; y > 0; y--)
      seam[y - 1] = columnFrom[y][seam[y]];

    return seam;
  }

  public void removeHorizontalSeam(int[] seam) {
    validateHorizontalSeam(seam);

    int[][] newRed = new int[height() - 1][width()];
    int[][] newGreen = new int[height() - 1][width()];
    int[][] newBlue = new int[height() - 1][width()];

    for (int x = 0; x < width(); x++) {
      int removalRow = seam[x];
      for (int y = 0; y < height(); y++) {
        if (y < removalRow) {
          newRed[y][x] = red[y][x];
          newGreen[y][x] = green[y][x];
          newBlue[y][x] = blue[y][x];
        } else if (y > removalRow) {
          newRed[y - 1][x] = red[y][x];
          newGreen[y - 1][x] = green[y][x];
          newBlue[y - 1][x] = blue[y][x];
        }
      }
    }

    red = newRed;
    green = newGreen;
    blue = newBlue;
  }

  public void removeVerticalSeam(int[] seam) {
    validateVerticalSeam(seam);

    int[][] newRed = new int[height()][width() - 1];
    int[][] newGreen = new int[height()][width() - 1];
    int[][] newBlue = new int[height()][width() - 1];

    for (int y = 0; y < height(); y++) {
      int removalColumn = seam[y];
      for (int x = 0; x < width(); x++) {
        if (x < removalColumn) {
          newRed[y][x] = red[y][x];
          newGreen[y][x] = green[y][x];
          newBlue[y][x] = blue[y][x];
        } else if (x > removalColumn) {
          newRed[y][x - 1] = red[y][x];
          newGreen[y][x - 1] = green[y][x];
          newBlue[y][x - 1] = blue[y][x];
        }
      }
    }

    red = newRed;
    green = newGreen;
    blue = newBlue;
  }

  private void validateNonNullInput(Object input) {
    if (input == null)
      throw new IllegalArgumentException();
  }

  private void validateCoordinates(int x, int y) {
    if (x < 0 || x >= width() || y < 0 || y >= height())
      throw new IllegalArgumentException();
  }

  private void validateHorizontalSeam(int[] seam) {
    validateNonNullInput(seam);
    if (seam.length != width() || height() <= 1)
      throw new IllegalArgumentException();

    if (seam[0] < 0 || seam[0] >= height())
      throw new IllegalArgumentException();

    for (int i = 1; i < width(); i++)
      if (seam[i] < 0 || seam[i] >= height() || Math.abs(seam[i] - seam[i - 1]) > 1)
        throw new IllegalArgumentException();
  }

  private void validateVerticalSeam(int[] seam) {
    validateNonNullInput(seam);
    if (seam.length != height() || width() <= 1)
      throw new IllegalArgumentException();

    if (seam[0] < 0 || seam[0] >= width())
      throw new IllegalArgumentException();

    for (int i = 1; i < height(); i++)
      if (seam[i] < 0 || seam[i] >= width() || Math.abs(seam[i] - seam[i - 1]) > 1)
        throw new IllegalArgumentException();
  }

  public static void main(String[] args) {
    Picture p = new Picture(args[0]);
    SeamCarver sc = new SeamCarver(p);
    System.out.println("width: " + sc.width());
    System.out.println("height: " + sc.height());

    for (int x = 0; x < sc.width(); x++)
      for (int y = 0; y < sc.height(); y++)
        System.out.println("(" + x + ", " + y + "): " + sc.energy(x, y));

    System.out.println("horizontal seam: " + Arrays.toString(sc.findHorizontalSeam()));
    System.out.println("vertical seam: " + Arrays.toString(sc.findVerticalSeam()));

    sc.removeHorizontalSeam(sc.findHorizontalSeam());
    sc.removeVerticalSeam(sc.findVerticalSeam());
    System.out.println("width: " + sc.width());
    System.out.println("height: " + sc.height());

    for (int x = 0; x < sc.width(); x++)
      for (int y = 0; y < sc.height(); y++)
        System.out.println("(" + x + ", " + y + "): " + sc.energy(x, y));

    System.out.println("horizontal seam: " + Arrays.toString(sc.findHorizontalSeam()));
    System.out.println("vertical seam: " + Arrays.toString(sc.findVerticalSeam()));
  }
}
