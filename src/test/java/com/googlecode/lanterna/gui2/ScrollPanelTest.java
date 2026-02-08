/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2026 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.bundle.LanternaThemes;

import java.io.IOException;
import java.util.function.*;
import java.util.*;

/**
 * <p>
 * Serves to manually test ScrollPanel during development of mouse support.
 * Uses Telnet port 23000 as you need something different than swing terminal
 * provided by IDE. After launching main method you can connect to it via terminal "telnet localhost 23000" (or something of that nature)
 * 
 * Or, this can be simply launched at the command line in a suitable terminal.
 * 
 * <p>
 */
public class ScrollPanelTest {

    public static void main(String[] args) throws Exception {
        new ScrollPanelTest().go();
    }
    
    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    
    Window window;
    
    void assignTheme(String themeName) {
        window.setTheme(LanternaThemes.getRegisteredTheme(themeName));
    }
    
    private void logAppendMax(int lineCount, String message) {
        TextBox log = logTextBox;
        try {
            while (log.getLineCount() >= lineCount) {
                log.removeLine(0);
            }
        } finally {
            log.addLine(message);
            // unfortunately some methods expect (row, column), some (column, row)
            log.setCaretPosition(new TerminalPosition(Integer.MAX_VALUE, log.getLineCount()));
        }
    }

    private TextBox logTextBox;
    
