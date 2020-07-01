package Pathfinding;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Ian Sodersjerna
 * @date 6/30/2020
 */
public class Dijkstra extends Algorithm {
    private final Node[][] nodeMap;
    private final ArrayList<Node> visited;
    private final ArrayList<Node> unvisited;

    Dijkstra(mapPanel panel) {
        super(panel);
        nodeMap = new Node[size.x][size.y];
        visited = new ArrayList<>();
        unvisited = new ArrayList<>();
        for (int i = 0; i < nodeMap.length; ++i) {
            for (int j = 0; j < nodeMap[0].length; ++j) {
                nodeMap[i][j] = new Node(new Point(i, j), Integer.MAX_VALUE);
                if (map[i][j] == 1) {
                    nodeMap[i][j].wall =true;
                } else {
                    unvisited.add(nodeMap[i][j]);
                }
            }
        }
        this.nodeMap[start.x][start.y].setStart();
        this.nodeMap[end.x][end.y].end = true;
    }

    @Override
    public void paint() {
        for (int i = 0; i < this.size.x; i++) {
            for (int j = 0; j < this.size.y; j++) {
                if (nodeMap[i][j].wall) {
                    this.panel.setPosition(i, j, mapPanel.WALL_COLOR);
                } else if (nodeMap[i][j].start) {
                    this.panel.setPosition(i, j, mapPanel.START_COLOR);
                } else if (nodeMap[i][j].end) {
                    this.panel.setPosition(i, j, mapPanel.END_COLOR);
                } else if (this.unvisited.contains(nodeMap[i][j])) {
                    this.panel.setPosition(i, j, Color.green);
                } else if (this.visited.contains(nodeMap[i][j])) {
                    this.panel.setPosition(i, j, Color.red);
                }
            }
        }
        this.panel.paintComponent(this.panel.getGraphics());
    }
    public void paintPath(){
        Node current = nodeMap[end.x][end.y];
        while (current.parent!=null){
            current = current.parent;
            this.panel.setPosition(current.position.x,current.position.y,Color.blue);
        }
        this.panel.setPosition(this.start, mapPanel.START_COLOR);
        this.panel.setPosition(this.end, mapPanel.END_COLOR);
        this.panel.paintComponent(this.panel.getGraphics());
    }

    @Override
    public void generatePath(boolean updateWhileRunning) throws IllegalArgumentException{
        Node current = null;
        while (!unvisited.isEmpty()) {
            current = minDistance();
            if(current.distance == Integer.MAX_VALUE){
                throw new IllegalArgumentException("course cannot be solved.");
            }
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // Insure the current node is not selected
                    if (i != 0 || j != 0) {
                        // If the neighbor is on the board
                        if ((current.position.x + i) >= 0 && (current.position.y + j) >= 0 && (current.position.x + i) < this.size.x && (current.position.y + j) < this.size.y) {
                            // If the neighbor is inaccessible due to neighboring walls
                            if ((i != 0 && j != 0) && (this.nodeMap[current.position.x + i][current.position.y].wall && this.nodeMap[current.position.x][current.position.y + j].wall)) {
                                continue;
                            }
                            //check the unvisited set
                            for (Node node : unvisited) {
                                if (node.position.equals(new Point(current.position.x + i, current.position.y + j))) {
                                    if (node.distance > current.distance + distanceBetween(current.position, new Point(current.position.x + i, current.position.y + j))) {
                                        node.distance = current.distance + distanceBetween(current.position, new Point(current.position.x + i, current.position.y + j));
                                        node.parent = current;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            visited.add(current);
            unvisited.remove(current);
            if(current.end){
                break;
            }
            if(updateWhileRunning){
                paint();
            }
        }
        paintPath();
    }

    Node minDistance() {
        Node temp = new Node(new Point(), Integer.MAX_VALUE);
        for (Node node : unvisited) {
            if (node.distance <= temp.distance) {
                temp = node;
            }
        }
        return temp;
    }

    class Node {
        private final Point position;
        private Node parent = null;
        private int distance;
        private boolean start = false, end = false, wall = false;

        Node(Point position, int dist) {
            this.position = position;
            this.distance = dist;
        }

        void setStart(){
            distance = 0;
            start = true;
        }
    }
}
