
public class Ejercicio6 {
	public static void main(String[] args) {
		
		//Ejercicio 6
		long poblacion_actual = 7309784505L;
		long poblacion_creciente = 24807909L;
		long poblacion_total;

		
		poblacion_total = poblacion_actual;
		for (int i = 1; i < 6;i++){
			poblacion_total += poblacion_creciente;
			System.out.println("La poblacion en "+ i + " años es:"+ poblacion_total);
		}
	}
}
