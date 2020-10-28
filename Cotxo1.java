package agents;

/**
 *
 * @author Felix Aguilar y Jaume Mesquida.
 */
public class Cotxo1 extends Agent {

    //Constantes para facilitar la lectura del codigo.
    static final boolean DEBUG = false;
    static final int IZQUIERDA = 0;
    static final int CENTRAL = 1;
    static final int DERECHA = 2;
    static final int COCHE = 1;

    // Estado del agente y algunos de sus valores mas usados.
    Estat estado;
    boolean es_carrera;
    double dizquierda, dderecha, dcentral, oizquierda, oderecha, ocentral;

    // Variables relacionadas con acciones.
    int tiempo_disparo = 0;
    boolean hay_aceite = false;

    // Variables relacionadas con la correccion de direccion.
    int tiempo_correccion = 0;
    int intentos_correccion = 0;
    boolean giro = false;
    boolean contra = false;

    // Constructor de la clase.
    public Cotxo1(Agents pare) {
        super(pare, "19", "imatges/Coche_Koenigsegg_One.png");
    }

    // Inicializacion del agente.
    @Override
    public void inicia() {
        setAngleVisors(40);
        setDistanciaVisors(350);
        setVelocitatAngular(9);

        // Identifica si esta en una carrera o contrareloj.
        estado = estatCombat();
        es_carrera = estado.numBitxos > 1;
    }

    @Override
    public void avaluaComportament() {

        // Recupera el entorno del coche.
        estado = estatCombat();

        // Obtiene las distancias de los visores.
        dderecha = estado.distanciaVisors[DERECHA];
        dizquierda = estado.distanciaVisors[IZQUIERDA];
        dcentral = estado.distanciaVisors[CENTRAL];

        // Selecciona el comportamiento segun si esta en una carrera o bien en contrareloj.
        if (es_carrera) {

            // Obtiene los objetos que ve con los visores.
            oderecha = estado.objecteVisor[DERECHA];
            oizquierda = estado.objecteVisor[IZQUIERDA];
            ocentral = estado.objecteVisor[CENTRAL];

            carrera();
        } else {
            contrareloj();
        }
    }

    /**
     * Comportamiento si esta en un entorno de carrera.
     */
    public void carrera() {

        // Si no hace falta correguir el comportamiento entonces entra.
        if (!correccion()) {

            int aceite = DetectarRecursos(TACAOLI);
            int enemigo = DetectarCoche();
            if (aceite > 0) {
                if (dderecha > 50 && aceite == 2) {
                    esquerra();
                } else if (dizquierda > 50 && aceite == 3) {
                    dreta();
                } else {
                    if (dderecha < dizquierda) {
                        esquerra();
                    } else {
                        dreta();
                    }
                }

                // Indica que hay aceite para dejar uno como trampa.
                hay_aceite = true;
            
            } else if (enemigo > 20) {
                System.out.println("Hola");

                        if (dderecha > 50 && enemigo == 2) {
                            esquerra();
                        } else if (dizquierda > 50 && enemigo == 3) {
                            dreta();
                        } else {
                            if (dderecha < dizquierda) {
                                esquerra();
                            } else {
                                dreta();
                            }
                        }
            } else {

                // Comprueba que este en el rango valido para realizar  las acciones validas.
                if (Math.abs(dderecha - dizquierda) < 105 && dcentral > 200) {

                    /* Mira si hay aceite y si puede colisionar con este procede a 
                       girar por el lado donde sea mas facil esquivarla. */
                    if (estado.fuel < 1000) {
                        /* Si tiene poca gasolina entonces si encuentra una cercana 
                       a el procede a girar para recojerla. */
                        int gasolina = DetectarRecursos(RECURSOS);
                        if (gasolina > 0) {
                            if (dderecha > 80 && gasolina == 2) {
                                dreta();
                            } else if (dizquierda > 80 && gasolina == 3) {
                                esquerra();
                            } else {
                                if (dderecha < dizquierda) {
                                    esquerra();
                                } else {
                                    dreta();
                                }
                            }
                        }
//                } else if (Math.abs(dderecha - dizquierda) < 20) {
//                    if (dderecha < dizquierda) {
//                        esquerra();
//                    } else {
//                        dreta();
//                    }
                    } else {
                        // Si no hay accion disponible entoces no gires.
                        noGiris();
                    }
                    // Si hay aceite y ya no hay visibles pone un aceite en la pista.
                    if (hay_aceite && aceite < 0) {
                        posaOli();
                        hay_aceite = false;
                    }
                } else {
                    if (dderecha > dizquierda) {
                        dreta();
                    } else {
                        esquerra();
                    }
                }
            }

            if (ocentral == COCHE && tiempo_disparo == 0) {
                dispara();
                tiempo_disparo = 50;
            } else {
                if (tiempo_disparo > 0) {
                    tiempo_disparo--;
                }
            }

            /* Para frenar, mira que este en la 5 marcha y la distancia 
                   hacia la pared, sino acelera. */
            if (dcentral < 160 && estado.marxa == 5) {
                endavant(4);
            } else {
                endavant(marcha(estado.velocitat));
            }

        }

    }

