package com.ragego.gui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.ragego.gui.screens.GoGameScreen;

/**
 * Defines the infobar at the top of the HUD in the {@link GoGameScreen}
 */
public class HexaFrameTop extends WidgetGroup {
    private static final int BUTTONS_NB = 11;
    private final static String HEXA_FRAME_NAME = "frame_top";

    private boolean isHidden = true;
    private Skin hudSkin, uiSkin;
    private Image hexaFrameImage;
    private WidgetGroup frameVisibleGroup = new WidgetGroup();
    private Button frameHiddenButton;
    private Label blackPrisonersNumberLabel, whitePrisonersNumberLabel, timeValueLabel,
            blackPrisonersNumberHiddenLabel, whitePrisonersNumberHiddenLabel, timeValueHiddenLabel,
            blackPrisonersHiddenLabel, whitePrisonersHiddenLabel, timeHiddenLabel;

    public HexaFrameTop(Skin hudSkin, Skin uiSkin) {
        super();
        this.hudSkin = hudSkin;
        this.uiSkin = uiSkin;
        hexaFrameImage = new Image(hudSkin.getRegion(HEXA_FRAME_NAME));
        setWidth(hexaFrameImage.getWidth());
        setHeight(hexaFrameImage.getHeight());

        frameHiddenButton = new Button(hudSkin, "frame_top_hidden");
        frameHiddenButton.setPosition(0, hexaFrameImage.getHeight() - frameHiddenButton.getHeight());

        frameVisibleGroup.addActor(hexaFrameImage);
        addLabels();
        addActor(frameVisibleGroup);
        addActor(frameHiddenButton);
    }

