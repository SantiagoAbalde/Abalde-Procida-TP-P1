package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Zombie {
	double x, y, velocidad, ancho, escala;
	boolean estaActivo;
	int vida;
	Image imagenZombie;
	Entorno e;
	
	public Zombie() {
		this.x = x;
		this.y = y;
		this.e = e;
		this.velocidad = 1;
		this.ancho = 5;
		this.vida = 100;
		this.escala = escala;
		this.estaActivo = false;
		this.imagenZombie = Herramientas.cargarImagen(null);
	}
	
	public void dibujar() {
		e.dibujarImagen(imagenZombie, x, y, 0, escala);
	}
	
	public int vidaZombie() { // VIDA DEL ZOMBIE
    	return vida;
    }
    
    
    public void restarVida(int cantidad) { //permite restarle vida al Zombie. Se usa en la clase juego para cuando colisiona el zombie con una planta o proyectil
    	this.vida -=cantidad;
    	if(this.vida <= 0) this.vida = 0 ;
    }
	
	public void seguirRegalo(Regalo regalo, Zombie todos) {
		if (Regalo.getX() < x) {
			x -= velocidad;
		}
		
		// Evita que los zombies se superpongan entre si
	    for (int i = 0; i < todos.length; i++) {
	        Zombie otro = todos[i];

	        if (otro == null) continue; // evita la comparacion entre iguales, es decir compara un zombie con otro distinto al mismo
	            double dx = this.x - otro.x; // Diferencia en X con el otro zombie
	            double dy = this.y - otro.y; // Diferencia en Y con el otro zombie
	            double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia entre ambos

	            if (distancia < 50 && distancia > 0) {
	                // Si estan demasiado cerca, se aleja un poco del otro zombie
	                this.x += dx / distancia * 1.5;
	                this.y += dy / distancia * 1.5;
	            }
	        }
	}
	
	public void colisionConRegalo() {
		
	}
	
	public void colisionConPlanta() {
		
	}
	
	public boolean colisionConProyectil(Proyectil proyectil) {

	     if (proyectil == null) {
	            return false; // No hay proyectil, entonces no hay colisión
	        }


	     double anchoZombie= ancho;
	     double anchoProyectil= proyectil.getAncho();

	     double dx = this.x - proyectil.getX();
	     double dy = this.y - proyectil.getY();

	     double distancia = Math.sqrt(dx * dx + dy * dy);
	 
	     double distanciaColision = (anchoProyectil / 2 + anchoZombie / 2);

	     if(distancia<distanciaColision) {
	         return true;

	     }else {
	         return false;
	     }
	 }
	

}
