package com.ynov.devnotebook.ui;

import javax.swing.*;
import java.awt.*;

public final class IconFactory {
	private IconFactory() {}

	public static Icon plusIcon(int w, int h) {
		return new Icon() {
			@Override public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(60, 60, 60));
				int cx = x + w / 2; int cy = y + h / 2; int t = Math.max(1, Math.min(w, h) / 6);
				g2.fillRect(cx - t/2, y + h/6, t, h - h/3);
				g2.fillRect(x + w/6, cy - t/2, w - w/3, t);
				g2.dispose();
			}
			@Override public int getIconWidth() { return w; }
			@Override public int getIconHeight() { return h; }
		};
	}
}
