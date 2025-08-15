package com.haykkonen.lanyard;

import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class LanyardSharedData {

    private LanyardSharedData() {
        throw new IllegalStateException("Utility class");
    }

    public static final List<Packet<?>> delayedUIPackets = new ArrayList<>();
    public static boolean isPacketDelayActive = false;

}
