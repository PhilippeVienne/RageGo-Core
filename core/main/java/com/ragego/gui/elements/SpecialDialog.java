package com.ragego.gui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.ragego.gui.RageGoGame;

public class SpecialDialog extends Dialog {
    private static final int MESSAGE = 0;
    private static final int CONFIRM = 1;
    private static final int INPUT = 2;

    private static Skin uiSkin;

    public SpecialDialog(String title, int dialogType, CharSequence... args) {
        super(title, RageGoGame.getUiSkin());
        uiSkin = RageGoGame.getUiSkin();

        if ((dialogType > 2) || (dialogType < 0)) {
            throw new IllegalArgumentException("dialogType must be 0, 1 or 2");
        }

        Label[] labels = new Label[args.length];
        System.out.println(args.length);
        System.out.println(args[0]);
        for (int i = 0; i < args.length; i++) {
            labels[i] = new Label("No", uiSkin);
            System.out.println(i);
            this.getContentTable().row().colspan(i + 1).center();
            this.getContentTable().add(labels[i]);
        }

        switch (dialogType) {
            case 0:
                TextButton tb1 = new TextButton("Ok", uiSkin);
                break;
            case 1:

                break;
            case 2:
                break;
        }
        /*
        Label message = new Label("Dialog: Exit?", uiSkin);
        TextButton tb1 = new TextButton("Yes", uiSkin);
        tb1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RageGoGame.goHome();
            }
        });
        TextButton tb2 = new TextButton("No", uiSkin);
        this.getContentTable().row().colspan(1).center();
        this.getContentTable().add(message);
        this.row().colspan(2);
        this.button(tb1);
        this.button(tb2);
        */
        this.setKeepWithinStage(true);
        this.pack();
    }
}
