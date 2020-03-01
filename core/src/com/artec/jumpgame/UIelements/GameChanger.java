package com.artec.jumpgame.UIelements;

import com.artec.jumpgame.overlays.GameChangersShopOverlay;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.PlaySounds;
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
 * Created by bartek on 31.07.16.
 */
public class GameChanger
{
    private GameChangersShopOverlay overlay;
    private Viewport viewport;

    private ButtonState buttonState;
    private float baseTop, left, top, width, height;
    private Enums.Upgrade upgrade;

    private float iconSize;
    private float paddingBetweenElements;
    private float spaceBetweenTitle;
    private OverlayButton button;

    private BitmapFont titleFont;
    private BitmapFont descriptionFont;
    private GlyphLayout layout;
    private GlyphLayout titleLayout;

    public GameChanger(float left, float top, float width, Enums.Upgrade upgrade, Viewport viewport, GameChangersShopOverlay overlay)
    {
        this.titleFont = Assets.instance.fonts.comic40;
        this.descriptionFont = Assets.instance.fonts.descriptionFont;
        this.overlay = overlay;
        layout = new GlyphLayout();
        titleLayout = new GlyphLayout();
        init(left, top, width, upgrade, viewport);
    }

    public void init(float left, float top, float width, Enums.Upgrade upgrade, Viewport viewport)
    {
        if(upgrade.bought) buttonState = ButtonState.EQUIP;
        else buttonState = ButtonState.BUY;

        this.viewport = viewport;
        this.upgrade = upgrade;
        this.left = left;
        this.baseTop = top;
        this.top = top;
        this.width = width;
        iconSize = width * 0.3f;
        paddingBetweenElements = width * 0.05f;
        spaceBetweenTitle = viewport.getWorldHeight() * 0.02f;

        titleFont.getData().markupEnabled = true;
        titleFont.getData().setScale(0.4f);
        titleLayout.setText(titleFont, upgrade.TITLE, Color.WHITE, width, Align.center, true);

        descriptionFont.getData().markupEnabled = true;
        descriptionFont.getData().setScale(0.28f);
        layout.setText(descriptionFont, upgrade.DESCRIPTION, Color.WHITE, width, Align.center, true);
        height = titleLayout.height + spaceBetweenTitle + iconSize + paddingBetweenElements + layout.height;

        if(buttonState == ButtonState.EQUIP)
        {
            button = new OverlayButton(new Vector2(left + iconSize + paddingBetweenElements, top - titleLayout.height - spaceBetweenTitle - iconSize * 0.5f),
                    Assets.instance.uiElements.equipButton, Assets.instance.uiElements.equipButtonClicked,
                    Assets.instance.uiElements.equipButtonUnActive, width - iconSize - paddingBetweenElements, iconSize * 0.5f);
        }
        else
        {
            button = new OverlayButton(new Vector2(left + iconSize + paddingBetweenElements, top - titleLayout.height - spaceBetweenTitle - iconSize * 0.5f),
                    Assets.instance.uiElements.buyButton, Assets.instance.uiElements.buyButtonClicked,
                    Assets.instance.uiElements.buyButtonUnActive, width - iconSize - paddingBetweenElements, iconSize * 0.5f);
        }
    }

