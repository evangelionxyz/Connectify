package views;

import imgui.internal.ImGui;

public class HRDWindow extends WindowBase {
    @Override
    public void init() {
    }

    @Override
    public void render() {
        ImGui.begin("HRD");
        ImGui.text("HRD WINDOW");
        ImGui.end();
    }
}
