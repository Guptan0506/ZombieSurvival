import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class ZombieSurvival extends JPanel implements ActionListener, KeyListener {

    private int playerX = 300, playerY = 300;
    private int playerSpeed = 5;
    private int health = 100;

    private int zombieX, zombieY;
    private int zombieSpeed = 2;

    private int highScore = 0;
    private final String HIGH_SCORE_FILE = "highscore.txt";

    private boolean up, down, left, right;

    private Timer timer;
    private Timer secondTimer; // NEW: counts seconds
    private int secondsSurvived = 0; // NEW
    private int winTime = 60; // NEW: survive 60 seconds to win

    private Random rand = new Random();

    public ZombieSurvival() {
        setFocusable(true);
        addKeyListener(this);
        spawnZombie();

        loadHighScore();

        timer = new Timer(16, this); // ~60 FPS
        timer.start();

        // NEW: Timer that fires every 1000ms (1 second)
        secondTimer = new Timer(1000, e -> {
            secondsSurvived++;
            if (secondsSurvived >= winTime) {
                timer.stop();
                secondTimer.stop();
                if (secondsSurvived > highScore) {
                    highScore = secondsSurvived;
                    saveHighScore();
                    JOptionPane.showMessageDialog(this, "You survived " + winTime + " seconds! New High Score!");
                } else {
                    JOptionPane.showMessageDialog(this, "You survived " + winTime + " seconds! You win!");
                }
                System.exit(0);
            }
        });
        secondTimer.start();
    }

    private void spawnZombie() {
        zombieX = rand.nextInt(600);
        zombieY = rand.nextInt(600);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePlayer();
        moveZombie();
        checkCollision();
        repaint();
    }

    private void movePlayer() {
        if (up) playerY -= playerSpeed;
        if (down) playerY += playerSpeed;
        if (left) playerX -= playerSpeed;
        if (right) playerX += playerSpeed;
    }

    private void moveZombie() {
        if (zombieX < playerX) zombieX += zombieSpeed;
        if (zombieX > playerX) zombieX -= zombieSpeed;
        if (zombieY < playerY) zombieY += zombieSpeed;
        if (zombieY > playerY) zombieY -= zombieSpeed;
    }

    private void checkCollision() {
        Rectangle player = new Rectangle(playerX, playerY, 30, 30);
        Rectangle zombie = new Rectangle(zombieX, zombieY, 30, 30);

        if (player.intersects(zombie)) {
            health -= 1;
            if (health <= 0) {
                timer.stop();
                secondTimer.stop();
                if (secondsSurvived > highScore) {
                    highScore = secondsSurvived;
                    saveHighScore();
                    JOptionPane.showMessageDialog(this, "New High Score! " + secondsSurvived + " seconds");
                } else {
                    JOptionPane.showMessageDialog(this, "You died after " + secondsSurvived + " seconds!");
                }
                System.exit(0);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Player
        g.drawString("High Score: " + highScore + "s", 10, 100);
        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, 30, 30);

        // Zombie
        g.setColor(Color.RED);
        g.fillRect(zombieX, zombieY, 30, 30);

        // Health bar
        g.setColor(Color.GREEN);
        g.fillRect(10, 10, health * 2, 20);
        g.setColor(Color.BLACK);
        g.drawRect(10, 10, 200, 20);

        // NEW: Timer display
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Time: " + secondsSurvived + "s", 10, 50);
        g.drawString("Goal: " + winTime + "s", 10, 75);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: up = true; break;
            case KeyEvent.VK_S: down = true; break;
            case KeyEvent.VK_A: left = true; break;
            case KeyEvent.VK_D: right = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: up = false; break;
            case KeyEvent.VK_S: down = false; break;
            case KeyEvent.VK_A: left = false; break;
            case KeyEvent.VK_D: right = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Survival");
        ZombieSurvival game = new ZombieSurvival();

        frame.add(game);
        frame.setSize(640, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void loadHighScore() {
        try {
            java.io.File file = new java.io.File(HIGH_SCORE_FILE);
            if (file.exists()) {
                java.util.Scanner scanner = new java.util.Scanner(file);
                if (scanner.hasNextInt()) {
                    highScore = scanner.nextInt();
                }
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveHighScore() {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(HIGH_SCORE_FILE);
            writer.println(highScore);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}