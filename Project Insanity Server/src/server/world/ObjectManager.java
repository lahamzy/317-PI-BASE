package server.world;

import java.util.ArrayList;

import server.model.objects.Object;
import server.util.Misc;
import server.model.players.Client;
import server.model.players.PlayerHandler;

/**
 * @author Sanity
 */

public class ObjectManager {

	public ArrayList<Object> objects = new ArrayList<Object>();
	private ArrayList<Object> toRemove = new ArrayList<Object>();

	public void process() {
		for (Object o : objects) {
			if (o.tick > 0)
				o.tick--;
			else {
				updateObject(o);
				toRemove.add(o);
			}
		}
		for (Object o : toRemove) {
			if (isObelisk(o.newId)) {
				int index = getObeliskIndex(o.newId);
				if (activated[index]) {
					activated[index] = false;
					teleportObelisk(index);
				}
			}
			objects.remove(o);
		}
		toRemove.clear();
	}

	public void removeObject(int x, int y) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				c.getPA().object(-1, x, y, 0, 10);
			}
		}
	}

	public void updateObject(Object o) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				c.getPA().object(o.newId, o.objectX, o.objectY, o.face, o.type);
			}
		}
	}

	public void placeObject(Object o) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				if (c.distanceToPoint(o.objectX, o.objectY) <= 60)
					c.getPA().object(o.objectId, o.objectX, o.objectY, o.face,
							o.type);
			}
		}
	}

	public Object getObject(int x, int y, int height) {
		for (Object o : objects) {
			if (o.objectX == x && o.objectY == y && o.height == height)
				return o;
		}
		return null;
	}

	public void loadObjects(Client c) {
		if (c == null)
			return;
		for (Object o : objects) {
			if (loadForPlayer(o, c))
				c.getPA().object(o.objectId, o.objectX, o.objectY, o.face,
						o.type);
		}
		loadCustomSpawns(c);
		if (c.distanceToPoint(2813, 3463) <= 60) {
			c.getFarming().updateHerbPatch();
		}
	}

	public void loadCustomSpawns(Client client) {
		client.getPA().checkObjectSpawn(410, 3218, 3433, 2, 10);
		client.getPA().checkObjectSpawn(3044, 3044, 9790, 2, 10);
		client.getPA().checkObjectSpawn(2213, 3049, 9786, 2, 10);
		client.getPA().checkObjectSpawn(2213, 2541, 3886, 2, 10);
		client.getPA().checkObjectSpawn(2213, 2542, 3886, 2, 10);
		client.getPA().checkObjectSpawn(2213, 2543, 3886, 2, 10);
		client.getPA().checkObjectSpawn(2213, 2544, 3886, 2, 10);
		client.getPA().checkObjectSpawn(2213, 2545, 3886, 2, 10);
		client.getPA().checkObjectSpawn(6552, 3208, 3438, 2, 10);
		if (client.heightLevel == 0) {
			client.getPA().checkObjectSpawn(2492, 2911, 3614, 1, 10);
		} else {
			client.getPA().checkObjectSpawn(-1, 2911, 3614, 1, 10);
		}
	}

	public final int IN_USE_ID = 14825;

	public boolean isObelisk(int id) {
		for (int j = 0; j < obeliskIds.length; j++) {
			if (obeliskIds[j] == id)
				return true;
		}
		return false;
	}

	public int[] obeliskIds = { 14829, 14830, 14827, 14828, 14826, 14831 };
	public int[][] obeliskCoords = { { 3154, 3618 }, { 3225, 3665 },
			{ 3033, 3730 }, { 3104, 3792 }, { 2978, 3864 }, { 3305, 3914 } };
	public boolean[] activated = { false, false, false, false, false, false };

	public void startObelisk(int obeliskId) {
		int index = getObeliskIndex(obeliskId);
		if (index >= 0) {
			if (!activated[index]) {
				activated[index] = true;
				addObject(new Object(14825, obeliskCoords[index][0],
						obeliskCoords[index][1], 0, -1, 10, obeliskId, 16));
				addObject(new Object(14825, obeliskCoords[index][0] + 4,
						obeliskCoords[index][1], 0, -1, 10, obeliskId, 16));
				addObject(new Object(14825, obeliskCoords[index][0],
						obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16));
				addObject(new Object(14825, obeliskCoords[index][0] + 4,
						obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16));
			}
		}
	}

	public int getObeliskIndex(int id) {
		for (int j = 0; j < obeliskIds.length; j++) {
			if (obeliskIds[j] == id)
				return j;
		}
		return -1;
	}

	public void teleportObelisk(int port) {
		int random = Misc.random(5);
		while (random == port) {
			random = Misc.random(5);
		}
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				int xOffset = c.absX - obeliskCoords[port][0];
				int yOffset = c.absY - obeliskCoords[port][1];
				if (c.goodDistance(c.getX(), c.getY(),
						obeliskCoords[port][0] + 2, obeliskCoords[port][1] + 2,
						1)) {
					c.getPA().startTeleport2(
							obeliskCoords[random][0] + xOffset,
							obeliskCoords[random][1] + yOffset, 0);
				}
			}
		}
	}

	public boolean loadForPlayer(Object o, Client c) {
		if (o == null || c == null)
			return false;
		return c.distanceToPoint(o.objectX, o.objectY) <= 60
				&& c.heightLevel == o.height;
	}

	public void addObject(Object o) {
		if (getObject(o.objectX, o.objectY, o.height) == null) {
			objects.add(o);
			placeObject(o);
		}
	}

}