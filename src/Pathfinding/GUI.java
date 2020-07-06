package Pathfinding;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;

/**
 * @author Ian Sodersjerna
 * @date 6/30/2020
 */
public class GUI extends JFrame {

    private final mapPanel panel;
    public static Color currentColor = mapPanel.WALL_COLOR;
    public static boolean updateWhileRunning = true;
    public static Thread panelTread;

    /**
     * Action Listener to run the thread that runs the A-Star pathfinding algorithm and activates the panels self
     * painting thread.
     */
    public ActionListener aStarAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            panel.clearPaths();
            new Thread(new AStar(panel,updateWhileRunning),"A-Star").start();
        }
    };

    /**
     * Action Listener to run the thread that runs the Dijkstra pathfinding algorithm and activates the panels self
     * painting thread.
     */
    public ActionListener dijkstraAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            panel.clearPaths();
            new Thread(new Dijkstra(panel,updateWhileRunning),"Dijkstra").start();
        }
    };

    /**
     * Constructor for the GUI of the program, sets up MapPanel, button panel and menuBar.
     *
     * @param mapSize  Size of map
     * @param mapScale Scale of squares on map
     */
    public GUI(Point mapSize, int mapScale) {
        //
        super("Pathfinding");

        // Add the menu bar
        setJMenuBar(menuBar());

        // Set Layout to border
        this.setLayout(new BorderLayout());

        // Add button panel
        this.add(buttonPanel(), BorderLayout.NORTH);

        // Add MapPanel and mouse listeners.
        this.panel = new mapPanel(mapSize, mapScale);
        this.add(panel, BorderLayout.SOUTH);
        panel.addMouseListener(panel);
        panel.addMouseMotionListener(panel);

        // Resize and adjust frame
        pack();

        // Set JFrame options
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        this.setVisible(true);
        //Load default map if available.
        try {
            if (new File("maps\\hello.map").exists())
                panel.load("hello");
        } catch (IOException exception) {
            panel.clearMap();
        }
    }

    /**
     * Function to create the button panel which includes helpful buttons to quickly manipulate the map.
     *
     * @return JPanel buttonPanel to be displayed above MapPanel.
     */
    public JPanel buttonPanel() {
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        ButtonGroup tileButtons = new ButtonGroup();

        JLabel tilesLabel = new JLabel("Tiles:");
        buttonPanel.add(tilesLabel);

        JButton emptyButton = new JButton("Empty");
        emptyButton.addActionListener(e -> currentColor = mapPanel.EMPTY_COLOR);
        emptyButton.setBackground(mapPanel.EMPTY_COLOR);
        emptyButton.setFocusPainted(false);
        tileButtons.add(emptyButton);
        buttonPanel.add(emptyButton);

        JButton wallButton = new JButton("Wall");
        wallButton.isDefaultButton();
        wallButton.isDefaultCapable();
        wallButton.addActionListener(e -> currentColor = mapPanel.WALL_COLOR);
        wallButton.setBackground(mapPanel.WALL_COLOR);
        if (mapPanel.WALL_COLOR == Color.black) {
            wallButton.setForeground(Color.white);
        }
        wallButton.setFocusPainted(false);
        tileButtons.add(wallButton);
        buttonPanel.add(wallButton);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> currentColor = mapPanel.START_COLOR);
        startButton.setBackground(mapPanel.START_COLOR);
        startButton.setFocusPainted(false);
        tileButtons.add(startButton);
        buttonPanel.add(startButton);

        JButton endButton = new JButton("End");
        endButton.addActionListener(e -> currentColor = mapPanel.END_COLOR);
        endButton.setBackground(mapPanel.END_COLOR);
        endButton.setFocusPainted(false);
        tileButtons.add(endButton);
        buttonPanel.add(endButton);

        JSeparator s1 = new JSeparator(SwingConstants.VERTICAL);
        s1.setForeground(Color.black);

        buttonPanel.add(s1);
        JLabel algorithmsLabel = new JLabel("Algorithms");
        buttonPanel.add(algorithmsLabel);

        JButton aStarButton = new JButton("A-Star");
        aStarButton.addActionListener(aStarAction);
        buttonPanel.add(aStarButton);

        JButton dijkstraButton = new JButton("Dijkstra");
        dijkstraButton.addActionListener(dijkstraAction);
        buttonPanel.add(dijkstraButton);

        return buttonPanel;
    }

    /**
     * Function to create the Menu bar which includes all of the available commands in the program.
     *
     * @return JMenuBar to be added to JFrame.
     */
    public JMenuBar menuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu algorithmsMenu = new JMenu("Algorithms");

        JMenu updateMenu = new JMenu("Update Mode");

        ButtonGroup updateGroup = new ButtonGroup();

        JRadioButtonMenuItem slowedRadio = new JRadioButtonMenuItem("Slowed", true);
        slowedRadio.addActionListener(e -> updateWhileRunning = true);
        updateGroup.add(slowedRadio);
        updateMenu.add(slowedRadio);

        JRadioButtonMenuItem realRadio = new JRadioButtonMenuItem("Realtime");
        realRadio.addActionListener(e -> updateWhileRunning = false);
        updateGroup.add(realRadio);
        updateMenu.add(realRadio);

        algorithmsMenu.add(updateMenu);

        JMenuItem aStarActivate = new JMenuItem("A-Star");
        aStarActivate.addActionListener(aStarAction);
        algorithmsMenu.add(aStarActivate);

        JMenuItem dijkstraActivate = new JMenuItem("Dijkstra");
        dijkstraActivate.addActionListener(dijkstraAction);
        algorithmsMenu.add(dijkstraActivate);

        menuBar.add(algorithmsMenu);

        JMenu mapMenu = new JMenu("Map");

        JMenuItem saveMapItem = new JMenuItem("Save Map");
        saveMapItem.addActionListener(e -> {
            try {
                String fileName = JOptionPane.showInputDialog(this, "Filename:");
                if (new File("maps\\" + fileName).exists()) {
                    int overwrite = JOptionPane.showConfirmDialog(this, "File exists, Overwrite?");
                    if (overwrite == 0) {
                        if (!new File("maps\\" + fileName).delete()) {
                            JOptionPane.showMessageDialog(this, "File could not be overwritten!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "File was not overwritten");
                        return;
                    }
                } else if (new File("maps\\" + fileName + ".map").exists()) {
                    int overwrite = JOptionPane.showConfirmDialog(this, "File exists, Overwrite?");
                    if (overwrite == 0) {
                        if (!new File("maps\\" + fileName + ".map").delete()) {
                            JOptionPane.showMessageDialog(this, "File could not be overwritten!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "File was not overwritten");
                        return;
                    }
                }
                panel.save(fileName);
                JOptionPane.showMessageDialog(this, "Save successful.");
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(this, "There was a problem saving this file");
            }
        });
        mapMenu.add(saveMapItem);

        JMenuItem loadMapItem = new JMenuItem("Load Map");
        loadMapItem.addActionListener(e -> {
            try {
                String fileName = JOptionPane.showInputDialog(this, "Filename:");
                panel.load(fileName);
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(this, "File does not exist");
            }
        });
        mapMenu.add(loadMapItem);
        menuBar.add(mapMenu);

        JMenu tileMenu = new JMenu("Tiles");

        ButtonGroup colorGroup = new ButtonGroup();

        JRadioButtonMenuItem emptyRadio = new JRadioButtonMenuItem("Empty");
        emptyRadio.addActionListener(e -> currentColor = mapPanel.EMPTY_COLOR);
        colorGroup.add(emptyRadio);
        tileMenu.add(emptyRadio);

        JRadioButtonMenuItem wallRadio = new JRadioButtonMenuItem("Wall", true);
        wallRadio.addActionListener(e -> currentColor = mapPanel.WALL_COLOR);
        colorGroup.add(wallRadio);
        tileMenu.add(wallRadio);

        JRadioButtonMenuItem startRadio = new JRadioButtonMenuItem("Start");
        startRadio.addActionListener(e -> currentColor = mapPanel.START_COLOR);
        colorGroup.add(startRadio);
        tileMenu.add(startRadio);

        JRadioButtonMenuItem endRadio = new JRadioButtonMenuItem("End");
        endRadio.addActionListener(e -> currentColor = mapPanel.END_COLOR);
        colorGroup.add(endRadio);
        tileMenu.add(endRadio);
        menuBar.add(tileMenu);


        JMenu clearMenu = new JMenu("Clear");

        JMenuItem clearPaths = new JMenuItem("Clear paths");
        clearPaths.addActionListener(e -> panel.clearPaths());
        menuBar.add(clearPaths);
        clearMenu.add(clearPaths);

        JMenuItem clearMap = new JMenuItem("Clear map");
        clearMap.addActionListener(e -> panel.clearMap());
        menuBar.add(clearMap);
        clearMenu.add(clearMap);

        menuBar.add(clearMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("help");
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);
        return menuBar;
    }

    /**
     * Main method of program responsible for screen sizing, insuring boxes are large and making sure the window will
     * not take up the entire screen.
     *
     * @param args command line args.
     */
    public static void main(String[] args) {
        Point idealMapSize = new Point(128, 72);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.setSize(screenSize.width / idealMapSize.x, screenSize.height / idealMapSize.y);
        new GUI(idealMapSize, (int) (screenSize.height * .8));
    }

}

class mapPanel extends JPanel implements MouseMotionListener, MouseListener, Runnable {
    private Color[][] map;
    private final int scale;
    public static final Color BORDER_COLOR = Color.black;
    public static final Color EMPTY_COLOR = Color.white;
    public static final Color WALL_COLOR = Color.black;
    public static final Color START_COLOR = Color.orange;
    public static final Color END_COLOR = Color.cyan;

    /**
     * Constructor sets panel size and initializes map.
     *
     * @param mapSize the size of the map as ints.
     * @param scale   the scale of the boxes in pixels.
     */
    public mapPanel(Point mapSize, int scale) {
        setPreferredSize(new Dimension(mapSize.x * scale, mapSize.y * scale));
        this.scale = scale;
        this.map = new Color[mapSize.x][mapSize.y];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = EMPTY_COLOR;
            }
        }
    }

    /**
     * Function to save the current state of the MapPanel to the maps directory.
     *
     * @param fileName Desired filename (if it does not contain ".map" it will be appended).
     * @throws IOException Thrown if file can not be written to.
     */
    public void save(String fileName) throws IOException {
        if (fileName.contains(".map")) {
            fileName = "maps\\" + fileName;
        } else {
            fileName = "maps\\" + fileName + ".map";
        }
        BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
        br.write(map.length + "," + map[0].length + "\n");
        int[][] intmap = getIntMap();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                br.write(intmap[i][j] + ",");
            }
            br.write("\n");
        }
        br.close();
    }

    /**
     * Loads the state of a previous MapPanel from a file in the maps directory.
     *
     * @param fileName Desired filename (if it does not contain ".map" it will be appended).
     * @throws IOException Thrown if file can not be read.
     */
    public void load(String fileName) throws IOException {
        if (fileName.contains(".map")) {
            fileName = "maps\\" + fileName;
        } else {
            fileName = "maps\\" + fileName + ".map";
        }
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        int[] arr = Arrays.stream(br.readLine().split(",")).mapToInt(Integer::parseInt).toArray();
        this.map = new Color[arr[0]][arr[1]];
        for (int i = 0; i < map.length; i++) {
            arr = Arrays.stream(br.readLine().split(",")).mapToInt(Integer::parseInt).toArray();
            for (int j = 0; j < map[0].length; j++) {
                if (arr[j] == 1) {
                    map[i][j] = WALL_COLOR;
                } else if (arr[j] == 2) {
                    map[i][j] = START_COLOR;
                } else if (arr[j] == 3) {
                    map[i][j] = END_COLOR;
                } else {
                    map[i][j] = EMPTY_COLOR;
                }
            }
        }
        br.close();
        paintComponent(getGraphics());
    }

    /**
     * Method to allow MapPanel to be run as a thread, so it can update map for the running algorithm.
     */
    @Override
    public void run() {
        while (true) {
            paintComponent(this.getGraphics());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Method to set the x, y position in the map to the provided color.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @param c Color to be set.
     */
    public void setPosition(int x, int y, Color c) {
        map[x][y] = c;
    }

    /**
     * Method to set the map to the provided color at the give point.
     *
     * @param p Point to be set.
     * @param c Color to be set.
     */
    public void setPosition(Point p, Color c) {
        map[p.x][p.y] = c;
    }

    /**
     * Returns position of starting node.
     *
     * @return position of starting node.
     */
    public Point getStart() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (START_COLOR.equals(map[i][j])) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    /**
     * Returns position of ending node.
     *
     * @return position of ending node.
     */
    public Point getEnd() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (END_COLOR.equals(map[i][j])) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    /**
     * Returns a int map of the panel.
     * <p>
     * map key: 0 = unoccupied, 1 = wall, 2 = start position, 3 = end position
     *
     * @return integer map representing current status of MapPanel.
     */
    public int[][] getIntMap() {
        int[][] temp = new int[map.length][map[0].length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                if (WALL_COLOR.equals(map[i][j])) {
                    temp[i][j] = 1;
                } else if (START_COLOR.equals(map[i][j])) {
                    temp[i][j] = 2;
                } else if (END_COLOR.equals(map[i][j])) {
                    temp[i][j] = 3;
                } else {
                    temp[i][j] = 0;
                }
            }
        }
        return temp;
    }

    /**
     * Clears entire panel.
     */
    public void clearMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = EMPTY_COLOR;
            }
        }
        this.paintComponent(this.getGraphics());
    }

    /**
     * Clears paths from panel excluding walls and the starting and ending positions.
     */
    public void clearPaths() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (!START_COLOR.equals(map[i][j]) && !END_COLOR.equals(map[i][j]) && !WALL_COLOR.equals(map[i][j])) {
                    map[i][j] = EMPTY_COLOR;
                }
            }
        }
        this.paintComponent(this.getGraphics());
    }

    /**
     * Method that alows drawing on the MapPanel, handles its own painting as thread will not be running.
     *
     * @param e MouseEvent to get the position of the cursor.
     */
    private void mouseAction(MouseEvent e) {
        Point p = new Point();
        p.x = e.getPoint().x / scale;
        p.y = e.getPoint().y / scale;
        if (p.x >= 0 && p.x < map.length && p.y >= 0 && p.y < map[0].length) {
            if (GUI.currentColor == START_COLOR) {
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[0].length; j++) {
                        if (map[i][j] == START_COLOR) {
                            map[i][j] = EMPTY_COLOR;
                        }
                    }
                }
            } else if (GUI.currentColor == END_COLOR) {
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[0].length; j++) {
                        if (map[i][j] == END_COLOR) {
                            map[i][j] = EMPTY_COLOR;
                        }
                    }
                }
            }
            setPosition(p.x, p.y, GUI.currentColor);
            paintComponent(getGraphics());
        }
    }

    /**
     * Method to paint entire Map Panel.
     *
     * @param g graphics to paint with.
     * @apiNote should be replaced with version that only paints changed squares.
     */
    @Override
    public void paintComponent(Graphics g) {
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                g.setColor(map[i][j]);
                g.fillRect(i * this.scale, j * this.scale, this.scale, this.scale);
                g.setColor(BORDER_COLOR);
                g.drawRect(i * scale, j * scale, scale, scale);
            }
        }
    }

    /**
     * Passes to mouseAction.
     *
     * @param e MouseEvent to pass to mouseAction.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseAction(e);
    }

    /**
     * Passes to mouseAction.
     *
     * @param e MouseEvent to pass to mouseAction.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseAction(e);
    }

    /**
     * Not used.
     *
     * @param e not used.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Not used.
     *
     * @param e not used.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Not used.
     *
     * @param e not used.
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Not used.
     *
     * @param e not used.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Not used.
     *
     * @param e not used.
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

}

