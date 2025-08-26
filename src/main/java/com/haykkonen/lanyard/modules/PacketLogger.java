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

import java.lang.reflect.Field;
import java.util.Set;

public class PacketLogger extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<Class<? extends Packet<?>>>> clientPacketsToLog = sgGeneral.add(new PacketListSetting.Builder()
        .name("client-packets")
        .description("Packets from client to server (C2S) to log.")
        .filter(PacketUtils.getC2SPackets()::contains)
        .build()
    );

    private final Setting<Set<Class<? extends Packet<?>>>> serverPacketsToLog = sgGeneral.add(new PacketListSetting.Builder()
        .name("server-packets")
        .description("Packets from server to client (S2C) to log.")
        .filter(PacketUtils.getS2CPackets()::contains)
        .build()
    );

    public PacketLogger() {
        super(Lanyard.LANYARD_UTILS_CATEGORY, "PacketLogger", "Logs specified packets sent and received by the client.");
        this.runInMainMenu = true;
    }

    @EventHandler
    private void onPacketSend(@NotNull PacketEvent.Send event) {
        if (clientPacketsToLog.get().contains(event.packet.getClass())) {
            logPacketDetails(event.packet, "Sending");
        }
    }

    @EventHandler
    private void onPacketReceive(@NotNull PacketEvent.Receive event) {
        if (serverPacketsToLog.get().contains(event.packet.getClass())) {
            logPacketDetails(event.packet, "Receiving");
        }
    }

    private void logPacketDetails(@NotNull Packet<?> packet, @NotNull String direction) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("%s packet: %s%n", direction, packet.getClass().getSimpleName()));

        Class<?> currentClass = packet.getClass();
        while (currentClass != null && Packet.class.isAssignableFrom(currentClass)) {
            for (Field field : currentClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(packet);
                    logMessage.append(String.format("  -> %s: %s%n", field.getName(), value));
                } catch (IllegalAccessException e) {
                    warning("Failed to access field: %s", field.getName());
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        info(logMessage.toString());
    }
}
