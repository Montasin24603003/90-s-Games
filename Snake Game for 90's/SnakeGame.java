// Updated full Snake Game code with Timer fix
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import java.io.*;
import javax.sound.sampled.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 25;
    private final int WIDTH = 600;
    private final int HEIGHT = 600;

    private LinkedList<Point> snake;
    private Point food;
    private Point enemy;

    private int direction = KeyEvent.VK_RIGHT;
    private javax.swing.Timer timer;
    private Random rand;
    private boolean gameOver = false;
    private boolean paused = false;

    private int level = 1;
    private int score = 0;
    private int highScore = 0;

    private boolean powerSpeed = false;
    private boolean powerShield = false;

    private int skin = 1;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        snake = new LinkedList<>();
        rand = new Random();

        loadHighScore();
        resetGame();

        timer = new javax.swing.Timer(120, this);
        timer.start();
    }

    private void resetGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        snake.add(new Point(4, 5));
        snake.add(new Point(3, 5));
        spawnFood();
        spawnEnemy();
        score = 0;
        level = 1;
    }

    private void spawnFood() {
        food = new Point(rand.nextInt(WIDTH / TILE_SIZE), rand.nextInt(HEIGHT / TILE_SIZE));
    }

    private void spawnEnemy() {
        enemy = new Point(rand.nextInt(WIDTH / TILE_SIZE), rand.nextInt(HEIGHT / TILE_SIZE));
    }

    private void loadHighScore() {
        try {
            File file = new File("highscore.dat");
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                highScore = Integer.parseInt(br.readLine());
                br.close();
            }
        } catch (Exception ignored) {}
    }

    private void saveHighScore() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("highscore.dat"));
            bw.write(String.valueOf(highScore));
            bw.close();
        } catch (Exception ignored) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Food
        g.setColor(Color.red);
        g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Enemy
        g.setColor(Color.magenta);
        g.fillRect(enemy.x * TILE_SIZE, enemy.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Snake skins
        Color snakeColor = switch (skin) {
            case 2 -> Color.cyan;
            case 3 -> Color.yellow;
            default -> Color.green;
        };

        for (Point p : snake) {
            g.setColor(snakeColor);
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        g.setColor(Color.white);
        g.drawString("Score: " + score + "  High: " + highScore + "  Level: " + level, 10, 20);

        if (paused) {
            g.setFont(new Font("Arial", Font.BOLD, 35));
            g.drawString("PAUSED", WIDTH/2 - 80, HEIGHT/2);
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", WIDTH / 2 - 120, HEIGHT / 2);
            g.drawString("Press R to Restart", WIDTH/2 - 150, HEIGHT/2 + 50);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !paused) move();
        repaint();
    }

    private void move() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case KeyEvent.VK_UP -> newHead.y--;
            case KeyEvent.VK_DOWN -> newHead.y++;
            case KeyEvent.VK_LEFT -> newHead.x--;
            case KeyEvent.VK_RIGHT -> newHead.x++;
        }

        // Crash / kill check
        if (newHead.equals(enemy)) {
            gameOver();
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            score += 10;
            if (score > highScore) highScore = score;
            saveHighScore();
            spawnFood();
            levelUp();
        } else {
            snake.removeLast();
        }
    }

    private void levelUp() {
        if (score % 50 == 0) {
            level++;
            timer.setDelay(Math.max(40, 120 - (level * 10)));
        }
    }

    private void gameOver() {
        gameOver = true;
        timer.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!paused && !gameOver) {
            if ((key == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) ||
                (key == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) ||
                (key == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) ||
                (key == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT)) {
                direction = key;
            }
        }

        if (key == KeyEvent.VK_P) paused = !paused;
        if (key == KeyEvent.VK_R) restartGame();
        if (key == KeyEvent.VK_1) skin = 1;
        if (key == KeyEvent.VK_2) skin = 2;
        if (key == KeyEvent.VK_3) skin = 3;
    }

    private void restartGame() {
        gameOver = false;
        paused = false;
        direction = KeyEvent.VK_RIGHT;
        resetGame();
        timer.start();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game - Enhanced");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
