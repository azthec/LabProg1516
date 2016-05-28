package trabalho2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by up201103891 on 4/19/16.
 */

public class Buffer {
    protected List<StringBuilder> lineList; //lineList.get(3) obtem o quarto elemento da lista
    private int cursorRow, cursorCol;

    /**
     * Metodo construtor para um documento vazio
     */
    public Buffer() {
        lineList = new ArrayList<StringBuilder>();
        lineList.add(new StringBuilder("a"));
        lineList.add(new StringBuilder("b"));
        lineList.add(new StringBuilder("c"));
        lineList.add(new StringBuilder("d"));
        lineList.add(new StringBuilder("e"));
        lineList.add(new StringBuilder("f"));
        lineList.add(new StringBuilder(""));
        System.out.println(lineList.size());
        cursorRow = 0;
        cursorCol = 0;
    }
    
    /**
     * Metodo contrutor para um documento com uma string tmp (que pode conter \n)
     * @param tmp
     */
    public Buffer(String tmp) {
    	String stringArray[] = tmp.split("\n");
    	lineList = new ArrayList<StringBuilder>();
    	
    	for(String string : stringArray) {
    		lineList.add(new StringBuilder(string));
    	}
    	cursorRow = 0;
    	cursorCol = 0;
    }

    /**
     * Insere caracteres, caso particular do insertString
     * @param c
     * @throws LineInputException
     */
    public void insert(char c) throws LineInputException {
    	if(c=='\n') {
    		breakLine();
    	} else {
    		insertString(Character.toString(c));
    	}
    }
    
    /**
     * Insere uma string, precondicao de que o texto nao deve conter \n
     * @param tmp
     * @throws LineInputException
     */
    public void insertString(String tmp) throws LineInputException {
    	String stringsArray[] = tmp.split("\n");
    	
//    	for(String string : stringsArray) {
//    		lineList.get(cursorRow).insert(cursorCol, string);
//        	cursorCol += tmp.length();
//    	}
    	if(stringsArray.length > 1) {
    		throw new LineInputException();
    	} else {
    		lineList.get(cursorRow).insert(cursorCol, tmp);
        	cursorCol += tmp.length();
    	}
    }
    
    /**
     * Elimina um caracter imediatamente antes do cursor (backspace)
     */
    public void delete() {
        if(cursorCol == 0 && cursorRow != 0) {
            lineList.get(cursorRow-1).append(lineList.get(cursorRow));
            lineList.remove(cursorRow);
            cursorCol=lineList.get(cursorRow-1).length();
            moveUp();
        } else if(cursorCol != 0) {
        	//apaga caracter a esquerda do cursor
            lineList.get(cursorRow).deleteCharAt(cursorCol-1);
            moveLeft();
        }
    }
    
    /**
     * Move o cursor para cima
     */
    public void moveUp() {
    	//se a linha de cima for menor, salta para a posicao do final dela
    	if(cursorRow > 0) {
    		cursorCol = Math.min(cursorCol, lineList.get(cursorRow-1).length());
    		cursorRow--;
    	}
    }

    /**
     * Move o cursor para a direita
     */
    public void moveRight() {
    	if(cursorCol == lineList.get(cursorRow).length() && cursorRow < lineList.size()-1) {
    		cursorCol=0;
    		cursorRow++;
    	} else if(cursorCol < lineList.get(cursorRow).length()) {
    		cursorCol++;
    	}
    }

    /**
     * Move o cursor para baixo
     */
    public void moveDown() {
    	if(cursorRow < lineList.size() -1) {
    		cursorCol = Math.min(cursorCol, lineList.get(cursorRow+1).length());
    		cursorRow++;
    	}
    }

    /**
     * Move o cursor para a esquerda
     */
    public void moveLeft() {
    	if(cursorCol == 0 && cursorRow != 0) {
    		cursorCol=lineList.get(cursorRow-1).length();
    		cursorRow--;
    	} else if (cursorCol!=0) {
    		cursorCol--;
    	}
    }

    /**
     * Devolve o numero de linhas existentes na lista de linhas logicas
     * @return
     */
    public int getNLines() {
    	//ter atencao que o index das linhas comeca no 0
        return lineList.size();
    }
    
    /**
     * Quebra a linha na posicao do cursor (breakline, enter)
     */
    public void breakLine() {
    	//guardar string da pos cursor ate ao fim da linha
    	String rest = lineList.get(cursorRow).substring(cursorCol,lineList.get(cursorRow).length());
    	//apagar stringbuilder do cursor ate ao fim da linha
    	lineList.get(cursorRow).replace(cursorCol, lineList.get(cursorRow).length(), "");
    	//adicionar linha nova com string rest
    	lineList.add(cursorRow+1,new StringBuilder(rest));
    	//posicionar cursor no novo sitio
    	cursorRow++;
    	cursorCol=0;
    }

    /**
     * Devolve uma string correspondente a linhaLogica com index pedido
     * @param nLineIndex
     * @return
     */
    public String getLine(int nLineIndex) {
        //ter atencao que as linha comecam no 0
    	return lineList.get(nLineIndex).toString();
    }
    
    /**
     * Devolve coluna corrente do cursor, comeca em 0
     * @return
     */
    public int getcursorCol() {
    	return cursorCol;
    }
    
    /**
     * Devolve linha corrente do cursor, comeca em 0
     * @return
     */
    public int getcursorRow() {
    	return cursorRow;
    }
    
    /**
     * Devolve booleano conforme a posicao pedida e valida ou nao
     * @param row
     * @param col
     * @return
     */
    public boolean validPosition(int row, int col) {
    	try {
    		if(0 <= col && col <= lineList.get(row).length())
        		if(0 <= row && row <= lineList.size()-1)
        			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
    	return false;
    }
    
    /**
     * Se a posicao pedida for valida actualiza o cursor
     * @param row
     * @param col
     * @throws InvalidCursorPosition
     */
    public void setCursor(int row, int col) throws InvalidCursorPosition {
    	if(validPosition(row,col)) {
    		cursorRow = row;
    		cursorCol = col;
    	} else {
    		throw new InvalidCursorPosition();
    	}
    }
    
    public void setCursorRow(int row) {
    	cursorRow = row;
    }
    
    public void setCursorCol(int col) {
    	cursorCol = col;
    }
}
