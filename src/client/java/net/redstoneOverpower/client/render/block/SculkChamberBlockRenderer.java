package net.redstoneOverpower.client.render.block;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.redstoneOverpower.block.entity.SculkChamberBlockEntity;
import net.redstoneOverpower.client.SculkChamberBlockModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SculkChamberBlockRenderer extends GeoBlockRenderer<SculkChamberBlockEntity> {
    public SculkChamberBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new SculkChamberBlockModel());
    }
}
