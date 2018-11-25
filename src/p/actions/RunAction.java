package p.actions;

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
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
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
		/*
		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
		IProject[] iProjectList = iWorkspaceRoot.getProjects();
		for (IProject iProject : iProjectList) {
			IJavaProject iJavaProject = JavaCore.create(iProject);

			try {
				IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}

					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
						ICompilationUnit workingCopy = iCompilationUnit.getWorkingCopy(null);
						ASTParser astParser = ASTParser.newParser(AST.JLS3);
						astParser.setResolveBindings(true);
						//astParser.setSource(iCompilationUnit);
						astParser.setSource(workingCopy);
						CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
						ASTVisitorEx astVisitorEx = new ASTVisitorEx(workingCopy.getSource());
						compilationUnit.accept(astVisitorEx);						
						//String source = workingCopy.getBuffer().getContents();
						Document document = new Document(astVisitorEx.source);
						compilationUnit.recordModifications();
						TextEdit edits = compilationUnit.rewrite(document, workingCopy.getJavaProject().getOptions(true));
						try {
							edits.apply(document);
						} catch (MalformedTreeException | BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String newSource = document.get();
						workingCopy.getBuffer().setContents(newSource);
						workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
						workingCopy.commitWorkingCopy(true, null);
						workingCopy.discardWorkingCopy();
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		*/
		
		// TO DO:  Rename_Instance_Method_Refactoring
		//
		//The script will look like this: 
		//
		//RPackage pkg = RProject.findPackage("P", "p");
		//RClass cls = pkg.findClass("A");
		//RMethod mth = cls.findMethod("m", "java.lang.String", "int", "q.A");
		//mth.rename("n");
		//
		// For now, you may assume that we always rename method m(String, int, q.A) in class A of package p in Eclipse project P.
		//
		
		String[] parameterTypes = new String[3];
		parameterTypes[0] = "java.lang.String";
		parameterTypes[1] = "int";
		parameterTypes[2] = "q.B";
		RMethod target = new RMethod("m", parameterTypes);
		
		traverseASTFirst("P", "q", "D.java", new ASTVisitor_MethodDeclaration(target), target);
		traverseASTSecond("P", "q", "D.java", new ASTVisitor_MethodDeclaration(target), target);
		
		
		//traverseAST("P", "p", "A.java", new ASTVisitor_CheckNative(target));		
		//RMethod target2 = new RMethod("A1", parameterTypes);		
		//traverseAST("P", "p", "A.java", new ASTVisitor_CheckCon(target));	
		
		/*
		//System.out.println(target.isStatic());
		//System.out.println(target.isNative());
		//System.out.println(target.isConstructor());
		*/
		
		
		
		//traverseAllASTs(target);
		
		
		//target.rename("n");
		
		// 1. Check preconditions -- what are the preconditions?  Tackle one at a time.
		//      1.1. static method cannot be renamed by rename-instance-method-refactoring.		
		// 2. Make code changes -- rename all polymorphic methods
		// 3. Update workspace -- if a compilation unit has at least one code change, it should be updated. 
		//
	}
	
	public void traverseAllASTs(RMethod target) {
		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
		IProject[] iProjectList = iWorkspaceRoot.getProjects();
		for (IProject iProject : iProjectList) {
			IJavaProject iJavaProject = JavaCore.create(iProject);
			
			try {
				IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}
						
					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
					
						ICompilationUnit workingCopy = iCompilationUnit.getWorkingCopy(null);
						ASTParser astParser = ASTParser.newParser(AST.JLS3);
						astParser.setResolveBindings(true);
						//astParser.setSource(iCompilationUnit);
						astParser.setSource(workingCopy);
						CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);			
						
						//two visitors for checking duplicate methods
						
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void traverseASTFirst(String projectName, String packageName, String compilationUnitName, ASTVisitor_MethodDeclaration astVisitor, RMethod target) {
		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
		IProject[] iProjectList = iWorkspaceRoot.getProjects();
		for (IProject iProject : iProjectList) {
			IJavaProject iJavaProject = JavaCore.create(iProject);
			
			////System.out.println("iJavaProject.getElementName(): " + iJavaProject.getElementName());
			////System.out.println("projectName: " + projectName);
			
			
			if(iJavaProject.getElementName().compareTo(projectName) != 0)
				continue;
			
			
			try {
				IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}
					
					
					////System.out.println("iPackageFragment.getElementName(): " + iPackageFragment.getElementName());
					////System.out.println("packageName: " + packageName);
					astVisitor.target.setPackageName(packageName);
					
					
					if(iPackageFragment.getElementName().compareTo(packageName) != 0)
						continue;					
					
					
					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
						
						
						////System.out.println("iCompilationUnit.getElementName(): " + iCompilationUnit.getElementName());
						////System.out.println("typeName: " + compilationUnitName);
						
						
					if(iCompilationUnit.getElementName().compareTo(compilationUnitName) != 0)
							continue;						
						
						
						ICompilationUnit workingCopy = iCompilationUnit.getWorkingCopy(null);
						ASTParser astParser = ASTParser.newParser(AST.JLS3);
						astParser.setResolveBindings(true);
						//astParser.setSource(iCompilationUnit);
						astParser.setSource(workingCopy);
						CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);						
						compilationUnit.accept(astVisitor);
						
						compilationUnit.accept(new FirstASTVisitorToFindDuplicateMethods(target));
						//compilationUnit.accept(new SecondASTVisitorToFindDuplicateMethods(target));

					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void traverseASTSecond(String projectName, String packageName, String compilationUnitName, ASTVisitor_MethodDeclaration astVisitor, RMethod target) {
		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
		IProject[] iProjectList = iWorkspaceRoot.getProjects();
		for (IProject iProject : iProjectList) {
			IJavaProject iJavaProject = JavaCore.create(iProject);
			
			////System.out.println("iJavaProject.getElementName(): " + iJavaProject.getElementName());
			////System.out.println("projectName: " + projectName);
			
			/*
			if(iJavaProject.getElementName().compareTo(projectName) != 0)
				continue;
			*/
			
			try {
				IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}
					
					
					////System.out.println("iPackageFragment.getElementName(): " + iPackageFragment.getElementName());
					////System.out.println("packageName: " + packageName);
					astVisitor.target.setPackageName(packageName);
					
					/*
					if(iPackageFragment.getElementName().compareTo(packageName) != 0)
						continue;					
					*/
					
					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
						
						
						////System.out.println("iCompilationUnit.getElementName(): " + iCompilationUnit.getElementName());
						////System.out.println("typeName: " + compilationUnitName);
						
						/*
						if(iCompilationUnit.getElementName().compareTo(compilationUnitName) != 0)
							continue;						
						*/
						
						ICompilationUnit workingCopy = iCompilationUnit.getWorkingCopy(null);
						ASTParser astParser = ASTParser.newParser(AST.JLS3);
						astParser.setResolveBindings(true);
						//astParser.setSource(iCompilationUnit);
						astParser.setSource(workingCopy);
						CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);						
						compilationUnit.accept(astVisitor);
						
						//compilationUnit.accept(new FirstASTVisitorToFindDuplicateMethods(target));
						compilationUnit.accept(new SecondASTVisitorToFindDuplicateMethods(target));

					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
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