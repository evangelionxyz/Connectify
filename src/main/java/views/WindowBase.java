package views;

public abstract class WindowBase {

    private String title;

    public WindowBase() {
        this.title = "empty window";
    }

    public WindowBase(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void init();
    public abstract void render();
}
