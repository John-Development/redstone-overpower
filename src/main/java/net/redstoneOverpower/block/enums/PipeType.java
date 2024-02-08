package net.redstoneOverpower.block.enums;

import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public enum PipeType implements StringIdentifiable {
        NONE("none"),
        IN("input"),
        IN_HOPPER("input_hopper"),
        OUT("output");

        private final String name;

        PipeType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String asString() {
            return this.name;
        }

        @Nullable
        public static PipeType getOpposite(PipeType value) {
            if (value == PipeType.IN) {
                return PipeType.OUT;
            }
            if (value == PipeType.OUT) {
                return PipeType.IN;
            }

            return null;
        }
}
