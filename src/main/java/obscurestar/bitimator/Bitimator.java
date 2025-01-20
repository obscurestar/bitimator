package obscurestar.bitimator;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;

public class Bitimator extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JButton mBtnBegin;
	private JButton mBtnBack;
	private JButton mBtnPlay;
	private JButton mBtnFwd;
	private JButton mBtnEnd;
	private JButton mBtnAdd;
	private JButton mBtnDelete;
	private BitPanel mBitPanel;
	private JLabel mLabelInfo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty(
            "com.apple.mrj.application.apple.menu.about.name", "Name");
        
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Bitimator frame = new Bitimator();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void addMenuItems()
	{	
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Open");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Open Recent");
		mnNewMenu.add(mntmNewMenuItem_2);
		
		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Close");
		mnNewMenu.add(mntmNewMenuItem_3);
		
		JSeparator separator_1 = new JSeparator();
		mnNewMenu.add(separator_1);
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Save");
		mnNewMenu.add(mntmNewMenuItem_4);
		
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Save As");
		mnNewMenu.add(mntmNewMenuItem_5);
		
		JSeparator separator_2 = new JSeparator();
		mnNewMenu.add(separator_2);
		
		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Preferences");
		mnNewMenu.add(mntmNewMenuItem_6);	
	}
	
	public void addButtons()
	{
		JPanel panel_buttons = new JPanel();
		contentPane.add(panel_buttons, BorderLayout.SOUTH);
		
		Dimension button_dims = new Dimension(40,30);
		mBtnBegin = new JButton("|<");
		mBtnBegin.setPreferredSize(button_dims);
		panel_buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_buttons.add(mBtnBegin);
		mBtnBegin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.begin();
			}
		});
		
		mBtnBack = new JButton("<");
		mBtnBack.setPreferredSize(button_dims);
		panel_buttons.add(mBtnBack);
		mBtnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.back();
			}
		});
		
		mBtnPlay = new JButton("|>");
		mBtnPlay.setPreferredSize(button_dims);
		panel_buttons.add(mBtnPlay);
		mBtnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.play();
			}
		});
		
		mBtnFwd = new JButton(">");
		mBtnFwd.setPreferredSize(button_dims);
		panel_buttons.add(mBtnFwd);
		mBtnFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.fwd();
			}
		});
		
		mBtnEnd = new JButton(">|");
		mBtnEnd.setPreferredSize(button_dims);
		panel_buttons.add(mBtnEnd);
		mBtnEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.end();
			}
		});
		
		JLabel lblSpacer1 = new JLabel("      ");
		mBtnBegin.setPreferredSize(button_dims);
		panel_buttons.add(lblSpacer1);
		
		mBtnAdd = new JButton("Add");
		mBtnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.add();
			}
		});
		
		JCheckBox chkOnion = new JCheckBox("Onion");
		panel_buttons.add(chkOnion);
		chkOnion.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    mBitPanel.setOnion(true);
                } else {
                    mBitPanel.setOnion(false);
                }
            }
        });
		
		JLabel lblNewLabel_2 = new JLabel("   ");
		panel_buttons.add(lblNewLabel_2);
		
		mLabelInfo = new JLabel("(1 of 1)");
		panel_buttons.add(mLabelInfo);
		
		JLabel lblSpacer3 = new JLabel("   ");
		panel_buttons.add(lblSpacer3);
		panel_buttons.add(mBtnAdd);
		
		JLabel lblSpacer4 = new JLabel("   ");
		panel_buttons.add(lblSpacer4);
		
		mBtnDelete = new JButton("Del");
		panel_buttons.add(mBtnDelete);
		mBtnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.delete();
			}
		});
	}
	
	public void setFrameInfo(String text)
	{
		mLabelInfo.setText(text);
	}
	
	/**
	 * Create the frame.
	 */
	public Bitimator() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 606, 525);

		addMenuItems();
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		mBitPanel = new BitPanel(this);
		contentPane.add(mBitPanel, BorderLayout.CENTER);
		
		addButtons();
	}

}
