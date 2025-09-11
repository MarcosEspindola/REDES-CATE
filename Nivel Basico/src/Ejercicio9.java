import java.util.Scanner;

public class Ejercicio9 {

	public static void main(String[] args) {
		Scanner sc= new Scanner(System.in);
		
		System.out.print("Ingrese un numero: ");
		int num = sc.nextInt();
		
		sc.close();
		
		num = Math.abs(num);

		int valor = 0;
		int variable = 1;
		int variable2 = 2;
		
		System.out.print(variable);
		
		for(int i = 1; i < num; i++) {
			System.out.print(" * " + (i+1));
			valor = variable * variable2;
			variable2++;
			variable = valor;
		}
		System.out.println(" = " + valor);
		System.out.println(" ");
		System.out.println("Factorial de " + num + " es: " + valor);
	}

}
