package com.ragego.gui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ragego.gui.RageGoGame;

public class RageGoDialog extends Dialog {
    public static final int MESSAGE = 0;
    public static final int CONFIRM = 1;
    private static final int INPUT = 2;
    private TextButton okButton = new TextButton("Ok", RageGoGame.getUiSkin());
    private TextButton cancelButton = new TextButton("Cancel", RageGoGame.getUiSkin());

    public RageGoDialog(String title, int dialogType, final Runnable actionOnOk, final Runnable actionOnCancel, CharSequence... args) {
        super(title, RageGoGame.getUiSkin());
        //As Dialog does not implement a getter for the Skin :
        Skin uiSkin = RageGoGame.getUiSkin();

        if ((dialogType > 2) || (dialogType < 0)) {
            throw new IllegalArgumentException("dialogType must be 0, 1 or 2");
        }

        Label[] labels = new Label[args.length];
        System.out.println(args.length);
        System.out.println(args[0]);
        for (int i = 0; i < args.length; i++) {
            labels[i] = new Label(args[i], uiSkin);
            labels[i].setAlignment(Align.center);
            labels[i].setWrap(true);
            labels[i].setFontScale(0.8f);
            this.getContentTable().add(labels[i]).width(500).center().row();
        }

        this.getButtonTable().padTop(50);

        switch (dialogType) {
            case MESSAGE:
                okButton.addListener(new ButtonChangeListener(actionOnOk));
                cancelButton.addListener(new ButtonChangeListener(actionOnCancel));
                this.getButtonTable().add(okButton).width(500);
                break;
            case CONFIRM:
                okButton.addListener(new ButtonChangeListener(actionOnOk));
                cancelButton.addListener(new ButtonChangeListener(actionOnCancel));
                this.getButtonTable().add(okButton).width(400);
                this.getButtonTable().add(cancelButton).width(400);
                break;
            case INPUT:
                break;
        }
        this.setKeepWithinStage(true);
    }

    public RageGoDialog(String title, String message, int dialogType, Runnable actionOnOk) {
        this(title, dialogType, actionOnOk, null, message);
    }

    public TextButton getOkButton() {
        return okButton;
    }

    public TextButton getCancelButton() {
        return cancelButton;
    }

    public RageGoDialog centerOnViewport(Viewport viewport){
        pack();
        setPosition((viewport.getWorldWidth() - getWidth()) * 0.5f,
                (viewport.getWorldHeight() - getHeight()) * 0.5f);
        return this;
    }

    public RageGoDialog displayOn(Stage stage){
        stage.addActor(this);
        return this;
    }

    private void addButton(Runnable action, Skin uiSkin, String textOnButton) {
        TextButton button = new TextButton(textOnButton, uiSkin);
        button.addListener(new ButtonChangeListener(action));
        this.button(button);
    }

    private class ButtonChangeListener extends ChangeListener{

        private final Runnable action;

        public ButtonChangeListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            try {
                if (action != null)
                    action.run();
            }catch (Exception e){
                event.cancel();
                event.stop();
                Gdx.app.error(e.getClass().getCanonicalName(),e.getMessage());
                StringBuilder builder = new StringBuilder("\n");
                for (StackTraceElement element : e.getStackTrace()) {
                    builder.append(element.toString()).append('\n');
                }
                Gdx.app.log("StackTrace",builder.toString());
            }
        }
    }
}
