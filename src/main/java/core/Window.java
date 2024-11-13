package core;

import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private final long windowHandle;
    private final ImGuiImplGlfw imguiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imguiGl3 = new ImGuiImplGl3();
    private ImGuiRenderFunction imGuiRenderFunction;

    public Window(int width, int height, String title) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        windowHandle = glfwCreateWindow(width, height, title, 0, 0);
        if (windowHandle == 0) {
            throw new RuntimeException("Failed to create Window");
        }

        int[] pWidth = new int[1];
        int[] pHeight = new int[1];

        glfwGetWindowSize(windowHandle, pWidth, pHeight);
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode != null) {
            glfwSetWindowPos(windowHandle,
                    (videoMode.width() - pWidth[0])/ 2,
                    (videoMode.height() - pHeight[0])/2);
        }

        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        glfwSwapInterval(1);
        glfwShowWindow(windowHandle);

        // init imgui
        ImGui.createContext();
        applyTheme();

        imguiGlfw.init(windowHandle, true);
        String glslVersion = "#version 450";
        imguiGl3.init(glslVersion);

        glClearColor(0.1f, 0.1f,0.1f, 0.1f);
    }

    public void setGuiRenderFunction(ImGuiRenderFunction func) {
        this.imGuiRenderFunction = func;
    }

    public boolean isLooping() {
        return !glfwWindowShouldClose(windowHandle);
    }

    public void swapBuffer() {
        glClear(GL_COLOR_BUFFER_BIT);

        imguiGl3.newFrame();
        imguiGlfw.newFrame();

        ImGui.newFrame();

        beginDockSpace();

        if (imGuiRenderFunction != null) {
            imGuiRenderFunction.render();
        }

        endDockSpace();

        ImGui.render();
        imguiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }

        glfwSwapBuffers(windowHandle);
    }

    public void pollEvents() {
        glfwWaitEvents();
    }

    private void applyTheme() {
        ImGuiStyle style = getImGuiStyle();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoDecoration(true);

        float fontSize = 16.0f;
        io.setFontDefault(io.getFonts().addFontFromFileTTF("resources/fonts/inter-regular.ttf", fontSize));

        //style.setWindowPadding(new ImVec2(12.0f, 8.0f));

        style.setColor(ImGuiCol.Text, 1.00f, 1.0f, 1.00f, 1.00f);
        style.setColor(ImGuiCol.TextDisabled, 0.60f, 0.60f, 0.60f, 1.00f);
        style.setColor(ImGuiCol.WindowBg, 0.10f, 0.10f, 0.10f, 1.00f);
        style.setColor(ImGuiCol.ChildBg, 0.10f, 0.10f, 0.10f, 1.00f);
        style.setColor(ImGuiCol.PopupBg, 0.10f, 0.10f, 0.10f, 0.94f);
        style.setColor(ImGuiCol.Border, 0.16f, 0.16f, 0.16f, 1.00f);
        style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.FrameBg, 0.25f, 0.25f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.29f, 0.29f, 0.29f, 1.00f);
        style.setColor(ImGuiCol.FrameBgActive, 0.38f, 0.38f, 0.38f, 1.00f);
        style.setColor(ImGuiCol.TitleBg, 0.12f, 0.12f, 0.12f, 1.00f);
        style.setColor(ImGuiCol.TitleBgActive, 0.20f, 0.20f, 0.20f, 1.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.31f, 0.31f, 0.31f, 0.75f);
        style.setColor(ImGuiCol.MenuBarBg, 0.00f, 0.00f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.17f, 0.17f, 0.17f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.35f, 0.35f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.35f, 0.35f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.56f, 0.56f, 0.56f, 1.00f);
        style.setColor(ImGuiCol.CheckMark, 0.90f, 0.90f, 0.90f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab, 0.35f, 0.35f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.91f, 0.91f, 0.91f, 1.00f);
        style.setColor(ImGuiCol.Button, 0.17f, 0.17f, 0.17f, 1.00f);
        style.setColor(ImGuiCol.ButtonHovered, 0.17f, 0.17f, 0.17f, 1.00f);
        style.setColor(ImGuiCol.ButtonActive, 0.00f, 0.00f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.Header, 0.17f, 0.17f, 0.17f, 1.00f);
        style.setColor(ImGuiCol.HeaderHovered, 0.17f, 0.17f, 0.17f, 1.00f);
        style.setColor(ImGuiCol.HeaderActive, 0.00f, 0.00f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.Separator, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(ImGuiCol.SeparatorHovered, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(ImGuiCol.SeparatorActive, 0.80f, 0.80f, 0.80f, 1.00f);
        style.setColor(ImGuiCol.ResizeGrip, 0.40f, 0.40f, 0.40f, 1.00f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.56f, 0.56f, 0.56f, 1.00f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.80f, 0.80f, 0.80f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.15f, 0.15f, 0.15f, 1.00f);
        style.setColor(ImGuiCol.Tab, 0.23f, 0.23f, 0.23f, 1.00f);
        style.setColor(ImGuiCol.TabHovered, 0.29f, 0.29f, 0.29f, 1.00f);
        style.setColor(ImGuiCol.TabActive, 0.10f, 0.15f, 0.33f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocused, 0.23f, 0.23f, 0.23f, 1.00f);
        style.setColor(ImGuiCol.DockingPreview, 0.15f, 0.15f, 0.15f, 1.00f);
        style.setColor(ImGuiCol.DockingEmptyBg, 0.15f, 0.15f, 0.15f, 1.00f);
        style.setColor(ImGuiCol.PlotLines, 0.78f, 0.52f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.67f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.TableHeaderBg, 0.24f, 0.24f, 0.24f, 1.00f);
        style.setColor(ImGuiCol.TableBorderStrong, 0.29f, 0.29f, 0.29f, 1.00f);
        style.setColor(ImGuiCol.TableBorderLight, 0.25f, 0.25f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.TableRowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TableRowBgAlt, 0.12f, 0.12f, 0.12f, 1.00f);
        style.setColor(ImGuiCol.TextSelectedBg, 0.45f, 0.45f, 0.45f, 0.50f);
        style.setColor(ImGuiCol.DragDropTarget, 1.00f, 0.67f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.NavHighlight, 0.45f, 0.45f, 0.45f, 1.00f);
        style.setColor(ImGuiCol.NavWindowingHighlight, 0.90f, 0.90f, 0.90f, 0.70f);
        style.setColor(ImGuiCol.NavWindowingDimBg, 0.20f, 0.20f, 0.20f, 0.20f);
        style.setColor(ImGuiCol.ModalWindowDimBg, 0.20f, 0.20f, 0.20f, 0.45f);
    }

    @NotNull
    private static ImGuiStyle getImGuiStyle() {
        ImGuiStyle style = ImGui.getStyle();

        style.setWindowPadding(new ImVec2());
        style.setFramePadding(new  ImVec2(6.0f, 3.0f));
        style.setCellPadding(new ImVec2(7.0f, 3.0f));
        style.setItemSpacing(new ImVec2(4.0f, 3.0f));
        style.setItemInnerSpacing(new ImVec2(6.0f, 3.0f));
        style.setTouchExtraPadding(new ImVec2(0.0f, 0.0f));
        style.setIndentSpacing(8);
        style.setScrollbarSize(16.0f);
        style.setGrabMinSize(13);
        style.setWindowBorderSize(0);
        style.setChildBorderSize(1);
        style.setPopupBorderSize(1);
        style.setFrameBorderSize(0);
        style.setTabBorderSize(0);
        style.setWindowRounding(0);
        style.setChildRounding(0);
        style.setFrameRounding(0);
        style.setPopupRounding(0);
        style.setScrollbarRounding(0);
        style.setWindowMenuButtonPosition(ImGuiDir.Right);
        style.setColorButtonPosition(ImGuiDir.Right);
        style.setAntiAliasedFill(true);
        style.setAntiAliasedLines(true);
        style.setAntiAliasedLinesUseTex(true);
        return style;
    }

    private void beginDockSpace() {
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPos());
        ImGui.setNextWindowSize(mainViewport.getWorkSize());
        ImGui.setNextWindowViewport(mainViewport.getID());

        int dockNodeFlags = ImGuiDockNodeFlags.None;
        int windowFlags = ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse
                | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoBringToFrontOnFocus
                | ImGuiWindowFlags.NoNavFocus;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(1.0f, 2.0f));
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

        ImGui.begin("Connectify", windowFlags);
        ImGui.popStyleVar(2);

        ImGuiStyle style = ImGui.getStyle();
        final float minWindowSizeX = 220.0f;
        final float minWindowSizeY = 26.0f;
        final int dockSpaceId = ImGui.getID("MyDockSpace");
        ImGui.dockSpace(dockSpaceId, new ImVec2(), dockNodeFlags);
        style.setWindowMinSize(minWindowSizeX, minWindowSizeY);
    }

    private void endDockSpace() {
        ImGui.end();
    }

    public void close() {
        glfwSetWindowShouldClose(windowHandle, true);
    }

    public void destroy() {
        imguiGlfw.shutdown();
        imguiGl3.shutdown();
        ImGui.destroyContext();

        Callbacks.glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }
}
