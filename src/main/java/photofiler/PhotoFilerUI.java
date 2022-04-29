package photofiler;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


public class PhotoFilerUI extends JFrame implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private Properties prop = new Properties();
	private File lastDir = new File("./");
	private final JFileChooser fch = new JFileChooser("./");
	private JPanel contentPane;
	private final ButtonGroup btnGrpAction = new ButtonGroup();
	private final ButtonGroup btnGrpOption = new ButtonGroup();
	private JTextField txtSrcDir;
	private JTextField txtDstDir;
	private JTextArea textArea;
	private JRadioButton rdoAction0;
	private JRadioButton rdoAction1;
	private JRadioButton rdoOption0;
	private JRadioButton rdoOption1;
	private JRadioButton rdoOption2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PhotoFilerUI frame = new PhotoFilerUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PhotoFilerUI() {
		readCfg();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				writeCfg();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				writeCfg();
			}
		});

		setTitle("Photo file tree arranger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));

		JLabel lblAction = new JLabel("Action:");
		lblAction.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.add(lblAction);

		rdoAction0 = new JRadioButton("Move;");
		lblAction.setLabelFor(rdoAction0);
		rdoAction0.setFont(new Font("SansSerif", Font.PLAIN, 12));
		btnGrpAction.add(rdoAction0);
		rdoAction0.setSelected(true);
		panel.add(rdoAction0);

		rdoAction1 = new JRadioButton("Copy;");
		rdoAction1.setPreferredSize(new Dimension(160, 21));
		rdoAction1.setFont(new Font("SansSerif", Font.PLAIN, 12));
		btnGrpAction.add(rdoAction1);
		panel.add(rdoAction1);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));

		JLabel lblOption = new JLabel("Option:");
		lblOption.setAlignmentY(0.0f);
		panel_1.add(lblOption);

		rdoOption0 = new JRadioButton("Act if not exists;");
		lblOption.setLabelFor(rdoOption0);
		rdoOption0.setSelected(true);
		btnGrpOption.add(rdoOption0);
		rdoOption0.setFont(new Font("SansSerif", Font.PLAIN, 12));
		panel_1.add(rdoOption0);

		rdoOption1 = new JRadioButton("Act overwrite;");
		btnGrpOption.add(rdoOption1);
		rdoOption1.setFont(new Font("SansSerif", Font.PLAIN, 12));
		panel_1.add(rdoOption1);

		rdoOption2 = new JRadioButton("Rename if exists;");
		btnGrpOption.add(rdoOption2);
		rdoOption2.setFont(new Font("SansSerif", Font.PLAIN, 12));
		panel_1.add(rdoOption2);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel_2);
		panel_2.setLayout(new BorderLayout(2, 0));

		JLabel lblSourceDir = new JLabel("Source dir:");
		lblSourceDir.setPreferredSize(new Dimension(100, 13));
		lblSourceDir.setAlignmentY(0.0f);
		panel_2.add(lblSourceDir, BorderLayout.WEST);

		txtSrcDir = new JTextField();
		panel_2.add(txtSrcDir);
		txtSrcDir.setColumns(30);

		JButton btnRefSrc = new JButton("...");
		btnRefSrc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectDir("Source Directory", txtSrcDir);
			}
		});
		panel_2.add(btnRefSrc, BorderLayout.EAST);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel_3);
		panel_3.setLayout(new BorderLayout(2, 0));

		JLabel lblDestinationDir = new JLabel("Destination dir:");
		lblDestinationDir.setPreferredSize(new Dimension(100, 13));
		lblDestinationDir.setAlignmentY(0.0f);
		panel_3.add(lblDestinationDir, BorderLayout.WEST);

		txtDstDir = new JTextField();
		txtDstDir.setColumns(30);
		panel_3.add(txtDstDir, BorderLayout.CENTER);

		JButton btnRefDst = new JButton("...");
		btnRefDst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectDir("Destination Directory", txtDstDir);
			}
		});
		panel_3.add(btnRefDst, BorderLayout.EAST);

		JPanel panel_4 = new JPanel();
		panel_4.setPreferredSize(new Dimension(400, 32));
		contentPane.add(panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));

		JButton btnOK = new JButton("OK");
		btnOK.setHorizontalAlignment(SwingConstants.LEFT);
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		panel_4.add(btnOK);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		panel_4.add(btnClose);

		JPanel panel_5 = new JPanel();
		contentPane.add(panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(480, 200));
		panel_5.add(scrollPane, BorderLayout.NORTH);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
	}

	private void readCfg(){
		InputStream is = null;
		try {
			is = new FileInputStream("./photofiler.properires");
			prop.load(is);
			lastDir = new File(prop.getProperty("lastDir", "./"));
		} catch (FileNotFoundException e) {
			e.toString();
		} catch (IOException e) {
			e.toString();
		}finally{
			if (is != null){
				try{
					is.close();
				}catch(IOException e){}
			}
		}
	}

	private void writeCfg(){
		OutputStream os = null;
		try {
			os = new FileOutputStream("./photofiler.properires");
			prop.store(os, "PhotoFiler");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (os != null){
				try{
					os.close();
				}catch(IOException e){}
			}
		}
	}

	private void selectDir(String title, JTextField tbox){
		fch.setCurrentDirectory(lastDir);
		fch.setDialogTitle(title);
		fch.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (JFileChooser.APPROVE_OPTION != fch.showOpenDialog(this)){
			return;
		}
		lastDir = fch.getSelectedFile();
		if (lastDir.isFile()){
			lastDir = lastDir.getParentFile();
		}
		prop.setProperty("lastDir", lastDir.getAbsolutePath());
		tbox.setText(lastDir.getAbsolutePath());
	}

	private void execute(){
		String srcDir = txtSrcDir.getText();
		String dstDir = txtDstDir.getText();
		StringBuilder actOpt = new StringBuilder();
		if (srcDir == null || "".equals(srcDir) || dstDir == null || "".equals(srcDir)){
			JOptionPane.showMessageDialog(this, "Directory not selected"
										  , "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		textArea.setText("");
		if (rdoAction0.isSelected()){
			actOpt.append('0');
		}else if (rdoAction1.isSelected()){
			actOpt.append('1');
		}else{
			actOpt.append('0');
		}
		if (rdoOption0.isSelected()){
			actOpt.append('0');
		}else if (rdoOption1.isSelected()){
			actOpt.append('1');
		}else if (rdoOption2.isSelected()){
			actOpt.append('2');
		}else{
			actOpt.append('0');
		}
		DateTree sw = new DateTree(srcDir, dstDir, actOpt.toString());
		sw.addPropertyChangeListener(this);
		sw.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if ("process".equals(prop)) {
			textArea.append(evt.getNewValue().toString());
			textArea.append("\n");
		} else if ("state".equals(prop)) {
			JOptionPane.showMessageDialog(this, evt.getNewValue(), "State info", JOptionPane.INFORMATION_MESSAGE);
		} else if ("event".equals(prop)) {
			JOptionPane.showMessageDialog(this, evt.getNewValue(), "Event info", JOptionPane.INFORMATION_MESSAGE);
		} else {
			System.out.print(prop);
			System.out.print(':');
			System.out.println(evt.getNewValue());
		}
	}

	private void close(){
		this.dispose();
	}
	protected JRadioButton getRdoAction0() {
		return rdoAction0;
	}
	protected JRadioButton getRdoAction1() {
		return rdoAction1;
	}
	protected JRadioButton getRdoOption0() {
		return rdoOption0;
	}
	protected JRadioButton getRdoOption1() {
		return rdoOption1;
	}
	protected JRadioButton getRdoOption2() {
		return rdoOption2;
	}
}
