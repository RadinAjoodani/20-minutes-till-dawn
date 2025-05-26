<<<<<<< HEAD

=======
// model/Seed.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

<<<<<<< HEAD

public class Seed {
    private float x, y;
    private TextureRegion textureRegion;
    private int xpValue;
    private static final float DRAW_SIZE = 30f;
=======
/**
 * Represents a collectible seed dropped by a defeated enemy.
 */
public class Seed {
    private float x, y;
    private TextureRegion textureRegion;
    private int xpValue; // How much XP this seed grants
    private static final float DRAW_SIZE = 30f; // Standard size for the seed
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

    public Seed(float x, float y, TextureRegion textureRegion, int xpValue) {
        this.x = x;
        this.y = y;
        this.textureRegion = textureRegion;
        this.xpValue = xpValue;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(textureRegion, x, y, DRAW_SIZE, DRAW_SIZE);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, DRAW_SIZE, DRAW_SIZE);
    }

    public int getXpValue() {
        return xpValue;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
