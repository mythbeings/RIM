package p.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.w3c.dom.Node;

public class FirstASTVisitorToFindDuplicateMethods extends ASTVisitor {
	public static Set<Object> methodset = new HashSet<Object>();

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
			// Package print -- running into problems (code below retrieves package org.eclipse.jdt.core.dom)
			System.out.println(m.getClass().getPackage()); 
//.getqualifiedname
			// System.out.println(m);  // = easy way to get entirety of method (ex. public void m(String s,int i,q.B a){})
			// Class print
			System.out.println(type.getName().getIdentifier());  //prints class name
			System.out.println(m.getName().getIdentifier() + " | " + target.getName()); //checks what both are
			if(m.getName().getIdentifier().compareTo(target.getName()) == 0) { 
				List parameterList = m.parameters();
				int i = 0;
				for(String parameterType : target.getParameterTypes()) {		
					System.out.print((SingleVariableDeclaration)parameterList.get(i) + " "); //Test parameter types
					
					SingleVariableDeclaration p = (SingleVariableDeclaration)parameterList.get(i);						
					if(p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0)
						break;
					i++;
				}
				if(i == target.getParameterTypes().length) {						
					//You found a method declaration with the same name and parameters of the target.
					//done here...
					System.out.println(type.getClass().getPackage()); //trying to find how to print package...
					System.out.println(type.getName().getIdentifier());  //prints class name
					
					methodset.add(type.getName().getIdentifier()); 
					System.out.println("Current Methodset contents: ");
					System.out.println(methodset);
					System.out.println(type);
					collectAllduplicatemethodsInParents(type.resolveBinding());
				}
			}			
		}
		
		
		
		
		return true;
	}

	/*
	void collectAllduplicatemethodsInParents(TypeDeclaration m) {
		if (m!=null) {
		System.out.println(m + "*");
		if (m.resolveBinding().getInterfaces() != null) {
		Interfacesearch(m);}
		else {
			System.out.println("No Interfaces");
		}
		Superclasssearch(m);
	}
	}
	*/
	
	void collectAllduplicatemethodsInParents(ITypeBinding typeBinding) {
		if (typeBinding!=null) {
			System.out.println(typeBinding.getQualifiedName() + "*");
			
			ITypeBinding[] interfaces = typeBinding.getInterfaces();
			for(ITypeBinding i : interfaces) {
				searchInterface(i);
			}
			searchSuperClass(typeBinding);
		}
	}

	void searchSuperClass(ITypeBinding typeBinding) {		
		if (typeBinding.getSuperclass() != null) {
			System.out.println(typeBinding.getSuperclass().getQualifiedName());
			
			Boolean m = findDuplicateMethodPM(typeBinding);
			if (m == true) {
				methodset.add(typeBinding.getName());
			}
			System.out.println("Current Methodset contents: ");
			System.out.println(methodset);
			System.out.println(typeBinding.getSuperclass().getQualifiedName());
			//Type parent =  node.getSuperclassType();
			ITypeBinding parent =  typeBinding.getSuperclass();
			if(parent == null)
				System.out.println("Parent is null");
			else {
				System.out.println("This works: " + parent.getQualifiedName());
				//System.out.println("This works: " + (TypeDeclaration) parent);
				collectAllduplicatemethodsInParents(parent);
			}			
		}
		else {
			System.out.println("typeBinding.getSuperclass() returns null");
		}
	}

	//void searchInterface(TypeDeclaration node) {
	void searchInterface(ITypeBinding typeBinding) {
		
	/*	For each interface in origin.getInterfaces()
		RMethod m = findDuplicateMethod(interface)
		if (m != null) methodSet.add(m)
		Interfacesearch(interface, methodSet) 	
		*/	

		//ITypeBinding[] passon = new ITypeBinding[];
		//ITypeBinding[] passon = node.resolveBinding().getInterfaces();
		for(ITypeBinding t : typeBinding.getInterfaces()) {
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
							System.out.println("Duplicate found in" + typeBinding.getQualifiedName());
							System.out.println("Method " + n.getName() + "(" + parameterList + ")");//For the print test
							methodset.add(typeBinding.getQualifiedName());
						}
						
						System.out.println("Current Methodset contents: ");
						System.out.println(methodset.toString());
					}
			}
		    
			//TypeDeclaration next = (TypeDeclaration) t.getTypeDeclaration().;
					
			if (t.getInterfaces() != null) {
				searchInterface(t);
			}
			else {
				System.out.println("No Interfaces");
			}
		}
	}


	public boolean findDuplicateMethodPM(ITypeBinding typeBinding) {
		IMethodBinding[] methods = typeBinding.getDeclaredMethods();
		for(IMethodBinding m : methods) {
			System.out.println(m.getName());//For the print test
			if(m.getName().compareTo(target.getName()) == 0) {
				if(m.getParameterTypes().length == target.getParameterTypes().length) {
					int index = 0;
					for(ITypeBinding p : m.getParameterTypes()) {						
						System.out.print(p.getName() + " "); //For the print test						
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
				methodset.add(typeBinding.getName());
				return true;
			}
		}
		
		return false;
	}
}
