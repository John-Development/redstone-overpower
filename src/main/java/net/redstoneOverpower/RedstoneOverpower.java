package net.redstoneOverpower;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.redstoneOverpower.block.entity.LogicalComparatorBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.redstoneOverpower.block.entity.CopperHopperBlockEntity;
import net.redstoneOverpower.block.screen.CopperHopperScreenHandler;

import static net.redstoneOverpower.utils.CopperHopperVariants.UNAFFECTED_COPPER_HOPPER_BLOCK;
import static net.redstoneOverpower.utils.Initialiser.*;

public class RedstoneOverpower implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "redstoneoverpower";

	public static BlockEntityType<CopperHopperBlockEntity> COPPER_HOPPER_BLOCK_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		new Identifier(MOD_ID, "copper_hopper"),
		FabricBlockEntityTypeBuilder.create(CopperHopperBlockEntity::new, UNAFFECTED_COPPER_HOPPER_BLOCK).build()
	);

	public static final BlockEntityType<LogicalComparatorBlockEntity> LOGICAL_COMPARATOR_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		new Identifier(MOD_ID, "logical_comparator_block_entity"),
		FabricBlockEntityTypeBuilder.create(LogicalComparatorBlockEntity::new, LOGICAL_COMPARATOR_BLOCK).build()
	);

	public static final ScreenHandlerType<CopperHopperScreenHandler> COPPER_HOPPER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
		new Identifier(MOD_ID, "copper_hopper"),
		CopperHopperScreenHandler::new
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		initBlockItems();
		initCreativePlacement();
		initOxidizableChains();
	}
}