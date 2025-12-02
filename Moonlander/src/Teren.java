import java.util.Random;

/**
 * Trieda Teren – vytvára a zobrazuje plynulé „kopce“,
 * pričom pristávacia zelená plocha je rovná.
 */
public class Teren {
    private int sirka;        // počet stĺpcov
    private int vyska;        // počet riadkov
    private int velkost;      // veľkosť jednej bunky (pixelov)
    private Stvorec[][] stvorce;
    private Random nahoda;
    private int posunY;       // posun na spodok plátna

    private int zaciatokPlochy;
    private int koniecPlochy;
    private int zakladnaVyska;

    public Teren(int sirka, int vyska) {
        this.sirka = sirka;
        this.vyska = vyska;
        this.velkost = 15;
        this.stvorce = new Stvorec[vyska][sirka];
        this.nahoda = new Random();

        int vyskaPlatna = Platno.dajPlatno().getVyska();
        this.posunY = vyskaPlatna - (vyska * this.velkost);

        vytvorTeren();
    }

    /** Vytvorí plynulý terén s kopcami a rovným pristávacím pásom. */
    private void vytvorTeren() {
        this.zakladnaVyska = vyska - 3;
        this.zaciatokPlochy = nahoda.nextInt(sirka - 6);
        this.koniecPlochy = zaciatokPlochy + 4;

        // výšková mapa kopcov (plynulé "vlny")
        int[] vysky = new int[sirka];
        vysky[0] = zakladnaVyska - nahoda.nextInt(2); // začiatok

        for (int c = 1; c < sirka; c++) {
            int pred = vysky[c - 1];

            if (c >= zaciatokPlochy && c <= koniecPlochy) {
                // pristávací úsek - rovný
                vysky[c] = zakladnaVyska;
            } else {
                // mimo pristávacej zóny – plynulé malé stúpanie/klesanie
                int delta = nahoda.nextInt(3) - 1; // -1, 0, alebo +1
                vysky[c] = pred + delta;
                if (vysky[c] > zakladnaVyska) vysky[c] = zakladnaVyska;
                if (vysky[c] < zakladnaVyska - 3) vysky[c] = zakladnaVyska - 3;
            }
        }

        // vykresli celý terén podľa výškov
        for (int c = 0; c < sirka; c++) {
            for (int r = vysky[c]; r < vyska; r++) {
                Stvorec s = new Stvorec();
                s.zmenStranu(velkost);

                // pristávací pás zostane zelený
                if (c >= zaciatokPlochy && c <= koniecPlochy && r == zakladnaVyska) {
                    s.zmenFarbu("green");
                } else {
                    s.zmenFarbu("black");
                }

                s.zobraz();
                s.posunVodorovne(c * velkost);
                s.posunZvisle(r * velkost + posunY);
                stvorce[r][c] = s;
            }
        }
    }

    /** Vracia Y-súradnicu vrchu zeme pre určitý X pixel (presné kolízie). */
    public int getYPovrchPreX(int xPixel) {
        int stlpec = xPixel / velkost;
        if (stlpec < 0) stlpec = 0;
        if (stlpec >= sirka) stlpec = sirka - 1;
        for (int r = 0; r < vyska; r++) {
            if (stvorce[r][stlpec] != null) {
                return posunY + (r * velkost);
            }
        }
        return posunY + (zakladnaVyska * velkost);
    }

    /** vracia stred výšky roviny (napr. pre orientáciu landera) */
    public int getYPodlaha() {
        return posunY + zakladnaVyska * velkost;
    }

    public int getVelkostBunky() {
        return velkost;
    }
}