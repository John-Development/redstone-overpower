package net.redstoneOverpower.block;

import net.minecraft.block.*;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

public class SculkNoteBlock extends NoteBlock {
    public SculkNoteBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NOTE, 0).with(POWERED, false));
    }

    private BlockState getStateWithInstrument(WorldAccess world, BlockPos pos, BlockState state) {
        Instrument instrument = world.getBlockState(pos.up()).getInstrument();

        return state.with(INSTRUMENT, instrument);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction.getAxis() == Direction.Axis.Y
            ? this.getStateWithInstrument(world, pos, state)
            : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean isPowered = world.isReceivingRedstonePower(pos);
        if (isPowered != state.get(POWERED)) {
            if (isPowered) {
                this.playNote(state, world, pos);
                world.emitGameEvent(
                    Vibrations.getResonation((state.get(NOTE) % 15) + 1),
                    pos,
                    GameEvent.Emitter.of(state)
                );
            }
            world.setBlockState(pos, state.with(POWERED, isPowered), Block.NOTIFY_ALL);
        }
    }

    private void playNote(BlockState state, World world, BlockPos pos) {
        if (state.get(INSTRUMENT).isNotBaseBlock() || world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS) && hit.getSide() == Direction.UP) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        state = state.cycle(NOTE);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        this.playNote(state, world, pos);
        player.incrementStat(Stats.TUNE_NOTEBLOCK);

        return ActionResult.CONSUME;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) {
            return;
        }
        this.playNote(state, world, pos);
        player.incrementStat(Stats.PLAY_NOTEBLOCK);
    }
}
