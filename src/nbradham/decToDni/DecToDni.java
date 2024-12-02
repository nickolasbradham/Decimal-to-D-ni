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
				JSpinner spin = new JSpinner();
				int h = spin.getPreferredSize().height;
				spin.setPreferredSize(new Dimension(50, h));
				frame.add(spin);
				JPanel pane = new JPanel() {
					private static final long serialVersionUID = 1L;
					private static final byte B_25 = 25, B_5 = 5;
					private static final float PI_2 = (float) (-Math.PI / 2);
					private final float w = bis[0].getWidth(), hw = w / 2, hh = bis[0].getHeight() / 2, wm1 = w - 1;

					@Override
					public void paint(Graphics g) {
						super.paint(g);
						int i = (int) spin.getValue();
						Stack<Integer> stack = new Stack<>();
						while (i > 0) {
							stack.push(i % B_25);
							i /= B_25;
						}
						System.out.println(stack);
						Graphics2D g2d = (Graphics2D) g;
						//TODO Fix this.
						boolean f25 = false;
						while (!stack.isEmpty()) {
							i = stack.pop();
							if (i == 1 && !stack.isEmpty() && stack.peek() == 0) {
								f25 = true;
								drawDigit(g2d, i == 0 ? 24 : --i);
							} else
								drawDigit(g2d, i);
						}
						if (f25)
							g2d.drawRenderedImage(bis[4], null);
					}

					private final void drawDigit(Graphics2D g2d, int i) {
						boolean drew = drawImg(g2d, i);
						g2d.rotate(PI_2, hh, hw);
						drew = drawImg(g2d, i / B_5) || drew;
						g2d.rotate(-PI_2, hh, hw);
						if (drew)
							g2d.translate(wm1, 0);
					}

					private final boolean drawImg(Graphics2D g, int i) {
						if ((i %= B_5) > 0) {
							g.drawRenderedImage(bis[i - 1], null);
							return true;
						}
						return false;
					}
				};
				spin.addChangeListener(e -> pane.repaint());
				pane.setPreferredSize(new Dimension(100, h));
				frame.add(pane);
				frame.pack();
				frame.setVisible(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}