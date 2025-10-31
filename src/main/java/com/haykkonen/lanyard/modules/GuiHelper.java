package com.haykkonen.lanyard.modules;

import com.haykkonen.lanyard.Lanyard;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class GuiHelper extends Module {

    private final SettingGroup sgVisuals = settings.createGroup("Visuals");
    private final SettingGroup sgStateTools = settings.createGroup("State Tools");
    private final SettingGroup sgChatBox = settings.createGroup("Chat Box");

    public final Setting<Boolean> showSlotIndex = sgVisuals.add(new BoolSetting.Builder()
            .name("show-slot-index")
            .description("Shows the slot index numbers in the inventory GUI.")
            .defaultValue(false)
            .build());

    public final Setting<Boolean> showSyncInfo = sgVisuals.add(new BoolSetting.Builder()
            .name("show-sync-info")
            .description("Shows information about the current GUI sync status.")
            .defaultValue(false)
            .build());

    public final Setting<SettingColor> textColor = sgVisuals.add(new ColorSetting.Builder()
            .name("text-color")
            .description("The color of the slot index text.")
            .defaultValue(new SettingColor(255, 255, 255, 255))
            .build());

    public final Setting<Boolean> saveGuiButton = sgStateTools.add(new BoolSetting.Builder()
            .name("save-gui-button")
            .description("Shows a button to save the current GUI state.")
            .defaultValue(true)
            .build());

    public final Setting<Boolean> closeWithoutPacketButton = sgStateTools.add(new BoolSetting.Builder()
            .name("force-close-button")
            .description("Shows a button to close the GUI without sending a server packet.")
            .defaultValue(true)
            .build());

    public final Setting<Boolean> delayUIPackets = sgStateTools.add(new BoolSetting.Builder()
            .name("delay-packets")
            .description("Show a button to delay sending UI packets.")
            .defaultValue(true)
            .build());

    public final Setting<Keybind> restoreGuiKeybind = sgStateTools.add(new KeybindSetting.Builder()
            .name("restore-gui-keybind")
            .description("Restores the last saved GUI state.")
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_V))
            .build()
    );

    public final Setting<Boolean> enableChatBox = sgChatBox.add(new BoolSetting.Builder()
            .name("enable-chat-box")
            .description("Adds a command input text box to inventory screens.")
            .defaultValue(true)
            .build());

    public final Setting<Integer> xOffset = sgChatBox.add(new IntSetting.Builder()
            .name("x-offset")
            .description("The X offset of the command input box.")
            .defaultValue(84)
            .visible(enableChatBox::get)
            .build()
    );

    public final Setting<Integer> yOffset = sgChatBox.add(new IntSetting.Builder()
            .name("y-offset")
            .description("The Y offset of the command input box from the top of the screen.")
            .defaultValue(-22)
            .visible(enableChatBox::get)
            .build()
    );

    public final Setting<Integer> width = sgChatBox.add(new IntSetting.Builder()
            .name("width")
            .description("The width of the command input box.")
            .defaultValue(100)
            .visible(enableChatBox::get)
            .build()
    );

    @Nullable
    private Screen storedScreen;
    @Nullable
    private ScreenHandler storedScreenHandler;

    public GuiHelper() {
        super(Lanyard.LANYARD_UTILS_CATEGORY, "Gui Helper", "A collection of GUI-related utilities.");
    }

    public void sendCommand(@NotNull String command) {
        if (command.trim().isEmpty() || mc.player == null || mc.player.networkHandler == null) return;

        if (command.startsWith("/")) {
            mc.player.networkHandler.sendChatCommand(command.substring(1));
        } else {
            mc.player.networkHandler.sendChatMessage(command);
        }
    }

    public void storeCurrentGui() {
        if (mc.currentScreen != null && mc.player != null) {
            this.storedScreen = mc.currentScreen;
            this.storedScreenHandler = mc.player.currentScreenHandler;
        }
    }

    @EventHandler
    private void onTick(@NotNull TickEvent.Post event) {
        if (restoreGuiKeybind.get().isPressed()) {
            restoreSavedGui();
        }
    }

    private void restoreSavedGui() {
        if (this.storedScreen != null && this.storedScreenHandler != null && mc.world != null && mc.player != null) {
            mc.setScreen(this.storedScreen);
            mc.player.currentScreenHandler = this.storedScreenHandler;

            this.storedScreen = null;
            this.storedScreenHandler = null;
        }
    }
}
