package obscurestar.bitimator;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/*  FILE FORMAT
 * This is extremely basic:
 * short x; //Width in pixels
 * short y; //height in pixels
 * short frames; //Number of frames
 * For frames: FrameData:  A y=length array of ceil(x/8)
 */
public class FileIO {
	private String mFilename;
	private String mFullpath;
	private int mFrameMS=100;   //This is dumb but I don't want to make a class to return a tuple.
	private static final String mExtension = ".bpa"; //Bit pixel animation
	
	public void setFilename( String name )
	{
		mFilename = name;
	}
	
	public int getFrameMS()
	{
		return mFrameMS;
	}
	
	public boolean canSave()
	{
		if (mFullpath != null)
		{
			return true;
		}
		return false;
	}
	
	public boolean pickerOpen()
	{ 
		JFileChooser fileChooser = new JFileChooser();
		
		FileFilter filter = new FileFilter()
		{
	        @Override
	        public boolean accept(File file) {
	            // Check if the file is a directory or has the desired extension
	            return file.isDirectory() || 
	                   file.getName().toLowerCase().endsWith(mExtension);
	        }

            @Override
            public String getDescription() {
                return "BitPixel Animation files (*." + mExtension +")";
            }
        };

        // Set the file filter
        fileChooser.setFileFilter(filter);

		int returnValue = fileChooser.showOpenDialog(null);

	    if (returnValue == JFileChooser.APPROVE_OPTION)
	    {
	        File selectedFile = fileChooser.getSelectedFile();
	        mFullpath = selectedFile.getAbsolutePath();
	        mFilename = selectedFile.getName();
	        int ext_pos = mFilename.lastIndexOf(".");
            if (ext_pos > 1) {
            	mFilename = mFilename.substring(0, ext_pos);
            }
            
	        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
	        return true;
	    }
	    return false;
    }
	
	public static boolean confirm(String title, String message)
	{
        int result = JOptionPane.showConfirmDialog(null, 
                                                  message, 
                                                  title, 
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE );
        if (result == JOptionPane.OK_OPTION)
        {
           return true;
        }

        return false;
	}
	
	public boolean pickerSaveAs()
	{
		JFileChooser fileChooser = new JFileChooser();
		
        fileChooser.setDialogTitle("Save File As");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            mFilename = file.getName();
            mFullpath = file.getAbsolutePath();
            
            int ext_pos = mFilename.lastIndexOf(".");
            if (ext_pos > 1) { // Ensure there's a dot and it's not the first character
            	//Just chop off the extension and append the right one.
            	mFilename = mFilename.substring(0, ext_pos);
            	mFullpath = mFullpath.substring(0, mFullpath.lastIndexOf("."));
            }
            mFullpath += mExtension;
            file = new File(mFullpath);
            if( file.exists() )
            {
            	if ( file.isDirectory() )
            	{
            		JOptionPane.showMessageDialog(null, mFullpath + " is a directory.", "Not Saved!", JOptionPane.INFORMATION_MESSAGE);
            		return false;
            	}
            	if ( !confirm("Save As", "Confirm overwrite of " + mFullpath) )
            	{
            		return false;
            	}
            }
            return true;
        }
        return false;
	}
	
    public static short reverseShort(short value)
    {
    	//Fucking java stupid fucking little endian with no fucking builtin function for shorts.
        int temp = value & 0xFFFF; // Treat the short as an unsigned 16-bit int
        return (short) ((temp >>> 8) | (temp << 8));
    }
    
    public static byte[] reverseBytes(byte[] byteArray)
    {
    	//Java you absolute fucking knob.
        int length = byteArray.length;
        byte[] reversedArray = new byte[length];
        for (int i = 0; i < length; i++) {
            reversedArray[i] = byteArray[length - 1 - i];
        }
        return reversedArray;
    }
    
	private byte[] shortToBytes( short num )
	{
		ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putShort( reverseShort(num) );
        return buffer.array();
	}
	
	public ArrayList<Frame> open()
	{
		ArrayList<Frame> frames = new ArrayList<Frame>();
		
		if( mFullpath == null )
		{
			return null;
		}
		
        try (DataInputStream instream = new DataInputStream(new FileInputStream(mFullpath))) {

            short width = reverseShort( instream.readShort() );
            short height = reverseShort( instream.readShort() );
            mFrameMS =  reverseShort( instream.readShort() );
            short num_frames = reverseShort( instream.readShort() );
            
            int x_bytes = (int)Math.ceil( (float)width / 8.0 );
            
            int bytes_in_frame = x_bytes * height;
            
            for (int i=0;i<num_frames;++i)
            {
                byte[] data = instream.readNBytes( bytes_in_frame );
                data = reverseBytes(data);
                if( data.length != bytes_in_frame )
                {
                	instream.close();
                	throw new IndexOutOfBoundsException();                	
                }
                frames.add( new Frame( width, height, data ) );
            }

            instream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		return frames;
	}
	
	public void save( ArrayList<Frame> frames, int frame_ms )
	{
		if (mFullpath == null || frames.size() == 0)
		{
			//Nothing to save
			return;
		}
		Point dim = Frame.getDimensions();
		try (FileOutputStream outstream = new FileOutputStream(mFullpath)) {
            byte[] data = shortToBytes( (short) dim.x );
            outstream.write(data);
            data = shortToBytes( (short) dim.y );
            outstream.write(data);
            data = shortToBytes( (short) frame_ms );
            outstream.write(data);
            data = shortToBytes( (short) frames.size() );
            outstream.write(data);
            for( Frame frame:frames )
            {
            	data = frame.pack();
            	outstream.write(reverseBytes(data));
            }
            outstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
