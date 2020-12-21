package p.actions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.w3c.dom.Node;

public class FirstASTVisitorToFindDuplicateMethods extends ASTVisitor {
	public static Set<Object> methodset = new HashSet<Object>();
//	public static Set<Object> methodset2 = new HashSet<Object>();
	public static ArrayList<ITypeBinding> backupsend = new ArrayList<ITypeBinding>();
	public static ArrayList<ITypeBinding> paraset = new ArrayList<ITypeBinding>();
	// public static ArrayList<ITypeBinding> paraset2 = new
	// ArrayList<ITypeBinding>();
	public static Set<Object> polymethodset = new HashSet<Object>();
	static RMethod target = null;
	static int tally = 0;
	public static ArrayList<String> poly = new ArrayList<String>();
	public static ArrayList<String> allRelations = new ArrayList<String>();
	public static ArrayList<ITypeBinding> searchAgain = new ArrayList<ITypeBinding>();
	public static ArrayList<ITypeBinding> exclusive = new ArrayList<ITypeBinding>();
	static boolean revisit = false;
	static boolean no = false;
	public static boolean privacytest = false;
	public static boolean privacy = false;
	public static boolean initial = false;
	public static CompilationUnit cu = null;

	public FirstASTVisitorToFindDuplicateMethods(RMethod target/*, ICompilationUnit iCU*/) {
		this.target = target;
	}

