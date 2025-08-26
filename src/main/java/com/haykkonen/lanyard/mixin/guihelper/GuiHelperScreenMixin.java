package com.haykkonen.lanyard.mixin.guihelper;

import com.haykkonen.lanyard.modules.GuiHelper;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
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

    @Shadow protected int x;
    @Shadow protected int y;

    @Unique private TextFieldWidget lanyardTextFieldWidget;

    @Unique private static final int BUTTON_WIDTH = 115;
    @Unique private static final int BUTTON_HEIGHT = 20;
    @Unique private static final int WIDGET_X_POS = 5;
    @Unique private static final int WIDGET_Y_POS = 5;
    @Unique private static final int WIDGET_SPACING = 2;

    protected GuiHelperScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!((Object) this instanceof GenericContainerScreen screen)) return;

        GuiHelper module = Modules.get().get(GuiHelper.class);
        if (module == null || !module.isActive() || !Boolean.TRUE.equals(module.showSlotIndex.get()) || mc.player == null) {
            return;
        }

        for (Slot slot : screen.getScreenHandler().slots) {
            if (slot.inventory == mc.player.getInventory()) continue;

            String text = String.valueOf(slot.getIndex());
            double textWidth = mc.textRenderer.getWidth(text);
            int textX = this.x + slot.x + (int) ((16 - textWidth) / 2.0);
            int textY = this.y + slot.y + (int) ((16 - mc.textRenderer.fontHeight) / 2.0) + 1;

            context.drawTextWithShadow(mc.textRenderer, text, textX, textY, module.textColor.get().getPacked());
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if ((Object) this instanceof CreativeInventoryScreen) return;

        GuiHelper module = Modules.get().get(GuiHelper.class);
        if (module == null || !module.isActive()) return;

        int currentY = WIDGET_Y_POS;
        currentY = initSaveButton(module, currentY);
        currentY = initCloseButton(module, currentY);
        initPacketDelayButton(module, currentY);
        initChatBox(module);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof CreativeInventoryScreen) return;

        GuiHelper module = Modules.get().get(GuiHelper.class);
        if (module == null || !module.isActive() || !Boolean.TRUE.equals(module.enableChatBox.get()) || lanyardTextFieldWidget == null || !lanyardTextFieldWidget.isFocused()) {
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_E) {
            cir.setReturnValue(true);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            module.sendCommand(lanyardTextFieldWidget.getText());
            lanyardTextFieldWidget.setText("");
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemoved(CallbackInfo ci) {
        this.lanyardTextFieldWidget = null;
    }

    @Unique
    private int initSaveButton(@NotNull GuiHelper module, int yPos) {
        if (Boolean.TRUE.equals(module.saveGuiButton.get())) {
            addDrawableChild(ButtonWidget.builder(Text.of("Save GUI"), button -> module.storeCurrentGui())
                .dimensions(WIDGET_X_POS, yPos, BUTTON_WIDTH, BUTTON_HEIGHT).build());
            return yPos + BUTTON_HEIGHT + WIDGET_SPACING;
        }
        return yPos;
    }

    @Unique
    private int initCloseButton(@NotNull GuiHelper module, int yPos) {
        if (Boolean.TRUE.equals(module.closeWithoutPacketButton.get())) {
            addDrawableChild(ButtonWidget.builder(Text.of("Close without packet"), button -> mc.setScreen(null))
                .dimensions(WIDGET_X_POS, yPos, BUTTON_WIDTH, BUTTON_HEIGHT).build());
            return yPos + BUTTON_HEIGHT + WIDGET_SPACING;
        }
        return yPos;
    }

    @Unique
    private void initPacketDelayButton(@NotNull GuiHelper module, int yPos) {
        if (Boolean.TRUE.equals(module.delayUIPackets.get())) {
            Text buttonText = Text.of("Delay packets: " + (isPacketDelayActive ? "ON" : "OFF"));
            addDrawableChild(ButtonWidget.builder(buttonText, GuiHelperScreenMixin::togglePacketDelay)
                .dimensions(WIDGET_X_POS, yPos, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        }
    }

    @Unique
    private void initChatBox(@NotNull GuiHelper module) {
        if (Boolean.TRUE.equals(module.enableChatBox.get())) {
            int chatBoxX = this.x + module.xOffset.get();
            int chatBoxY = this.y + module.yOffset.get();
            int chatBoxWidth = module.width.get();

            lanyardTextFieldWidget = new TextFieldWidget(this.textRenderer, chatBoxX, chatBoxY, chatBoxWidth, 20, Text.literal(""));
            lanyardTextFieldWidget.setPlaceholder(Text.literal("Message..."));
            lanyardTextFieldWidget.setMaxLength(256);
            addDrawableChild(lanyardTextFieldWidget);
        }
    }

    @Unique
    private static void togglePacketDelay(@NotNull ButtonWidget button) {
        isPacketDelayActive = !isPacketDelayActive;
        button.setMessage(Text.of("Delay packets: " + (isPacketDelayActive ? "ON" : "OFF")));

        if (!isPacketDelayActive && !delayedUIPackets.isEmpty() && mc.getNetworkHandler() != null) {
            List<Packet<?>> packetsToSend = new ArrayList<>(delayedUIPackets);
            delayedUIPackets.clear();

            packetsToSend.forEach(mc.getNetworkHandler()::sendPacket);

            if (mc.player != null) {
                mc.player.sendMessage(Text.of("Sent " + packetsToSend.size() + " delayed packets."), false);
            }
        }
    }
}
