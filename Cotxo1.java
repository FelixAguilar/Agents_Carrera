/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

/**
 *
 * @author Ramon
 */
// Exemple de Cotxo molt bàsic
public class Cotxo1 extends Agent {

    static final boolean DEBUG = false;

    static final int IZQUIERDA = 0;
    static final int CENTRAL = 1;
    static final int DERECHA = 2;
    static final int COCHE = 1;

    Estat estado;
    int espera = 0;

    double dizquierda, dderecha, dcentral, pdderecha, pdizquierda;

    public Cotxo1(Agents pare) {
        super(pare, "Rapido", "imatges/Coche_Koenigsegg_One.png");
    }

    @Override
    public void inicia() {
        setAngleVisors(40);
        setDistanciaVisors(350);
        setVelocitatAngular(9);

        pdizquierda = dizquierda;
        pdderecha = dderecha;
    }

    @Override
    public void avaluaComportament() {

        estado = estatCombat();  // Recuperam la informació actualitzada de l'entorn

        dderecha = estado.distanciaVisors[DERECHA];
        dizquierda = estado.distanciaVisors[IZQUIERDA];
        dcentral = estado.distanciaVisors[CENTRAL];

        acelerar();
        girar();

        pdderecha = dderecha;
        pdizquierda = dizquierda;

    }

    public void girar() {
        if (Math.abs(dderecha - dizquierda) < 70 && dcentral > 200 ) {
            noGiris();
        } else {
            if (dderecha > dizquierda) {
                dreta();
            } else {
                esquerra();
            }
        }
    }

    public void acelerar() {
        if (dcentral > 100) {
            endavant(marcha(estado.velocitat));
        } else if (dcentral < 130) {
            endavant(4/*frenar(dcentral, marcha(velocidad))*/);
        }
    }

    public int frenar(double distancia, int marcha) {
        if (distancia > 100 && marcha == 5) {
            return 4;
        } else if (distancia > 80 && marcha == 4) {
            return 3;
        } else if (distancia > 40 && marcha == 3) {
            return 2;
        } else if (distancia <= 40 && marcha == 2) {
            return 1;
        }
        return 5;
    }

    public int marcha(double velocidad) {
        if (velocidad < 110) {
            return 1;
        } else if (velocidad < 200) {
            return 2;
        } else if (velocidad < 250) {
            return 3;
        } else if (velocidad < 300) {
            return 4;
        } else {
            return 5;
        }
    }
}