	public boolean visit(TypeDeclaration type) {
		MethodDeclaration[] methods = type.getMethods();
		for (MethodDeclaration m : methods) {
			if (m.getName().getIdentifier().compareTo(target.getName()) == 0) {
				privacytest=false;
				List parameterList = m.parameters();
				if (m.isConstructor() == true) {
					no = true;
				}
				if (Modifier.isPrivate(m.getModifiers())) {
					privacytest = true;
				}
				if (Modifier.isNative(m.getModifiers())) {
					no = true;
				}
				if (Modifier.isStatic(m.getModifiers())) {
					no = true;
				}
				
			//	if (Modifier.isSynchronized(m.getModifiers())) {
			//		no = true;
			//	}
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
						if (p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0) {no = true;}
						else {
						i++;
						}
					}
					if (i == target.getParameterTypes().length && no == false) {
						if(privacytest==true) {
							privacy=true;
						}
						if(privacytest==false) {
							initial=true;
							privacy=false;
						}
						if(!methodset.contains(type.resolveBinding().getQualifiedName())) {
							methodset.add(type.resolveBinding().getQualifiedName());
					//		methodset2.add(m.resolveBinding());
							exclusive.add(type.resolveBinding());
							RMethod.runtimePolymorphicMethods.add(m.resolveBinding().getKey());
						}
						if(!allRelations.contains(type.resolveBinding().getQualifiedName())) {
							allRelations.add(type.resolveBinding().getQualifiedName());
							searchAgain.add(type.resolveBinding());
						}
						if (paraset.size() == 0) {
							for (ITypeBinding p : m.resolveBinding().getParameterTypes()) {
								paraset.add(p);
							}
						}
						RunAction.valid=true;
					}
				}
			}
			no = false;
		}
		
		if(privacy == false) {
			findDuplicateMethodPM(type.resolveBinding());
			initial=false;
			collectAllduplicatemethodsInParents(type.resolveBinding());
		}
		else {
			findDuplicateMethodPM(type.resolveBinding());
		}
		
		return true;
	}

	static void collectAllduplicatemethodsInParents(ITypeBinding typeBinding) {
		if (typeBinding != null) {
			if (typeBinding.getInterfaces() != null) {}
			for (ITypeBinding i : typeBinding.getInterfaces()) {
				searchInterface(i);
			}
			searchSuperClass(typeBinding);
		}
	}

	static void searchSuperClass(ITypeBinding typeBinding) {
		Boolean m = findDuplicateMethodPM(typeBinding);
		if (m == true) {
			if(!allRelations.contains(typeBinding.getQualifiedName())) {
				allRelations.add(typeBinding.getQualifiedName());
				searchAgain.add(typeBinding);
			}						
			if(!methodset.contains(typeBinding.getQualifiedName())) {
				methodset.add(typeBinding.getQualifiedName());
		//		methodset2.add(typeBinding);
				exclusive.add(typeBinding);
				RMethod.runtimePolymorphicMethods.add(typeBinding.getKey());
			}
		}
		ITypeBinding parent = typeBinding.getSuperclass();
		if (parent == null) {} 
		else {
			if(!allRelations.contains(parent.getQualifiedName())) {
				searchAgain.add(parent);
				allRelations.add(parent.getQualifiedName());
			}
			collectAllduplicatemethodsInParents(parent);
		}
	}

	static void searchInterface(ITypeBinding typeBinding) {
		boolean next = false;
		boolean wrong = false;
		boolean once = false;
		if(!allRelations.contains(typeBinding.getQualifiedName())) {
			allRelations.add(typeBinding.getQualifiedName());
			searchAgain.add(typeBinding);
		}

		for (IMethodBinding m : typeBinding.getDeclaredMethods()) {
			if(!RMethod.runtimePolymorphicMethods.contains(m.getKey())) {
				if (m.getName().compareTo(target.getName()) == 0) {
					if (m.isConstructor() == true) {
						wrong = true;
					}
					if (Modifier.isPrivate(m.getModifiers())) {
						wrong = true;
					}
					if (Modifier.isNative(m.getModifiers())) {
						wrong = true;
					}
					if (Modifier.isStatic(m.getModifiers())) {
						wrong = true;
					}
			//		if (Modifier.isSynchronized(m.getModifiers())) {
			//			wrong = true;
			//		}
					if (cu == null) {
						ICompilationUnit unit = (ICompilationUnit) m.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
						if ( unit == null ) {}
						ASTParser parser = ASTParser.newParser( AST.JLS8 );
						parser.setKind( ASTParser.K_COMPILATION_UNIT );
						parser.setSource( unit );
						parser.setResolveBindings( true );
						cu = (CompilationUnit) parser.createAST( null );
					}
					MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode( m.getKey() );
					IJavaElement compare = getIJavaElement(decl);
					if (compare!=null) {
						if (compare.isReadOnly()) {
							wrong=true;
						}
						if (compare instanceof IMember && ((IMember)compare).isBinary()) {
							wrong=true;
						}
					}
					int barrier = 0;
					if (m.getParameterTypes().length == target.getParameterTypes().length) {
						int index = 0;
						for (ITypeBinding p : m.getParameterTypes()) {
							if (p.getQualifiedName().compareTo(target.getParameterType(index)) == 0) {
								barrier++;
							}
							index++;
						}
						if (barrier < m.getParameterTypes().length) {
							boolean secondtry = polymorph(m.getParameterTypes(), paraset);
							if (secondtry == true) {
								next = true;
							} 
							else {
								if (backupsend.contains(typeBinding)) {} 
								else {
									backupsend.add(typeBinding);
								}
							}
						}
					}
					if (barrier == m.getParameterTypes().length) {
						next = true;
					}
					if (next == true && wrong == false) {
						polymethodset.add(typeBinding.getQualifiedName());
						if(!methodset.contains(typeBinding.getQualifiedName())) {
							methodset.add(typeBinding.getQualifiedName());
				//			methodset2.add(typeBinding);
							exclusive.add(typeBinding);
						}
						RMethod.runtimePolymorphicMethods.add(m.getKey());
						next = false;
						wrong = false;
					}
				}
			}
			next = false;
			wrong = false;
		}
		if(once == true && revisit == false) {
			lastCheck();
		}
		ITypeBinding[] interfaces = typeBinding.getInterfaces();
		for (ITypeBinding i : interfaces) {
			searchInterface(i);
		}
	}

	public static boolean findDuplicateMethodPM(ITypeBinding typeBinding) {
		IMethodBinding[] methods = typeBinding.getDeclaredMethods();
		boolean clean = false;
		boolean good = false;
		boolean reconsider = false;
		boolean correct = true;
		boolean maintain = true;
		if(!allRelations.contains(typeBinding.getQualifiedName())) {
			allRelations.add(typeBinding.getQualifiedName());
			searchAgain.add(typeBinding);
		}
		for (IMethodBinding m : methods) {
			if (m.getName().compareTo(target.getName()) == 0) {
				if(!RMethod.runtimePolymorphicMethods.contains(m.getKey())) {
					if (m.getParameterTypes().length == target.getParameterTypes().length) {
						if (m.isConstructor() == true) {
							correct = false;
						}
						if (Modifier.isPrivate(m.getModifiers())) {
							if(privacy == false && initial==false) {
								correct = false;
							}
						}
						else {
							maintain = false;
						}
						if (Modifier.isNative(m.getModifiers())) {
							correct = false;
						}
						if (Modifier.isStatic(m.getModifiers())) {
							correct = false;
						}
			//			if (Modifier.isSynchronized(m.getModifiers())) {
			//				correct = false;
			//			}
						if (cu == null) {
							ICompilationUnit unit = (ICompilationUnit) m.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
							if ( unit == null ) {}
							ASTParser parser = ASTParser.newParser( AST.JLS8 );
							parser.setKind( ASTParser.K_COMPILATION_UNIT );
							parser.setSource( unit );
							parser.setResolveBindings( true );
							cu = (CompilationUnit) parser.createAST( null );
						}
						MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode( m.getKey() );
						IJavaElement compare = getIJavaElement(decl);
						if (compare!=null) {
							if (compare.isReadOnly()) {
								correct=false;
							}
							if (compare instanceof IMember && ((IMember)compare).isBinary()) {
								correct=false;
							}
						}
						
						int index = 0, barrier = 0;
						for (ITypeBinding p : m.getParameterTypes()) {
							if (p.getQualifiedName().compareTo(target.getParameterType(index)) == 0) {
								barrier++;
							}
							index++;
						}
						if (barrier != m.getParameterTypes().length) {
							boolean lastshot = polymorph(m.getParameterTypes(), paraset);
							if (lastshot == true) {
								reconsider = true;
								good = true;
							} else {
								if (backupsend.contains(typeBinding)) {
								} else {
									backupsend.add(typeBinding);
								}
								good = false;
							}
						} 
						else {
							good = true;
						}
						if (good == true && correct == true) {
							if(!methodset.contains(typeBinding.getQualifiedName())) {
								methodset.add(typeBinding.getQualifiedName());
					//			methodset2.add(typeBinding);
								exclusive.add(typeBinding);
							}
						//	RMethod.runtimePolymorphicMethods.add(m.getKey());
							RMethod.runtimePolymorphicMethods.add(m.getKey());
							clean = true;
						}
					}
				}
			}
			good = false;
			correct = true;
		}
		if(privacy==true && reconsider == true && maintain == false) {
			privacy = false;
			collectAllduplicatemethodsInParents(typeBinding);
		}
		if(clean == true && revisit == false) {
			lastCheck();
		}
		if (clean != true) {
			return false;
		}
		return true;
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
						if(potentials.get(j).equals(paraset.get(i).getQualifiedName())){
							tally++;
						}
					}
					if (!(tally>tallystart)) {
						if (paraset.get(i).isInterface() || paraset.get(i).isClass()) {
							temp = paraset.get(i);
							potentials.clear();
							allRelations(temp, potentials);
							for (int j = 0; j<potentials.size(); j++) {
								if(potentials.get(j).equals(types[i].getQualifiedName()))
								{
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
					if (parent.getInterfaces() != null) {
						potentials.add(parent.getQualifiedName());
						allRelations(parent, potentials);
					}
					if (collect.getSuperclass().getQualifiedName().equals("java.lang.Object")) {
						return false;
					}
					parent = parent.getSuperclass();
				}
			}
		}
		
		return true;
	}
	
	
