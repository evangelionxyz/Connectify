package views;

import imgui.type.ImBoolean;

public abstract class WindowBase {

    protected String title;
    protected ImBoolean show = new ImBoolean(true);

    public WindowBase() {
        this.title = "empty window";
    }

    public WindowBase(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void open() {
        show.set(true);
    }

    public void close() {
        show.set(false);
    }

    public abstract void init();
    public abstract void render();
}
