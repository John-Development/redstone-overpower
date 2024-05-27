package net.redstoneoverpower.block;

import net.minecraft.block.*;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

import static net.redstoneoverpower.utils.Initialiser.NOTE_BLOCK_SOUND_EVENT;

public class SculkNoteBlock extends Block {
    public static final BooleanProperty POWERED;
    public static final IntProperty NOTE;

    public SculkNoteBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NOTE, 0).with(POWERED, false));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean isPowered = world.isReceivingRedstonePower(pos);
        if (isPowered != state.get(POWERED)) {
            if (isPowered) {
                this.playNote(state, world, pos);
            }
            world.setBlockState(pos, state.with(POWERED, isPowered), Block.NOTIFY_ALL);
        }
    }

    private void playNote(BlockState state, World world, BlockPos pos) {
        if (world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.emitGameEvent(
                Vibrations.getResonation(state.get(NOTE) + 1),
                pos,
                GameEvent.Emitter.of(state)
            );
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(player.getActiveHand());
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

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        int i = state.get(NOTE);
        world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
        world.playSound(null,
            (double)pos.getX() + 0.5,
            (double)pos.getY() + 0.5,
            (double)pos.getZ() + 0.5,
            NOTE_BLOCK_SOUND_EVENT,
            SoundCategory.RECORDS,
            3.0f,
            NoteBlock.getNotePitch(i),
            world.random.nextLong()
        );

        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, NOTE);
    }

    static {
        POWERED = Properties.POWERED;
        NOTE = IntProperty.of("note", 0, 14);
    }
}
