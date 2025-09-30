import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class MeteorCreate extends JPanel implements Runnable {
    private int count;
    private Image[] meteor;
    private Image background;
    private int[] meteorX, meteorY;
    private float[] speedX;
    private float[] speedY;
    private boolean[] exploding;
    private JLabel infoText;

    boolean alive = true;

    MeteorCreate() {
        this.count = 0;
        setSize(Setting.window_Width, Setting.window_Height);
        new Thread(this).start();
    }

    public void setInfoText(JLabel infoText) {
        this.infoText = infoText;
    }

    public void setMeteor(int count) {
        this.count = count;

        meteorX = new int[count];
        meteorY = new int[count];
        meteor = new Image[count];
        speedX = new float[count];
        speedY = new float[count];
        exploding = new boolean[count];

        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            exploding[i] = false;
            int randImg = rand.nextInt(5);
            String meteorPath = System.getProperty("user.dir") + File.separator + "image" + File.separator + randImg
                    + ".png";

            meteorX[i] = rand.nextInt(600);
            meteorY[i] = rand.nextInt(540);
            speedX[i] = rand.nextInt(Setting.speedMeteor) - Setting.speedMeteor / 2;
            speedY[i] = rand.nextInt(Setting.speedMeteor) - Setting.speedMeteor / 2;
            if (speedX[i] == 0) {
                speedX[i] = 1;
            }
            if (speedY[i] == 0) {
                speedY[i] = 1;
            }

            meteor[i] = Toolkit.getDefaultToolkit().createImage(meteorPath);
        }

        updateMeteorCount();
        repaint();
    }

    public void setBackgroundImage() {
        String bgPath = System.getProperty("user.dir") + File.separator + "image" + File.separator + "background.png";
        background = Toolkit.getDefaultToolkit().createImage(bgPath);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            if (background != null) {
                g.drawImage(background, 0, 0, Setting.window_Width, Setting.window_Height, this);
            }
            for (int i = 0; i < count; i++) {
                if (meteor[i] != null)
                    g.drawImage(meteor[i], meteorX[i], meteorY[i], Setting.meteorSize, Setting.meteorSize, this);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void explode(int index) {
        if (meteor == null || exploding == null){
            return;
            //เช็คว่า array ถูกสร้างยัง
        }
        if (index >= meteor.length) {
            return;
            //เช็คว่าต้อง index มากกว่า array meteor ให้ออกจากเมดธอดนี้
        }
        if (meteor[index] == null || exploding[index]) {
            return;
            //เช็คว่า meteor หรือ exploding มีรูปยัง
        }
        exploding[index] = true;

        meteor[index] = Toolkit.getDefaultToolkit().createImage(
                System.getProperty("user.dir") + File.separator + "image" + File.separator + "explosion.gif");
        speedX[index] = 0;
        speedY[index] = 0;

        updateMeteorCount();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                meteor[index] = null;
                exploding[index] = false;
                updateMeteorCount();
            }
        }).start();
    }

    private void updateMeteorCount() {
        if (infoText == null){
            return;
        }

        int remaining = 0;
        for (int i = 0; i < count; i++) {
            if (meteor[i] != null && !exploding[i]) {
                remaining++;
            }
        }
        infoText.setText("Number of Meteorites : " + remaining);
    }

    @Override
    public void run() {
        while (alive) {
            for (int i = 0; i < count; i++) {
                if (meteor[i] == null || exploding[i]) {
                    continue;
                }

                meteorX[i] += speedX[i];
                meteorY[i] += speedY[i];

                if (meteorX[i] < -5 || meteorX[i] > Setting.window_Width - Setting.meteorSize) {
                    speedX[i] *= -Setting.speedMultiply;
                }
                if (meteorY[i] < -5 || meteorY[i] > (Setting.window_Height - 45) - Setting.meteorSize) {
                    speedY[i] *= -Setting.speedMultiply;
                }

                //เช็คไม่ให้ speed เกิน
                if (speedX[i] > Setting.maxSpeed) {
                    speedX[i] = Setting.maxSpeed;
                }
                if (speedX[i] < -Setting.maxSpeed) {
                    speedX[i] = -Setting.maxSpeed;
                }
                if (speedY[i] > Setting.maxSpeed) {
                    speedY[i] = Setting.maxSpeed;
                }
                if (speedY[i] < -Setting.maxSpeed) {
                    speedY[i] = -Setting.maxSpeed;
                }
            }

            // ตรวจสอบการชน
            for (int i = 0; i < count; i++) {
                if (meteor[i] == null || exploding[i]) {
                    continue;
                }
                for (int j = i + 1; j < count; j++) {
                    if (meteor[j] == null || exploding[j]) {
                        continue;
                    }
                    // สูตรหาระยะทาง
                    double distance = Math.sqrt(
                            Math.pow(meteorX[i] - meteorX[j], 2) + Math.pow(meteorY[i] - meteorY[j], 2));

                    //เช็คระยะทางกับขนาดอุกกาบาต
                    if (distance < (float) (Setting.meteorSize) / 2) {
                        //ถ้าระยะทางน้อยกว่าขนาดอุกกาบาตหาร 2 = ชน
                        float speed1 = Math.abs(speedX[i]) + Math.abs(speedY[i]);//abs = คือค่าเป็น + เสมอ
                        float speed2 = Math.abs(speedX[j]) + Math.abs(speedY[j]);

                        if (speed1 < speed2) {
                            explode(i);
                            speedX[j] = -speedX[j];
                            speedY[j] = -speedY[j];
                        } else {
                            explode(j);
                            speedX[i] = -speedX[i];
                            speedY[i] = -speedY[i];
                        }
                    }
                }
            }

            repaint();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
        }
    }
}
