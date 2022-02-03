import javazoom.jl.decoder.JavaLayerException;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import javax.swing.*;

/**
 * @author zhaoxu
 */
public class FireFlower extends Applet implements MouseListener, Runnable {
    int xClick, yClick;
    static int panelLength = 1600;
    static int panelHeight = 800;
    //烟花上升速度
    static int upSpeed = 5;
    //爆炸条数
    static int boomNum = 100;
    //重力加速度
    static double G = 9.8;
    //摩擦力加速度
    static double GM = 5;
    //半径
    static int d = 1500;
    //频率
    static double freq = 0.08;
    //烟花炸开时保留长度
    static int boomLength = 7;
    //上升图形宽度
    static int upWidth = 5;
    //上升高度
    static int upHeight = 5;
    //爆炸点宽度
    static int boomWidth = 3;
    //爆炸点高度
    static int boomHeight = 3;
    //水平速度
    static int horV = 50;
    //竖直速度
    static int verV = 40;
    //总速度
    static int sumV = 60;


    FireFlower() {
        addMouseListener(this);
    }

    @Override
    public void paint(Graphics g) {

        ImageIcon image = new ImageIcon("src\\main\\resources\\image\\1.jpg");
        getGraphics().drawImage(image.getImage(), 0, 0, getSize().width, getSize().height, this);
        super.paint(g);
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
    }

    /**
     * 使该程序能够作为应用程序执行。
     */
    public static void main(String args[]) {
        FireFlower fireFlower = new FireFlower();
        JFrame frame = new JFrame("FireFlower by:zx");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.getContentPane().add(fireFlower, BorderLayout.CENTER);
        frame.setSize(panelLength, panelHeight);
        //背景色黑色
        fireFlower.setBackground(Color.black);
        fireFlower.init();
        fireFlower.start();
        frame.setVisible(true);
        AudioPlayer audioPlayer = new AudioPlayer(new File("src/main/resources/1.mp3"));
        try {
            audioPlayer.play();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击会产生一个线程来执行烟花升空
     */
    public void run() {

        //已移动量,会递减，直到大于鼠标点击的y坐标
        int hasMoved = panelHeight;
        //需要一个线程级变量来存储单个线程的坐标
        int threadyClick = yClick;
        int threadxClick = xClick;
        //新建一个Graphics变量
        Graphics graphics = getGraphics();
        int v;
        v = 3;
        //rgb颜色变量
        int r, g, b;
        //烟花上升过程
        while (threadyClick < hasMoved) {
            hasMoved -= upSpeed;
            graphics.setColor(new Color(247, 247, 248));
            graphics.fillOval(threadxClick, hasMoved, upWidth, upHeight);
            for (int j = 0; j <= 10; j++) {
                graphics.setColor(new Color(247, 247, 248));
                graphics.fillOval(threadxClick, hasMoved + j * upSpeed, upWidth, upHeight);
            }
            graphics.setColor(Color.black);
            graphics.fillOval(threadxClick, hasMoved + upSpeed * 10, upWidth, upHeight);
            try {
                Thread.currentThread().sleep(v++);
            } catch (InterruptedException e) {
            }
        }
        //置黑色
        for (int j = 10; j >= 0; j--) {
            graphics.setColor(Color.black);
            graphics.fillOval(threadxClick, hasMoved + (j * upSpeed), upWidth, upHeight);
            try {
                Thread.currentThread().sleep((v++) / 3);
            } catch (InterruptedException e) {
            }
        }

        hasMoved = panelHeight;
        while (hasMoved > threadyClick) {
            graphics.setColor(Color.black);
            graphics.fillOval(threadxClick - 2, hasMoved, upWidth, upHeight);
            hasMoved -= upSpeed;
        }
        int atX = threadxClick;
        int atY = threadyClick;

        //初始化x、y方向初速度
        int x0 = 0;
        int y0 = 0;
        int[][] xPoints = new int[boomNum][400];
        int[][] yPoints = new int[boomNum][400];
        int[] usedSize = new int[boomNum];
        for (int j = 0; j < boomNum; j++) {
            x0 = (int) (Math.random() * (sumV * 2 + 1)) - sumV;
//            y0 = (int) (Math.random() * verV) - verV / 2;
            int yinit = (int) Math.sqrt(sumV * sumV - x0 * x0);
            y0 = yinit >= 0 ? yinit : -yinit;
            y0 = (int) (Math.random() * y0);
            for (int i = 0; i < 400; i++) {
                int y = (int) (y0 * i * freq - 0.5 * G * i * i * freq * freq - 0.5 * GM * i * i * freq * freq);
                int x = (int) (x0 * i * freq - 0.5 * GM * i * i * freq * freq);

//                x = Math.abs(x) > Math.abs(atX - xPoints[j][i - 1]) ? x : atX - xPoints[j][i - 1];
                if (x * x + y * y <= d * d) {
                    xPoints[j][i] = atX - x;
                    yPoints[j][i] = atY - y;
                    usedSize[j]++;
                } else {
                    break;
                }
            }
        }

        v = 20;

        r = (int) (Math.random() * (255 - 200 + 1) + 200);
        g = (int) (Math.random() * (255 - 150 + 1) + 150);
        b = (int) (Math.random() * (255 - 10 + 1) + 10);
        for (int j = 0; j <= 30; j++) {
            for (int i = 0; i < boomNum; i++) {
                //剔除空值
                int pointSize = 0;
                int[] thisPointsx = new int[400];
                int[] thisPointsy = new int[400];
                for (int size = 0; size < xPoints[i].length; size++) {
                    if (xPoints[i][size] != 0 && yPoints[i][size] != 0) {
                        thisPointsx[pointSize] = xPoints[i][size];
                        thisPointsy[pointSize] = yPoints[i][size];
                        pointSize++;
                    }
                }
                if (j < boomLength) {
                    graphics.setColor(new Color(247, 247, 248));
                    graphics.fillOval(thisPointsx[j], thisPointsy[j], boomWidth + 10, boomHeight);
                } else {
                    graphics.setColor(new Color(r, g, b));
                    graphics.fillOval(thisPointsx[j], thisPointsy[j], boomWidth, boomHeight);
                }

//                graphics.drawPolyline(thisPointsx, thisPointsy, usedSize[i]);

                if (j >= boomLength) {
                    graphics.setColor(Color.black);
//                    graphics.drawPolyline(thisPointsx, thisPointsy, j - boomLength);
                    graphics.fillOval(thisPointsx[j - boomLength], thisPointsy[j - boomLength], boomWidth + 10, boomHeight);
                }
            }
            v++;
            v = Math.min(v, 150);
            try {
                Thread.currentThread().sleep(v);
            } catch (InterruptedException e) {
            }
        }

        for (int i = 0; i < boomNum; i++) {
            for (int j = 0; j < 100; j++) {
                graphics.setColor(Color.black);
//                graphics.drawPolyline(xPoints[i], yPoints[i], 100);
                graphics.fillOval(xPoints[i][j], yPoints[i][j], boomWidth, boomHeight);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    /**
     * 监听鼠标按键
     *
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        xClick = e.getX();
        yClick = e.getY();
        Thread thread = new Thread(this);
        thread.start();
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
}
