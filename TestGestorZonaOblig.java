package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.gestoresplazas.huecos.Plaza;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;
import modelo.vehiculos.Vehiculo;

public class TestGestorZonaOblig {
	
	@Rule //Se establece un time out general para todos los tests. Se debe comentar esta línea y la de abajo para depurar
    public TestRule  globalTimeout = new DisableOnDebug(Timeout.millis(100)); // 100 milisegundos máximos por test	

	private GestorZona gestor;

	@Before
	public void setUp() {
		gestor = new GestorZona(0, 1, 3, 1.0);
	}

	/**
	 * Comprueba el constructor de GestorZona
	 */
	@Test
	public void testConst() {		

		assertEquals("No se ha inicializado correctamente el atributo de plazas", "[(0-null), (1-null), (2-null)]", gestor.getPlazas());

		assertEquals("La lista de espera no está vacía", "[]", gestor.getListaEspera());

		assertEquals("La lista de huecos reservados no está vacía", "[]", gestor.getEstadoHuecosReservados());

		assertEquals("No se ha inicializado correctamente el gestor de huecos", "[[(00:00, 03:00, (0-null)),\n"
				+ "(00:00, 03:00, (1-null)),\n"
				+ "(00:00, 03:00, (2-null))]]", gestor.getEstadoHuecosLibres());		
	}

	/**
	 * Comprueba el método reservarHueco() para el caso de que haya
	 * un hueco disponible en el intervalo solicitado
	 */
	@Test
	public void testReservarHuecoExistente() {
		//Primera reserva
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);

		Hueco hueco = gestor.reservarHueco(tI, tF);
		assertEquals("La hora de inicio del hueco reservado no es correcta", tI, hueco.gettI());
		assertEquals("La hora de final del hueco reservado no es correcta", tF, hueco.gettF());

		assertTrue("No se ha guardado el hueco reservado en la lista de huecos reservados", 
				gestor.getEstadoHuecosReservados().contains("01:00, 02:30"));

		assertEquals("No se ha actualizado correctamente el gestor de huecos", "[[(00:00, 01:00, (0-null)),\n"
				+ "(00:00, 03:00, (1-null)),\n"
				+ "(00:00, 03:00, (2-null))],\n"
				+ "[(02:30, 03:00, (0-null))]]", gestor.getEstadoHuecosLibres());
				
		//Segunda reserva
		LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 0, 0);
		LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 0, 10);

		hueco = gestor.reservarHueco(tI1, tF1);
		assertEquals("La hora de inicio del hueco reservado no es correcta", tI1, hueco.gettI());
		assertEquals("La hora de final del hueco reservado no es correcta", tF1, hueco.gettF());

		assertTrue("No se ha guardado el hueco reservado en la lista de huecos reservados", 
				gestor.getEstadoHuecosReservados().contains("00:00, 00:10"));

	}

	/**
	 * Comprueba el método reservarHueco() para el caso de que NO haya
	 * un hueco disponible en el intervalo solicitado
	 */
	@Test
	public void testReservarHuecoNoExistente() {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);

		gestor.reservarHueco(tI, tF);
		gestor.reservarHueco(tI, tF);
		gestor.reservarHueco(tI, tF);
		
		String listaEspera = "[]";
		String huecosReservados = gestor.getEstadoHuecosReservados();
		String huecosLibres = gestor.getEstadoHuecosLibres();
		
		assertEquals("No debería encontrar un hueco disponible", null, gestor.reservarHueco(tI, tF));
		
		assertEquals("No se debe modificar la lista de espera", listaEspera, gestor.getListaEspera());
		assertEquals("No se debe modificar la lista de huecos reservados", huecosReservados, gestor.getEstadoHuecosReservados());
		assertEquals("No se debe modificar la lista de huecos libres", huecosLibres, gestor.getEstadoHuecosLibres());
	}
	
	/**
	 * Comprueba el método existeHuecoReservado() tanto si existe como si no
	 */
	@Test
	public void testExisteHuecoReservado() {
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);

		Hueco hueco = gestor.reservarHueco(tI, tF);
		
		assertTrue("No se ha encontrado el hueco reservado", gestor.existeHuecoReservado(hueco));
		
		LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 1, 30);
		
		hueco = new Hueco(tI1, tF1, new Plaza(1));
		
		assertFalse("No se debería haber encontrado este hueco como reservado", gestor.existeHuecoReservado(hueco));

	}
	
	/**
	 * Comprueba que el método meterEnListaEspera() actualiza correctamente la lista de espera de solicitudes
	 */
	@Test
	public void testMeterListaEspera() {
		
		// se añade la primera solicitud
		LocalDateTime tI = LocalDateTime.of(2021, 10, 5, 1, 0);
		LocalDateTime tF = LocalDateTime.of(2021, 10, 5, 2, 30);
		Vehiculo car = new Vehiculo("7883CTB");
		
		SolicitudReservaAnticipada solicitud = new SolicitudReservaAnticipada(0, 1, tI, tF, car);
		gestor.meterEnListaEspera(solicitud);
		assertTrue("La lista de espera no contiene la solicitud dada", gestor.getListaEspera().contains("(Sol:0 1 01:00 02:30 7883CTB)"));
		
		// se añade una segunda solicitud
		LocalDateTime tI1 = LocalDateTime.of(2021, 10, 5, 2, 0);
		LocalDateTime tF1 = LocalDateTime.of(2021, 10, 5, 3, 0);
		Vehiculo car1 = new Vehiculo("1233TWB");
		
		SolicitudReservaAnticipada solicitud1 = new SolicitudReservaAnticipada(1, 0, tI1, tF1, car1);
		gestor.meterEnListaEspera(solicitud1);
		
		assertTrue("La lista de espera no contiene la primera solicitud dada", gestor.getListaEspera().contains("(Sol:0 1 01:00 02:30 7883CTB)"));
		assertTrue("La lista de espera no contiene la segunda solicitud dada", gestor.getListaEspera().contains("(Sol:1 0 02:00 03:00 1233TWB)"));

	}

}
