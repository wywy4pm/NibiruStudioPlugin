package com.nibiru.plugin.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

public class AboutNibiruView extends JPanel {

    public static void showAbout() {
        JFrame jf = new JFrame("");
        jf.setSize(300, 150);
        jf.setIconImage(null);
        jf.add(new AboutNibiruView());
        jf.setUndecorated(true);
        jf.setLocationRelativeTo(null);
        jf.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        JPanel jp = new JPanel();
        Box box=Box.createVerticalBox();
        box.setSize(300,150);
        box.add(Box.createVerticalStrut(20));
        box.add(new JLabel("关于Nibiru"));
        box.add(Box.createVerticalStrut(20));
        box.add(new JLabel("当前版本: v1.0"));
        box.add(Box.createVerticalStrut(20));
        box.add(new JLabel("发布日期: 2020/3/20"));
        jp.setBackground(Color.DARK_GRAY);
        jp.add(box);
        jf.add(jp);
        jf.setVisible(true);


        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addVetoableChangeListener("focusedWindow",
                        new VetoableChangeListener() {
                            private boolean gained = false;
                            @Override
                            public void vetoableChange(PropertyChangeEvent evt)
                                    throws PropertyVetoException {
                                if (evt.getNewValue() == jf) {
                                    gained = true;
                                }
                                if (gained && evt.getNewValue() != jf) {
                                    jf.dispose();
                                }
                            }
                        });

    }

}
