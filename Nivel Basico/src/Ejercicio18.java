import java.util.Scanner;

public class Ejercicio18 {

	public static void main(String[] args) {
		// Crear un programa que pida al usuario un número entero y lo convierta a binario.
		Scanner sc = new Scanner(System.in);
		int N;
		System.out.println("Ingrese un entero:");
		N = sc.nextInt();
		
		System.out.println(Integer.toBinaryString(N));
	}

}
