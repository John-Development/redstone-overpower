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
import net.minecraft.sound.SoundEvent;
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
  public static final SculkPulserBlock SCULK_PULSER_BLOCK = new SculkPulserBlock(AbstractBlock.Settings.create().strength(1.0f).nonOpaque());
  public static final DuctBlock DUCT_BLOCK = new DuctBlock(AbstractBlock.Settings.create().strength(1.0f));
  public static final BuddingSculkAmethystBlock BUDDING_SCULK_AMETHIST_BLOCK = new BuddingSculkAmethystBlock(AbstractBlock.Settings.copy(Blocks.BUDDING_AMETHYST));
  public static final SculkAmethystClusterBlock SCULK_AMETHIST_CLUSTER_BLOCK = new SculkAmethystClusterBlock(7.0f, 3.0f, AbstractBlock.Settings.copy(Blocks.AMETHYST_CLUSTER));
  public static final SculkAmethystClusterBlock LARGE_SCULK_AMETHIST_BUD_BLOCK = new SculkAmethystClusterBlock(5.0f, 3.0f, AbstractBlock.Settings.copy(Blocks.LARGE_AMETHYST_BUD));
  public static final SculkAmethystClusterBlock MEDIUM_SCULK_AMETHIST_BUD_BLOCK = new SculkAmethystClusterBlock(4.0f, 3.0f, AbstractBlock.Settings.copy(Blocks.MEDIUM_AMETHYST_BUD));
  public static final SculkAmethystClusterBlock SMALL_SCULK_AMETHIST_BUD_BLOCK = new SculkAmethystClusterBlock(3.0f, 4.0f, AbstractBlock.Settings.copy(Blocks.SMALL_AMETHYST_BUD));

  public static BlockEntityType<LogicalComparatorBlockEntity> LOGICAL_COMPARATOR_BLOCK_ENTITY;
  public static BlockEntityType<SculkChamberBlockEntity> SCULK_CHAMBER_BLOCK_ENTITY;
  public static BlockEntityType<DuctBlockEntity> DUCT_BLOCK_ENTITY;

  public static final Identifier NOTE_BLOCK_SOUND_ID = new Identifier("redstoneoverpower:sculk_note_block_sound");
  public static SoundEvent NOTE_BLOCK_SOUND_EVENT = SoundEvent.of(NOTE_BLOCK_SOUND_ID);

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
    registerBlockItem("budding_sculk_amethyst", BUDDING_SCULK_AMETHIST_BLOCK);
    registerBlockItem("sculk_amethyst_cluster", SCULK_AMETHIST_CLUSTER_BLOCK);
    registerBlockItem("large_sculk_amethyst_bud", LARGE_SCULK_AMETHIST_BUD_BLOCK);
    registerBlockItem("medium_sculk_amethyst_bud", MEDIUM_SCULK_AMETHIST_BUD_BLOCK);
    registerBlockItem("small_sculk_amethyst_bud", SMALL_SCULK_AMETHIST_BUD_BLOCK);

    // Block entities
    LOGICAL_COMPARATOR_BLOCK_ENTITY = (BlockEntityType<LogicalComparatorBlockEntity>) registerBlockEntityItem("logical_comparator", LOGICAL_COMPARATOR_BLOCK, LogicalComparatorBlockEntity::new);
    SCULK_CHAMBER_BLOCK_ENTITY = (BlockEntityType<SculkChamberBlockEntity>) registerBlockEntityItem("sculk_chamber", SCULK_CHAMBER_BLOCK, SculkChamberBlockEntity::new);
    DUCT_BLOCK_ENTITY = (BlockEntityType<DuctBlockEntity>) registerBlockEntityItem("duct", DUCT_BLOCK, DuctBlockEntity::new);

    // Screen handlers
    Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "duct"), DUCT_SCREEN_HANDLER);

    // Sounds
    Registry.register(Registries.SOUND_EVENT, NOTE_BLOCK_SOUND_ID, NOTE_BLOCK_SOUND_EVENT);
  }

  public static void initCreativePlacement() {
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
      content.addAfter(Items.COMPARATOR, LOGICAL_COMPARATOR_BLOCK);
      content.addAfter(LOGICAL_COMPARATOR_BLOCK, SCULK_CHAMBER_BLOCK);
      content.addAfter(SCULK_CHAMBER_BLOCK, SCULK_NOTE_BLOCK);
      content.addAfter(SCULK_NOTE_BLOCK, SCULK_PULSER_BLOCK);
    });
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
      content.addAfter(Items.SCULK_VEIN, BUDDING_SCULK_AMETHIST_BLOCK);
      content.addAfter(BUDDING_SCULK_AMETHIST_BLOCK, SCULK_AMETHIST_CLUSTER_BLOCK);
      content.addAfter(SCULK_AMETHIST_CLUSTER_BLOCK, LARGE_SCULK_AMETHIST_BUD_BLOCK);
      content.addAfter(LARGE_SCULK_AMETHIST_BUD_BLOCK, MEDIUM_SCULK_AMETHIST_BUD_BLOCK);
      content.addAfter(MEDIUM_SCULK_AMETHIST_BUD_BLOCK, SMALL_SCULK_AMETHIST_BUD_BLOCK);
    });
  }
}
