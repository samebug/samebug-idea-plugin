/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.base.scrollPane;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static java.awt.Adjustable.VERTICAL;

// NOTE most of this code is lifted from com.intellij.ui.components.DefaultScrollBarUI
public final class SamebugScrollBarUI extends ScrollBarUI {
    private final SamebugScrollBarUI.Listener myListener = new SamebugScrollBarUI.Listener();
    private final Timer myScrollTimer = new Timer(60, myListener);

    private Color[] ThumbColor = ColorService.ScrollbarThumb;
    private Color[] TrackColor = ColorService.ScrollbarTrack;

    private final Rectangle myThumbBounds = new Rectangle();
    private final Rectangle myTrackBounds = new Rectangle();
    private final int myThickness;

    private JScrollBar myScrollBar;

    private boolean isValueCached;
    private int myCachedValue;
    private int myOldValue;

    SamebugScrollBarUI() {
        this(20);
    }

    private SamebugScrollBarUI(int thickness) {
        myThickness = thickness;
    }

    private int getThickness() {
        return myThickness;
    }

    private boolean isAbsolutePositioning(MouseEvent event) {
        return SwingUtilities.isMiddleMouseButton(event);
    }

    private boolean isTrackClickable() {
        return true;
    }

    private boolean isTrackContains(int x, int y) {
        return myTrackBounds.contains(x, y);
    }

    private boolean isThumbContains(int x, int y) {
        return myThumbBounds.contains(x, y);
    }

    private void onTrackHover(boolean hover) {
    }

    private void onThumbHover(boolean hover) {
    }

    private void paintTrack(Graphics2D g, int x, int y, int width, int height, JComponent c) {
        g.setColor(ColorService.forCurrentTheme(TrackColor));
        int arc = Math.min(width, height);
        g.fillRoundRect(x, y, width, height, arc, arc);
    }

    private void paintThumb(Graphics2D g, int x, int y, int width, int height, JComponent c) {
        g.setColor(ColorService.forCurrentTheme(ThumbColor));
        int arc = Math.min(width, height);
        g.fillRoundRect(x, y, width, height, arc, arc);
    }

    private void onThumbMove() {
    }

    private void repaint() {
        if (myScrollBar != null) myScrollBar.repaint();
    }

    private void repaint(int x, int y, int width, int height) {
        if (myScrollBar != null) myScrollBar.repaint(x, y, width, height);
    }

    @Override
    public void installUI(JComponent c) {
        myScrollBar = (JScrollBar) c;
        myScrollBar.setFocusable(false);
        myScrollBar.addMouseListener(myListener);
        myScrollBar.addMouseMotionListener(myListener);
        myScrollBar.getModel().addChangeListener(myListener);
        myScrollBar.addPropertyChangeListener(myListener);
        myScrollBar.addFocusListener(myListener);
        myScrollTimer.setInitialDelay(300);
    }

