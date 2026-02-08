/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.input.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ginkoblongata
 */
public class SplitPanel extends Panel {

    private final Component compA;
    private final ImageComponent thumb;
    private final Component compB;

    private boolean isHorizontal;
    private double ratio = 0.5;
    private boolean hasChanged = true;

    public static SplitPanel ofHorizontal(Component left, Component right) {
        SplitPanel split = new SplitPanel(left, right, true);
        return split;
    }

    public static SplitPanel ofVertical(Component top, Component bottom) {
        SplitPanel split = new SplitPanel(top, bottom, false);
        return split;
    }

    /**
     *
     */
    protected SplitPanel(Component a, Component b, boolean isHorizontal) {
        this.compA = a;
        this.compB = b;
        this.isHorizontal = isHorizontal;

        setRatio(10, 10);
        thumb = makeThumb();
        setLayoutManager(new SplitPanelLayoutManager());

        addComponent(a);
        addComponent(thumb);
        addComponent(b);
    }

    /*
     * Use whatever sizing.
     */
    public void setRatio(int a, int b) {
        double r = ratio;
        if (a == 0 || b == 0) {
            ratio = 0.5;
        } else {
            int total = Math.abs(a) + Math.abs(b);
            ratio = (double) a / (double) total;
        }
        hasChanged |= ratio != r;
    }

    TextCharacter thumbRenderer() {
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // TODO: themed
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        ThemeStyle themeStyle = getTheme().getDefaultDefinition().getNormal();
        TextCharacter thumbRenderer = TextCharacter.fromCharacter(
            (char)(isHorizontal ? Symbols.SINGLE_LINE_VERTICAL : Symbols.SINGLE_LINE_HORIZONTAL),
            themeStyle.getForeground(),
            themeStyle.getBackground());
        if (thumb.isFocused()) {
            thumbRenderer = thumbRenderer.withModifier(SGR.BOLD);
        }
        return thumbRenderer;
    }
    ImageComponent makeThumb() {
        ImageComponent imageComponent = new ImageComponent() {
            TerminalSize aSize = TerminalSize.OF_0x0;
            TerminalSize bSize = TerminalSize.OF_0x0;
            TerminalSize tSize = TerminalSize.OF_0x0;
            TerminalPosition down = null;
            TerminalPosition drag = null;

            @Override
            public Result handleKeyStroke(KeyStroke keyStroke) {
                Result result;
                if (keyStroke instanceof MouseAction) {
                    result = handleMouseAction((MouseAction) keyStroke);
                }
                // TODO: Implement keyboard based resizing
                else {
                    result = super.handleKeyStroke(keyStroke);
                }
                return result;
            }

            private Result handleMouseAction(MouseAction mouseAction) {
                if (mouseAction.isMouseDown()) {
                    aSize = aSize.as(compA.getSize());
                    bSize = bSize.as(compB.getSize());
                    tSize = tSize.as(thumb.getSize());
                    down = mouseAction.getPosition();
                }
                if (mouseAction.isMouseDrag()) {
                    drag = mouseAction.getPosition();

                    // xxxxxxxxxxxxxxxxxxxxx
                    // this is a hack, should not be needed if the pane drag
                    // only on mouse down'd comp stuff was completely working
                    if (down == null) {
                        down = drag;
                    }
                    // xxxxxxxxxxxxxxxxxxxxx

                    int delta = isHorizontal ? drag.minus(down).getColumn() : drag.minus(down).getRow();
                    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                    if (isHorizontal) {
                        int a = Math.max(1, tSize.getColumns() + aSize.getColumns() + delta);
                        int b = Math.max(1, bSize.getColumns() - delta);
                        setRatio(a, b);
                    } else {
                        int a = Math.max(1, tSize.getRows() + aSize.getRows() + delta);
                        int b = Math.max(1, bSize.getRows() - delta);
                        setRatio(a, b);
                    }
                    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                }
                if (mouseAction.isMouseUp()) {
                    down = null;
                    drag = null;
                }
                return Result.HANDLED;
            }
        };
        return imageComponent;
    }

