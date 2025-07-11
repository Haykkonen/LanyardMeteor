package com.haykkonen.lanyard;

import com.haykkonen.lanyard.commands.CommandExample;
import com.haykkonen.lanyard.modules.ChatHelper;
import com.haykkonen.lanyard.modules.PacketDelay;
import com.haykkonen.lanyard.modules.PacketLogger;
import com.haykkonen.lanyard.modules.crashers.flood.CommandFloodCrash;
import com.haykkonen.lanyard.modules.crashers.flood.VelocityCrash;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class Lanyard extends MeteorAddon {
    public static final Category MAIN_CATEGORY = new Category("Lanyard");
    public static final Category CATEGORY_UTILS = new Category("Lanyard Utils");
    public static final Category CATEGORY_CRASH = new Category("Lanyard Crashers");

    @Override
    public void onInitialize() {


        // Modules
        Modules.get().add(new PacketDelay());
        Modules.get().add(new VelocityCrash());
        Modules.get().add(new CommandFloodCrash());
        Modules.get().add(new PacketLogger());
        Modules.get().add(new ChatHelper());

        // Commands
        Commands.add(new CommandExample());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(MAIN_CATEGORY);
        Modules.registerCategory(CATEGORY_UTILS);
        Modules.registerCategory(CATEGORY_CRASH);
    }

    @Override
    public String getPackage() {
        return "com.haykkonen.lanyard";
    }
}
