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
    double dizquierda, dderecha, dcentral;

    // Variables para la correccion del coche en el circuito.
    boolean inicio_correccion = true;
    int tiempo_correccion = 10;
    int intentos_correccion = 0;
    double cdderecha, cdizquierda;

    public Cotxo1(Agents pare) {
        super(pare, "19", "imatges/Coche_Koenigsegg_One.png");
    }

    @Override
    public void inicia() {
        setAngleVisors(40);
        setDistanciaVisors(350);
        setVelocitatAngular(9);

    }

    @Override
    public void avaluaComportament() {

        estado = estatCombat();  // Recuperam la informació actualitzada de l'entorn

        dderecha = estado.distanciaVisors[DERECHA];
        dizquierda = estado.distanciaVisors[IZQUIERDA];
        dcentral = estado.distanciaVisors[CENTRAL];

        if (tiempo_correccion == 0) {
            if (!correccion()) {
                acelerar();
                girar();
            }
        } else {
            tiempo_correccion--;
        }
    }

    public boolean correccion() {
        if (estado.enCollisio) {
            if (intentos_correccion == 1) {
                endavant(1);
                intentos_correccion--;
                return false;
            }
            enrere(1);
            intentos_correccion++;
            tiempo_correccion = 10;
            return true;
        } else if (estado.contraDireccio) {
            // Corregir.
            if (inicio_correccion) {
                cdizquierda = dizquierda;
                cdderecha = dderecha;
                inicio_correccion = false;
            }
            if (cdderecha > cdizquierda) {
                dreta();
            } else {
                esquerra();
            }
            return true;
        }  else {
            inicio_correccion = true;
            return false;
        }
    }

    public void girar() {
        if (Math.abs(dderecha - dizquierda) < 75 && dcentral > 150) {
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
        if (estaGirant()) {
            if (dcentral < 115 && dcentral > 110 && marcha(estado.velocitat) == 5) {
                endavant(4);
            }
        } else {
            endavant(marcha(estado.velocitat));
        }
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
