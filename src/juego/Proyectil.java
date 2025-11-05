package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Proyectil {
    double x, y;
    double velocidad;
    double escala;
    Image imagen;
    Entorno entorno;
    boolean activo;

    public Proyectil(double x, double y, Entorno entorno) {
        this.x = x;
        this.y = y;
        this.entorno = entorno;
        this.velocidad = 2.4;
        this.escala = 0.5; // 🔹 Ajustá este valor para el tamaño del proyectil
        this.imagen = Herramientas.cargarImagen("BolaFuego.png");
        this.activo = true;
    }

    public void mover() {
        x += velocidad;
        if (x > 800) {
            activo = false;
        }
    }

    public void dibujar() {
        entorno.dibujarImagen(imagen, x, y, 0, escala);
    }

    public boolean colisionaCon(ZombieGrinch z) {
        return Math.abs(this.x - z.x) < 30 && Math.abs(this.y - z.y) < 40;
    }
}
