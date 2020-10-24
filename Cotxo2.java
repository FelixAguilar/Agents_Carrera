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
public class Cotxo2 extends Agent {

    static final boolean DEBUG = false;

    static final int IZQUIERDA = 0;
    static final int CENTRAL = 1;
    static final int DERECHA = 2;
    static final int COCHE = 1;

    boolean es_carrera = false;

    Estat estado;
    double dizquierda, dderecha, dcentral;

    // Variables para la correccion del coche en el circuito.
    boolean inicio_correccion = true;
    int tiempo_correccion = 10;
    int intentos_correccion = 0;
    double cdderecha, cdizquierda;

    public Cotxo2(Agents pare) {
        super(pare, "enemigo", "imatges/CotxoV.png");
    }

    @Override
    public void inicia() {
        setAngleVisors(40);
        setDistanciaVisors(350);
        setVelocitatAngular(9);

        estado = estatCombat();
        es_carrera = estado.numBitxos > 1;
    }

    @Override
    public void avaluaComportament() {

        estado = estatCombat();  // Recuperam la informació actualitzada de l'entorn

        dderecha = estado.distanciaVisors[DERECHA];
        dizquierda = estado.distanciaVisors[IZQUIERDA];
        dcentral = estado.distanciaVisors[CENTRAL];

        if (es_carrera) {
            carrera();
        } else {
            contrareloj();
        }
    }

    public void carrera() {

        if (Math.abs(dderecha - dizquierda) < 80 && dcentral > 180) {
            noGiris();
        } else {
            if (dderecha > dizquierda) {
                dreta();
            } else {
                esquerra();
            }
        }
        if (estaGirant()) {
            if (dcentral < 115 && dcentral > 110 && estado.marxa == 5) {
                endavant(4);
            }
        } else {
            endavant(marcha(estado.velocitat));
        }

    }

    public void contrareloj() {

        if (tiempo_correccion == 0) {
            if (!correccion()) {

                // Giro.
                if (dcentral > 180) {
                    if (Math.abs(dderecha - dizquierda) > 80) {
                        if (dderecha > dizquierda) {
                            dreta();
                        } else {
                            esquerra();
                        }
                    } else if (Math.abs(dderecha - dizquierda) < 35) {
                        if (dderecha > dizquierda) {
                            esquerra();
                        } else {
                            dreta();
                        }
                    } else {
                        noGiris();
                    }
                } else {
                    if (dderecha > dizquierda) {
                        dreta();
                    } else {
                        esquerra();
                    }
                }

                // Acceleracion.
                if (estaGirant()) {
                    if (dcentral < 115 && dcentral > 110 && estado.marxa == 5) {
                        endavant(4);
                    }
                } else {
                    if (estado.fuel < 4700) {
                        endavant(marcha(estado.velocitat));
                    } else {
                        endavant(Math.min(marcha(estado.velocitat), 4));
                    }
                }
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
        } else {
            inicio_correccion = true;
            return false;
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
