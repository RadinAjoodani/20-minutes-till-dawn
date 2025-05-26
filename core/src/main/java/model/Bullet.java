<<<<<<< HEAD

=======
// model/Bullet.java
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
package model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
<<<<<<< HEAD
import com.badlogic.gdx.math.Rectangle;
=======
import com.badlogic.gdx.math.Rectangle; // Import Rectangle
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86

public class Bullet {
    private float x, y;
    private float speed;
<<<<<<< HEAD
    private float dirX, dirY;
    private TextureRegion textureRegion;
    private float width, height;
    private int damage;

    public Bullet(float x, float y, float speed, float dirX, float dirY,
                  TextureRegion textureRegion, float width, float height,
                  int damage) {
=======
    private float dirX, dirY; // Direction vector
    private TextureRegion textureRegion;
    private float width, height;
    private int damage; // NEW: Damage value for the bullet

    public Bullet(float x, float y, float speed, float dirX, float dirY,
                  TextureRegion textureRegion, float width, float height,
                  int damage) { // NEW: Constructor now accepts damage
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.dirX = dirX;
        this.dirY = dirY;
        this.textureRegion = textureRegion;
        this.width = width;
        this.height = height;
<<<<<<< HEAD
        this.damage = damage;
=======
        this.damage = damage; // Set the damage
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
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

<<<<<<< HEAD
    public int getDamage() {
        return damage;
    }

    public Rectangle getBounds() {
=======
    public int getDamage() { // NEW: Getter for damage
        return damage;
    }

    public Rectangle getBounds() { // NEW: getBounds method
>>>>>>> 1713f21e921b05a8bbc5730f5aa20cd1530a7a86
        return new Rectangle(x, y, width, height);
    }
}
