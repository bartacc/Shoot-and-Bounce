package com.artec.jumpgame.UIelements;

import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 05.08.16.
 */
public class DialogWindow
{
    private Viewport viewport;
    private String text;
    private Vector2 position;
    private float width, height;

    private BitmapFont font;
    private float textPaddingSides, textPaddingUp;
    private GlyphLayout layout;

    private OverlayButton yesButton;
    private OverlayButton noButton;

    private boolean visible;

    public DialogWindow()
    {
        this.layout = new GlyphLayout();
        position = new Vector2();
    }

    public void init(String text, Vector2 position, float width, float height, Viewport viewport)
    {
        this.font = Assets.instance.fonts.descriptionFont;
        this.text = text;
        this.position.set(position);
        this.width = width;
        this.height = height;
        this.viewport = viewport;
        layout.setText(font, text, Color.BLACK, width, Align.center, true);

        textPaddingSides = Constants.TEXT_PADDING_SIDES * viewport.getWorldWidth();
        textPaddingUp = Constants.TEXT_PADDING_UP * viewport.getWorldHeight();

        float buttonWidth = Constants.DIALOG_WINDOW_BUTTON_SIZE * viewport.getWorldWidth();
        yesButton = new OverlayButton(new Vector2(position.x + width/4 - buttonWidth/2, position.y + width*0.05f),
                Assets.instance.uiElements.yesButton, Assets.instance.uiElements.yesButtonClicked, buttonWidth);
        noButton = new OverlayButton(new Vector2(position.x + width*0.75f - buttonWidth/2, position.y + width*0.05f),
                Assets.instance.uiElements.noButton, Assets.instance.uiElements.noButtonClicked, buttonWidth);
    }

    public void render(SpriteBatch batch)
    {
        Vector2 tempPosition = Pools.obtain(Vector2.class).set(0, 0);
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.fadedBackground, tempPosition, viewport.getWorldWidth(), viewport.getWorldHeight(),false);

        Assets.instance.uiElements.textBackground.draw(batch, position.x, position.y, width, height);
        font.getData().setScale(0.25f);
        font.draw(batch, text, position.x + textPaddingSides, position.y + height - textPaddingUp, width - textPaddingSides*2, Align.center, true);

        yesButton.render(batch);
        noButton.render(batch);

        Pools.free(tempPosition);
    }

    public boolean checkTouch(Vector2 touchPosition)
    {
        return yesButton.checkTouched(touchPosition) || noButton.checkTouched(touchPosition);
    }

    public int checkTouchUp(Vector2 touchPosition)
    {
        if(yesButton.checkTouched(touchPosition) && yesButton.getTouched())
            return 1;
        else if (noButton.checkTouched(touchPosition) && noButton.getTouched())
            return -1;
        else return 0;
    }

    public void reload()
    {
        if(yesButton != null) yesButton.setRegions(Assets.instance.uiElements.yesButton, Assets.instance.uiElements.yesButtonClicked);
        if(noButton != null) noButton.setRegions(Assets.instance.uiElements.noButton, Assets.instance.uiElements.noButtonClicked);
        font = Assets.instance.fonts.descriptionFont;
    }

    public void setVisible(boolean newVisible) {visible = newVisible;}
    public boolean isVisible() {return visible;}
}

