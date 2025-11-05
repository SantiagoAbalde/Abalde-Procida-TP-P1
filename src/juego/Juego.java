package juego;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import entorno.Entorno;
import entorno.InterfaceJuego;
import entorno.Herramientas;

public class Juego extends InterfaceJuego {
    private Entorno entorno;
    Cuadricula cua;
    Regalo[] regalos;
    Planta[] plantas;
    ZombieGrinch[] zombis;

    // --- Control general del juego ---
    int totalZombis = 50;
    int zombisEliminados = 0;
    int zombisActivos = 0;
    boolean juegoTerminado = false;
    boolean gano = false;

    // --- Sistema de recarga del banco de plantas ---
    boolean recargandoPlanta = false;
    int tiempoRecargaPlanta = 300;
    int contadorRecargaPlanta = 0;

    // --- Imágenes del banco ---
    Image imagenPlantaNormal;
    Image imagenPlantaCooldown;

    public Juego() {
        this.entorno = new Entorno(this, "La invasión del Grinch Zombie", 800, 600);
        cua = new Cuadricula(50, 150, entorno);

        regalos = new Regalo[5];
        for (int i = 0; i < 5; i++) {
            regalos[i] = new Regalo(50, 150 + i * 100, entorno);
        }

        plantas = new Planta[15];
        plantas[0] = new Planta(50, 50, entorno);

        zombis = new ZombieGrinch[15];

        imagenPlantaNormal = Herramientas.cargarImagen("planta.png");
        imagenPlantaCooldown = Herramientas.cargarImagen("plantaCooldown.png");

        this.entorno.iniciar();
    }

    public void tick() {
        if (juegoTerminado) {
            entorno.cambiarFont("Arial", 40, Color.WHITE);
            if (gano)
                entorno.escribirTexto("¡GANASTE!", 300, 300);
            else
                entorno.escribirTexto("¡LOS ZOMBIES GANARON!", 220, 300);
            return;
        }

        // --- DIBUJAR ESCENARIO ---
        cua.dibujar();
        for (Regalo r : regalos) r.dibujar();

        // --- DIBUJAR TODAS LAS PLANTAS ---
        for (Planta p : plantas) {
            if (p != null) {
                p.dibujar();
                p.actualizar();
            }
        }

        // --- SELECCIONAR PLANTA ---
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

        // --- ARRASTRAR SOLO PLANTAS NO PLANTADAS ---
        if (entorno.estaPresionado(entorno.BOTON_IZQUIERDO)) {
            for (Planta p : plantas) {
                if (p != null && p.seleccionada && !p.plantada) {
                    p.arrastrar(entorno.mouseX(), entorno.mouseY());
                }
            }
        }

        // --- SOLTAR PLANTA ---
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
        
     // --- MOVER PLANTA SELECCIONADA CON W/A/S/D ---
        for (Planta p : plantas) {
            if (p != null && p.plantada && p.seleccionada) {
                // Obtener la posición actual en la cuadrícula
                Point celda = cua.cercano(p.x, p.y);
                int col = celda.x;
                int fila = celda.y;

                // --- W: mover arriba ---
                if (entorno.sePresiono('W')) {
                    if (fila > 0 && !cua.ocupado[col][fila - 1]) {
                        p.y -= 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col][fila - 1] = true;
                    }
                }

                // --- S: mover abajo ---
                if (entorno.sePresiono('S')) {
                    if (fila < 4 && !cua.ocupado[col][fila + 1]) {
                        p.y += 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col][fila + 1] = true;
                    }
                }

                // --- A: mover izquierda ---
                if (entorno.sePresiono('A')) {
                    if (col > 0 && !cua.ocupado[col - 1][fila]) {
                        p.x -= 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col - 1][fila] = true;
                    }
                }

                // --- D: mover derecha ---
                if (entorno.sePresiono('D')) {
                    if (col < 7 && !cua.ocupado[col + 1][fila]) {
                        p.x += 100;
                        cua.ocupado[col][fila] = false;
                        cua.ocupado[col + 1][fila] = true;
                    }
                }
            }
        }


        // --- COOLDOWN DEL BANCO ---
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

        // --- CREAR NUEVAS PLANTAS ---
        if (!plantasNoPlantadas(plantas) && !recargandoPlanta) {
            crearPlanta(plantas);
        }

        // --- ZOMBIES ---
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

        for (int i = 0; i < zombis.length; i++) {
            ZombieGrinch z = zombis[i];
            if (z != null) {
                z.mover();
                z.dibujar();

                // Colisión con proyectiles
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

                // Colisión ZOMBIE vs PLANTA
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

        if (zombisEliminados >= totalZombis) {
            juegoTerminado = true;
            gano = true;
        }

        entorno.cambiarFont("Arial", 18, Color.WHITE);
        entorno.escribirTexto("Zombies eliminados: " + zombisEliminados, 550, 30);
        entorno.escribirTexto("Zombies restantes: " + (totalZombis - zombisEliminados), 550, 50);
    }

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
