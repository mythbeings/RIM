package p.actions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ASTVisitorEx extends ASTVisitor {

	public static ArrayList<RMethod> locations = new ArrayList<RMethod>();
	public static ArrayList<ITypeBinding> Baseparameters = FirstASTVisitorToFindDuplicateMethods.paraset;
//	public static ArrayList<ITypeBinding> Baseparameters = new ArrayList<ITypeBinding>();
	public static IMethodBinding baseline = null;
	public String source = "";
	public boolean doublecheck = true;
	public boolean first = true;
	public static ArrayList<MethodDeclaration> potentialcopies = new ArrayList<MethodDeclaration>();
	public static Set<Object> truecopies = new HashSet<Object>();
	public static Set<Object> wheretocheck = FirstASTVisitorToFindDuplicateMethods.methodset;
	public static int extraLength = 0;
	public static boolean additionalBoost = false;
	public static boolean additionalBoost2 = false;
	public static String classname = "";
	public static String oclassname = "";
	public static MethodDeclaration method = null;
	int growth = 0;
	static int tally = 0;
	ArrayList<String> intree = new ArrayList<String>();
	ArrayList<String> drasticmeasures = new ArrayList<String>();
	public static boolean pointless = false;

	// here's the important two -- change repName for the new name; repLength then
	// ensures that the correct movement is performed
	public static String repName = "mmm";
	public static int repLength = repName.length();

	// polymorphism group here

	public static ArrayList<String> poly = new ArrayList<String>();

	public ASTVisitorEx(String source) {
		this.source = source;
	}

	public boolean visit(TypeDeclaration type) {
		classname = type.resolveBinding().getQualifiedName();

		if (oclassname.toString() != classname.toString()) {
			extraLength = 0;
			growth = 0;
			oclassname = classname;
			method = null;
			first = true;
			if(type.toString().contains(repName)) {
				intree.clear();
				allRelations(type.resolveBinding(), intree);
			}
			doublecheck = false;
		}

		if (wheretocheck.contains(classname)) { //////////////////////////////
			doublecheck = true;
		}

		return true;
	}

	public boolean visit(MethodDeclaration node) {
		if (node.toString().contains(".txt")) {}
		else if(RunAction.rename.toLowerCase().equals(repName.toLowerCase())) {
			pointless = true;
		}
		else {
			if (!node.getName().toString().toLowerCase().equals(repName.toLowerCase())) {
				int start = node.getName().getStartPosition();
				String newSource = ""; // newSource is initialized as a blank string
				if (RMethod.runtimePolymorphicMethods.contains(node.resolveBinding().getKey())) {
					if (baseline == null) {
						baseline = node.resolveBinding();
					}
					if (first == true) {
						doublecheck = true;
						locations.add(new RMethod(node.getStartPosition(), node.getLength()));
						newSource += source.substring(0, node.getName().getStartPosition());
						newSource += repName;
						if (node.getName().getLength() > repLength) {
							growth -= (node.getName().getLength() - repLength);
						}
						if (node.getName().getLength() < repLength) {
							growth += (repLength - node.getName().getLength());
						}
						newSource += source.substring(node.getName().getStartPosition() + node.getName().getLength());
						additionalBoost = true;
						source = newSource;
						extraLength = growth;
						first = false;
					} 
					else {
						locations.add(new RMethod(node.getStartPosition(), node.getLength()));
						start += extraLength;
						newSource += source.substring(0, start);
						newSource += repName;
						if (node.getName().getLength() > repLength) {
							growth -= (node.getName().getLength() - repLength);
						}
						if (node.getName().getLength() < repLength) {
							growth += (repLength - node.getName().getLength());
						}
						newSource += source.substring(start + node.getName().getLength());
						extraLength = growth;
						source = newSource;
					}
				}
			}
			else {
				method = node;
				if(FirstASTVisitorToFindDuplicateMethods.allRelations.contains(classname)) {
					if(node.resolveBinding().getParameterTypes().length == RunAction.s) {
						boolean copy = polymorph(node.resolveBinding().getParameterTypes(), Baseparameters);
						if (copy == true) {
							boolean no = false;
							if (node.isConstructor() == true) {
								no = true;
							}
							if (Modifier.isPrivate(node.getModifiers())) {
								if(FirstASTVisitorToFindDuplicateMethods.privacy==false) {
									no = true;
								}
							}
							if (Modifier.isNative(node.getModifiers())) {
								no = true;
							}
							if (Modifier.isStatic(node.getModifiers())) {
								no = true;
							}
			//				if (Modifier.isSynchronized(node.getModifiers())) {
			//					no = true;
			//				}
							IJavaElement compare = getIJavaElement(node);
							if (compare.isReadOnly()) {
								no = true;
							}
							if (compare instanceof IMember && ((IMember)compare).isBinary()) {
								no = true;
							}
							if(no == false) {
								for(int i = 0; i<intree.size(); i++) {
									if(FirstASTVisitorToFindDuplicateMethods.methodset.contains(intree.get(i)) && !intree.get(i).contains("java.lang.Object")) {
										if(!RunAction.copies.contains(classname)) {
											RunAction.copies.add(classname);
										}
										truecopies.add(node.resolveBinding().getName());
									}
									else {
										if(drasticmeasures.size()<1) {
											for(int j = 0; j<FirstASTVisitorToFindDuplicateMethods.methodset.size(); j++) {
												allRelations(FirstASTVisitorToFindDuplicateMethods.exclusive.get(j), drasticmeasures);
											}										
										}
										if(drasticmeasures.contains(intree.get(i)) && !intree.get(i).contains("java.lang.Object")) {
											if(!RunAction.copies.contains(classname)) {
												RunAction.copies.add(classname);
											}
											truecopies.add(node.resolveBinding().getName());
										}
									}
								}
						}
							no=false;
						}
					}
					}
					else {}
				}
			}
		return true;
	}


	public boolean visit(MethodInvocation node) {
		String newSource = ""; // newSource is initialized as a blank string
		int start = node.getName().getStartPosition();
		if (node.toString().contains(".txt")) {}

		else {
			if (RMethod.runtimePolymorphicMethods.contains(node.resolveMethodBinding().getMethodDeclaration().getKey())) {
				locations.add(new RMethod(node.getStartPosition(), node.getLength()));
				start += extraLength;
				newSource += source.substring(0, start);
				newSource += repName;
				if (first == true) {
					first = false;
				}
				if (node.getName().getLength() > repLength) {
					growth -= (node.getName().getLength() - repLength);
				}
				if (node.getName().getLength() < repLength) {
					growth += (repLength - node.getName().getLength());
				}
				newSource += source.substring(start + node.getName().getLength());
				extraLength = growth;
				source = newSource;
			}

		}
		return true;
	}
	
	public boolean visit(SuperMethodInvocation node) {
		String newSource = ""; // newSource is initialized as a blank string
		int start = node.getName().getStartPosition();
		if (node.toString().contains(".txt")) {}

		else {
			if (RMethod.runtimePolymorphicMethods.contains(node.resolveMethodBinding().getMethodDeclaration().getKey())) {
				locations.add(new RMethod(node.getStartPosition(), node.getLength()));
				start += extraLength;
				newSource += source.substring(0, start);
				newSource += repName;
				if (first == true) {
					first = false;
				}
				if (node.getName().getLength() > repLength) {
					growth -= (node.getName().getLength() - repLength);
				}
				if (node.getName().getLength() < repLength) {
					growth += (repLength - node.getName().getLength());
				}
				newSource += source.substring(start + node.getName().getLength());
				extraLength = growth;
				source = newSource;
			}

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
		if(types.length>0) {
			for (int i = 0; i < paraset.size(); i++) {
				tallystart = tally;
				potentials.clear();
	
				if (types[i].getQualifiedName().compareTo(paraset.get(i).getQualifiedName()) == 0) {
					tally++;
				}
	
				else if (poly.contains(types[i].getQualifiedName()) && poly.contains(paraset.get(i).getQualifiedName())) {
					tally++;
				}
	
				else if (types[i].getQualifiedName().contains("boolean")
						&& paraset.get(i).getQualifiedName().contains("boolean")) {
					tally++;
				}
				else if (types[i].isSubTypeCompatible(paraset.get(i)) || paraset.get(i).isSubTypeCompatible(types[i])) {
					tally++;
					System.out.println(tally);
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
					else {
				}
			}
				tallystart = tally;
	
		}
	
			if (tally == paraset.size()) {
				return true;
			}
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
	public static IJavaElement getIJavaElement(ASTNode node){

	     IJavaElement javaElement = null;

	     // Find IJavaElement corresponding to the ASTNode

	     if (node instanceof MethodDeclaration) {

	           javaElement = ((MethodDeclaration) node).resolveBinding().getJavaElement();

	     }

	     return javaElement;

	}
	
}	
