package p.actions;

import java.lang.reflect.Type;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SecondASTVisitorToFindDuplicateMethods extends ASTVisitor {
	/*
	Set methodSet; // methodset has red duplicate methods collected at phase #1 above.
					// is this true? Does something further need to occur to pass everything down?

	public boolean visit(TypeDeclaration node) {
		findDuplicate(node);

		return true;
	}

	void findDuplicate(TypeDeclaration node) {

//traversing all parent classes
		for (MethodDeclaration m : node.getMethods()) {
			if (isDuplicateMethod(m, methodSet) == true)
				methodSet.add(m);
		}

		Type parent = node.getSuperclassType(); // Tried following info from link provided. Doesn't seem to work...
		if (parent != null) {
			findDuplicate(parent);

			for (Type parent : node.superInterfaceTypes()) {
				findDuplicate((TypeDeclaration) parent);

			}
		}
	}

	private boolean isDuplicateMethod(MethodDeclaration m, Set methodSet) {
// Is there a way to call rename to run this section?
		if (m.getName().toString().compareTo("n") == 0) {
			String[] parameterTypes = m.getParameterTypes(); // is there a better way for this part?
			if (parameterTypes.length == 3) {
				if (parameterTypes[0] == "String") {
					if (parameterTypes[1] == "int") { // this reminds me: also need to modify rename (and this, should
// rename not be usable here) to check for type conversion
						if (parameterTypes[2] == "q.B") {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	*/
}
