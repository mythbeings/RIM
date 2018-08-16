package p.actions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
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
					//done here...
					
					methodset.add(m);
					
					//code from the email -- should it have its own method?
					ITypeBinding[] passon = type.resolveBinding().getInterfaces();
					for(ITypeBinding t : type.resolveBinding().getInterfaces()) {
					            for(IMethodBinding n : t.getDeclaredMethods()) {
					                n.getName();
					                n.getParameterTypes();
					            }
					        }


					collectAllduplicatemethodsInParents(m, methodset);
				}
			}			
		}
		
		
		
		
		return true;
	}

	void collectAllduplicatemethodsInParents(MethodDeclaration m, Set methodset) {
		Interfacesearch(m, methodset);
		Superclasssearch(m, methodset);
	}

	void Superclasssearch(MethodDeclaration node, Set methodSet) {

		if (node.getParent() != null) {
			RMethod m = findDuplicateMethodPM(node, methodSet);
			if (m != null) {
				methodSet.add(m);
			}
			ASTNode parent = node.getParent();
			collectAllduplicatemethodsInParents((MethodDeclaration) parent, methodset);
		}
	}

	void Interfacesearch(MethodDeclaration node, Set methodSet) {
	/*	For each interface in origin.getInterfaces()
		RMethod m = findDuplicateMethod(interface)
		if (m != null) methodSet.add(m)
		Interfacesearch(interface, methodSet) 	
		*/	
		//Need to use getInterfaces
		//getInterfaces is used by type or class
	/*	int i;
		Class[] interfaces = node.getClass().getInterfaces();  //this is java reflection, which you cannot use here...
																//what should be used instead?
		for (i = 0; i < interfaces.length; i++) {
			Method[] methods;
			Class interfacecheck = interfaces[i];
			methods = interfaces[i].getMethods();
			RMethod m = findDuplicateMethodPM(methods, methodSet); // need to find a way to send interface FDM -- should one
																// for classes be made?
			if (m != null)
				methodSet.add(m);
			Interfacesearch(interfacecheck, methodSet); // can a class be turned into a methoddeclaration to fix this error?
		}*/
	}


	public RMethod findDuplicateMethodPM(MethodDeclaration node, Set methodSet2) {
			if(node.getName().getIdentifier().compareTo(target.getName()) == 0) { 
				List parameterList = node.parameters();
				int i = 0;
				for(String parameterType : target.getParameterTypes()) {				
					SingleVariableDeclaration p = (SingleVariableDeclaration)parameterList.get(i);						
					if(p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0)
						break;
					i++;
				}
				if(i == target.getParameterTypes().length) {						
					//return ;    //an RMethod requires something returned (other than null if the node is to be added to the set). 
								  //What would qualify?
				}
	}
		// TODO Auto-generated method stub
		return null;
	}
}
