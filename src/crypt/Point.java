package crypt;

import java.math.BigInteger;

public class Point {
	public BigInteger x;
	public BigInteger y;
	
	public Point(){
		this.x = BigInteger.ZERO;
		this.y = BigInteger.ZERO;
	}
	
	public Point(BigInteger x, BigInteger y){
		this.x = x;
		this.y = y;
	}
	
	public String toString(){
		return "(" + this.x.toString() + "," + this.y.toString() + ")"; 
	}

}
