package net.redstoneoverpower.utils;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.redstoneoverpower.block.*;
import net.redstoneoverpower.block.entity.DuctBlockEntity;
import net.redstoneoverpower.block.entity.LogicalComparatorBlockEntity;
import net.redstoneoverpower.block.entity.SculkChamberBlockEntity;
import net.redstoneoverpower.block.screen.DuctScreenHandler;

import static net.redstoneoverpower.RedstoneOverpower.MOD_ID;

public class Initialiser {
  public static final LogicalComparatorBlock LOGICAL_COMPARATOR_BLOCK = new LogicalComparatorBlock(AbstractBlock.Settings.copy(Blocks.COMPARATOR));
  public static final SculkChamberBlock SCULK_CHAMBER_BLOCK = new SculkChamberBlock(AbstractBlock.Settings.create().strength(1.0f).nonOpaque());
  public static final SculkNoteBlock SCULK_NOTE_BLOCK = new SculkNoteBlock(AbstractBlock.Settings.copy(Blocks.NOTE_BLOCK));
  public static final SculkPulserBlock SCULK_PULSER_BLOCK = new SculkPulserBlock(AbstractBlock.Settings.create().strength(1.0f));
  public static final DuctBlock DUCT_BLOCK = new DuctBlock(AbstractBlock.Settings.create().strength(1.0f));

  public static BlockEntityType<LogicalComparatorBlockEntity> LOGICAL_COMPARATOR_BLOCK_ENTITY;
  public static BlockEntityType<SculkChamberBlockEntity> SCULK_CHAMBER_BLOCK_ENTITY;
  public static BlockEntityType<DuctBlockEntity> DUCT_BLOCK_ENTITY;

  public static final ScreenHandlerType<DuctScreenHandler> DUCT_SCREEN_HANDLER = new ScreenHandlerType<>(
    DuctScreenHandler::new,
    FeatureFlags.VANILLA_FEATURES
  );

  private static void registerBlockItem(String path, Block block) {
    Registry.register(Registries.BLOCK, new Identifier(MOD_ID, path), block);
    Registry.register(Registries.ITEM, new Identifier(MOD_ID, path), new BlockItem(block, new Item.Settings()));
  }

  private static <T extends BlockEntity> BlockEntityType<? extends T> registerBlockEntityItem(
    String path,
    Block block,
    BlockEntityType.BlockEntityFactory<? extends T> factory
  ) {
    registerBlockItem(path, block);

    return Registry.register(
      Registries.BLOCK_ENTITY_TYPE,
      new Identifier(MOD_ID, path),
      BlockEntityType.Builder.create(factory, block).build()
    );
  }

  public static void initBlockItems() {
    // Blocks
    registerBlockItem("sculk_note_block", SCULK_NOTE_BLOCK);
    registerBlockItem("sculk_pulser", SCULK_PULSER_BLOCK);

    // Block entities
    LOGICAL_COMPARATOR_BLOCK_ENTITY = (BlockEntityType<LogicalComparatorBlockEntity>) registerBlockEntityItem("logical_comparator", LOGICAL_COMPARATOR_BLOCK, LogicalComparatorBlockEntity::new);
    SCULK_CHAMBER_BLOCK_ENTITY = (BlockEntityType<SculkChamberBlockEntity>) registerBlockEntityItem("sculk_chamber", SCULK_CHAMBER_BLOCK, SculkChamberBlockEntity::new);
    DUCT_BLOCK_ENTITY = (BlockEntityType<DuctBlockEntity>) registerBlockEntityItem("duct", DUCT_BLOCK, DuctBlockEntity::new);

    // Screen handlers
    Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "duct"), DUCT_SCREEN_HANDLER);
  }

  public static void initCreativePlacement() {
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
      content.addAfter(Items.COMPARATOR, LOGICAL_COMPARATOR_BLOCK);
      content.addAfter(LOGICAL_COMPARATOR_BLOCK, SCULK_CHAMBER_BLOCK);
      content.addAfter(SCULK_CHAMBER_BLOCK, SCULK_NOTE_BLOCK);
      content.addAfter(SCULK_NOTE_BLOCK, SCULK_PULSER_BLOCK);
    });
  }
}
