package editor;

import java.util.ArrayList;
import javafx.scene.text.Text;

public class TextData {

	private ArrayList pointers; 
	private Line structure;

	public Node cursorPrevious;
	public Node cursorNext;
 
	public TextData(){
		pointers = new ArrayList<Node>();
		structure = new Line();

		cursorPrevious = structure.sentinel();
		cursorNext = structure.sentinel();
	}
	public void insert(Text i){
		structure.insert(i);
	}
	public Text delete(){
		return structure.delete();
	}
	public int numLines(){
		return pointers.size();
	}
	public Node get(int i){
		return (Node) pointers.get(i);
	}
	public Line structure() {
		return structure;
	}
	public ArrayList pointers(){
		return pointers;
	}
	public void right(){
		structure.right();
	}
	public void left(){
		structure.left();
	}
	public void up(){
		structure.up();
	}
	public void down(){
		structure.down();
	}
	public boolean isEmpty(){
		return structure.isEmpty();
	}
	public void begin(){
		structure.begin();
	}
	public TextData copy(){
		if (!structure.isEmpty()){
			TextData copy = new TextData();
			Node current = structure.first();
			while (current != structure.sentinel()) {
				Text copied = new Text(0, 0, current.item.getText());
				copy.insert(copied);
				if (current == cursorPrevious){
					copy.cursorPrevious = copy.structure().sentinel().previous;
				}
				if (current == cursorNext){
					copy.cursorNext = copy.structure().sentinel().previous;
				}
				current = current.next;
			}
			return copy;
		}
		TextData copy = new TextData();
		return copy;
	}
public class  Node {
		public Text item;
      	public Node next;
      	public Node previous;
        
        public Node(Text i, Node p, Node n) {
           	this.item = i;
          	this.previous = p;
      		this.next = n;
      	}
      	public Text item(){
      		return item;
      	}
}

public class Line {
    	private Node sentinel;
    	private int size;

