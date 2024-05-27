package net.redstoneoverpower.mixin.block.piston;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {
  @Shadow @Final
  private World world;
  @Shadow @Final
  private Direction motionDirection;

  @Shadow
  private boolean tryMove(BlockPos blockPos, Direction direction) {
    return false;
  }
  @Shadow
  private static boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
    return false;
  }

  @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
  private static void isBlockStickyMixin(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if(state.isOf(Blocks.CHAIN)) {
      cir.setReturnValue(true);
      cir.cancel();
    }
  }

  @Unique
  private boolean canConnectChain(BlockState state, Direction dir) {
    return switch (dir) {
      case UP, DOWN -> state.get(Properties.AXIS) == Direction.Axis.Y;
      case NORTH, SOUTH -> state.get(Properties.AXIS) == Direction.Axis.Z;
      case WEST, EAST -> state.get(Properties.AXIS) == Direction.Axis.X;
    };
  }

  @Unique
  private boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState, Direction dir) {
    if (state.isOf(Blocks.CHAIN) && adjacentState.isOf(Blocks.CHAIN) && dir.getAxis() == motionDirection.getAxis()) {
      return canConnectChain(state, motionDirection.getOpposite()) && canConnectChain(adjacentState, motionDirection);
    }
    if (state.isOf(Blocks.CHAIN) && !canConnectChain(state, dir)) {
      return false;
    }
    if (adjacentState.isOf(Blocks.CHAIN)
      && !state.isOf(Blocks.SLIME_BLOCK)
      && !state.isOf(Blocks.HONEY_BLOCK)
      && !canConnectChain(adjacentState, dir.getOpposite())
    ) {
      return false;
    }

    return isAdjacentBlockStuck(state, adjacentState);
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

  @Redirect(
    method = "tryMove",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"
    )
  )
  private boolean isAdjacentBlockStuckRedirect(BlockState state, BlockState adjacentState, BlockPos pos, Direction dir) {
    return isAdjacentBlockStuck(state, adjacentState, this.motionDirection);
  }
}