// model/Bullet.java
package model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle; // Import Rectangle

public class Bullet {
    private float x, y;
    private float speed;
    private float dirX, dirY; // Direction vector
    private TextureRegion textureRegion;
    private float width, height;
    private int damage; // NEW: Damage value for the bullet

    public Bullet(float x, float y, float speed, float dirX, float dirY,
                  TextureRegion textureRegion, float width, float height,
                  int damage) { // NEW: Constructor now accepts damage
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.dirX = dirX;
        this.dirY = dirY;
        this.textureRegion = textureRegion;
        this.width = width;
        this.height = height;
        this.damage = damage; // Set the damage
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

    public int getDamage() { // NEW: Getter for damage
        return damage;
    }

    public Rectangle getBounds() { // NEW: getBounds method
        return new Rectangle(x, y, width, height);
    }
}
