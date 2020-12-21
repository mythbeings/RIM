package p.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SecondASTVisitorToFindDuplicateMethods extends ASTVisitor {
	public static ArrayList<String> allMethods = new ArrayList<String>();
	public static ArrayList<ITypeBinding> backup = new ArrayList<ITypeBinding>();
	public static ArrayList<ITypeBinding> TTW = new ArrayList<ITypeBinding>();
	public static ArrayList<ITypeBinding> backupbackup = new ArrayList<ITypeBinding>();
	RMethod target = null;
	public static Set<Object> Overshot = FirstASTVisitorToFindDuplicateMethods.methodset;
	static int tally = 0;
	public static ArrayList<String> poly = new ArrayList<String>();
	public static ArrayList<ITypeBinding> pset = new ArrayList<ITypeBinding>();
	public static String current = null;
	public static String past = null;
	public static Boolean revisit = false;
	public static ArrayList<ITypeBinding> review = new ArrayList<ITypeBinding>();
	public static ArrayList<ITypeBinding> review2 = new ArrayList<ITypeBinding>();
	public static ArrayList<String> round2 = new ArrayList<String>();
	public static ArrayList<String> transfer = new ArrayList<String>();
	public static IPackageBinding pastPack = null;
	static boolean no = false;
	public static CompilationUnit cu2 = null;


	public SecondASTVisitorToFindDuplicateMethods(RMethod target) {
		review2 = review;
		this.target = target;
	}

	public boolean visit(TypeDeclaration node) {
		if (!RunAction.TTW.contains(node.resolveBinding()) && node.resolveBinding().toString().contains(RunAction.rename)) {
			RunAction.TTW.add(node.resolveBinding());
		}
		if (pset.size() <= 0) {
			MethodDeclaration[] methods = node.getMethods();
			for (MethodDeclaration m : methods) {
				if (m.getName().getIdentifier().compareTo(target.getName()) == 0) {
					List parameterList = m.parameters();
					if (m.isConstructor() == true) {
						no = true;
					}
					if (Modifier.isPrivate(m.getModifiers())) {
						no = true;
					}
					if (Modifier.isNative(m.getModifiers())) {
						no = true;
					}
					if (Modifier.isStatic(m.getModifiers())) {
						no = true;
					}
		//			if (Modifier.isSynchronized(m.getModifiers())) {
		//				no = true;
		//			}
					
					IJavaElement compare = getIJavaElement(m);
					if (compare.isReadOnly()) {
						no = true;
					}
					if (compare instanceof IMember && ((IMember)compare).isBinary()) {
						no = true;
					}
					if (target.getParameterTypes().length == parameterList.size()) {
						int i = 0;
						for (String parameterType : target.getParameterTypes()) {
							SingleVariableDeclaration p = (SingleVariableDeclaration) parameterList.get(i);
							if (p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0) {
								no = true;
								}
							else {
							i++;
							}
						}
						if (i == target.getParameterTypes().length && no == false) {
							if (pset.size() == 0) {
								for (ITypeBinding p : m.resolveBinding().getParameterTypes()) {
									pset.add(p);
								}
							}
						}
					}
				}
				no = false;
			}
		}
		findDuplicate(node.resolveBinding());
		return true;
	}

	void findDuplicate(ITypeBinding node) {

		// traversing all parent classes

		current = node.getPackage().getName();

		if (past == null) {
			past = node.getPackage().getName();
		}
		if (current.equals(past)) {
			backup = backupbackup;
		} 
		if (node.isInterface()) {
			findDuplicateMethodCM(node);
			if (node.getInterfaces() != null) {
				for (ITypeBinding parent2 : node.getInterfaces()) {
					findDuplicate(parent2);
				}
			}
		}
		if (node != null && node.getQualifiedName() != "java.lang.Object") {
			if (Overshot.contains(node.getQualifiedName())) {
			} else {
				findDuplicateMethodCM(node);
				if (node.getSuperclass() != null) {
					ITypeBinding parent = node.getSuperclass();
					findDuplicate(parent);
				}
			}
		}
	}

	public boolean findDuplicateMethodCM(ITypeBinding typeBinding) {
		boolean clean = false, clear = true, onethrough = false;
		IMethodBinding[] methods = typeBinding.getDeclaredMethods();
		if (Overshot.contains(typeBinding.getQualifiedName())) { // check if already added (saves time)
			clear = false;
		}
		boolean relation = relations(typeBinding);
		if (relation==true && !FirstASTVisitorToFindDuplicateMethods.allRelations.contains(typeBinding.getQualifiedName())) {
			FirstASTVisitorToFindDuplicateMethods.allRelations.add(typeBinding.getQualifiedName());
			FirstASTVisitorToFindDuplicateMethods.searchAgain.add(typeBinding);
		}
		for (IMethodBinding m : methods) {
			clear = true;
			if (m.getName().compareTo(target.getName()) == 0) {
				if (m.isConstructor() == true) {
					clear = false;
				}
		//		if (Modifier.isSynchronized(m.getModifiers())) {
		//			clear = false;
		//		}
				if (Modifier.isPrivate(m.getModifiers())) {
					clear = false;
				}
				if (Modifier.isNative(m.getModifiers())) {
					clear = false;
				}
				if (Modifier.isStatic(m.getModifiers())) {
					clear = false;
				}
				if(cu2 == null) {
					ICompilationUnit unit = (ICompilationUnit) m.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
					if ( unit == null ) {}
					ASTParser parser = ASTParser.newParser( AST.JLS8 );
					parser.setKind( ASTParser.K_COMPILATION_UNIT );
					parser.setSource( unit );
					parser.setResolveBindings( true );
					cu2 = (CompilationUnit) parser.createAST( null );
				}
				MethodDeclaration decl = (MethodDeclaration)cu2.findDeclaringNode( m.getKey() );
				IJavaElement compare = getIJavaElement(decl);
				if (compare!=null) {
					if (compare.isReadOnly()) {
						clear=false;
					}
					if (compare instanceof IMember && ((IMember)compare).isBinary()) {
						clear=false;
					}
				}
			
				if (m.getParameterTypes().length == target.getParameterTypes().length && clear == true) {
					int index = 0, barrier = 0;
					for (ITypeBinding p : m.getParameterTypes()) {
						if (p.getQualifiedName().compareTo(target.getParameterType(index)) == 0) {
							barrier++;
						}
						index++;
					}
					if (barrier != m.getParameterTypes().length) { // confirm parameter contents
						boolean try2;
						try2 = polymorph(m.getParameterTypes(), FirstASTVisitorToFindDuplicateMethods.paraset);
						if (try2 == false) {
							if (FirstASTVisitorToFindDuplicateMethods.backupsend.contains(typeBinding)) {} 
							else {
								FirstASTVisitorToFindDuplicateMethods.backupsend.add(typeBinding);
							}
							clear = false;
						}
					}
				
					boolean child = parentage(typeBinding); // check if parent is currently in the list
					if (child == false && clear == true) {
						if (backup.contains(typeBinding)) {} 
						else {
							backup.add(typeBinding); // save name of those that failed just in case a future instance is
															// linked to it
							review.add(typeBinding);
							round2.add(typeBinding.getQualifiedName());
						}
						if (FirstASTVisitorToFindDuplicateMethods.backupsend.contains(typeBinding)) {} 
						else {
							FirstASTVisitorToFindDuplicateMethods.backupsend.add(typeBinding);
						}
						clear = false;
					}
					if (clear == true) {
						onethrough = true;
						if (Overshot.contains(typeBinding.getQualifiedName())) { // check if already added (saves time)
							onethrough = false;
						}
						FirstASTVisitorToFindDuplicateMethods.methodset.add(typeBinding.getQualifiedName());
						FirstASTVisitorToFindDuplicateMethods.exclusive.add(typeBinding);
					//	FirstASTVisitorToFindDuplicateMethods.methodset2.add(typeBinding);
						if(!FirstASTVisitorToFindDuplicateMethods.allRelations.contains(typeBinding.getQualifiedName())) {
							FirstASTVisitorToFindDuplicateMethods.allRelations.add(typeBinding.getQualifiedName());
							FirstASTVisitorToFindDuplicateMethods.searchAgain.add(typeBinding);
						}
						if (RMethod.runtimePolymorphicMethods.contains(m.getKey()) == false) {
							RMethod.runtimePolymorphicMethods.add(m.getKey());
						}
						if (current.equals(typeBinding.getPackage().getName())) {} 
						else {
							backup.clear();
							current = typeBinding.getPackage().getName();
						}
						Overshot.add(typeBinding.getQualifiedName());
						
						if (typeBinding.getSuperclass() != null) {
							FirstASTVisitorToFindDuplicateMethods.cu = null;
							FirstASTVisitorToFindDuplicateMethods.collectAllduplicatemethodsInParents(typeBinding);
							cu2 = null;
						}
						if (typeBinding.getInterfaces() != null) {
							FirstASTVisitorToFindDuplicateMethods.cu = null;
							FirstASTVisitorToFindDuplicateMethods.collectAllduplicatemethodsInParents(typeBinding);
							cu2 = null;
						}
						if (backup.size() > 0) {
							if (revisit == false) {
								lastCheck();
							}
						}
						clean = true;
						}
					}
				}
			}
		if (onethrough == true) {
		}
		onethrough = false;
		if (clean == true) {
			return true;
		}
		return false;
	}
	

	public boolean parentage(ITypeBinding tB) {

		if (tB != null) {
			if (tB.isInterface()) { // i.e. only interfaces would be found in the parentage
				loopInterface(tB, tB);
			}
			if (tB.getInterfaces() != null) {
				loopInterface(tB, tB);
			}
			if (tB.getSuperclass() != null) {
				ITypeBinding parent = tB.getSuperclass();
				while (parent != null) {
					if (tB.getSuperclass().getQualifiedName().equals("java.lang.Object")) {
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

	public boolean relations(ITypeBinding tB) {

		if (tB != null) {
			if (tB.isInterface()) { // i.e. only interfaces would be found in the parentage
				loopInterface(tB, tB);
			}
			if (tB.getInterfaces() != null) {
				loopInterface(tB, tB);
			}
			if (tB.getSuperclass() != null) {
				ITypeBinding parent = tB.getSuperclass();
				while (parent != null) {
					if (tB.getSuperclass().getQualifiedName().equals("java.lang.Object")) {
						return false;
					}
					if (FirstASTVisitorToFindDuplicateMethods.allRelations.contains(parent.getQualifiedName())) {
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

	
	void loopInterface(ITypeBinding tB, ITypeBinding prime) {
		if (tB.getInterfaces() != null) {
			ITypeBinding[] interfaces = tB.getInterfaces();
			for (ITypeBinding i : interfaces) {
				ITypeBinding parent = i;
				if (FirstASTVisitorToFindDuplicateMethods.allRelations.contains(parent.getQualifiedName())) {
					if(!FirstASTVisitorToFindDuplicateMethods.allRelations.contains(prime.getQualifiedName())) {
						FirstASTVisitorToFindDuplicateMethods.allRelations.add(prime.getQualifiedName());
						FirstASTVisitorToFindDuplicateMethods.searchAgain.add(prime);
					}
				}
				if (FirstASTVisitorToFindDuplicateMethods.methodset.contains(parent.getQualifiedName())) {
					FirstASTVisitorToFindDuplicateMethods.methodset.add(prime.getQualifiedName());
				//	FirstASTVisitorToFindDuplicateMethods.methodset2.add(prime);
					FirstASTVisitorToFindDuplicateMethods.exclusive.add(prime);
					if (!FirstASTVisitorToFindDuplicateMethods.allRelations.contains(prime.getQualifiedName())) {
						FirstASTVisitorToFindDuplicateMethods.allRelations.add(prime.getQualifiedName());
						FirstASTVisitorToFindDuplicateMethods.searchAgain.add(prime);
					}
					IMethodBinding[] methods = prime.getDeclaredMethods();
					for (IMethodBinding m : methods) {
						boolean clear = true;
						if (m.getName().compareTo(target.getName()) == 0) {
							if (m.isConstructor() == true) {
								clear = false;
							}
			//				if (Modifier.isSynchronized(m.getModifiers())) {
			//					clear = false;
			//				}
							if (Modifier.isPrivate(m.getModifiers())) {
								clear = false;
							}
							if (Modifier.isNative(m.getModifiers())) {
								clear = false;
							}
							if (Modifier.isStatic(m.getModifiers())) {
								// System.out.println("this method is static (second)!");
								clear = false;
							}
							if(cu2 == null) {
								ICompilationUnit unit = (ICompilationUnit) m.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
								if ( unit == null ) {}
								ASTParser parser = ASTParser.newParser( AST.JLS8 );
								parser.setKind( ASTParser.K_COMPILATION_UNIT );
								parser.setSource( unit );
								parser.setResolveBindings( true );
								cu2 = (CompilationUnit) parser.createAST( null );
							}
							MethodDeclaration decl = (MethodDeclaration)cu2.findDeclaringNode( m.getKey() );
							IJavaElement compare = getIJavaElement(decl);
							if (compare!=null) {
								if (compare.isReadOnly()) {
									clear=false;
								}
								if (compare instanceof IMember && ((IMember)compare).isBinary()) {
									clear=false;
								}
							}
							if (m.getParameterTypes().length == target.getParameterTypes().length && clear == true) {
								int index = 0, barrier = 0;
								for (ITypeBinding p : m.getParameterTypes()) {
									if (p.getQualifiedName().compareTo(target.getParameterType(index)) == 0) {
										barrier++;
									}
									index++;
								}
								if (barrier != m.getParameterTypes().length) { // confirm parameter contents
									boolean try2;
									try2 = polymorph(m.getParameterTypes(), FirstASTVisitorToFindDuplicateMethods.paraset);
									if (try2 == false) {
										if (FirstASTVisitorToFindDuplicateMethods.backupsend.contains(prime)) {
										} else {
											FirstASTVisitorToFindDuplicateMethods.backupsend.add(prime);
										}
										clear = false;
									}
								}

								if (clear == true) {
									FirstASTVisitorToFindDuplicateMethods.methodset.add(prime.getQualifiedName());
							//		FirstASTVisitorToFindDuplicateMethods.methodset2.add(prime);
									FirstASTVisitorToFindDuplicateMethods.exclusive.add(prime);
									RMethod.runtimePolymorphicMethods.add(m.getKey());
									ITypeBinding[] test = m.getParameterTypes();
									clear = false;
								}
							}
						}
					}
					if (current.equals(prime.getPackage().getName())) {} 
					else {
						backup.clear();
						current = prime.getPackage().getName();
					}
					if (Overshot.contains(prime.getQualifiedName())) {
					} else {
						if (revisit == false) {
							lastCheck();
						}
					}
				
					Overshot.add(prime.getQualifiedName());
					if (prime.getSuperclass() != null) {
						FirstASTVisitorToFindDuplicateMethods.cu = null;
						FirstASTVisitorToFindDuplicateMethods.collectAllduplicatemethodsInParents(prime);
						cu2 = null;
					}
				}
				loopInterface(i, prime);
			}
		}
	}

	void lastCheck() { // checks to see if the new children added by the search are related in some way
						// to previously checked instances
		ArrayList<ITypeBinding> backup2 = new ArrayList<ITypeBinding>();
		ArrayList<String> compare = new ArrayList<String>();
		if (backup.size() > 0 && backup2.size() <= 0) {
			revisit = true;
			backup2 = backup;
			backupbackup = backup; // area causes issue when backup remains full in new packages. backupbackup made
									// to alleviate this issue
			if (backup2.size() > 0) {
				for (int i = 0; i < backup2.size(); i++) {
					compare.clear();
					allRelations(backup2.get(i), compare);
					ITypeBinding now = backup2.get(i);
					if(compare.size()>0) {
					for(int j = 0; j < compare.size(); j++) {
						if(FirstASTVisitorToFindDuplicateMethods.methodset.contains(compare.get(j))) {
							findDuplicateMethodCM(now);
						}
					}
					}
				}
			}
		}
		revisit = false;
	}

	public static boolean polymorph(ITypeBinding[] types, ArrayList<ITypeBinding> paraset) {
		ArrayList<String> potentials = new ArrayList<String>();
		int tallystart = 0;
		if (tally != 0) {
			tally = 0;
			tallystart = tally;
		}
		if (poly.size() < 7) {
			poly.add("byte");
			poly.add("int");
			poly.add("double");
			poly.add("short");
			poly.add("float");
			poly.add("char");
			poly.add("long");
		}

		for (int i = 0; i < paraset.size(); i++) {
			tallystart = tally;
			potentials.clear();

			if (types[i].getQualifiedName().compareTo(paraset.get(i).getQualifiedName()) == 0) {
				tally++;
			}

			else if (poly.contains(types[i].getQualifiedName()) && poly.contains(paraset.get(i).getQualifiedName())) {
				tally++;
			}

			else if (types[i].getQualifiedName().contains("boolean") && paraset.get(i).getQualifiedName().contains("boolean")) {
				tally++;
			}
			else if (types[i].isSubTypeCompatible(paraset.get(i)) || paraset.get(i).isSubTypeCompatible(types[i])) {
				tally++;
			}
			else if (!types[i].getQualifiedName().equals("java.lang.Object") && !paraset.get(i).getQualifiedName().equals("java.lang.Object")) {
				if (types[i].isInterface() || types[i].isClass()) {
					potentials.clear();
					ITypeBinding temp = types[i];
					allRelations(temp, potentials);
					for (int j = 0; j<potentials.size(); j++) {
							if(potentials.get(j).equals(paraset.get(i).getQualifiedName()))
							{
								tally++;
							}
					}
					if ((tally==tallystart) == true) {
						if (paraset.get(i).isInterface() || paraset.get(i).isClass()) {
							temp = paraset.get(i);
							potentials.clear();
							allRelations(temp, potentials);
							for (int j = 0; j<potentials.size(); j++) {
								if(potentials.get(j).equals(types[i].getQualifiedName())){
									tally++;
								}
							}
						}
					}
				}
			}
			if (tally==tallystart == true){
				if (RunAction.compatibleTypes.containsKey(types[i]) && RunAction.compatibleTypes.containsKey(paraset.get(i))) {
					if(RunAction.compatibleTypes.get(types[i]).contains(paraset.get(i))) {
						if (RunAction.compatibleTypes.get(paraset.get(i)).contains(types[i])) {
							tally++;
						}
					}
					else {}
				}
			tallystart = tally;
			}
			if (tally == paraset.size()) {
			return true;
			}
		}
		return false;
	}
	
	public static boolean allRelations(ITypeBinding collect, ArrayList<String> potentials) {
		if (collect != null) {
			if(collect.getQualifiedName().equals("java.lang.Object")) {
				return false;
			}
			if(!potentials.contains(collect.getQualifiedName())) {
				potentials.add(collect.getQualifiedName());
			}
			if (collect.getInterfaces()!=null) { // i.e. only interfaces would be found in the parentage
				ITypeBinding[] interfaces = collect.getInterfaces();
				for (ITypeBinding i : interfaces) {
					ITypeBinding parent = i;
					allRelations(parent, potentials);
					}
			}
			if (collect.getSuperclass() != null) {
				ITypeBinding parent = collect.getSuperclass();
				while (parent != null) {
					if (collect.getSuperclass().getQualifiedName().equals("java.lang.Object")) {
						return false;
					}
					if (parent.getInterfaces() != null) {
						allRelations(parent, potentials);
					}
					parent = parent.getSuperclass();
				}
			}
		}
		
		return true;
	}
	public static IJavaElement getIJavaElement(ASTNode node){

	     IJavaElement javaElement = null;

	     // Find IJavaElement corresponding to the ASTNode

	     if (node instanceof MethodDeclaration) {
	  //  	 	System.out.println(((MethodDeclaration) node).getName());

	           javaElement = ((MethodDeclaration) node).resolveBinding().getJavaElement();

	     }

	     return javaElement;

	}
}