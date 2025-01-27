package obscurestar.bitimator;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

public class Selection {
	private boolean mSelecting = false;
	private boolean mCutting = false;
	private boolean mHasSelection = false;
	
	private Rectangle mRect = new Rectangle();
	private Point mSelectionStart = new Point();
	private Point mCurrentPosition = new Point();

	protected Point mOriginalPosition = new Point();
	protected HashMap<Point, Boolean> mSelection = new HashMap<Point, Boolean>();
	protected Frame mOriginalFrame;
	
	public Selection(){}
	
	public Selection( Selection selection, Frame frame )
	{
		//Copy CTOR
		mRect = new Rectangle ( selection.getRect() );
		mOriginalPosition = new Point( selection.mOriginalPosition );
		mCurrentPosition = new Point( mOriginalPosition );
		mHasSelection = true;
		for ( HashMap.Entry<Point, Boolean> entry:selection.mSelection.entrySet() )
		{
			mSelection.put( new Point( entry.getKey() ), entry.getValue() );
		}
		mOriginalFrame = new Frame ( frame );
	}
	
	public Rectangle getRect()
	{
		return mRect;
	}
	
	private Frame restoreReplaced( Frame frame )
	{
		if ( mOriginalFrame != null )
		{
			return new Frame( mOriginalFrame );
		}
		return frame;
	}
	
	public Frame overlaySelection( Frame frame )
	{
		//Splat the selection over the frame.
		for ( Point p : mSelection.keySet() )
		{
			Point pos = new Point( p );
			pos.x += mCurrentPosition.x;
			pos.y += mCurrentPosition.y;
            //Only stored positives.
            frame.set( pos, true );
		}
		return frame;
	}
	
	public boolean selecting()
	{
		return mSelecting;
	}
	
	public boolean hasSelection()
	{
		return mHasSelection;
	}
	
	public void getSelection( Frame frame )
	{
		for ( int x=0; x<mRect.width; ++x )
		{
			for (int y=0; y<mRect.height; ++y )
			{
				Point s = new Point( x + mRect.x, y + mRect.y );
				if ( frame.get(s) )
				{
					//Only store the set bits.
					Point pos = new Point( x, y );
					mSelection.put(pos,true);
				}
			}
		}
	}
	
	public void begin( Point point )
	{  //Start marking selection area.
		mSelection.clear();
		mOriginalFrame = null;
		mCutting = false;
		mSelecting = true;
		mHasSelection = false;
		mSelectionStart = point;
	}
	
	public Frame end ( Point point, Frame frame )
	{
		mSelecting = false;
		mRect.x = Math.min( mSelectionStart.x, point.x );
		mRect.y = Math.min( mSelectionStart.y, point.y );
		mRect.width = Math.abs( mSelectionStart.x - point.x );
		mRect.height = Math.abs( mSelectionStart.y - point.y );
		
		mOriginalPosition.x = mRect.x;
		mOriginalPosition.y = mRect.y;
		
		mCurrentPosition = mOriginalPosition;
		
		getSelection( frame );

		mOriginalFrame = new Frame ( frame );
		mHasSelection = true;
		return refresh ( cut() );
	}
	
	public Frame cut( )
	{
		//Call this after end() or you'll be sad.
		mCutting = true;
		for ( Point p : mSelection.keySet() )
		{
			Point pos = new Point(p);
            pos.x += mCurrentPosition.x;
            pos.y += mCurrentPosition.y;
            
            //Wipe them out, all of them.
            mOriginalFrame.set( pos, false );
		}
		return new Frame ( mOriginalFrame );
	}
	
	public Frame restore( Frame frame )
	{
		frame = restoreReplaced( frame );
		for (Point p : mSelection.keySet())
		{
			//Only stored positive values so can just do keyset.
			Point pos = new Point( p );
            pos.x += mOriginalPosition.x;
            pos.y += mOriginalPosition.y;
            frame.set(pos, true);
		}
		return frame;
	}
	
	public Frame cancel( Frame frame )
	{
		frame = restoreReplaced( frame );
		if (mCutting)
		{
			frame = restore ( frame );
		}
		mHasSelection = false;
		return frame;
	}
	
	public void complete()
	{
		mHasSelection = false;
	}
	
	public Frame refresh( Frame frame )
	{
		frame = restoreReplaced( frame );		
		frame = overlaySelection( frame );
		
		return frame;	
	}
	
	public Frame move( Point point, Frame frame )
	{	
		mCurrentPosition = point;

		return refresh( frame );
	}
}
