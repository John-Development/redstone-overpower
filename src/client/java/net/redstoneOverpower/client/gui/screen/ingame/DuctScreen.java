package net.redstoneOverpower.client.gui.screen.ingame;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.redstoneOverpower.block.screen.DuctScreenHandler;

public class DuctScreen extends HandledScreen<DuctScreenHandler> {
  private static final Identifier TEXTURE = new Identifier("redstoneoverpower", "textures/gui/container/duct.png");

  public DuctScreen(DuctScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
    this.backgroundHeight = 133;
    this.playerInventoryTitleY = this.backgroundHeight - 94;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    this.drawMouseoverTooltip(context, mouseX, mouseY);
  }

  @Override
  protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    int i = (this.width - this.backgroundWidth) / 2;
    int j = (this.height - this.backgroundHeight) / 2;
    context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
  }
}
