package photofiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.swing.SwingWorker;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * @author kamali
 *
 */
public class DateTree extends SwingWorker<String, String> {
	private static final MessageFormat fmtYMD = new MessageFormat("{0,number,0000}_{1,number,00}_{2,number,00}");
	static {
		((NumberFormat)fmtYMD.getFormats()[0]).setGroupingUsed(false);
	}
	private final Runtime rt = Runtime.getRuntime();
	private final String osName;
	//private final String osArch;
	private final File dir1;
	private final File dir2;
	private final String move;
	private final StringBuffer message = new StringBuffer();

	private void convert(File dir1) {
		Object[] ymd = new Object[3];
		String[] cmd = new String[5];
		File[] files = dir1.listFiles();
		for (File f : files){
			String name = f.getName();
			if (".".equals(name) || "..".equals(name))
				continue;
			if (f.isDirectory()){
				convert(f);
			}else{
				if (!name.toLowerCase().endsWith(".jpg")
				&& !name.toLowerCase().endsWith(".jpeg")
				&& !name.toLowerCase().endsWith(".mov")
				&& !name.toLowerCase().endsWith(".mp4")) {
					continue;
				}
				long time = f.lastModified();
				try {
				    Metadata metadata = ImageMetadataReader.readMetadata(f);
				    Directory dir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
				    if (dir != null) {
					    int[] dateTags = new int[] {
					    	ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL,
					    	ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED,
					    	ExifSubIFDDirectory.TAG_DATETIME
						};
					    for (int dateTag : dateTags) {
						    if (dir.containsTag(dateTag)) {
						    	time = dir.getDate(dateTag, TimeZone.getDefault()).getTime();
						    	break;
						    }
					    }
				    }
				} catch (ImageProcessingException e) {
					message.replace(0, message.length(), f.getPath());
					message.append(" has no JPEG metadata.");
					publish(message.toString());
				} catch (IOException e) {
					message.replace(0, message.length(), f.getPath());
					message.append(" has no JPEG metadata.");
				}
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(time);
				int y = cal.get(Calendar.YEAR);
				int M = cal.get(Calendar.MONTH) + 1;
				int d = cal.get(Calendar.DATE);
				ymd[0] = Integer.valueOf(y);
				ymd[1] = Integer.valueOf(M);
				ymd[2] = Integer.valueOf(d);
				File dest = new File(new File(dir2, String.valueOf(y)), fmtYMD.format(ymd));
				dest.mkdirs();
				if (dest.exists()) {
					File dfile = new File(dest, name);
					if (!dfile.exists() || move.charAt(1) != '0') {
						if ("Linux".equals(osName)){
							cmd[0] = move.charAt(0) == '1' ? "cp" : "mv";
							cmd[1] = "-v";
							cmd[2] = "-f";
							cmd[3] = f.getPath();
							cmd[4] = dfile.getPath();
						}else {
							cmd[0] = "cmd";
							cmd[1] = "/c";
							cmd[2] = move.charAt(0) == '1' ? "copy" : "move";
							cmd[3] = f.getPath();
							cmd[4] = dfile.getPath();
						}
						message.replace(0, message.length(), cmd[0]);
						message.append(' ');
						message.append(cmd[1]);
						message.append(' ');
						message.append(cmd[2]);
						message.append(' ');
						message.append(cmd[3]);
						message.append(' ');
						message.append(cmd[4]);
						publish(message.toString());
						try {
							Process process = rt.exec(cmd);
							BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
							BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							boolean idle = false;
							while (!idle) {
								String msg = null;
								idle = true;
								if (out.ready()) {
									idle = false;
									msg = out.readLine();
									publish(msg);
								}
								if (err.ready()) {
									idle = false;
									msg = err.readLine();
									publish(msg);
								}
							}
							try {
								process.waitFor();
							} catch (InterruptedException e) {
								publish("process interrupted");
							}
						} catch (IOException e) {
							publish("process I/O error");
						}
					}
				}else{
					message.replace(0, message.length(), "Can't create directory ");
					message.append(dest.getPath());
					publish(message.toString());
				}
			}
		}
	}

	/**
	 *
	 */
	public DateTree(String src, String dest, String move) {
		osName = System.getProperty("os.name");
		//osArch = System.getProperty("os.arch");
		dir1 = new File(src);
		dir2 = new File(dest);
		this.move = move;
	}

	@Override
	protected String doInBackground() throws Exception {
		if (dir1.isDirectory() && dir2.isDirectory()) {
			convert(dir1);
		}
		return "finish";
	}

	/**
	 * 完了時にGUI側で実行される
	 */
	@Override
	protected void done() {
		super.done();
		//firePropertyChange("event", null, "done");
	}

	@Override
	protected void process(List<String> chunks) {
		for (String p : chunks) {
			firePropertyChange("process", null, p);
		}
	}

}
