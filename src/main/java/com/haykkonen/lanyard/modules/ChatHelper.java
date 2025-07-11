package com.haykkonen.lanyard.modules;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ChatHelper extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Integer> xOffset = sgGeneral.add(new IntSetting.Builder()
            .name("x-offset")
            .description("The X offset of the command input box.")
            .defaultValue(84)
            .build()
    );

    public final Setting<Integer> yOffset = sgGeneral.add(new IntSetting.Builder()
            .name("y-offset")
            .description("The Y offset of the command input box from the top of the screen.")
            .defaultValue(-22)
            .build()
    );

    public final Setting<Integer> width = sgGeneral.add(new IntSetting.Builder()
            .name("width")
            .description("The width of the command input box.")
            .defaultValue(100)
            .build()
    );

    public ChatHelper() {
        super(Lanyard.CATEGORY_UTILS, "ChatHelper", "Adds a command input text box to inventory screens.");
    }

    public void sendCommand(String command) {
        if (command == null || command.trim().isEmpty() || mc.player == null) return;

        mc.player.networkHandler.sendChatMessage(command);
    }
}
