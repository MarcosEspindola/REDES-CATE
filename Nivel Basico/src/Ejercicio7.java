import java.util.Scanner;

public class Ejercicio7 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Ingrese un valor en pesos: ");
		int pesos = sc.nextInt();
		
		sc.close();
		
		int valor1000 = pesos / 1000;
		int resto1 = pesos % 1000;
		
		int valor100 = resto1 / 100;
		int resto2 = resto1 % 100;
		
		int valor10 = resto2 / 10;
		int resto3 = resto2 % 10;
		
		System.out.println("Necesitas: " + valor1000 + " billetes de 1000 | " + valor100 + " billetes de 100 | " + valor10 + " billetes de 10 | " + resto3 + " monedas de 1");
		
		
	}

}
