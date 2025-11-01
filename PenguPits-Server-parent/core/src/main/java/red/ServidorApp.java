package red; // Asume que HiloServer está en el paquete 'red'

// Importaciones necesarias si las usas
// import java.io.IOException;

public class ServidorApp {
    public static void main(String[] args) {
        System.out.println("--- Servidor de Pengu en ejecución ---");
        System.out.println("Puerto 9013. Esperando dos clientes...");
        
        // Inicia el servidor
        HiloServer hs = new HiloServer(); 
        hs.start();
        
        // Bloquear el thread principal para que el servidor siga corriendo
        try {
            // El servidor se mantendrá activo hasta que se detenga el thread o se fuerce el cierre.
            hs.join(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Servidor interrumpido.");
        }
    }
}
