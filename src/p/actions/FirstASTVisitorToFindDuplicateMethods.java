package p.actions;

import java.lang.reflect.Modifier;
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
	static RMethod target= null;
			
	public FirstASTVisitorToFindDuplicateMethods(RMethod target) {
		this.target = target;
	}
	
	public boolean visit(TypeDeclaration type) {
		if (methodset.size()>0) {
			methodset.clear();
		}
		MethodDeclaration[] methods = type.getMethods();
		for(MethodDeclaration m : methods) {
			if(m.getName().getIdentifier().compareTo(target.getName()) == 0) { 
				List parameterList = m.parameters();
				if(target.getParameterTypes().length == parameterList.size()) {
					int i = 0;
					for(String parameterType : target.getParameterTypes()) {		
						SingleVariableDeclaration p = (SingleVariableDeclaration)parameterList.get(i);						
						if(p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0)
							break;
						i++;
					}
					if(i == target.getParameterTypes().length) {						
						methodset.add(type.resolveBinding().getQualifiedName()); 
						collectAllduplicatemethodsInParents(type.resolveBinding());
					}
				}
			}			
		}
		
		
		
		
		return true;
	}
	
	static void collectAllduplicatemethodsInParents(ITypeBinding typeBinding) {
		if (typeBinding!=null) {
			System.out.println(typeBinding.getName());
			if (typeBinding.getInterfaces()!=null) {
				System.out.print(typeBinding.getName());
			}
			System.out.print("|" + typeBinding.getName());
			for(ITypeBinding i : typeBinding.getInterfaces()) {
				System.out.println(i.getName() + "*");
				searchInterface(i);
			}
			searchSuperClass(typeBinding);
		}
	}

	static void searchSuperClass(ITypeBinding typeBinding) {	
	//	if (typeBinding.getSuperclass() != null) {
			Boolean m = findDuplicateMethodPM(typeBinding);
			if (m == true) {
				methodset.add(typeBinding.getQualifiedName());
			}
			ITypeBinding parent =  typeBinding.getSuperclass();
			if(parent == null) {}
			else {
				System.out.println(parent.getName());
				collectAllduplicatemethodsInParents(parent);
			}			
	//	}
	}

	static void searchInterface(ITypeBinding typeBinding) {
		System.out.println(typeBinding.getName());
		for(IMethodBinding m : typeBinding.getDeclaredMethods()) {		
			if(m.getName().compareTo(target.getName()) == 0) {
				if (Modifier.isPrivate(m.getModifiers())) {
				//	System.out.println("this method is stealthy!");
					break;
				}
				if(m.getParameterTypes().length == target.getParameterTypes().length) {
					int index = 0, barrier = 0;
					for(ITypeBinding p : m.getParameterTypes()) {						
						if(p.getQualifiedName().compareTo(target.getParameterType(index)) == 0) {
							barrier++;		
						}
						index++;	
					}						
				if (barrier<m.getParameterTypes().length) {
					break;
					}
				System.out.println("[FIRST SEARCH] Duplicate found in " + typeBinding.getName());
				System.out.println("Method " + m.getName() + "("); 
				for(ITypeBinding p : m.getParameterTypes()) {
					System.out.print(p.getQualifiedName() + ", ");					
					methodset.add(typeBinding.getQualifiedName());
					System.out.println(methodset.toString());
					}
				}
			//			//System.out.println("Current Methodset contents: ");
			//			//System.out.println(methodset.toString())
				}
			}
		ITypeBinding[] interfaces = typeBinding.getInterfaces();
		for(ITypeBinding i : interfaces) {
			searchInterface(i);
		}
		}


	public static boolean findDuplicateMethodPM(ITypeBinding typeBinding) {
		IMethodBinding[] methods = typeBinding.getDeclaredMethods();
		for(IMethodBinding m : methods) {
	//	System.out.println(m.getName());//For the print test
			if(m.getName().compareTo(target.getName()) == 0) {
				if (Modifier.isPrivate(m.getModifiers())) {
		//			System.out.println("this method is stealthy!");
					return false;
				}
				if(m.getParameterTypes().length == target.getParameterTypes().length) {
					int index = 0, barrier = 0;
					for(ITypeBinding p : m.getParameterTypes()) {						
					//	System.out.print(p.getQualifiedName() + " "); //For the print test	
					//	System.out.print(target.getParameterType(index) + " ");
						if(p.getQualifiedName().compareTo(target.getParameterType(index)) == 0) {
							barrier++;
						}
						index++;
					}						
					if (barrier<m.getParameterTypes().length) {
						return false;
					}
				
					System.out.println("[FIRST SEARCH] Duplicate found in " + typeBinding.getName());
					System.out.println("Method " + m.getName() + "("); 
					for(ITypeBinding p : m.getParameterTypes())
						System.out.print(p.getQualifiedName() + ", ");						
					//System.out.println(")");//For the print test
					methodset.add(typeBinding.getQualifiedName());
					System.out.println(methodset.toString());
					return true;
				}
			}
		}
		
		return false;
	}
}
