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
 * Copyright (C) 2010-2026 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.bundle.*;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.*;

/**
 * Some common code for the GUI tests to get a text system up and running on a separate thread
 * @author Martin
 */
public abstract class TestBase {

    MultiWindowTextGUI textGUI;

    void run(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        textGUI = createTextGUI(screen);
        assignTheme(extractTheme(args));
        textGUI.setBlockingIO(false);
        textGUI.setEOFWhenNoWindows(true);
        //noinspection ResultOfMethodCallIgnored
        textGUI.isEOFWhenNoWindows();   //No meaning, just to silence IntelliJ:s "is never used" alert

        try {
            textGUI.addWindow(makeThemeChangerWindow());
            init(textGUI);
            arrangeWindows();
            AsynchronousTextGUIThread guiThread = (AsynchronousTextGUIThread)textGUI.getGUIThread();
            guiThread.start();
            afterGUIThreadStarted(textGUI);
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    private String extractTheme(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("--theme") && i + 1 < args.length) {
                return args[i+1];
            }
        }
        return null;
    }

    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        return new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager());
    }

    public abstract void init(WindowBasedTextGUI textGUI);
    public void afterGUIThreadStarted(WindowBasedTextGUI textGUI) {
        // By default do nothing
    }
    public Window makeThemeChangerWindow() {
        Collection<String> names = LanternaThemes.getRegisteredThemes();
        ActionListBox themes = new ActionListBox();
        for (String name : names) {
            themes.addItem( "theme: " + name, () -> assignTheme(name));
        }
        
        final Window window = new BasicWindow("Themes");
        window.setComponent(themes);
        
        // unsure why, there is still some flicker case if ScrollPanel not quite used preferred size 
        //ScrollPanel scrollPanel = new ScrollPanel(themes);
        //scrollPanel.setPreferredSize(new TerminalSize(40, 20));
        //window.setComponent(scrollPanel);
        return window;
    }
    
    public void assignTheme(String themeName) {
        if (themeName == null) {
            return;
        }
        Theme theme = LanternaThemes.getRegisteredTheme(themeName);
        Collection<Window> windows = textGUI.getWindows();
        if (theme != null && windows != null) {
            for (Window w : windows) {
                w.setTheme(theme);
            }
            textGUI.setTheme(theme);
        }
    }
    
    public void arrangeWindows() {
        final int PAD = 4;
        int x = 1;
        int y = 1;
        for (Window w : textGUI.getWindows()) {
            TerminalSize size = w.getPreferredSize();
            w.setPosition(new TerminalPosition(x, y));
            w.setHints(Collections.singletonList(Window.Hint.FIXED_POSITION));
            x += (size.getColumns() + PAD) / 2;
            y += size.getRows() + PAD;
        }
    }
}
