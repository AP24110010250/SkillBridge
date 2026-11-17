package com.skillbridge.gui.components;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * A JPanel that implements the Swing Scrollable interface.
 * Forces getScrollableTracksViewportWidth() = true so child components always wrap
 * and fit within the viewport width, eliminating unwanted horizontal scrolling.
 */
public class ScrollablePanel extends JPanel implements Scrollable {

    public ScrollablePanel() {
        super();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 64;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Enforce content width to always match viewport width — NO HORIZONTAL SCROLLBAR
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // Allow vertical scrolling
        return false;
    }
}
