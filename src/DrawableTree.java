import processing.core.*;
import processing.data.*;

//This class renders an XML tree visually, and provides
//a mouse interface to pan and zoom around large images.
public class DrawableTree 
{
	// These fields are accessible to classes that extend DrawableTree.
	protected PApplet p;		 // can be used to access processing functionality
	protected XML tree;			 // this is the tree that will be drawn
	protected boolean dirtyTree; // graphics are only updated when this is true
	
	// for testing purposes, you may capture screenshot or full tree graphic
	public void captureScreenshot(String filename) { if(p != null) p.save(filename); }
	public void captureFullGraphic(String filename) { if(g != null) g.save(filename); }

	// ************************************************************************
	// The rest of this implementation can be ignored by students. 
	// It is used to help you visualize your solution, and may be
	// changed for the purposes of testing and grading your code.
	// ************************************************************************
	
	private PGraphics g;
	private float x, y, w, h, zoom;
	private boolean keyReady;
	private static final int RECT_WIDTH = 96;
	private static final int RECT_HEIGHT = 32;

	public DrawableTree(PApplet p)
	{
		this.p = p;
		tree = null;
		dirtyTree = false;
		g = null;
	}
	
	public void draw()
	{
		if(dirtyTree) render(tree);
		p.background(255);
		if(g != null) p.image(g,x,y,w,h);		
		if(p.mousePressed)
		{
			if(p.mouseButton == PApplet.LEFT)
				pan(p.mouseX-p.pmouseX,p.mouseY-p.pmouseY);
			if(p.mouseButton == PApplet.RIGHT)
				zoom(p.pmouseY-p.mouseY);
		}
		if(p.keyPressed)
		{
			if(p.key == '1' && keyReady)
				captureFullGraphic("temp.png");
			if(p.key == '2' && keyReady)
				captureScreenshot("temp.png");
			if(p.key == '0' && keyReady)
				resetView();
			keyReady = false;
		}
		else
			keyReady = true;
	}
	
	private void render(XML xml)
	{
		int depth = getDepth(xml);
		int[] widths = new int[depth];
		int width = getWidths(xml,widths);
		
		g = p.createGraphics((int)((width+1)*RECT_WIDTH*1.5), (int)((depth+1)*RECT_HEIGHT*1.5));
		g.beginDraw();
			g.background(128);
			renderTree(xml, widths);
		g.endDraw();
		
		resetView();
		dirtyTree = false;
	}
	
	private int getDepth(XML xml)
	{
		int maxDepth = 0;		
		XML[] children = xml.getChildren();
		for(int i=0;i<children.length; i++)
		{
			int depth = getDepth(children[i]);
			if(depth > maxDepth) maxDepth = depth;
		}
		return maxDepth + 1;
	}

	private int getWidths(XML xml, int[] widths)
	{
		return getWidthsHelper(xml,widths,0,0);
	}
	
	private int getWidthsHelper(XML xml, int[] widths, int depth, int maxWidth)
	{
		widths[depth]++;
		if(widths[depth] > maxWidth) maxWidth = widths[depth];
		XML[] children = xml.getChildren();
		for(int i=0;i<children.length; i++)
		{
			maxWidth = getWidthsHelper(children[i],widths,depth+1,maxWidth);
		}
		return maxWidth;
	}

	private void renderTree(XML xml, int[] widths)
	{
		g.rectMode(PApplet.CENTER);
		g.textAlign(PApplet.CENTER,PApplet.CENTER);
		renderTreeHelper(xml, widths, 0, new int[widths.length]);
	}
	
	private void renderTreeHelper(XML xml, int[] widths, int depth, int[] ltor)
	{
		float x = (g.width / widths[depth]) * (0.5f + ltor[depth]++);
		float y = (g.height / widths.length) * (0.5f + depth);
		xml.setFloat("x", x);
		xml.setFloat("y", y);
		if(xml.getParent() != null)
			g.line(xml.getParent().getFloat("x"), xml.getParent().getFloat("y")+RECT_HEIGHT/2, x, y-RECT_HEIGHT/2);
		if(xml.hasAttribute("altColor"))
		{
			String hexColor = xml.getString("altColor");
			g.fill(getColorComponent(hexColor,0), getColorComponent(hexColor,1), getColorComponent(hexColor,2));
		}
		g.rect(x, y, RECT_WIDTH, RECT_HEIGHT, 4);
		g.fill(0); 
		if(xml.hasAttribute("altName"))
			g.text(xml.getString("altName"),x, y, RECT_WIDTH, RECT_HEIGHT);
		else
			g.text(xml.getName(),x, y, RECT_WIDTH, RECT_HEIGHT);
		g.fill(255);
		
		XML[] children = xml.getChildren();
		for(int i=0;i<children.length; i++)
			renderTreeHelper(children[i],widths,depth+1,ltor);
	}
	
	private int getColorComponent(String hex, int component)
	{
		int componentValue = 0;
		hex = hex.trim();
		if(hex.startsWith("#")) hex = hex.substring(1);
		hex = hex.substring(component*2, component*2+2);
		componentValue = Integer.parseInt(hex,16);				
		return componentValue;
	}
	
	private void pan(int dx, int dy)
	{
		x += dx;
		y += dy;
	}
	
	private void zoom(int dy)
	{
		zoom += dy;
		if(zoom > 8) 
		{
			x += w/2;
			y += h/2;
				w *= 1.25f;
				h *= 1.25f;
			x -= w/2;
			y -= h/2;
			zoom = 0;
		}
		if(zoom < -8)
		{
			x += w/2;
			y += h/2;
				w /= 1.25f;
				h /= 1.25f;
			x -= w/2;
			y -= h/2;
			zoom = 0;
		}
	}
	
	private void resetView()
	{
		x = (p.width-g.width)/2;
		y = (p.height-g.height)/2;
		w = g.width;
		h = g.height;
		zoom = 0;
		keyReady = true;
	}
	
	// list of methods that will be implemented/overridden in your DecisionTree
	// *** DO NOT IMPLEMENT ANY OF THESE METHODS HERE ***
	public void eliminateBiconditions() {}
	public void eliminateConditions() {}
	public void moveNegationInwards() {}
	public void distributeOrsOverAnds() {}
	public void collapse() {}
	public boolean applyResolution() { return false; }
	public void resolve() {}
}
