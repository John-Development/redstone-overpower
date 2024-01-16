package net.redstoneOverpower;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.redstoneOverpower.client.gui.screen.ingame.CopperHopperScreen;

import static net.redstoneOverpower.utils.Initialiser.*;

public class RedstoneOverpowerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		HandledScreens.register(COPPER_HOPPER_SCREEN_HANDLER, CopperHopperScreen::new);

		BlockRenderLayerMap.INSTANCE.putBlock(LOGICAL_COMPARATOR_BLOCK, RenderLayer.getCutout());
	}
}