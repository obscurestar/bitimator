package obscurestar.bitimator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BitPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	    
    //Member variables
	private Point mDim= new Point(24,24);
    private Rectangle mDrawArea;
    private int mPixelSize;
    private boolean mPressed;
    private Onion mOnion = new Onion();
    private Bitimator mParent;
    private boolean mPlaying = false;
    private int mFrameMS=100; //10FPS
    private ActionListener mFrameTimer;
    private boolean mOnionSkinning = false; //For play mode
    private Timer mTimer;

    private Set<Point> mStroke = new HashSet<Point>();
    private ArrayList<Frame> mFrames = new ArrayList<Frame>();
    private int mCurrentFrame;
    
    public BitPanel(Bitimator parent) {    			
//    	ChoordData.getInstance();

    	mParent = parent;
    	mDrawArea = getBounds();
    	mPixelSize = 0;
    	mPressed = false;
    	mFrames.add( new Frame (mDim) );
    	mCurrentFrame = 0;
    	
    	flushStroke();
        addMouseListener(this);
        addMouseMotionListener(this);
        
        mFrameTimer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playBack();
            }
        };
    }
    
    public void playBack()
    {
    	mCurrentFrame++;
    	if (mCurrentFrame >= mFrames.size())
    	{
    		mCurrentFrame = 0;
    	}
    	repaint();
    	mTimer.restart();
    }
    
    public void setFPS(int fps)
    {
    	mFrameMS = 1000/fps;
    }
    
    public void begin()
    {
    	mCurrentFrame = 0;
    	repaint();
    }
    
    public void back()
    {
    	if (mCurrentFrame > 0)
    	{
    		mCurrentFrame--;
        	repaint();
    	}
    }
    
    public void fwd()
    {
    	if (mCurrentFrame < mFrames.size()-1)
    	{
    		mCurrentFrame++;
    		repaint();
    	}
    }
    
    public void end()
    {
    	mCurrentFrame  = mFrames.size() - 1;
    	repaint();
    }
    
    public void add()
    {
    	mCurrentFrame++;
    	mFrames.add( mCurrentFrame, new Frame(mDim) );
    	repaint();
    }
    
    public void delete()
    {
    	mFrames.remove(mCurrentFrame);
    	
    	mCurrentFrame =  Math.max( Math.min( mCurrentFrame,  mFrames.size()-1 ), 0);
    	if (mFrames.size() == 0)
    	{
    		mFrames.add( new Frame(mDim) );
    	}

    		
    	repaint();
    }
    
    public void play()
    {
    	mPlaying = !mPlaying;
    	
    	if (mPlaying)
    	{
    		mOnionSkinning = mOnion.getVisible();
    		mOnion.setVisible(false);
    		mTimer = new Timer(mFrameMS, mFrameTimer);
    		mTimer.start();
    	}
    	else
    	{
    		mTimer.stop();
    		mOnion.setVisible(mOnionSkinning);
    		repaint();
    	}
    }
    
    public void setOnion(boolean onion)
    {
    	mOnion.setVisible(onion);
    	repaint();
    }
    
    public void  flushStroke()
    {
    	mStroke.clear();
    }
    
    private Frame getFrame()
    {
    	//Get the current frame.
    	return mFrames.get(mCurrentFrame);
    }
    
    private Frame getFrame(int frame_no)
    {
    	//Get frame # foo.
    	return mFrames.get(frame_no);
    }
    
    private void defineDrawArea()
    {
    	mDrawArea = getBounds();
    	int width = mDrawArea.width/mDim.x;
    	int height = mDrawArea.height/mDim.y;
    	
    	mPixelSize = Math.min(width, height);
    	
    	width = mDim.x * mPixelSize;
    	height = mDim.y * mPixelSize;
    	mDrawArea.x = (mDrawArea.width - width) / 2;
    	mDrawArea.y = (mDrawArea.height - height) / 2;
    	
    	mDrawArea.width = width;
    	mDrawArea.height = height;
    }
    
    private void handleMouseClick(int x, int y, boolean pressed)
    {
    	boolean repaint = false;
    	defineDrawArea();
    	
    	if (x > mDrawArea.x && x < mDrawArea.x + mDrawArea.width
    		&& y > mDrawArea.y && y < mDrawArea.y + mDrawArea.height )
    	{
    		//If mouse in drawing area.
    		
    		Point p = new Point( ( x - mDrawArea.x ) / mPixelSize, 
    							 ( y - mDrawArea.y ) / mPixelSize );
    		
    		if ( !mPressed
    			|| ! mStroke.contains(p) )
    		{
    			//New click or we've moved to a new cell.
    			getFrame().toggle(p);
    			mStroke.add( p );
    			repaint = true;
    		}
    	}
    	
    	mPressed = pressed;

    	if ( !mPressed )
    	{
    		//End the stroke.
    		flushStroke();
    	}
 
    	
    	if (repaint)
    	{
    		repaint();  //If changes.
    	}
    }
    
    private void drawGrid(Graphics g)
    {
        g.setColor(Color.BLUE);
        
        for(int x=0;x<mDim.x + 1;++x)
        {
        	int x_pos = x * mPixelSize + mDrawArea.x;
        	g.drawLine( x_pos, mDrawArea.y, x_pos, mDrawArea.y + mDrawArea.height );
        }
        
        for(int y=0;y<mDim.y + 1;++y)
        {
        	int y_pos = y * mPixelSize + mDrawArea.y;
        	g.drawLine( mDrawArea.x, y_pos, mDrawArea.x + mDrawArea.width, y_pos );
        }
    }
    
    private void drawPixels(Graphics g, int frame, Color color)
    {
    	g.setColor(color);
        for(int x=0;x<mDim.x;++x)
        {
        	int x_pos = x * mPixelSize + mDrawArea.x;
        	
            for(int y=0;y<mDim.y;++y)
            {
            	int y_pos = y * mPixelSize + mDrawArea.y;
            	
            	if (getFrame(frame).get(x,y))
            	{
            		g.fillRect(x_pos, y_pos, mPixelSize, mPixelSize);
            	}
            }
        }
    }
    
    public void drawOnion(Graphics g)
    {
    	if ( !mOnion.getVisible() )
    	{
    		return;
    	}
    	
    	int layers = mOnion.getNumLayers();
    	
    	for ( int i=0;i<layers;++i )
    	{
        	int dist = layers - i;

    		int frame_no = mCurrentFrame - dist;
    		if ( frame_no >= 0)
    		{
    			//Draw Previous frame
    			drawPixels( g, frame_no, mOnion.getPrevColor(i) );
    		}
    		
    		frame_no = mCurrentFrame + dist;
    		if ( frame_no < mFrames.size() )
    		{
    			//Draw Previous frame
    			drawPixels( g, frame_no, mOnion.getNextColor(i) );
    		}
    	}
    }
    
    private void setFrameInfo()
    {
    	String info = "(" + ( mCurrentFrame + 1 ) + " of " + mFrames.size() + ")";
    	mParent.setFrameInfo( info );
    }
    
    @Override
    public void paintComponent(Graphics g)
    {	
    	setFrameInfo();
        super.paintComponent(g);
        defineDrawArea();
        drawOnion(g);
        drawPixels(g, mCurrentFrame, Color.BLACK);
        drawGrid(g);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        handleMouseClick( e.getX(), e.getY(), true ); 
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        handleMouseClick( e.getX(), e.getY(), false ); 
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseClick( e.getX(), e.getY(), true ); 
    }

    // Unused MouseListener/MouseMotionListener methods
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}
