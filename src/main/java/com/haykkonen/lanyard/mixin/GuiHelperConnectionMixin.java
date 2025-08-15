package com.haykkonen.lanyard.mixin;

import com.haykkonen.lanyard.LanyardSharedData;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.haykkonen.lanyard.LanyardSharedData.isPacketDelayActive;

@Mixin(ClientConnection.class)
public class GuiHelperConnectionMixin {
    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void onSendImmediately(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo callbackInfo) {
        if (isPacketDelayActive && (packet instanceof ClickSlotC2SPacket || packet instanceof ButtonClickC2SPacket)) {
            LanyardSharedData.delayedUIPackets.add(packet);
            callbackInfo.cancel();
        }
    }
}
