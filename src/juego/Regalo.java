package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Regalo {
	private static double x;
	double y;
	double escala;
	double angulo;
	Entorno e;
	Image regalo;
	boolean sentido;
	
	public Regalo(double x, double y, Entorno e) {  
		this.x=x;
		this.y=y;
		this.e=e;
		this.angulo=0.0;
		regalo=Herramientas.cargarImagen("regalo.png");
		this.escala=0.45;
		sentido=true;
		if(Math.random() < 0.5) {
			sentido=false;
		}
	}
	
	public void dibujar() {
		this.angulo += (sentido?1:-1)*Math.random()/70.0;
		e.dibujarImagen(regalo, this.x, this.y, this.angulo, this.escala);
	}
	
	public static double getX() {
		return x;
	}
}








