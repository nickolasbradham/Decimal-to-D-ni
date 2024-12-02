package nbradham.decToDni;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

final class DecToDni {

	private static final byte B_25 = 25;

	private final JToggleButton[] tb1s = new JToggleButton[5];
	private int d5 = 0;

	private void start() {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Decimal-Dani Converter");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
			JSpinner spin = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
			JPanel decToDni = new JPanel(), dni = new JPanel(), dni5s = new JPanel(new GridLayout(0, 1)),
					dni1s = new JPanel(new GridLayout(0, 1));
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
			dni5s.setBorder(new TitledBorder("5s"));
			dni1s.setBorder(new TitledBorder("1s"));
			ButtonGroup bg5s = new ButtonGroup(), bg1s = new ButtonGroup();
			tb1s[0] = createToggleButton(bg1s, dni1s);
			for (byte n = 1; n < 5; ++n) {
				(tb1s[n] = createToggleButton(bg1s, dni1s)).setIcon(new ImageIcon(DniPane.GLYPHS[n - 1]));
			}
			tb1s[1].setSelected(true);
			JToggleButton tb50 = create5sToggleButton(bg5s, dni5s, 0, false);
			tb50.setSelected(true);
			for (byte n = 1; n < 6; ++n) {
				BufferedImage bi = new BufferedImage(DniPane.GH, DniPane.GW, BufferedImage.TYPE_INT_ARGB_PRE);
				DniPane.rotateDraw(bi.createGraphics(), DniPane.GLYPHS[n - 1]);
				JToggleButton tb = create5sToggleButton(bg5s, dni5s, n * 5, true);
				tb.setIcon(new ImageIcon(bi));
			}
			dni.add(dni5s);
			dni.add(dni1s);
			frame.add(dni);
			frame.pack();
			frame.setVisible(true);
		});
	}

	private JToggleButton create5sToggleButton(ButtonGroup bg, JPanel pane, int val, boolean tb10en) {
		JToggleButton b = createToggleButton(bg, pane);
		b.addActionListener(e -> {
			d5 = val;
			tb1s[0].setEnabled(tb10en);
			if (!tb10en && tb1s[0].isSelected())
				tb1s[1].setSelected(true);
		});
		return b;
	}

	private JToggleButton createToggleButton(ButtonGroup bg, JPanel pane) {
		JToggleButton b = new JToggleButton();
		bg.add(b);
		pane.add(b);
		return b;
	}

	private static BufferedImage readImage(int i) throws IOException {
		return ImageIO.read(DecToDni.class.getResource("/" + i + ".png"));
	}

	public static void main(String[] args) {
		new DecToDni().start();
	}

	private static final class DniPane extends JPanel {
		private static final BufferedImage[] GLYPHS = new BufferedImage[5];
		static {
			byte i = 0;
			try {
				while (i < 4)
					GLYPHS[i] = readImage(++i);
				GLYPHS[4] = readImage(25);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private static final long serialVersionUID = 1L;
		private static final byte B_5 = 5;
		private static final float PI_2 = (float) (-Math.PI / 2);
		private static final int GW = GLYPHS[0].getWidth(), HW = GW / 2, GH = GLYPHS[0].getHeight(), HH = GH / 2,
				W_1 = GW - 1;

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
					g2.drawRenderedImage(GLYPHS[4], null);
				else {
					if ((d = i % B_5) > 0)
						g2.drawRenderedImage(GLYPHS[d - 1], null);
					if ((i = i / B_5 % B_5) > 0) {
						rotateDraw(g2, GLYPHS[i - 1]);
						g2.rotate(-PI_2, HH, HW);
					}
				}
				g2.translate(W_1, 0);
			}
		}

		private void setDigits(int[] newDigits) {
			digits = newDigits;
			repaint();
		}

		private static final void rotateDraw(Graphics2D g2, BufferedImage bi) {
			g2.rotate(PI_2, HH, HW);
			g2.drawRenderedImage(bi, null);
		}
	}
}