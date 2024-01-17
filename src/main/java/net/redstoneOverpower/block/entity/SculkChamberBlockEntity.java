package net.redstoneOverpower.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import net.redstoneOverpower.block.SculkChamberBlock;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;

import static net.redstoneOverpower.utils.Initialiser.*;

public class SculkChamberBlockEntity extends BlockEntity implements GameEventListener.Holder<Vibrations.VibrationListener>, Vibrations {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Vibrations.ListenerData listenerData;
    private final Vibrations.VibrationListener listener;
    private final Vibrations.Callback callback;
    private int lastVibrationFrequency;

    protected SculkChamberBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.callback = this.createCallback();
        this.listenerData = new Vibrations.ListenerData();
        this.listener = new Vibrations.VibrationListener(this);
    }

    public SculkChamberBlockEntity(BlockPos pos, BlockState state) {
        this(SCULK_CHAMBER_BLOCK_ENTITY, pos, state);
    }

    public Vibrations.Callback createCallback() {
        return new SculkChamberBlockEntity.VibrationCallback(this.getPos());
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
        if (nbt.contains("listener", 10)) {
            DataResult<ListenerData> var10000 = ListenerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var10000.resultOrPartial(var10001::error).ifPresent((listener) -> this.listenerData = listener);
        }

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        DataResult<NbtElement> var10000 = ListenerData.CODEC.encodeStart(NbtOps.INSTANCE, this.listenerData);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((listenerNbt) -> nbt.put("listener", listenerNbt));
    }

    @Override
    public ListenerData getVibrationListenerData() {
        return this.listenerData;
    }

    @Override
    public Callback getVibrationCallback() {
        return this.callback;
    }

    @Override
    public VibrationListener getEventListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int lastVibrationFrequency) {
        this.lastVibrationFrequency = lastVibrationFrequency;
    }

    protected class VibrationCallback implements Vibrations.Callback {
        public static final int RANGE = 8;
        protected final BlockPos pos;
        private final PositionSource positionSource;

        public VibrationCallback(BlockPos pos) {
            this.pos = pos;
            this.positionSource = new BlockPositionSource(pos);
        }

        public int getRange() {
            return RANGE;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public boolean triggersAvoidCriterion() {
            return true;
        }

        // Decides if the block can accept a sound
        public boolean accepts(ServerWorld world, BlockPos pos, GameEvent event, @Nullable GameEvent.Emitter emitter) {
            BlockState blockState = SculkChamberBlockEntity.this.getCachedState();

            if (blockState.getBlock() instanceof SculkChamberBlock) {
                return (!pos.equals(this.pos)
                    || event != GameEvent.BLOCK_DESTROY
                    && event != GameEvent.BLOCK_PLACE)
                    && SculkChamberBlock.canStoreSound(blockState);
            }

            return !pos.equals(this.pos) || event != GameEvent.BLOCK_DESTROY && event != GameEvent.BLOCK_PLACE;
        }

        public void accept(ServerWorld world, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            BlockState blockState = SculkChamberBlockEntity.this.getCachedState();

            if (SculkChamberBlock.canStoreSound(blockState)) {
                SculkChamberBlockEntity.this.setLastVibrationFrequency(Vibrations.getFrequency(event));
                if (blockState.getBlock() instanceof SculkChamberBlock sculkChamberBlock) {
                    sculkChamberBlock.setCharged(sourceEntity, world, this.pos, blockState, SculkChamberBlockEntity.this.getLastVibrationFrequency());
                }
            }
        }

        public void onListen() {
            SculkChamberBlockEntity.this.markDirty();
        }

        public boolean requiresTickingChunksAround() {
            return true;
        }
    }
}
