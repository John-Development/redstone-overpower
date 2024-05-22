package net.redstoneoverpower;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.redstoneoverpower.client.gui.screen.ingame.DuctScreen;

import static net.redstoneoverpower.utils.Initialiser.*;

public class RedstoneOverpowerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		HandledScreens.register(DUCT_SCREEN_HANDLER, DuctScreen::new);

		BlockRenderLayerMap.INSTANCE.putBlock(LOGICAL_COMPARATOR_BLOCK, RenderLayer.getCutout());
	}
}