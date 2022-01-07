package io.github.alloffabric.artis.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class ColorableEntryWidget extends EntryWidget {
    private final int color;
    private final int x;
    private final int y;

    protected ColorableEntryWidget(int x, int y, int color) {
        this(new Point(x, y), color);
    }

    protected ColorableEntryWidget(Point point, int color) {
        super(point);
        this.x = point.getX();
        this.y = point.getY();
        this.color = color;
    }

    public static ColorableEntryWidget create(int x, int y, int color) {
        return create(new Point(x, y), color);
    }

    public static ColorableEntryWidget create(Point point, int color) {
        return new ColorableEntryWidget(point, color);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    @Override
    public void drawBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.drawBackground(matrices, mouseX, mouseY, delta);
        
        // TODO: Colored background
        /*int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);
    
        GlStateManager._clearColor((r + 30) / 255F, (g + 30) / 255F, (b + 30) / 255F, 1.0F);*/
    }
    
    @Override
    public List<? extends Element> children() {
        return null;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(20, 20); // TODO: actual values
    }
}
