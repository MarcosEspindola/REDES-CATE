
public class Ejercicio5 {

	public static void main(String[] args) {
		int valor = 0;
		int valor2 = 1;
		int total;
		
		for(int i = 0; i < 20; i++) {
			total = valor + valor2;
			System.out.println(valor2 + " + " + valor + "= " + total);
			valor2 = valor;
			valor = total;
		}
	}
}
