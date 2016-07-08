/** 
 * @author  LiuCong  
 * @file    test.java
 * @date    Date：2015年12月20日 下午2:55:03 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.exception;

public class NoteException extends BstarException{
	public NoteException(){
		super();
	}
	public NoteException(String fileName, int row){
		super(fileName+": NoteException:exception occured in line"+row);
	}
	public String getMessage(){
		return super.getMessage();
	}
	
}
