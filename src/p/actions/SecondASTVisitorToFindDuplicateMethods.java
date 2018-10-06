package p.actions;

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
	
	public SecondASTVisitorToFindDuplicateMethods(RMethod target) {
		this.target = target;
	}

	public boolean visit(TypeDeclaration node) {
		findDuplicate(node.resolveBinding());

		
		return true;
	}

	void findDuplicate(ITypeBinding node) {

//traversing all parent classes
		System.out.println("In second part now");
		System.out.println("Current Methodset contents: ");
		System.out.println(FirstASTVisitorToFindDuplicateMethods.methodset);
		if (node != null) {
		System.out.println(node.getQualifiedName());
		findDuplicateMethodCM(node);
		if (node.getSuperclass() != null) {
			System.out.println("*  " + node.getSuperclass().getQualifiedName());
			System.out.println("*  " + node.getPackage().getName());
			ITypeBinding parent = node.getSuperclass();
			findDuplicate(parent);

			for (ITypeBinding parent2 : node.getInterfaces()) {
				findDuplicate(parent2);

			}
		}
	}
	}

	public boolean findDuplicateMethodCM(ITypeBinding typeBinding) {
		IMethodBinding[] methods = typeBinding.getDeclaredMethods();
		for(IMethodBinding m : methods) {
			//System.out.println(m.getName());//For the print test
			if(m.getName().compareTo(target.getName()) == 0) {
				if(m.getParameterTypes().length == target.getParameterTypes().length) {
					int index = 0;
					for(ITypeBinding p : m.getParameterTypes()) {						
				//		System.out.print(p.getName() + " "); //For the print test						
						if(p.getName().compareTo(target.getParameterType(index++)) != 0) {
							return false;
						}
					}						
				}
				
				System.out.println("Duplicate found in" + typeBinding.getName());
				System.out.println("Method " + m.getName() + "("); 
				for(ITypeBinding p : m.getParameterTypes())
					System.out.print(p.getQualifiedName() + ", ");						
				System.out.println(")");//For the print test
				FirstASTVisitorToFindDuplicateMethods.methodset.add(typeBinding.getName());
				return true;
			}
		}
		
		return false;
	}
}
