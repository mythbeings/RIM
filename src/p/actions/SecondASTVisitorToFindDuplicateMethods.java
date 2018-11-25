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

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SecondASTVisitorToFindDuplicateMethods extends ASTVisitor {
	public static ArrayList<ITypeBinding> backup = new ArrayList<ITypeBinding>();
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
	//	System.out.println("We are examining" + node.getName());
		
		if (node.isInterface()) {
			findDuplicateMethodCM(node);
			if (node.getInterfaces()!=null) {
				for (ITypeBinding parent2 : node.getInterfaces()) {
			//		System.out.println(parent2.getName());
					findDuplicate(parent2);	
				}
			}
		}
		if (node != null && node.getQualifiedName() != "java.lang.Object") {
			findDuplicateMethodCM(node);
			if (node.getSuperclass() != null) {
			//	System.out.println("*  " + node.getSuperclass().getQualifiedName());
				//	//System.out.println("*  " + node.getPackage().getName());
				ITypeBinding parent = node.getSuperclass();
				findDuplicate(parent);
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
				if (Modifier.isPrivate(m.getModifiers())) {
					System.out.println("this method is stealthy!");
					return false;
				}
				if(m.getParameterTypes().length == target.getParameterTypes().length) {
					int index = 0, barrier = 0;
					for(ITypeBinding p : m.getParameterTypes()) {						
				//		System.out.print(p.getQualifiedName() + " | "); //For the print test	
				//		System.out.print(target.getParameterType(index) + " ");
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
						backup.add(typeBinding);
						return false;
					}
				//	System.out.println(child + " is what we have left");
			
					FirstASTVisitorToFindDuplicateMethods.methodset.add(typeBinding.getQualifiedName());
					System.out.println("[SECOND SEARCH] Duplicate found in package " + typeBinding.getPackage().getName() + ", class " + typeBinding.getName());
					System.out.println("Method " + m.getName() + "("); 
					for(ITypeBinding p : m.getParameterTypes())
						System.out.print(p.getQualifiedName() + ", ");						
					System.out.println(")");
					Overshot.add(typeBinding.getQualifiedName());
					System.out.println(Overshot);
					if (typeBinding.getSuperclass() != null) {
						FirstASTVisitorToFindDuplicateMethods.searchSuperClass(typeBinding);
					}
					lastCheck();
					System.out.println(FirstASTVisitorToFindDuplicateMethods.methodset);
					return true;
				}
			}
		}
		
		return false;
	}
	public boolean parentage(ITypeBinding tB) {
		
		if (tB!=null) {
			if (tB.isInterface()){
				loopInterface(tB,tB);
							}
						
			if (tB.getInterfaces()!=null) {
				loopInterface(tB, tB);
				}
			if (tB.getSuperclass() != null) {
				ITypeBinding parent = tB.getSuperclass();
			//	System.out.println("Time to help an orphaned " + tB.getSuperclass().getQualifiedName() +"!");
			//	System.out.println("Time to help an orphaned " + tB.getQualifiedName() +"!");
				while (parent != null) { 
					if (tB.getSuperclass().getQualifiedName().equals("java.lang.Object")){
						return false;
					}
					if (FirstASTVisitorToFindDuplicateMethods.methodset.contains(parent.getQualifiedName())) {
						return true;
					}
					if (parent.getInterfaces() != null) {
						loopInterface(parent, tB);
					}
						parent = parent.getSuperclass();
					}
				}
			
		}
			return false;
		}
	
void loopInterface(ITypeBinding tB, ITypeBinding prime){
	if (tB.getInterfaces()!=null) {
		ITypeBinding[] interfaces = tB.getInterfaces();
		for(ITypeBinding i : interfaces) {
			ITypeBinding parent = i;
			if (FirstASTVisitorToFindDuplicateMethods.methodset.contains(parent.getQualifiedName())) {
				FirstASTVisitorToFindDuplicateMethods.methodset.add(prime.getQualifiedName());
				System.out.println("[SECOND SEARCH] (inter) Duplicate found in package " + prime.getPackage().getName() + ", class " + prime.getName());
				Overshot.add(prime.getQualifiedName());
				System.out.println(Overshot);
				lastCheck();
				System.out.println(FirstASTVisitorToFindDuplicateMethods.methodset);
				if (prime.getSuperclass() != null) {
					FirstASTVisitorToFindDuplicateMethods.searchSuperClass(prime);
				}
				}
			
			loopInterface(i, prime);
			}
		}
	}

void lastCheck() {
	ArrayList<ITypeBinding> backup2 = new ArrayList<ITypeBinding>();
	if (backup.size()>0) {
		backup2 = backup;
		for(int i=0; i<backup2.size(); i++) {
		//	System.out.println("The issue is here");
		//	System.out.println(backup.get(i).getQualifiedName());
		//	System.out.println(backup2.get(i).getQualifiedName());
			findDuplicateMethodCM(backup2.get(i));
			backup2.remove(i);
		}
	}
}
}

