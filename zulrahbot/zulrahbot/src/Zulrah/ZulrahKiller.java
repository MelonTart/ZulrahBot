package Zulrah;

import java.awt.BasicStroke;
import java.awt.Color;
//import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.osbot.rs07.api.Chatbox.MessageType;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Projectile;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.PrayerButton;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
// import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import Zulrah.RunePouch.Slot;

@SuppressWarnings("unused")
@ScriptManifest(author = "Spooky Scripts", info = "Zulrah killer", logo = "", name = "Spooky Zulrah", version = .69)
public class ZulrahKiller extends Script {
	int lastAnimation = -1;
	boolean IsAttacking = false;

	boolean Vengeance = false;
	boolean DBow = false;
	boolean ImbuedHeart = false;
	boolean RangedPotion = false;
	boolean AntiVenom = false;
	int numOfPPots = 2;
	boolean Augury = false;
	boolean Rigour = false;

	long deltaTZ = 0;
	int numReset = 0;
	int food = 385;

	int numOfDeaths = 0;
	int numOfZulrahKills = 0;
	long startOfBot = logTime();
	int minFood = 10;
	boolean oneLessShark = false;

	int killsToEnd = 5000; //
	double timeToEnd = 360; // minutes

	int EVT = 13072;
	int EVB = 13073;
	int VG = 8842;

	int MH = 11663;
	int RH = 11664;
	int OccN = 12002;
	int fury = 6585;

	int ATop = 4712;
	int ATop2 = 4868;
	int ABot = 4714;
	int ABot2 = 4874;

	int DTop = 2503;
	int DBot = 2497;
	int BG = 7462;
	int SHelm = 12931;
	int Prims = 13237;
	int EternalB = 13235;
	int boot = EternalB;
	int Trident = 22292;
	int BP = 12926;
	// int MWeapon = Trident;
	int Ava = 22109;
	int RSuffc = 20657; // charged
	int IH = 20724;
	long lTime = 0;
	double dTime = 0;
	long lastEat = logTime();
	long lastVenge = logTime();
	boolean hasRing = true;
	boolean RP = false;

	String rune1 = "None";
	String rune2 = "None";
	String rune3 = "None";
	//
	boolean autoScan = false;
	int[] Magic_gear = new int[] { ATop, ABot, ATop2, ABot2, SHelm, OccN };
	int[] Range_gear = new int[] { DTop, DBot, SHelm, fury, BP };
	int MWeapon1 = 22292;
	int MWeapon2 = 22292;
	int[] lootedItems = new int[10];
	int[] allLootedItems = new int[100000];
	int count = 0;

	int totalGoldEarned = 0;

	camera camCam = new camera(this);
	Magic magic = new Magic();
	Position lumbridgeCastle = new Position(3222, 3218, 0);
	Position lumbridgeCastleMid = new Position(3212, 3211, 0);
	Position clanWars = new Position(3367, 3164, 0);
	Position Portal = new Position(3328, 4752, 0);

	public int rotation = 0;
	public int phase = 1;
	public final int bluezulrah = 2044;
	public final int redzulrah = 2043;
	public final int greenzulrah = 2042;
	public final int[] ZulrahIDS = new int[] { bluezulrah, redzulrah, greenzulrah };
	public Position StartLocation = null;
	public Position DEnd = null;
	BufferedImage background;

	static Object lock = new Object();
	public static RunePouch RunePouch = new RunePouch();

