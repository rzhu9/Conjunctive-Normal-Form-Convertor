import java.util.ArrayList;

import processing.core.PApplet;
import processing.data.XML;

public class Ruihao_Zhu_Resolution extends DrawableTree
{
	public Ruihao_Zhu_Resolution(PApplet p, XML tree) 
	{ 
		super(p); 
		this.tree = tree; 
		dirtyTree = true;
	}
	
	public void eliminateBiconditions()
	{
		// TODO - Implement the first step in converting logic in currNode to CNF:
		// Replace all biconditions with truth preserving conjunctions of conditions.
		XML currNode = tree;
		recursiveEliminateBiconditions(currNode);
		dirtyTree = true;
	}	
	
	public void recursiveEliminateBiconditions(XML xml){
		if (xml.getName() == "logic"){
			xml = xml.getChild(0);
		}
		if (xml.getName().equals("bicondition")){
			XML left = xml.getChild(0);
			XML right = xml.getChild(1);
			xml.setName("and");
			xml.removeChild(left);;
			xml.removeChild(right);
			xml.addChild("condition");
			xml.addChild("condition");
			xml.getChild(0).addChild(left);
			xml.getChild(0).addChild(right);
			xml.getChild(1).addChild(right);
			xml.getChild(1).addChild(left);
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length;i++){
				xml = children[i];
				recursiveEliminateBiconditions(xml);
			}
		}
		else {
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length; i++){
				xml = children[i];
				recursiveEliminateBiconditions(xml);
			}
		}
	}
	
	public void eliminateConditions()
	{
		// TODO - Implement the second step in converting logic in tree to CNF:
		// Replace all conditions with truth preserving disjunctions.
		XML currNode = tree;
		recursiveEliminateConditions(currNode);
		dirtyTree = true;
	}
	
	public void recursiveEliminateConditions(XML xml){
		if (xml.getName() == "logic"){
			xml = xml.getChild(0);
		}
		if (xml.getName().equals("condition")){
			XML left = xml.getChild(0);
			XML right = xml.getChild(1);
			xml.setName("or");
			xml.removeChild(left);;
			xml.removeChild(right);
			xml.addChild("not");
			xml.addChild(right);
			xml.getChild(0).addChild(left);
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length;i++){
				xml = children[i];
				recursiveEliminateConditions(xml);
			}
		}
		else {
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length; i++){
				xml = children[i];
				recursiveEliminateConditions(xml);
			}
		}
	}
	
	public void moveNegationInwards()
	{
		// TODO - Implement the third step in converting logic in tree to CNF:
		// Move negations in a truth preserving way to apply only to literals.
		XML currNode = tree;
		recursiveMoveNegationInward(currNode);
		dirtyTree = true;
	}
	
	public void recursiveMoveNegationInward(XML xml){
		if (xml.getName() == "logic"){
			xml = xml.getChild(0);
		}
		if (xml.getName().equals("not")){
			if (xml.getChildren().length == 1 && xml.getChild(0).getName().equals("not")){
				XML next = xml.getChild(0).getChild(0);
				xml.getParent().removeChild(xml);
				xml.getParent().addChild(next);
				xml = xml.getParent();
			}
			if (xml.getChildren().length == 1 && xml.getChild(0).getName().equals("and")){
				XML left = xml.getChild(0).getChild(0);
				XML right = xml.getChild(0).getChild(1);
				if (left.getName().equals("not")){
					left = left.getChild(0);
				}
				if (right.getName().equals("not")){
					right = right.getChild(0);
				}
				boolean leftTest = isLiteralNegated(left);
				boolean rightTest = isLiteralNegated(right);
				xml.setName("or");
				xml.removeChild(xml.getChild(0));
				if (leftTest == true){
					xml.addChild(left);
				}
				else{
					xml.addChild("not");
					xml.getChild(0).addChild(left);
				}
				if (rightTest == true){
					xml.addChild(right);
				}
				else{
					xml.addChild("not");
					xml.getChild(1).addChild(right);
				}
			}
			if (xml.getChildren().length == 1 && xml.getChild(0).getName().equals("or")){
				XML left = xml.getChild(0).getChild(0);
				XML right = xml.getChild(0).getChild(1);
				if (left.getName().equals("not")){
					left = left.getChild(0);
				}
				if (right.getName().equals("not")){
					right = right.getChild(0);
				}
				boolean leftTest = isLiteralNegated(xml.getChild(0).getChild(0));
				boolean rightTest = isLiteralNegated(xml.getChild(0).getChild(1));
				xml.setName("and");
				xml.removeChild(xml.getChild(0));
				if (leftTest == true){
					xml.addChild(left);
				}
				else{
					xml.addChild("not");
					xml.getChild(0).addChild(left);
				}
				if (rightTest == true){
					xml.addChild(right);
				}
				else{
					xml.addChild("not");
					xml.getChild(1).addChild(right);
				}
			}
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length;i++){
				xml = children[i];
				recursiveMoveNegationInward(xml);
			}
		}
		else {
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length; i++){
				xml = children[i];
				recursiveMoveNegationInward(xml);
			}
		}
	}
	
	public void distributeOrsOverAnds()
	{
		// TODO - Implement the fourth step in converting logic in tree to CNF:
		// Move negations in a truth preserving way to apply only to literals.
		boolean stop = false;
		XML currNode = tree;
		while(stop == false){
			recursiveDistributeOrsOverAnds(currNode);
			XML tree2 = tree;
			recursiveDistributeOrsOverAnds(currNode);
			XML tree3 = tree;
			recursiveDistributeOrsOverAnds(currNode);
			XML tree4 = tree;
			if (tree2.equals(tree3)&& tree3.equals(tree4)){
				stop = true;
			}
		}
		dirtyTree = true;
	}
	
	public void recursiveDistributeOrsOverAnds(XML xml){
		if (xml.getName() == "logic"){
			xml = xml.getChild(0);
		}
		if (xml.getName().equals("or")){
			if (xml.getChild(0).getName().equals("and")){
					XML leftOne = xml.getChild(0).getChild(0);
					XML leftTwo = xml.getChild(0).getChild(1);
					XML right = xml.getChild(1);
					xml.setName("and");
					xml.removeChild(xml.getChild(0));
					xml.removeChild(right);
					xml.addChild("or");
					xml.addChild("or");
					xml.getChild(0).addChild(leftOne);
					xml.getChild(0).addChild(right);
					xml.getChild(1).addChild(leftTwo);
					xml.getChild(1).addChild(right);
			}
			if (xml.getChild(1).getName().equals("and")){
				XML rightOne = xml.getChild(1).getChild(0);
				XML rightTwo = xml.getChild(1).getChild(1);
				XML left = xml.getChild(0);
				xml.setName("and");
				xml.removeChild(left);
				xml.removeChild(xml.getChild(0));
				xml.addChild("or");
				xml.addChild("or");
				xml.getChild(0).addChild(left);
				xml.getChild(0).addChild(rightOne);
				xml.getChild(1).addChild(left);
				xml.getChild(1).addChild(rightTwo);
			}
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length;i++){
				xml = children[i];
				recursiveDistributeOrsOverAnds(xml);
			}
		}
		else {
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length; i++){
				xml = children[i];
				recursiveDistributeOrsOverAnds(xml);
			}
		}
	}
	
	public void collapse()
	{
		// TODO - Clean up logic in tree in preparation for Resolution:
		// 1) Convert nested binary ands and ors into n-ary operators so
		// there is a single and-node child of the root logic-node, all of
		// the children of this and-node are or-nodes, and all of the
		// children of these or-nodes are literals: either atomic or negated	
		// 2) Remove redundant literals from every clause, and then remove
		// redundant clauses from the tree.
		// 3) Also remove any clauses that are always true (tautologies)
		// from your tree to help speed up resolution.
		boolean stop = false;
		XML currNode = tree;
		//if only one literal is the whole set
		if ((!tree.getChild(0).getName().equals("and")) && (!tree.getChild(0).getName().equals("or"))){
			XML save = tree.getChild(0);
			tree.addChild("and");
			tree.removeChild(save);
			tree.getChild(0).addChild("or");
			tree.getChild(0).getChild(0).addChild(save);
		}
		//if only OR gate appear in the set
		if (tree.getChild(0).getName().equals("or")){
			XML save = tree.getChild(0);
			tree.addChild("and");
			tree.removeChild(save);
			tree.getChild(0).addChild(save);
			collapseHelper1(currNode.getChild(0).getChild(0));
		}
		else {
			collapseHelper1(currNode);
			currNode = tree;
			collapseHelper1plus(currNode.getChild(0));
		}
		currNode = tree;
		currNode = currNode.getChild(0);
		XML children[] = currNode.getChildren();
		for (int i=0; i<children.length; i++){
			collapseHelper2(children[i]);
		}
		collapseHelper3(currNode);
		currNode = tree;
		currNode = currNode.getChild(0);
		ArrayList<XML> children3 = new ArrayList<XML>();
		XML children2[]  = currNode.getChildren();
		int length = children2.length;
		for (int i =0; i< length; i++){
			XML child = children2[i];
			if (clauseIsTautology(child) == false){
				children3.add(child);
			}
		}
		for (int i =0; i<length; i++){
			currNode.removeChild(currNode.getChild(0));
		}
		for (int i =0; i<children3.size(); i++){
			currNode.addChild(children3.get(i));
		}
		dirtyTree = true;
	}	
	
	public void collapseHelper1(XML xml){
		if (xml.getName() == "logic"){
			xml = xml.getChild(0);
			collapseHelper1(xml);
		}
		if (xml.getName().equals("and") && xml.getChildren().length == 2){
			if ((xml.getChild(0).getName().equals("and")) && (!xml.getChild(1).getName().equals("and"))){
				XML children[] = xml.getChild(0).getChildren();
				xml.removeChild(xml.getChild(0));
				for (int i=0; i<children.length; i++){
					xml.addChild(children[i]);
				}
				XML children2[] = xml.getChildren();
				for (int i=0; i<children2.length; i++){
					if (!(children2[i].getName().equals("and") || children2[i].getName().equals("or"))){
						XML save = children2[i];
						XML add = new XML("or");
						add.addChild(save);
						xml.addChild(add);
						xml.removeChild(save);
						collapseHelper1(add);
					}
					else {
						collapseHelper1(children2[i]);
					}
				}
			}
			if ((!xml.getChild(0).getName().equals("and")) && (xml.getChild(1).getName().equals("and"))){
				XML children[] = xml.getChild(1).getChildren();
				xml.removeChild(xml.getChild(1));
				for (int i=0; i<children.length; i++){
					xml.addChild(children[i]);
				}
				XML children2[] = xml.getChildren();
				for (int i=0; i<children2.length; i++){
					if (!(children2[i].getName().equals("and") || children2[i].getName().equals("or"))){
						XML save = children2[i];
						XML add = new XML("or");
						add.addChild(save);
						xml.addChild(add);
						xml.removeChild(save);
						collapseHelper1(add);
					}
					else {
						collapseHelper1(children2[i]);
					}
				}
			}
			if ((xml.getChild(0).getName().equals("and")) && (xml.getChild(1).getName().equals("and"))){
				XML children1[] = xml.getChild(0).getChildren();
				XML children2[] = xml.getChild(1).getChildren();
				xml.removeChild(xml.getChild(0));
				xml.removeChild(xml.getChild(0));
				for (int i=0; i<children1.length; i++){
					xml.addChild(children1[i]);
				}
				for (int i=0; i<children2.length; i++){
					xml.addChild(children2[i]);
				}
				XML children[] = xml.getChildren();
				for (int i=0; i<children.length; i++){
					if (!(children[i].getName().equals("and") || children[i].getName().equals("or"))){
						XML save = children[i];
						XML add = new XML("or");
						add.addChild(save);
						xml.addChild(add);
						xml.removeChild(save);
						collapseHelper1(add);
					}
					else {
						collapseHelper1(children[i]);
					}
				}
			}
			if (!(xml.getChild(0).getName().equals("and")) && !(xml.getChild(1).getName().equals("and"))){
				XML children[] = xml.getChildren();
				for (int i=0; i<xml.getChildren().length; i++){
					if (!(children[i].getName().equals("and") || children[i].getName().equals("or"))){
						XML save = children[i];
						XML add = new XML("or");
						add.addChild(save);
						xml.addChild(add);
						xml.removeChild(save);
					}
					else {
						collapseHelper1(children[i]);
					}
				}
			}
		}
		if (xml.getName().equals("or") && xml.getChildren().length ==2){
			if (xml.getChild(0).getName().equals("or") && (!xml.getChild(1).getName().equals("or"))){
				XML children[] = xml.getChild(0).getChildren();
				xml.removeChild(xml.getChild(0));
				for (int i=0; i<children.length; i++){
					xml.addChild(children[i]);
				}
				XML children2[] = xml.getChildren();
				for (int i=0; i<children2.length;i++){
					collapseHelper1(children2[i]);
				}
			}
			if (!xml.getChild(0).getName().equals("or") && xml.getChild(1).getName().equals("or")){
				XML children[] = xml.getChild(1).getChildren();
				xml.removeChild(xml.getChild(1));
				for (int i=0; i<children.length; i++){
					xml.addChild(children[i]);
				}
				XML children2[] = xml.getChildren();
				for (int i=0; i<children2.length;i++){
					collapseHelper1(children2[i]);
				}
			}
			if (xml.getChild(0).getName().equals("or") && xml.getChild(1).getName().equals("or")){
				XML children1[] = xml.getChild(0).getChildren();
				XML children2[] = xml.getChild(1).getChildren();
				xml.removeChild(xml.getChild(0));
				xml.removeChild(xml.getChild(0));
				for (int i=0; i<children1.length; i++){
					xml.addChild(children1[i]);
				}
				for (int i=0; i<children2.length; i++){
					xml.addChild(children2[i]);
				}
				XML children[] = xml.getChildren();
				for (int i=0; i<children.length;i++){
					collapseHelper1(children[i]);
				}
			}
			if (!(xml.getChild(0).getName().equals("or")) && !(xml.getChild(1).getName().equals("or"))){}
		}
		if (xml.getName().equals("or") && xml.getChildren().length == 1){
			if (xml.getChild(0).getName().equals("or")){
				XML children[] = xml.getChild(0).getChildren();
				xml.removeChild(xml.getChild(0));
				for (int i=0; i<children.length; i++){
					xml.addChild(children[i]);
				}
				XML children2[] = xml.getChildren();
				for (int i=0; i<children2.length; i++){
					collapseHelper1(children2[i]);
				}
			}
		}
		if (xml.getName().equals("or") && xml.getChildren().length > 2){
			for (int i=0; i<xml.getChildren().length; i++){
				if (xml.getChildren()[i].getName().equals("or")){
					XML children[] = xml.getChild(i).getChildren();
					xml.removeChild(xml.getChild(i));
					for (int j=0; j<children.length; j++){
						xml.addChild(children[j]);
					}
					i--;
				}
			}
			XML children2[] = xml.getChildren();
			for (int j=0; j<children2.length; j++){
				collapseHelper1(children2[j]);
			}
		}
		else{}
	}
	public void collapseHelper1plus(XML xml){
		boolean stop = false;
		while (stop == false){
			XML children[] = xml.getChildren();
			for (int i=0; i<children.length; i++){
				XML child = children[i];
				if (child.getName().equals("and")){
					XML save[] = child.getChildren();
					xml.removeChild(child);
					for (int j=0; j<save.length; j++){
						xml.addChild(save[j]);
					}
				}
			}
			stop = true;
			XML children2[] = xml.getChildren();
			for (int i=0; i<children2.length; i++){
				if (children2[i].getName().equals("and")){
					stop = false;
				}
			}
		}
	}
	public void collapseHelper2(XML xml){
		XML replace = new XML("or");
		XML children[] = xml.getChildren();
		for (int i=0; i<children.length; i++){
			XML child = children[i];
			if (children[i].getName().equals("not") && clauseContainsLiteral(replace,children[i].getChild(0).getName(), true) == false){
				replace.addChild(child);
			}
			if (clauseContainsLiteral(replace,children[i].getName(), false) == false) {
				replace.addChild(child);
			}
		}
		xml.getParent().addChild(replace);
		xml.getParent().removeChild(xml);
	}	
	
	public void collapseHelper3(XML xml){
		XML replace = new XML("and");
		XML children[] = xml.getChildren();
		for (int i=0; i<children.length; i++){
			XML child = children[i];
			if (setContainsClause(replace, child) == false){
				replace.addChild(child);
			}
		}
		xml.getParent().addChild(replace);
		xml.getParent().removeChild(xml);
	}
	
	public boolean applyResolution()
	{
		// TODO - Implement resolution on the logic in tree.  New resolvents
		// should be added as children to the only and-node in tree.  This
		// method should return true when a conflict is found, otherwise it
		// should only return false after exploring all possible resolvents.
		// Note: you are welcome to leave out resolvents that are always
		// true (tautologies) to help speed up your search.
		ArrayList <XML> newResolution = new ArrayList<XML>();
		ArrayList<XML> list = new ArrayList<XML>();
		ArrayList<XML> list2 = new ArrayList<XML>();
		boolean stop = false;
		boolean add = true;
		XML currNode = tree;
		currNode = currNode.getChild(0);
		int counter = 0;
		int counter2 =0;
		int counter3 =0;
		for (int i=0; i<currNode.getChildren().length; i++){
			list.add(currNode.getChild(i));
		}//check
		while(stop == false){
			newResolution = applyResolutionHelper(list);
			//if no new stuff is generated, jump out of the loop
			if (newResolution.size() == 0){
				stop = true;
			}
			//if a conflict is generated
			if (newResolution.size() == 1 && newResolution.get(0).getName().equals("conflict")){
				stop = true;
				counter = 1;
			}
			//if a conflict is generated
			if (newResolution.size() > 1 && newResolution.get(newResolution.size()-1).getName().equals("conflict")){
				stop = true;
				for (int i=0; i<newResolution.size()-1; i++){
					list.add(newResolution.get(i));
				}
				counter = 1;
			}
			else {
				for (int i=0; i<newResolution.size(); i++){
					for (int j=0; j<list.size(); j++){
						if (equalTo(newResolution.get(i), list.get(j))){
							counter2++;
						}
					}
					if (counter2 ==0){
						list.add(newResolution.get(i));
						counter3++;
					}
					counter2 =0;
				}
				if (counter3==0){
					stop = true;
				}
				counter3 = 0;
			}
		}
		for (int i = 0; i<list.size(); i++){
			for (int j=0; j<list2.size();j ++){
				if (equalTo(list.get(i),list2.get(j))== true){
					add = false;
				}
			}
			if (add == true){
				list2.add(list.get(i));
			}
		}
		XML replace = new XML("and");
		for (int i=0; i<list2.size(); i++){
			replace.addChild(list2.get(i));
		}
		currNode.getParent().addChild(replace);
		currNode.getParent().removeChild(currNode);
		currNode = replace;
		dirtyTree = true;
		if (counter == 1){
			return true;
		}
		return false;
	}

	//for a list of given xml input, resolve on any two pairs of two input
	//if there is a result of merging, add it to the output, 
	public ArrayList<XML> applyResolutionHelper(ArrayList<XML> input){
		int counter = 0;
		ArrayList<XML> output = new ArrayList<XML>();
		for (int i=0; i<input.size(); i++){
			for (int j=i+1; j<input.size(); j++){
				//resolve on given pair
				XML xml = resolve(input.get(i), input.get(j));
				//if there is a resolution of this pair
				if (xml != null && xml.getChildren().length !=0){
					//check redundancy and add it to the output
					for (int k=0; k<input.size(); k++){
						if (equalTo(xml, input.get(i)) ==  true){
							counter++;
						}
					}
					if (counter == 0){
						output.add(xml);
					}
				}
				counter =0;
				//if it is conflict
				if (xml != null && xml.getChildren().length == 0){
					XML conflict = new XML("conflict");
					output.add(conflict);
					return output;
				}
				//if it is null, do nothing and go to next pair
				if (xml == null){}
			}
		}
		return output;
	}
	
	public XML resolve(XML clause1, XML clause2)
	{
		// TODO - Attempt to resolve these two clauses and return the resulting
		// resolvent.  You should remove any redundant literals from this 
		// resulting resolvent.  If there is a conflict, you will simply be
		// returning an XML node with zero children.  If the two clauses cannot
		// be resolved, then return null instead.
		boolean add = true;
		boolean add2 = true;
		int counter =0;
		XML children1[] = clause1.getChildren();
		XML children2[] = clause2.getChildren();
		XML resolve = new XML("or");
		ArrayList<XML> list = new ArrayList<XML>();//contains all element from both clauses
		ArrayList<XML> list2 = new ArrayList<XML>();//the element that is kept after merging 
		ArrayList<XML> list3 = new ArrayList<XML>();//the element remains after removing redundency 
		//two same clause can never be merged
		if (equalTo(clause1, clause2) ==  true){
			return null;
		}
		//if two clause is opposite for every element, they are conflict
		if (isConflict(clause1, clause2) == true){
			XML conflict = new 	XML("conflict");
			return conflict;
		}	
		else { 
			for (int i=0; i<children1.length; i++){
				list.add(children1[i]);
			}
			for (int i=0; i<children2.length; i++){
				list.add(children2[i]);
			}//check
			
			//remove redundency first
			for (int i=0; i<list.size();i++){
				for (int j =0; j<list3.size(); j++){
					if (list.get(i).getName().equals("not")){
						if (list3.get(j).getName().equals("not") && list.get(i).getChild(0).getName().equals(list3.get(j).getChild(0).getName())){
							add2 = false;
						}
					}
					if (!list.get(i).getName().equals("not")){
						if (!list3.get(j).getName().equals("not") && list.get(i).getName().equals(list3.get(j).getName())){
							add2 = false;
						}
					}
				}
				if (add2 == true){
					list3.add(list.get(i));
				}
				add2 = true;
			}//check
			//start emerging
			for (int i=0; i<list3.size(); i++){
				if (list3.get(i).getName().equals("not")){
					for (int j=0; j<list3.size(); j++){
						if (!(list3.get(j).getName().equals("not"))){
							if (list3.get(j).getName().equals(list3.get(i).getChild(0).getName())){
								add = false;
								counter++;
							}
						}
					}
					if (add == true){
						list2.add(list3.get(i));
					}
				}
				if (!(list3.get(i).getName().equals("not"))){
					for (int j=0; j<list3.size(); j++){
						if ((list3.get(j).getName().equals("not"))){
							if (list3.get(j).getChild(0).getName().equals(list3.get(i).getName())){
								add = false;
								counter++;
							}
						}
					}
					if (add == true){
						list2.add(list3.get(i));
					}
				}
				add = true;
			}
			if (counter ==0){
				return null;
			}
			for (int i=0; i<list2.size(); i++){
				resolve.addChild(list2.get(i));
			}
			return resolve;
		}
	}	
	
	public boolean isConflict(XML a, XML b){
		int counter1 = 0;//count "not"
		int counter2 = 0;//count "not"
		ArrayList<String> one = new ArrayList<String>();
		ArrayList<String> two = new ArrayList<String>();
		for (int i=0; i<a.getChildren().length; i++){
			if (a.getChildren()[i].getName().equals("not")){
				counter1++;
				one.add(a.getChildren()[i].getChild(0).getName());
			}
			else {
				one.add(a.getChildren()[i].getName());
			}
		}
		for (int i=0; i<b.getChildren().length; i++){
			if (b.getChildren()[i].getName().equals("not")){
				counter2++;
				two.add(b.getChildren()[i].getChild(0).getName());
			}
			else {
				two.add(b.getChildren()[i].getName());
			}
		}
		if ((counter1 == 0 && counter2 == two.size()) || (counter1 == one.size() && counter2 == 0)){
			if (one.size() != two.size()){
				return false;
			}
			else {
				for (int i=0; i<one.size(); i++){
					if (two.contains(one.get(0))){
						two.remove(one.get(0));
						one.remove(0);
					}
					else {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	// REQUIRED HELPERS: may be helpful to implement these before collapse(), applyResolution(), and resolve()
	// Some terminology reminders regarding the following methods:
	// atom: a single named proposition with no children independent of whether it is negated
	// literal: either an atom-node containing a name, or a not-node with that atom as a child
	// clause: an or-node, all the children of which are literals
	// set: an and-node, all the children of which are clauses (disjunctions)
		
	public boolean isLiteralNegated(XML literal) 
	{ 
		// TODO - Implement to return true when this literal is negated and false otherwise.
		if (literal.getName().equals("not")){
			return true;
		}
		return false; 
	}

	public String getAtomFromLiteral(XML literal) 
	{ 
		// TODO - Implement to return the name of the atom in this literal as a string.
		if (literal.getName().equals("not")){
			return literal.getChild(0).getName();
		}
		return literal.getName();
	}	
	
	public boolean clauseContainsLiteral(XML clause, String atom, boolean isNegated)
	{
		// TODO - Implement to return true when the provided clause contains a literal
		// with the atomic name and negation (isNegated).  Otherwise, return false.		
		if (isNegated == false){
			XML children[] = clause.getChildren();
			for (int i=0; i<children.length; i++){
				if (children[i].getName().equals(atom)){
					return true;
				}
			}
			return false;
		}
		else {
			XML children[] = clause.getChildren();
			for (int i=0; i<children.length; i++){
				if (children[i].getName().equals("not")){
					if (children[i].getChild(0).getName().equals(atom)){
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public boolean setContainsClause(XML set, XML clause)
	{
		// TODO - Implement to return true when the set contains a clause with the
		// same set of literals as the clause parameter.  Otherwise, return false.
		if (set.hasChildren() == false){
			return false;
		}
		XML children[] = set.getChildren();
		for (int i=0; i<children.length; i++){
			if (equalTo(children[i], clause) == true){
				return true;
			}
		}
		return false;
	}
	
	public boolean equalTo(XML a, XML b){
		XML childrenA[] = a.getChildren();
		XML childrenB[] = b.getChildren();
		ArrayList<String> A = new ArrayList<String>();
		ArrayList<String> B = new ArrayList<String>();
		for (int i=0; i<childrenA.length; i++){
			if (childrenA[i].getName().equals("not")){
				A.add("not"+childrenA[i].getChild(0).getName());
			}
			else {
				A.add(childrenA[i].getName());
			}
		}
		
		int size = A.size();
		for (int i=0; i<childrenB.length; i++){
			if (childrenB[i].getName().equals("not")){
				B.add("not"+childrenB[i].getChild(0).getName());
			}
			else {
				B.add(childrenB[i].getName());
			}
		}
		
		if (a.getChildren().length != b.getChildren().length){
			return false;
		}
		else {
			for (int i=0; i<size; i++){
				String check = A.get(0);
				if(B.contains(check) == true){
					B.remove(B.indexOf(check));
					A.remove(0);
				}
				else {
					return false;
				}
			}
			return true;
		}
	}
	
	public boolean clauseIsTautology(XML clause)
	{
		// TODO - Implement to return true when this clause contains a literal
		// along with the negated form of that same literal.  Otherwise, return false.
		ArrayList<String> one = new ArrayList<String>();
		ArrayList<String> two = new ArrayList<String>();
		XML children[] = clause.getChildren();
		for (int i=0; i<children.length; i++){
			if (!(children[i].getName().equals("not"))){
				one.add(children[i].getName());
			}
			else {
				two.add(children[i].getChild(0).getName());
			}
		}
		for (int i=0; i< one.size(); i++){
			if (two.contains(one.get(i))){
				return true;
			}
		}
		return false;
	}	
}
