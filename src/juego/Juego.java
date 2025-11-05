package juego;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import entorno.Entorno;
import entorno.InterfaceJuego;
import entorno.Herramientas;

public class Juego extends InterfaceJuego {
    private Entorno entorno;
    Cuadricula cua; //objeto de tipo cuadricula
    Regalo[] regalos; //arreglo de objetos de tipo regalo
    Planta[] plantas; //arreglo de objetos de tipo planta
    ZombieGrinch[] zombis; //arreglo de objetos de tipo zombiegrinch

    //Control de juego//
    int totalZombis = 50; //cantidad de zombies a eliminar
    int zombisEliminados = 0; //contador de la cantidad de zombies eliminados
    int zombisActivos = 0; //cantidad de zombies en mapa
    boolean juegoTerminado = false; //variable booleana que controla el estado del juego
    boolean gano = false; //variable booleana que controla la condicion de victoria

    //Variables utilizadas para el cooldown//
    boolean recargandoPlanta = false; //variable booleana para saber si la planta esta en modo de recarga
    int tiempoRecargaPlanta = 300; //indica cuanto dura el cooldown, a 60 ticks/segundo (300/60 = 5 seg)
    int contadorRecargaPlanta = 0; //contador de los ticks

    //imagenes de las plantas//
    Image imagenPlantaNormal; //la planta sin recarga
    Image imagenPlantaCooldown; //planta recargando, bloqueada

    public Juego() {
        this.entorno = new Entorno(this, "La invasión del Grinch Zombie", 800, 600); //Crea el entorno con titulo y resolucion
        cua = new Cuadricula(50, 150, entorno); //Se crea la cuadricula en la posicion (50,150)

        regalos = new Regalo[5];
        for (int i = 0; i < 5; i++) {
            regalos[i] = new Regalo(50, 150 + i * 100, entorno); //Se crean los regalos verticalmente sobre el eje Y separados por 100 pixeles
        }

        plantas = new Planta[15];
        plantas[0] = new Planta(50, 50, entorno); //inicializa las plantas, tenemos 15 disponibles para usar en el mapa y se ubica la primera en (50/50)

        zombis = new ZombieGrinch[15]; //cantidad de zombies activos = 15

        imagenPlantaNormal = Herramientas.cargarImagen("planta.png");
        imagenPlantaCooldown = Herramientas.cargarImagen("plantaCooldown.png"); //Las imagenes necesarias para las plantas

        this.entorno.iniciar(); //comienza el ciclo de juego
    }