    public void render(SpriteBatch batch)
    {
        Vector2 drawingPosition = Pools.obtain(Vector2.class).set(left, top - titleLayout.height - spaceBetweenTitle - iconSize);
        Utils.drawTextureRegion(batch, upgrade.icon, drawingPosition, iconSize, iconSize, false);
        Pools.free(drawingPosition);

        if(buttonState == ButtonState.EQUIP)
        {
            if (upgrade == Enums.Upgrade.currentUpgrade) button.setActive(false);
            else button.setActive(true);
        }
        else
        {
            if(Enums.Upgrade.coins >= upgrade.price) button.setActive(true);
            else button.setActive(false);
        }

        titleFont.getData().markupEnabled = true;
        titleFont.getData().setScale(0.35f);

        button.render(batch);
        if(buttonState == ButtonState.BUY)
        {
            if(Enums.Upgrade.coins < upgrade.price)
            {
                batch.setColor(1, 1, 1, 0.4f);
                titleFont.setColor(130f/255f, 130f/255f, 130f/255f, 0.4f);
            }
            float coinSize = Constants.OVERLAY_COIN_SIZE * 0.7f * viewport.getWorldWidth();
            layout.setText(titleFont, ""+upgrade.price);
            Vector2 tempPosition = Pools.obtain(Vector2.class).set(button.getPosition().x + button.getWidth() - button.getWidth()*0.05f - layout.width,
                    button.getPosition().y + button.getHeight()/2 + layout.height/2);

            titleFont.getData().markupEnabled = false;
            titleFont.draw(batch, String.valueOf(upgrade.price), tempPosition.x, tempPosition.y);
            titleFont.getData().markupEnabled = true;

            tempPosition.set(tempPosition.x - coinSize - button.getWidth()*0.02f, button.getPosition().y + button.getHeight()/2 - coinSize/2);
            Utils.drawTextureRegion(batch, Assets.instance.gameChangers.coinAnimation.getKeyFrame(0), tempPosition, coinSize, coinSize, true);

            Pools.free(tempPosition);
            batch.setColor(1, 1, 1, 1);
            titleFont.setColor(1, 1, 1, 1);
        }

        titleFont.getData().setScale(0.4f);
        titleLayout.setText(titleFont, upgrade.TITLE, Color.WHITE, width, Align.center, true);
        titleFont.draw(batch, "[#ffffff]"+upgrade.TITLE, left, top, width, Align.center, true);


        titleFont.getData().setScale(0.35f);
        titleFont.draw(batch, "[#ffffff]BEST: [#50be4b]" + upgrade.highScore + "[#ffffff]", left + iconSize + paddingBetweenElements,
                top - titleLayout.height - spaceBetweenTitle - iconSize * 0.5f - paddingBetweenElements,
                width - iconSize - paddingBetweenElements, Align.center, false);

        descriptionFont.getData().markupEnabled = true;
        descriptionFont.getData().setScale(0.28f);
        layout.setText(descriptionFont, upgrade.DESCRIPTION, Color.WHITE, width, Align.center, true);
        height = titleLayout.height + spaceBetweenTitle + iconSize + paddingBetweenElements + layout.height;
        descriptionFont.draw(batch, upgrade.DESCRIPTION, left, top - titleLayout.height - spaceBetweenTitle - iconSize - paddingBetweenElements, width, Align.center, true);
    }

    public void reload()
    {
        if(buttonState == ButtonState.EQUIP)
            button.setNinePatches(Assets.instance.uiElements.equipButton,
                Assets.instance.uiElements.equipButtonClicked, Assets.instance.uiElements.equipButtonUnActive);
        else
            button.setNinePatches(Assets.instance.uiElements.buyButton, Assets.instance.uiElements.buyButtonClicked,
                    Assets.instance.uiElements.buyButtonUnActive);

        titleFont = Assets.instance.fonts.comic40;
        descriptionFont = Assets.instance.fonts.descriptionFont;
    }

    public boolean checkTouch(Vector2 touchPosition)
    {
        return button.checkTouched(touchPosition);
    }

    public int checkTouchUp(Vector2 touchPosition)
    {
            if (button.checkTouched(touchPosition) && button.getTouched())
            {
                if(buttonState == ButtonState.EQUIP)
                {
                    upgrade.applyValues();
                    System.out.println("TOUCHED UP " + upgrade.name());
                    Enums.Upgrade.save();
                    return 1;
                }
                else
                {
                    if(!overlay.getDialogWindow().isVisible())
                    {
                        overlay.getDialogWindow().init("[#ffffff]Are you sure, you want to buy [#50be4b]"+upgrade.TITLE+"[#ffffff] for [#50be4b]"+upgrade.price+"[#ffffff] coins?",
                                new Vector2(viewport.getWorldWidth()*0.05f, viewport.getWorldHeight()*0.3f), viewport.getWorldWidth()*0.9f,
                                viewport.getWorldHeight()*0.4f, viewport);
                        overlay.getDialogWindow().setVisible(true);
                        return 2;
                    }
                }
            }
        return 0;
    }

    public void buy()
    {
        if(Enums.Upgrade.coins >= upgrade.price)
        {
            Enums.Upgrade.coins -= upgrade.price;
            upgrade.bought = true;
            upgrade.applyValues();
            buttonState = ButtonState.EQUIP;
            Enums.Upgrade.save();
            reload();
            PlaySounds.boughtItem(1f);

            if(overlay.getGameScreen().getGameMain().gameOnAndroid)
            {
                overlay.getGameScreen().getGameMain().androidHandler.boughtGameChanger();
            }
        }
    }

    public void setTopDifference(float padding)
    {
        top = baseTop + padding;
        Vector2 buttonPosition = Pools.obtain(Vector2.class).set(left + iconSize + paddingBetweenElements, top - titleLayout.height - spaceBetweenTitle - iconSize * 0.5f);
        button.setPosition(buttonPosition);
        Pools.free(buttonPosition);
    }

    public float getHeight() {return height;}
    public float getTop() {return top;}

    public enum ButtonState {BUY, EQUIP}
}
