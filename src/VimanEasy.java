
import java.awt.*;
import java.awt.event.*;

import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class VimanEasy extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            switch (this.direction) {
                case 'U':
                    this.velocityX = 0;
                    this.velocityY = -tileSize / 4;
                    break;
                case 'D':
                    this.velocityX = 0;
                    this.velocityY = tileSize / 4;
                    break;
                case 'L':
                    this.velocityX = -tileSize / 4;
                    this.velocityY = 0;
                    break;
                case 'R':
                    this.velocityX = tileSize / 4;
                    this.velocityY = 0;
                    break;
                default:
                    break;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }

    }

    final int rowCount = 21;
    final int columnCount = 19;
    final int tileSize = 32;
    final int boardWidth = columnCount * tileSize;
    final int boardHeight = rowCount * tileSize;
private String playerUsername;

    final Image wallImage;
    final Image blueGhostImage;
    final Image orangeGhostImage;
    final Image pinkGhostImage;
    final Image redGhostImage;
    final Image powerFoodImage;

    final Image vimanUpImage;
    final Image vimanDownImage;
    final Image vimanLeftImage;
    final Image vimanRightImage;
    final Icon mainMenuButtonImg;
    final Icon pauseButtonImg;
    final Icon retryButtonImg;
    final Icon continueButtonImg;
    private boolean isPaused = false;
    private JDialog pauseDialog;
    private JButton pauseButton;
    private JDialog gameOverDialog;
    private boolean gameOverScreenShown = false;
    private JLabel finalScoreLabel; // Add this as a class field
    private JFrame parentFrame;

    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block viman;

    Timer gameLoop;
    char[] directions = { 'U', 'D', 'L', 'R' }; // up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    VimanEasy(String username) {
	 this.playerUsername=username;
        this.parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // load images
        wallImage = new ImageIcon(getClass().getResource("./Images/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./Images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./Images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./Images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./Images/redGhost.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("./Images/powerFood.png")).getImage();
        ;
        vimanUpImage = new ImageIcon(getClass().getResource("./Images/vimanUp.png")).getImage();
        vimanDownImage = new ImageIcon(getClass().getResource("./Images/vimanDown.png")).getImage();
        vimanLeftImage = new ImageIcon(getClass().getResource("./Images/vimanLeft.png")).getImage();
        vimanRightImage = new ImageIcon(getClass().getResource("./Images/vimanRight.png")).getImage();
        mainMenuButtonImg = new ImageIcon(getClass().getResource("./Images/mainMenuButton.png"));
        continueButtonImg = new ImageIcon(getClass().getResource("./Images/continueButton.png"));
        pauseButtonImg = new ImageIcon(getClass().getResource("./Images/pauseButton.png"));
        retryButtonImg = new ImageIcon(getClass().getResource("./Images/retryButton.png"));
        loadMap();

        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        // how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); // 20fps (1000/50)
        gameLoop.start();
        createGameOverScreen();
        createPauseMenu();
        pauseButton = new JButton(pauseButtonImg);
        pauseButton.setBounds(550, 10, 40, 40);
        pauseButton.setBackground(new java.awt.Color(0, 255, 51));
        pauseButton.addActionListener(e -> pauseGame());
        setLayout(null);
        add(pauseButton);
    
    }

private void createGameOverScreen() {
        gameOverDialog = new JDialog(parentFrame);
        gameOverDialog.setSize(300, 300); // Increased height to accommodate new labels
        gameOverDialog.setLayout(new BorderLayout());
        gameOverDialog.setLocationRelativeTo(null);
        gameOverDialog.setModal(true);
        gameOverDialog.setUndecorated(true);

        // Create main panel with black background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

        // Game Over Label
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 26));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Score Label
        finalScoreLabel = new JLabel();
        finalScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        finalScoreLabel.setForeground(Color.WHITE);
        finalScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Game Mode Label
        JLabel gameModeLabel = new JLabel("Mode: Easy");
        gameModeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gameModeLabel.setForeground(Color.GREEN);
        gameModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Label
        String username = playerUsername;
        JLabel usernameLabel = new JLabel("Player: " + username);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.BLACK);

        JButton retryButton = new JButton(retryButtonImg);
        retryButton.addActionListener(e -> {
            gameOverDialog.setVisible(false);
            gameOverScreenShown = false;
            restartGame();
        });

        JButton mainMenuButton = new JButton(mainMenuButtonImg);
        mainMenuButton.addActionListener(e -> returnToMainMenu());

        // Add components with spacing
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(gameOverLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(finalScoreLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(gameModeLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(retryButton);
        buttonPanel.add(mainMenuButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        gameOverDialog.add(mainPanel);
    }

    private void showGameOverScreen() {
        if (!gameOverScreenShown) {
            gameOverScreenShown = true;
            finalScoreLabel.setText("Final Score: " + score);
            gameOverDialog.setVisible(true);
        }
    }

    private void createPauseMenu() {
        pauseDialog = new JDialog();
        pauseDialog.setSize(300, 150);
        pauseDialog.setLayout(new BorderLayout());
        pauseDialog.setLocationRelativeTo(null);
        pauseDialog.setModal(true);
        pauseDialog.setUndecorated(true);

        // Set background color of content pane
        pauseDialog.getContentPane().setBackground(Color.BLACK);

        JLabel pauseLabel = new JLabel("Game Paused", JLabel.CENTER);
        pauseLabel.setFont(new Font("Arial", Font.BOLD, 20));
        pauseLabel.setOpaque(true);
        pauseLabel.setBackground(Color.BLACK);
        pauseLabel.setForeground(Color.WHITE);

        pauseDialog.add(pauseLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Color.BLACK);

        JButton continueButton = new JButton(continueButtonImg);
        continueButton.setOpaque(true);
        continueButton.setContentAreaFilled(false); // Optional: Makes it blend with background
        continueButton.addActionListener(e -> resumeGame());

        JButton retryButton = new JButton(retryButtonImg);
        retryButton.setOpaque(true);
        retryButton.setContentAreaFilled(false);
        retryButton.addActionListener(e -> restartGame());

        JButton mainMenuButton = new JButton(mainMenuButtonImg);
        mainMenuButton.setOpaque(true);
        mainMenuButton.setContentAreaFilled(false);
        mainMenuButton.addActionListener(e -> returnToMainMenu());

        buttonPanel.add(continueButton);
        buttonPanel.add(retryButton);
        buttonPanel.add(mainMenuButton);

        pauseDialog.add(buttonPanel, BorderLayout.CENTER);
    }

    private void pauseGame() {
        if (!isPaused) {
            isPaused = true;
            gameLoop.stop();
            pauseButton.setEnabled(false);
            pauseDialog.setVisible(true);
        }
    }

    private void restartGame() {
        score = 0;
        lives = 3;
        gameOver = false;

        // Reset the map and all entities
        loadMap();
        resetPositions();

        // Reset game controls
        isPaused = false;
        pauseDialog.setVisible(false);

        // Restart the game loop
        gameLoop.start();
        pauseButton.setEnabled(true);
    }

    private void resumeGame() {
        isPaused = false;
        pauseDialog.setVisible(false);
        gameLoop.start();
        pauseButton.setEnabled(true);
    }

    private void returnToMainMenu() {
        // Stop the game
        gameLoop.stop();

        // Hide and dispose the game over dialog if it's showing
        if (gameOverDialog != null && gameOverDialog.isVisible()) {
            gameOverDialog.setVisible(false);
            gameOverDialog.dispose();
        }

        // Hide and dispose the pause dialog if it's showing
        if (pauseDialog != null && pauseDialog.isVisible()) {
            pauseDialog.setVisible(false);
            pauseDialog.dispose();
        }

        // Create and show the menu page
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuPage().setVisible(true);
            }
        });

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.dispose();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
    }

    /**
     * loads the map
     */
    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X':
                        // block wall
                        Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                        walls.add(wall);
                        break;
                    case 'b': {
                        // blue ghost
                        Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                        break;
                    }
                    case 'o': {
                        // orange ghost
                        Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                        break;
                    }
                    case 'p': {
                        // pink ghost
                        Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                        break;
                    }
                    case 'r': {
                        // red ghost
                        Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                        break;
                    }
                    case 'P':
                        // viman
                        viman = new Block(vimanRightImage, x, y, tileSize, tileSize);
                        break;
                    case ' ':
                        // food
                        Block food = new Block(powerFoodImage, x + 14, y + 14, 4, 4);
                        foods.add(food);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(viman.image, viman.x, viman.y, viman.width, viman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.drawImage(food.image, food.x, food.y, food.width, food.height, null);
        }
        // score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        } else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }
    }

    public void move() {
        viman.x += viman.velocityX;
        viman.y += viman.velocityY;

        // check wall collisions
        for (Block wall : walls) {
            if (collision(viman, wall)) {
                viman.x -= viman.velocityX;
                viman.y -= viman.velocityY;
                break;
            }
        }

        // check ghost collisions
        for (Block ghost : ghosts) {
            if (collision(ghost, viman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        // check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(viman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        viman.reset();
        viman.velocityX = 0;
        viman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            showGameOverScreen();
        }
        if (!isPaused) {
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            pauseGame();
        }

        // Left
        // Down
        // Up
        // Right

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        // // System.out.println("KeyEvent: " + e.getKeyCode());
        // switch (e.getKeyCode()) {
        // case KeyEvent.VK_UP -> viman.updateDirection('U');
        // case KeyEvent.VK_DOWN -> viman.updateDirection('D');
        // case KeyEvent.VK_LEFT -> viman.updateDirection('L');
        // case KeyEvent.VK_RIGHT -> viman.updateDirection('R');
        // default -> {
        // }
        // }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_H, KeyEvent.VK_LEFT -> // Left
            {
                viman.updateDirection('L');
                viman.image = vimanLeftImage;
            }
            case KeyEvent.VK_J, KeyEvent.VK_DOWN -> {
                viman.updateDirection('D');
                viman.image = vimanDownImage;
            }
            case KeyEvent.VK_K, KeyEvent.VK_UP -> // Down
            {
                viman.updateDirection('U');
                viman.image = vimanUpImage;
            }
            case KeyEvent.VK_L, KeyEvent.VK_RIGHT -> {
                viman.updateDirection('R');
                viman.image = vimanRightImage;
            }
        }

        switch (viman.direction) {
            case 'U':
                viman.image = vimanUpImage;
                break;
            case 'D':
                viman.image = vimanDownImage;
                break;
            case 'L':
                viman.image = vimanLeftImage;
                break;
            case 'R':
                viman.image = vimanRightImage;
                break;
            default:
                break;
        }
    }
}