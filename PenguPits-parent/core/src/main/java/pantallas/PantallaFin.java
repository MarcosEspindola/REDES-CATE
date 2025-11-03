package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import red.HiloCliente;
import elementos.Imagen;
import utiles.Config;
import utiles.Render;

// NOTA: Asume que tienes acceso al HiloCliente para enviar el comando de reinicio.

public class PantallaFin implements Screen {
    
    private final int ganadorID;
    private Imagen fondo;
    private Config t;
    private int opcionSeleccionada = 1; // 1: Jugar de nuevo, 2: Salir
    
    public PantallaFin(int id) {
        this.ganadorID = id;
        String rutaImagen = (id == 1) ? "Pingu 1 win.png" : "Pingu 2 win.png";
        
        // Cargar imagen de fondo específica (debe ser una textura local)
        Texture tFondo = new Texture(rutaImagen);
        fondo = new Imagen(tFondo);
        fondo.setSize(Config.ANCHO, Config.ALTO);
    }
    
    @Override
    public void show() {
        t = new Config();
        t.Texto("Thunder-BoldLC.otf", 60, Color.WHITE);
        // Reseteamos el estado de juego al entrar a la pantalla final
        utiles.Global.empieza = false; 
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        Render.batch.begin();
        fondo.dibujar();
        

        // 1. Textos del menú final
        t.setColor(opcionSeleccionada == 1 ? Color.CORAL : Color.WHITE);
        t.dibujarTexto("JUGAR DE NUEVO", 100, 200);
        
        t.setColor(opcionSeleccionada == 2 ? Color.CORAL : Color.WHITE);
        t.dibujarTexto("SALIR", 100, 100);
        
        Render.batch.end();
        
        // 2. Lógica de Input 
        manejarInput();
    }
    
    private void manejarInput() {
        // Navegación (Arriba/Abajo)
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            opcionSeleccionada = (opcionSeleccionada == 1) ? 2 : 1;
        }
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            opcionSeleccionada = (opcionSeleccionada == 2) ? 1 : 2;
        }
        
        // Selección (Enter/Espacio)
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            
            // --- Opción: JUGAR DE NUEVO ---
            if (opcionSeleccionada == 1) {
                try {
                    // Acceder al HiloCliente estático (expuesto en PantallaJuego)
                    HiloCliente cliente = PantallaJuego.getHiloClienteEstatico();
                    if (cliente != null) {
                        cliente.enviarMensaje("REINICIAR");
                        System.out.println("Comando REINICIAR enviado al servidor.");
                        // El cambio de pantalla ocurrirá cuando el servidor responda con INICIO_PARTIDA
                    }
                } catch (Exception e) {
                    System.err.println("Error al enviar comando REINICIAR: " + e.getMessage());
                }
            
            // --- Opción: SALIR ---
            } else if (opcionSeleccionada == 2) {
                // *** CRÍTICO: DETENER EL HILO DE RED ANTES DE SALIR ***
                try {
                    HiloCliente cliente = PantallaJuego.getHiloClienteEstatico();
                    if (cliente != null) {
                        // Opcional: Avisar al servidor
                        cliente.enviarMensaje("SALIR_APLICACION"); 
                        // Detener el Thread y cerrar el socket para evitar bloqueos del SO
                        cliente.detener(); 
                    }
                } catch (Exception e) {
                    // Ignorar errores al salir
                }
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        // Liberar el fondo de esta pantalla
        if (fondo != null && fondo.t != null) {
            fondo.t.dispose(); 
        }
    }
}