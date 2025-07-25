package ui;

import java.util.ArrayList;
import java.util.List;

import graphics.IGraphics;
import input.GameMouseListener;
import input.GameMouseEvent;
import input.MouseListenerRegistrar;

public abstract class UIComponent {
    protected int x, y, width, height;
    protected boolean visible = true;
    protected List<UIComponent> children = new ArrayList<>();

    public UIComponent(int x, int y, int width, int height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public void addChild(UIComponent child) {
        children.add(child);
    }

    public void render(IGraphics g) {
        if (!visible) return;
        draw(g);
        for (UIComponent child : children) {
            child.render(g);
        }
    }

    protected abstract void draw(IGraphics g);

    public boolean handleMouse(GameMouseEvent e) {
        if (!visible) return false;
        for (UIComponent child : children) {
            if (child.handleMouse(e)) return true;
        }
        return onMouse(e);
    }

    protected boolean onMouse(GameMouseEvent e) {
        // Override in subclasses for custom behavior
        return false;
    }

    public boolean contains(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public static void registerAllListeners(UIComponent component, MouseListenerRegistrar registrar) {
        if (component instanceof GameMouseListener) {
            registrar.addGameMouseListener((GameMouseListener) component);
        }
        for (UIComponent child : component.children) {
            registerAllListeners(child, registrar);
        }
    }
} 