import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Lander pre hru Moon Lander
 * – používa triedy Platno, Manazer a Teren.
 */
public class Lander {
    private double x;
    private double y;
    private double vy;
    private double vx;
    private double fuel;
    private boolean thrust;
    private boolean left;
    private boolean right;
    private double angle;
    private Platno platno;
    private BufferedImage obrazok;
    private Teren teren;

    private final double GRAVITY = 0.05;
    private final double THRUST_POWER = 0.15;
    private final double ROTATION_STEP = 5;

    private boolean pristal = false;

    public Lander(Teren teren) {
        this.x = 250;
        this.y = 50;
        this.vx = 0;
        this.vy = 0;
        this.fuel = 100;
        this.angle = 0;
        this.thrust = false;
        this.teren = teren;
        this.platno = Platno.dajPlatno();

        // Trojuholník ako raketa
        this.obrazok = new BufferedImage(20, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = obrazok.createGraphics();
        g.setColor(Color.GRAY);
        int[] xs = {10, 0, 20};
        int[] ys = {0, 30, 30};
        g.fillPolygon(xs, ys, 3);
        g.dispose();

        platno.draw(this, obrazok, AffineTransform.getTranslateInstance(x, y));

        new Manazer().spravujObjekt(this);
    }

    // Ovládanie
    public void posunHore()   { if (!pristal) this.thrust = true; }
    public void posunDole()   { this.thrust = false; }
    public void posunVlavo()  { if (!pristal) this.left = true; }
    public void posunVpravo() { if (!pristal) this.right = true; }

    public void zrus() {
        this.left = false;
        this.right = false;
        this.thrust = false;
    }

    // Hlavný fyzikálny cyklus
    public void tik() {
        if (pristal) return; // keď je pristátie ukončené, nič nerob
        if (left)  angle -= ROTATION_STEP;
        if (right) angle += ROTATION_STEP;

        if (thrust && fuel > 0) {
            double rad = Math.toRadians(angle);
            vx += Math.sin(rad) * THRUST_POWER;
            vy -= Math.cos(rad) * THRUST_POWER;
            fuel -= 0.3;
            if (fuel < 0) fuel = 0;
        }

        // gravitácia
        vy += GRAVITY;

        // pohyb
        x += vx;
        y += vy;

        // kontrola zeme
        double spodokLandera = y + obrazok.getHeight();
        double vyskaZeme = teren.getYPodlaha();

        if (spodokLandera >= vyskaZeme) {
            // kontakt so zemou
            y = vyskaZeme - obrazok.getHeight();

            if (Math.abs(vy) < 5 && Math.abs(vx) < 5 && Math.abs(angle) < 15) {
                System.out.println("✅ Bezpečné pristátie!");
            } else {
                System.out.println("💥 Havária!");
            }


            pristal = true;
            vy = 0;
            vx = 0;
        }

        // vykreslenie
        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        t.rotate(Math.toRadians(angle), obrazok.getWidth() / 2.0, obrazok.getHeight() / 2.0);
        platno.draw(this, obrazok, t);
    }
}