package utiles;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class Entradas implements InputProcessor {

	public static boolean arriba = false, abajo = false, enter = false, derecha = false, izquierda = false;
	public static int mouseX = 0 , mouseY=0;
	public boolean keyDown(int keycode) {
	
		if (Keys.UP == keycode) {
			arriba = true;
		}
		
		if (Keys.DOWN == keycode) {
			abajo = true;
		}
		if (Keys.ENTER == keycode) {
			enter = true;
		}
		
		if (Keys.D == keycode) {
			derecha = true;
		}
		
		if (Keys.A == keycode) {
			izquierda = true;
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (Keys.UP == keycode) {
			arriba = false;
		}
		
		if (Keys.DOWN == keycode) {
			abajo = false;
		}
		
		if (Keys.ENTER == keycode) {
			enter = false;
		}
		
		if (Keys.D == keycode) {
			derecha = false;
		}
		
		if (Keys.A == keycode) {
			izquierda = false;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouseX = screenX;
		mouseY = Config.ALTO - screenY;
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}
