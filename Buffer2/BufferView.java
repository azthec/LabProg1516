package trabalho2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.screen.*;


public class BufferView {
	private static FileBuffer buffer;
	private static Terminal terminal;
	private static Screen screen;
	private static String path; // buffer.open(FileSystems.getDefault().getPath(path)) ??
	private static int bufferSizeX;
	private static int bufferSizeY;
	private static int bufferFirstLineDraw;
	private static int cursorVisualX;
	private static int cursorVisualY;
	
	/**
	 * Usage:
	 * BufferView fileName -new | Create new file named fileName, when saved save to fileName
	 * BufferView fileName -open | Open new file named fileName, when saved save to fileName
	 * @param args
	 * @throws LineInputException 
	 * @throws InvalidCursorPosition 
	 */
	public static void main(String[] args) throws InvalidCursorPosition, LineInputException {
		System.out.println(args[0] + " " + args[1]);
		if(args[1].equals("-new")) {
			//TODO fix path to create file / save file location
			buffer = new FileBuffer();
		} else if (args[1].equals("-open")) {
			//TODO fix path to create file / save file location
			//TODO everything else
		} else {
			System.out.println("Argument Error");
			System.exit(1);
		}
		
		
		screen = new Screen(TerminalFacade.createTerminal(System.in, System.out, 
				Charset.forName("UTF-8")));
		createTerminal(screen);
		terminal.setCursorVisible(true);
		printBuffer();
		readInput();
	}
	
	
	/**
	 * Main program loop, reads input, draws and updates screen and its variables.
	 * @throws InvalidCursorPosition
	 * @throws LineInputException
	 */
	private static void readInput() throws InvalidCursorPosition, LineInputException {
		//TODO
		boolean update = false;
		updateBufferSize();
		Key k = null;
		do {
			onWindowResized();
			k = terminal.readInput();
			if(k != null) {
				//Hotkeys para executar funcionalidades do programa, save, etc
				if(k.isCtrlPressed()) {
					switch(k.getCharacter()) { //return compares a um char especiico
						case 's': {  } //TODO savefile on path
						case 'w': { System.exit(0); }
					}
				} else {
					switch(k.getKind()) { //return compares a um tipo de char
						case NormalKey: { buffer.insert(k.getCharacter()); update = true; break; } 
						case Backspace: { buffer.delete(); update = true; break; } //pedir ao prof os 2% de volta que era public por isto
						case ArrowUp:    { buffer.moveUp();    update = true; break; }
						case ArrowRight: { buffer.moveRight(); update = true; break; }
						case ArrowDown:  { buffer.moveDown();  update = true; break; }
						case ArrowLeft:  { buffer.moveLeft();  update = true; break; }
						case Enter:      { buffer.breakLine(); update = true; break; }
						case Tab:        { buffer.insertString("    "); update = true; break; }
//						case PageUp:     { scrollUp(); break; }
//						case PageDown:   { scrollDown(); break; }
						case Escape:     { System.exit(0); }
						default: continue;
					}
				}
			}
			if(update) {
				printBuffer();
				update = false;
			}
		} while ((k == null) || ((k.getKind() != Key.Kind.Escape)));
	}
	
	/**
	 * Event handler if window resized, refreshes screen and updates known screen dimensions
	 * 
	 */
	private static void onWindowResized() {
		if(screen.resizePending()) {
			screen.refresh();
			updateBufferSize();	
		}
	}
	
	/**
	 * Updates global variables to indicate known screen dimensions
	 */
	private static void updateBufferSize() {
		TerminalSize window_size = screen.getTerminalSize();

    	bufferSizeX = window_size.getRows() - 1;
        bufferSizeY  = window_size.getColumns();
	}
	
	/**
	 * Create terminal.
	 * 
	 * @param terminal
	 */
	public static void createTerminal(Screen screen) {
		terminal = screen.getTerminal();
		screen.startScreen();
	}
	
	/**
	 * Get terminal.
	 * 
	 * @return
	 */
	public static Terminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Prints buffer structure onto terminal. 
	 */
	public static void printBuffer() {
		screen.clear();
		screen.setCursorPosition(buffer.getcursorCol()+2, buffer.getcursorRow()-bufferFirstLineDraw);
		ScreenWriter writer = new ScreenWriter(screen);
		int row = 0;
		for(int i = bufferFirstLineDraw; i < buffer.lineList.size(); i++) {
			
			String tmp = buffer.lineList.get(i).toString();
			writer.drawString(0, row, "| "+tmp);
			row++;
		}

		screen.refresh();
		
		
	}
	
//	private void updateVisualCursor() {
//		int[] pos
//	}
//	
	/**
	 * Scrolls the buffer visualisation up, if possible
	 */
	private static void moveUp() {
		int isFirstVisualLineOfCurrentLogicLine = buffer.getcursorCol() / bufferSizeY; 
		
		if(isFirstVisualLineOfCurrentLogicLine == 0) {
			int col = buffer.getcursorCol();
			
			if(buffer.getcursorRow() == 0) return;
			
			buffer.moveUp();
			
			String tmpLine = buffer.getLine(buffer.getcursorRow());
			
			int nVisualLines = tmpLine.length() / bufferSizeY;
			
			buffer.setCursorCol(Math.min(tmpLine.length(), nVisualLines * bufferSizeY + col));
			
		} else {
			buffer.setCursorCol(buffer.getcursorCol() - bufferSizeY);
		}
		
	}
	
	/**
	 * Scrolls the buffer visualisation down, if possible
	 */
	private static void moveDown() {
		int isFirstVisualLineOfCurrentLogicLine = buffer.getcursorCol() / bufferSizeY; 
		
	}
}
