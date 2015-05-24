package com.ragego.gui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class HexaFrameTop extends WidgetGroup {
    private static final int BUTTONS_NB = 11;
    private final static String HEXA_FRAME_NAME = "frame_top";

    private Skin hudSkin, uiSkin;
    private Image hexaFrameTop;
    private Label capturedBlackStonesNumberLabel, capturedWhiteStonesNumberLabel, timeLabel;

    public HexaFrameTop(Skin hudSkin, Skin uiSkin) {
        super();
        this.hudSkin = hudSkin;
        this.uiSkin = uiSkin;
        hexaFrameTop = new Image(hudSkin.getRegion(HEXA_FRAME_NAME));
        this.setWidth(hexaFrameTop.getWidth());
        this.setHeight(hexaFrameTop.getHeight());
        this.addActor(hexaFrameTop);
        this.addLabels();
    }

    public void addLabels() {
        capturedBlackStonesNumberLabel = new Label("0", uiSkin);
        capturedWhiteStonesNumberLabel = new Label("0", uiSkin);
        timeLabel = new Label("0", uiSkin);

        capturedBlackStonesNumberLabel.setWidth(hexaFrameTop.getWidth() * 4 / 64);
        capturedWhiteStonesNumberLabel.setWidth(hexaFrameTop.getWidth() * 4 / 64);
        timeLabel.setWidth(hexaFrameTop.getWidth() * 10 / 64);

        capturedBlackStonesNumberLabel.setFontScale(0.5f);
        capturedWhiteStonesNumberLabel.setFontScale(0.5f);
        timeLabel.setFontScale(1.3f);

        capturedBlackStonesNumberLabel.setAlignment(Align.center, Align.center);
        capturedWhiteStonesNumberLabel.setAlignment(Align.center, Align.center);
        timeLabel.setAlignment(Align.center, Align.center);

        capturedBlackStonesNumberLabel.setPosition(hexaFrameTop.getWidth() * 20 / 64 - capturedBlackStonesNumberLabel.getWidth() * 0.5f + 4,
                hexaFrameTop.getHeight() * 10 / 14 - capturedBlackStonesNumberLabel.getHeight() * 0.5f);
        capturedWhiteStonesNumberLabel.setPosition(hexaFrameTop.getWidth() * 44 / 64 - capturedWhiteStonesNumberLabel.getWidth() * 0.5f + 4,
                hexaFrameTop.getHeight() * 10 / 14 - capturedWhiteStonesNumberLabel.getHeight() * 0.5f);
        timeLabel.setPosition(hexaFrameTop.getWidth() * 0.5f - timeLabel.getWidth() * 0.5f + 5,
                hexaFrameTop.getHeight() * 0.5f - timeLabel.getHeight() * 0.5f);

        this.addActor(capturedBlackStonesNumberLabel);
        this.addActor(capturedWhiteStonesNumberLabel);
        this.addActor(timeLabel);
    }

    public void updateTime(int time) {
        this.timeLabel.setText(String.valueOf(time));
    }

    public void updateCapturedBlackStones(int capturedBlackStonesNumber) {
        this.timeLabel.setText(String.valueOf(capturedBlackStonesNumber));
    }

    public void updateCapturedWhiteStones(int capturedWhiteStonesNumber) {
        this.timeLabel.setText(String.valueOf(capturedWhiteStonesNumber));
    }
}
