package p.actions;

import java.util.LinkedHashMap;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

public class ASTVisitorEx extends ASTVisitor {
	
	public String source = "";
	
	public ASTVisitorEx(String source) {
		this.source = source;
	}
	
	/*public boolean visit(MethodDeclaration node) {
		String newSource = ""; //newSource is initialized as a blank string
		newSource += source.substring(0, node.getName().getStartPosition()); //current data from the java type is recorded
		newSource += "xyz"; //use xyz as the change to the name
		newSource += source.substring(node.getName().getStartPosition()+node.getName().getLength());
		source = newSource;
		return true;
	}*/
	
	 
// Testing grounds 
	public boolean visit(MethodDeclaration node) {
		//Prints method name + contents
		System.out.println(node + "*");
		System.out.println();
			return true;
		}
	
	public boolean visit(BlockComment node) {
		//Research suggests it should print text enclosed in a block comment (*/ - /*). Cannot seem to make it work, however...
		System.out.println(node);
		System.out.println();
			return true;
		}
	
	
	/*public boolean visit(Block node) {
		//Prints contents of block, including the enveloping {}s 
		System.out.println(node + "!");
		System.out.println();
			return true;
		}
	
	public boolean visit(MethodInvocation node) {
		//Prints cases where a method is called (ex. a.m(1);)
		System.out.println(node);
		System.out.println();
			return true;
		}

	public boolean visit(Assignment node) {
		//Prints cases where a variable receives a new value (ex. v += 3)
		System.out.println(node);
		System.out.println();
			return true;
		}
		
	public boolean visit(FieldDeclaration node) {
		//Prints field variables
		System.out.println(node + "?");
		System.out.println();
		//Prints root
		System.out.println(node.getRoot());
			return true;
		}*/
}
