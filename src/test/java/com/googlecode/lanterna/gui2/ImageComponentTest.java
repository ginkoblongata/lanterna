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
import com.googlecode.lanterna.bundle.*;
import com.googlecode.lanterna.graphics.*;

public class ImageComponentTest extends TestBase {
    public static void main(String[] args) throws Exception {
        new ImageComponentTest().run(args);
    }
    
    static String[] IMAGE = new String[] {
        "-====================================================-",
        "xx                                                  xx",
        "xx  X                                            X  xx",
        "xx                                                  xx",
        "xx    .d8b.  d8888b.  .o88b.                        xx",
        "xx   d8' `8b 88  `8D d8P  Y8                        xx",
        "xx   88ooo88 88oooY' 8P            asdfasdf         xx",
        "xx   88~~~88 88~~~b. 8b                             xx",
        "xx   88   88 88   8D Y8b  d8              1234      xx",
        "xx   YP   YP Y8888P'  `Y88P'                        xx",
        "xx                                 asdfasdf         xx",
        "xx                                                  xx",
        "xx   db    db db    db d88888D                      xx",
        "xx   `8b  d8' `8b  d8' YP  d8'                      xx",
        "xx    `8bd8'   `8bd8'     d8'          xxxxxxx      xx",
        "xx    .dPYb.     88      d8'           x     x      xx",
        "xx   .8P  Y8.    88     d8' db         x     x      xx",
        "xx   YP    YP    YP    d88888P         x     x      xx",
        "xx                                     xxxxxxx      xx",
        "xx  X                                            X  xx",
        "xx                                                  xx",
        "-====================================================-"
    };

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("ImageComponentTest");
        window.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));

        ImageComponent unscrollable = makeImageComponent();
        ScrollPanel both = new ScrollPanel(makeImageComponent(), true, true);
        ScrollPanel vertical = new ScrollPanel(makeImageComponent(), false, true);
        ScrollPanel horizontal = new ScrollPanel(makeImageComponent(), true, false);
        
        vertical.setPreferredSize(new TerminalSize( 24, 12));
        horizontal.setPreferredSize(new TerminalSize( 24, 12));
        both.setPreferredSize(new TerminalSize( 24, 12));
        
        
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new GridLayout(2));
        mainPanel.addComponent(unscrollable.withBorder(Borders.singleLine("full size")));
        mainPanel.addComponent(both.withBorder(Borders.singleLine("v & h scroll")));
        mainPanel.addComponent(vertical.withBorder(Borders.singleLine("v scroll")));
        mainPanel.addComponent(horizontal.withBorder(Borders.singleLine("h scroll")));
        
        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }
    
    
    ImageComponent makeImageComponent() {
        ImageComponent imageComponent = new ImageComponent();
        TerminalSize imageSize = new TerminalSize(IMAGE[0].length(), IMAGE.length);
        TextImage textImage = new BasicTextImage(imageSize);
        
        for (int row = 0; row < IMAGE.length; row++) {
            fillImageLine(textImage, row, IMAGE[row]);
        }
        
        imageComponent.setTextImage(textImage);
        return imageComponent;
    }
    
    void fillImageLine(TextImage textImage, int row, String line) {
        for (int x = 0; x < line.length(); x++) {
            char c = line.charAt(x);
            TextCharacter textCharacter = new TextCharacter(c);
            textImage.setCharacterAt(x, row, textCharacter);
        }
    }
}
