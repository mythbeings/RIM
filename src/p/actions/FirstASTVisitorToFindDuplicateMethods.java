package p.actions;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.w3c.dom.Node;

public class FirstASTVisitorToFindDuplicateMethods extends ASTVisitor {
	Set methodset;

	RMethod target= null;
			
	public FirstASTVisitorToFindDuplicateMethods(RMethod target) {
		this.target = target;
	}
	
	public boolean visit(TypeDeclaration type) {
		// check if type is the class where the target method resides
		//if(type.isInterface() == true)
		//	return true;
		
		MethodDeclaration[] methods = type.getMethods();
		for(MethodDeclaration m : methods) {
			if(m.getName().getIdentifier().compareTo(target.getName()) == 0) { 
				List parameterList = m.parameters();
				int i = 0;
				for(String parameterType : target.getParameterTypes()) {				
					SingleVariableDeclaration p = (SingleVariableDeclaration)parameterList.get(i);						
					if(p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0)
						break;
					i++;
				}
				
				if(i == target.getParameterTypes().length) {						
					//You found a method declaration with the same name and parameters of the target.								
				}
			}			
		}
		
		
		//done here...
		
		
		if (type.getMethods().toString().contains("n(")) {
			// if true ...
			collectAllduplicatemethodsInParents((Node) type, methodset);
		}
		
		return true;
	}

	void collectAllduplicatemethodsInParents(Node currentclass, Set methodset) {

		Interfacesearch(currentclass, methodset);
		Superclasssearch(currentclass, methodset);
	}

	void Superclasssearch(Node origin, Set methodSet) {

		if (origin.getParentNode() != null) {
			RMethod m = findDuplicateMethod(origin);
			if (m != null) {
				methodSet.add(m);
			}
			Node parent = origin.getParentNode();
			collectAllduplicatemethodsInParents(parent, methodset);
		}
	}

	void Interfacesearch(Node origin, Set methodSet) {
		int i;
		Class[] interfaces = origin.getClass().getInterfaces();  //this is java reflection, which you cannot use here...
		for (i = 0; i < interfaces.length; i++) {
			Class interfacecheck = interfaces[i];
			RMethod m = findDuplicateMethod(interfacecheck); // need to find a way to send interface FDM -- should one
																// for classes be made?
			if (m != null)
				methodSet.add(m);
			Interfacesearch(interfacecheck, methodSet); // can a class be turned into a node to fix this error?
		}
	}
	
	public static RMethod findDuplicateMethod(Node origin) {
		// What's the difference between this and isDuplicateMethod (besides parameters)? Is it necessary?
		return null;
	}
}
