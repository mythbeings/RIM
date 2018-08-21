package p.actions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
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
					/*ITypeBinding[] passon = type.resolveBinding().getInterfaces();
					for(ITypeBinding t : type.resolveBinding().getInterfaces()) {
					            for(IMethodBinding n : t.getDeclaredMethods()) {
					                n.getName();
					                n.getParameterTypes();
					            }
					        }
*/

					collectAllduplicatemethodsInParents(type, methodset);
				}
			}			
		}
		
		
		
		
		return true;
	}

	void collectAllduplicatemethodsInParents(TypeDeclaration m, Set methodset) {
		Interfacesearch(m, methodset);
		Superclasssearch(m, methodset);
	}

	void Superclasssearch(TypeDeclaration node, Set methodSet) {

		if (node.getParent() != null) {
			
			Boolean m = findDuplicateMethodPM(node, methodSet);
			if (m == true) {
				methodSet.add(node);
			}
			Type parent = (Type) node.getSuperclassType();
			collectAllduplicatemethodsInParents((TypeDeclaration) parent, methodset);
		}
	}

	void Interfacesearch(TypeDeclaration node, Set methodSet) {
		
	/*	For each interface in origin.getInterfaces()
		RMethod m = findDuplicateMethod(interface)
		if (m != null) methodSet.add(m)
		Interfacesearch(interface, methodSet) 	
		*/	

		ITypeBinding[] passon = node.resolveBinding().getInterfaces();
		for(ITypeBinding t : node.resolveBinding().getInterfaces()) {
		            for(IMethodBinding n : t.getDeclaredMethods()) {
		    			System.out.println(n.getName());//For the print test
		                if (n.getName().compareTo(target.getName()) == 0);{
		                ITypeBinding[] parameterList = n.getParameterTypes();
		                int i = 0;
						for(String parameterType : target.getParameterTypes()) {	
							System.out.print(parameterType + " "); //For the print test
							SingleVariableDeclaration p = (SingleVariableDeclaration)parameterList[i];						
							if(p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0)
								break;
							i++;
						}
						if(i == target.getParameterTypes().length) {
							System.out.print("***");//For the print test
							methodset.add(n.getName());
						}
		                }
		            }
		            TypeDeclaration next = (TypeDeclaration) t.getTypeDeclaration();
					Interfacesearch(next, methodSet);
		        }
		 // can a class be turned into a methoddeclaration to fix this error?
		}


	public Boolean findDuplicateMethodPM(TypeDeclaration node, Set methodSet2) {
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