	    public Line() {
    	    size = 0;
        	sentinel = new Node(null, sentinel, sentinel);
	    }
 	    public void insert (Text item) {
 	    	if(size == 0){
 	    		sentinel.next = new Node(item,sentinel,sentinel);
 	    		sentinel.previous = sentinel.next;
 	    		cursorPrevious = sentinel.next;
 	    		cursorNext = sentinel; 
 	    		pointers.add(sentinel.next);
 	    		size++;
 	    	} else {
 	    		Node inserted = new Node(item, cursorPrevious,cursorNext);
 	    		cursorPrevious.next = inserted;
 	    		cursorNext.previous = inserted;
 	    		cursorPrevious = inserted;
 	    		size++;
 	    	} 
 	    }
 	    public Text delete() {
 	    	if (cursorPrevious == sentinel){
 	    		return null;
 	    	}
 	    	Text deleted = cursorPrevious.item;
 	    	cursorPrevious = cursorPrevious.previous;
 	    	cursorPrevious.next = cursorNext;
 	    	cursorNext.previous = cursorPrevious;
 	    	size--;
 	    	return deleted;
 	    }
 	    public void right(){
 	    	if (cursorNext == sentinel){
 	    		return;
 	    	} else {
 	    		cursorNext = cursorNext.next;
 	    		cursorPrevious = cursorPrevious.next;
 	    	}
 	    }
 	    public void left(){
 	    	if (cursorPrevious == sentinel){
 	    		return;
 	    	} else { 
 	    		cursorPrevious = cursorPrevious.previous;
 	    		cursorNext = cursorNext.previous;
 	    	}
 	    }
 	    public void up(){
 	    	if (size == 0){
 	    		return;
 	    	} else if (cursorPrevious.item.getY() == 0){
 	    		return;
 	    	} else {
 	    		Node current = cursorPrevious;
 	    		double curosorX = cursorPrevious.item.getX() + Math.round(cursorPrevious.item.getLayoutBounds().getWidth());
 	    		if (cursorPrevious.previous.item.getText().equals("/n")){
 	    			cursorPrevious = cursorPrevious.previous;
 	    			cursorNext = cursorPrevious;
 	    		}
 	    		while(!(current.item.getY() < cursorPrevious.item.getY())){
 	    			current = current.previous;
 	    		}
 	    		if (current.item.getX() < curosorX){
 	    			cursorPrevious = current;
 	    			cursorNext = current.next;
 	    			return;
 	    		}
 	    		while(current.item.getX() + current.item.getLayoutBounds().getWidth() > curosorX){
 	    			current = current.previous;
 	    		}
 	    		double left = curosorX - current.item.getX() + Math.round(current.item.getLayoutBounds().getWidth());
 	    		double right = current.next.item.getX() + Math.round(current.next.item.getLayoutBounds().getWidth() - curosorX);

 	    		if (left < right){
 	    			cursorPrevious = current;
 	    			cursorNext = current.next;
 	    		} else {
 	    			cursorPrevious = current.next;
 	    			cursorNext = current.next.next;
 	    		}
 	    	}
 	    }
 	    public void down(){
 	    	if (size == 0){
 	    		return;
 	    	} else if (pointers.size() * cursorPrevious.item.getLayoutBounds().getHeight() <= cursorPrevious.item.getY()){
 	    		return;
 	    	} else {
 	    		Node current = cursorPrevious;
 	    		double curosorX = cursorPrevious.item.getX() + Math.round(cursorPrevious.item.getLayoutBounds().getWidth());
 	    		if (cursorPrevious.next.item.getText().equals("/n")){
 	    			cursorPrevious = cursorPrevious.next;
 	    			cursorNext = cursorNext.next;
 	    			return;
 	    		}
 	    		while(!(current.item.getY() > cursorPrevious.item.getY())){
 	    			current = current.next;
 	    		}

 	    		while(current.item.getX() + current.item.getLayoutBounds().getWidth() < curosorX){
 	    			if (current.next.item.getY() > current.item.getY() || current.next == sentinel){
 	    				cursorPrevious = current;
 	    				cursorNext = current.next;
 	    				return;
 	    			}	
 	    			current = current.next;
 	    		}
 	    		double left = curosorX - current.item.getX() + Math.round(current.item.getLayoutBounds().getWidth());
 	    		double right = current.previous.item.getX() + Math.round(current.previous.item.getLayoutBounds().getWidth() - curosorX);

 	    		if (left < right){
 	    			cursorPrevious = current;
 	    			cursorNext = current.next;
 	    		} else {
 	    			cursorPrevious = current.previous;
 	    			cursorNext = current;
 	    		}
 	    	}
 	    }
 	    public void click(double xPos, double yPos, double barValue, int window_width){
 	    	if (size == 0){
 	    		return;
 	    	}
 	    	if (xPos >= window_width-20){
 	    		return;
 	    	}
 	    	if (cursorPrevious == sentinel){
 	    		cursorPrevious = cursorNext;
 	    		cursorNext = cursorNext.next;
 	    	}
 	    	if (yPos > (pointers.size() * cursorPrevious.item.getLayoutBounds().getHeight())){
 	    		cursorPrevious = sentinel.previous;
 	    		cursorNext = sentinel;
 	    		return;
 	    	}
 	    	double height = Math.round(cursorPrevious.item.getLayoutBounds().getHeight());
 	    	int lineNum = Math.round((Math.round(yPos) + Math.round(barValue)) / Math.round(height));
 	    	Node current = (Node) pointers.get(lineNum);
 	    	double ofCurrent = current.item.getY();
 	    	while(current.item.getX() + Math.round(current.item.getLayoutBounds().getWidth()) < xPos){
 	    			if (current.item.getY() != ofCurrent){
 	    				cursorPrevious = current.previous;
 	    				cursorNext = current;
 	    				return;
 	    			}
 	    			current = current.next;
 	    	}
 	   		double left = xPos- current.item.getX() + Math.round(current.item.getLayoutBounds().getWidth());
 	    	double right = current.previous.item.getX() + Math.round(current.previous.item.getLayoutBounds().getWidth() - xPos);

 	    	if (left < right){
 	   			cursorPrevious = current;
 	   			cursorNext = current.next;
 	   		} else {
    			cursorPrevious = current.previous;
	    		cursorNext = current;
	    	}
 	    }
  	    public boolean isEmpty() {
  	        return size == 0;
    	}
    	public int size() {
        	return size;
    	}
    	public Node sentinel() {
    		return sentinel;
    	}
    	public Node first() {
    		return sentinel.next;
    	}
    	public void begin(){
    		cursorPrevious = sentinel;
    		cursorNext = sentinel.next;
    	}
    	public Text get(int index) {
        	int currindex = 0;
        	Node current = sentinel.next;
        	while(current !=  sentinel){
            	if(currindex == index){
          			return current.item;
            	}
            	current = current.next;
            	currindex++;
        	}
       		return null;
    	}
	}
}