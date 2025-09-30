import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyFrame extends JFrame {
    public static int numOfMeteor = Setting.defaultMeteor;

    MyFrame() {
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setLayout(new BorderLayout());

        JPanel bg = new JPanel();
        bg.setLayout(null);
        bg.setBackground(Color.BLACK);

        JPanel actPanel = new JPanel();
        actPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        TextField input = new TextField("" + Setting.defaultMeteor, 4);

        // ใช้ infoText ของคุณเพื่อแสดงจำนวน meteor
        JLabel infoText = new JLabel("Number of Meteorites : " + Setting.defaultMeteor, JLabel.CENTER);
        infoText.setFont(new Font("Tahoma", Font.BOLD, 16));
        infoText.setForeground(Color.LIGHT_GRAY);
        infoText.setBounds(10, 10, 300, 30);

        MeteorCreate meteorCreate = new MeteorCreate();
        meteorCreate.setBounds(0, 0, Setting.window_Width, Setting.window_Height);
        meteorCreate.setBackground(Color.BLACK);
        meteorCreate.setBackgroundImage();
        meteorCreate.setInfoText(infoText);

        JButton addButton = new JButton("ADD");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int addCount;
                try {
                    addCount = Integer.parseInt(input.getText());
                    if (addCount < Setting.minMeteor)
                        addCount = Setting.minMeteor;
                    if (addCount > Setting.maxMeteor)
                        addCount = Setting.maxMeteor;
                } catch (NumberFormatException ex) {
                    addCount = Setting.defaultMeteor;
                }

                numOfMeteor = addCount;
                meteorCreate.setMeteor(numOfMeteor);
            }
        });

        JButton clearButton = new JButton("CLEAR");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numOfMeteor = 0;
                meteorCreate.setMeteor(0);
            }
        });

        actPanel.add(input);
        actPanel.add(addButton);
        actPanel.add(clearButton);

        bg.add(meteorCreate);
        mainPanel.add(infoText, BorderLayout.NORTH);

        mainPanel.add(bg, BorderLayout.CENTER);
        mainPanel.add(actPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
}
