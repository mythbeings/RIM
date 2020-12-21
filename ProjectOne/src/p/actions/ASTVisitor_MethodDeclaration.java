package p.actions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ASTVisitor_MethodDeclaration extends ASTVisitor {
	public RMethod target = null;

	// int tester = 0, tested = 0;

	public ASTVisitor_MethodDeclaration(RMethod target) {
		this.target = target;
	}

	public boolean visit(TypeDeclaration node) {
		this.target.setClassName(node.resolveBinding().getQualifiedName());
		return true;
	}

	public boolean visit(MethodDeclaration node) {

		// System.out.println("node.getName().getIdentifier(): " +
		// node.getName().getIdentifier());
		// System.out.println("target.getName(): " + target.getName());

		// Collect ALL methods (name, parameter types) in the target program and store
		// them in RMethod.allMethods
		String[] parameterTypes = new String[node.parameters().size()];
		for (int i = 0; i < node.parameters().size(); i++) {
			SingleVariableDeclaration p = (SingleVariableDeclaration) node.parameters().get(i);
			parameterTypes[i] = p.getType().resolveBinding().getQualifiedName();
		}
		RMethod mth = new RMethod(node.getName().getIdentifier(), parameterTypes);
		RMethod.allMethods.add(mth);

		if (node.getName().getIdentifier().compareTo(target.getName()) == 0) {
			List<SingleVariableDeclaration> parameterList = node.parameters();
			int i = 0;
			if (target.getParameterTypes().length == parameterList.size()) {
				for (String parameterType : target.getParameterTypes()) {
					SingleVariableDeclaration p = (SingleVariableDeclaration) parameterList.get(i);

					// System.out.println("p.getType().resolveBinding().getQualifiedName(): " +
					// p.getType().resolveBinding().getQualifiedName());
					// System.out.println("parameterType: " + parameterType);

					if (p.getType().resolveBinding().getQualifiedName().compareTo(parameterType) != 0)
						break;
					i++;
				}

				if (i == target.getParameterTypes().length) {
					/*
					 * //To reach HERE, you should have already found a method declaration with the
					 * same name and parameters of the target. tested += target.namecounter(node);
					 * //System.out.println("tested: " + tested);
					 * //System.out.println("tester + \"|\" + tested: " + tester + "|" + tested); if
					 * (tested == 1) { tester++; tested = 0; } if (tester > 1) {
					 * //System.out.println("There are multiple methods with this name!"); return
					 * false; }
					 * 
					 * //System.out.println("We found " + tester +
					 * " duplicate methods in this program.");
					 */
					target.setNative(Modifier.isNative(node.getModifiers()));
					target.setStatic(Modifier.isStatic(node.getModifiers()));

					//// System.out.println("node.isConstructor(): " + node.isConstructor());
					target.setConstructor(node.isConstructor());

				} else if (i < target.getParameterTypes().length) {
					// System.out.println("Error: Method " + target.getName() +" doesn't satisfy the
					// criteria.");
					return false;
				}
			}
		}
		return true;
	}
}
