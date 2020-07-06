package Pathfinding;

import java.awt.*;
import java.util.PriorityQueue;

/**
 * @author Ian Sodersjerna
 * @date 6/30/2020
 */
public class Dijkstra extends Algorithm {
    private final Node[][] nodeMap;
    //private final ArrayList<Node> visited;
    private final PriorityQueue<Node> unvisited;
    private final Color visitedColor = Color.green;
    private final Color unvisitedColor = Color.red;


    /**
     * Constructor for algorithm
     *
     * @param panel
     * @param updateWhileRunning
     */
    Dijkstra(mapPanel panel, boolean updateWhileRunning) {
        super(panel, updateWhileRunning);
        nodeMap = new Node[size.x][size.y];
        //visited = new ArrayList<>();
        unvisited = new PriorityQueue<>();
        for (int i = 0; i < nodeMap.length; ++i) {
            for (int j = 0; j < nodeMap[0].length; ++j) {
                nodeMap[i][j] = new Node(new Point(i, j), Integer.MAX_VALUE);
                if (map[i][j] == 1) {
                    nodeMap[i][j].wall = true;
                }
            }
        }
        this.unvisited.add(nodeMap[start.x][start.y]);
        this.panel.setPosition(nodeMap[start.x][start.y].position, visitedColor);
        this.nodeMap[start.x][start.y].setStart();
        this.nodeMap[end.x][end.y].end = true;
    }

    public void paintPath() {
        Node current = nodeMap[end.x][end.y];
        while (current.parent != null) {
            current = current.parent;
            this.panel.setPosition(current.position.x, current.position.y, Color.blue);
        }
        this.panel.setPosition(this.start, mapPanel.START_COLOR);
        this.panel.setPosition(this.end, mapPanel.END_COLOR);
        this.panel.paintComponent(this.panel.getGraphics());
    }

    @Override
    public void generatePath() throws IllegalArgumentException {
        Node current = null;
        while (!unvisited.isEmpty()) {
            current = unvisited.poll();
            if (current.distance == Integer.MAX_VALUE) {
                throw new IllegalArgumentException("course cannot be solved.");
            }
            //neighbors
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // Insure the current node is not selected
                    if (i != 0 || j != 0) {
                        // If the neighbor is on the board
                        if ((current.position.x + i) >= 0 && (current.position.y + j) >= 0 && (current.position.x + i) < this.size.x && (current.position.y + j) < this.size.y) {
                            // If the neighbor is wall or inaccessible due to neighboring walls
                            if (this.nodeMap[current.position.x + i][current.position.y + j].wall || ((i != 0 && j != 0) && (this.nodeMap[current.position.x + i][current.position.y].wall && this.nodeMap[current.position.x][current.position.y + j].wall))) {
                                continue;
                            }
                            if (nodeMap[current.position.x + i][current.position.y + j].distance > current.distance + distanceBetween(current.position, new Point(current.position.x + i, current.position.y + j))) {
                                nodeMap[current.position.x + i][current.position.y + j].distance = current.distance + distanceBetween(current.position, new Point(current.position.x + i, current.position.y + j));
                                nodeMap[current.position.x + i][current.position.y + j].parent = current;
                                unvisited.remove(nodeMap[current.position.x + i][current.position.y + j]);
                                unvisited.add(nodeMap[current.position.x + i][current.position.y + j]);
                                this.panel.setPosition(current.position.x + i, current.position.y + j, unvisitedColor);
                            }
                        }
                    }
                }
            }
            this.panel.setPosition(current.position.x, current.position.y, visitedColor);
            if (current.end) {
                break;
            }
            if (this.updateWhileRunning) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    GUI.panelTread.interrupt();
                    return;
                }
            }
        }
        if (current == null || !current.end) {
            GUI.panelTread.interrupt();
            throw new IllegalArgumentException("course cannot be solved.");
        }
        paintPath();
        GUI.panelTread.interrupt();
    }


    static class Node implements Comparable<Node> {
        private final Point position;
        private Node parent = null;
        private Integer distance;
        private boolean start = false, end = false, wall = false;

        @Override
        public int compareTo(Node node) {
            return this.distance.compareTo(node.distance);
        }

        Node(Point position, int dist) {
            this.position = position;
            this.distance = dist;
        }

        void setStart() {
            distance = 0;
            start = true;
        }
    }
}
