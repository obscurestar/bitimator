package obscurestar.bitimator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class Iconic
{
	private HashMap<String,ImageIcon> mIcons = new HashMap<String,ImageIcon>();
	
	public ImageIcon getIcon(String name)
	{
		return mIcons.get(name);
	}
	
    public Iconic()
    {
    	String path = "icons/";
        File directory = new File( path );

        FilenameFilter fileFilter = new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".png");
            }
        };

        // Get the matching files
        File[] files = directory.listFiles(fileFilter);

        // Process the matching files
        if (files != null) {
            for (File file : files)
            {
            	String name =file.getName();
            	name = name.substring(0, name.length() - 4);
            	ImageIcon icon = new ImageIcon( file.getAbsolutePath() );
            	mIcons.put( name, icon );
            	//System.out.println(name);
                //System.out.println(file.getAbsolutePath());
            }
        }
    }
}