package core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Window {
    private final long hWindow;
    public Window(int width, int height, String title) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

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
        glfwSwapInterval(1);
        glfwShowWindow(hWindow);

        GL.createCapabilities();
        glClearColor(0.1f, 0.1f,0.1f, 0.1f);
    }

    public boolean isLooping() {
        return !glfwWindowShouldClose(hWindow);
    }

    public void swapBuffer() {
        glfwSwapBuffers(hWindow);
    }

    public void pollEvents() {
        glfwWaitEvents();
    }
}
