package net.redstoneOverpower.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum SculkChamberMode implements StringIdentifiable {
    LISTEN("listen"),
    CHARGED("charged"),
    ISOLATED("isolated"),
    COOLDOWN("cooldown");

    private final String name;

    SculkChamberMode(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
