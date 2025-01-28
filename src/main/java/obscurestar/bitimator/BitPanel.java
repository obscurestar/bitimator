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
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BitPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	    
	public enum Tool
	{
		PENCIL,
		ERASER,
		NOT,
		LINE,
		RECT,
		CIRCLE,
		MOVE,
		SELECT,
		COPY,
		CUT,
		PASTE
	}
	
    //Member variables
	private Point mDim= new Point(20,16);
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
    private Point mClickPoint = new Point(-1,-1); //Where click originated.
    private Point mLastPoint = new Point(-1,-1);
    private HashMap<Point, Boolean> mStroke = new HashMap<Point, Boolean>();
    private ArrayList<Frame> mFrames = new ArrayList<Frame>();
    private BitPanel.Tool mTool = Tool.PENCIL;
    private Selection mSelection = new Selection();
    private Rectangle mSelectionRect = new Rectangle();
    private Selection mClipboard = new Selection();
    
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
    
    public ArrayList<Frame> getFrames()
    {
    	return mFrames;
    }
    
    public void deleteFrames()
    {
    	//NOTE:  This should be called before opening a file.
    	mFrames.clear();
    }
    
    public void newAnimation()
    {
    	mSelection.complete();
    	mPlaying = false;
    	mFrames.clear();
    	mCurrentFrame = 0;
    	mClickPoint = new Point(-1,-1);
    	mLastPoint = new Point(-1,-1);
    	mFrames.add( new Frame() );
    	repaint();
    }
    
    public void setFrames( ArrayList<Frame> frames )
    {
    	if (frames == null || frames.size() == 0)
    	{
    		return;
    	}
    	
    	mDim = frames.get(0).getDimensions();
    	mSelection.complete();
    	mFrames = frames;
    	mPlaying = false;
    	mCurrentFrame = 0;
    	mClickPoint = new Point(-1,-1);
    	mLastPoint = new Point(-1,-1);
    	repaint();
    }
    
    public void setFrameMS( int frame_ms )
    {
    	mFrameMS = frame_ms;
    }
    
    public int getFrameMS()
    {
    	return mFrameMS; //miliseconds per frame.
    }
    
    public void setTool( Tool tool )
    {
    	mTool = tool;
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
    	mSelection.complete();
    	mCurrentFrame = 0;
    	repaint();
    }
    
    public void back()
    {
    	mSelection.complete();
    	if (mCurrentFrame > 0)
    	{
    		mCurrentFrame--;
        	repaint();
    	}
    }
    
    public void fwd()
    {
    	mSelection.complete();
    	if (mCurrentFrame < mFrames.size()-1)
    	{
    		mCurrentFrame++;
    		repaint();
    	}
    }
    
    public void end()
    {
    	mSelection.complete();
    	mCurrentFrame  = mFrames.size() - 1;
    	repaint();
    }
    
    public void add()
    {
    	mSelection.complete();
    	mCurrentFrame++;
    	mFrames.add( mCurrentFrame, new Frame(mDim) );
    	repaint();
    }
    
    public void delete()
    {
    	mSelection.complete();

    	mFrames.remove(mCurrentFrame);
    	
    	mCurrentFrame =  Math.max( Math.min( mCurrentFrame,  mFrames.size()-1 ), 0);
    	if (mFrames.size() == 0)
    	{
    		mFrames.add( new Frame(mDim) );
    	}

    	undoStroke();
    	
    	repaint();
    }
    
    public void play()
    {
    	mPlaying = !mPlaying;
    	mSelection.complete();
    	
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
    	mClickPoint.x = -1;
    	mClickPoint.y = -1;
    	mLastPoint = mClickPoint;
    }
    
    public void undoStroke()
    {
    	for (HashMap.Entry<Point, Boolean> item : mStroke.entrySet()) {
    	    Point p = item.getKey();
    	    boolean b   = (boolean) item.getValue();
    	    getFrame().set(p, b);
    	}
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
    
    private Point[] getCorners( Point p )
    {
    	Point[] corner = { new Point(), new Point() };
   	 
    	corner[0].x = Math.min(p.x,  mClickPoint.x);
    	corner[0].y = Math.min(p.y,  mClickPoint.y);
    	
    	corner[1].x = Math.max(p.x,  mClickPoint.x);
    	corner[1].y = Math.max(p.y,  mClickPoint.y);
    	
    	return corner;
    }
    
    private void toolNot( Point p )
    {
    	boolean previous_value = getFrame().toggle(p);
    	
    	mStroke.putIfAbsent( p, previous_value );
    }
    
    private void toolDraw( Point p, boolean mode )
    {
    	getFrame().set(p, mode);
    }
    
    private void toolRect( Point p )
    {
    	undoStroke();  //Erase the last drawing.
    	
    	Point[] corner = getCorners(p);
    	
    	for (int c=0; c<2; ++c)
    	{
	    	for ( int i=corner[0].x; i<=corner[1].x; ++i )
	    	{
	    		Point new_point = new Point( i, corner[c].y );
	    		boolean previous_value = getFrame().set(new_point, true);
	    		mStroke.putIfAbsent( new_point,  previous_value );
	    	}
	    	for ( int i=corner[0].y; i<=corner[1].y; ++i )
	    	{
	    		Point new_point = new Point( corner[c].x, i );
	    		boolean previous_value = getFrame().set(new_point, true);
	    		mStroke.putIfAbsent( new_point,  previous_value );
	    	}
    	}
    }
    
    private void toolCircle( Point p )
    {
    	undoStroke();
       	Point[] corner = getCorners(p);
       	Point range = new Point ( corner[1].x - corner[0].x,
				  corner[1].y - corner[0].y );
       	Point middle = new Point (range.x/2, range.y/2);
       	Point draw_mid = new Point ( corner[0].x + middle.x, corner[0].y + middle.y );
       	for (int x=0;x<middle.x;++x )
       	{
       		int y = (int)Math.round( Math.sqrt( (double)(Math.pow(middle.x, 2) - Math.pow(x, 2) ) ) );
       		Point pixel = new Point ( draw_mid.x + x, draw_mid.y + y );
       		boolean previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value );
    		
    		pixel = new Point ( draw_mid.x + x, draw_mid.y - y );
       		previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value );    
    		
    		pixel = new Point ( draw_mid.x - x, draw_mid.y + y );
       		previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value ); 
    		
    		pixel = new Point ( draw_mid.x - x, draw_mid.y - y );
       		previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value ); 
       	}
       	
       	for (int y=0;y<middle.x;++y )
       	{
       		int x = (int)Math.round( Math.sqrt( (double)(Math.pow(middle.x, 2) - Math.pow(y, 2) ) ) );
       		Point pixel = new Point ( draw_mid.x + x, draw_mid.y + y );
       		boolean previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value );
    		
    		pixel = new Point ( draw_mid.x + x, draw_mid.y - y );
       		previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value );    
    		
    		pixel = new Point ( draw_mid.x - x, draw_mid.y + y );
       		previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value ); 
    		
    		pixel = new Point ( draw_mid.x - x, draw_mid.y - y );
       		previous_value = getFrame().set(pixel, true);
    		mStroke.putIfAbsent( pixel,  previous_value ); 
       	}
    }
    
    private void toolLine( Point p )
    {
    	undoStroke();
    	
    	//Probably not the most efficient
       	Point[] corner = getCorners(p);
       	       	
    	if ( corner[0].x == corner[1].x ) //vertical
    	{
    		for (int y=corner[0].y; y<=corner[1].y; ++y)
    		{
    			Point pixel = new Point ( corner[0].x, y );
    			boolean previous_value = getFrame().set(pixel, true);
        		mStroke.putIfAbsent( pixel,  previous_value );
    		}
    	}
    	else if ( corner[0].y == corner[1].y ) //horizontal
    	{
    		for (int x=corner[0].x; x<=corner[1].x; ++x)
    		{
    			Point pixel = new Point ( x , corner[0].y );
    			boolean previous_value = getFrame().set(pixel, true);
        		mStroke.putIfAbsent( pixel,  previous_value );
    		}
    	}
    	else
    	{
           	Point range = new Point ( corner[1].x - corner[0].x,
  				  corner[1].y - corner[0].y );
           	
    		double slope = (double)( p.y - mClickPoint.y ) / (double)( p.x - mClickPoint.x );
    		
           	Point direction = new Point(1,1);
    		if (mClickPoint.x > p.x)
    		{
    			direction.x = -1;
    		}
    		if (mClickPoint.y > p.y)
    		{
    			direction.y = -1;
    		}
    		
    		for( int i=0; i<range.x; ++i )
    		{
    			int x = direction.x * i;
    			int y = (int) Math.round((double)x * slope);
    			
    			Point pixel = new Point( mClickPoint.x + x, 
    									 mClickPoint.y + y );
    			boolean previous_value = getFrame().set(pixel, true);
        		mStroke.putIfAbsent( pixel,  previous_value );
    		}
    		
    		if (range.x != range.y)
    		{
    			//At 45 degrees we don't need to draw the other half of the line.
	    		slope = 1.0/slope;
	    		for( int i=0; i<range.y; ++i )
	    		{
	    			int y = direction.y * i;
	    			int x = (int) Math.round((double)y * slope);
	    			
	    			Point pixel = new Point( mClickPoint.x + x, 
	    									 mClickPoint.y + y );
	    			boolean previous_value = getFrame().set(pixel, true);
	        		mStroke.putIfAbsent( pixel,  previous_value );
	    		}
    		}
    	}
    }
    
    
    private void toolSelection( Point p, boolean pressed )
    {
    	mSelectionRect = new Rectangle (  Math.min( mClickPoint.x, p.x ),
    									  Math.min( mClickPoint.y, p.y ),
    									  Math.abs( mClickPoint.x - p.x ),
    									  Math.abs( mClickPoint.y - p.y ) );
    	
    	if ( !mPressed  && !mSelection.selecting() )
		{
			mSelection.begin( mClickPoint );
		}
		if ( !pressed )
		{
			mFrames.set( mCurrentFrame,  new Frame ( mSelection.end( p, getFrame() ) ) );
			//mFrames.set( mCurrentFrame,  new Frame ( mSelection.cut() ) );
			mTool = Tool.MOVE;
		}
    }
    
    private void toolMove( Point p )
    {
    	if ( mSelection.hasSelection() && ! mSelection.selecting() )
    	{
    		mFrames.set( mCurrentFrame, new Frame ( mSelection.move( p, getFrame() ) ) );
    	}
    }
    
    public void copy()
    {
    	if (mSelection.hasSelection())
    	{
	    	mSelection.complete( );
	    	mClipboard = new Selection( mSelection, getFrame() );
	    	mFrames.set( mCurrentFrame, new Frame( mSelection.restore( getFrame() ) ) );
	    	repaint();
    	}
    	else
    	{
    		System.out.println("Nothing selected to copy.");
    	}
    }
    
    public void cut()
    {
    	if (mSelection.hasSelection())
    	{
	    	System.out.println("Copied to clipboard.");
	    	mClipboard = new Selection( mSelection, getFrame() );
	    	mFrames.set( mCurrentFrame, new Frame( mSelection.cut( ) ) );
	    	mSelection.complete( );
	    	repaint();
    	}
    	else
    	{
    		System.out.println("Nothing selected to copy.");
    	}
    }
    
    public void paste()
    {
    	if (mClipboard.hasSelection())
    	{
    		System.out.println("Pasting from clipboard.");
    		mSelection.complete();
    		mSelection = new Selection( mClipboard, getFrame() );
    		mFrames.set( mCurrentFrame,  new Frame( mSelection.refresh( getFrame() ) ) );
    		repaint();
    	}
    	else
    	{
    		System.out.println("Nothing in clipboard.");
    	}
    }
    private void handleCancel()
    {
    	mFrames.set( mCurrentFrame,  new Frame ( mSelection.cancel( getFrame() ) ) );
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
    		
    		if ( !mPressed )
    		{
    			mClickPoint = p;
    			mLastPoint = p;
    		}
    		

			switch ( mTool )
			{
			case PENCIL:
				toolDraw( p, true );
				break;
			case ERASER:
				toolDraw( p, false );
				break;
			case NOT:
	    		if ( !mPressed
	        			|| ! mStroke.containsKey(p) )
	        		{
	        			//New click or we've moved to a new cell.
	    				toolNot( p );
	        		}
	    			break;
			case LINE:
				toolLine( p );
				break;
			case CIRCLE:
				toolCircle( p );
				break;
			case RECT:
				toolRect( p );
				break;
			case SELECT:
				toolSelection( p, pressed );
				break;
			case MOVE:
				if (pressed)
				{
					toolMove( p );
				}
				break;
			default:
				System.out.println("Unsupported tool.");
				return;
			}
			
			repaint = true;
			mLastPoint = p;
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
    
    private void drawOnion(Graphics g)
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
    
    private void drawSelection( Graphics g )
    {
    	if ( mSelection.selecting() )
    	{
    		//Draw rect around selection area.
    		g.setColor(Color.GREEN);
    		g.drawRect( mDrawArea.x + mSelectionRect.x * mPixelSize,
    					mDrawArea.y + mSelectionRect.y * mPixelSize,
    					mPixelSize * mSelectionRect.width, 
    					mPixelSize * mSelectionRect.height );
    	}
    	else if ( mSelection.hasSelection() )
    	{
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
        drawSelection(g);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        handleMouseClick( e.getX(), e.getY(), true ); 
        if (e.getButton() == MouseEvent.BUTTON3)
        {
        	handleCancel();
        }
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
