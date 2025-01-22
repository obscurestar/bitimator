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

import obscurestar.bitimator.BitPanel.Tool;

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
	private Iconic mIcons = new Iconic();
	private boolean mPlaying = false;
	private JPanel panel_1;
	private JPanel mToolPanel;

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
	
	public JButton addIconButton( JPanel panel, String name )
	{
		/*Creates a button with an icon.*/	
		Dimension button_dims = new Dimension(30,30);
		
		JButton button = new JButton();
		button.setIcon( mIcons.getIcon( name ) );
		button.setPreferredSize(button_dims);
		panel.add(button);
		
		return button;
	}
	
	public void addButtons()
	{
		JPanel panel_buttons = new JPanel();
		contentPane.add(panel_buttons, BorderLayout.SOUTH);
		panel_buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Dimension button_dims = new Dimension(30,30);
		mBtnBegin = addIconButton( panel_buttons, "begin" );
		mBtnBegin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.begin();
			}
		});
		
		mBtnBack = addIconButton( panel_buttons, "back" );
		mBtnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.back();
			}
		});
		
		mBtnPlay = addIconButton( panel_buttons, "play" );
		mBtnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				mPlaying = !mPlaying;
				
				if(mPlaying)
				{
					mBtnPlay.setIcon( mIcons.getIcon("play") );
				}
				else
				{
					mBtnPlay.setIcon( mIcons.getIcon("pause") );
				}
				mBitPanel.play();
			}
		});
		
		mBtnFwd = addIconButton( panel_buttons, "fwd" );
		mBtnFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.fwd();
			}
		});
		
		mBtnEnd = addIconButton( panel_buttons, "end" );
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
		mBtnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.delete();
			}
		});
		panel_buttons.add(mBtnDelete);
	}
	
	public JButton addDrawTool( JPanel panel, String name, BitPanel.Tool tool )
	{
		JButton button = addIconButton( panel, name );
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mBitPanel.setTool( tool );
			}
		});
		return button;
	}
	
	public void addTools( JPanel panel )
	{
		mToolPanel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JButton btnPencil = addDrawTool( panel, "pencil", Tool.PENCIL );	
		JButton btnEraser = addDrawTool( panel, "eraser", Tool.ERASER );
		JButton btnNot = addDrawTool( panel, "not", Tool.NOT );
		JButton btnLine = addDrawTool( panel, "line", Tool.LINE );
		JButton btnSquare = addDrawTool( panel, "square", Tool.RECT );
		JButton btnCircle = addDrawTool( panel, "circle", Tool.CIRCLE );
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
		setBounds(100, 100, 634, 615);

		addMenuItems();
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		//JPanel mBitPanel = new JPanel();
		mBitPanel = new BitPanel(this);
		contentPane.add(mBitPanel, BorderLayout.CENTER);
		
		mToolPanel = new JPanel();
		contentPane.add(mToolPanel, BorderLayout.WEST);
		
		addTools( mToolPanel );
		addButtons();
	}

}
