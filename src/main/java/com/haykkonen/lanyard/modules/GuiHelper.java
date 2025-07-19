package com.haykkonen.lanyard.modules;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class GuiHelper extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SettingColor> textColor = sgGeneral.add(new ColorSetting.Builder()
            .name("text-color")
            .description("The color of the slot index text.")
            .defaultValue(new SettingColor(255, 255, 255, 255))
            .build()
    );

    public GuiHelper() {
        super(Lanyard.CATEGORY_UTILS, "GuiHelper", "Shows the slot index numbers in the inventory GUI.");
    }
}
