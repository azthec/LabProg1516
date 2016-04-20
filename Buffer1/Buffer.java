import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by up201103891 on 4/19/16.
 */

public class Buffer {
    protected List<StringBuilder> lineList; //lineList.get(3) obtem o quarto elemento da lista
    private int cursorRow, cursorCol;

    // inicialização
    public Buffer() {
        lineList = new ArrayList<StringBuilder>();
        lineList.add(new StringBuilder(""));
        cursorRow = 0;
        cursorCol = 0;
    }

    // inserir carater
    public void insert(char c) {
    	if(c=='\n') {
    		breakLine();
    	} else { //talvez seja preciso adiciona trycatch
    		insertString(Character.toString(c));
    	}
    }
    //inserior string
    public void insertString(String tmp) {
    	lineList.get(cursorRow).insert(cursorCol, tmp);
		cursorCol += tmp.length();
    }
    // apagar carater

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
    // mover o cursor
    public void moveUp() {
    	//se a linha de cima for menor, salta para a posicao do final dela
    	if(cursorRow > 0)
    		cursorCol = Math.min(cursorCol, lineList.get(cursorRow-1).length());
    }

    public void moveRight() {
    	if(cursorCol == lineList.get(cursorRow).length() && cursorRow != lineList.size()-1) {
    		cursorCol=0;
    		cursorRow++;
    	} else if(cursorCol != lineList.get(cursorRow).length()) {
    		cursorCol++;
    	}
    }

    public void moveDown() {
    	if(cursorRow < lineList.size() -1) {
    		cursorCol = Math.min(cursorCol, lineList.get(cursorRow+1).length());
    	}
    }

    public void moveLeft() {
    	if(cursorCol == 0 && cursorRow != 0) {
    		cursorCol=lineList.get(cursorRow-1).length();
    		cursorRow--;
    	} else if (cursorCol!=0) {
    		cursorCol--;
    	}
    }

    public int getNLines() {
    	//ter atencao que as linha comecam no 0
        return lineList.size();
    }
    
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

    public StringBuilder getLine(int nLine) { //ou toString se quisermos retornar strings simples
        //ter atencao que as linha comecam no 0
    	return lineList.get(nLine);
    }
    
    public int getcursorCol() {
    	return cursorCol;
    }
    
    public int getcursorRow() {
    	return cursorRow;
    }
    
    public boolean validPosition(int row, int col) {
    	if(0 <= col && col <= lineList.get(row).length())
    		if(0 <= row && row <= lineList.size()-1)
    			return true;
    	return false;
    }
    
    public void setCursor(int row, int col) throws IOException {
    	if(validPosition(row,col)) {
    		cursorRow = row;
    		cursorCol = col;
    	} else {
    		throw new IOException();
    	}
    }
}