    /**
     * Comportamiento si esta en un entorno de contrareloj.
     */
    public void contrareloj() {

        // Si no hace falta correccion entonces procede.
        if (!correccion()) {

            /* Si el coche esta en una posicion adecuada entonces comprueba si 
               tambien respeta las limitaciones de dentro. si es asi no realiza 
               nada. */
            if (dcentral > 200 && Math.abs(dderecha - dizquierda) < 75) {
                if (Math.abs(dderecha - dizquierda) < 35) {
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

            /* Para tener gasolina al acabar la carrera se comienza con una 
               velocidad menor hasta que tenga X combustible en donde comenzara 
               a correr. */
            if (estado.fuel < 9000) {
                endavant(marcha(estado.velocitat));
            } else {
                endavant(Math.min(marcha(estado.velocitat), 4));
            }
        }

    }

    /**
     * Metodo para obtener el sector donde lo ve el agente del recurso del tipo
     * indicado por parametro que este proximo al coche.
     *
     * @param objeto
     * @return
     */
    public int DetectarRecursos(int objeto) {

        // Obtiene el angulo y la posicion del coche y ajusta las coordenadas.
        double angulo = estado.angle;
        int posCocheX = estado.posicio.x;
        int posCocheY = estado.posicio.y;
        double cx = posCocheX * Math.cos(angulo) + posCocheY * Math.sin(angulo);
        double cy = posCocheY * Math.cos(angulo) - posCocheX * Math.sin(angulo);

        // Recorriodo del array de objetos.
        for (int i = 0; i < (int) estado.numObjectes; i++) {

            //Comprobamos si el objeto es del tipo indicado.
            if (estado.objectes[i].tipus == objeto) {

                //Miramos el sector donde se encuentra el objeto.
                if (estado.objectes[i].sector == 2 || estado.objectes[i].sector == 3) {

                    //Calculo de la posicion del objeto segun el angulo del coche.
                    int posObjX = estado.objectes[i].posicio.x;
                    int posObjY = estado.objectes[i].posicio.y;
                    double ox = posObjX * Math.cos(angulo) + posObjY * Math.sin(angulo);
                    double oy = posObjY * Math.cos(angulo) - posObjX * Math.sin(angulo);

                    // Calcula la distancia del coche al objeto.
                    double zonaX = Math.abs(cx - ox);
                    double zonaY = Math.abs(cy - oy);

                    // Si esta en la zona delante del coche entonces devuelve el sector.
                    if (zonaX < 150 && zonaY < 60) {
                        return estado.objectes[i].sector;
                    }

                }
            }
        }

        // Si no hay objeto que este cerca del tipo indicado entonces -1.
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
            if (zonaX < 200 && zonaY < 100) {
                System.out.println(estado.sector[1]);
                return estado.sector[1];
            }

        }

        return -1;
    }

    /**
     * Si se da el caso de colision o contradireccion procede a realizar las
     * acciones para solventar este estado.
     *
     * @return
     */
    public boolean correccion() {
        if (tiempo_correccion == 0) {
            if (estado.enCollisio) {
                if (oderecha == COCHE && ocentral == COCHE) {
                    System.out.println("derecha");
                    if (intentos_correccion == 0) {
                        esquerra();
                        intentos_correccion = 1;
                    } else {
                        dreta();
                        intentos_correccion = 0;
                    }
                    enrere(1);
                    return true;
                } else if (oizquierda == COCHE && ocentral == COCHE) {
                    System.out.println("izquierda");
                    if (intentos_correccion == 0) {
                        dreta();
                        intentos_correccion = 1;
                    } else {
                        esquerra();
                        intentos_correccion = 0;
                    }
                    enrere(1);
                    return true;
                } else if (ocentral == COCHE) {
                    System.out.println("Delante");
                    if (intentos_correccion == 0) {
                        endavant(1);
                        intentos_correccion = 1;
                    } else {
                        enrere(1);
                        intentos_correccion = 0;
                    }
                    tiempo_correccion = 5;
                    return true;
                } else if (oderecha == COCHE && oizquierda == COCHE) {
                    endavant(1);
                    return true;
                } else {
                    accion_correccion();

                    tiempo_correccion = (int) Math.floor(10 * Math.random());
                    return true;
                }
            } else if (estado.contraDireccio) {
                if (!contra) {
                    contra = true;
                    giro = dderecha > dizquierda;
                }
                endavant(1);
                if (giro) {
                    dreta();
                } else {
                    esquerra();
                }
                return true;

            } else {
                contra = false;
                intentos_correccion = 0;
                return false;
            }
        } else {
            tiempo_correccion--;
            return true;
        }
    }

    public void accion_correccion() {
        switch (intentos_correccion) {
            case 0:
                System.out.println("0");
                noGiris();
                endavant(1);
                intentos_correccion = 1;
                break;
            case 1:
                System.out.println("1");
                enrere(1);
                noGiris();
                intentos_correccion = 2;
                break;
            case 2:
                System.out.println("2");
                endavant(1);
                if (dderecha > dizquierda) {
                    dreta();
                } else {
                    esquerra();
                }
                intentos_correccion = 3;
                break;
            case 3:
                System.out.println("3");
                enrere(1);
                if (dderecha > dizquierda) {
                    dreta();
                } else {
                    esquerra();
                }
                intentos_correccion = 4;
                break;
            case 4:
                System.out.println("4");
                atura();
                if (dderecha > dizquierda) {
                    dreta();
                } else {
                    esquerra();
                }
                intentos_correccion = 0;
                break;
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