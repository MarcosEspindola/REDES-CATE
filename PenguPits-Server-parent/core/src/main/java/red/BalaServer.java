package red;

//No usamos librerías de libGDX (Texture, Actor, Rectangle), solo Java puro.

public class BalaServer {

private float x;
private float y;
// La velocidad se ajustará en el Servidor (HiloServer.actualizarBalas)
private final float VELOCIDAD = 400f; // Velocidad en píxeles/segundo (ajustada para el servidor)
private boolean direccionDerecha;
private int idBala;   // ID único para sincronizar con el cliente
private int idJugadorDano; // Quién disparó la bala (1 o 2)

public BalaServer(float x, float y, boolean direccionDerecha, int idBala, int idJugadorDano) {
   this.x = x;
   this.y = y;
   this.direccionDerecha = direccionDerecha;
   this.idBala = idBala;
   this.idJugadorDano = idJugadorDano;
}

public void actualizar(float delta) {
   // Aplica la velocidad en función del tiempo transcurrido (delta)
   if (direccionDerecha) {
       this.x += VELOCIDAD * delta;
   } else {
       this.x -= VELOCIDAD * delta;
   }
}

public boolean debeEliminarse() {
   // Límite de la pantalla (Asumimos el límite de 1280 del mundo de juego)
   return x < -50 || x > 1330; 
}

// --- Getters y Setters para HiloServer ---

public float getX() {
   return x;
}

public float getY() {
   return y;
}

public boolean isDerecha() {
   return direccionDerecha;
}

public int getIdJugadorDano() {
   return idJugadorDano;
}

public int getIdBala() {
   return idBala;
}

// Métodos setX/setY no necesarios, ya que solo se actualiza en el bucle
// y la posición es solo leída.
}