static void lastCheck() { // checks to see if the new children added by the search are related in some way
		// to previously checked instances
		ArrayList<ITypeBinding> backup2 = new ArrayList<ITypeBinding>();
		ArrayList<String> compare = new ArrayList<String>();
		ArrayList<String> compareolder = new ArrayList<String>();
		ArrayList<String> compareo = new ArrayList<String>();
		boolean works = false;
		if (SecondASTVisitorToFindDuplicateMethods.backup.size() > 0 && backup2.size() <= 0) {
			revisit = true;
			backup2 = SecondASTVisitorToFindDuplicateMethods.backup;
			if (backup2.size() > 0) {
				for (int i = 0; i < backup2.size(); i++) {
					compare.clear();
					allRelations(backup2.get(i), compare); //get all parent classes of backup to compare
					for(int j = 0; j < compare.size(); j++) {
						if(methodset.contains(compare.get(j))) {
							works = true;
							findDuplicateMethodPM(backup2.get(i));
						}
					}
					if(works == false) {
						compareo = compare;
						compareolder = compare;
						for (int j = 0; j<exclusive.size(); j++) {
							compare.clear();
							allRelations(exclusive.get(j), compare); //get all parent classes of other classes to compare
							if(compare.contains(backup2.get(i).getQualifiedName())) {
								works = true;
								allRelations.add(backup2.get(i).getQualifiedName());
								findDuplicateMethodPM(backup2.get(i));
							}
							if(compareolder.contains(backup2.get(i).getQualifiedName())) {
								works = true;
								allRelations.add(backup2.get(i).getQualifiedName());
								findDuplicateMethodPM(backup2.get(i));
							}
						}
					}
					works = false;
				}
			}
		}
	revisit = false;
	}

	static void lastlastCheck() { // checks to see if the new children added by the search are related in some way
		// to previously checked instances
		ArrayList<ITypeBinding> backup2 = new ArrayList<ITypeBinding>();
		ArrayList<String> compare = new ArrayList<String>();
		ArrayList<String> compare2 = new ArrayList<String>();
		boolean works = false;
		if (RunAction.TTW.size() > 0) {
			revisit = true;
			backup2 = RunAction.TTW;
			if (backup2.size() > 0) {
				for (int i = 0; i < backup2.size(); i++) {
					compare.clear();
					compare2.clear();
					allRelations(backup2.get(i), compare); //get all parent classes of backup to compare
					for(int j = 0; j < compare.size(); j++) {
						if(methodset.contains(compare.get(j))) {
							works = true;
							findDuplicateMethodPM(backup2.get(i));
						}
					}
					if(works == false) {
						for (int j = 0; j<exclusive.size(); j++) {
							compare.clear();
							allRelations(exclusive.get(j), compare); //get all parent classes of other classes to compare
							if(compare.contains(backup2.get(i).getQualifiedName())) {
								works = true;
								findDuplicateMethodPM(backup2.get(i));
							}
							for(int k = 0; k < compare2.size(); k++) {
								if(compare.contains(compare2.get(k)) && methodset.contains(compare2.get(k)))
								{
									works = true;
									findDuplicateMethodPM(backup2.get(i));
								}
								else{}
							}
						}
					}
					works = false;
				}
			}
		revisit = false;
		}
	}
	public static IJavaElement getIJavaElement(ASTNode node){

	     IJavaElement javaElement = null;

	     // Find IJavaElement corresponding to the ASTNode

	     if (node instanceof MethodDeclaration) {
	    //	 System.out.println(((MethodDeclaration) node).getName());
	           javaElement = ((MethodDeclaration) node).resolveBinding().getJavaElement();

	     }

	     return javaElement;

	}
}