	public void onStart() {
		try {
			background = ImageIO.read(ZulrahKiller.class.getResourceAsStream("/resources/background.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GUI gui = new GUI();
		gui.run();
		while (gui.isVisible()) {
			// log(gui.isVisible());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		scanGear(); // debug auto scan inv feature
		getSettings().setRunning(true);
		if (!autoScan) {
			setGear(gui.jComboBox1.getSelectedItem().toString()); // "Ahrims", "Mystics", "VoidMage"
			setGear(gui.jComboBox2.getSelectedItem().toString()); // "Armadyl", "Karils", "BlessedDhide", "BlackDhide",
																	// "VoidRange", "EliteVoidRange"
			setGear(gui.jComboBox3.getSelectedItem().toString());// "Shark", "Angler", "Monkfish", "MantaRay"
			setGear(gui.jComboBox4.getSelectedItem().toString());// "TridentOfTheSwamp", "TridentOfTheSwampE",

			DBow = gui.jRadioButton7.isSelected();
			ImbuedHeart = gui.jRadioButton5.isSelected();
			numOfPPots = Integer.parseInt(gui.jTextField1.getText().replaceAll(" ", ""));
			RangedPotion = gui.jRadioButton4.isSelected();

		}
		Vengeance = gui.jRadioButton6.isSelected();
		Augury = gui.jRadioButton9.isSelected();
		Rigour = gui.jRadioButton8.isSelected();
		log("DBow " + DBow);
		log("Vengeance " + Vengeance);
		log("ImbuedHeart " + ImbuedHeart);
		log("numOfPPots " + numOfPPots);
		log("RangedPotion " + RangedPotion);
		log("Augury " + Augury);
		log("Rigour " + Rigour);
		// add boots if you want
		// set gear with auto scan feature
		gui.dispose();
		log("Bot Started");
		checkDeath();
		startKill();
	}

	public void setGear(String s) {
		// mage gear
		if (s.equals("Ahrims")) {
			Magic_gear = new int[] { ATop, ABot, ATop2, ABot2, SHelm, OccN, BG, Ava, boot, RSuffc };
		}
		if (s.equals("Mystics")) {
			Magic_gear = new int[] { 4091, 4093, SHelm, OccN, BG, Ava, boot, RSuffc };
		}
		if (s.equals("VoidMage")) {
			Magic_gear = new int[] { 8839, 8840, MH, VG, OccN, Ava, boot, RSuffc };
			AntiVenom = true;
		}
		if (s.equals("EliteVoidMage")) {
			Magic_gear = new int[] { EVT, EVB, MH, VG, OccN, Ava, boot, RSuffc };
			AntiVenom = true;
		}
		// range gear
		if (s.equals("BlackDhide")) {
			Range_gear = new int[] { DTop, DBot, SHelm, fury, BP, BG, Ava, boot, RSuffc };
		}
		if (s.equals("Armadyl")) {
			Range_gear = new int[] { 11828, 11830, SHelm, fury, BP, BG, Ava, boot, RSuffc };
		}
		if (s.equals("Karils")) {
			Range_gear = new int[] { 4736, 4739, 4737, 4738, 4940, 4941, 4942, 4943, 4946, 4947, 4948, 4949, SHelm,
					fury, BP, BG, Ava, boot, RSuffc };
		}
		if (s.equals("VoidRange")) {
			Range_gear = new int[] { 8839, 8840, RH, VG, fury, BP, Ava, boot, RSuffc };
			AntiVenom = true;
		}
		if (s.equals("EliteVoidRange")) {
			Range_gear = new int[] { EVT, EVB, RH, VG, fury, BP, Ava, boot, RSuffc };
			AntiVenom = true;
		}
		if (s.equals("BlessedDhide")) {
			Range_gear = new int[] { 12492, 12494, SHelm, fury, BP, BG, Ava, boot, RSuffc };
		}
		// food
		if (s.equals("Shark")) {
			food = 385;
		}
		if (s.equals("Angler")) {
			food = 13441;
		}
		if (s.equals("Monkfish")) {
			food = 7946;
		}
		if (s.equals("MantaRay")) {
			food = 391;
		}
		// mage weapon
		if (s.equals("TridentOfTheSwamp")) {
			// MWeapon = 12899;
		}
		if (s.equals("TridentOfTheSwampE")) {
			// MWeapon = Trident;
		}
		if (s.equals("TridentOfTheSeas")) {
			// MWeapon = 11907;
		}
	}

	public boolean CheckCharacter() {
		if (this.inventory.getAmount(food) < minFood || this.inventory.getAmount("Prayer potion(4)") < 1) {
			return false;
		} else {
			if (AntiVenom) {
				if (this.inventory.getAmount("Anti-venom+(1)") < 1 && this.inventory.getAmount("Anti-venom+(2)") < 1
						&& this.inventory.getAmount("Anti-venom+(3)") < 1
						&& this.inventory.getAmount("Anti-venom+(4)") < 1) {
					return false;
				}
			}
			try {
				if (Vengeance && getMagic().canCast(Spells.LunarSpells.VENGEANCE) == false) {
					return false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
	}

	public void regear() {
		try {
			Thread.sleep(random(1000, 1500));
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// teleport to clan wars
		jeweleryHandler("Ring of dueling", "Wear");
		try {
			Thread.sleep(random(1000, 1500));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getTabs().open(Tab.EQUIPMENT);
		getEquipment().interact(EquipmentSlot.RING, "Clan Wars");
		try {
			Thread.sleep(random(3500, 4500));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getTabs().open(Tab.INVENTORY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		GearSwap(Magic_gear);
		try {
			Thread.sleep(random(2500, 3500));
			camCam.toEntity(getObjects().closest("Bank chest"));
			camCam.toTop();
			Position Mid = new Position(3377 + random(-2, 2), 3164 + random(-2, 2), 0);
			Mid.interact(bot, "Walk here");
			sleep(random(4000, 4500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (getObjects().closest("Bank chest") != null && !getObjects().closest("Bank chest").isVisible()) {
			camCam.toEntity(getObjects().closest("Bank chest"));
		}
		getObjects().closest("Bank chest").interact("Use");
		try {
			Thread.sleep(random(700, 900));
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (myPlayer().isMoving()) {

		}

		long Sharks = getInventory().getAmount(food);
		long PPots = getInventory().getAmount("Prayer potion(4)");
		long RPots = getInventory().getAmount("Ranging potion(4)") + getInventory().getAmount("Ranging potion(3)")
				+ getInventory().getAmount("Ranging potion(2)") + getInventory().getAmount("Ranging potion(1)");
		long VPots = getInventory().getAmount("Anti-venom+(4)") + getInventory().getAmount("Anti-venom+(3)")
				+ getInventory().getAmount("Anti-venom+(2)") + getInventory().getAmount("Anti-venom+(1)");
		long EPots = getInventory().getAmount("Vial");
		if (inventory.contains("Ring of dueling(1)") || inventory.contains("Ring of dueling(2)")
				|| inventory.contains("Ring of dueling(3)") || inventory.contains("Ring of dueling(4)")
				|| inventory.contains("Ring of dueling(5)") || inventory.contains("Ring of dueling(6)")
				|| inventory.contains("Ring of dueling(7)") || inventory.contains("Ring of dueling(8)")) {
			hasRing = true;
		} else {
			hasRing = false;
		}
		while (myPlayer().isMoving()) {
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (getBank().isOpen() != true) {
			getObjects().closest("Bank chest").interact("Use");
		}
		// deposit loot
		log("Depositing Items"); // debug removed wait times for test(was successful)
		for (int i : lootedItems) {
			if (i != 12938 && ((RP || (i != 560)) || !Vengeance)) {
				getBank().depositAll(i);
//				try {
//					Thread.sleep(600);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
		count = 0;
		// re-gear
		if (EPots != 0) {
			getBank().depositAll("Vial");
		}
		if (hasRing == false) {
			getBank().withdraw("Ring of dueling(8)", 1);
		}
//		try {
//			Thread.sleep(random(100, 300));
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (RPots == 0 && RangedPotion) {
			getBank().withdraw("Ranging potion(4)", 1);
		}
//		try {
//			Thread.sleep(random(100, 300));
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (PPots != numOfPPots) {
			getBank().depositAll("Prayer potion(3)", "Prayer potion(2)", "Prayer potion(1)");
			getBank().withdraw("Prayer potion(4)", (int) (numOfPPots - PPots));
		}
		if (VPots < 1 && AntiVenom) {
			getBank().withdraw("Anti-venom+(4)", 1);
		}
//		try {
//			Thread.sleep(random(100, 300));
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		getBank().withdrawAll(food);

		if (oneLessShark) {
			getBank().deposit(food, 1);
		}
		try {
			Thread.sleep(random(100, 300));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// exit bank screen
		getBank().close();
		// check gear if refills are needed
		refillGear();
		// go into ffa portal to recover stats
		Position Mid2 = new Position(3361 + random(-2, 2), 3162 + random(-2, 2), 0);
		camCam.toPosition(Mid2);

		try {
			sleep(random(500, 700));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Mid2.interact(bot, "Walk here");
		try {
			sleep(random(1500, 1700));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camCam.toEntity(getObjects().closest("Free-for-all portal"));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (myPlayer().getPosition().distance(new Position(3327, 4751, 0)) != 0) {

			if (getObjects().closest(26645) == null && !myPlayer().isMoving()) {
				Mid2.interact(bot, "Walk here");
				log("Walking to Portal");
			} else {
				getObjects().closest(26645).interact("Enter");
				log("Going through Portal");
			}
			try {
				Thread.sleep(random(700, 900));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log("You are in the Portal");

//		while (myPlayer().getPosition().distance(Portal) > 1) {
//			log("Going through Portal");
//			while (getObjects().closest(26645) != null
//					&& myPlayer().getPosition().distance(getObjects().closest(26645)) < 50) {
//				while (getObjects().closest(26646) == null) {
//					if (getObjects().closest(26645) != null) {
//						getObjects().closest(26645).interact("Enter");
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		try {
			Thread.sleep(random(1500, 2000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getTabs().open(Tab.INVENTORY);
		while (myPlayer().getAnimation() != 3864) {
			getInventory().interact("Teleport", 12938);
			log("Teleporting to Zulrah");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(random(4000, 4200));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int onLoop() {
		try {
			fightZulrah();
		} catch (NullPointerException e) {
			log("Zulrah is Dead");
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Position zulandra = new Position(2204, 3056, 0);
		if (!checkDeath() && myPlayer().getPosition().distance(zulandra) > 50) {
			collectLoot();
			if (!CheckCharacter()) {
				log("Not Enough Gear For Another Kill");
				regear();
			} else {
				// start new kill, teleport then "Board" the boat again
				log("Enough Gear For Another Kill");
				try {
					getObjects().closest(11701).interact("Read");
					Thread.sleep(2500);
					while (myPlayer().isMoving()) {

					}
					dialogues.completeDialogue("Yes");
					Thread.sleep(1500);
				} catch (Exception e) {

				}
			}
			numOfZulrahKills++;
			if (deltaTime(startOfBot) / 60000 > timeToEnd || numOfZulrahKills > killsToEnd) {
				log("End Condition Met");
				log("Gold Earned: " + totalGoldEarned);
				log("Total Kills: " + numOfZulrahKills);
				log("Total Deaths: " + numOfDeaths);
				stop(false);
				return 0;
			}
		} else {

		}

		startKill();
		return 0;
	}

	public void onExit() {
		log("Gold Earned: " + totalGoldEarned);
		log("Total Kills: " + numOfZulrahKills);
		log("Total Deaths: " + numOfDeaths);
	}

	public void onPaint(Graphics2D g) {
		if (background != null) {
			g.setColor(Color.WHITE);
			g.fillRect(0, bot.getCanvas().getHeight() - 165, 519, 165);
			g.setColor(Color.CYAN);
			g.setStroke(new BasicStroke(10));
			g.drawRect(0, bot.getCanvas().getHeight() - 165, 519, 165);
			g.drawImage(background, null, 0, bot.getCanvas().getHeight() - background.getHeight());
			g.setStroke(new BasicStroke());
		}
		g.setColor(Color.BLACK);
		g.drawString("Total Runtime(s): " + formatTime(deltaTime(startOfBot)), 130, bot.getCanvas().getHeight() - 70);
		g.drawString("Total Kills: " + numOfZulrahKills, 130, bot.getCanvas().getHeight() - 90);
		g.drawString("Total Deaths: " + numOfDeaths, 130, bot.getCanvas().getHeight() - 110);

		g.drawString("Total Gold Earned: " + totalGoldEarned, 130, bot.getCanvas().getHeight() - 50);
		g.drawString("Kills per Hour: " + PerHour(numOfZulrahKills, deltaTime(startOfBot), 0), 130,
				bot.getCanvas().getHeight() - 30);
		g.drawString("Gold per Hour: " + PerHour(totalGoldEarned, deltaTime(startOfBot), 2), 130,
				bot.getCanvas().getHeight() - 10);
	}

	public void setup() {
		numReset++;
		rotation = 0;
		phase = 1;
		StartLocation = myPlayer().getPosition();

	}

	public void checkRotation() {
		if (rotation != 0 || phase == 1) {
			return;
		}
		while (getNpcs().closest(ZulrahIDS).getAnimation() == 5071
				|| getNpcs().closest(ZulrahIDS).getAnimation() == 5072
				|| getNpcs().closest(ZulrahIDS).getAnimation() == 5073) {

		}
		if (phase == 2) {

			NPC ZulrahC1 = getNpcs().closest(greenzulrah);
			NPC ZulrahC2 = getNpcs().closest(bluezulrah);

			if (ZulrahC1 != null) {
				rotation = 3;
				return;
			}
			if (ZulrahC2 != null) {
				rotation = 4;
				return;
			}
		}
		if (phase == 4) {
			if (getNpcs().closest(greenzulrah).getY() < StartLocation.getY()) {
				rotation = 1;
				return;
			} else {
				rotation = 2;
				return;
			}
		}
		return;
	}

	public void fightZulrah() {
		setup();
		boolean ZulrahKilled = false;
		while (ZulrahKilled == false) {
			checkRotation();
			attackZulrah();
			if (getNpcs().closest(ZulrahIDS) == null) {
				ZulrahKilled = true;
				break;
			}
			if ((getNpcs().closest(ZulrahIDS).getHealthPercent() == 0 && getNpcs().closest(ZulrahIDS) != null)
					|| getNpcs().closest(ZulrahIDS) == null) {
				log("Zulrah has Died");
				ZulrahKilled = true;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			if (getSkills().getDynamic(Skill.HITPOINTS) <= 0) {
				return;
			}
		}

	}

	public void attackZulrah() {
		if (getSkills().getDynamic(Skill.HITPOINTS) <= 0) {
			return;
		}
		if (rotation == 0) {
			if (phase == 1) {
				attack("green", 6, 9, 0, 18800, null);
			}
			if (phase == 2) {
				// 13100
				attack("red", 6, 9, 0, 5500, "red");
				attack("red", 4, 10, 0, 7600, "blue");
			}
			if (phase == 3) {
				attack("blue", -4, 4, 0, 10800, "green");
			}
		}
		if (rotation == 1) {
			if (phase == 4) {
				attack("green", -4, 4, 0, 24000, "red");
			}
			if (phase == 5) {
				attack("red", -4, 4, 0, 13200, "blue");
			}
			if (phase == 6) {
				attack("blue", -4, 4, 0, 12600, "green");
			}
			if (phase == 7) {
				attack("green", 4, 3, 0, 17400, "blue");
			}
			if (phase == 8) {
				attack("blue", 4, 3, 0, 7000, "blue");
				attack("blue", -4, 2, 0, 15000, "jad1");
			}
			if (phase == 9) {
				attack("jad1", -4, 10, 0, 29200, "red");
			}
			if (phase == 10) {
				// 13250
				attack("red", -4, 10, 0, 2000, "red");
				attack("red", -6, 8, 0, 2000, "red");
				attack("red", -4, 10, 0, 9250, "green");
			}
			if (phase == 11) {
				attack("green", 4, 10, 0, 21000, null);
			}
			if (phase > 11) {
				regear();
				return;
			}
		}
		if (rotation == 2) {
			if (phase == 4) {
				attack("green", -4, 1, 0, 17400, "blue");
			}
			if (phase == 5) {
				attack("blue", -4, 1, 0, 9000, "blue");
				attack("blue", 1, 2, 0, 15000, "red");
			}
			if (phase == 6) {
				attack("red", 4, 6, 0, 4000, "red");
				attack("red", 4, 8, 0, 9350, "green");
			}
			if (phase == 7) {
				attack("green", 4, 2, 0, 12460, "blue");
			}
			if (phase == 8) {
				attack("blue", -4, 2, 0, 22220, "jad1");
			}
			if (phase == 9) {
				attack("jad1", -4, 10, 0, 29400, "red");
			}
			if (phase == 10) {
				attack("red", -4, 10, 0, 1250, "red");
				attack("red", -6, 8, 0, 1250, "red");
				attack("red", -4, 10, 0, 10100, "green");
			}
			if (phase == 11) {
				attack("green", 4, 10, 0, 21600, null);
			}
			if (phase > 11) {
				regear();
				return;
			}
		}
		if (rotation == 3) {
			if (phase == 2) {
				attack("green", 4, 10, 0, 10000, "green");
				attack("green", -4, 10, 0, 8300, "red");
			}
			if (phase == 3) {
				// 23750
				attack("red", -6, 8, 0, 17500, "red");
				attack("red", -4, 10, 0, 6250, "blue");

			}
			if (phase == 4) {
				attack("blue", -5, 7, 0, 12620, "green");
			}
			if (phase == 5) { // not attacking for some reason
				attack("green", 4, 2, 0, 12600, "blue");
			}
			if (phase == 6) { // not attacking for some reason
				attack("blue", 4, 2, 0, 12600, "green");
			}
			if (phase == 7) {
				attack("green", -4, 4, 0, 15600, "green");
			}
			if (phase == 8) {
				attack("green", -4, 4, 0, 12600, "blue");
			}
			if (phase == 9) {
				attack("blue", 4, 4, 0, 22220, "jad2");
			}
			if (phase == 10) {
				attack("jad2", 4, 4, 0, 21600, "blue");
			}
			if (phase == 11) {
				attack("blue", 4, 4, 0, 21600, null);
			}
			if (phase > 11) {
				regear();
				return;
			}
		}
		if (rotation == 4) {
			if (phase == 2) {
				attack("blue", 4, 10, 0, 12200, "blue");
				attack("blue", -4, 4, 0, 10000, "green");
			}
			if (phase == 3) {
				attack("green", -4, 4, 0, 15020, "blue");
			}
			if (phase == 4) {
				attack("blue", -4, 4, 0, 18550, "red");
			}
			if (phase == 5) {
				attack("red", 4, 4, 0, 17450, "green");
			}
			if (phase == 6) {
				attack("green", 4, 3, 0, 10780, "green");
			}
			if (phase == 7) {
				attack("green", 4, 3, 0, 10000, "green");
				attack("green", -4, 4, 0, 11000, "blue");
			}
			if (phase == 8) {
				attack("blue", -4, 4, 0, 20440, "green");
			}
			if (phase == 9) {
				attack("green", 4, 4, 0, 12600, "blue");
			}
			if (phase == 10) {
				attack("blue", 4, 4, 0, 16840, "jad2");
			}
			if (phase == 11) {
				attack("jad2", 4, 4, 0, 18000, "blue");
			}
			if (phase == 12) {
				attack("blue", 4, 10, 0, 11400, null);
			}
			if (phase > 12) {
				regear();
				return;
			}
		}
		if (phase < 20) {
			phase++;
		} else {
			regear();
			return;
		}
	}

	public boolean eat() {
		log("Eating Food");
		return getInventory().interact("Eat", food);

	}

	public void attack(String Color, int x, int y, int z, double time, String Next) {
		deltaTZ = logTime();
		long logTime = logTime();
		long lastAttack = logTime();
		long lastMove = logTime();
		// Phase Timing Debug Vars
		lastEat = logTime();
		//
		lTime = logTime;
		// lag check if current phase is off of expected phase then tele

		if (Color == "blue" && getPrayer().isActivated(PrayerButton.PROTECT_FROM_MAGIC) == false) {
			getPrayer().open();
			getPrayer().set(PrayerButton.PROTECT_FROM_MAGIC, true);
			if (Rigour) {
				getPrayer().set(PrayerButton.RIGOUR, true);
			} else {
				getPrayer().set(PrayerButton.EAGLE_EYE, true);
			}
			log("Praying Magic");
			getTabs().open(Tab.INVENTORY);
			GearSwap(Range_gear);

		}

		if (Color == "red") {
			if (Augury) {
				if (getPrayer().isActivated(PrayerButton.AUGURY) != true) {
					getPrayer().open();
					getPrayer().deactivateAll();
					getPrayer().set(PrayerButton.AUGURY, true);
					getTabs().open(Tab.INVENTORY);
				}
			} else {
				if (getPrayer().isActivated(PrayerButton.MYSTIC_MIGHT) != true) {
					getPrayer().open();
					getPrayer().deactivateAll();
					getPrayer().set(PrayerButton.MYSTIC_MIGHT, true);
					getTabs().open(Tab.INVENTORY);
				}
			}
			GearSwap(Magic_gear);

			// GearSwap(MWeapon);

		}

		if (Color == "green" && getPrayer().isActivated(PrayerButton.PROTECT_FROM_MISSILES) == false) {
			getPrayer().open();
			getPrayer().set(PrayerButton.PROTECT_FROM_MISSILES, true);
			if (Augury) {
				getPrayer().set(PrayerButton.AUGURY, true);
			} else {
				getPrayer().set(PrayerButton.MYSTIC_MIGHT, true);
			}
			log("Praying Range");
			getTabs().open(Tab.INVENTORY);
			GearSwap(Magic_gear);

			// GearSwap(MWeapon);

		}

		Position End = new Position(x + StartLocation.getX(), y + StartLocation.getY(), z);
		DEnd = End;

		// Initial move when starting fight
		if (myPlayer().getPosition().equals(End) == false) {
			if (End.isVisible(bot) == false) {
				camCam.toPosition(End);
			}
			if (Color != "jad1" && Color != "jad2") {
				End.hover(bot);
				End.interact(bot, "Walk here");
				IsAttacking = false;
			}
			if (myPlayer().getPosition().distance(End) > 4) {
				try {
					if (Color != "jad1" && Color != "jad2") {
						if (phase == 1) {
							Thread.sleep(random(2600, 2800));
						} else {
							Thread.sleep(random(500, 700));
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (getNpcs().closest(ZulrahIDS) != null) {
			NPC Zulrah = getNpcs().closest(ZulrahIDS);
			if (getNpcs().closest(ZulrahIDS).isAttackable()) {
				camCam.toEntity(Zulrah);
				Zulrah.hover();
				Zulrah.interact("Attack");
				IsAttacking = true;
				log("Attacking Zulrah");
				lastAttack = logTime();
			}
		}

		if (Color == "jad1" || Color == "jad2") {
			getPrayer().open();
		}
		if (getNpcs().closest(ZulrahIDS) == null) {
			return;
		}
		while (deltaTime(logTime) < time + 20) { // start of attack loop
			if (getCamera().getPitchAngle() < 57 && Color != "jad1" && Color != "jad2") {
				camCam.toTop();
			}
//			if (phase > 1 && deltaTime(logTime) > 1500 && getNpcs().closest(ZulrahIDS) != null) {
//				if (Color == "blue" && getNpcs().closest(bluezulrah) == null) {
//					regear();
//				}
//				if (Color == "red" && getNpcs().closest(redzulrah) == null) {
//					regear();
//				}
//
//				if ((Color == "green" || Color == "jad1" || Color == "jad2")
//						&& getNpcs().closest(greenzulrah) == null) {
//					regear();
//				}
//			}
			if (deltaTime(logTime) > time - 7700 && Color == "blue" && getCombat().getSpecialPercentage() >= 55
					&& DBow == true && Next != "blue") {
				getInventory().interact("Wield", 11235);
				getCombat().toggleSpecialAttack(true);
				getNpcs().closest(ZulrahIDS).interact("Attack");
				try {
					Thread.sleep(random(1000, 1200));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getInventory().interact("Wield", BP);
				// getNpcs().closest(ZulrahIDS).interact("Attack");
			}
			if (getSkills().getDynamic(Skill.HITPOINTS) <= 0) {
				return;
			}
			if (phase == 1) {
				try {
					Thread.sleep(random(400, 600));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				NPC Zulrah = getNpcs().closest(ZulrahIDS);
				if (Zulrah == null) {
					return;
				}
			}
			long lastPraySwitch = logTime();
			while ((Color == "jad1" || Color == "jad2") && deltaTime(lastPraySwitch) < 4000) {
				for (Projectile p : projectiles.getAll()) {
					if (p.getId() == 1046) { // Mage
						getPrayer().open();
						getPrayer().set(PrayerButton.PROTECT_FROM_MISSILES, true);
						lastPraySwitch = logTime();
						break;
					}
					if (p.getId() == 1044) {// Range
						getPrayer().open();
						getPrayer().set(PrayerButton.PROTECT_FROM_MAGIC, true);
						lastPraySwitch = logTime();
						break;
					}
				}
				if (getNpcs().closest(ZulrahIDS).isUnderAttack() == false) {
					getNpcs().closest(ZulrahIDS).interact("Attack");
				}
				if (deltaTime(lastPraySwitch) > 4000) {
					break;
				}
//				if (getSkills().getDynamic(Skill.HITPOINTS) < 45 && deltaTime(lastEat) > 4000 && deltaTime(lastPraySwitch) < 75) {
//					lastEat = logTime();
//					eat();
//				}

			}
			lastAnimation = getNpcs().closest(ZulrahIDS).getAnimation();
			// vengeance on blue when range projectile is being thrown
			if (Vengeance == true) {
				if (Color == "blue" && deltaTime(lastVenge) > 30000) {
					for (Projectile p : projectiles.getAll()) {
						if (p.getId() == 1044) { // Range projectile
							getTabs().open(Tab.MAGIC);
							getMagic().castSpell(Spells.LunarSpells.VENGEANCE);
							log("Casting Vengeance");
							lastVenge = logTime();
							IsAttacking = false;
							try {
								Thread.sleep(random(75, 100));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							getTabs().open(Tab.INVENTORY);
						}
					}
				}
			}
			if (getNpcs().closest(ZulrahIDS) != null) {
				if (getNpcs().closest(ZulrahIDS).getAnimation() == 5071
						|| getNpcs().closest(ZulrahIDS).getAnimation() == 5072
						|| getNpcs().closest(ZulrahIDS).getAnimation() == 5073) {// debug
																					// premove
					if (Next == "blue" && getPrayer().isActivated(PrayerButton.PROTECT_FROM_MAGIC) == false) {
						getPrayer().open();
						getPrayer().set(PrayerButton.PROTECT_FROM_MAGIC, true);
						if (Rigour) {
							getPrayer().set(PrayerButton.RIGOUR, true);
						} else {
							getPrayer().set(PrayerButton.EAGLE_EYE, true);
						}
						log("Praying Magic");
						getTabs().open(Tab.INVENTORY);
						GearSwap(Range_gear);
					}
					if (Next == "red") {
						if (Augury) {
							if (getPrayer().isActivated(PrayerButton.AUGURY) != true) {
								getPrayer().open();
								getPrayer().deactivateAll();
								getPrayer().set(PrayerButton.AUGURY, true);
								getTabs().open(Tab.INVENTORY);
							}
						} else {
							if (getPrayer().isActivated(PrayerButton.MYSTIC_MIGHT) != true) {
								getPrayer().open();
								getPrayer().deactivateAll();
								getPrayer().set(PrayerButton.MYSTIC_MIGHT, true);
								getTabs().open(Tab.INVENTORY);
							}
						}
						GearSwap(Magic_gear);

						// GearSwap(MWeapon);

					}

					if (Next == "green" && getPrayer().isActivated(PrayerButton.PROTECT_FROM_MISSILES) == false) {
						getPrayer().open();
						getPrayer().set(PrayerButton.PROTECT_FROM_MISSILES, true);

						if (Augury) {
							getPrayer().set(PrayerButton.AUGURY, true);
						} else {
							getPrayer().set(PrayerButton.MYSTIC_MIGHT, true);
						}
						log("Praying Range");
						getTabs().open(Tab.INVENTORY);
						GearSwap(Magic_gear);

						// GearSwap(MWeapon);

					}
					if (Next == "jad1") {
						getPrayer().open();
						getPrayer().set(PrayerButton.PROTECT_FROM_MISSILES, true);
						if (Augury) {
							getPrayer().set(PrayerButton.AUGURY, true);
						} else {
							getPrayer().set(PrayerButton.MYSTIC_MIGHT, true);
						}
						log("Praying Range");
						getTabs().open(Tab.INVENTORY);
						GearSwap(Magic_gear);

						// GearSwap(MWeapon);

					}
					if (Next == "jad2") {
						getPrayer().open();
						getPrayer().set(PrayerButton.PROTECT_FROM_MAGIC, true);
						if (Augury) {
							getPrayer().set(PrayerButton.AUGURY, true);
						} else {
							getPrayer().set(PrayerButton.MYSTIC_MIGHT, true);
						}
						log("Praying Magic");
						getTabs().open(Tab.INVENTORY);
						GearSwap(Magic_gear);

						// GearSwap(MWeapon);

					}
					if (getSkills().getDynamic(Skill.HITPOINTS) < 80 && deltaTime(lastEat) > 500) {
						lastEat = logTime();
						eat();
					}
					if (myPlayer().getPosition().equals(End) == false) {
						if (End.isVisible(bot) == false) {
							camCam.toPosition(End);
							camCam.moveYaw(random(-30, 30));
						}
						End.hover(bot);
						End.interact(bot, "Walk here");
						IsAttacking = false;
					}
					while (getNpcs().closest(ZulrahIDS).getAnimation() == 5071
							|| getNpcs().closest(ZulrahIDS).getAnimation() == 5072
							|| getNpcs().closest(ZulrahIDS).getAnimation() == 5073) {

					}
					break;
				}
			}
			dTime = deltaTime(logTime);
			if (getSkills().getDynamic(Skill.HITPOINTS) < 55 && deltaTime(lastEat) > 500) {
				lastEat = logTime();
				eat();
				IsAttacking = false;
			}
			if (getSkills().getDynamic(Skill.PRAYER) <= 25) {
				potionHandler("Prayer potion", "Drink");
				IsAttacking = false;
			}

			DEnd = End;
			NPC Zulrah = getNpcs().closest(ZulrahIDS);
			if (Zulrah != null) {
				if (Zulrah.exists()) {
					if (((((deltaTime(lastAttack) > 1500 && Zulrah.isAttackable()) && (Zulrah.isUnderAttack() == false))
							|| (deltaTime(lastAttack) > 1000 && IsAttacking == false && Zulrah.isAttackable()))
							|| (myPlayer().getAnimation() != 1167 && deltaTime(lastAttack) > 75
									&& IsAttacking == false))
							&& Color != "blue") {
						if (Zulrah.isOnScreen() == false) {
							camCam.toEntity(Zulrah);
						}
						if (Zulrah != null && Zulrah.getHealthPercent() > 0) {
							Zulrah.hover();
							Zulrah.interact("Attack");
						}
						IsAttacking = true;
						log("Attacking Zulrah");
						lastAttack = logTime();
					}
					if (((((deltaTime(lastAttack) > 1250 && Zulrah.isAttackable()) && (Zulrah.isUnderAttack() == false))
							|| (deltaTime(lastAttack) > 1250 && IsAttacking == false && Zulrah.isAttackable())))
							&& Color == "blue") {
						if (Zulrah.isOnScreen() == false) {
							camCam.toEntity(Zulrah);
						}
						Zulrah.hover();
						if (Zulrah != null && Zulrah.getHealthPercent() > 0) {
							Zulrah.interact("Attack");
						}
						IsAttacking = true;
						log("Attacking Zulrah");
						lastAttack = logTime();

					}
				}

			}
			if (phase > 13) {
				return;
			}
			// Zulrah Phase Timing Log Debug

			//

			if (myPlayer().getPosition().equals(End) == false && deltaTime(lastAttack) > 75
					&& deltaTime(lastMove) > 150) {
				if (!End.isVisible(bot)) {
					camCam.toPosition(End);
					camCam.moveYaw(random(-30,30));
				}
				End.hover(bot);
				End.interact(bot, "Walk here");
				lastMove = logTime();
				IsAttacking = false;
			}

		}

	}

	public long logTime() {
		return System.currentTimeMillis();
	}

	public long deltaTime(long logTime) {
		Long deltaTime = System.currentTimeMillis() - logTime;
		return deltaTime;
	}

	public void collectLoot() {
		log("Collecting Loot");
		getPrayer().open();
		getPrayer().deactivateAll();
		try {
			Thread.sleep(random(1500, 1700));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Thread.sleep(random(1000, 1200));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getTabs().open(Tab.INVENTORY);
		for (GroundItem i : groundItems.getAll()) {
			if (i != null && i.getId() != 11701) {
				if (getInventory().isFull()) {
					getTabs().open(Tab.INVENTORY);
					getInventory().interact("Eat", food);
					try {
						Thread.sleep(random(500, 700));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				i.interact("Take");
				lootedItems[count++] = i.getId();
				if (ItemDefinition.forId(i.getId()) != null && ItemDefinition.forId(i.getId()).isNoted()) {
					totalGoldEarned += i.getAmount() * getPrice(ItemDefinition.forId(i.getId()).getUnnotedId());
					;
				} else {
					totalGoldEarned += i.getAmount() * getPrice(i.getId());
				}
				while (myPlayer().isMoving()) {

				}
			}

			// allLootedItems[i.getId()] += * i.getAmount();
			try {
				Thread.sleep(random(1200, 1600));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void potionHandler(String potionName, String potionInteraction) {
		for (int i = 1; i <= 4; i++) {
			if (this.inventory.contains(potionName + "(" + i + ")")) {
				getTabs().open(Tab.INVENTORY);
				this.inventory.interact(this.inventory.getSlot(potionName + "(" + i + ")"), potionInteraction);
				break;
			}
		}
	}

	public void jeweleryHandler(String JewleryName, String Interaction) {
		for (int i = 1; i <= 8; i++) {
			if (this.inventory.contains(JewleryName + "(" + i + ")")) {
				getTabs().open(Tab.INVENTORY);
				this.inventory.interact(this.inventory.getSlot(JewleryName + "(" + i + ")"), Interaction);
				break;
			}
		}
	}

	public void startKill() {
		if (!CheckCharacter()) {
			stop(false);
			return;
		}
		getTabs().open(Tab.INVENTORY);
		if (RangedPotion) {
			potionHandler("Ranging potion", "Drink");
			try {
				Thread.sleep(random(2500, 3000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (AntiVenom) {
			potionHandler("Anti-venom+", "Drink");
			try {
				Thread.sleep(random(2000, 2500));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ImbuedHeart) {
			getInventory().interact("Invigorate", IH);
			try {
				Thread.sleep(random(1000, 1200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		GearSwap(Magic_gear);

		// GearSwap(MWeapon);

		try {
			Thread.sleep(random(1000, 1200));
		} catch (InterruptedException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		while (getSkills().getDynamic(Skill.HITPOINTS) < 80) {
			eat();
			try {
				Thread.sleep(random(700, 900));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (getSkills().getDynamic(Skill.PRAYER) < 50) {
			potionHandler("Prayer potion", "Drink");
			try {
				Thread.sleep(random(700, 900));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Position Mid3 = new Position(2204 + random(-1, 1), 3056 + random(-1, 1), 0);

		while (myPlayer().getPosition().distance(Mid3) > 2) {
			if (!myPlayer().isMoving()) {
				camCam.toPosition(Mid3);
				Mid3.interact(bot, "Walk here");
				try {
					Thread.sleep(random(1000, 1200));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		try {
			Thread.sleep(random(1000, 1200));
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int attempts = 0;
		while (getObjects().closest("Sacrificial boat") != null
				&& myPlayer().getPosition().distance(getObjects().closest("Sacrificial boat").getPosition()) < 50) {
			camCam.toEntity(getObjects().closest("Sacrificial boat"));
			camCam.toTop();
			if (attempts > 2) {
				stop(false);
				break;
			}
			attempts++;

			try {
				Thread.sleep(random(1500, 1700));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (getObjects().closest("Sacrificial boat") != null) {
				getObjects().closest("Sacrificial boat").interact("Board");
			}
			log("Boarding Boat");
			try {
				Thread.sleep(random(5000, 5200));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(random(2000, 2500));
				dialogues.selectOption(1);
				log("Entering instance");
				Thread.sleep(random(4000, 4500));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			Thread.sleep(random(3000, 3500));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean checkDeath() {
		try {
			Thread.sleep(random(4000, 4200));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (myPlayer().getPosition().distance(lumbridgeCastle) < 25) {
			numOfDeaths++;
			log("You Probably Died");
			// walk to lumb bank
			try {
				Thread.sleep(random(2000, 2200));// death buffer
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			camCam.toPosition(lumbridgeCastleMid);
			camCam.toTop();
			lumbridgeCastleMid.interact(bot, "Walk here");
			while (myPlayer().getPosition().distance(lumbridgeCastleMid) > 2) {
				if (myPlayer().isMoving() == false) {
					lumbridgeCastleMid.interact(bot, "Walk here");
				}
			}
			getObjects().closest(16671).interact("Climb-up");
			while (myPlayer().getPosition().distance(getObjects().closest(16671)) > 2) {
				if (myPlayer().isMoving() == false && getObjects().closest(16671) != null) {
					getObjects().closest(16671).interact("Climb-up");
				}
			}
			try {
				Thread.sleep(random(2000, 2200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getObjects().closest(16672).interact("Climb-up");
			try {
				Thread.sleep(random(2000, 2200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			camCam.toPosition(getObjects().closest(18491).getPosition());
			while (myPlayer().getPosition().distance(getObjects().closest(18491)) > 2) {
				if (myPlayer().isMoving() == false && getObjects().closest(18491) != null) {
					getObjects().closest(18491).interact("Bank");
				}
			}
			try {
				Thread.sleep(random(2000, 2200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// grab one zul-andra tele
			getBank().withdraw(12938, 1);
			try {
				Thread.sleep(random(1000, 1200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getBank().close();
			recollectGear();
			return true;
		}
		if (myPlayer().getPosition().distance(clanWars) < 30) {
			numOfDeaths++;
			log("You Probably Died");
			while (myPlayer().getPosition().distance(getObjects().closest(26707)) > 2) {
				if (myPlayer().isMoving() == false) {
					getObjects().closest(26707).interact("Use");
				}
			}
			try {
				Thread.sleep(random(2000, 2200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// grab one zul-andra tele
			getBank().withdraw(12938, 1);
			try {
				Thread.sleep(random(1000, 1200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getBank().close();
			recollectGear();
			return true;
		}
		return false;
	}

	public void equipstartinggear() {
		GearSwap(Magic_gear);

		// GearSwap(MWeapon);

	}

	public boolean GearSwap(int GearId[]) {
		log("Swapping Gear");
		for (int id : GearId) {
			if (getInventory().getItem(id) != null) {
				getInventory().getItem(id).hover();
				mouse.click(false);
			}
			// getInventory().interact("Wear", id);
			// getInventory().interact("Wield", id);
		}
		return false;

	}

	public boolean GearSwap(int Gear) {
		log("Swapping Gear");
		if (getInventory().getItem(Gear) != null) {
			getInventory().getItem(Gear).hover();
			mouse.click(false);
		}
		return false;

	}

	public void scanGear() {
		autoScan = true;
		// set mage gear
		getEquipment().openTab();
		Magic_gear = new int[20];
		// log("Mage Gear");
		int count = 0;
		boolean skipAhrims = false;
		if (getEquipment().isWearingItem(EquipmentSlot.SHIELD)) {
			oneLessShark = true;
		}
		for (Item i : getEquipment().getItems()) {
			if (i != null) {
				if (i.getId() == 22292) { // MWeapon1 == 22292 , MWeapon1 == 12899 , (MWeapon1 == 11905 || MWeapon2 ==
											// 11907)
					MWeapon1 = 22292;
				}
				if (i.getId() == 12899) {
					MWeapon1 = 12899;
				}
				if (i.getId() == 11905 || i.getId() == 11907) {
					MWeapon1 = 11905;
					MWeapon2 = 11907;
					Magic_gear[count++] = 11905;
					Magic_gear[count++] = 11907;
				}
			}
			if (i != null && i.getName().startsWith("Ahrim") && skipAhrims == false) {
				Magic_gear[count++] = ATop;
				Magic_gear[count++] = ATop2;
				Magic_gear[count++] = ABot;
				Magic_gear[count++] = ABot2;
				skipAhrims = true;
			}
			if (i != null && i.getName().contains("Void")) {
				AntiVenom = true;
			}
			if (i != null && !i.getName().contains("Ahrim")) {
				// log(i.getName());
				Magic_gear[count++] = i.getId();
			}
		}
		// set ranged gear
		getTabs().open(Tab.INVENTORY);
		Range_gear = new int[20];
		// log("Range Gear");
		int count2 = 0;
		for (Item i : getInventory().getItems()) {
			if (i != null && i.getId() != 11235 && (i.hasAction("Wield") || i.hasAction("Wear"))
					&& !i.getName().contains("Ring of dueling")) {
				// log(i.getName());
				Range_gear[count2++] = i.getId();
			}
			if (i != null && i.hasAction("Eat")) {
				food = i.getId();
			}
		}
		// set other options
		if (getInventory().getAmount(20724) > 0) {
			ImbuedHeart = true;
		} else {
			ImbuedHeart = false;
		}
		if (getInventory().getAmount(11235) > 0) {
			DBow = true;
		} else {
			DBow = false;
		}
		if (getInventory().getAmount(12791) > 0) {// check for runepouch
			RP = true;
		} else {
			RP = false;
		}
		if (RP == true) {
			rune1 = Zulrah.RunePouch.getName(getConfigs(), Slot.FIRST);
			rune2 = Zulrah.RunePouch.getName(getConfigs(), Slot.SECOND);
			rune3 = Zulrah.RunePouch.getName(getConfigs(), Slot.THIRD);
		}
		numOfPPots = (int) getInventory().getAmount(2434);
		for (int i = 1; i < 5; i++) {
			if (getInventory().getAmount("Ranging potion(" + i + ")") > 0) {
				RangedPotion = true;
				break;
			} else {
				RangedPotion = false;
			}
		}
		if (this.inventory.getAmount("Anti-venom+(1)") < 1 || this.inventory.getAmount("Anti-venom+(2)") < 1
				|| this.inventory.getAmount("Anti-venom+(3)") < 1 || this.inventory.getAmount("Anti-venom+(4)") < 1) {
			AntiVenom = true;
		}

	}

	public void recollectGear() { // checkDeath takes your from the death loc to the bank to get a zul-andra tele
		getTabs().open(Tab.INVENTORY);
		getInventory().interact("Teleport", 12938);
		while (myPlayer().getAnimation() != 3864) {

		}
		try {
			Thread.sleep(random(3500, 3700));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Position Mid3 = new Position(2204 + random(-1, 1), 3056 + random(-1, 1), 0);
		camCam.toPosition(Mid3);
		Mid3.interact(bot, "Walk here");
		try {
			Thread.sleep(random(2000, 2200));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camCam.toEntity(getObjects().closest(2124));
		try {
			Thread.sleep(random(2000, 2200));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		npcs.closest("Priestess Zul-Gwenwynig").interact("Collect");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		equipstartinggear();
		inventory.dropAll(food);
		try {
			Thread.sleep(random(3000, 3200));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		npcs.closest("Priestess Zul-Gwenwynig").interact("Collect");
		try {
			Thread.sleep(random(2000, 2200));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (RP) {
			getInventory().interact("Use", rune1);
			getInventory().interact("Use", 12791);
			try {
				Thread.sleep(random(200, 450));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getInventory().interact("Use", rune2);
			getInventory().interact("Use", 12791);
			try {
				Thread.sleep(random(200, 450));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getInventory().interact("Use", rune3);
			getInventory().interact("Use", 12791);
			try {
				Thread.sleep(random(200, 450));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(random(2000, 2200));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (GroundItem i : groundItems.getAll()) {
			if (inventory.isFull()) {
				break;
			}
			if (i.getId() == food) {
				if (i.isVisible() != true) {
					camCam.toEntity(i);
				}
				i.interact("Take");
				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			Thread.sleep(random(2000, 2500));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		regear();
	}

	private static int getPrice(int i) {
		Optional<Integer> price = Optional.empty();

		try {
			URL url = new URL("http://services.runescape.com/m=itemdb_oldschool/Dark_bow/viewitem?obj=" + i);
			URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			con.setUseCaches(true);
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			for (int x = 0; x < 100; x++) {
				String data = br.readLine();
				if (data.contains("<h3>Current Guide Price <span title=")) {
					data = data.replace("<h3>Current Guide Price <span title", "");
					int start = data.indexOf("=") + 2;
					int end = data.indexOf(">") - 1;
					data = data.substring(start, end);
					data = data.replace(",", "");
					return Integer.parseInt(data);
				}

			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void refillGear() { // after regear check SerpHelm, Trident, Blowpipe, RSuff
		boolean refillBP = false;
		boolean refillTrident = false;
		boolean refillSHelm = false;
		String typeOfDarts = null;
		int numOfDarts = 0;
		int numOfScales = 0;
		// blowpipe
		if (getInventory().getAmount(BP) > 0) {
			getInventory().interact("Check", BP);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sBlowpipe = chatbox.getMessages(MessageType.GAME).get(0);
			log(sBlowpipe);
			typeOfDarts = sBlowpipe.substring(7, sBlowpipe.indexOf(" ", 7)) + " dart"; // type of dart
			log(typeOfDarts);
			numOfDarts = Integer.parseInt(
					sBlowpipe.substring(sBlowpipe.lastIndexOf(" ", sBlowpipe.indexOf(".")) + 1, sBlowpipe.indexOf("."))
							.replace(",", ""));// num
			// of
			// darts
			numOfScales = Integer
					.parseInt(sBlowpipe.substring(sBlowpipe.lastIndexOf(" ", sBlowpipe.lastIndexOf(" ") - 1) + 1,
							sBlowpipe.lastIndexOf(" ")).replace(",", ""));// num
			log(numOfScales); // of
			// scales

			if (numOfDarts < 600 || numOfScales < 600) {
				refillBP = true;
			}
		}
		// trident
		if (getEquipment().getAmount(22292) > 0 || getEquipment().getAmount(11905) > 0
				|| getEquipment().getAmount(11907) > 0 || getEquipment().getAmount(12899) > 0) {
			getEquipment().interact(EquipmentSlot.WEAPON, "Check");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sTrident = chatbox.getMessages(MessageType.GAME).get(0);
			int tCharges = Integer.parseInt(sTrident.substring(16, sTrident.lastIndexOf(" ")).replace(",", ""));
			log(tCharges);
			if (tCharges < 600) {
				refillTrident = true;
			}
		}
		// serp helm
		if (getEquipment().getAmount(12931) > 0) {
			getEquipment().interact(EquipmentSlot.HAT, "Check");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sSHelm = chatbox.getMessages(MessageType.GAME).get(0);
			int sHelmCharges = Integer.parseInt(sSHelm.substring(sSHelm.indexOf(" ") + 1, sSHelm.indexOf("."))) * 110;
			log(sHelmCharges);
			if (sHelmCharges < 2500) {
				refillSHelm = true;
			}
		}
		// take from bank
		if (refillSHelm || refillTrident || refillBP) {
			try {
				getBank().open();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getBank().depositAll(food);
			getBank().close();
			if (refillSHelm) {
				log("refilling helm");
				getEquipment().interact(EquipmentSlot.HAT, "Remove");
				try {
					getBank().open();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getBank().withdraw(12934, 2500);
				getBank().close();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getTabs().open(Tab.INVENTORY);
				getInventory().interact("Use", 12934);
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getInventory().interact("Use", 12931);
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getInventory().interact("Wear", 12931);
				log("refilling helm");
			}
			if (refillTrident) {
				log("refilling trident");
				getEquipment().interact(EquipmentSlot.WEAPON, "Remove");
				try {
					getBank().open();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (MWeapon1 == 22292 || MWeapon1 == 12899) {
					getBank().withdraw(12934, 600);
				}

				if (MWeapon1 == 11905 || MWeapon2 == 11907) {
					getBank().withdraw(995, 6000);
				}
				getBank().withdraw(560, 600);
				getBank().withdraw(562, 600);
				getBank().withdraw(554, 600 * 5);
				getBank().close();
				getTabs().open(Tab.INVENTORY);
				if (MWeapon1 == 22292 || MWeapon1 == 12899) { // MWeapon1 == 22292 || MWeapon1 == 12899 && MWeapon1 ==
																// 11905 || MWeapon2 == 11907
					getInventory().interact("Use", MWeapon1);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (MWeapon1 == 11905 || MWeapon2 == 11907) {
					getInventory().interact("Use", 995);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (MWeapon1 == 22292 || MWeapon1 == 12899) {
					getInventory().interact("Use", MWeapon1);
				}
				if (MWeapon1 == 11905 || MWeapon2 == 11907) {
					getInventory().interact("Use", MWeapon2);
				}
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (MWeapon1 == 22292 || MWeapon1 == 12899) {
					getInventory().interact("Wield", MWeapon1);
				}
				if (MWeapon1 == 11905 || MWeapon2 == 11907) {
					getInventory().interact("Wield", MWeapon2);
				}
				log("refilling trident");
			}
			if (refillBP) {
				log("refilling blowpipe");
				try {
					getBank().open();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (numOfScales < 600) {
					getBank().withdraw(12934, 1200);
				}
				if (numOfDarts < 600) {
					getBank().withdraw(typeOfDarts, 600);
				}
				getBank().close();
				getTabs().open(Tab.INVENTORY);
				if (numOfScales < 600) {
					getInventory().interact("Use", 12934);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getInventory().interact("Use", 12926);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (numOfDarts < 600) {
					getInventory().interact("Use", typeOfDarts);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getInventory().interact("Use", 12926);
				}
				log("refilling blowpipe");
			}
			try {
				getBank().open();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getBank().withdrawAll(food);
			getBank().close();
		}

	}

	public final String formatTime(final long ms) {
		long s = ms / 1000, m = s / 60, h = m / 60;
		s %= 60;
		m %= 60;
		h %= 24;
		return String.format("%02d:%02d:%02d", h, m, s);
	}

	public static String PerHour(float thing, float timems, int decimals) {
		double PerH = thing / (timems / 60 / 60 / 1000);
		String Formatted = String.format("%." + decimals + "f", PerH);
		return Formatted;
	}
}