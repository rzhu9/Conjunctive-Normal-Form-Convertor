
import java.util.Scanner;

import processing.core.PApplet;
import processing.data.XML;

public class Main extends PApplet
{
	public static XML inputXML;
	public DrawableTree dtree;
	public boolean keyWasDown;
	public String status;
	
	public void settings() { size(800,600); }
	
	public void setup()
	{
		dtree = new Ruihao_Zhu_Resolution(this,inputXML);
		keyWasDown = true;
		status = "Logic Loaded";
	}

	public void draw()
	{
		background(255);
		// use keyboard input to trigger CNF and Resolution
		if(keyPressed)
		{
			if(!keyWasDown)
			{
				switch(key)
				{
				case '1': 
					status = "Biconditions Eliminated";
					dtree.eliminateBiconditions(); 
					break;
				case '2': 
					status = "Conditions Eliminated";
					dtree.eliminateConditions(); 
					break;
				case '3':
					status = "Moved Negations Inwards";
					dtree.moveNegationInwards(); 
					break;
				case '4': 
					status = "Distributed Ors Over Ands";
					dtree.distributeOrsOverAnds(); 
					break;
				case '5':
					status = "Collapsed Tree";
					dtree.collapse(); 
					break;
				case '6': 
					status = "Resolution Conflict: " +
					dtree.applyResolution(); 
					break;					
				}
				
			}
			keyWasDown = true;
		}
		else
			keyWasDown = false;
		// draw tree
		dtree.draw();
		// draw status
		fill(255,0,0);
		text(status,32,32);
	}
	
	public static void main(String[] args) 
	{
		System.out.print("Enter a statement of propositional logic:");
		Scanner in = new Scanner(System.in);		
		String logic = in.nextLine();
		inputXML = LogicParser.toXML(logic);
		in.close();
		PApplet.main("Main");
	}
}
