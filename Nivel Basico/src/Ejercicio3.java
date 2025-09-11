
public class Ejercicio3 {

	public static void main(String[] args) {
		
		System.out.println("Numero    Cuadrado    Cubo");
		
		for(int i = 0; i < 11; i++) {
			
			int c= (int) Math.pow(i, 2);
			int c2 = (int) Math.pow(i, 3);
			
			System.out.println(i + "            " + c + "            " + c2);
		}
	}

}
