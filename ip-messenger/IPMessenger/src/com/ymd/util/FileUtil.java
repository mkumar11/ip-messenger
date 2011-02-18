/**
 * 
 */
package com.ymd.util;

import java.io.File;

/**
 * Simple file utilities.
 * 
 * @author yaragalla Muralidhar
 *
 */
public class FileUtil {

	/**
	 * It generates a file which is not existing.
	 * 
	 * @param simpleName
	 * @return File.
	 * modified today
	 */
	public static File createNonExistingFile(String simpleName){            
        File file=new File(simpleName);
        int value=1;
        if(file.exists()){                      
                String name=value+"_"+simpleName;
                file=createNonExistingFile(name);
        }
        return file;
	}
	
	/**
	 * It generates a file which is not existing.
	 * 
	 * @param simpleName
	 * @param dir
	 * @return File.
	 * modified today
	 */
	public static File createNonExistingFile(String simpleName, String dir){            
        File file=new File(dir+"\\"+simpleName);
        int value=1;
        if(file.exists()){                      
                String name=value+"_"+simpleName;
                file=createNonExistingFile(name,dir);
        }
        return file;
	}
}
