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

/**
 * Customized implementation of the Libgdx dialog class
 * Features three types of dialog boxes :
 * - MESSAGE : displays a message on screen, only features a OK button to validate that it has been read
 * - CONFIRM : displays a message, and gives the choice to the player to accept or deny the proposition
 * - INPUT : displays a text field for each label and gives the choice to the player to validate or cancel
 */
public class RageGoDialog extends Dialog {
    public static final int MESSAGE = 0;
    public static final int CONFIRM = 1;
    private static final int INPUT = 2;
    protected static final Runnable EMPTY_RUNNABLE = new Runnable() {
        @Override
        public void run() {

        }
    };
    private TextButton okButton = new TextButton("Ok", RageGoGame.getUiSkin());
    private TextButton cancelButton = new TextButton("Cancel", RageGoGame.getUiSkin());

    //As Dialog does not implement a getter for the Skin :
    private Skin uiSkin = RageGoGame.getUiSkin();
    private ChangeListener closeDialogListener;

    public RageGoDialog(String title, int dialogType, final Runnable actionOnOk, final Runnable actionOnCancel, CharSequence... args) {
        super(title, RageGoGame.getUiSkin());

        if ((dialogType > 2) || (dialogType < 0)) {
            throw new IllegalArgumentException("dialogType must be equal to 0, 1 or 2");
        }

        Label[] labels = new Label[args.length];
        System.out.println(args.length);
        System.out.println(args[0]);
        for (int i = 0; i < args.length; i++) {
            labels[i] = new Label(args[i], uiSkin);
            labels[i].setAlignment(Align.center);
            labels[i].setWrap(true);
            labels[i].setFontScale(0.8f);
            getContentTable().add(labels[i]).width(500).center().row();
        }

        closeDialogListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        };

        switch (dialogType) {
            case MESSAGE:
                okButton.addListener(new ButtonChangeListener(actionOnOk));
                okButton.addListener(closeDialogListener);
                okButton.getLabel().setFontScale(0.8f);
                this.getButtonTable().add(okButton).width(200);
                break;
            case CONFIRM:
                okButton.addListener(new ButtonChangeListener(actionOnOk));
                okButton.addListener(closeDialogListener);
                cancelButton.addListener(new ButtonChangeListener(actionOnCancel));
                cancelButton.addListener(closeDialogListener);

                okButton.getLabel().setFontScale(0.7f);
                cancelButton.getLabel().setFontScale(0.7f);

                this.getButtonTable().add(okButton).width(200);
                this.getButtonTable().add(cancelButton).width(200);
                break;
            case INPUT:
                break;
        }

        setKeepWithinStage(true);
    }

    public RageGoDialog(String title, String message, int dialogType, Runnable actionOnOk) {
        this(title, dialogType, actionOnOk, null, message);
    }

    public RageGoDialog(String title, String message) {
        this(title, message, RageGoDialog.MESSAGE, EMPTY_RUNNABLE);
    }

    /**
     * Centers the dialog box in the given worldViewport
     *
     * @param viewport The worldViewport of the stage on which the dialog is displayed
     * @return The dialog box.
     */
    public RageGoDialog centerOnViewport(Viewport viewport){
        pack();
        setPosition((viewport.getWorldWidth() - getWidth()) * 0.5f,
                (viewport.getWorldHeight() - getHeight()) * 0.5f);
        return this;
    }

    /**
     * Adds the dialog box to the given stage
     * @param stage The stage on which the dialog is to be displayed
     * @return The dialog box
     */
    public RageGoDialog displayOn(Stage stage){
        stage.addActor(this);
        return this;
    }

    /**
     * Adds the a new button to the button table of the dialog box
     *
     * @param action       The action to be performed when the button is clicked
     * @param textOnButton The text to be displayed on the button
     */
    private void addButton(Runnable action, String textOnButton) {
        TextButton button = new TextButton(textOnButton, uiSkin);
        button.addListener(new ButtonChangeListener(action));
        button(button);
    }

    /**
     * Gets the okButton associated with the dialog box.
     *
     * @return The hudSkin
     */
    public TextButton getOkButton() {
        return okButton;
    }

    /**
     * Gets the cancelButton associated with the dialog box.
     *
     * @return The hudSkin
     */
    public TextButton getCancelButton() {
        return cancelButton;
    }

    /**
     * Defines a customized button change listener
     */
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
