package com.haykkonen.lanyard.mixin;

import com.haykkonen.lanyard.modules.GuiHelper;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.haykkonen.lanyard.LanyardSharedData.delayedUIPackets;
import static com.haykkonen.lanyard.LanyardSharedData.isPacketDelayActive;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(HandledScreen.class)
public abstract class GuiHelperScreenMixin extends Screen {
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    @Unique
    private TextFieldWidget lanyardTextFieldWidget;

    protected GuiHelperScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {
        if (!((Object) this instanceof GenericContainerScreen screen)) return;

        GuiHelper module = Modules.get().get(GuiHelper.class);
        if (module == null || !module.isActive() || !Boolean.TRUE.equals(module.showSlotIndex.get())) return;
        if (mc.player == null || mc.world == null) return;

        int guiX = this.x;
        int guiY = this.y;

        for (Slot slot : screen.getScreenHandler().slots) {
            if (slot.inventory == mc.player.getInventory()) continue;

            String text = String.valueOf(slot.getIndex());
            double textWidth = mc.textRenderer.getWidth(text);
            int slotX = guiX + slot.x;
            int slotY = guiY + slot.y;
            int centeredX = (int) (slotX + (16 - textWidth) / 2.0);
            int centeredY = (int) (slotY + (16 - mc.textRenderer.fontHeight) / 2.0) + 1;

            context.drawTextWithShadow(mc.textRenderer, text, centeredX, centeredY, module.textColor.get().getPacked());
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo callbackInfo) {
        GuiHelper module = Modules.get().get(GuiHelper.class);
        if (!module.isActive()) return;

        int buttonWidth = 115;
        int buttonHeight = 20;
        int xPos = 5;
        int yPos = 5;

        if (Boolean.TRUE.equals(module.saveGuiButton.get())) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("Save GUI"), button -> module.storeCurrentGui())
                .dimensions(xPos, yPos, buttonWidth, buttonHeight).build());
            yPos += buttonHeight + 2;
        }

        if (Boolean.TRUE.equals(module.closeWithoutPacketButton.get())) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("Close without packet"), button -> mc.setScreen(null))
                .dimensions(xPos, yPos, buttonWidth, buttonHeight).build());
            yPos += buttonHeight + 2;
        }

        if (Boolean.TRUE.equals(module.delayUIPackets.get())) {
            Text buttonText = Text.of("Delay packets: " + (isPacketDelayActive ? "ON" : "OFF"));

            addDrawableChild(ButtonWidget.builder(buttonText, button -> {
                isPacketDelayActive = !isPacketDelayActive;
                button.setMessage(Text.of("Delay packets: " + (isPacketDelayActive ? "ON" : "OFF")));

                if (!isPacketDelayActive && !delayedUIPackets.isEmpty() && mc.getNetworkHandler() != null) {
                    List<Packet<?>> packetsToSend = new ArrayList<>(delayedUIPackets);

                    delayedUIPackets.clear();

                    packetsToSend.forEach(packet -> mc.getNetworkHandler().sendPacket(packet));

                    if (mc.player != null)
                        mc.player.sendMessage(Text.of("Sent " + packetsToSend.size() + " packets."), false);
                }
            }).dimensions(xPos, yPos, buttonWidth, buttonHeight).build());
        }

        if (Boolean.TRUE.equals(module.enableChatBox.get())) {
            int w = module.width.get();
            int pX = this.x + module.xOffset.get();
            int pY = this.y + module.yOffset.get();

            lanyardTextFieldWidget = new TextFieldWidget(this.textRenderer, pX, pY, w, 20, Text.literal(""));
            lanyardTextFieldWidget.setPlaceholder(Text.literal("Message..."));
            lanyardTextFieldWidget.setMaxLength(256);

            addDrawableChild(lanyardTextFieldWidget);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        GuiHelper module = Modules.get().get(GuiHelper.class);
        if (module == null || !module.isActive() || !Boolean.TRUE.equals(module.enableChatBox.get()) || lanyardTextFieldWidget == null || !lanyardTextFieldWidget.isFocused())
            return;

        if (keyCode == GLFW.GLFW_KEY_E && lanyardTextFieldWidget.isFocused()) {
            callbackInfoReturnable.setReturnValue(true);
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            module.sendCommand(lanyardTextFieldWidget.getText());
            lanyardTextFieldWidget.setText("");
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemoved(CallbackInfo callbackInfo) {
        lanyardTextFieldWidget = null;
    }
}
