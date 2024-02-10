package net.redstoneOverpower.client.gui.screen.ingame;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.redstoneOverpower.block.screen.DuctScreenHandler;
import net.redstoneOverpower.block.screen.DuctSlot;

public class DuctScreen extends HandledScreen<DuctScreenHandler> {
  private static final Identifier FILTER_SLOT_TEXTURE = new Identifier("container/crafter/disabled_slot");
  private static final Text ENABLE_FILTER_TEXT = Text.translatable("gui.enable_filter");
  private static final Text DISABLE_FILTER_TEXT = Text.translatable("gui.disable_filter");
  private static final Identifier TEXTURE = new Identifier("redstoneoverpower", "textures/gui/container/duct.png");

  private final PlayerEntity player;

  public DuctScreen(DuctScreenHandler handler, PlayerInventory playerInventory, Text title) {
    super(handler, playerInventory, title);
    this.backgroundHeight = 133;
    this.playerInventoryTitleY = this.backgroundHeight - 94;
    this.player = playerInventory.player;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    this.drawMouseoverTooltip(context, mouseX, mouseY);
    if (this.focusedSlot instanceof DuctSlot && this.handler.getCursorStack().isEmpty() && !this.focusedSlot.hasStack()) {
      context.drawTooltip(this.textRenderer, this.handler.isSlotDisabled(this.focusedSlot.id) ? DISABLE_FILTER_TEXT : ENABLE_FILTER_TEXT, mouseX, mouseY);
    }
  }

  @Override
  protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    int i = (this.width - this.backgroundWidth) / 2;
    int j = (this.height - this.backgroundHeight) / 2;
    context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
  }

  protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
    if (slot instanceof DuctSlot && !slot.hasStack() && !this.player.isSpectator()) {
      if (actionType == SlotActionType.PICKUP) {
        if (this.handler.getCursorStack().isEmpty() && !this.handler.getSlot(slotId).hasStack()) {
          this.toggleSlot(this.handler.isSlotDisabled(slotId));
        }
      }
    }

    super.onMouseClick(slot, slotId, button, actionType);
  }

  private void toggleSlot(boolean enabled) {
    float f = enabled ? 1.0F : 0.75F;
    this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, f);
  }

  public void drawSlot(DrawContext context, Slot slot) {
    if (slot instanceof DuctSlot ductSlot) {
      if (this.handler.isSlotDisabled(slot.id)) {
        this.drawDisabledSlot(context, ductSlot);

        return;
      }
    }

    super.drawSlot(context, slot);
  }

  private void drawDisabledSlot(DrawContext context, DuctSlot slot) {
    context.drawItem(slot.getStack(), slot.x, slot.y);
    context.drawItemInSlot(this.textRenderer, slot.getStack(), slot.x, slot.y);
    context.drawGuiTexture(FILTER_SLOT_TEXTURE, slot.x - 1, slot.y - 1, 18, 18);
  }
}
