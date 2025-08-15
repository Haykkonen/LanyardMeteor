package com.haykkonen.lanyard.modules;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class PacketDelay extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Queue<Packet<?>> delayedPacketQueue = new LinkedList<>();

    private final Setting<Set<Class<? extends Packet<?>>>> packetsToDelay = sgGeneral.add(new PacketListSetting.Builder()
        .name("c2s-packets")
        .description("Packets from client to server (C2S) to delay.")
        .filter(PacketUtils.getC2SPackets()::contains)
        .build()
    );

    public PacketDelay() {
        super(Lanyard.CATEGORY_UTILS, "PacketDelay", "Delays sending specified packets from client to server (C2S).");
    }

    @Override
    public void onDeactivate() {
        if (mc.getNetworkHandler() != null) {
            while (!delayedPacketQueue.isEmpty()) {
                mc.getNetworkHandler().sendPacket(delayedPacketQueue.poll());
            }
        } else {
            delayedPacketQueue.clear();
        }
    }


    @EventHandler
    private void onSendPacket(@NotNull PacketEvent.Send event) {
        if (packetsToDelay.get().contains(event.packet.getClass())) {
            delayedPacketQueue.add(event.packet);

            event.cancel();
        }
    }
}
