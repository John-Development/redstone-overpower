package net.redstoneOverpower.utils;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.redstoneOverpower.block.LogicalComparatorBlock;
import net.redstoneOverpower.block.SculkChamberBlock;
import net.redstoneOverpower.block.entity.CopperHopperBlockEntity;
import net.redstoneOverpower.block.entity.LogicalComparatorBlockEntity;
import net.redstoneOverpower.block.entity.SculkChamberBlockEntity;
import net.redstoneOverpower.block.screen.CopperHopperScreenHandler;

import static net.redstoneOverpower.RedstoneOverpower.MOD_ID;
import static net.redstoneOverpower.utils.CopperHopperVariants.*;

public class Initialiser {

  public static final LogicalComparatorBlock LOGICAL_COMPARATOR_BLOCK = new LogicalComparatorBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR));
  public static final SculkChamberBlock SCULK_CHAMBER_BLOCK = new SculkChamberBlock(FabricBlockSettings.create().strength(1.0f));

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

  public static final BlockEntityType<SculkChamberBlockEntity> SCULK_CHAMBER_BLOCK_ENTITY = Registry.register(
    Registries.BLOCK_ENTITY_TYPE,
    new Identifier(MOD_ID, "sculk_chamber_block_entity"),
    FabricBlockEntityTypeBuilder.create(SculkChamberBlockEntity::new, SCULK_CHAMBER_BLOCK).build()
  );

  public static final ScreenHandlerType<CopperHopperScreenHandler> COPPER_HOPPER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
    new Identifier(MOD_ID, "copper_hopper"),
    CopperHopperScreenHandler::new
  );

  private static void registerBlockItem(String path, Block block) {
    Registry.register(Registries.BLOCK, new Identifier(MOD_ID, path), block);
    Registry.register(Registries.ITEM, new Identifier(MOD_ID, path), new BlockItem(block, new FabricItemSettings()));
  }

  private static <T extends BlockEntity> BlockEntityType<? extends T> registerBlockEntityItem(
    String path,
    Block block,
    FabricBlockEntityTypeBuilder.Factory<? extends T> factory
  ) {
    registerBlockItem(path, block);

    return Registry.register(
      Registries.BLOCK_ENTITY_TYPE,
      new Identifier(MOD_ID, path),
      FabricBlockEntityTypeBuilder.create(factory, block).build()
    );
  }

  public static void initBlockItems() {
    registerBlockItem("copper_hopper", UNAFFECTED_COPPER_HOPPER_BLOCK);
    registerBlockItem("weathered_copper_hopper", WEATHERED_COPPER_HOPPER_BLOCK);
    registerBlockItem("oxidized_copper_hopper", OXIDIZED_COPPER_HOPPER_BLOCK);
    registerBlockItem("exposed_copper_hopper", EXPOSED_COPPER_HOPPER_BLOCK);
    registerBlockItem("waxed_copper_hopper", UNAFFECTED_WAXED_COPPER_HOPPER_BLOCK);
    registerBlockItem("waxed_weathered_copper_hopper", WEATHERED_WAXED_COPPER_HOPPER_BLOCK);
    registerBlockItem("waxed_oxidized_copper_hopper", OXIDIZED_WAXED_COPPER_HOPPER_BLOCK);
    registerBlockItem("waxed_exposed_copper_hopper", EXPOSED_WAXED_COPPER_HOPPER_BLOCK);
    registerBlockEntityItem("logical_comparator", LOGICAL_COMPARATOR_BLOCK, LogicalComparatorBlockEntity::new);
    registerBlockEntityItem("sculk_chamber", SCULK_CHAMBER_BLOCK, SculkChamberBlockEntity::new);
  }

  public static void initOxidizableChains() {
    OxidizableBlocksRegistry.registerOxidizableBlockPair(UNAFFECTED_COPPER_HOPPER_BLOCK, EXPOSED_COPPER_HOPPER_BLOCK);
    OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_COPPER_HOPPER_BLOCK, WEATHERED_COPPER_HOPPER_BLOCK);
    OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_COPPER_HOPPER_BLOCK, OXIDIZED_COPPER_HOPPER_BLOCK);
    OxidizableBlocksRegistry.registerWaxableBlockPair(UNAFFECTED_COPPER_HOPPER_BLOCK, UNAFFECTED_WAXED_COPPER_HOPPER_BLOCK);
    OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_COPPER_HOPPER_BLOCK, EXPOSED_WAXED_COPPER_HOPPER_BLOCK);
    OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_COPPER_HOPPER_BLOCK, WEATHERED_WAXED_COPPER_HOPPER_BLOCK);
    OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_COPPER_HOPPER_BLOCK, OXIDIZED_WAXED_COPPER_HOPPER_BLOCK);
  }

  public static void initCreativePlacement() {
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
      content.addAfter(Items.COMPARATOR, LOGICAL_COMPARATOR_BLOCK);
      content.addAfter(LOGICAL_COMPARATOR_BLOCK, SCULK_CHAMBER_BLOCK);
      content.addAfter(Items.HOPPER, UNAFFECTED_COPPER_HOPPER_BLOCK);
      content.addAfter(UNAFFECTED_COPPER_HOPPER_BLOCK, UNAFFECTED_WAXED_COPPER_HOPPER_BLOCK);
      content.addAfter(UNAFFECTED_WAXED_COPPER_HOPPER_BLOCK, EXPOSED_COPPER_HOPPER_BLOCK);
      content.addAfter(EXPOSED_COPPER_HOPPER_BLOCK, EXPOSED_WAXED_COPPER_HOPPER_BLOCK);
      content.addAfter(EXPOSED_WAXED_COPPER_HOPPER_BLOCK, WEATHERED_COPPER_HOPPER_BLOCK);
      content.addAfter(WEATHERED_COPPER_HOPPER_BLOCK, WEATHERED_WAXED_COPPER_HOPPER_BLOCK);
      content.addAfter(WEATHERED_WAXED_COPPER_HOPPER_BLOCK, OXIDIZED_COPPER_HOPPER_BLOCK);
      content.addAfter(OXIDIZED_COPPER_HOPPER_BLOCK, OXIDIZED_WAXED_COPPER_HOPPER_BLOCK);
    });
  }
}
