package net.redstoneOverpower.client;


import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.redstoneOverpower.block.SculkChamberBlock;
import net.redstoneOverpower.block.entity.SculkChamberBlockEntity;
import net.redstoneOverpower.block.enums.SculkChamberMode;
import software.bernie.geckolib.model.GeoModel;

import static net.redstoneOverpower.RedstoneOverpower.MOD_ID;

public class SculkChamberBlockModel extends GeoModel<SculkChamberBlockEntity> {
    @Override
    public Identifier getModelResource(SculkChamberBlockEntity sculkChamberBlockEntity) {
        World world = sculkChamberBlockEntity.getWorld();
        BlockPos pos = sculkChamberBlockEntity.getPos();
        assert world != null;
        BlockState blockState = world.getBlockState(pos);

        if (world.getBlockEntity(pos) instanceof SculkChamberBlockEntity && blockState.get(SculkChamberBlock.MODE) == SculkChamberMode.CHARGED) {
            return new Identifier(MOD_ID, "geo/sculk_chamber_closed.geo.json");
        }

        return new Identifier(MOD_ID, "geo/sculk_chamber.geo.json");
    }

    @Override
    public Identifier getTextureResource(SculkChamberBlockEntity sculkChamberBlockEntity) {
        return new Identifier(MOD_ID, "textures/block/sculk_chamber.png");
    }

    @Override
    public Identifier getAnimationResource(SculkChamberBlockEntity sculkChamberBlockEntity) {
        World world = sculkChamberBlockEntity.getWorld();
        BlockPos pos = sculkChamberBlockEntity.getPos();
        assert world != null;
        BlockState blockState = world.getBlockState(pos);

        if (world.getBlockEntity(pos) instanceof SculkChamberBlockEntity && blockState.get(SculkChamberBlock.MODE) == SculkChamberMode.CHARGED) {
            return new Identifier(MOD_ID, "animations/sculk_chamber_closed.animation.json");
        }

        return new Identifier(MOD_ID, "animations/permasculk_chamber.animation.json");
    }
}
