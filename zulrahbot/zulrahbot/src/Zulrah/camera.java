package Zulrah;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.script.Script;

public class camera {

	private Script script;

	public camera(Script s) {
		this.script = s;
	}

	public void moveNorth() {
		int r = Script.random(0, 30);
		if (r > 15)
			r = 375 - r;
		moveYaw(r);
	}
	
	public void moveWest() {
		moveYaw(75 + Script.random(0, 30));
	}

	public void moveSouth() {
		moveYaw(165 + Script.random(0, 30));
	}

	public void moveEast() {
		moveYaw(255 + Script.random(0, 30));
	}
	
	public int getLowestPitchAngle() {
		return script.getCamera().getLowestPitchAngle();
	}

	public int getX() {
		return script.getCamera().getX();
	}

	public int getY() {
		return script.getCamera().getY();
	}

	public int getZ() {
		return script.getCamera().getZ();
	}

	public int getYawAngle() {
		return script.getCamera().getYawAngle();
	}

	public int getPitchAngle() {
		return script.getCamera().getPitchAngle();
	}

	public boolean isDefaultScaleZ() {
		return script.getCamera().isDefaultScaleZ();
	}

	public boolean movePitch(int pitch) {
		moveCamera(getYawAngle(), pitch);
		return true;
	}

	public boolean moveYaw(int yaw) {
		moveCamera(yaw, getPitchAngle());
		return true;
	}

	public boolean toBottom() {
		return movePitch(0);
	}

	public boolean toTop() {
		return movePitch(67);
	}

	public void toEntity(Entity e) {
		toEntity(script.myPosition(), e);
	}

	public void toEntity(Position origin, Entity e) {
		if (e == null)
			return;
		moveCamera(getAngleTo(origin, e.getPosition()), getPitchTo(origin, e.getPosition()));
	}

	
	public void toPosition(Position origin, Position position) {
		if (position.distance(script.myPosition()) > 16)
			return;
		moveCamera(getAngleTo(origin, position), getPitchTo(origin, position));
	}

	public void toPosition(Position Position) {
		toPosition(script.myPosition(), Position);
	} 
	
	public void moveCamera(int yaw, int pitch) {
		if (pitch > 67)
			pitch = 67;
		else if (pitch < 22)
			pitch = 22;
		
		int pitchCur = getPitchAngle(),
			yawCur = getYawAngle(),
			pitchDir = pitch < pitchCur ? -1 : 1,
			pitchDiff = Math.abs(pitch - pitchCur),
			yawDir = yaw > yawCur ? -1 : 1,
			yawDiff = Math.abs(yaw - yawCur);
		
		if (yawDiff > 180) {
			// Flip how we get there
			yawDiff = 360 - yawDiff;
			yawDir *= -1;
		}
		
		if (yawDiff < 22 && pitchDiff < 14)
			return;
		
		int x = yawDir * yawDiff * 3,
			y = pitchDir * pitchDiff * 3;
		
		int minX = 40 - (yawDir == -1 ? x : 0),
			maxX = 724 - (yawDir == 1 ? x : 0),
			minY = 40 + (pitchDir == -1 ? y : 0),
			maxY = 460 + (pitchDir == 1 ? y: 0);
		
		Point mp = script.getMouse().getPosition();
		
		for (int i = 0; i < 5 && !script.getMouse().isOnScreen(); i++) {
			script.getMouse().move(Script.random(minX, maxX), Script.random(minY, maxY));
			sleep(5, 50);
		}
		
		if (mp.x < minX || mp.x > maxX
				|| mp.y < minY || mp.y > maxY) {
			script.getMouse().move(Script.random(minX, maxX), Script.random(minY, maxY));
			sleep(5, 50);
		}
		
		mousePress(true);
		
		mp = script.getMouse().getPosition();
		
		int newX = Math.min(764, Math.max(0, mp.x + x)),
			newY = Math.min(502, Math.max(0, mp.y + y));
		
		script.getMouse().move(newX, newY);
		
		sleep(5, 50);

		mousePress(false);
	}

	@SuppressWarnings("static-access")
	private void sleep(int i, int j) {
		try {
			script.sleep(Script.random(i, j));
		} catch (InterruptedException e) {
			
		}
	}

	private int getPitchTo(Position origin, Position t) {
		int pitch = 67 - (int) (t.distance(origin) * 5);

		if (pitch > 67) {
			pitch = 67;
		} else if (pitch < 22) {
			pitch = 22;
		}

		return pitch;
	}
	
	private int getAngleTo(Position origin, Position Position) {
		int degree = (int) Math.toDegrees(Math.atan2(
				Position.getY() - origin.getY(), Position.getX() - origin.getX()));
		int a = ((degree >= 0 ? degree : 360 + degree) - 90) % 360;
		return a < 0 ? a + 360 : a;
	}
	
	private void mousePress(boolean press) {
		script.getBot()
			.getMouseEventHandler()
			.generateBotMouseEvent(press ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED,
					System.currentTimeMillis(), 0,
					script.getMouse().getPosition().x,
					script.getMouse().getPosition().y, 1, false,
					MouseEvent.BUTTON2, true);
	}
	
}
