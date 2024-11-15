package views;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class Gui {
    public static boolean beginCentered(String title) {
        ImGuiIO io = ImGui.getIO();
        ImVec2 pos = new ImVec2(io.getDisplayFramebufferScale().x * 0.5f, io.getDisplaySize().y * 0.5f);
        ImGui.setNextWindowPos(pos, ImGuiCond.Always, new ImVec2(0.5f, 0.5f));
        int flags = ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoDecoration
                | ImGuiWindowFlags.AlwaysAutoResize
                | ImGuiWindowFlags.NoSavedSettings;
        return ImGui.begin(title, null, flags);
    }
}
