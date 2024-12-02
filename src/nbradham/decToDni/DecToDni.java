package nbradham.decToDni;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

final class DecToDni {

	private static final byte B_25 = 25;

	private static BufferedImage readImage(int i) throws IOException {
		return ImageIO.read(DecToDni.class.getResource("/" + i + ".png"));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Decimal-Dani Converter");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
			JSpinner spin = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
			JPanel decToDni = new JPanel();
			decToDni.setBorder(new TitledBorder("Decimal to D'ni"));
			spin.setPreferredSize(new Dimension(60, spin.getPreferredSize().height));
			decToDni.add(spin);
			DniPane dniPane = new DniPane();
			dniPane.setDigits(new int[] { 1 });
			spin.addChangeListener(e -> {
				Stack<Integer> stack = new Stack<>();
				int i = (int) spin.getValue(), d;
				while (i > 0) {
					d = i % B_25;
					i /= B_25;
					if (d == 0) {
						stack.push(25);
						--i;
					} else
						stack.push(d);
				}
				int[] arr = new int[stack.size()];
				for (byte n = 0; n < arr.length; ++n)
					arr[n] = stack.pop();
				dniPane.setDigits(arr);
			});
			decToDni.add(dniPane);
			frame.add(decToDni);
			frame.pack();
			frame.setVisible(true);
		});
	}

	private static final class DniPane extends JPanel {
		private static final BufferedImage[] bis = new BufferedImage[5];
		static {
			byte i = 0;
			try {
				while (i < 4)
					bis[i] = readImage(++i);
				bis[4] = readImage(25);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private static final long serialVersionUID = 1L;
		private static final byte B_5 = 5;
		private static final float PI_2 = (float) (-Math.PI / 2);
		private static final int w = bis[0].getWidth(), hw = w / 2, hh = bis[0].getHeight() / 2, wm1 = w - 1;

		private int[] digits = new int[0];

		private DniPane() {
			setPreferredSize(new Dimension(70, 16));
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			int d;
			for (int i : digits) {
				if (i == 25)
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

		private void setDigits(int[] newDigits) {
			digits = newDigits;
			repaint();
		}
	}
}