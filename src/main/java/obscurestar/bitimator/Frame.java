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
	
	public Frame()
	{
		this( Frame.mDim );
	}
	
	public Frame( int x, int y, byte[] bytes )
	{
		Frame.mDim.x = x;
		Frame.mDim.y = y;
		
		this.unpack( bytes );
	}
	
	public Frame( Frame frame )
	{
		//Copy CTOR
		this( Frame.mDim );
		for (int x=0; x<Frame.mDim.x; ++x )
		{
			for (int y=0; y<Frame.mDim.y; ++y )
			{
				mDrawing[x][y] = frame.get( x, y );
			}
		}
	}
	
	public static final Point getDimensions()
	{
		return mDim;
	}
	
	public byte[] pack()
	{
		//Convert the frame to an array of bytes.
		int x_bytes = (int)Math.ceil( (float)mDim.x / 8.0 );
		byte[] result = new byte[ x_bytes * mDim.y ];
		
		for (int y=0;y<mDim.y;++y)
		{
			int byteno = y * x_bytes;
			int bitcount = 0;

			for (int x=0;x<mDim.x;++x)
			{
				if (mDrawing[x][y])
				{
					result[byteno] += 1 << bitcount;
				}
				
				bitcount++;
				
				if (bitcount == 8)
				{
					byteno++;
					bitcount = 0;
				}
			}
		}	
		return result;
	}
	
	public void unpack(byte[] bytes)
	{
		int x_bytes = (int)Math.ceil( (float)mDim.x / 8.0 );
		if (	bytes.length != x_bytes * mDim.y )
		{
			System.out.println("Byte size mismatch.");
			throw new IndexOutOfBoundsException();
		}
		
		mDrawing = new boolean[Frame.mDim.x][Frame.mDim.y];
		for (int y=0;y<mDim.y;++y)
		{
			int byteno = y * x_bytes;
			int bitcount = 0;
			for (int x=0;x<mDim.x;++x)
			{
				mDrawing[x][y] = false; //Zero out anything already there.
				int bit = ( bytes[byteno] >> bitcount );
				if ( (bit & 1) == 1 )
				{
					mDrawing[x][y] = true;
				}
				bitcount++;
				
				if (bitcount == 8)
				{
					byteno++;
					bitcount = 0;
				}
			}
		}
	}
	
	public boolean validIndex(int x, int y)
	{
		if ( x < 0 || y < 0
				|| x >= mDim.x || y >=mDim.y )
		{
			return false;
		}
		return true;
	}
	
	public boolean validIndex(Point p)
	{
		return validIndex(p.x, p.y);
	}
	
	public boolean toggle(Point p)
	{
		if ( !validIndex(p) )
		{
			return false;
		}
		//Returns value before operation
		boolean previous_value = mDrawing[p.x][p.y];
		mDrawing[p.x][p.y] = !mDrawing[p.x][p.y];
		return previous_value;
	}
	
	public boolean set( int x, int y, boolean mode )
	{
		if ( !validIndex( x, y) )
		{
			return false;
		}
		
		boolean previous_value = (boolean) mDrawing[x][y];
		mDrawing[x][y] =  mode;
		return previous_value;	
	}
	
	public boolean set(Point p, boolean mode)
	{
		return set( p.x, p.y, mode );
	}
	
	public boolean get(int x,int y)
	{
		if ( !validIndex(x, y) )
		{
			return false;
		}
		return mDrawing[x][y];
	}
	
	public boolean get(Point p)
	{
		return get(p.x, p.y);
	}
}
