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
import java.util.logging.Level;
import java.util.logging.Logger;


public class PacketLogger extends Module {

    private static final Logger LOGGER = Logger.getLogger(PacketLogger.class.getName());

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<Class<? extends Packet<?>>>> clientPacketsToLog = sgGeneral.add(new PacketListSetting.Builder()
        .name("c2s-packets")
        .description("Packets from client to server (C2S) to log.")
        .filter(PacketUtils.getC2SPackets()::contains)
        .build()
    );

    private final Setting<Set<Class<? extends Packet<?>>>> serverPacketsToLog = sgGeneral.add(new PacketListSetting.Builder()
        .name("s2c-packets")
        .description("Packets from server to client (S2C) to log.")
        .filter(PacketUtils.getS2CPackets()::contains)
        .build()
    );

    public PacketLogger() {
        super(Lanyard.CATEGORY_UTILS, "PacketLogger", "Logs specified packets sent and received by the client.");
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


    private void logPacketDetails(@NotNull Packet<?> packet, String direction) {
        LOGGER.log(Level.INFO, "{0} packet: {1}", new Object[]{direction, packet.getClass().getSimpleName()});

        Class<?> currentClass = packet.getClass();

        while (currentClass != null && Packet.class.isAssignableFrom(currentClass)) {
            for (Field field : currentClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(packet);
                    LOGGER.log(Level.FINE, "  -> Key: {0}, Value: {1}", new Object[]{field.getName(), value});
                } catch (IllegalAccessException e) {
                    LOGGER.log(Level.WARNING, "Failed to access field: {0} ", new Object[]{field.getName()});
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }
}
