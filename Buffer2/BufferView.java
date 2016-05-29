package trabalho2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	private static int buffer_height;
	private static int buffer_width;
	private static int start_row = 0;
	private static List<FileBuffer> bufferList = new CircularList<FileBuffer>();
	private static boolean circular = false;

	/**
	 * Usage:
	 * BufferView fileName -new | Create new file named fileName, when saved save to fileName
	 * BufferView fileName -open | Open new file named fileName, when saved save to fileName
	 * BufferView #filesToOpen -cycle fileName1 fileName2 fileName3 fileName4 | Open however many new filed named fileName#, when saved save to fileName#
	 * @param args
	 * @throws LineInputException 
	 * @throws InvalidCursorPosition 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InvalidCursorPosition, LineInputException, IOException {
		System.out.println(args[0] + " " + args[1]);
		if(args[1].equals("-new")) {
			//acho que ta a funcionar direito
			File file = new File(args[0]);
			try {
				file.getCanonicalPath();
			}
			catch (IOException e){

			}
			
			if (!file.exists()) {
				buffer = new FileBuffer();
				buffer.setSavePath(file.toPath());
			} else {
				System.exit(5);
			}
		} else if (args[1].equals("-open")) {
			try {
				Path path = Paths.get(args[0]);
				buffer = new FileBuffer();
				buffer.open(path);
				buffer.setCursor(0, 0);
			}
			
			catch (NoSuchFileException e){
				return;
			}
			
			catch (IOException e) {
				
			}
		} else if (args[1].equals("-cycle")) {
			circular = true;
			for(int i = 0; i <Integer.parseInt(args[0]) ; i++) { //abre os ficheiros em sequencia
				
				try {
					FileBuffer tmpBuffer = new FileBuffer();
					Path path = Paths.get(args[i+2]);
					tmpBuffer.open(path);
					tmpBuffer.setCursor(0, 0);
					bufferList.add(tmpBuffer);
				}
				
				catch (NoSuchFileException e){
					return;
				}
				
				catch (IOException e) {
					
				}
				
			}
			buffer = bufferList.get(0);
		} else {
			System.out.println("Argument Error");
			System.exit(1);
		}


		screen = new Screen(TerminalFacade.createTerminal(System.in, System.out, 
				Charset.forName("UTF-8")));
		createTerminal(screen);
		terminal.setCursorVisible(true);

		readInput();
	}


	/**
	 * Main program loop, reads input, draws and updates screen and its variables. Also has other functions such as copy, cut, paste, save, exit through hotkey usage.
	 * Escape      | Exit program without saving
	 * Control + n | Set initial Mark for Copy
	 * Control + c | Set end Mark for Copy
	 * Control + v | Paste using initial and end Mark
	 * Control + S | Save to path given at launch
	 * 
	 * If opening with -cycle
	 * Control + k | Cycle through open files
	 * Control + W | Close current open file
	 * 
	 * @throws InvalidCursorPosition
	 * @throws LineInputException
	 * @throws IOException 
	 */
	private static void readInput() throws InvalidCursorPosition, LineInputException, IOException {
		int index = 1;
		updateBufferSize();
		printBuffer();
		Key k = null;
		do {
			onWindowResized();
			k = terminal.readInput();
			if(k != null) {
				//Hotkeys para executar funcionalidades do programa, save, etc
				if(k.isCtrlPressed()) {
					switch(k.getCharacter()) { //return compares a um char especiico
					case 'k': { if(circular) {buffer = bufferList.get(index); index++; updateVisualCursor();} break; } //tmpbuff = buffer; buffer = buffer2; buffer2 = tmpbuff; break;   
					case 'w': { if(circular && index>0) { buffer = bufferList.get(index); bufferList.remove(index-1); index++; updateVisualCursor(); } break; }
					case 'n': { buffer.setMark(buffer.getcursorRow(), buffer.getcursorCol()); break; }
					case 'c': { buffer.copy(); break; }
					case 'v': { buffer.paste(); updateVisualCursor(); break; }
					case 's': { buffer.save(); break; }
					}
				} else {
					switch(k.getKind()) { //return compares a um tipo de char
					case NormalKey: { insert(k.getCharacter());  break; } 
					case Backspace: { delete(); break; }
					case ArrowUp:    { moveUp();    break; }
					case ArrowRight: { moveRight(); break; }
					case ArrowDown:  { moveDown();  break; }
					case ArrowLeft:  { moveLeft();  break; }
					case Enter:      { breakLine(); break; }  //pedir ao prof os 2% de volta que era public por isto
					case Tab:        { insertString("    ");  break; }
					case Escape:     { System.exit(0); }
					default: continue;
					}
				}
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
			printBuffer();
		}
	}

	/**
	 * Updates global variables to indicate known screen dimensions
	 */
	private static void updateBufferSize() {
		TerminalSize window_size = screen.getTerminalSize();

		buffer_height = window_size.getRows() +1;
		buffer_width  = window_size.getColumns();
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
		int nLines = buffer.getNLines();

		ScreenWriter writer = new ScreenWriter(screen);

		screen.clear();

		int[] start = getLogicPos(start_row,0);

		int currScreenLine = 0;

		int currLogLineRow = start[0];
		int currLogLineCol = start[1];

		//loop principal do print
		while(currScreenLine < buffer_height && currLogLineRow < nLines) {
			String line = buffer.getLine(currLogLineRow);

			if(line.isEmpty()) { //nao se da ao trabalho de imprimir
				currLogLineRow++;
				currLogLineCol=0;
				currScreenLine++;
				continue;
			} 
			
			if (currLogLineCol >= line.length()) { //acabei de desenhar esta linha logica. gogo next
				currLogLineRow++;
				currLogLineCol = 0;
				continue;
			}

			int lenght = Math.min(line.length() - currLogLineCol, buffer_width);
			writer.drawString(0, currScreenLine, line.substring(currLogLineCol,currLogLineCol + lenght));

			currLogLineCol += lenght;
			currScreenLine++;
		}
		
		
		for(int i = currScreenLine; i < buffer_height; i++)
        	writer.drawString(0, i, "_");
		
		
		
		screen.refresh();
	}
	


	/**
	 * Updates visual position of the cursor
	 */
	private static void updateVisualCursor() {

		int[] tuploPos = getViewPos(buffer.getcursorRow(),buffer.getcursorCol());

		//se o cursor está acima do ecra
		if(tuploPos[0] < start_row) {
			//mexe a linha em que começa a desenhar para cima
			start_row = tuploPos[0];
			tuploPos = getViewPos(buffer.getcursorRow(),buffer.getcursorCol());
		} else if (tuploPos[0] +1>= start_row + buffer_height){ 		//se o cursor está abaixo do ecra
			//mexe a linha em que começa a desenhar para baixo
			start_row++;//tuploPos[0]; // - start_row - buffer_height +1 ;
			tuploPos = getViewPos(buffer.getcursorRow(),buffer.getcursorCol());
		}
		
		
		screen.setCursorPosition(tuploPos[1],tuploPos[0] - start_row);
		screen.refresh();

		printBuffer();

	}

	/**
	 * Converte posicao logica no buffer para posicao visual no screen
	 * @param row
	 * @param col
	 * @return array {row, col}
	 */
	private static int[] getViewPos(int row, int col) {
		int screen_line = 0;

		//conta linhas visuais ate a row
		for(int y =0; y < row; y++) {
			String line = buffer.getLine(y);

			if(line.isEmpty()) { //se a linha está vazia nao pode ter mais do que uma linha visual
				screen_line++;
				continue;
			}

			screen_line += line.length() / buffer_width;	//divisao inteira do numero de linhas visuais

			int resto = line.length() % buffer_width;		//caso haja resto existe mais uma linha visual para somar
			if(resto !=0) screen_line++;
		}

		//porque o cursor logico pode estar na segunda ou mais linha visual
		screen_line += col/buffer_width;


		return new int[] {screen_line,col%buffer_width};
	}


	/**
	 * Converte posicao visual no screen pra posicao logica no buffer
	 * @param row
	 * @param col
	 * @return array {row, col}
	 */
	private static int[] getLogicPos(int row, int col) {
		int nLines = buffer.getNLines();

		int start_screen_line = 0;

		for(int y =0; y < nLines;y++) {	//corre todas as linhas logicas, se o cursor estiver dentro do ecra retorna resultado mais cedo
			String line = buffer.getLine(y);

			int end_screen_line;

			if(line.isEmpty()) {	//se a line está vazia so pode ocupar uma linha visual
				end_screen_line = start_screen_line +1;
			} else {				//caso contrario calcula quantas linhas visuais a line ocupa
				int num_screen_lines = line.length() / buffer_width;
				int rest = line.length() % buffer_width;
				if(rest!=0) num_screen_lines++;
				end_screen_line = start_screen_line + num_screen_lines;
			}

			if(row < end_screen_line) { //se está dentro do ecra retorna
				int subline = row - start_screen_line;

				return new int[] {y, subline*buffer_width +col};
			}

			start_screen_line = end_screen_line; //prepara proximo loop
		}

		return null;

	}

	/**
	 * Insere char no buffer e actualiza cursor visual
	 * @param c
	 * @throws LineInputException
	 */
	private static void insert(char c) throws LineInputException {
		buffer.insert(c);
		updateVisualCursor();
	}
	
	/**
	 * Insere string no buffer e actualiza cursor visual
	 * @param string
	 * @throws LineInputException
	 */
	private static void insertString(String string) throws LineInputException {
		for(char c : string.toCharArray()) insert(c);
	}
	
	/**
	 * Insere breakline no buffer e actualiza cursor visual
	 */
	private static void breakLine() {
		buffer.breakLine();
		updateVisualCursor();
	}
	
	/**
	 * Apaga caracter ou breakline no buffer e actualiza cursor visual
	 */
	private static void delete() {
		buffer.delete();
		
		updateVisualCursor();
	}
	
	/**
	 * Move no buffer usando cursor visual como indicador
	 */
	private static void moveLeft() {
		buffer.moveLeft();
		updateVisualCursor();
	}
	
	/**
	 * Move no buffer usando cursor visual como indicador
	 */
	private static void moveRight() {
		buffer.moveRight();
		updateVisualCursor();
	}

	/**
	 * Move no buffer usando cursor visual como indicador de direcao, Scrolls the buffer visualisation up, if possible
	 */
	private static void moveUp() {
		int isFirstVisualLineOfCurrentLogicLine = buffer.getcursorCol() / buffer_width; 

		//se está na primeira linha visual da linha logica, cursor logico pode simplesmente andar para cima
		if(isFirstVisualLineOfCurrentLogicLine == 0) {
			int col = buffer.getcursorCol();

			if(buffer.getcursorRow() == 0) return;

			buffer.moveUp();

			String tmpLine = buffer.getLine(buffer.getcursorRow());

			int nVisualLines = tmpLine.length() / buffer_width;

			buffer.setCursorCol(Math.min(tmpLine.length(), nVisualLines * buffer_width + col));

		} 
		//se está numa outra linha visual anda uma linha visual para cima
		else {
			buffer.setCursorCol(buffer.getcursorCol() - buffer_width);
		}

		updateVisualCursor();

	}

	/**
	 * Move no buffer usando cursor visual como indicador de direcao, Scrolls the buffer visualisation down, if possible
	 */
	private static void moveDown() {
		int isFirstVisualLineOfCurrentLogicLine = buffer.getcursorCol() / buffer_width; 

		String line = buffer.getLine(buffer.getcursorRow());

		int nVisualLines = line.length()/buffer_width;
		int rest = line.length()%buffer_width;
		if(rest !=0) nVisualLines++;

		if(line.isEmpty() || isFirstVisualLineOfCurrentLogicLine == nVisualLines - 1) {
			buffer.moveDown();
		} else {
			buffer.setCursorCol(Math.min(line.length(), buffer.getcursorCol() + buffer_width));
		}

		updateVisualCursor();
	}
}

/**
 * Extensao de arraylist para permitir uma lista circular de buffers
 * @author azthec
 *
 * @param <E>
 */
class CircularList<E> extends ArrayList<E> {
    @Override
    public E get(int index) {
        return super.get(index % size());
    }
    
    @Override
    public E remove(int index) {
    	return super.remove(index % size());
    }
}