package p.actions;

	import java.lang.reflect.Type;
	import java.util.Set;


	import org.eclipse.jdt.core.dom.CompilationUnit;
	import org.eclipse.jdt.core.dom.MethodDeclaration;
	import org.eclipse.jdt.core.dom.TypeDeclaration;
	import org.w3c.dom.Node;


	public class ASTVisitorPhases {
		public void Main() {
		//	ASTVisitorPhaseOne visitorPhaseOne = new ASTVisitorPhaseOne();
		//	CompilationUnit.accept(visitorPhaseOne); //Didn't know how to handle this, even with answer

		//	ASTVisitorPhaseTwo visitorPhaseTwo = new ASTVisitorPhaseTwo();
		//	CompilationUnit.accept(visitorPhaseTwo);


			}

			public class ASTVisitorPhaseOne {


			
			}

			public class ASTVisitorPhaseTwo {
		

			}

			

	/*		Original code from   
	 * 
	 * 		Set methodset;

			 Void visit(TypeDeclaration type) {

				//check if type is the class where the target method resides
				//if true ...
				Node tocheck = type;
				collectAllduplicatemethodsInParents(tocheck, methodset);
			 }

			void collectAllduplicatemethodsInParents (Node currentclass, Set methodset) {

			Interfacesearch(currentclass, methodset); 
			Superclasssearch(currentclass, methodset);
			}

			Void Superclasssearch(Node origin, Set methodSet) {

				if (origin.hasSuperClass = true) {
					RMethod m = findDuplicateMethod(origin);
					if (m != null) {
					methodSet.add(m);}
					collectAllduplicatemethods(super class)
			 	}
			}

			Void Interfacesearch(Node origin, Set methodSet) {

			for i in origin.getInterfaces()
			RMethod m = findDuplicateMethod(interface);
			if (m != null) methodSet.add(m);
			Interfacesearch(interface, methodSet);	
				

			}
	*/
	}

