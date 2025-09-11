import java.util.Scanner;

public class Ejercicio17 {

	public static void main(String[] args) {
		// 17) Crear un programa que resuelva cualquier ecuación de segundo grado ax2-bx = c. Por
		//tanto, se debe pedir al usuario que introduzca los valores a, b y c, y el programa
		//indicará las posibles raíces solución.

		Scanner sc = new Scanner(System.in);

        System.out.println("Resolver a*x^2 + b*x + c = 0");
        System.out.print("Ingresa a: ");
        double a = sc.nextDouble();
        System.out.print("Ingresa b: ");
        double b = sc.nextDouble();
        System.out.print("Ingresa c: ");
        double c = sc.nextDouble();

        double EPS = 1e-12; // tolerancia para “casi cero”

        // Caso no cuadrático
        if (Math.abs(a) < EPS) {
            if (Math.abs(b) < EPS) {
                if (Math.abs(c) < EPS) {
                    System.out.println("Infinitas soluciones (identidad 0 = 0).");
                } else {
                    System.out.println("Sin solución.");
                }
            } else {
                double x = -c / b;
                System.out.printf("Ecuación lineal. Única solución: x = %.6f%n", x);
            }
            return;
        }

        // Cuadrática
        double delta = b * b - 4 * a * c;

        if (delta > EPS) {
            double sqrtDelta = Math.sqrt(delta);
            double x1 = (-b + sqrtDelta) / (2 * a);
            double x2 = (-b - sqrtDelta) / (2 * a);
            System.out.printf("Dos raíces reales distintas:%nx1 = %.6f%nx2 = %.6f%n", x1, x2);
        } else if (Math.abs(delta) <= EPS) {
            double x = (-b) / (2 * a);
            System.out.printf("Una raíz real doble:%nx = %.6f%n", x);
        } else {
            // Raíces complejas
            double parteReal = (-b) / (2 * a);
            double parteImag = Math.sqrt(-delta) / (2 * a);
            System.out.println("Dos raíces complejas conjugadas:");
            System.out.printf("x1 = %.6f + %.6fi%n", parteReal, parteImag);
            System.out.printf("x2 = %.6f - %.6fi%n", parteReal, parteImag);
        }
    
		
	}

}
