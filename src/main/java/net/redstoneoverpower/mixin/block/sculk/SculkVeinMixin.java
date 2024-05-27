package net.redstoneoverpower.mixin.block.sculk;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.redstoneoverpower.utils.Initialiser.BUDDING_SCULK_AMETHIST_BLOCK;
import static net.redstoneoverpower.utils.Initialiser.SCULK_NOTE_BLOCK;

@Mixin(SculkVeinBlock.class)
public class SculkVeinMixin {

    @Unique
    private WorldAccess world;
    @Unique
    private BlockPos pos;

    @Inject(
        method = "convertToBlock",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;"),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injected(SculkSpreadManager spreadManager, WorldAccess world, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos arg1) {
        this.world = world;
        this.pos = arg1;
    }

    @Redirect(method = "convertToBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;"))
    public BlockState convertToBlockMixin(Block instance) {
        if (world.getBlockState(pos).isOf(Blocks.BUDDING_AMETHYST)) {
            return BUDDING_SCULK_AMETHIST_BLOCK.getDefaultState();
        } else if (world.getBlockState(pos).isOf(Blocks.NOTE_BLOCK)) {
            return SCULK_NOTE_BLOCK.getDefaultState();
        } else {
            return instance.getDefaultState();
        }
    }
}
