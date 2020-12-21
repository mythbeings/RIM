package p.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class RunAction implements IWorkbenchWindowActionDelegate {

	public static ArrayList<String> copies = new ArrayList<String>();
	public static Map<ITypeBinding, ArrayList<ITypeBinding>> compatibleTypes = new HashMap<ITypeBinding, ArrayList<ITypeBinding>>();
	public static Map<String, ArrayList<String>> quickSkim = new HashMap<String, ArrayList<String>>();
	public static String projName = "P";
	public static String packName = "q";
	public static String start = "B.java";
	public static String rename = "m";
	public static Boolean valid = false;
	public static boolean end = false;
	public static int s = 0;
	public static ArrayList<ITypeBinding> TTW = new ArrayList<ITypeBinding>();
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public RunAction() {
	}

	
	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@Override
	public void run(IAction action) {
		long t1 = System.currentTimeMillis();
		List<ICompilationUnit> iCUs = new ArrayList<ICompilationUnit>();

		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
		IProject[] iProjectList = iWorkspaceRoot.getProjects();
		IProject iProject = iProjectList[0]; // JW: we're interested in the first project only.
		IJavaProject iJavaProject = JavaCore.create(iProject);

		try {
			IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
			for (IPackageFragment iPackageFragment : iPackageFragmentList) {
				if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
					continue;
				}

				// JW: this part is changed.
				// Here we collect all iCompilationUnits first.
				ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
				for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
					iCUs.add(iCompilationUnit);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		// JW: this part is newly added (or revised).
		// We now create all ASTs (not a single one at a time) altogether.
		// Just copy the following code.
		ICompilationUnit[] compUnits = iCUs.toArray(new ICompilationUnit[0]);
		final Map<ICompilationUnit, ASTNode> parsedCompilationUnits = new HashMap<ICompilationUnit, ASTNode>();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setEnvironment(null, null, null, true);
		parser.setProject(iJavaProject);
		parser.createASTs(compUnits, new String[0], new ASTRequestor() {
			@Override
			public final void acceptAST(final ICompilationUnit unit, final CompilationUnit node) {
				parsedCompilationUnits.put(unit, node);
			}

			@Override
			public final void acceptBinding(final String key, final IBinding binding) {
				// Do nothing
			}
		}, null);

		// JW: below is what you would be interested.
		// Each compilation unit is now retrieved from a hashmap above and then accepts
		// an ASTVisitor.
		
		
	 Iterator<ICompilationUnit> keySetIterator = parsedCompilationUnits.keySet().iterator();
	        while (keySetIterator.hasNext()) {
	            ICompilationUnit iCU = keySetIterator.next();
	            CompilationUnit cu = (CompilationUnit) parsedCompilationUnits.get(iCU);
	            cu.accept(new ASTVisitor() {
	                public boolean visit(TypeDeclaration node) {

	                       // (1) add parents (upward)

	                ArrayList<ITypeBinding> superTypes = null;
	                if(!compatibleTypes.containsKey(node.resolveBinding())) {
	                	superTypes = new ArrayList<ITypeBinding>();
	                }
	                else {
	                		superTypes = (ArrayList<ITypeBinding>) compatibleTypes.get(node.resolveBinding());
	                		
	                	}
	      
	                if(node.resolveBinding().getSuperclass() != null) {
                	if (node.resolveBinding().getSuperclass().getQualifiedName().equals("java.lang.Object")) {}
                	else {
                		superTypes.add(node.resolveBinding().getSuperclass());  // put the super class's ITypeBinding into superTypes.
                	}
	                }
	                ITypeBinding[] parentInterfaces = node.resolveBinding().getInterfaces();
	                if (parentInterfaces.length>0) {
		                for(ITypeBinding b : parentInterfaces) {
		                	superTypes.add(b);   // put the super interfaces' ITypeBindings into superTypes.
		                }
	                }

	                compatibleTypes.put(node.resolveBinding(), superTypes);  
	            	
	                   // (2) add children (downward)

	                for(ITypeBinding b: superTypes) {
	                    if(!compatibleTypes.containsKey(b)) {
	                    	compatibleTypes.put(b, new ArrayList<ITypeBinding>()); 
	                    }
	                    else {
	                        ArrayList<ITypeBinding> temp = (ArrayList<ITypeBinding>) compatibleTypes.get(b);
	                        temp.add(node.resolveBinding());
	                    }
	                }
	                return true;
	                }                
	            });
	        }//*/
	  
	   //DECIDE PARAMETERS HERE     
	      String[] parameterTypes = new String[3]; parameterTypes[0] = "int"; parameterTypes[1] = "int"; parameterTypes[2] = "q.B";
	//        String[] parameterTypes = new String[3]; parameterTypes[0] = "int";  parameterTypes[1] = "int"; parameterTypes[2] = "java.lang.Object";
	//   String[] parameterTypes = new String[3]; parameterTypes[0] = "int"; parameterTypes[1] = "p.I1"; parameterTypes[2] = "double";
	//	String[] parameterTypes = new String[2]; parameterTypes[0] = "p.A"; parameterTypes[1] = "double";
	 //  	String[] parameterTypes = new String[2]; parameterTypes[0] = "java.lang.Object"; parameterTypes[1] = "int";
	    	
	 //      String[] parameterTypes = new String[2]; parameterTypes[0] = "java.lang.String"; parameterTypes[1] = "java.lang.String";
	//    String[] parameterTypes = new String[0]; 
	 // 	      String[] parameterTypes = new String[1]; parameterTypes[0] = "java.lang.Object";
	//       String[] parameterTypes = new String[1]; parameterTypes[0] = "ElevatorSystem.Elevator";
//	       String[] parameterTypes = new String[2]; parameterTypes[0] = "org.junit.runners.model.RunnerBuilder"; parameterTypes[1] = "java.lang.Class<?>[]";
	// 	      String[] parameterTypes = new String[1]; parameterTypes[0] = "java.lang.String[]";
	//  	      String[] parameterTypes = new String[1]; parameterTypes[0] = "p.Board";
	    s = parameterTypes.length;
		RMethod target = new RMethod(rename, parameterTypes); // change this to change what is detected
		
		
		traverseASTFirst(projName, packName, start, new ASTVisitor_MethodDeclaration(target), target, parsedCompilationUnits);
		if (valid == false) {
			System.out.println("Sorry, but no method within the program matches that criteria. Please correct the submission.");
		    return;
		}
		
		if(FirstASTVisitorToFindDuplicateMethods.privacy == false) {
			traverseASTSecond(projName, packName, start, new ASTVisitor_MethodDeclaration(target), target, parsedCompilationUnits);
			int baseline = 0;
			int i = 0;
			if(FirstASTVisitorToFindDuplicateMethods.methodset.size()!=TTW.size()) {
				while(baseline < FirstASTVisitorToFindDuplicateMethods.methodset.size()) {
					baseline = FirstASTVisitorToFindDuplicateMethods.methodset.size();
					for(i = 0; i<TTW.size(); i++) {
						if(!TTW.get(i).getQualifiedName().equals("java.lang.Object")) {
							if(FirstASTVisitorToFindDuplicateMethods.allRelations.contains(TTW.get(i).getQualifiedName())) {
								FirstASTVisitorToFindDuplicateMethods.lastlastCheck();
							}
						}
					}
				}
			}
		}


		if (copies.size() > 0) {
			copies.clear();
		}
		long t2 = System.currentTimeMillis();
		makeCodeChange(target, parsedCompilationUnits);
		
		long t3 = System.currentTimeMillis();

		
		System.out.println("precondition check time: " + (t2 - t1));

		System.out.println("code transformation time: " + (t3 - t2));

		System.out.println("entire execution time: " + (t3 - t1));
		
	}

	public void makeCodeChange(RMethod target, Map<ICompilationUnit, ASTNode> parsedCompilationUnits) {
		LinkedHashMap<String, String> filepath_to_sources = new LinkedHashMap<String, String>();
		

		Iterator<ICompilationUnit> keySetIterator = parsedCompilationUnits.keySet().iterator();
		while (keySetIterator.hasNext()) {
			ICompilationUnit iCompilationUnit = keySetIterator.next();
			try {
				IResource iResource = iCompilationUnit.getUnderlyingResource();
				IFile iFile = (IFile) iResource;
				String fullPath = iFile.getRawLocation().toString();
				ICompilationUnit workingCopy = iCompilationUnit.getWorkingCopy(null);
				ASTParser astParser = ASTParser.newParser(AST.JLS3);
				astParser.setResolveBindings(true);
				astParser.setSource(workingCopy);
				CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);

				//////////////////////////////////////////////////////
				ASTVisitorEx astVisitorEx = new ASTVisitorEx(workingCopy.getSource());

				compilationUnit.accept(astVisitorEx);
				filepath_to_sources.put(fullPath, astVisitorEx.source);
				
				////////////////////////////////////////////////////
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		
		if (copies.size() == 0) {
			if(ASTVisitorEx.pointless==false) {
				makeRealChanges(filepath_to_sources);	
				}
			else {
				System.out.println("The change is pointless -- the replacement name is equal to the starting name.");
			}
		} 
		else {
			System.out.println("Warning: a compatible method already has the new name -- " + ASTVisitorEx.repName
					+ " -- within the following locations:");
			for (int i = 0; i < copies.size(); i++) {
				System.out.println(copies.get(i));
			}
			System.out.println("Please either change this method's name or repName");
		}
		

	}

	public void makeRealChanges(LinkedHashMap<String, String> filepath_to_sources) {
		for (String key : filepath_to_sources.keySet()) {
			String source = filepath_to_sources.get(key);

		//add for debugging:
		//System.out.println(source);

			///*
			// * skip for debugging. 
			 PrintStream output; try { output = new PrintStream(new
			 File(key)); output.println(source); } catch (FileNotFoundException e) { //TODO Auto-generated catch block e.printStackTrace(); }
						}
			// */

		}
		
	}

	public void traverseASTFirst(String projectName, String packageName, String compilationUnitName,
			ASTVisitor_MethodDeclaration astVisitor, RMethod target,
			Map<ICompilationUnit, ASTNode> parsedCompilationUnits) {

		

		astVisitor.target.setPackageName(packageName);
		astVisitor.target.setProjectName(projectName);

		

		Iterator<ICompilationUnit> keySetIterator = parsedCompilationUnits.keySet().iterator();
		while (keySetIterator.hasNext()) {
			ICompilationUnit iCU = keySetIterator.next();
			if (iCU.getElementName().compareTo(compilationUnitName) == 0) {
				IPackageFragment parentPack = (IPackageFragment) iCU.getParent();
				if (parentPack.getElementName().compareTo(packageName) == 0) {
					CompilationUnit cu = (CompilationUnit) parsedCompilationUnits.get(iCU);
					cu.accept(astVisitor);
					cu.accept(new FirstASTVisitorToFindDuplicateMethods(target/*, iCU*/));
				}
			}
		}
		
	}

	public void traverseASTSecond(String projectName, String packageName, String compilationUnitName,
			ASTVisitor_MethodDeclaration astVisitor, RMethod target,
			Map<ICompilationUnit, ASTNode> parsedCompilationUnits) {

		
		astVisitor.target.setPackageName(packageName);
		astVisitor.target.setProjectName(projectName);

		

		Iterator<ICompilationUnit> keySetIterator = parsedCompilationUnits.keySet().iterator();
		while (keySetIterator.hasNext()) {
			ICompilationUnit iCU = keySetIterator.next();
			CompilationUnit compilationUnit = (CompilationUnit) parsedCompilationUnits.get(iCU);
			compilationUnit.accept(astVisitor);
			compilationUnit.accept(new SecondASTVisitorToFindDuplicateMethods(target));
			

		}

		
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of the
	 * 'real' action here if we want, but this can only happen after the delegate
	 * has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	@Override
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell for
	 * the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}