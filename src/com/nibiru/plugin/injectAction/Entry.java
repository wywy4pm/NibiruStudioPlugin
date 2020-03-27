package com.nibiru.plugin.injectAction;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class Entry extends JPanel {

    protected EntryList mParent;
    protected Element mElement;
    protected OnCheckBoxStateChangedListener mListener;

    protected JCheckBox mCheck;
    protected JLabel mType;
    protected JLabel mID;
    protected JTextField mName;
    protected Color mNameDefaultColor;

    public JCheckBox getCheck() {
        return mCheck;
    }

    public void setListener(final OnCheckBoxStateChangedListener onStateChangedListener) {
        this.mListener = onStateChangedListener;
    }

    public Entry(EntryList parent, Element element) {
        mElement = element;
        mParent = parent;

        mCheck = new JCheckBox();
        mCheck.setPreferredSize(new Dimension(40, 26));
        mCheck.addChangeListener(new CheckListener());

        mType = new JLabel(mElement.type);
        mType.setPreferredSize(new Dimension(100, 26));

        mID = new JLabel(mElement.id);
        mID.setPreferredSize(new Dimension(100, 26));

        mName = new JTextField("m"+mElement.id, 10);
        mElement.vartriablename="m"+mElement.id;
        mNameDefaultColor = mName.getBackground();
        mName.setPreferredSize(new Dimension(100, 26));
        mName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // empty
            }

            @Override
            public void focusLost(FocusEvent e) {
                syncElement();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 54));
        add(mCheck);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mType);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mID);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mName);
        add(Box.createHorizontalGlue());

        checkState();
    }

    public Element syncElement() {
        mElement.used = mCheck.isSelected();
        mElement.vartriablename = mName.getText();
        return mElement;
    }

    private void checkState() {
        if (mCheck.isSelected()) {
            mType.setEnabled(true);
            mID.setEnabled(true);
            mName.setEnabled(true);
        } else {
            mType.setEnabled(false);
            mID.setEnabled(false);
            mName.setEnabled(false);
        }

        if (mListener != null) {
            mListener.changeState(mCheck.isSelected());
        }
    }

    public class CheckListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent event) {
            checkState();
        }
    }

}
