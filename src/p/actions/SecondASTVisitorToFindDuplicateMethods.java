package p.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import java.lang.reflect.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SecondASTVisitorToFindDuplicateMethods extends ASTVisitor {
	
	RMethod target= null;
	public static Set<Object> Overshot = FirstASTVisitorToFindDuplicateMethods.methodset;
	
	public SecondASTVisitorToFindDuplicateMethods(RMethod target) {
		this.target = target;
	}

	public boolean visit(TypeDeclaration node) {
	//	System.out.println("We are looking at " + node.getName());
	//	//System.out.println(FirstASTVisitorToFindDuplicateMethods.methodset);
		findDuplicate(node.resolveBinding());
		
		return true;
	}

	void findDuplicate(ITypeBinding node) {

		//traversing all parent classes
		////System.out.println("In second part now");
	//	System.out.println("Current Methodset contents: ");
	//	System.out.println(FirstASTVisitorToFindDuplicateMethods.methodset.toString());
		if (node != null && node.getQualifiedName() != "java.lang.Object") {
			findDuplicateMethodCM(node);
			if (node.getSuperclass() != null) {
			//	System.out.println("*  " + node.getSuperclass().getQualifiedName());
				//	//System.out.println("*  " + node.getPackage().getName());
				ITypeBinding parent = node.getSuperclass();
				findDuplicate(parent);
	
				for (ITypeBinding parent2 : node.getInterfaces()) {
					findDuplicate(parent2);	
				}
			}
			//	//System.out.println(node.getQualifiedName());
		}
	}

	public boolean findDuplicateMethodCM(ITypeBinding typeBinding) {
		String decoy = typeBinding.getQualifiedName();
		IMethodBinding[] methods = typeBinding.getDeclaredMethods();
		for(IMethodBinding m : methods) {
		//System.out.println(m.getName());//For the print test
			if(m.getName().compareTo(target.getName()) == 0) {
				if(m.getParameterTypes().length == target.getParameterTypes().length) {
					int index = 0, barrier = 0;
					for(ITypeBinding p : m.getParameterTypes()) {						
	//					System.out.print(p.getQualifiedName() + " | "); //For the print test	
	//					System.out.print(target.getParameterType(index) + " ");
						if(p.getQualifiedName().compareTo(target.getParameterType(index)) == 0) {
							barrier++;
						}
						index++;
					}						
					if (barrier<m.getParameterTypes().length) {
						return false;
					}
	//				boolean child = parentage(decoy);
					
					if (Overshot.contains(typeBinding.getQualifiedName())) {
						return false;
					}
					
					boolean child = parentage(typeBinding);
					if (child == false) {
						return false;
					}
					System.out.println(decoy + " is what we have left");
			
					FirstASTVisitorToFindDuplicateMethods.methodset.add(typeBinding.getQualifiedName());
					System.out.println("[SECOND SEARCH] Duplicate found in package " + typeBinding.getPackage().getName() + ", class " + typeBinding.getName());
					System.out.println("Method " + m.getName() + "("); 
					for(ITypeBinding p : m.getParameterTypes())
						System.out.print(p.getQualifiedName() + ", ");						
					System.out.println(")");
					Overshot.add(typeBinding.getQualifiedName());
					System.out.println(Overshot);
					return true;
				}
			}
		}
		
		return false;
	}
	public boolean parentage(ITypeBinding tB) {
		if (tB!=null) {
			if (tB.getSuperclass() != null) {
				ITypeBinding parent = tB.getSuperclass();
			//	System.out.println("Time to help an orphaned " + tB.getQualifiedName() +"!");
				while (parent != null) { 
				if (FirstASTVisitorToFindDuplicateMethods.methodset.contains(parent.getQualifiedName())) {
					return true;
				}
			/*	if (tB.getInterfaces() != null){
					ITypeBinding[] interfaces = tB.getInterfaces();
					for(ITypeBinding i : interfaces) {
						
					}*/
					parent = parent.getSuperclass();
				}
					/*if (tB.getInterfaces() != null){
					ITypeBinding[] interfaces = tB.getInterfaces();
					for(ITypeBinding i : interfaces) {
						parentage(i);
					}
					}*/

			
		}
	}
			return false;
	}
}
