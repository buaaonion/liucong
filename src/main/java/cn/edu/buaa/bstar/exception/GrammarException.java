/** 
 * @author  LiuCong  
 * @file    test.java
 * @date    Date：2015年12月20日 下午2:55:03 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.exception;

public class GrammarException extends BstarException{
	public GrammarException(){
		super();
	}
	public GrammarException(String fileName, String msg){
		super(fileName + ": GrammarException:exception occured in line"+msg);
	}
	public String getMessage(){
		return super.getMessage();
	}
}
