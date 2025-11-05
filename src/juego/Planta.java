package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Planta {
    double x, y, escala;
    Image imagen, imagenSeleccionada;
    Entorno e;
    boolean seleccionada;
    boolean plantada;

    // --- Sistema de disparo ---
    Proyectil[] proyectiles;
    int tiempoEntreDisparos = 80; // frames entre disparos
    int contadorDisparo = 0;

    // --- Sistema de recarga ---
    boolean enRecarga;        // true = la planta acaba de colocarse
    int tiempoRecarga = 300;  // ticks de recarga (~5 segundos)
    int contadorRecarga = 0;  // contador interno

    public Planta(double x, double y, Entorno e) {
        this.x = x;
        this.y = y;
        this.e = e;
        this.escala = 1;
        this.imagen = Herramientas.cargarImagen("planta.png");
        this.imagenSeleccionada = Herramientas.cargarImagen("plantaSeleccionada.png");
        this.seleccionada = false;
        this.plantada = false;
        this.enRecarga = false;

        // Hasta 10 proyectiles activos por planta
        this.proyectiles = new Proyectil[10];
    }

    public void dibujar() {
        // 🔹 Mostrar distinta imagen si está en recarga
        if (enRecarga) {
            e.dibujarImagen(imagen, x, y, 0, escala * 0.8); // un poco más chica o tenue
        } else {
            if (seleccionada) {
                e.dibujarImagen(imagenSeleccionada, x, y, 0, escala);
            }
            e.dibujarImagen(imagen, x, y, 0, escala);
        }

        // Dibujar proyectiles activos
        for (Proyectil p : proyectiles) {
            if (p != null) {
                p.dibujar();
            }
        }
    }

    public void actualizar() {
        // --- Control de recarga ---
        if (enRecarga) {
            contadorRecarga++;
            if (contadorRecarga >= tiempoRecarga) {
                enRecarga = false; // ya se puede volver a usar
            }
        }

        // --- Control de disparos ---
        if (plantada && !enRecarga) {
            contadorDisparo++;
            if (contadorDisparo >= tiempoEntreDisparos) {
                disparar();
                contadorDisparo = 0;
            }
        }

        // --- Actualizar proyectiles ---
        for (int i = 0; i < proyectiles.length; i++) {
            if (proyectiles[i] != null) {
                proyectiles[i].mover();
                // Eliminar si sale de pantalla
                if (proyectiles[i].x > 800) {
                    proyectiles[i] = null;
                }
            }
        }
    }

    private void disparar() {
        for (int i = 0; i < proyectiles.length; i++) {
            if (proyectiles[i] == null) {
                proyectiles[i] = new Proyectil(this.x + 30, this.y, this.e);
                break;
            }
        }
    }

    // --- Cuando la planta se coloca en el suelo ---
    public void plantar() {
        this.plantada = true;
        this.enRecarga = true;
        this.contadorRecarga = 0;
    }

    // --- Herramientas auxiliares ---
    public double distancia(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public boolean encima(double xM, double yM) {
        return distancia(xM, yM, this.x, this.y) < 20;
    }

    public void arrastrar(double xM, double yM) {
        this.x = xM;
        this.y = yM;
    }

    // Detectar colisión con un zombie
    public boolean colisionaCon(ZombieGrinch z) {
        return distancia(this.x, this.y, z.x, z.y) < 40;
    }

    public Proyectil getProyectil() {
        return null;
    }
}
