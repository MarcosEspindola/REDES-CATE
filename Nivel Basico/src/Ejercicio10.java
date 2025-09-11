import java.util.Scanner;

public class Ejercicio10 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Ingresar valor del prestamo: ");
		int prestamo = sc.nextInt();
		
		System.out.print("Ingresar porcentaje de aumento por mes: ");
		int porcentaje = sc.nextInt();
		
		System.out.print("Ingresar valor que paga al mes: ");
		int pago = sc.nextInt();
		
		sc.close();
		
		int interes = (prestamo * porcentaje) / 100;
		
		System.out.println("Prestamo: " + prestamo);
		System.out.println("interes: " + interes);
		System.out.println("pago: " + pago);
		
		int cont = 0;
		int años = 0;
		int meses;
		
		do {
			prestamo += interes;
			prestamo -= pago;
			cont++;
		}while(prestamo > 0);
		
		meses = cont;
		
		if(cont >= 12) {
			años = cont/12;
			meses = cont % 12;
		}
		System.out.println("Se tardaria " + años + " años y " + meses + " meses en finalizar de pagar el prestamo");
	}

}
