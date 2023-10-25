package net.redstoneOverpower;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.redstoneOverpower.client.gui.screen.ingame.CopperHopperScreen;

import static net.redstoneOverpower.utils.Initialiser.COPPER_HOPPER_SCREEN_HANDLER;

public class RedstoneOverpowerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		HandledScreens.register(COPPER_HOPPER_SCREEN_HANDLER, CopperHopperScreen::new);
	}
}