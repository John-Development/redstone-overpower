package net.redstoneoverpower;

import net.fabricmc.api.ModInitializer;

import static net.redstoneoverpower.utils.Initialiser.*;

public class RedstoneOverpower implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "redstoneoverpower";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		initBlockItems();
		initCreativePlacement();
	}
}