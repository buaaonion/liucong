/** 
 * @author  LiuCong  
 * @file    test.java
 * @date    Date：2015年12月20日 下午2:55:03 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.exception;

public class BstarException extends Exception {
	public BstarException(){
		super();
	}
	public BstarException(String msg){
		super(msg);
	}
	public String getMessage(){
		return super.getMessage();
	}
}
