package net.HearthianDev.redstoneoverpower.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;

import static net.HearthianDev.redstoneoverpower.utils.Initialiser.*;

public class BuddingSculkAmethystBlock extends BuddingAmethystBlock implements SculkSpreadable {
    private static final Direction[] DIRECTIONS = Direction.values();

    public BuddingSculkAmethystBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(5) != 0) {
            return;
        }
        Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = null;
        if (BuddingSculkAmethystBlock.canGrowIn(blockState)) {
            block = SMALL_SCULK_AMETHIST_BUD_BLOCK;
        } else if (blockState.isOf(SMALL_SCULK_AMETHIST_BUD_BLOCK) && blockState.get(SculkAmethystClusterBlock.FACING) == direction) {
            block = MEDIUM_SCULK_AMETHIST_BUD_BLOCK;
        } else if (blockState.isOf(MEDIUM_SCULK_AMETHIST_BUD_BLOCK) && blockState.get(SculkAmethystClusterBlock.FACING) == direction) {
            block = LARGE_SCULK_AMETHIST_BUD_BLOCK;
        } else if (blockState.isOf(LARGE_SCULK_AMETHIST_BUD_BLOCK) && blockState.get(SculkAmethystClusterBlock.FACING) == direction) {
            block = SCULK_AMETHIST_CLUSTER_BLOCK;
        }
        if (block != null) {
            BlockState blockState2 = block.getDefaultState().with(SculkAmethystClusterBlock.FACING, direction)
                .with(SculkAmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
            world.setBlockState(blockPos, blockState2);
        }
    }

    @Override
    public int spread(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
        return 0;
    }
}
