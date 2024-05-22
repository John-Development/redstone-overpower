package net.redstoneoverpower.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

public class SculkPulserBlock extends Block {
    public static final BooleanProperty POWERED;

    public SculkPulserBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean isPowered = world.isReceivingRedstonePower(pos);
        int power = world.getReceivedRedstonePower(pos);

        if (isPowered != state.get(POWERED)) {
            if (isPowered) {
                world.addSyncedBlockEvent(pos, this, 0, 0);
                world.emitGameEvent(
                    Vibrations.getResonation(power),
                    pos,
                    GameEvent.Emitter.of(state)
                );
            }
            world.setBlockState(pos, state.with(POWERED, isPowered), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    static {
        POWERED = Properties.POWERED;
    }
}
