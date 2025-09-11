import java.util.Scanner;

public class Ejercicio2 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		String num;
		do {
		System.out.print("Ingrese un numero de 5 digitos: ");
		num = sc.next();
		
		if(num.length() != 5) 
			System.out.println("El numero ingresado no tiene 5 digitos");
		}while(num.length() != 5);
		
		sc.close();
		
		char caractern;
		int total = 0;
		
		for(int i = 0; i < num.length(); i++) {
			caractern = num.charAt(i);
			total += Character.getNumericValue(caractern);
		}
		
		System.out.println("El resultado de la suma de los digitos es de " + total);
		
	}
}


