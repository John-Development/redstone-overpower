package net.redstoneOverpower;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.redstoneOverpower.block.LogicalComparatorBlock;
import net.redstoneOverpower.block.entity.LogicalComparatorBlockEntity;

public class RedstoneOverpower implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final LogicalComparatorBlock LOGICAL_COMPARATOR = new LogicalComparatorBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR));

	public static final BlockEntityType<LogicalComparatorBlockEntity> LOGICAL_COMPARATOR_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		new Identifier("redstoneoverpower", "logical_comparator_block_entity"),
		FabricBlockEntityTypeBuilder.create(LogicalComparatorBlockEntity::new, LOGICAL_COMPARATOR).build()
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registries.BLOCK, new Identifier("redstoneoverpower", "logical_comparator"), LOGICAL_COMPARATOR);
		Registry.register(Registries.ITEM, new Identifier("redstoneoverpower", "logical_comparator"), new BlockItem(LOGICAL_COMPARATOR, new FabricItemSettings()));
	}
}