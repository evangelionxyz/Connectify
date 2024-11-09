package core;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private final long hWindow;
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
        glfwWindowHint(GLFW_RESIZABLE, GLFW_RESIZABLE);

        hWindow = glfwCreateWindow(width, height, title, 0, 0);
        if (hWindow == 0) {
            throw new RuntimeException("Failed to create Window");
        }

        // get the thread stack and push a new frame
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // int *width, *height;
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(hWindow, pWidth, pHeight);
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (videoMode != null) {
                glfwSetWindowPos(hWindow,
                        (videoMode.width() - pWidth.get(0))/ 2,
                        (videoMode.height() - pHeight.get(0))/2);
            }
        } // the stack frame is popped automatically

        glfwMakeContextCurrent(hWindow);
        GL.createCapabilities();

        glfwSwapInterval(1);
        glfwShowWindow(hWindow);

        // init imgui
        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();

        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imguiGlfw.init(hWindow, true);
        String glslVersion = "#version 450";
        imguiGl3.init(glslVersion);

        glClearColor(0.1f, 0.1f,0.1f, 0.1f);
    }

    public void setGuiRenderFunction(ImGuiRenderFunction func) {
        this.imGuiRenderFunction = func;
    }

    public boolean isLooping() {
        return !glfwWindowShouldClose(hWindow);
    }

    public void swapBuffer() {
        glClear(GL_COLOR_BUFFER_BIT);

        imguiGl3.newFrame();
        imguiGlfw.newFrame();
        ImGui.newFrame();

        if (imGuiRenderFunction != null) {
            imGuiRenderFunction.render();
        }

        ImGui.render();
        imguiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }

        glfwSwapBuffers(hWindow);
    }

    public void pollEvents() {
        glfwWaitEvents();
    }

    public void destroy() {
        imguiGlfw.shutdown();
        imguiGl3.shutdown();
        ImGui.destroyContext();

        Callbacks.glfwFreeCallbacks(hWindow);
        glfwDestroyWindow(hWindow);
        glfwTerminate();
    }
}
