package nbradham.decToDni;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

final class DecToDni {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				BufferedImage[] bis = { ImageIO.read(DecToDni.class.getResource("/1.png")),
						ImageIO.read(DecToDni.class.getResource("/2.png")),
						ImageIO.read(DecToDni.class.getResource("/3.png")),
						ImageIO.read(DecToDni.class.getResource("/4.png")),
						ImageIO.read(DecToDni.class.getResource("/25.png")) };
				JFrame frame = new JFrame("Decimal to Dani");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new FlowLayout());
				JSpinner spin = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
				int h = spin.getPreferredSize().height;
				spin.setPreferredSize(new Dimension(60, h));
				frame.add(spin);
				JPanel pane = new JPanel() {
					private static final long serialVersionUID = 1L;
					private static final byte B_25 = 25, B_5 = 5;
					private static final float PI_2 = (float) (-Math.PI / 2);
					private final float w = bis[0].getWidth(), hw = w / 2, hh = bis[0].getHeight() / 2, wm1 = w - 1;

					@Override
					public void paint(Graphics g) {
						super.paint(g);
						int i = (int) spin.getValue(), d;
						Stack<Integer> stack = new Stack<>();
						while (i > 0) {
							d = i % B_25;
							i /= B_25;
							if (d == 0) {
								stack.push(25);
								--i;
							} else
								stack.push(d);
						}
						Graphics2D g2 = (Graphics2D) g;
						while (!stack.isEmpty()) {
							if ((i = stack.pop()) == 25)
								g2.drawRenderedImage(bis[4], null);
							else {
								if ((d = i % B_5) > 0)
									g2.drawRenderedImage(bis[d - 1], null);
								g2.rotate(PI_2, hh, hw);
								if ((i = i / B_5 % B_5) > 0)
									g2.drawRenderedImage(bis[i - 1], null);
								g2.rotate(-PI_2, hh, hw);
							}
							g2.translate(wm1, 0);
						}
					}
				};
				spin.addChangeListener(e -> pane.repaint());
				pane.setPreferredSize(new Dimension(70, h));
				frame.add(pane);
				frame.pack();
				frame.setVisible(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}