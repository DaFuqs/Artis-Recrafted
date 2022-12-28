package io.github.alloffabric.artis.compat.rei;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Point;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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
	
	@Contract("_, _, _ -> new")
	public static @NotNull ColorableEntryWidget create(int x, int y, int color) {
		return create(new Point(x, y), color);
	}
	
	@Contract("_, _ -> new")
	public static @NotNull ColorableEntryWidget create(Point point, int color) {
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
		int r = (color & 0xFF0000) >> 16;
		int g = (color & 0xFF00) >> 8;
		int b = (color & 0xFF);
		
		RenderSystem.setShaderColor((r + 30) / 255F, (g + 30) / 255F, (b + 30) / 255F, 1.0F);
		super.drawBackground(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}
	
}
