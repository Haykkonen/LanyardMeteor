package com.haykkonen.lanyard.mixin;

import com.haykkonen.lanyard.modules.GuiHelper;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(HandledScreen.class)
public abstract class GuiHelperScreenMixin {

    @Accessor("x")
    public abstract int getX();

    @Accessor("y")
    public abstract int getY();

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {
        if (!((Object) this instanceof GenericContainerScreen)) return;

        GuiHelper module = Modules.get().get(GuiHelper.class);

        if (module == null || !module.isActive()) return;
        if (mc.player == null || mc.world == null) return;

        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

        int guiX = this.getX();
        int guiY = this.getY();

        for (Slot slot : screen.getScreenHandler().slots) {
            if (slot.inventory == mc.player.getInventory()) continue;

            String text = String.valueOf(slot.getIndex());

            int slotX = guiX + slot.x;
            int slotY = guiY + slot.y;

            double textWidth = mc.textRenderer.getWidth(text);

            int centeredX = (int) (slotX + (16 - textWidth) / 2.0);
            int centeredY = (int) (slotY + (16 - mc.textRenderer.fontHeight) / 2.0) + 1;

            context.drawTextWithShadow(mc.textRenderer, text, centeredX, centeredY, module.textColor.get().getPacked());
        }
    }
}
