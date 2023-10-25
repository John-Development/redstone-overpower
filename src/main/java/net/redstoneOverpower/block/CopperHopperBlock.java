package net.redstoneOverpower.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.redstoneOverpower.block.entity.CopperHopperBlockEntity;
import org.jetbrains.annotations.Nullable;

import static net.redstoneOverpower.utils.Initialiser.COPPER_HOPPER_BLOCK_ENTITY;

public class CopperHopperBlock extends HopperBlock implements Oxidizable {
  private final OxidationLevel oxidationLevel;

  public CopperHopperBlock(OxidationLevel oxidationLevel, Settings settings) {
    super(settings);
    this.oxidationLevel = oxidationLevel;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new CopperHopperBlockEntity(pos, state);
  }

  @Override
  public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    this.tickDegradation(state, world, pos, random);
  }

  @Override
  public boolean hasRandomTicks(BlockState state) {
    return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
  }

  @Override
  public OxidationLevel getDegradationLevel() {
    return this.oxidationLevel;
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return world.isClient ? null : CopperHopperBlock.validateTicker(type, COPPER_HOPPER_BLOCK_ENTITY, CopperHopperBlockEntity::serverTick);
  }

  @Override
  public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
    BlockEntity blockEntity;
    if (itemStack.hasCustomName() && (blockEntity = world.getBlockEntity(pos)) instanceof CopperHopperBlockEntity) {
      ((CopperHopperBlockEntity)blockEntity).setCustomName(itemStack.getName());
    }
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (world.isClient) {
      return ActionResult.SUCCESS;
    }
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof CopperHopperBlockEntity) {
      player.openHandledScreen((CopperHopperBlockEntity)blockEntity);
      player.incrementStat(Stats.INSPECT_HOPPER);
    }
    return ActionResult.CONSUME;
  }

  @Override
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    if (state.isOf(newState.getBlock())) {
      return;
    }
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof CopperHopperBlockEntity) {
      ItemScatterer.spawn(world, pos, (CopperHopperBlockEntity)blockEntity);
      world.updateComparators(pos, this);
    }
    super.onStateReplaced(state, world, pos, newState, moved);
  }

  @Override
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof CopperHopperBlockEntity) {
      CopperHopperBlockEntity.onEntityCollided(world, pos, state, entity, (CopperHopperBlockEntity)blockEntity);
    }
  }
}
