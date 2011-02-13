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
	 */
	public static File createNonExistingFile(String dir,String simpleName){		
		File file=new File(dir+"\\"+simpleName);
		int value=1;
		if(file.exists()){			
			String name=value+"_"+simpleName;
			file=createNonExistingFile(dir,name);
		}
		return file;
	}
}
