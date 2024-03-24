package org.model;

import java.util.ArrayList;
import java.util.List;

public class Strategy1 implements Strategy {
    @Override
    public int check(Player player) {
        Board playerBoard = player.getBoard();
        List<Coordinates> alreadyUsedCoordinates = new ArrayList<>();
        PlayableCard[][] table;
        int dim = playerBoard.getBoardDimensions();
        int foundPatterns = 0;
        table = playerBoard.getTable();


        for (int i = 2; i<dim; i++) {
            for (int j=0; j<dim -2; j++) {
                if (!alreadyUsedCoordinates.contains(new Coordinates(i,j))) {
                    if (table[i][j].getCentralResources().size() == 1 && table[i][j].getCentralResources().get(0).equals("FUNGI")) {
                        if (table[i - 1][j + 1].getCentralResources().size() == 1 && table[i][j].getCentralResources().get(0).equals("FUNGI")) {
                            if (table[i - 2][j + 2].getCentralResources().size() == 1 && table[i][j].getCentralResources().get(0).equals("FUNGI")) {
                                foundPatterns++;
                                alreadyUsedCoordinates.add(new Coordinates(i, j));
                                alreadyUsedCoordinates.add(new Coordinates(i - 1, j + 1));
                                alreadyUsedCoordinates.add(new Coordinates(i - 2, j + 2));
                            }
                        }
                    }
                }
            }
        }
        return foundPatterns;
    }

}

