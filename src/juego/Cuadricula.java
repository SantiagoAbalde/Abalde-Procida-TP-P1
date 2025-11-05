package juego;

import java.awt.Image;
import java.awt.Point;

import entorno.Entorno;
import entorno.Herramientas;

public class Cuadricula {
  double x,y, escala;
  Entorno e;
  Image clara, oscura;
  int[] corX, corY;
  boolean[][] ocupado;
   
  public Cuadricula(double x, double y, Entorno e) {
	  this.x=x;
	  this.y=y;
	  this.e=e;
	  clara=Herramientas.cargarImagen("pasto1.png");
	  oscura=Herramientas.cargarImagen("pasto2.png");
	  this.escala=0.52;
	  int[]aux1= {150, 250,350,450,550};
	  int[]aux2= {50,150, 250,350,450,550,650,750};
	  corX=aux2;
	  corY=aux1;
	  this.ocupado = new boolean[8][5];
	  for(int j=0;j< 8;j++) {
			for(int i=0; i < 5; i++) {
				this.ocupado[j][i]= false;
				if(j == 0) {
					this.ocupado[j][i] = true;
					
				}
			}
		}
  }
  public void dibujar() {
	  Image cualquiera;
	  for(int ih=0; ih < 8; ih++) {
		  for(int iv=0; iv < 5;iv++) {
			  if((ih+iv)%2 == 0) {
				  cualquiera=clara;
			  }
			  else {
				  cualquiera=oscura; 
			  }
			  e.dibujarImagen(cualquiera, this.corX[ih], this.corY[iv],0,this.escala);
		  }
	  }
  }
  
  public double distancia(double x1, double y1, double x2, double y2) {
		return Math.sqrt( (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) );
	}
  
  public Point cercano(double xM, double yM) {
		int im =4;
		int jm =7;
		double distanciaM = distancia(xM,yM,corX[jm],corY[im]);
		for(int j=1;j< 8;j++) {
			for(int i=0; i < 5; i++) {
				if(distancia(xM,yM,corX[j],corY[i]) < distanciaM) {
					im = i;
					jm = j;
					distanciaM = distancia(xM,yM,corX[j],corY[i]);
				}
			}
		}
		return new Point(jm,im);
	}
	
	public Point cercanoL(double xM, double yM) {
		int im =0;
		int jm =0;
		for(int j=1;j< 8;j++) {
			for(int i=0; i < 5; i++) {
				if(!ocupado[j][i]) {
					im = i;
					jm = j;
					break;
				}
			}
		}
		double distanciaM = distancia(xM,yM,corX[jm],corY[im]);
		for(int j=1;j< 8;j++) {
			for(int i=0; i < 5; i++) {
				if(distancia(xM,yM,corX[j],corY[i]) < distanciaM && !ocupado[j][i]) {
					im = i;
					jm = j;
					distanciaM = distancia(xM,yM,corX[j],corY[i]);
				}
			}
		}
		return new Point(jm,im);
	}
  
}




