package net.redstoneOverpower.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum LogicalComparatorMode implements StringIdentifiable {
        AND("and"),
        OR("or"),
        XOR("xor"),
        NAND("nand"),
        NOR("nor"),
        XNOR("xnor");


        private final String name;

        LogicalComparatorMode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String asString() {
            return this.name;
        }
}
