package editor;

import javafx.scene.text.Text;
import javafx.scene.Group;
import editor.TextData.Node;
import editor.TextData.Line;
import editor.Editor.KeyEventHandler;
import javafx.scene.text.Font;
import javafx.geometry.VPos;

public class Render {

    public int fontSize = 20;
    public String fontName = "Verdana";
	
	public Render() {
	}
	public void increaseFont(){
		fontSize += 4;
	}
	public void decreaseFont(){
		fontSize = Math.max(0,fontSize-4);
	}

	//Iteraters through linked list of structure. Updates X and Y positiions of Text objects if needed and modifies pointers of 
	//structure. 
	public double[] render(TextData d, int w, int h) {

		int windowWidth = w;
		int windowHeight = h;
		TextData data = d;
		double currX = 5;
		double currY = 0;
		Boolean oneWord = true;
		double[] cursorPosition = new double[] {5.0,0.0,fontSize + 8,0.0};

		data.pointers().clear();

		if (data.structure().isEmpty()){
			return cursorPosition;
		}

		Node current = data.structure().first();
		data.pointers().add(current);

		while(current != data.structure().sentinel()) {
			
			//Check if it has been one word;
			if (current.item.getText().equals(" ")){
					oneWord = false;
			}

			current.item.setFont(Font.font(fontName, fontSize));
			current.item.setTextOrigin(VPos.TOP);

			double width = Math.round(current.item.getLayoutBounds().getWidth());

			if (current.item.getText().equals("/n")){
					currX  = 5;
					currY += current.item.getLayoutBounds().getHeight();
					current.item.setX(5);
					current.item.setY(currY);
					data.pointers().add(current);
			}

			else if (currX + width > windowWidth - 20 -5){
				currX = 5;
				currY += Math.round(current.item.getLayoutBounds().getHeight());
				if (oneWord){
					current.item.setX(currX);
					current.item.setY(currY);
					currX += width;
				} else {
					Node temp = current;
					while (!(temp.previous.item.getText().equals(" "))){
						temp = temp.previous;
						if (temp == data.structure().sentinel()){
							break;
						}
					}
					data.pointers().add(temp);
					while (temp != current.next){
						temp.item.setX(currX);
						temp.item.setY(currY);
						currX += Math.round(temp.item.getLayoutBounds().getWidth());
						temp = temp.next;
					}
				}
			oneWord = true;
			} else {
				current.item.setX(currX);
				current.item.setY(currY);
				currX += width;
			}
			current = current.next;
		}
		if (data.cursorPrevious != data.structure().sentinel()){
			if (data.cursorPrevious.item.getText().equals("/n")){
				cursorPosition[0] = 5;
				cursorPosition[1] = data.cursorPrevious.item.getY();
				cursorPosition[2] = Math.round(data.cursorPrevious.item.getLayoutBounds().getHeight());
				cursorPosition[3] = data.numLines() * Math.round(data.cursorPrevious.item.getLayoutBounds().getHeight());
			} else {
				cursorPosition[0] = data.cursorPrevious.item.getX() + Math.round(data.cursorPrevious.item.getLayoutBounds().getWidth());
				cursorPosition[1] = data.cursorPrevious.item.getY();
				cursorPosition[2] = Math.round(data.cursorPrevious.item.getLayoutBounds().getHeight());
				cursorPosition[3] = data.numLines() * Math.round(data.cursorPrevious.item.getLayoutBounds().getHeight());
			}	
		} else if (data.cursorNext != data.structure().sentinel()) {
				cursorPosition[0] = 5;
				cursorPosition[1] = 0;
				cursorPosition[2] = Math.round(data.cursorNext.item.getLayoutBounds().getHeight());
				cursorPosition[3] = data.numLines() * Math.round(data.cursorNext.item.getLayoutBounds().getHeight());
		}
		return cursorPosition;
	}
}