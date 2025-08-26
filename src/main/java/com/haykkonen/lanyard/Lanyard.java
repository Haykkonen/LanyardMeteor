package com.haykkonen.lanyard;

import com.haykkonen.lanyard.modules.GuiHelper;
import com.haykkonen.lanyard.modules.PacketDelay;
import com.haykkonen.lanyard.modules.PacketLogger;
import com.haykkonen.lanyard.modules.crashers.flood.CommandFloodCrasher;
import com.haykkonen.lanyard.modules.crashers.flood.VelocityCrasher;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.jetbrains.annotations.NotNull;

public class Lanyard extends MeteorAddon {
    public static final Category LANYARD_CATEGORY = new Category("Lanyard");
    public static final Category LANYARD_UTILS_CATEGORY = new Category("Lanyard Utils");
    public static final Category LANYARD_CRASHERS_CATEGORY = new Category("Lanyard Crashers");

    @Override
    public void onInitialize() {
        Modules modules = Modules.get();
        modules.add(new PacketDelay());
        modules.add(new VelocityCrasher());
        modules.add(new CommandFloodCrasher());
        modules.add(new PacketLogger());
        modules.add(new GuiHelper());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(LANYARD_CATEGORY);
        Modules.registerCategory(LANYARD_UTILS_CATEGORY);
        Modules.registerCategory(LANYARD_CRASHERS_CATEGORY);
    }

    @Override
    public @NotNull String getPackage() {
        return "com.haykkonen.lanyard";
    }
}
