
public class Ejercicio4 {

	public static void main(String[] args) {
		int cont = 0;
		for(int i = 30; i < 127; i++) {
			char c = (char) i;
			System.out.print(c + " ");
			cont++;
			if(cont == 15) {
				System.out.println(" ");
				cont = 0;
			}
		}
	}
}


