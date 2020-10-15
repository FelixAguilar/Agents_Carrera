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

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    static final int COTXE = 1;
    
    static final int GIRO = 9;
    static final int CORRECCION = 3;
    
    //int VELOCITATTOPE = 5;
    int VELOCITATFRE = 3;

    Estat estat;
    int espera = 0;

    double desquerra, ddreta, dcentral;


    public Cotxo1(Agents pare) {
        super(pare, "Rapido", "imatges/Coche_Koenigsegg_One.png");
    }

    @Override
    public void inicia() {
        setAngleVisors(40);
        setDistanciaVisors(350);
        setVelocitatAngular(9);
    }

    @Override
    public void avaluaComportament() {

        estat = estatCombat();  // Recuperam la informació actualitzada de l'entorn

        // Si volem repetir una determinada acció durant varies interaccions
        // ho hem de gestionar amb una variable (per exemple "espera") que faci
        // l'acció que volem durant el temps que necessitem
        
        if (espera > 0) {  // no facis res, continua amb el que estaves fent
            espera--;
            return;
        } else {
            
            ddreta = estat.distanciaVisors[DRETA];
            desquerra = estat.distanciaVisors[ESQUERRA];
            dcentral = estat.distanciaVisors[CENTRAL];
                  
            if (ddreta == desquerra && dcentral > 180){
                noGiris();
                endavant(marcha(estat));
            }
            else{
                if (dcentral > 200){
                    setVelocitatAngular(CORRECCION);
                    endavant(marcha(estat));
                    if (ddreta > desquerra){
                        dreta();
                    }else{
                        esquerra();
                    }
                }else{
                    setVelocitatAngular(GIRO);
                    endavant(frenar(dcentral));
                    if (ddreta > desquerra){
                        dreta();
                    }else{
                        esquerra();
                    }
                }   
            }
            
        }
    }
    
    public int  frenar (double distancia)
    {
        if (distancia < 40)
        {
            return 1;
        }
        else if (distancia < 60)
        {
            return 2;
        }
        else if (distancia < 80)
        {
            return 3;
        }
        else if (distancia < 120)
        {
            return 4;
        }
        else{
            return 5;
        }
    }

    public int  marcha (Estat estado)
    {
        if (estado.velocitat < 110)
        {
            return 1;
        }
        else if (estado.velocitat < 200)
        {
            return 2;
        }
        else if (estado.velocitat < 250)
        {
            return 3;
        }
        else if (estado.velocitat < 300)
        {
            return 4;
        }
        else
        {
            return 5;
        }            
   }
}

