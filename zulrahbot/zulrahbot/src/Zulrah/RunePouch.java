package Zulrah;

import org.osbot.rs07.api.Configs;

public class RunePouch {

	public static int getRuneId(final Configs configs, final Slot slot) {
        final int c = configs.get(slot.configRuneName);
        switch (slot) {
            case FIRST:     return c & 0x3F;
            case SECOND:    return c >>> 6 & 0x3F;
            case THIRD:     return c >>> 12 & 0x3F;
            default:        return 0;
        }
    }

    public static int getAmount(final Configs configs, final Slot slot) {
        final int c = configs.get(slot.configRuneAmount);
        switch (slot) {
            case FIRST:     return c >>> 18;
            case SECOND:    return c & 0x3FFF;
            case THIRD:     return c >>> 14;
            default:        return 0;
        }
    }

    public static String getName(final Configs configs, final Slot slot) {
        switch (RunePouch.getRuneId(configs, slot)) {
            case 1:     return "Air rune";
            case 2:     return "Water rune";
            case 3:     return "Earth rune";
            case 4:     return "Fire rune";
            case 5:     return "Mind rune";
            case 6:     return "Chaos rune";
            case 7:     return "Death rune";
            case 8:     return "Blood rune";
            case 9:     return "Cosmic rune";
            case 10:    return "Nature rune";
            case 11:    return "Law rune";
            case 12:    return "Body rune";
            case 13:    return "Soul rune";
            case 14:    return "Astral rune";
            case 15:    return "Mist rune";
            case 16:    return "Mud rune";
            case 17:    return "Dust rune";
            case 18:    return "Lava rune";
            case 19:    return "Steam rune";
            case 20:    return "Smoke rune";
            default:    return "None";
        }
    }

    public static int getAmount(final Configs configs, final String runeName) {
        for (final Slot slot : Slot.values()) {
            if (RunePouch.getName(configs, slot).equals(runeName)) {
                return getAmount(configs, slot);
            }
        }
        return 0;
    }


    public enum Slot {

        FIRST(1139, 1139),
        SECOND(1139, 1140),
        THIRD(1139, 1140);

    	public final int configRuneName;
    	public final int configRuneAmount;

        Slot(final int configRuneName, final int configRuneAmount) {
            this.configRuneName = configRuneName;
            this.configRuneAmount = configRuneAmount;
        }
    }
}



