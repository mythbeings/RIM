package p.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
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
		findDuplicate(node);

		
		return true;
	}

	void findDuplicate(TypeDeclaration node) {

//traversing all parent classes
		if (node != null) {
		isDuplicateMethod(node);
		if (node.getParent() != null) {
			Type parent = (Type) node.getSuperclassType();
			findDuplicate((TypeDeclaration) parent);

			for (Object parent2 : node.superInterfaceTypes()) {
				findDuplicate((TypeDeclaration) parent2);

			}
		}
	}
	}

	public Boolean isDuplicateMethod(TypeDeclaration node) {
		MethodDeclaration[] methods = node.getMethods();
		for(MethodDeclaration m : methods) {
			System.out.println(m.getName());//For the print test
			if(m.getName().getIdentifier().compareTo(target.getName()) == 0) { 
				List parameterList = m.parameters();
				int i = 0;
				for(String parameterType : target.getParameterTypes()) {
					System.out.print(parameterType + " "); //For the print test
					SingleVariableDeclaration p = (SingleVariableDeclaration)parameterList.get(i);						
					if(p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0)
						break;
					i++;
				}
				if(i == target.getParameterTypes().length) {
					System.out.println("Duplicate found in" + node.getName().getIdentifier());
					System.out.println("Method " + m.getName() + "(" + parameterList + ")");//For the print test
					FirstASTVisitorToFindDuplicateMethods.methodset.add(node.getName().getIdentifier());
					return true;
	}
}
		}
		return false;
	}
}