    @Override
    public void uninstallUI(JComponent c) {
        myScrollTimer.stop();
        myScrollBar.removeFocusListener(myListener);
        myScrollBar.removePropertyChangeListener(myListener);
        myScrollBar.getModel().removeChangeListener(myListener);
        myScrollBar.removeMouseMotionListener(myListener);
        myScrollBar.removeMouseListener(myListener);
        myScrollBar.setForeground(null);
        myScrollBar.setBackground(null);
        myScrollBar = null;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        int thickness = getThickness();
        // TODO Lift JBScrollPane.Alignment.get implementation? Now we just assume scrollbar is on the right.
        return new Dimension(thickness, thickness * 2);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = DrawService.init(g);
            // add padding to the track and the thumb, depending on the scrollbar orientation
            Rectangle bounds;
            final int padding = 6;
            if (VERTICAL == myScrollBar.getOrientation()) {
                bounds = new Rectangle(padding, 0, c.getWidth() - 2 * padding, c.getHeight());
            } else {
                bounds = new Rectangle(0, padding, c.getWidth(), c.getHeight() - 2 * padding);
            }

            // showing the track only when the area is actually scrollable (not the whole content is showed)
            if (myThumbBounds.width > 0 && myThumbBounds.height > 0) {
                paintTrack(g2, bounds.x, bounds.y, bounds.width, bounds.height, c);
            }

            myTrackBounds.setBounds(bounds);
            updateThumbBounds();

            // show the thumb only when the area is actually scrollable (not the whole content is showed)
            if (myThumbBounds.width > 0 && myThumbBounds.height > 0) {
                paintThumb(g2, myThumbBounds.x, myThumbBounds.y, myThumbBounds.width, myThumbBounds.height, c);
            }
        }
    }

    // NOTE: things below are not modified seriously from the original code
    // --------------------------------------------------------------------

    private void updateThumbBounds() {
        int value = 0;
        int min = myScrollBar.getMinimum();
        int max = myScrollBar.getMaximum();
        int range = max - min;
        if (range <= 0) {
            myThumbBounds.setBounds(0, 0, 0, 0);
        } else if (VERTICAL == myScrollBar.getOrientation()) {
            int extent = myScrollBar.getVisibleAmount();
            int height = Math.max(convert(myTrackBounds.height, extent, range), 2 * getThickness());
            if (myTrackBounds.height <= height) {
                myThumbBounds.setBounds(0, 0, 0, 0);
            } else {
                value = getValue();
                int maxY = myTrackBounds.y + myTrackBounds.height - height;
                int y = (value < max - extent) ? convert(myTrackBounds.height - height, value - min, range - extent) : maxY;
                myThumbBounds.setBounds(myTrackBounds.x, adjust(y, myTrackBounds.y, maxY), myTrackBounds.width, height);
                if (myOldValue != value) onThumbMove();
            }
        } else {
            int extent = myScrollBar.getVisibleAmount();
            int width = Math.max(convert(myTrackBounds.width, extent, range), 2 * getThickness());
            if (myTrackBounds.width <= width) {
                myThumbBounds.setBounds(0, 0, 0, 0);
            } else {
                value = getValue();
                int maxX = myTrackBounds.x + myTrackBounds.width - width;
                int x = (value < max - extent) ? convert(myTrackBounds.width - width, value - min, range - extent) : maxX;
                if (!myScrollBar.getComponentOrientation().isLeftToRight()) x = myTrackBounds.x - x + maxX;
                myThumbBounds.setBounds(adjust(x, myTrackBounds.x, maxX), myTrackBounds.y, width, myTrackBounds.height);
                if (myOldValue != value) onThumbMove();
            }
        }
        myOldValue = value;
    }

    private int getValue() {
        return isValueCached ? myCachedValue : myScrollBar.getValue();
    }

    /**
     * Converts a value from old range to new one.
     * It is necessary to use floating point calculation to avoid integer overflow.
     */
    private static int convert(double newRange, double oldValue, double oldRange) {
        return (int) (.5 + newRange * oldValue / oldRange);
    }

    private static int adjust(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    private final class Listener extends MouseAdapter implements ActionListener, FocusListener, ChangeListener, PropertyChangeListener {
        private int myOffset;
        private int myMouseX, myMouseY;
        private boolean isReversed;
        private boolean isDragging;
        private boolean isOverTrack;
        private boolean isOverThumb;

        private void updateMouse(int x, int y) {
            if (isTrackContains(x, y)) {
                if (!isOverTrack) onTrackHover(isOverTrack = true);
                boolean hover = isThumbContains(x, y);
                if (isOverThumb != hover) onThumbHover(isOverThumb = hover);
            } else {
                updateMouseExit();
            }
        }

        private void updateMouseExit() {
            if (isOverThumb) onThumbHover(isOverThumb = false);
            if (isOverTrack) onTrackHover(isOverTrack = false);
        }

        private boolean redispatchIfTrackNotClickable(MouseEvent event) {
            if (isTrackClickable()) return false;
            // redispatch current event to the view
            Container parent = myScrollBar.getParent();
            if (parent instanceof JScrollPane) {
                JScrollPane pane = (JScrollPane) parent;
                Component view = pane.getViewport().getView();
//                if (view != null) view.dispatchEvent(MouseEventAdapter.convert(event, view));
            }
            return true;
        }

        @Override
        public void mousePressed(MouseEvent event) {
            if (myScrollBar == null || !myScrollBar.isEnabled()) return;
            if (redispatchIfTrackNotClickable(event)) return;
            if (SwingUtilities.isRightMouseButton(event)) return;

            isValueCached = true;
            myCachedValue = myScrollBar.getValue();
            myScrollBar.setValueIsAdjusting(true);

            myMouseX = event.getX();
            myMouseY = event.getY();

            boolean vertical = VERTICAL == myScrollBar.getOrientation();
            if (isThumbContains(myMouseX, myMouseY)) {
                // pressed on the thumb
                myOffset = vertical ? (myMouseY - myThumbBounds.y) : (myMouseX - myThumbBounds.x);
                isDragging = true;
            } else if (isTrackContains(myMouseX, myMouseY)) {
                // pressed on the track
                if (isAbsolutePositioning(event)) {
                    myOffset = (vertical ? myThumbBounds.height : myThumbBounds.width) / 2;
                    isDragging = true;
                    setValueFrom(event);
                } else {
                    myScrollTimer.stop();
                    isDragging = false;
                    if (VERTICAL == myScrollBar.getOrientation()) {
                        int y = myThumbBounds.isEmpty() ? myScrollBar.getHeight() / 2 : myThumbBounds.y;
                        isReversed = myMouseY < y;
                    } else {
                        int x = myThumbBounds.isEmpty() ? myScrollBar.getWidth() / 2 : myThumbBounds.x;
                        isReversed = myMouseX < x;
                        if (!myScrollBar.getComponentOrientation().isLeftToRight()) {
                            isReversed = !isReversed;
                        }
                    }
                    scroll(isReversed);
                    startScrollTimerIfNecessary();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            if (isDragging) updateMouse(event.getX(), event.getY());
            if (myScrollBar == null || !myScrollBar.isEnabled()) return;
            if (redispatchIfTrackNotClickable(event)) return;
            if (SwingUtilities.isRightMouseButton(event)) return;
            isDragging = false;
            myOffset = 0;
            myScrollTimer.stop();
            isValueCached = true;
            myCachedValue = myScrollBar.getValue();
            myScrollBar.setValueIsAdjusting(false);
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            if (myScrollBar == null || !myScrollBar.isEnabled()) return;
            if (myThumbBounds.isEmpty() || SwingUtilities.isRightMouseButton(event)) return;
            if (isDragging) {
                setValueFrom(event);
            } else {
                myMouseX = event.getX();
                myMouseY = event.getY();
                updateMouse(myMouseX, myMouseY);
                startScrollTimerIfNecessary();
            }
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            if (myScrollBar == null || !myScrollBar.isEnabled()) return;
            if (!isDragging) updateMouse(event.getX(), event.getY());
            redispatchIfTrackNotClickable(event);
        }

        @Override
        public void mouseExited(MouseEvent event) {
            if (myScrollBar == null || !myScrollBar.isEnabled()) return;
            if (!isDragging) updateMouseExit();
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (myScrollBar == null) {
                myScrollTimer.stop();
            } else {
                scroll(isReversed);
                if (!myThumbBounds.isEmpty()) {
                    if (isReversed ? !isMouseBeforeThumb() : !isMouseAfterThumb()) {
                        myScrollTimer.stop();
                    }
                }
                int value = myScrollBar.getValue();
                if (isReversed ? value <= myScrollBar.getMinimum() : value >= myScrollBar.getMaximum() - myScrollBar.getVisibleAmount()) {
                    myScrollTimer.stop();
                }
            }
        }

        @Override
        public void focusGained(FocusEvent event) {
            repaint();
        }

        @Override
        public void focusLost(FocusEvent event) {
            repaint();
        }

        @Override
        public void stateChanged(ChangeEvent event) {
            updateThumbBounds();
            // TODO: update mouse
            isValueCached = false;
            repaint();
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String name = event.getPropertyName();
            if ("model" == name) {
                BoundedRangeModel oldModel = (BoundedRangeModel) event.getOldValue();
                BoundedRangeModel newModel = (BoundedRangeModel) event.getNewValue();
                oldModel.removeChangeListener(this);
                newModel.addChangeListener(this);
            }
            if ("model" == name || "orientation" == name || "componentOrientation" == name) {
                repaint();
            }
            if ("opaque" == name || "visible" == name) {
                myTrackBounds.setBounds(0, 0, 0, 0);
                myThumbBounds.setBounds(0, 0, 0, 0);
            }
        }

        private void setValueFrom(MouseEvent event) {
            int x = event.getX();
            int y = event.getY();

            int thumbMin, thumbMax, thumbPos;
            if (VERTICAL == myScrollBar.getOrientation()) {
                thumbMin = myTrackBounds.y;
                thumbMax = myTrackBounds.y + myTrackBounds.height - myThumbBounds.height;
                thumbPos = Math.min(thumbMax, Math.max(thumbMin, (y - myOffset)));
                if (myThumbBounds.y != thumbPos) {
                    int minY = Math.min(myThumbBounds.y, thumbPos);
                    int maxY = Math.max(myThumbBounds.y, thumbPos) + myThumbBounds.height;
                    myThumbBounds.y = thumbPos;
                    onThumbMove();
                    repaint(myThumbBounds.x, minY, myThumbBounds.width, maxY - minY);
                }
            } else {
                thumbMin = myTrackBounds.x;
                thumbMax = myTrackBounds.x + myTrackBounds.width - myThumbBounds.width;
                thumbPos = Math.min(thumbMax, Math.max(thumbMin, (x - myOffset)));
                if (myThumbBounds.x != thumbPos) {
                    int minX = Math.min(myThumbBounds.x, thumbPos);
                    int maxX = Math.max(myThumbBounds.x, thumbPos) + myThumbBounds.width;
                    myThumbBounds.x = thumbPos;
                    onThumbMove();
                    repaint(minX, myThumbBounds.y, maxX - minX, myThumbBounds.height);
                }
            }
            int valueMin = myScrollBar.getMinimum();
            int valueMax = myScrollBar.getMaximum() - myScrollBar.getVisibleAmount();
            // If the thumb has reached the end of the scrollbar, then just set the value to its maximum.
            // Otherwise compute the value as accurately as possible.
            boolean isDefaultOrientation = VERTICAL == myScrollBar.getOrientation() || myScrollBar.getComponentOrientation().isLeftToRight();
            if (thumbPos == thumbMax) {
                myScrollBar.setValue(isDefaultOrientation ? valueMax : valueMin);
            } else {
                int valueRange = valueMax - valueMin;
                int thumbRange = thumbMax - thumbMin;
                int thumbValue = isDefaultOrientation
                        ? thumbPos - thumbMin
                        : thumbMax - thumbPos;
                isValueCached = true;
                myCachedValue = valueMin + convert(valueRange, thumbValue, thumbRange);
                myScrollBar.setValue(myCachedValue);
            }
            if (!isDragging) updateMouse(x, y);
        }

        private void startScrollTimerIfNecessary() {
            if (!myScrollTimer.isRunning()) {
                if (isReversed ? isMouseBeforeThumb() : isMouseAfterThumb()) {
                    myScrollTimer.start();
                }
            }
        }

        private boolean isMouseBeforeThumb() {
            return VERTICAL == myScrollBar.getOrientation()
                    ? isMouseOnTop()
                    : myScrollBar.getComponentOrientation().isLeftToRight()
                    ? isMouseOnLeft()
                    : isMouseOnRight();
        }

        private boolean isMouseAfterThumb() {
            return VERTICAL == myScrollBar.getOrientation()
                    ? isMouseOnBottom()
                    : myScrollBar.getComponentOrientation().isLeftToRight()
                    ? isMouseOnRight()
                    : isMouseOnLeft();
        }

        private boolean isMouseOnTop() {
            return myMouseY < myThumbBounds.y;
        }

        private boolean isMouseOnLeft() {
            return myMouseX < myThumbBounds.x;
        }

        private boolean isMouseOnRight() {
            return myMouseX > myThumbBounds.x + myThumbBounds.width;
        }

        private boolean isMouseOnBottom() {
            return myMouseY > myThumbBounds.y + myThumbBounds.height;
        }

        private void scroll(boolean reversed) {
            int delta = myScrollBar.getBlockIncrement(reversed ? -1 : 1);
            if (reversed) delta = -delta;

            int oldValue = myScrollBar.getValue();
            int newValue = oldValue + delta;

            if (delta > 0 && newValue < oldValue) {
                newValue = myScrollBar.getMaximum();
            } else if (delta < 0 && newValue > oldValue) {
                newValue = myScrollBar.getMinimum();
            }
            if (oldValue != newValue) {
                myScrollBar.setValue(newValue);
            }
        }
    }
}
