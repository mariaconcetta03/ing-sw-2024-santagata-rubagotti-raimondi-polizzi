package org.server;

import java.util.List;

public interface Strategy {
    void findPattern(List<Coordinates> cardsWithTheSameColor,Color c);
}
