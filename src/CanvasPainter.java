import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

/**
 * 
 * @author bitrate16
 * @see http://js1k.com/2017-magic/details/2895
 */
public class CanvasPainter {
	private BufferedImage	source;
	private BufferedImage	canvas;

	private int				stroke_y;
	private int				stroke_x;
	private int				stroke_j;
	private int				stroke_i;
	private double			stroke_a;
	private double			stroke_r;
	private double			stroke_b;
	private double			stroke_g;
	public int				STROKE_STEP;
	public int				stroke_halfwidth;
	private int				stroke_Y;

	private int				iterator_t;
	private int				iterator_q;
	private int				iterator_u;
	private int				iterator_v;
	public int				iteration_iterations;

	private double			delta_r;
	private double			delta_b;
	private double			delta_g;

	private double			point_match;
	private double			point_best_match;

	private double			image_width;
	private double			image_height;
	private int				image_size4;
	private int				image_iwidth;
	private int				image_iheight;

	public int				HUE_START			= 600;
	public double			HALFWIDTH_FACTOR	= 80;
	public double			SAMPLE_U_SIZE		= 20;
	public double			SAMPLE_V_SIZE		= 20;
	public double			LUMINOSITY			= 25;
	public boolean			ENABLE_LOG			= false;

	public void setSource(BufferedImage source) {
		if (source != null) {
			// Repaint to RGB Mode
			this.source = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_RGB);
			this.source.createGraphics().drawImage(source, 0, 0, null);

			canvas = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = canvas.createGraphics();
			graphics.setPaint(Color.white);
			graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			graphics.dispose();

			this.image_width = source.getWidth();
			this.image_height = source.getHeight();
			this.image_iwidth = source.getWidth();
			this.image_iheight = source.getHeight();
			this.image_size4 = this.image_iwidth * this.image_iheight * 4;

			this.stroke_x = this.image_iwidth;
			this.stroke_y = this.iteration_iterations = 0;
			this.stroke_halfwidth = (int) Math.floor(this.image_height / HALFWIDTH_FACTOR);
			this.STROKE_STEP = 3;
		}
	}

	public BufferedImage getCanvas() {
		return canvas;
	}

	public void iterate(int iterations) {
		int total_iterations = 0;
		while (iterations-- > 0) {
			if (ENABLE_LOG) {
				if (total_iterations > 0) {
					int size = ((total_iterations - 1) + "").length();
					System.out.flush();
					for (int i = 0; i < size; i++)
						System.out.print('\b');
					System.out.flush();
					System.out.print(total_iterations++);
				} else
					System.out.print("#" + total_iterations++);
				if (iterations == 0)
					System.out.println();
			}
			iterate();
		}
	}

	@SuppressWarnings("deprecation")
	public void iterateGUI(int iterations, int delay) {
		final int its = iterations;
		Frame f = new Frame();
		f.setSize(image_iwidth, image_iheight);
		f.setTitle("Canvas Render");
		Panel p;
		f.add(p = new Panel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(image_iwidth, image_iheight);
			}

			private int	iterations			= its;
			private int	total_iterations	= 0;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (iterations-- > 0) {
					if (ENABLE_LOG) {
						if (total_iterations > 0) {
							int size = ((total_iterations - 1) + "").length();
							System.out.flush();
							for (int i = 0; i < size; i++)
								System.out.print('\b');
							System.out.flush();
							System.out.print(total_iterations++);
						} else
							System.out.print("#" + total_iterations++);
						if (iterations == 0)
							System.out.println();
					}
					iterate();
				}
				g.drawImage(getCanvas(), 0, 0, null);
			}
		});
		Timer t = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				p.repaint();
			}
		});
		t.start();
		f.show();
	}

	/**
	 * Click to stop processing
	 * 
	 * @param delay
	 */
	@SuppressWarnings("deprecation")
	public void iterateGUIHandleStop(int delay) {
		Frame f = new Frame();
		f.setSize(image_iwidth, image_iheight);
		f.setTitle("Canvas Render");
		Panel p;
		f.add(p = new Panel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(image_iwidth, image_iheight);
			}

			private int total_iterations;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (ENABLE_LOG) {
					if (total_iterations > 0) {
						int size = ((total_iterations - 1) + "").length();
						System.out.flush();
						for (int i = 0; i < size; i++)
							System.out.print('\b');
						System.out.flush();
						System.out.print(total_iterations++);
					} else
						System.out.print("#" + total_iterations++);
				}
				iterate();
				g.drawImage(getCanvas(), 0, 0, null);
			}
		});
		final Timer t = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				p.repaint();
			}
		});
		p.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				t.stop();
				f.dispose();
				if (ENABLE_LOG)
					System.out.println();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
		t.start();
		f.show();
	}

	public void iterate() {
		for (iterator_t = 0; iterator_t < image_width; iterator_t++) {
			if (stroke_y < 0 || stroke_x >= image_width || stroke_x < 0) {
				point_best_match = 0;
				for (iterator_v = 0; iterator_v < image_height; iterator_v += SAMPLE_V_SIZE)
					for (iterator_u = 0; iterator_u < image_width; iterator_u += SAMPLE_U_SIZE) {
						stroke_i = (int) (Math.floor(iterator_u + SAMPLE_U_SIZE * Math.random()) % image_iwidth);
						stroke_j = (int) (Math.floor(iterator_t + iterator_v + SAMPLE_V_SIZE * Math.random())
											% image_iheight);
						iterator_q = stroke_i * 4 + image_iwidth * 4 * stroke_j;
						stroke_r = getColorComponent(true, iterator_q) - getColorComponent(false, iterator_q);
						iterator_q++;
						stroke_g = getColorComponent(true, iterator_q) - getColorComponent(false, iterator_q);
						iterator_q++;
						stroke_b = getColorComponent(true, iterator_q) - getColorComponent(false, iterator_q);
						iterator_q++;

						point_match = max(stroke_r, stroke_g, stroke_b) / min(stroke_r, stroke_g, stroke_b)
										+ (iteration_iterations > HUE_START
												? max(stroke_r, stroke_g, stroke_b) / LUMINOSITY : 0);

						if (stroke_r > 0 && stroke_g > 0 && stroke_b > 0 && point_match > point_best_match) {
							stroke_x = stroke_i;
							stroke_y = stroke_j;
							point_best_match = point_match;
						}
					}

				iterator_q = stroke_x * 4 + image_iwidth * 4 * stroke_y;
				stroke_r = getColorComponent(true, iterator_q) - getColorComponent(false, iterator_q);
				iterator_q++;
				stroke_g = getColorComponent(true, iterator_q) - getColorComponent(false, iterator_q);
				iterator_q++;
				stroke_b = getColorComponent(true, iterator_q) - getColorComponent(false, iterator_q);
				iterator_q++;
				STROKE_STEP = -STROKE_STEP;
			}

			for (stroke_j = Math.max(0,
					stroke_y - stroke_halfwidth + 1); stroke_j < stroke_y + stroke_halfwidth; stroke_j++) {
				stroke_a = .7	* Math
						.sqrt(1 - (stroke_j - stroke_y) * (stroke_j - stroke_y) / stroke_halfwidth / stroke_halfwidth)
							/ 255;
				iterator_q = stroke_x * 4 + image_iwidth * 4 * stroke_j;

				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_r));
				iterator_q++;
				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_g));
				iterator_q++;
				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_b));
				iterator_q++;
				iterator_q++;

				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_r));
				iterator_q++;
				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_g));
				iterator_q++;
				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_b));
				iterator_q++;
				iterator_q++;

				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_r));
				iterator_q++;
				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_g));
				iterator_q++;
				addColorComponent(true, iterator_q,
						-Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_b));
				iterator_q++;
				iterator_q++;
			}

			stroke_x += STROKE_STEP;

			stroke_Y = -2;
			point_best_match = -2;
			for (stroke_j = Math.max(0, stroke_y - 1); stroke_j < stroke_y + 2; stroke_j++) {
				stroke_a = .7	* Math
						.sqrt(1 - (stroke_j - stroke_y) * (stroke_j - stroke_y) / stroke_halfwidth / stroke_halfwidth)
							/ 255;
				iterator_q = stroke_x * 4 + image_iwidth * 4 * stroke_j;

				delta_r = getColorComponent(true, iterator_q)
								- Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_r)
							- getColorComponent(false, iterator_q);
				iterator_q++;
				delta_g = getColorComponent(true, iterator_q)
								- Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_g)
							- getColorComponent(false, iterator_q);
				iterator_q++;
				delta_b = getColorComponent(true, iterator_q)
								- Math.floor(stroke_a * getColorComponent(true, iterator_q) * stroke_b)
							- getColorComponent(false, iterator_q);
				iterator_q++;

				if (delta_r > 0 && delta_g > 0 && delta_b > 0 && delta_r + delta_g + delta_b > point_best_match) {
					point_best_match = delta_r + delta_g + delta_b;
					stroke_Y = stroke_j;
				}
			}
			stroke_y = stroke_Y;
		}

		iteration_iterations++;
	}

	private double max(double a, double b, double c) {
		return Math.max(Math.max(a, b), c);
	}

	private double min(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}

	public double getColorComponent(boolean canvas, int component) {
		// component :
		// index = component / 4
		// R/G/B/A = component % 4
		// image = long array of [R, G, B, A, R, G, B, ... ]

		int component_bound = (image_size4 + (int) (component) % image_size4) % image_size4;
		int pixelIndex = component_bound / 4;
		int componentIndex = component_bound % 4;
		int x = pixelIndex % image_iwidth;
		int y = pixelIndex / image_iwidth;

		if (!canvas)
			return (source.getRGB(x, y) >> (componentIndex * 8)) & 0xFF;
		return (this.canvas.getRGB(x, y) >> (componentIndex * 8)) & 0xFF;
	}

	public void addColorComponent(boolean canvas, int component, double value) {
		// component :
		// index = component / 4
		// R/G/B/A = component % 4
		// image = long array of [R, G, B, A, R, G, B, ... ]

		int component_bound = (image_size4 + (int) (component) % image_size4) % image_size4;
		int pixelIndex = component_bound / 4;
		int componentIndex = component_bound % 4;
		int x = pixelIndex % image_iwidth;
		int y = pixelIndex / image_iwidth;

		int rgb = 0;
		int val = 0;
		if (!canvas)
			rgb = source.getRGB(x, y);
		else
			rgb = this.canvas.getRGB(x, y);

		val = (rgb >> (componentIndex * 8)) & 0xFF;
		val += value;

		rgb = (rgb & ~(255 << (componentIndex * 8))) | (val << (componentIndex * 8));

		if (!canvas)
			source.setRGB(x, y, rgb);
		else
			this.canvas.setRGB(x, y, rgb);
	}
}
