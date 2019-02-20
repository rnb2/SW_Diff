package com.rnb2.diff.com.rnb2.diff.utils;

import javax.swing.*;
import java.awt.*;

public class SwingUtils {

    private SwingUtils(){}

    public static void defaultCursor(final Component component){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public static void waitCursor(final Component component){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
        });
    }
}
