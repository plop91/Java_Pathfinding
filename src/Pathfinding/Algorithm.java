package Pathfinding;

import java.awt.*;

/**
 * @author Ian Sodersjerna
 * @date 6/30/2020
 */
public abstract class Algorithm {
    mapPanel panel;
    int[][] map;
    Point size, start, end;

    /**
     * Constructor to require MapPanel to be initialized.
     *
     * @param panel MapPanel for reference by algorithm
     */
    Algorithm(mapPanel panel) {
        this.panel = panel; // MapPanel to be drawn on and referenced
        this.start = this.panel.getStart(); // Find starting position
        this.end = this.panel.getEnd(); // Find ending position
        this.map = this.panel.getIntMap();  // get integer map from panel
        this.size = new Point(map.length, map[0].length); // get size of map
    }

    /**
     * Generate and print path to panel
     *
     * @param updateWhileRunning if the panel will be updated while running
     */
    public abstract void generatePath(boolean updateWhileRunning) throws IllegalArgumentException;

    /**
     * Method to paint the panel with the colors defined in MapPanel
     */
    public abstract void paint();

    /**
     * Gets the distance between two nodes using special method to calculate distance.
     *
     * @param p1 originating node.
     * @param p2 ending node.
     * @return distance between nodes.
     */
    public static int distanceBetween(Point p1, Point p2) {
        int deltaX = Math.abs(p2.x - p1.x);
        int deltaY = Math.abs(p2.y - p1.y);
        if (deltaX > deltaY) {
            return 14 * deltaY + 10 * (deltaX - deltaY);
        }
        return 14 * deltaX + 10 * (deltaY - deltaX);
    }
}