    /**
     * Adds the various labels containing informations of the current go game : the number of stones captured
     * by each player and the time remaining for the player's turn
     */
    public void addLabels() {
        blackPrisonersNumberLabel = new Label("0", uiSkin);
        whitePrisonersNumberLabel = new Label("0", uiSkin);
        timeValueLabel = new Label("", uiSkin);

        blackPrisonersNumberHiddenLabel = new Label("0", uiSkin);
        whitePrisonersNumberHiddenLabel = new Label("0", uiSkin);
        timeValueHiddenLabel = new Label("", uiSkin);
        blackPrisonersHiddenLabel = new Label("Black prisoners", uiSkin);
        whitePrisonersHiddenLabel = new Label("White prisoners", uiSkin);
        timeHiddenLabel = new Label("Time", uiSkin);

        blackPrisonersNumberLabel.setWidth(hexaFrameImage.getWidth() * 4 / 64);
        whitePrisonersNumberLabel.setWidth(hexaFrameImage.getWidth() * 4 / 64);
        timeValueLabel.setWidth(hexaFrameImage.getWidth() * 10 / 64);

        blackPrisonersNumberHiddenLabel.setWidth(frameHiddenButton.getWidth() * 2.5f / 64);
        whitePrisonersNumberHiddenLabel.setWidth(frameHiddenButton.getWidth() * 2.5f / 64);
        timeValueHiddenLabel.setWidth(frameHiddenButton.getWidth() * 2.5f / 64);
        blackPrisonersHiddenLabel.setWidth(frameHiddenButton.getWidth() * 6.5f / 64);
        whitePrisonersHiddenLabel.setWidth(frameHiddenButton.getWidth() * 6.5f / 64);
        timeHiddenLabel.setWidth(frameHiddenButton.getWidth() * 6.5f / 64);

        blackPrisonersNumberLabel.setFontScale(0.5f);
        whitePrisonersNumberLabel.setFontScale(0.5f);
        timeValueLabel.setFontScale(1.3f);

        blackPrisonersNumberHiddenLabel.setFontScale(0.32f);
        whitePrisonersNumberHiddenLabel.setFontScale(0.32f);
        timeValueHiddenLabel.setFontScale(0.32f);
        blackPrisonersHiddenLabel.setFontScale(0.32f);
        whitePrisonersHiddenLabel.setFontScale(0.32f);
        timeHiddenLabel.setFontScale(0.32f);

        blackPrisonersNumberLabel.setAlignment(Align.center, Align.center);
        whitePrisonersNumberLabel.setAlignment(Align.center, Align.center);
        timeValueLabel.setAlignment(Align.center, Align.center);

        blackPrisonersNumberHiddenLabel.setAlignment(Align.center, Align.center);
        whitePrisonersNumberHiddenLabel.setAlignment(Align.center, Align.center);
        timeValueHiddenLabel.setAlignment(Align.center, Align.center);
        blackPrisonersHiddenLabel.setAlignment(Align.center, Align.center);
        whitePrisonersHiddenLabel.setAlignment(Align.center, Align.center);
        timeHiddenLabel.setAlignment(Align.center, Align.center);

        blackPrisonersNumberLabel.setPosition(hexaFrameImage.getWidth() * 20 / 64 - blackPrisonersNumberLabel.getWidth() * 0.5f + 4,
                hexaFrameImage.getHeight() * 10 / 14 - blackPrisonersNumberLabel.getHeight() * 0.5f);
        whitePrisonersNumberLabel.setPosition(hexaFrameImage.getWidth() * 44 / 64 - whitePrisonersNumberLabel.getWidth() * 0.5f + 4,
                hexaFrameImage.getHeight() * 10 / 14 - whitePrisonersNumberLabel.getHeight() * 0.5f);
        timeValueLabel.setPosition(hexaFrameImage.getWidth() * 0.5f - timeValueLabel.getWidth() * 0.5f + 5,
                hexaFrameImage.getHeight() * 0.5f - timeValueLabel.getHeight() * 0.5f);

        blackPrisonersNumberHiddenLabel.setPosition(frameHiddenButton.getWidth() * 27.625f / 64 - blackPrisonersNumberHiddenLabel.getWidth() * 0.5f,
                frameHiddenButton.getHeight() * 0.5f - blackPrisonersNumberHiddenLabel.getHeight() * 0.5f + 2);
        whitePrisonersNumberHiddenLabel.setPosition(frameHiddenButton.getWidth() * 36.375f / 64 - whitePrisonersNumberHiddenLabel.getWidth() * 0.5f,
                frameHiddenButton.getHeight() * 0.5f - whitePrisonersNumberHiddenLabel.getHeight() * 0.5f + 2);
        timeValueHiddenLabel.setPosition(frameHiddenButton.getWidth() * 57.625f / 64 - timeValueHiddenLabel.getWidth() * 0.5f,
                frameHiddenButton.getHeight() * 0.5f - timeValueHiddenLabel.getHeight() * 0.5f + 2);
        blackPrisonersHiddenLabel.setPosition(frameHiddenButton.getWidth() * 16 / 64 - blackPrisonersHiddenLabel.getWidth() * 0.5f,
                frameHiddenButton.getHeight() * 0.5f - blackPrisonersHiddenLabel.getHeight() * 0.5f + 2);
        whitePrisonersHiddenLabel.setPosition(frameHiddenButton.getWidth() * 43 / 64 - whitePrisonersHiddenLabel.getWidth() * 0.5f,
                frameHiddenButton.getHeight() * 0.5f - whitePrisonersHiddenLabel.getHeight() * 0.5f + 2);
        timeHiddenLabel.setPosition(frameHiddenButton.getWidth() * 53 / 64 - timeHiddenLabel.getWidth() * 0.5f,
                frameHiddenButton.getHeight() * 0.5f - timeHiddenLabel.getHeight() * 0.5f + 2);

        frameVisibleGroup.addActor(blackPrisonersNumberLabel);
        frameVisibleGroup.addActor(whitePrisonersNumberLabel);
        frameVisibleGroup.addActor(timeValueLabel);

        frameHiddenButton.addActor(blackPrisonersNumberHiddenLabel);
        frameHiddenButton.addActor(whitePrisonersNumberHiddenLabel);
        frameHiddenButton.addActor(timeValueHiddenLabel);
        frameHiddenButton.addActor(blackPrisonersHiddenLabel);
        frameHiddenButton.addActor(whitePrisonersHiddenLabel);
        frameHiddenButton.addActor(timeHiddenLabel);
    }

    /**
     * Updates the displayed time
     *
     * @param time The time value (in seconds) of the remaining time
     */
    public void updateTime(int time) {
        timeValueLabel.setText(String.valueOf(time));
        timeValueHiddenLabel.setText(String.valueOf(time));
    }

    /**
     * Updates the displayed number of captured black stones
     * @param capturedBlackStonesNumber The number of captured black stones
     */
    public void updateBlackPrisoners(int capturedBlackStonesNumber) {
        blackPrisonersNumberLabel.setText(String.valueOf(capturedBlackStonesNumber));
        blackPrisonersNumberHiddenLabel.setText(String.valueOf(capturedBlackStonesNumber));
    }

    /**
     * Updates the displayed number of captured white stones
     * @param capturedWhiteStonesNumber The number of captured white stones
     */
    public void updateWhitePrisoners(int capturedWhiteStonesNumber) {
        whitePrisonersNumberLabel.setText(String.valueOf(capturedWhiteStonesNumber));
        whitePrisonersNumberHiddenLabel.setText(String.valueOf(capturedWhiteStonesNumber));
    }

    public void hide(boolean state) {
        isHidden = state;
        if (isHidden) {
            frameVisibleGroup.setVisible(false);
            frameHiddenButton.setVisible(true);
        } else {
            frameVisibleGroup.setVisible(true);
            frameHiddenButton.setVisible(false);
        }
    }

    public Button getHiddenButton() {
        return frameHiddenButton;
    }
}
