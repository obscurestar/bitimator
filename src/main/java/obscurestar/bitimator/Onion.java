package obscurestar.bitimator;

import java.awt.Color;

public class Onion {
    private int mLayers = 2;
    private boolean mVisible = true;
    private int mOpacity = 30;
    private int[][] mColor= { { 255,   0, 0 },
    							{ 0, 255, 0 } };
    
    public void toggle()
    {
    	mVisible = !mVisible;
    }
    
    public int getNumLayers()
    {
    	return mLayers;
    }
    
    public boolean getVisible()
    {
    	return mVisible;
    }
    
    public void setVisible(boolean visible)
    {
    	mVisible = visible;
    }
    
    public Color getColor(int direction, int layer)
    {
    	int[] c = mColor[direction];
    	return new Color( c[0], c[1], c[2], getOpacity(layer) );
    }
    
    public Color getPrevColor( int layer )
    {
    	return getColor(0, layer);
    }
    
    public Color getNextColor( int layer )
    {
    	return getColor(1, layer);
    }
    
    public int getOpacity(int layer)
    {
    	return (100 - ( mOpacity * (mLayers - layer) ) ) * 255 / 100 ;
    }
}
