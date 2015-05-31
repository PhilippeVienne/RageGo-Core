package com.ragego.gui.elements;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ragego.gui.screens.GoGameScreen;

/**
 * Defines the infobar at the top of the HUD in the {@link GoGameScreen}
 */
public class HexaFrameTop extends WidgetGroup {
    private static final int BUTTONS_NB = 11;
    private final static String HEXA_FRAME_NAME = "frame_top";

    private boolean isHidden = true;
    private Skin hudSkin, uiSkin;
    private Image hexaFrameTop;
    private WidgetGroup frameTopVisibleGroup = new WidgetGroup();
    private Button frameTopHiddenButton;
    private Label blackPrisonersNumberLabel, whitePrisonersNumberLabel, timeValueLabel,
            blackPrisonersNumberHiddenLabel, whitePrisonersNumberHiddenLabel, timeValueHiddenLabel,
            blackPrisonersHiddenLabel, whitePrisonersHiddenLabel, timeHiddenLabel;

    public HexaFrameTop(Skin hudSkin, Skin uiSkin) {
        super();
        this.hudSkin = hudSkin;
        this.uiSkin = uiSkin;
        hexaFrameTop = new Image(hudSkin.getRegion(HEXA_FRAME_NAME));
        setWidth(hexaFrameTop.getWidth());
        setHeight(hexaFrameTop.getHeight());

        frameTopHiddenButton = new Button(hudSkin, "frame_top_hidden");
        frameTopHiddenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide(false);
            }
        });
        //frameTopHiddenButton.setSize(getPrefWidth(), getPrefHeight());
        frameTopHiddenButton.setPosition(0, hexaFrameTop.getHeight() - frameTopHiddenButton.getHeight());

        frameTopVisibleGroup.addActor(hexaFrameTop);
        addLabels();
        addActor(frameTopVisibleGroup);
        addActor(frameTopHiddenButton);
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

        blackPrisonersNumberLabel.setWidth(hexaFrameTop.getWidth() * 4 / 64);
        whitePrisonersNumberLabel.setWidth(hexaFrameTop.getWidth() * 4 / 64);
        timeValueLabel.setWidth(hexaFrameTop.getWidth() * 10 / 64);

        blackPrisonersNumberHiddenLabel.setWidth(frameTopHiddenButton.getWidth() * 2.5f / 64);
        whitePrisonersNumberHiddenLabel.setWidth(frameTopHiddenButton.getWidth() * 2.5f / 64);
        timeValueHiddenLabel.setWidth(frameTopHiddenButton.getWidth() * 2.5f / 64);
        blackPrisonersHiddenLabel.setWidth(frameTopHiddenButton.getWidth() * 6.5f / 64);
        whitePrisonersHiddenLabel.setWidth(frameTopHiddenButton.getWidth() * 6.5f / 64);
        timeHiddenLabel.setWidth(frameTopHiddenButton.getWidth() * 6.5f / 64);

        blackPrisonersNumberLabel.setFontScale(0.5f);
        whitePrisonersNumberLabel.setFontScale(0.5f);
        timeValueLabel.setFontScale(1.3f);

        blackPrisonersNumberHiddenLabel.setFontScale(0.2f);
        whitePrisonersNumberHiddenLabel.setFontScale(0.2f);
        timeValueHiddenLabel.setFontScale(0.2f);
        blackPrisonersHiddenLabel.setFontScale(0.2f);
        whitePrisonersHiddenLabel.setFontScale(0.2f);
        timeHiddenLabel.setFontScale(0.2f);

        blackPrisonersNumberLabel.setAlignment(Align.center, Align.center);
        whitePrisonersNumberLabel.setAlignment(Align.center, Align.center);
        timeValueLabel.setAlignment(Align.center, Align.center);

        blackPrisonersNumberHiddenLabel.setAlignment(Align.center, Align.center);
        whitePrisonersNumberHiddenLabel.setAlignment(Align.center, Align.center);
        timeValueHiddenLabel.setAlignment(Align.center, Align.center);
        blackPrisonersHiddenLabel.setAlignment(Align.center, Align.center);
        whitePrisonersHiddenLabel.setAlignment(Align.center, Align.center);
        timeHiddenLabel.setAlignment(Align.center, Align.center);

        blackPrisonersNumberLabel.setPosition(hexaFrameTop.getWidth() * 20 / 64 - blackPrisonersNumberLabel.getWidth() * 0.5f + 4,
                hexaFrameTop.getHeight() * 10 / 14 - blackPrisonersNumberLabel.getHeight() * 0.5f);
        whitePrisonersNumberLabel.setPosition(hexaFrameTop.getWidth() * 44 / 64 - whitePrisonersNumberLabel.getWidth() * 0.5f + 4,
                hexaFrameTop.getHeight() * 10 / 14 - whitePrisonersNumberLabel.getHeight() * 0.5f);
        timeValueLabel.setPosition(hexaFrameTop.getWidth() * 0.5f - timeValueLabel.getWidth() * 0.5f + 5,
                hexaFrameTop.getHeight() * 0.5f - timeValueLabel.getHeight() * 0.5f);

        blackPrisonersNumberHiddenLabel.setPosition(frameTopHiddenButton.getWidth() * 15.25f / 64 - blackPrisonersNumberHiddenLabel.getWidth() * 0.5f,
                frameTopHiddenButton.getHeight() * 0.5f - blackPrisonersNumberHiddenLabel.getHeight() * 0.5f);
        whitePrisonersNumberHiddenLabel.setPosition(frameTopHiddenButton.getWidth() * 27.25f / 64 - whitePrisonersNumberHiddenLabel.getWidth() * 0.5f,
                frameTopHiddenButton.getHeight() * 0.5f - whitePrisonersNumberHiddenLabel.getHeight() * 0.5f);
        timeValueHiddenLabel.setPosition(frameTopHiddenButton.getWidth() * 51.25f / 64 - timeValueHiddenLabel.getWidth() * 0.5f,
                frameTopHiddenButton.getHeight() * 0.5f - timeValueHiddenLabel.getHeight() * 0.5f);
        blackPrisonersHiddenLabel.setPosition(frameTopHiddenButton.getWidth() * 10.75f / 64 - blackPrisonersHiddenLabel.getWidth() * 0.5f,
                frameTopHiddenButton.getHeight() * 0.5f - blackPrisonersHiddenLabel.getHeight() * 0.5f);
        whitePrisonersHiddenLabel.setPosition(frameTopHiddenButton.getWidth() * 22.75f / 64 - whitePrisonersHiddenLabel.getWidth() * 0.5f,
                frameTopHiddenButton.getHeight() * 0.5f - whitePrisonersHiddenLabel.getHeight() * 0.5f);
        timeHiddenLabel.setPosition(frameTopHiddenButton.getWidth() * 46.75f / 64 - timeHiddenLabel.getWidth() * 0.5f,
                frameTopHiddenButton.getHeight() * 0.5f - timeHiddenLabel.getHeight() * 0.5f);

        frameTopVisibleGroup.addActor(blackPrisonersNumberLabel);
        frameTopVisibleGroup.addActor(whitePrisonersNumberLabel);
        frameTopVisibleGroup.addActor(timeValueLabel);

        frameTopHiddenButton.addActor(blackPrisonersNumberHiddenLabel);
        frameTopHiddenButton.addActor(whitePrisonersNumberHiddenLabel);
        frameTopHiddenButton.addActor(timeValueHiddenLabel);
        frameTopHiddenButton.addActor(blackPrisonersHiddenLabel);
        frameTopHiddenButton.addActor(whitePrisonersHiddenLabel);
        frameTopHiddenButton.addActor(timeHiddenLabel);
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
            frameTopVisibleGroup.setVisible(false);
            frameTopHiddenButton.setVisible(true);
        } else {
            frameTopVisibleGroup.setVisible(true);
            frameTopHiddenButton.setVisible(false);
        }
    }
}
