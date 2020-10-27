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

    boolean es_carrera = false;
    boolean aceite = false;

    Estat estado;
    double dizquierda, dderecha, dcentral;

    // Variables para la correccion del coche en el circuito.
    boolean inicio_correccion = true;
    int tiempo_correccion = 10;
    int intentos_correccion = 0;
    double cdderecha, cdizquierda;

    int tiempo_disparo = 0;

    public Cotxo1(Agents pare) {
        super(pare, "19", "imatges/Coche_Koenigsegg_One.png");
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

        if (!correccion()) {

            int enemigo = DetectarCoche();
            int aceite = DetectarTaca();

            if (dcentral < 200) {
                if (dderecha > dizquierda) {
                    dreta();
                } else {
                    esquerra();
                }
            } else if (aceite > 0 || enemigo > 0) {

                if (aceite > 0) {
                    if (dderecha > 40 && aceite == 2) {
                        esquerra();
                    } else if (dizquierda > 40 && aceite == 3) {
                        dreta();
                    } else {
                        if (dderecha < dizquierda) {
                            esquerra();
                        } else {
                            dreta();
                        }
                    }
                    this.aceite = true;
                } else if(enemigo > 0){
                    if (dderecha > 40 && enemigo == 2) {
                        esquerra();
                    } else if (dizquierda > 40 && enemigo == 3) {
                        dreta();
                    } else {
                        if (dderecha < dizquierda) {
                            esquerra();
                        } else {
                            dreta();
                        }
                    }
                    this.aceite = true;
                }
            } else if (Math.abs(dderecha - dizquierda) > 80) {
                if (dderecha > dizquierda) {
                    dreta();
                } else {
                    esquerra();
                }
            } else {
                noGiris();
            }

            if (this.aceite && aceite < 0) {
                posaOli();
                this.aceite = false;
            }

            if (dcentral < 140 && dcentral > 130 && estado.marxa == 5) {
                endavant(4);
            } else {
                endavant(marcha(estado.velocitat));
            }

            if (estado.objecteVisor[CENTRAL] == COCHE && tiempo_disparo == 0) {
                dispara();
                tiempo_disparo = 50;
            } else {
                if (tiempo_disparo > 0) {
                    tiempo_disparo--;
                }
            }
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
//                    estado.fuel < 4700
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

    public int DetectarTaca() {
        //Recorrido de los objetos

        double angulo = estado.angle;
        int posCocheX = estado.posicio.x;
        int posCocheY = estado.posicio.y;
        double cx = posCocheX * Math.cos(angulo) + posCocheY * Math.sin(angulo);
        double cy = posCocheY * Math.cos(angulo) - posCocheX * Math.sin(angulo);

        for (int i = 0; i < (int) estado.numObjectes; i++) {
            //Comprobamos si el objeto es una taca
            if (estado.objectes[i].tipus == TACAOLI) {
                //Miramos el sector donde se encuentra el objeto
                if (estado.objectes[i].sector == 2 || estado.objectes[i].sector == 3) {

                    //Calculo de la posicion
                    int posObjX = estado.objectes[i].posicio.x;
                    int posObjY = estado.objectes[i].posicio.y;

                    double ox = posObjX * Math.cos(angulo) + posObjY * Math.sin(angulo);
                    double oy = posObjY * Math.cos(angulo) - posObjX * Math.sin(angulo);

                    double zonaX = Math.abs(cx - ox);
                    double zonaY = Math.abs(cy - oy);
                    if (zonaX < 150 && zonaY < 80) {
                        return estado.objectes[i].sector;
                    }

                }
            }
        }
        return -1;
    }

    public int DetectarCoche() {
        //Recorrido de los objetos

        double angulo = estado.angle;
        int posCocheX = estado.posicio.x;
        int posCocheY = estado.posicio.y;
        double cx = posCocheX * Math.cos(angulo) + posCocheY * Math.sin(angulo);
        double cy = posCocheY * Math.cos(angulo) - posCocheX * Math.sin(angulo);

        //Comprobamos si el objeto es una taca
        if (estado.veigEnemic[1]) {

            //Calculo de la posicion
            int posRivX = estado.posicioEnemic[1].x;
            int posRivY = estado.posicioEnemic[1].y;

            double ox = posRivX * Math.cos(angulo) + posRivY * Math.sin(angulo);
            double oy = posRivY * Math.cos(angulo) - posRivX * Math.sin(angulo);

            double zonaX = Math.abs(cx - ox);
            double zonaY = Math.abs(cy - oy);
            if (zonaX < 200 && zonaY < 80) {
                return estado.sector[1];
            }

        }

        return -1;
    }

    public boolean correccion() {
        return false;
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
