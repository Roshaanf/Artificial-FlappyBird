/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flappybird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author Roshaann 2.7 gpa
 */
public class FlappyBird implements ActionListener, MouseListener {

    public static FlappyBird flappyBird;

    public final int WIDTH = 700, HEIGHT = 700;
    int score;

    public final int TIMER_DELAY_IN_MILI_S = 20;
    public final int PIPE_SPEED = 10;

    Random rand;

    Rectangle bird;
    Renderer renderer;
    ArrayList<Rectangle> pipes;

//    for bird motion
    int ticks;
    int yMotion = 0;
    
      Timer timer ;

    boolean gameOver, isStarted;

    FlappyBird() {

//        initializing JFrame
        JFrame frame = new JFrame();

         timer = new Timer(TIMER_DELAY_IN_MILI_S, this);

//        initializing Jpanel
        renderer = new Renderer();

//        setting JFrame attributes
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.addMouseListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Flappy Bird");
        frame.setVisible(true);

        //     adding renderer to JFRAme
        frame.add(renderer);

//        creating bird in the center of the screen x,y,width,height
        bird = new Rectangle(WIDTH / 2, HEIGHT / 2, 20, 20);
        pipes = new ArrayList<>();

        rand = new Random();

        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);

//        starting timer
        timer.start();

    }

    public void addPipe(boolean isStart) {

//        vertical space bw pipes
        int verticalSpace = 300;
        int horizontalSpace = 600;
        int width = 100;
        int height = 50 + rand.nextInt(200);

        if (isStart) {

//            addding lower pipe ( horizontal space 600          , -120 for grass and land, ,                            
            pipes.add(new Rectangle(WIDTH + width + pipes.size() * 300, HEIGHT - height - 120, width, height));
//            upper pipe            x of prev lower pipe
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, width, HEIGHT - height - verticalSpace));
        } else {
//            lower pipe            (add 600 horizontal to prev pipe
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x + horizontalSpace, HEIGHT - height - 120, width, height));
//            upper pipe
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, width, HEIGHT - height - verticalSpace));
        }

    }

    public void paintPipe(Graphics graphics, Rectangle pipe) {

        graphics.setColor(Color.green.darker());
        graphics.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
    }

    public void repaint(Graphics graphics) {

//        setting full screen green screen color
        graphics.setColor(Color.cyan);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

//        setting ground color
        graphics.setColor(Color.ORANGE);
        graphics.fillRect(0, HEIGHT - 120, WIDTH, 120);

//        setting grass on ground
        graphics.setColor(Color.GREEN);
        graphics.fillRect(0, HEIGHT - 120, WIDTH, 20);

//        setting bird color
        graphics.setColor(Color.red);
        graphics.fillRect(bird.x, bird.y, bird.width, bird.height);

//        painting pipes
        for (Rectangle pipe : pipes) {
            paintPipe(graphics, pipe);
        }

//        showing game over or click to start
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", 1, 80));

        if (!isStarted) {
            graphics.drawString("Click to start!", 75, HEIGHT / 2 - 50);
        } else if (gameOver) {
            graphics.drawString("Game Over", 100, HEIGHT / 2 - 50);
        } //      show score
        else {
            graphics.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        timer call back, this will execute after every 20 mili seconds
        ticks++;
     
        if (isStarted) {
            
//        moving rectangles
            for (Rectangle pipe : pipes) {
                pipe.x -= PIPE_SPEED;
            }

//            calculating bird fall  motion
            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

//        removing pipes that are gone from the screen
//        it was crashing with for each loop thats why using thi loop
            for (int i = 0; i < pipes.size(); i++) {

                Rectangle pipe = pipes.get(i);

//            if pipes is gone
                if (pipe.x + pipe.getWidth() <= 0) {

//                because we added lower pipe first so lower pipe will be removed and thn upper
                    pipes.remove(pipe);
                    /*   because we have to remove two pipes(upper and lower) simultaneously we will add new pipe
                     only when both(upper and lower) pipes are removed*/

//                upper pipe will have y coordinate==0
                    if (pipe.y == 0) {
                        addPipe(false);
                    }
                }
            }

//        bird fall motion
            bird.y += yMotion;

//        checking game terminating condtiiton and collisions
            for (Rectangle pipe : pipes) {

//                calculating score checking for y=0 so only one time score is count not for two times for upper and lower pipes
                if (pipe.y == 0 && pipe.x+pipe.width==bird.x) {
                    score++; 
//                    timer.stop();
                    System.out.println(pipe);
                }

                if (pipe.intersects(bird)) {
                    gameOver = true;
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y <= 0) {
                gameOver = true;
            }

        }
        
//      calling repaing of renderer will call paint component in Renderer which eventually will call repaint of Flappy bird(this class)
        renderer.repaint();
        
    }

    public void jump() {

        if (!isStarted) {
            isStarted = true;
        } else if (gameOver) {
//            resetting eveything

//        creating bird in the center of the screen x,y,width,height
            bird = new Rectangle(WIDTH / 2, HEIGHT / 2, 20, 20);
            pipes = new ArrayList<>();

            yMotion = 0;
            score = 0;
            rand = new Random();

            addPipe(true);
            addPipe(true);
            addPipe(true);
            addPipe(true);

            gameOver = false;

        } //        jump
        else {

            yMotion = 0;
            yMotion -= 10;
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        jump();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        flappyBird = new FlappyBird();
    }

}
