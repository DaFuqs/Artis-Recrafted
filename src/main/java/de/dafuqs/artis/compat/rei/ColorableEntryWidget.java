package de.dafuqs.artis.compat.rei;

import com.mojang.blaze3d.systems.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.impl.client.gui.widget.*;
import net.minecraft.client.gui.*;
import org.jetbrains.annotations.*;

import java.util.*;

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
	public void drawBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		int r = (color & 0xFF0000) >> 16;
		int g = (color & 0xFF00) >> 8;
		int b = (color & 0xFF);
		
		RenderSystem.setShaderColor((r + 30) / 255F, (g + 30) / 255F, (b + 30) / 255F, 1.0F);
		super.drawBackground(context, mouseX, mouseY, delta);
	}
	
	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}
	
}
