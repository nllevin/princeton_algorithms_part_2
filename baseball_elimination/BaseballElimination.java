/* *****************************************************************************
 *  Name:              Noah Levin
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BaseballElimination {
  private final HashMap<String, Integer> teamIndices = new HashMap<>();
  private final String[] teamNames;
  private final int[] wins;
  private final int[] losses;
  private final int[] remaining;
  private final int[][] schedule;
  private final boolean[] eliminated;
  private final Iterable<String>[] eliminationSets;
  private int maxWins = Integer.MIN_VALUE;
  private String divisionLeader = "";

  public BaseballElimination(String filename) {
    In input = new In(filename);
    int numTeams = Integer.parseInt(input.readLine());
    teamNames = new String[numTeams];
    wins = new int[numTeams];
    losses = new int[numTeams];
    remaining = new int[numTeams];
    schedule = new int[numTeams][numTeams];
    eliminated = new boolean[numTeams];
    eliminationSets = (Iterable<String>[]) new Bag[numTeams];

    for (int teamIndex = 0; teamIndex < numTeams; teamIndex++) {
      processTeamData(teamIndex, input);
      if (wins[teamIndex] > maxWins) {
        maxWins = wins[teamIndex];
        divisionLeader = teamNames[teamIndex];
      }
    }
    for (int teamIndex = 0; teamIndex < numTeams; teamIndex++)
      determineTeamElimination(teamIndex);
  }

  private void processTeamData(int teamIndex, In input) {
    String[] teamData = input.readLine().trim().split("\\s+");
    teamIndices.put(teamData[0], teamIndex);
    teamNames[teamIndex] = teamData[0];
    wins[teamIndex] = Integer.parseInt(teamData[1]);
    losses[teamIndex] = Integer.parseInt(teamData[2]);
    remaining[teamIndex] = Integer.parseInt(teamData[3]);
    for (int opponentIndex = 0; opponentIndex < numberOfTeams(); opponentIndex++) {
      schedule[teamIndex][opponentIndex] = Integer.parseInt(teamData[4 + opponentIndex]);
    }
  }

  private void determineTeamElimination(int teamIndex) {
    checkTrivialElimination(teamIndex);
    if (!eliminated[teamIndex])
      checkNonTrivialElimination(teamIndex);
  }

  private void checkTrivialElimination(int teamIndex) {
    if (wins[teamIndex] + remaining[teamIndex] >= maxWins)
      return;

    eliminated[teamIndex] = true;
    Bag<String> r = new Bag<>();
    r.add(divisionLeader);
    eliminationSets[teamIndex] = r;
  }

  private void checkNonTrivialElimination(int teamIndex) {
    int stVertices = 2;
    int otherTeamVertices = numberOfTeams() - 1;
    int matchupVertices = otherTeamVertices * (otherTeamVertices - 1) / 2;
    int numVertices = stVertices + otherTeamVertices + matchupVertices;
    FlowNetwork fn = new FlowNetwork(numVertices);

    int teamVerticesStart = 1 + matchupVertices;
    buildMatchupEdges(teamIndex, fn, teamVerticesStart);
    buildWinLimitEdges(teamIndex, fn, teamVerticesStart, numVertices);

    FordFulkerson ff = new FordFulkerson(fn, 0, numVertices - 1);
    if (inContention(ff, matchupVertices))
      return;

    eliminated[teamIndex] = true;
    buildEliminationSet(teamIndex, ff, teamVerticesStart);
  }

  private void buildMatchupEdges(int teamIndex, FlowNetwork fn, int teamVerticesStart) {
    int s = 0;
    int matchupVertex = 1;
    for (int i = 0; i < numberOfTeams(); i++) {
      if (i == teamIndex)
        continue;

      int teamVertex1 = teamVerticesStart + i - (i > teamIndex ? 1 : 0);

      for (int j = i + 1; j < numberOfTeams(); j++) {
        if (j == teamIndex)
          continue;

        int teamVertex2 = teamVerticesStart + j - (j > teamIndex ? 1 : 0);

        FlowEdge matchup = new FlowEdge(s, matchupVertex, schedule[i][j]);
        FlowEdge matchupToTeam1 = new FlowEdge(matchupVertex, teamVertex1, Integer.MAX_VALUE);
        FlowEdge matchupToTeam2 = new FlowEdge(matchupVertex, teamVertex2, Integer.MAX_VALUE);
        fn.addEdge(matchup);
        fn.addEdge(matchupToTeam1);
        fn.addEdge(matchupToTeam2);

        matchupVertex++;
      }
    }
  }

  private void buildWinLimitEdges(int teamIndex, FlowNetwork fn, int teamVerticesStart, int numVertices) {
    int t = numVertices - 1;
    for (int i = 0; i < numberOfTeams(); i++) {
      if (i == teamIndex)
        continue;

      int teamVertex = teamVerticesStart + i - (i > teamIndex ? 1 : 0);
      int winLimit = wins[teamIndex] + remaining[teamIndex] - wins[i];
      FlowEdge winLimitEdge = new FlowEdge(teamVertex, t, winLimit);
      fn.addEdge(winLimitEdge);
    }
  }

  private boolean inContention(FordFulkerson ff, int numMatchups) {
    for (int i = 1; i <= numMatchups; i++)
      if (ff.inCut(i))
        return false;

    return true;
  }

  private void buildEliminationSet(int teamIndex, FordFulkerson ff, int teamVerticesStart) {
    Bag<String> r = new Bag<>();
    for (int i = numberOfTeams() - 1; i >= 0; i--) {
      if (i == teamIndex)
        continue;

      int teamVertex = teamVerticesStart + i - (i > teamIndex ? 1 : 0);
      if (ff.inCut(teamVertex)) {
        r.add(teamNames[i]);
      }
    }
    eliminationSets[teamIndex] = r;
  }

  public int numberOfTeams() {
    return schedule.length;
  }

  public Iterable<String> teams() {
    Bag<String> teams = new Bag<>();
    for (int i = numberOfTeams() - 1; i >= 0; i--)
      teams.add(teamNames[i]);
    return teams;
  }

  public int wins(String team) {
    validateTeam(team);
    return wins[teamIndices.get(team)];
  }

  public int losses(String team) {
    validateTeam(team);
    return losses[teamIndices.get(team)];
  }

  public int remaining(String team) {
    validateTeam(team);
    return remaining[teamIndices.get(team)];
  }

  public int against(String team1, String team2) {
    validateTeam(team1);
    validateTeam(team2);
    return schedule[teamIndices.get(team1)][teamIndices.get(team2)];
  }

  public boolean isEliminated(String team) {
    validateTeam(team);
    return eliminated[teamIndices.get(team)];
  }

  public Iterable<String> certificateOfElimination(String team) {
    validateTeam(team);
    return eliminationSets[teamIndices.get(team)];
  }

  private void validateTeam(String team) {
    if (!teamIndices.containsKey(team))
      throw new IllegalArgumentException();
  }

  public static void main(String[] args) {
    BaseballElimination division = new BaseballElimination(args[0]);
    System.out.println(division.numberOfTeams());
    for (String team : division.teams()) {
      System.out.println(team + ": " + division.wins(team) + "-" + division.losses(team) + "-" + division.remaining(team));
    }
    System.out.println();
    for (String team1 : division.teams()) {
      System.out.println(team1);
      for (String team2 : division.teams()) {
        System.out.println(team2 + ": " + division.against(team1, team2));
      }
      System.out.println();
    }

//    System.out.println(division.isEliminated("Philadelphia"));
//    for (String t : division.certificateOfElimination("Philadelphia")) {
//      StdOut.print(t + " ");
//    }
//    System.out.println();
//    System.out.println(division.isEliminated("Atlanta"));
//    for (String t : division.certificateOfElimination("Atlanta")) {
//      StdOut.print(t + " ");
//    }

    for (String team : division.teams()) {
      if (division.isEliminated(team)) {
        StdOut.print(team + " is eliminated by the subset R = { ");
        for (String t : division.certificateOfElimination(team)) {
          StdOut.print(t + " ");
        }
        StdOut.println("}");
      } else {
        StdOut.println(team + " is not eliminated");
      }
    }
  }
}
