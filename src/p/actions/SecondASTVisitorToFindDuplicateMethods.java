package p.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import java.lang.reflect.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SecondASTVisitorToFindDuplicateMethods extends ASTVisitor {
	Set methodSet; // methodset has red duplicate methods collected at phase #1 above.
					// is this true? Seems like we're creating a new one in here. Does something further need to occur to pass everything down?
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
		isDuplicateMethod(node, methodSet);
		if (node.getParent() != null) {
			Type parent = (Type) node.getSuperclassType();
			findDuplicate((TypeDeclaration) parent);

			for (Object parent2 : node.superInterfaceTypes()) {
				findDuplicate((TypeDeclaration) parent2);

			}
		}
	}

	public Boolean isDuplicateMethod(TypeDeclaration node, Set methodSet2) {
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
					System.out.print("***");//For the print test
					methodSet2.add(m);
					return true;
	}
}
		}
		return false;
	}
}