    void go() throws Exception {
        try (Screen screen = new DefaultTerminalFactory()
                .setTelnetPort(23000)
                .setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE)
                .setInitialTerminalSize(new TerminalSize(100, 140))
                .createScreen()) {
            screen.startScreen();
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            window = new BasicWindow("ScrollPanelTest");
            window.addWindowListener(new WindowListenerAdapter() {
                @Override
                public void onInput(Window basePane, com.googlecode.lanterna.input.KeyStroke keyStroke, java.util.concurrent.atomic.AtomicBoolean deliverEvent) {
                    log("input: " + keyStroke);
                }
            });
            window.setTheme(LanternaThemes.getRegisteredTheme("businessmachine"));
            window.setComponent(makeUi());
            gui.addWindowAndWait(window);
        }
    }

    Component makeUi() {
        
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // instantiate ui components (no layout activities)
        logTextBox = new TextBox(new TerminalSize(80, 12));
        logTextBox.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.FILL));
        Button clearLogButton = new Button("CLEAR LOG", () -> logTextBox.setText(""));
        
        ActionListBox themes = new ActionListBox(new TerminalSize(40, 62));
        themes.addItem("theme: default        ", () -> assignTheme("default"));
        themes.addItem("theme: defrost        ", () -> assignTheme("defrost"));
        themes.addItem("theme: bigsnake       ", () -> assignTheme("bigsnake"));
        themes.addItem("theme: conqueror      ", () -> assignTheme("conqueror"));
        themes.addItem("theme: businessmachine", () -> assignTheme("businessmachine"));
        themes.addItem("theme: blaster        ", () -> assignTheme("blaster"));
		
        ActionListBox actionListBox = new ActionListBox();
        RadioBoxList<String> radioListBox = new RadioBoxList<>();
        CheckBoxList<String> checkListBox = new CheckBoxList<>();
        
        fillActionListBoxWithTestItems("actionListBox", 60, actionListBox);
        fillAbstractListBoxWithTestItems("radioListBox", 60, radioListBox);
        fillAbstractListBoxWithTestItems("checkListBox", 60, checkListBox);
        
        ActionListBox numberChooser = new ActionListBox();
        eachOf(20, i -> numberChooser.addItem("assign: " + (5*i) + " items", () -> {
            fillActionListBoxWithTestItems("actionListBox", 5*i, actionListBox);
            fillAbstractListBoxWithTestItems( "radioListBox", 5*i, radioListBox);
            fillAbstractListBoxWithTestItems( "checkListBox", 5*i, checkListBox);
        }));
        
        ActionListBox listBox3 = new ActionListBox();
        eachOf(245, i -> listBox3.addItem("item: " + i, () -> log("listBox3 item: " + i)));
        
        RadioBoxList radioBoxList2 = new RadioBoxList();
        eachOf(245, i -> radioBoxList2.addItem("radio item: " + i));
        
        CheckBoxList<String> checkboxList2 = new CheckBoxList<>();
        eachOf(245, i -> checkboxList2.addItem("heckboxList2: " + i));
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        
        
        
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // arrange components
        Panel ui = new Panel(new LinearLayout(Direction.VERTICAL));
        ui.setPreferredSize(new TerminalSize(160, 40));
        ui.addComponent(logTextBox.withBorder(Borders.singleLine("log")));
        
        Panel hpanel = new Panel(new GridLayout(100));
        hpanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.FILL));
        hpanel.addComponent(themes.withBorder(Borders.singleLine("themes")));
        
        Component numberChooserBordered = numberChooser.withBorder(Borders.singleLine("choose number"));
        numberChooserBordered.setPreferredSize(TerminalSize.of(30, 20));
        hpanel.addComponent(numberChooserBordered);
        
        
        ScrollPanel sp_0 = new ScrollPanel(actionListBox);
        ScrollPanel sp_1 = new ScrollPanel(radioListBox);
        ScrollPanel sp_2 = new ScrollPanel(checkListBox);
        
        
        Component comp_sp_0 = sp_0.withBorder(Borders.singleLine("scrollPanel actionListBox"));
        Component comp_sp_1 = sp_1.withBorder(Borders.singleLine("scrollPanel radioListBox"));
        Component comp_sp_2 = sp_2.withBorder(Borders.singleLine("scrollPanel checkListBox"));
        
        
        //hpanel.addComponent(new ScrollPanel(actionListBox).withBorder(Borders.singleLine("scrollPanel actionListBox")));
        //hpanel.addComponent(new ScrollPanel(radioListBox).withBorder(Borders.singleLine("scrollPanel radioListBox")));
        //hpanel.addComponent(new ScrollPanel(checkListBox).withBorder(Borders.singleLine("scrollPanel checkListBox")));
        
        // comp_sp_0.setPreferredSize(TerminalSize.of(30, 20));
        // comp_sp_1.setPreferredSize(TerminalSize.of(30, 20));
        // comp_sp_2.setPreferredSize(TerminalSize.of(30, 20));
        
        hpanel.addComponent(comp_sp_0);
        hpanel.addComponent(comp_sp_1);
        hpanel.addComponent(comp_sp_2);
        
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // bug: need ".setPrefferedSize(..)" when ActionListBox is in ScrollPanel or ".withBorder(..)" single line label won't show up
        sp_0.setPreferredSize(TerminalSize.of(30, 20));
        // the behavior of the layout will be different if ScrollPanel has preferredSize set vs not set
        // but also maybe there is issue with if the border single line is too long
        //sp_1.setPreferredSize(TerminalSize.of(30, 20));
        //sp_2.setPreferredSize(TerminalSize.of(30, 20));
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        
        
        Panel hpanel2 = new Panel(new GridLayout(100));
        hpanel2.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.FILL));
        hpanel2.addComponent(listBox3.withBorder(Borders.singleLine("listBox3")));
        hpanel2.addComponent(radioBoxList2.withBorder(Borders.singleLine("radio list 2")));
        hpanel2.addComponent(checkboxList2.withBorder(Borders.singleLine("checkListBox 2")));
        TextBox textBox = new TextBox("", TextBox.Style.MULTI_LINE);
        ScrollPanel textBoxScrollPanel = new ScrollPanel(textBox);
        textBoxScrollPanel.setPreferredSize(new TerminalSize(32, 16));
        eachOf(30, i -> textBox.addLine("-> " + i + ", aklkjh 0 "+i+" 876  "+i+" 76 s   "+i+" ==ssss55 "+i+" 55 555   "+i+" 5 5 55 "+i+"  55555 s "+i+" sssfa --> " + i ));
        hpanel2.addComponent(textBoxScrollPanel.withBorder(Borders.singleLine("scroll TextBox")));
        
        
        TextBox textBox2 = new TextBox("", TextBox.Style.MULTI_LINE);
        textBox2.setPreferredSize(new TerminalSize(32, 16));
        eachOf(30, i -> textBox2.addLine("abc: "+i+", aklkjh 0 "+i+" 876  "+i+" 76 s   "+i+" ==ssss55 "+i+" 55 555   "+i+" 5 5 55 "+i+"  55555 s "+i+" sssfa --> " + i ));
        hpanel2.addComponent(textBox2.withBorder(Borders.singleLine("TextBox old style")));
        
        //ui.addComponent(Panels.vertical(hpanel, hpanel2));
        ui.addComponent(hpanel);
        ui.addComponent(clearLogButton);
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        
        return ui;
    }
    
    void fillActionListBoxWithTestItems(String label, int count, ActionListBox listbox) {
        log(label + ", fillActionListBoxWithTestItems(" + count + ", " + listbox + ")");
        listbox.clearItems();
        eachOf(count, i -> listbox.addItem(label + " item: " + i, () -> log(label + " item: " + i)));
    }
    void fillAbstractListBoxWithTestItems(String label, int count, AbstractListBox listbox) {
        log(label + ", fillAbstractListBoxWithTestItems(" + count + ", " + listbox + ")");
        listbox.clearItems();
        eachOf(count, i -> listbox.addItem(label + " item: " + i));
    }

    void log(String message) {
        logAppendMax(10, message);
    }
    
    void eachOf(int count, Consumer<Integer> op) {
        for (int i = 0; i < count; i++) op.accept(i);
    }
    
    <T> void eachOf(Collection<T> items, Consumer<T> op) {
        for (T item : items) op.accept(item);
    }
}