    public void tick() {
    	
    	//Control de mfinalizacion de juego///////////////////////////////////
        if (juegoTerminado) {
            entorno.cambiarFont("Arial", 40, Color.WHITE);
            if (gano)
                entorno.escribirTexto("¡GANASTE!", 300, 300);
            else
                entorno.escribirTexto("¡LOS ZOMBIES GANARON!", 220, 300);
            return;
        }
        //////////////////////////////////////////////////////////////////////
        
        cua.dibujar(); //Dibujo de cuadricula
        for (Regalo r : regalos) r.dibujar(); //Dibujo de los regalos

        //Dibujo de las plantas y actualizacion de su estado////////////////////
        for (Planta p : plantas) {
            if (p != null) {
                p.dibujar();
                p.actualizar();
            }
        }

        //SELECCION DE PLANTAS//////////////////////////////////////////////////
        if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            boolean clicEnPlanta = false;

            for (Planta p : plantas) {
                if (p != null && p.encima(entorno.mouseX(), entorno.mouseY())) {
                    p.seleccionada = true;
                    clicEnPlanta = true;
                } else if (p != null) {
                    p.seleccionada = false;
                }
            }

            // Si no se hizo clic sobre ninguna planta, se deseleccionan todas
            if (!clicEnPlanta) {
                for (Planta p : plantas) {
                    if (p != null) p.seleccionada = false;
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////

        // --- ARRASTRAR SOLO PLANTAS NO PLANTADAS ---
        if (entorno.estaPresionado(entorno.BOTON_IZQUIERDO)) {
            for (Planta p : plantas) {
                if (p != null && p.seleccionada && !p.plantada) {
                    p.arrastrar(entorno.mouseX(), entorno.mouseY());
                }
            }
        }
        ///////////////////////////////////////////////////////////////////////

        //Soltar planta
        if (entorno.seLevantoBoton(entorno.BOTON_IZQUIERDO)) {
            for (Planta p : plantas) {
                if (p != null && p.seleccionada && !p.plantada) {
                    if (entorno.mouseY() < 70) {
                        p.arrastrar(50, 50);
                    } else {
                        Point celda = cua.cercano(entorno.mouseX(), entorno.mouseY());
                        int col = celda.x;
                        int fila = celda.y;

                        if (!cua.ocupado[col][fila]) {
                            p.arrastrar(cua.corX[col], cua.corY[fila]);
                            cua.ocupado[col][fila] = true;
                            p.plantada = true;
                            recargandoPlanta = true;
                            contadorRecargaPlanta = 0;
                        } else {
                            // vuelve al banco si la celda está ocupada
                            p.arrastrar(50, 50);
                        }
                    }
                    p.seleccionada = false;
                }
            }
        }
        ////////////////////////////////////////////////////////////////////////////
        
     //Mover planta seleccionada con (W/A/S/D)/////////////////////////////////////
        for (Planta p : plantas) {
            if (p != null && p.plantada && p.seleccionada) {
                // Obtener la posición actual en la cuadrícula
                Point celda = cua.cercano(p.x, p.y);
                int col = celda.x;
                int fila = celda.y;

                //W = arriba
                if (entorno.sePresiono('W')) {
                    if (fila > 0 && !cua.ocupado[col][fila - 1]) {
                        p.y -= 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col][fila - 1] = true;
                    }
                }

                // S = abajo
                if (entorno.sePresiono('S')) {
                    if (fila < 4 && !cua.ocupado[col][fila + 1]) {
                        p.y += 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col][fila + 1] = true;
                    }
                }

                // A = izquierda
                if (entorno.sePresiono('A')) {
                    if (col > 0 && !cua.ocupado[col - 1][fila]) {
                        p.x -= 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col - 1][fila] = true;
                    }
                }

                // D = derecha
                if (entorno.sePresiono('D')) {
                    if (col < 7 && !cua.ocupado[col + 1][fila]) {
                        p.x += 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col + 1][fila] = true;
                    }
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////


        //Cooldown de las plantas////////////////////////////////////////////////////////////////
        if (recargandoPlanta) {
            entorno.dibujarImagen(imagenPlantaCooldown, 50, 50, 0, 1);
            entorno.cambiarFont("Arial", 14, Color.WHITE);
            entorno.escribirTexto("Recargando...", 90, 55);

            double progreso = (double) contadorRecargaPlanta / tiempoRecargaPlanta;
            int ancho = 60, alto = 8;
            int xBarra = 50 - ancho / 2, yBarra = 80;

            entorno.dibujarRectangulo(xBarra + ancho / 2, yBarra, ancho, alto, 0, Color.DARK_GRAY);
            entorno.dibujarRectangulo(
                xBarra + (ancho * progreso) / 2,
                yBarra,
                ancho * progreso,
                alto,
                0,
                Color.GREEN
            );

            contadorRecargaPlanta++;
            if (contadorRecargaPlanta >= tiempoRecargaPlanta) recargandoPlanta = false;
        } else {
            entorno.dibujarImagen(imagenPlantaNormal, 50, 50, 0, 1);
        }
        //////////////////////////////////////////////////////////////////////////////////////////

        //Creacion nuevas plantas/////////////////////////////////////////////////////////////////
        if (!plantasNoPlantadas(plantas) && !recargandoPlanta) {
            crearPlanta(plantas);
        }
        //////////////////////////////////////////////////////////////////////////////////////////

        //Generacion de zombies//////////////////////////////////////////////////////////////////
        if (zombisActivos < 15 && zombisEliminados + zombisActivos < totalZombis) {
            if (Math.random() < 0.02) {
                for (int i = 0; i < zombis.length; i++) {
                    if (zombis[i] == null) {
                        double y = 150 + (int) (Math.random() * 5) * 100;
                        zombis[i] = new ZombieGrinch(850, y, entorno);
                        zombisActivos++;
                        break;
                    }
                }
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////
        
        //Dibujo y comportamiento de los zombies///////////////////////////////////////////////
        for (int i = 0; i < zombis.length; i++) {
            ZombieGrinch z = zombis[i];
            if (z != null) {
                z.mover();
                z.dibujar();

                //Colision con proyectil
                for (Planta p : plantas) {
                    if (p != null && p.proyectiles != null) {
                        for (int j = 0; j < p.proyectiles.length; j++) {
                            Proyectil pr = p.proyectiles[j];
                            if (pr != null && pr.colisionaCon(z)) {
                                z.recibirGolpe();
                                p.proyectiles[j] = null;
                                if (!z.estaVivo()) {
                                    zombis[i] = null;
                                    zombisEliminados++;
                                    zombisActivos--;
                                    break;
                                }
                            }
                        }
                    }
                }

                //Colision con planta
                for (int j = 0; j < plantas.length; j++) {
                    if (plantas[j] != null && colisionZombiePlanta(z, plantas[j])) {
                        Point celda = cua.cercano(plantas[j].x, plantas[j].y);
                        cua.ocupado[celda.x][celda.y] = false;
                        plantas[j] = null;
                        break;
                    }
                }

                if (z.llegoARegalo()) {
                    juegoTerminado = true;
                    gano = false;
                    return;
                }
            }
        }
        //////////////////////////////////////////////////////////////////////////////////
        
        //Control de condicion de victoria o derrota/////////////////////////////////////
        if (zombisEliminados >= totalZombis) {
            juegoTerminado = true;
            gano = true;
        }
        ////////////////////////////////////////////////////////////////////////////////
        
        //Progreso del juego///////////////////////////////////////////////////////////
        entorno.cambiarFont("Arial", 18, Color.WHITE);
        entorno.escribirTexto("Zombies eliminados: " + zombisEliminados, 550, 30);
        entorno.escribirTexto("Zombies restantes: " + (totalZombis - zombisEliminados), 550, 50);
    }
    //Final de tick()/////////////////////////////////////////////////////////////////////////
    
    

    public boolean plantasNoPlantadas(Planta[] pl) {
        for (Planta p : pl) {
            if (p != null && !p.plantada)
                return true;
        }
        return false;
    }

    private void crearPlanta(Planta[] pl) {
        for (int i = 0; i < pl.length; i++) {
            if (pl[i] == null) {
                pl[i] = new Planta(50, 50, entorno);
                break;
            }
        }
    }

    private boolean colisionZombiePlanta(ZombieGrinch z, Planta p) {
        if (z == null || p == null) return false;
        double dx = z.x - p.x;
        double dy = z.y - p.y;
        return Math.sqrt(dx * dx + dy * dy) < 30;
    }

    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}
