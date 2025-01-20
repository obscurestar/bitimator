package obscurestar.bitimator;

import java.awt.Point;

public class Frame {
	static private Point mDim;
	private boolean[][] mDrawing;
	
	public Frame(Point dimensions)
	{
		if ( Frame.mDim == null )
		{
			Frame.mDim = dimensions;
		}
		
		if ( dimensions.x != Frame.mDim.x 
				|| dimensions.y != Frame.mDim.y )
		{
			throw new IndexOutOfBoundsException();
		}
		else
		{
			mDrawing = new boolean[Frame.mDim.x][Frame.mDim.y];
		}
	}
	
	public boolean toggle(Point p)
	{
		mDrawing[p.x][p.y] = !mDrawing[p.x][p.y];
		return mDrawing[p.x][p.y];
	}
	
	public boolean get(int x,int y)
	{
		return mDrawing[x][y];
	}
	
	public boolean get(Point p)
	{
		return get(p.x, p.y);
	}
}
