package modelo.gestoresplazas;

import anotacion.Programacion2;
import list.IList;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

//TO-DO alumno obligatorio
@Programacion2 (
nombreAutor1 = "nombre",
apellidoAutor1 = "apellido1 apellido2",
emailUPMAutor1 = "usr@alumnos.upm.es",
nombreAutor2 = "",
apellidoAutor2 = "",
emailUPMAutor2 = ""
)




public class GestorLocalidad {
	
	private GestorZona[][] gestoresZonas;

	public GestorLocalidad(int[][] plazas, double[][] precios) {
		
		for(int i=0;i<plazas.length;i++) {
			gestoresZonas=new GestorZona[plazas.length][plazas[i].length];
			
			for(int j=0;j<plazas[i].length;j++) {
				gestoresZonas[i][j]=new GestorZona(i,j,plazas[i][j],precios[i][j]);
			}
		}
	}
	
	public int getRadioMaxI() {
		return gestoresZonas.length-1;
	}
	
	public int getRadioMaxJ() {
		return gestoresZonas[0].length;
	}
	
	public boolean existeZona(int i, int j) {
		//TO-DO
		return false;
	}

	public boolean existeHuecoReservado(Hueco hueco, int i, int j) {
	
		return false;
	}

	public GestorZona getGestorZona(int i, int j) {
		return gestoresZonas[i][j];
	}
	
	//TO-DO alumno opcional
	
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera(int i, int j) {
		//TO-DO
		return null;
	}
	
}
