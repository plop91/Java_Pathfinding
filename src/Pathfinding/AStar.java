package Pathfinding;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Ian Sodersjerna
 * @date 6/30/2020
 */
public class AStar extends Algorithm {
    private final Node[][] nodeMap;
    private final ArrayList<Node> open;
    private final ArrayList<Node> closed;
    private final ArrayList<Node> neighbors;
    private Node current;

    /**
     * Constructor for algorithm
     *
     * @param panel panel to be used by algorithm
     */
    public AStar(mapPanel panel) {
        super(panel); // Pass panel to super constructor
        this.nodeMap = new Node[this.size.x][this.size.y];
        this.open = new ArrayList<>(); // The set of nodes to be evaluated
        this.closed = new ArrayList<>(); // The set of nodes already evaluated
        this.current = null; // The node that will be evaluated
        this.neighbors = new ArrayList<>(); // The set of neighboring nodes to the current node
        // Create a 2-d Node array and set walls
        for (int i = 0; i < size.x; i++) {
            for (int j = 0; j < size.y; j++) {
                this.nodeMap[i][j] = new Node(new Point(i, j), end);
                if (map[i][j] == 1) {
                    this.nodeMap[i][j].wall = true;
                }
            }
        }
        this.nodeMap[start.x][start.y].setStartNode();
        this.nodeMap[end.x][end.y].setEndNode();
        this.open.add(this.nodeMap[start.x][start.y]);
    }

    /**
     * Generate and print path to panel
     *
     * @param updateWhileRunning if the panel will be updated while running
     */
    public void generatePath(boolean updateWhileRunning) throws IllegalArgumentException{
        while (!this.open.isEmpty()) {
            // Find the node in the open set with the lowest f cost.
            this.current = this.open.get(0);
            for (Node n : this.open) {
                if (n.getFCost() < this.current.getFCost() || n.getFCost() == this.current.getFCost()) {
                    if (n.hCost < this.current.hCost) {
                        this.current = n;
                    }
                }
            }
            // Remove selected node from open set and add it to the closed set checking if it is the end node and clearing neighbors
            this.open.remove(this.current);
            this.closed.add(this.current);
            if (this.current.end) {
                break;
            }
            this.neighbors.clear();
            // Populate neighbor set with neighbors
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // Insure the current node is not added
                    if (i != 0 || j != 0) {
                        // If the neighbor is on the board
                        if ((this.current.position.x + i) >= 0 && (this.current.position.y + j) >= 0 && (this.current.position.x + i) < this.size.x && (this.current.position.y + j) < this.size.y) {
                            // If the neighbor is inaccessible due to neighboring walls
                            if ((i != 0 && j != 0) && (this.nodeMap[this.current.position.x + i][this.current.position.y].wall && this.nodeMap[this.current.position.x][this.current.position.y + j].wall)) {
                                continue;
                            }
                            this.neighbors.add(this.nodeMap[this.current.position.x + i][this.current.position.y + j]);
                        }
                    }
                }
            }
            //for each neighbor calculate F, G, and H costs and assign parent
            for (Node n : this.neighbors) {
                if (n.wall || this.closed.contains(n)) {
                    continue;
                }
                int newCostToNeighbour = this.current.gCost + distanceBetween(n.position, this.current.position);
                if (!this.open.contains(n) || newCostToNeighbour < n.gCost) {
                    n.gCost = newCostToNeighbour;
                    n.setHCost(this.end);
                    n.parent = this.current;
                    if (!this.open.contains(n)) {
                        this.open.add(n);
                    }
                }
            }
            if (updateWhileRunning)
                this.paint();
        }
        if (current == null || !current.end){
            throw new IllegalArgumentException("course cannot be solved.");
        }
        this.paint();
        this.paintPath();
    }

    /**
     * Method to paint the panel with the colors defined in MapPanel
     */
    public void paint() {
        for (int i = 0; i < this.size.x; i++) {
            for (int j = 0; j < this.size.y; j++) {
                if (nodeMap[i][j].wall) {
                    this.panel.setPosition(i, j, mapPanel.WALL_COLOR);
                } else if (nodeMap[i][j].start) {
                    this.panel.setPosition(i, j, mapPanel.START_COLOR);
                } else if (nodeMap[i][j].end) {
                    this.panel.setPosition(i, j, mapPanel.END_COLOR);
                } else if (this.open.contains(nodeMap[i][j])) {
                    this.panel.setPosition(i, j, Color.green);
                } else if (this.closed.contains(nodeMap[i][j])) {
                    this.panel.setPosition(i, j, Color.red);
                }
            }
        }
    }

    /**
     * Method to paint the panel the path generated using the colors defined in MapPanel.
     */
    public void paintPath() {
        Stack<Point> path = new Stack<>();
        while (this.current.parent != null) {
            path.push(current.position);
            this.current = this.current.parent;
        }
        while (!path.empty()) {
            this.panel.setPosition(path.pop(), Color.blue);
        }
        this.panel.setPosition(this.start, mapPanel.START_COLOR);
        this.panel.setPosition(this.end, mapPanel.END_COLOR);
        this.panel.paintComponent(this.panel.getGraphics());
    }

    static class Node {
        private final Point position; //location of the Node on a 2d plane.
        private boolean wall = false; //boolean value to determine if a wall.
        private boolean start = false; //boolean value to determine if the starting node
        private boolean end = false; //boolean value to determine if the ending node
        private int gCost; //distance from starting node
        private int hCost; //distance from end node
        private Node parent = null; // parent of the node.

        /**
         * Constructor for a node with distance to ending node.
         *
         * @param position Location the node is located on a 2-d plane.
         * @param target   Location of the target to determine hcost(distance from the end node)
         */
        public Node(Point position, Point target) {
            this.position = position;
            hCost = AStar.distanceBetween(position, target);
        }
        public void setStartNode() {
            start = true;
            gCost = 0;
        }
        public void setEndNode() {
            end = true;
            hCost = 0;
        }
        public int getFCost() {
            return hCost + gCost;
        }
        public void setHCost(Point hCost) {
            this.hCost = AStar.distanceBetween(position, hCost);
        }
    }
}


