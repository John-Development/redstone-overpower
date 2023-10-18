package net.redstoneOverpower.mixin.block.piston;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {
  @Shadow @Final
  private World world;
  @Shadow @Final
  private BlockPos posFrom;
  @Shadow @Final
  private Direction motionDirection;
  @Final @Shadow
  private final List<BlockPos> movedBlocks = new ArrayList<>();
  @Final @Shadow
  private final List<BlockPos> brokenBlocks = new ArrayList<>();

  @Shadow
  private static boolean isBlockSticky(BlockState state) {
    return false;
  }

  @Shadow
  protected abstract void setMovedBlocks(int from, int to);

  @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
  private static void isBlockStickyMixin(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if(state.isOf(Blocks.CHAIN)) {
      cir.setReturnValue(true);
      cir.cancel();
    }
  }

  @Unique
  private boolean canConnectChain(BlockState state, Direction dir) {
    System.out.println("state" + state);
    System.out.println("dir" + dir);

    return switch (dir) {
      case UP, DOWN -> state.get(Properties.AXIS) == Direction.Axis.Y;
      case NORTH, SOUTH -> state.get(Properties.AXIS) == Direction.Axis.Z;
      case WEST, EAST -> state.get(Properties.AXIS) == Direction.Axis.X;
    };
  }

  @Unique
  private boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState, Direction dir) {
    System.out.println("motiondir y dir " + motionDirection + " " + dir);
    if (state.isOf(Blocks.CHAIN) && adjacentState.isOf(Blocks.CHAIN) && dir.getAxis() == motionDirection.getAxis()) {
      return canConnectChain(state, motionDirection.getOpposite()) && canConnectChain(adjacentState, motionDirection);
    }
    if (state.isOf(Blocks.CHAIN) && !canConnectChain(state, dir)) {
      return false;
    }
    if (adjacentState.isOf(Blocks.CHAIN)
      && (!state.isOf(Blocks.SLIME_BLOCK) && !state.isOf(Blocks.HONEY_BLOCK) && !canConnectChain(adjacentState, dir.getOpposite()))
    ) {
      return false;
    }
    if (state.isOf(Blocks.HONEY_BLOCK) && adjacentState.isOf(Blocks.SLIME_BLOCK)) {
      return false;
    }
    if (state.isOf(Blocks.SLIME_BLOCK) && adjacentState.isOf(Blocks.HONEY_BLOCK)) {
      return false;
    }
    return PistonHandlerMixin.isBlockSticky(state) || PistonHandlerMixin.isBlockSticky(adjacentState);
  }

   /**
   * @author Juarrin
   * @reason different isAdjacentBlockStuck invocation
   */
  @Overwrite
  private boolean tryMoveAdjacentBlock(BlockPos pos) {
    BlockState blockState = this.world.getBlockState(pos);
    for (Direction direction : Direction.values()) {
      BlockPos blockPos;
      if (direction.getAxis() == this.motionDirection.getAxis()
        || !isAdjacentBlockStuck(blockState, this.world.getBlockState(blockPos = pos.offset(direction)), direction)
        || this.tryMove(blockPos, direction)
      ) continue;
      return false;
    }
    return true;
  }

  /**
   * @author Juarrin
   * @reason redo chain logic
   */
  @Overwrite
  private boolean tryMove(BlockPos pos, Direction dir) {
    int k;
    BlockState blockState = this.world.getBlockState(pos);
    if (blockState.isAir()) {
      return true;
    }
    if (!PistonBlock.isMovable(blockState, this.world, pos, this.motionDirection, false, dir)) {
      return true;
    }
    if (pos.equals(this.posFrom)) {
      return true;
    }
    if (this.movedBlocks.contains(pos)) {
      return true;
    }
    int i = 1;
    if (i + this.movedBlocks.size() > 12) {
      return false;
    }
    while (PistonHandlerMixin.isBlockSticky(blockState)) {
      BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
      BlockState blockState2 = blockState;
      blockState = this.world.getBlockState(blockPos);
      if (blockState.isAir()
        || !isAdjacentBlockStuck(blockState2, blockState, this.motionDirection)
        || !PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.getOpposite())
        || blockPos.equals(this.posFrom)
      ) {
        break;
      }
      if (++i + this.movedBlocks.size() <= 12) {
        continue;
      }
      return false;
    }
    int j = 0;
    for (k = i - 1; k >= 0; --k) {
      this.movedBlocks.add(pos.offset(this.motionDirection.getOpposite(), k));
      ++j;
    }
    k = 1;
    while (true) {
      BlockPos blockPos2;
      int l;
      if ((l = this.movedBlocks.indexOf(blockPos2 = pos.offset(this.motionDirection, k))) > -1) {
        this.setMovedBlocks(j, l);
        for (int m = 0; m <= l + j; ++m) {
          BlockPos blockPos3 = this.movedBlocks.get(m);
          if (!PistonHandlerMixin.isBlockSticky(this.world.getBlockState(blockPos3)) || this.tryMoveAdjacentBlock(blockPos3)) continue;
          return false;
        }
        return true;
      }
      blockState = this.world.getBlockState(blockPos2);
      if (blockState.isAir()) {
        return true;
      }
      if (!PistonBlock.isMovable(blockState, this.world, blockPos2, this.motionDirection, true, this.motionDirection)
        || blockPos2.equals(this.posFrom)
      ) {
        return false;
      }
      if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
        this.brokenBlocks.add(blockPos2);
        return true;
      }
      if (this.movedBlocks.size() >= 12) {
        return false;
      }
      this.movedBlocks.add(blockPos2);
      ++j;
      ++k;
    }
  }
}