package nbradham.decToDni;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

final class DecToDni {

	private static final byte B_25 = 25;

	private final JToggleButton[] tb1s = new JToggleButton[5];
	private final DniPane dniTopPane = new DniPane(), dniCharPane = new DniPane();
	private final ButtonGroup bg5s = new ButtonGroup(), bg1s = new ButtonGroup();
	private final JPanel dni5s = new JPanel(new GridLayout(0, 1)), dni1s = new JPanel(new GridLayout(0, 1));
	private final JSpinner spin = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
	private int d5 = 0, d1 = 1;

	private void start() {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Decimal-Dani Converter");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
			JPanel decToDni = new JPanel(), dni = new JPanel(), dniCharOp = new JPanel(new GridLayout(0, 1));
			spin.setPreferredSize(new Dimension(60, spin.getPreferredSize().height));
			decToDni.add(spin);
			decToDni.add(new JLabel("="));
			dniTopPane.setPreferredSize(new Dimension(DniPane.GW * 4, dniTopPane.getPreferredSize().height));
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
				dniTopPane.setDigits(arr);
			});
			decToDni.add(dniTopPane);
			frame.add(decToDni);
			dni5s.setBorder(new TitledBorder("5s"));
			dni1s.setBorder(new TitledBorder("1s"));
			(tb1s[0] = create1sToggleButton(0)).setEnabled(false);
			for (byte n = 1; n < 5; ++n)
				(tb1s[n] = create1sToggleButton(n)).setIcon(new ImageIcon(DniPane.GLYPHS[n - 1]));
			tb1s[1].setSelected(true);
			JToggleButton tb50 = create5sToggleButtonWAct(0, false);
			tb50.setSelected(true);
			for (byte n = 1; n < 5; ++n) {
				BufferedImage bi = new BufferedImage(DniPane.GH, DniPane.GW, BufferedImage.TYPE_INT_ARGB_PRE);
				DniPane.rotateDraw(bi.createGraphics(), DniPane.GLYPHS[n - 1]);
				JToggleButton tb = create5sToggleButtonWAct(n * 5, true);
				tb.setIcon(new ImageIcon(bi));
			}
			JToggleButton tb = create5sToggleButton();
			tb.setIcon(new ImageIcon(DniPane.GLYPHS[4]));
			tb.addActionListener(e -> {
				d5 = 25;
				tb1s[0].setEnabled(true);
				tb1s[0].doClick();
				SwingUtilities.invokeLater(() -> set1s(false));
			});
			dni.add(dni5s);
			dni.add(dni1s);
			dniCharOp.add(dniCharPane);
			dniCharOp.add(createButton("Add Digit", e -> {
				int i = dniTopPane.digits.length;
				int[] arr = Arrays.copyOf(dniTopPane.digits, i + 1);
				arr[i] = dniCharPane.digits[0];
				updateDni(arr);
			}));
			dniCharOp.add(createButton("Backspace", e -> {
				if (dniTopPane.digits.length > 0)
					updateDni(Arrays.copyOf(dniTopPane.digits, dniTopPane.digits.length - 1));
			}));
			dni.add(dniCharOp);
			frame.add(dni);
			frame.pack();
			frame.setVisible(true);
		});
	}

	private void updateDni(int... arr) {
		dniTopPane.setDigits(arr);
		int i = 0;
		for (byte n = 0; n < arr.length; ++n)
			i += arr[n] * Math.pow(25, arr.length - n - 1);
		spin.setValue(i);
	}

	private void set1s(boolean enabled) {
		for (JToggleButton t : tb1s)
			t.setEnabled(enabled);
	}

	private void updateChar() {
		dniCharPane.setDigits(d5 + d1);
	}

	private JToggleButton create1sToggleButton(int val) {
		JToggleButton b = createToggleButton(bg1s, dni1s);
		b.addActionListener(e -> {
			d1 = val;
			updateChar();
		});
		return b;
	}

	private JToggleButton create5sToggleButtonWAct(int val, boolean tb10en) {
		JToggleButton b = create5sToggleButton();
		b.addActionListener(e -> {
			d5 = val;
			set1s(true);
			tb1s[0].setEnabled(tb10en);
			if (!tb10en && tb1s[0].isSelected())
				tb1s[1].doClick();
			updateChar();
		});
		return b;
	}

	private JToggleButton create5sToggleButton() {
		JToggleButton b = createToggleButton(bg5s, dni5s);
		return b;
	}

	private static JButton createButton(String label, ActionListener l) {
		JButton b = new JButton(label);
		b.addActionListener(l);
		return b;
	}

	private static JToggleButton createToggleButton(ButtonGroup bg, JPanel pane) {
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

		private int[] digits = { 1 };

		private DniPane() {
			setPreferredSize(new Dimension(16, 16));
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

		private void setDigits(int... newDigits) {
			digits = newDigits;
			repaint();
		}

		private static final void rotateDraw(Graphics2D g2, BufferedImage bi) {
			g2.rotate(PI_2, HH, HW);
			g2.drawRenderedImage(bi, null);
		}
	}
}