    class SplitPanelLayoutManager implements LayoutManager {
        TerminalSize aSize = TerminalSize.OF_0x0;
        TerminalSize bSize = TerminalSize.OF_0x0;
        TerminalSize tSize = TerminalSize.OF_0x0;
        @Override
        public boolean hasChanged() {
            return hasChanged;
        }
        @Override
        public TerminalSize getPreferredSize(List<Component> components) {
            aSize = compA.getPreferredSize();
            int aWidth = aSize.getColumns();
            int aHeight = aSize.getRows();
            bSize = compB.getPreferredSize();
            int bWidth = bSize.getColumns();
            int bHeight = bSize.getRows();

            int tWidth = thumb.getPreferredSize().getColumns();
            int tHeight = thumb.getPreferredSize().getRows();

            if (isHorizontal) {
                return TerminalSize.of(aWidth + tWidth + bWidth, Math.max(aHeight, Math.max(tHeight, bHeight)));
            } else {
                return TerminalSize.of(Math.max(aWidth, Math.max(tWidth, bWidth)), aHeight + tHeight + bHeight);
            }
        }
        TerminalSize thumbSize() {
            return tSize = isHorizontal ? tSize.as(1, getSize().height()) : tSize.as(getSize().width(), 1);
        }
        @Override
        public void doLayout(TerminalSize area, List<Component> components) {
            List<TerminalRectangle> rectangles = components.stream().map(c -> c.getBounds()).collect(Collectors.toList());

            thumb.setTextImage(thumb.getTextImage().resize(thumbSize(), thumbRenderer()));

            int tWidth = thumb.getPreferredSize().getColumns();
            int tHeight = thumb.getPreferredSize().getRows();

            TerminalSize size = getSize();
            int w = size.getColumns();
            int h = size.getRows();

            if (isHorizontal) {
                w -= tWidth;
            } else {
                h -= tHeight;
            }

            if (isHorizontal) {
                int leftWidth = Math.max(0, (int) (w * ratio));
                int leftHeight = Math.max(0, Math.min(compA.getPreferredSize().getRows(), h));

                int rightWidth = Math.max(0, w - leftWidth);
                int rightHeight = Math.max(0, Math.min(compB.getPreferredSize().getRows(), h));

                compA.setSize(TerminalSize.of(leftWidth, leftHeight));
                thumb.setSize(thumb.getPreferredSize());
                compB.setSize(TerminalSize.of(rightWidth, rightHeight));

                compA.setPosition(TerminalPosition.of(0, 0));
                thumb.setPosition(TerminalPosition.of(leftWidth, h / 2 - tHeight / 2));
                compB.setPosition(TerminalPosition.of(leftWidth + tWidth, 0));
            } else {
                int leftWidth = Math.max(0, Math.min(compA.getPreferredSize().getColumns(), w));
                int leftHeight = Math.max(0, (int) (h * ratio));

                int rightWidth = Math.max(0, Math.min(compB.getPreferredSize().getColumns(), w));
                int rightHeight = Math.max(0, h - leftHeight);

                compA.setSize(TerminalSize.of(leftWidth, leftHeight));
                thumb.setSize(thumb.getPreferredSize());
                compB.setSize(TerminalSize.of(rightWidth, rightHeight));

                compA.setPosition(TerminalPosition.of(0, 0));
                thumb.setPosition(TerminalPosition.of(w / 2 - tWidth / 2, leftHeight));
                compB.setPosition(TerminalPosition.of(0, leftHeight + tHeight));
            }
            hasChanged |= !rectangles.equals(components.stream().map(c -> c.getBounds()).collect(Collectors.toList()));
        }
    }

    public void setThumbVisible(boolean visible) {
        thumb.setVisible(visible);

        if (visible) {
            this.setPreferredSize(null);
        } else {
            thumb.setPreferredSize(TerminalSize.of(1, 1));
        }
    }

}

