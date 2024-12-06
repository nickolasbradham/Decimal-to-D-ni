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

/**
 * Handles the entire program.
 */
final class DecToDni {

	private static final byte B_25 = 25;

	private final JToggleButton[] tb1s = new JToggleButton[5];
	private final DniPane dniTopPane = new DniPane(), dniCharPane = new DniPane();
	private final ButtonGroup bg5s = new ButtonGroup(), bg1s = new ButtonGroup();
	private final JPanel dni5s = new JPanel(new GridLayout(0, 1)), dni1s = new JPanel(new GridLayout(0, 1));
	private final JSpinner spin = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
	private int d5 = 0, d1 = 1;

	/**
	 * Creates and shows the GUI.
	 */
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
				SwingUtilities.invokeLater(() -> set1sEnabled(false));
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

	/**
	 * Updates the D'ni number and sets the Spinner to the converted Decimal value.
	 * 
	 * @param dniDigits The digits of the D'ni number.
	 */
	private void updateDni(int... dniDigits) {
		dniTopPane.setDigits(dniDigits);
		int i = 0;
		for (byte n = 0; n < dniDigits.length; ++n)
			i += dniDigits[n] * Math.pow(25, dniDigits.length - n - 1);
		spin.setValue(i);
	}

	/**
	 * Sets the enable state of all {@link JToggleButton}s in the 1s column.
	 * 
	 * @param enabled Whether the buttons should be enabled or not.
	 */
	private void set1sEnabled(boolean enabled) {
		for (JToggleButton t : tb1s)
			t.setEnabled(enabled);
	}

	/**
	 * Updates the D'ni char that would be added if the add button is clicked.
	 */
	private void updateDniChar() {
		dniCharPane.setDigits(d5 + d1);
	}

	/**
	 * Calls {@link #createToggleButton(ButtonGroup, JPanel)}, passing in the 1s
	 * button group and pane and adds an action listener.
	 * 
	 * @param val The decimal value of this button.
	 * @return The instance of the created button.
	 */
	private JToggleButton create1sToggleButton(int val) {
		JToggleButton b = createToggleButton(bg1s, dni1s);
		b.addActionListener(e -> {
			d1 = val;
			updateDniChar();
		});
		return b;
	}

	/**
	 * Calls {@link #create5sToggleButtonWAct(int, boolean)}, passing in {@code val}
	 * and {@code tb10en} and adds an action listener.
	 * 
	 * @param val    The decimal value of this button.
	 * @param tb10en What to set the enable state of the 0 button in the 1s column
	 *               on click.
	 * @return The instance of the created button.
	 */
	private JToggleButton create5sToggleButtonWAct(int val, boolean tb10en) {
		JToggleButton b = create5sToggleButton();
		b.addActionListener(e -> {
			d5 = val;
			set1sEnabled(true);
			tb1s[0].setEnabled(tb10en);
			if (!tb10en && tb1s[0].isSelected())
				tb1s[1].doClick();
			updateDniChar();
		});
		return b;
	}

	/**
	 * Calls {@link #createToggleButton(ButtonGroup, JPanel)}, passing in the 5s
	 * button group and pane.
	 * 
	 * @return The new button instance.
	 */
	private JToggleButton create5sToggleButton() {
		return createToggleButton(bg5s, dni5s);
	}

	/**
	 * Constructs a new {@link JButton}, sets the label to {@code label}, and adds
	 * {@code l} to the listeners.
	 * 
	 * @param label The label of the button.
	 * @param l     The {@link ActionListener} to add to the button.
	 * @return The new button instance.
	 */
	private static JButton createButton(String label, ActionListener l) {
		JButton b = new JButton(label);
		b.addActionListener(l);
		return b;
	}

	/**
	 * Constructs a new {@link JToggleButton} and adds it to the specified
	 * {@link ButtonGroup} and {@link JPanel}.
	 * 
	 * @param bg   The {@link ButtonGroup} to add the new button too.
	 * @param pane The {@link JPanel} to add the new button too.
	 * @return The new button instance.
	 */
	private static JToggleButton createToggleButton(ButtonGroup bg, JPanel pane) {
		JToggleButton b = new JToggleButton();
		bg.add(b);
		pane.add(b);
		return b;
	}

	/**
	 * Constructs and starts a new {@link #DecToDni()} instance.
	 * 
	 * @param args Ignored.
	 */
	public static void main(String[] args) {
		new DecToDni().start();
	}

	/**
	 * Used to display D'ni numbers.
	 */
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

		/**
		 * Constructs a new {@link DniPane}.
		 */
		private DniPane() {
			setPreferredSize(new Dimension(16, 16));
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			g.translate(getWidth() / 2 - digits.length * GW / 2, 0);
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

		/**
		 * Sets the D'ni digits to display.
		 * 
		 * @param newDigits The new D'ni digits. Each digit is in Decimal an in the
		 *                  range of [0,25].
		 */
		private void setDigits(int... newDigits) {
			digits = newDigits;
			repaint();
		}

		/**
		 * Rotates {@code g2} by -pi/2 and draws {@code bi} to it.
		 * 
		 * @param g2 The {@link Gaphics2D} instance to use.
		 * @param bi The {@link BufferedImage} to draw to {@code g2}.
		 */
		private static final void rotateDraw(Graphics2D g2, BufferedImage bi) {
			g2.rotate(PI_2, HH, HW);
			g2.drawRenderedImage(bi, null);
		}

		/**
		 * Retrieves the image of the specified digit character from disk.
		 * 
		 * @param i The digit to retrieve.
		 * @return The character image.
		 * @throws IOException Thrown by {@link ImageIO#read(java.net.URL)}.
		 */
		private static BufferedImage readImage(int i) throws IOException {
			return ImageIO.read(DecToDni.class.getResource("/" + i + ".png"));
		}
	}
}