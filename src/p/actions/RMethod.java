package p.actions;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class RMethod {
	public static ArrayList<RMethod> allMethods = new ArrayList<RMethod>();
	private String packageName;
	private String className;
	private String name;
	private String[] parameterTypes;
	private boolean bStatic = false;
	private boolean bNative = false;
	private boolean bConstructor = false;
	
	public RMethod(String name, String[] parameterTypes) {
		
		this.name = name;
		this.parameterTypes = parameterTypes;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getName() {
		return this.name;
	}
	
	
	public int namecounter(MethodDeclaration node) {
		int counted = 0;
		//	System.out.println(node);
		//	System.out.println();
			System.out.println("node.toString():" + node.toString());
		//	System.out.println(name);
		if (node.toString().contains(name) ) {
			//System.out.println(parameterTypes[0]);
			if (node.toString().contains("String") ) {
				//System.out.println(parameterTypes[1]);
				if (node.toString().contains(parameterTypes[1]) ) {
					//System.out.println(parameterTypes[2]);
					if (node.toString().contains(parameterTypes[2]) ) {
				
			counted++;
			System.out.println("counted #1: " + counted);
					}
				}
		}
		}
		System.out.println("counted #2: " + counted);
		return counted;
	}
	
	public String[] getParameterTypes() {
		return parameterTypes;
	}
	
	public void setStatic(boolean bStatic) {
		this.bStatic = bStatic;
	}
	
	public boolean isStatic() {
		return this.bStatic;
	}
	
	public void setNative(boolean bNative) {
		this.bNative = bNative;
	}
	
	public boolean isNative() { 
		return this.bNative;
	}
	
	public void setConstructor(boolean bConstructor) {
		this.bConstructor = bConstructor; 
	}
	
	public boolean isConstructor() {
		return this.bConstructor;
	}
	
	public boolean rename(String newName) {
		//precondition checks
		for(RMethod mth : RMethod.allMethods) {
			if(mth.getName().compareTo(newName) == 0) {
				if(this.parameterTypes.length == mth.parameterTypes.length) {
					System.out.println("Found a method with the same name and parameter types but the method may or may not be duplicate.  The method is:");
					System.out.println(mth.name + "(");			
					for(int i = 0; i < mth.parameterTypes.length; i++) {
						if(this.parameterTypes[i].compareTo(mth.parameterTypes[i]) == 0) { 
							System.out.println(mth.parameterTypes[i] + " ");
						}
					} 
					System.out.println(") in " + this.className + " of " + this.packageName);					
				}
			}
		}
		
		//code changes
		
		return true;
	}
}
