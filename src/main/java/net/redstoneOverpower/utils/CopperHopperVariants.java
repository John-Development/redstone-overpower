package net.redstoneOverpower.utils;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Oxidizable;
import net.minecraft.sound.BlockSoundGroup;
import net.redstoneOverpower.block.CopperHopperBlock;

public class CopperHopperVariants {
  public static final Block UNAFFECTED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.UNAFFECTED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
  public static final Block WEATHERED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.WEATHERED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
  public static final Block OXIDIZED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.OXIDIZED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
  public static final Block EXPOSED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.EXPOSED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
  public static final Block UNAFFECTED_WAXED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.UNAFFECTED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
  public static final Block WEATHERED_WAXED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.WEATHERED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
  public static final Block OXIDIZED_WAXED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.OXIDIZED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
  public static final Block EXPOSED_WAXED_COPPER_HOPPER_BLOCK = new CopperHopperBlock(Oxidizable.OxidationLevel.EXPOSED, FabricBlockSettings.copy(Blocks.HOPPER).sounds(BlockSoundGroup.METAL).hardness(2f).requiresTool());
}

