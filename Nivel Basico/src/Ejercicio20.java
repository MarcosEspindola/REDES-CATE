import java.util.Scanner;
public class Ejercicio20 {
	

	
	    public static void main(String[] args) {
	        Scanner sc = new Scanner(System.in);

	        System.out.print("Variedad de artículos: ");
	        int n = sc.nextInt();

	        System.out.printf("%-10s %-10s %-12s %-12s%n", "Artículo", "Cantidad", "P.Unit", "Importe");

	        double total = 0.0;

	        for (int i = 1; i <= n; i++) {
	            System.out.println("Artículo #" + i);
	            System.out.print("  Cantidad: ");
	            int cant = sc.nextInt();
	            System.out.print("  Precio unitario: ");
	            double precio = sc.nextDouble();

	            double importe = cant * precio;
	            total += importe;

	            System.out.printf("%-10d %-10d %-12.2f %-12.2f%n", i, cant, precio, importe);
	        }

	        System.out.println("----------------------------------------------");
	        System.out.printf("%-34s %-12.2f%n", "TOTAL A PAGAR:", total);
	    }
	

}
