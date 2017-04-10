package editor;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.input.MouseEvent;
import java.util.List;
import java.util.LinkedList;
import editor.TextData.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;


public class Editor extends Application {

    private int window_width = 500;
    private int window_height = 500;
    private double textLength;

    private TextData textData;
    private Render render;

    private LinkedList<TextData> z;
    private LinkedList<TextData> y;
    
    private File file;

    private ScrollBar bar;
    private Rectangle cursor;

    private Group group;
    private Group textRoot;

    public Editor(){
        cursor = new Rectangle(1, 20, Color.BLACK);
        textData = new TextData();
        render = new Render();

        textLength = 0;

        z = new LinkedList<TextData>();
        y = new LinkedList<TextData>();
        z.addFirst(textData.copy());
    }
    /** An EventHandler to handle keys that get pressed. */
    public class KeyEventHandler implements EventHandler<KeyEvent> {

        KeyEventHandler(final Group root, int windowWidth, int window_height) {
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                String characterTyped = keyEvent.getCharacter();
                if (keyEvent.isShortcutDown()) {
                    keyEvent.consume();
                } else if(characterTyped.equals("\r")) {
                    Text letter = new Text(5,0, "/n");
                    textData.insert(letter);
                    updateCursor(render.render(textData,window_width,window_height));
                    keyEvent.consume();
 
                    if (z.size() > 100){
                        z.removeLast();
                    }
                    z.addFirst(textData.copy());



                } else if (characterTyped.length() > 0) {
                    Text letter = new Text(5,0, characterTyped);
                    textData.insert(letter);
                    textRoot.getChildren().add(letter);
                        
                    updateCursor(render.render(textData,window_width,window_height));
                    
                    if (z.size() > 100){
                        z.removeLast();
                    }
                    z.addFirst(textData.copy());

                    keyEvent.consume();
                }	
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                if (keyEvent.isShortcutDown()) {
                    if (keyEvent.getCode() == KeyCode.Z) {
                        undo();
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.Y) {
                        redo();
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.P){
                        System.out.println(cursor.getX() + ", " + cursor.getY());
                    }  else if (keyEvent.getCode() == KeyCode.MINUS) {
                        render.decreaseFont();
                        updateCursor(render.render(textData,window_width,window_height));;
                    } else if (keyEvent.getCode() == KeyCode.PLUS || keyEvent.getCode() == KeyCode.EQUALS) {
                        render.increaseFont();
                        updateCursor(render.render(textData,window_width,window_height));
                    } else if (keyEvent.getCode() == KeyCode.S){
                        try {
                            FileWriter writer = new FileWriter(file);

                            Node current = textData.structure().first();
                            while(current != textData.structure().sentinel()) {
                                char charRead;
                                if (current.item.getText().equals("/n")){
                                    charRead = '\r';
                                } else {
                                    charRead = current.item.getText().charAt(0);
                                }
                                writer.write(charRead);
                                current = current.next;
                            }
                            writer.close();
                        } catch (FileNotFoundException fileNotFoundException) {
                            System.out.println("File not found! Exception was: " + fileNotFoundException);
                        } catch (IOException ioException) {
                            System.out.println("Error when copying; exception was: " + ioException);
                        }
                        keyEvent.consume();
                    }
                }
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.UP) {
                    textData.up();
                    updateCursor(render.render(textData,window_width,window_height));
                } else if (code == KeyCode.DOWN) {
                    textData.down();
                    updateCursor(render.render(textData,window_width,window_height));
                } else if (code == KeyCode.RIGHT) {
                    textData.right();
                    updateCursor(render.render(textData,window_width,window_height));
                } else if (code == KeyCode.LEFT) {
                    textData.left();
                    updateCursor(render.render(textData,window_width,window_height));
                } else if (code == KeyCode.BACK_SPACE) {
                    
                    Text deleted = textData.delete();
                    textRoot.getChildren().remove(deleted);

                    updateCursor(render.render(textData,window_width,window_height));

                    if (z.size() > 100){
                        z.removeLast();
                    }
                    z.addFirst(textData.copy());
                }
            }
        }
    }
    public void updateCursor(double[] pos){
        cursor.setX(pos[0]);
        cursor.setY(pos[1]);
        cursor.setHeight(pos[2]);
        textLength = pos[3];

        bar.setMin(0);
        bar.setMax(Math.max(0,textLength-window_height));

        if (cursor.getX() < bar.getValue()){
            bar.setValue(cursor.getX());
        } else if (cursor.getX() + pos[2] > bar.getValue() + window_height){
            bar.setValue(cursor.getX() + pos[2] - window_height);
        }

    }
    public void redo(){
        if (y.size() > 0){
            textRoot.getChildren().clear();
            textData = y.removeFirst();
            z.addFirst(textData);
            if (!textData.structure().isEmpty()) {
                Node current = textData.structure().first();
                while(current != textData.structure().sentinel()) {
                    if (current.item.getText() == "/n"){
                        current = current.next;
                    } else {
                        textRoot.getChildren().add(current.item);
                        current = current.next;
                    }
                }
            }
            textRoot.getChildren().add(cursor);
            updateCursor(render.render(textData,window_width,window_height));
        }
    }
    public void undo() {
        if (z.size() > 1) {
            textRoot.getChildren().clear();
            y.addFirst(z.removeFirst());
            textData = z.peek();
            if (!textData.structure().isEmpty()) {
                Node current = textData.structure().first();
                while(current != textData.structure().sentinel()) {
                    if (current.item.getText() == "/n"){
                        current = current.next;
                    } else {
                        textRoot.getChildren().add(current.item);
                        current = current.next;
                    }
                }
            }
            textRoot.getChildren().add(cursor);
            updateCursor(render.render(textData,window_width,window_height));
        }
    }
    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        group = new Group();
        textRoot = new Group();
        bar = new ScrollBar();

        bar.setMax(0);
        bar.setMin(0);

        group.getChildren().add(bar);
        group.getChildren().add(textRoot);
        textRoot.getChildren().add(cursor);

        Scene scene = new Scene(group, window_width, window_height, Color.WHITE);
        EventHandler<KeyEvent> keyEventHandler = new KeyEventHandler(group, window_width, window_height);

        List<String> parameters = getParameters().getRaw();

        if (parameters.size() == 0) {
           System.out.println("No Filename Provided");
           System.exit(1);
        }
        if (parameters.size() >= 1){
            String inputFilename = parameters.get(0);
            file = new File(inputFilename);
            try {
                if (file.exists()) {

                    FileReader reader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    int intRead = -1;
                    while ((intRead = bufferedReader.read()) != -1) {
                        char charRead = (char) intRead;
                        if (("\r").equals(Character.toString(charRead)) || ("\r\n").equals(Character.toString(charRead))) {
                            Text letter = new Text(5,0, "/n");
                            textData.insert(letter);
                        } else {
                            Text inserted = new Text(5, 0, ("" + charRead));
                            textRoot.getChildren().add(inserted);
                            textData.insert(inserted);
                        }
                    }
                    bufferedReader.close(); 
                    updateCursor(render.render(textData,window_width,window_height));

                    textData.begin();
                    cursor.setX(5);
                    cursor.setY(0);
                    bar.setValue(0);

                    z.removeFirst();
                    z.addFirst(textData.copy());
                }
            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found! Exception was: " + fileNotFoundException);
            } catch (IOException ioException) {
                System.out.println("Error when copying; exception was: " + ioException);
            }
        }
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                window_width = newScreenWidth.intValue();
                bar.setLayoutX(window_width-bar.getLayoutBounds().getWidth());
                updateCursor(render.render(textData,window_width,window_height));
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                window_height = newScreenHeight.intValue();
                bar.setPrefHeight(window_height);
                updateCursor(render.render(textData,window_width,window_height));
            }
        });

        bar.setOrientation(Orientation.VERTICAL);
        bar.setPrefHeight(window_height);
        bar.setLayoutX(window_width-bar.getLayoutBounds().getWidth());
        bar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                textRoot.setLayoutY(-newValue.intValue());
                updateCursor(render.render(textData,window_width,window_height));
            }
        });

        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler(group));
        makeCursorBlink();

        primaryStage.setTitle("Editor");

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
    }
    public class MouseClickEventHandler implements EventHandler<MouseEvent> {

            MouseClickEventHandler(Group root) {
            }
            @Override
            public void handle(MouseEvent mouseEvent) {
                textData.structure().click(mouseEvent.getX(),mouseEvent.getY(),bar.getValue(),window_width);
                updateCursor(render.render(textData,window_width,window_height));
            }
    }
    public void makeCursorBlink() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinkEventHandler cursorChange = new CursorBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
    private class CursorBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 1;
        
        CursorBlinkEventHandler() {
            blink();
        }
        private void blink() {
            if (currentColorIndex == 1){
                cursor.setWidth(0);
            }else{
                cursor.setWidth(1);
            }
            currentColorIndex = (currentColorIndex % 2) + 1;
        }
        @Override
        public void handle(ActionEvent event) {
            blink();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}