
package model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    private float x, y;
    private float speed;
    private float dirX, dirY;
    private TextureRegion textureRegion;
    private float width, height;
    private int damage;

    public Bullet(float x, float y, float speed, float dirX, float dirY,
                  TextureRegion textureRegion, float width, float height,
                  int damage) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.dirX = dirX;
        this.dirY = dirY;
        this.textureRegion = textureRegion;
        this.width = width;
        this.height = height;
        this.damage = damage;
    }

    public void update(float delta) {
        x += dirX * speed * delta;
        y += dirY * speed * delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(textureRegion, x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getDamage() {
        return damage;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
