package org.server;

import java.util.List;

public interface Strategy {
    void findPattern(Color c,List<Integer> cardsWithTheSameColor);
}
