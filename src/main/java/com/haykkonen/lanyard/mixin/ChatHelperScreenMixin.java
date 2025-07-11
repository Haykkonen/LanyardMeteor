package com.haykkonen.lanyard.mixin;

import com.haykkonen.lanyard.modules.ChatHelper;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class ChatHelperScreenMixin extends Screen {

    @Shadow
    protected int x;
    @Shadow
    protected int y;

    @Unique
    private TextFieldWidget lanyard$chatHelperTextField;

    protected ChatHelperScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo callbackInfo) {
        ChatHelper module = Modules.get().get(ChatHelper.class);
        if (module == null || !module.isActive()) return;

        int w = module.width.get();
        int pX = this.x + module.xOffset.get();
        int pY = this.y + module.yOffset.get();

        lanyard$chatHelperTextField = new TextFieldWidget(this.textRenderer, pX, pY, w, 20, Text.literal(""));
        lanyard$chatHelperTextField.setPlaceholder(Text.literal("Message..."));

        addDrawableChild(lanyard$chatHelperTextField);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        ChatHelper module = Modules.get().get(ChatHelper.class);
        if (module == null || !module.isActive() || lanyard$chatHelperTextField == null || !lanyard$chatHelperTextField.isFocused()) return;

        if (keyCode == GLFW.GLFW_KEY_E && lanyard$chatHelperTextField.isFocused()) cir.setReturnValue(true);

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            module.sendCommand(lanyard$chatHelperTextField.getText());
            lanyard$chatHelperTextField.setText("");
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemoved(CallbackInfo ci) {
        lanyard$chatHelperTextField = null;
    }
}
