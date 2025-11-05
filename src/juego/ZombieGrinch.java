package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class ZombieGrinch {
	public static int length;
	double x, y;
    double velocidad;
    int vida;
    double escala;
    Image imagen;
    Entorno entorno;

    public ZombieGrinch(double x, double y, Entorno entorno) {
        this.x = x;
        this.y = y;
        this.entorno = entorno;
        this.velocidad = 0.1;
        this.vida = 2;
        this.escala = 0.14; // 🔹 Ajustá este valor para cambiar tamaño
        this.imagen = Herramientas.cargarImagen("zombieGrinch.png");
    }

    public void mover() {
        this.x -= velocidad;
    }

    public void dibujar() {
        entorno.dibujarImagen(imagen, x, y, 0, escala);
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public void recibirGolpe() {
        vida--;
    }

    public boolean colisionaCon(Proyectil p) {
        return Math.abs(this.x - p.x) < 30 && Math.abs(this.y - p.y) < 40;
    }

    public boolean llegoARegalo() {
        return this.x < 100;
    }

	public void recibirDanio() {
		// TODO Auto-generated method stub
		
	}
}