import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class CanvasPainterTest {

	public static void main(String[] args) throws IOException {

		System.out.println("CanvasPainter");
		System.out.println("write \"help\" for help");

		CanvasPainter painter = new CanvasPainter();

		String in = null;
		Scanner s = new Scanner(System.in);

		boolean log_enabled = false;
		String fname = null;
		String fsimplename = null;
		boolean gui_enabled = false;
		int gui_delta = 100;
		BufferedImage image = null;

		while ((in = s.nextLine()) != null && !in.equals("exit")) {
			if (in.equals("help")) {
				System.out.println("v2.2");
				System.out.println("Command help:");
				System.out.println("help - print this");
				System.out.println("setf <filename> - set input file name");
				System.out.println("startGUI - iterate and render result in window. Click on window to end and save");
				System.out.println("setlog <1 or 0> - enable or disable log");
				System.out.println("setGUI <1 or 0> - set gui mode on or off (render result in gui while iterating)");
				System.out.println("setGUIdelta - set delta between GUI frame updates (default - 100)");
				System.out.println(
						"iterate <iterations count> - process image with iterations amount given as parameter. After - save result image");
				System.out.println(
						"gifFrames <amount of frames> <iterations per frame> - render image and save it's frames into a gif file");
				System.out.println(
						"gifIterations <amount of repeats> <iterations per repeat> <frame delay> - iterate on image for amount times and save all results into gif");
				System.out.println("reset - reset iterations");
				System.out.println("citerate <iterations count> - reset iterations then iterate");
				System.out.println("sethwf <halfwidth> - set halfwidth scale factor");
				System.out.println("sethw <halfwidth> - set halfwidth");
				System.out.println("Program help:");
				System.out.println("Output file could be found in /out/<time>_<total iterations count>_<filename>");
				System.out.println("1 - setf <your file>");
				System.out.println("2 - iterate <iterations count>");
			} else if (in.startsWith("setf ")) {
				fname = in.substring(5).trim();
				fsimplename = new File(fname).getName();
				fsimplename = fsimplename.substring(0, fsimplename.lastIndexOf('.'));
				try {
					image = ImageIO.read(new File(fname));
					painter.setSource(image);
					if (log_enabled)
						System.out.println("File set to " + fname);
				} catch (Exception e) {
					System.out.println("File does not exists or is not an image file");
				}
			} else if (in.startsWith("setGUI ")) {
				gui_enabled = in.substring(7).trim().equals("1");
				if (log_enabled)
					if (gui_enabled)
						System.out.println("GUI enabled");
					else
						System.out.println("GUI disabled");
			} else if (in.startsWith("setlog ")) {
				log_enabled = in.substring(7).trim().equals("1");
				if (log_enabled)
					System.out.println("log enabled");
				else
					System.out.println("log disabled");
				painter.ENABLE_LOG = log_enabled;
			} else if (in.startsWith("setGUIdelta ")) {
				try {
					gui_delta = Integer.parseInt(in.substring(12).trim());
					if (log_enabled)
						System.out.println("GUI delta set to " + gui_delta);
				} catch (Exception e) {
					System.out.println("Invalid number format");
				}
			} else if (in.startsWith("iterate ")) {
				try {
					int iterations = Integer.parseInt(in.substring(8).trim());
					try {
						if (image == null) {
							System.out.println("Image not set");
							continue;
						}
						if (gui_enabled)
							painter.iterateGUI(iterations, gui_delta);
						else
							painter.iterate(iterations);

						File f = new File("out/"	+ System.currentTimeMillis() + "_" + painter.iteration_iterations + "_"
											+ fsimplename + ".png");
						if (log_enabled)
							System.out.println("saving in " + f.getPath());
						f.getParentFile().mkdir();
						f.getParentFile().mkdirs();
						f.createNewFile();
						ImageIO.write(painter.getCanvas(), "PNG", f);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					System.out.println("Invalid number format");
				}
			} else if (in.startsWith("citerate ")) {
				try {
					int iterations = Integer.parseInt(in.substring(9).trim());
					try {
						if (image == null) {
							System.out.println("Image not set");
							continue;
						}
						painter.setSource(image);
						if (gui_enabled)
							painter.iterateGUI(iterations, gui_delta);
						else
							painter.iterate(iterations);

						File f = new File("out/"	+ System.currentTimeMillis() + "_" + painter.iteration_iterations + "_"
											+ fsimplename + ".png");
						if (log_enabled)
							System.out.println("saving in " + f.getPath());
						f.getParentFile().mkdir();
						f.getParentFile().mkdirs();
						f.createNewFile();
						ImageIO.write(painter.getCanvas(), "PNG", f);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					System.out.println("Invalid number format");
				}
			} else if (in.equals("startGUI")) {
				try {
					if (image == null) {
						System.out.println("Image not set");
						continue;
					}
					painter.iterateGUIHandleStop(gui_delta);

					File f = new File("out/"	+ System.currentTimeMillis() + "_" + painter.iteration_iterations + "_"
										+ fsimplename + ".png");
					if (log_enabled)
						System.out.println("saving in " + f.getPath());
					f.getParentFile().mkdir();
					f.getParentFile().mkdirs();
					f.createNewFile();
					ImageIO.write(painter.getCanvas(), "PNG", f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (in.startsWith("gifFrames")) {
				try {
					String[] parts = in.split("[ ]+");
					if (parts.length != 4) {
						System.out.println("Expected 3 parameters");
						continue;
					}
					int frames = 0;
					int iterations = 0;
					int delay = 0;
					try {
						frames = Integer.parseInt(parts[1]);
						iterations = Integer.parseInt(parts[2]);
						delay = Integer.parseInt(parts[3]);
					} catch (Exception e) {
						System.out.println("Invalid number format");
						continue;
					}
					if (image == null) {
						System.out.println("Image not set");
						continue;
					}

					GifSequenceWriter writer = null;
					File f = new File(
							"out/" + System.currentTimeMillis() + "_" + iterations + "_" + fsimplename + ".gif");
					if (log_enabled)
						System.out.println("writing to " + f.getPath());
					f.getParentFile().mkdir();
					f.getParentFile().mkdirs();
					f.createNewFile();
					ImageOutputStream output = new FileImageOutputStream(f);

					for (int i = 0; i < frames; i++) {
						if (log_enabled)
							System.out.println("Frame: " + i);
						painter.iterate(iterations);
						if (i == 0)
							writer = new GifSequenceWriter(output, painter.getCanvas().getType(), delay, false);

						writer.writeToSequence(painter.getCanvas());
					}
					if (log_enabled)
						System.out.println("saved in " + f.getPath());

					writer.close();
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (in.startsWith("gifIterations")) {
				try {
					String[] parts = in.split("[ ]+");
					if (parts.length != 4) {
						System.out.println("Expected 3 parameters");
						continue;
					}
					int frames = 0;
					int iterations = 0;
					int delay = 0;
					try {
						frames = Integer.parseInt(parts[1]);
						iterations = Integer.parseInt(parts[2]) + painter.iteration_iterations;
						delay = Integer.parseInt(parts[3]);
					} catch (Exception e) {
						System.out.println("Invalid number format");
						continue;
					}
					if (image == null) {
						System.out.println("Image not set");
						continue;
					}

					GifSequenceWriter writer = null;
					File f = new File(
							"out/" + System.currentTimeMillis() + "_" + iterations + "_" + fsimplename + ".gif");
					if (log_enabled)
						System.out.println("writing to " + f.getPath());
					f.getParentFile().mkdir();
					f.getParentFile().mkdirs();
					f.createNewFile();
					ImageOutputStream output = new FileImageOutputStream(f);

					for (int i = 0; i < frames; i++) {
						if (log_enabled)
							System.out.println("Frame: " + i);
						painter.setSource(image);
						painter.iterate(iterations);
						if (i == 0)
							writer = new GifSequenceWriter(output, painter.getCanvas().getType(), delay, false);

						writer.writeToSequence(painter.getCanvas());
					}
					if (log_enabled)
						System.out.println("saved in " + f.getPath());

					writer.close();
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (in.equals("reset")) {
				if (image == null) {
					System.out.println("Image not set");
					continue;
				}
				if (log_enabled)
					System.out.println("Resetting iterations");
				painter.setSource(image);

			} else if (in.startsWith("sethwf ")) {
				try {
					int halfwidth = Integer.parseInt(in.substring(7).trim());

					painter.HALFWIDTH_FACTOR = halfwidth;
				} catch (Exception e) {
					System.out.println("Invalid number format");
				}
			} else if (in.startsWith("sethw ")) {
				try {
					int halfwidth = Integer.parseInt(in.substring(6).trim());

					painter.stroke_halfwidth = halfwidth;
				} catch (Exception e) {
					System.out.println("Invalid number format");
				}
			}
		}

		s.close();
	}
}
