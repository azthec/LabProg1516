package trabalho1;

/**
 * Created by up201103891 on 2/29/16.
 */

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;
//import com.googlecode.lanterna.TerminalSize; //MAYBE?
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;


/*
 * 
 */
class Matriz {
	int[][] matriz;
	int score = 0;
	int highscore = 0;
	boolean vitoria = false;

	Matriz() {
		matriz = new int[4][4];
		Random rgen = new Random();
		// gera 2 posicoes aleatorias para os 2 primeiros numeros

		int x = rgen.nextInt(4);
		int y = rgen.nextInt(4);

		int z = rgen.nextInt(4);
		int w = rgen.nextInt(4);

		// gerar 2 com 90% de probabilidade e 4 com 10%
		if (x == z && y == w) {
			while (x == z && y == w) {
				y = rgen.nextInt(4);
				w = rgen.nextInt(4);
			}
			//This expression produces a random number 0 <= x < 1, then if x is between 0 and 0.9 (i.e. 90% probability), outputs 2, otherwise outputs 4 (10% probability).
			matriz[z][w] = (Math.random() >= .9 ? 4 : 2);
		} else {
			matriz[x][y] = (Math.random() >= .9 ? 4 : 2);
			matriz[z][w] = (Math.random() >= .9 ? 4 : 2);
		}
	}

	public int getScore() {
		return score;
	}


	void moveleft() {

		totheleft();
		// aqui e calculada a soma dos valores quando se juntam e a atualizacao dos valores em cada posicao afetada
		for (int i = 0; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {
				if (matriz[i][j] == matriz[i][j - 1]) {
					matriz[i][j] = 0;
					matriz[i][j - 1] = matriz[i][j - 1] * 2;
					score += matriz[i][j - 1];                // o score e igual ao valor da soma dos numeros que convergiram
					if(matriz[i][j-1]>=2048) vitoria = true;
				}
			}
		}

		totheleft();

	}

	void totheleft() {
		//encostar a esquerda
		for (int i = 0; i < 4; i++) {
			int[] row = new int[4];
			for (int j = 0; j < 4; j++) {
				if (matriz[i][j] != 0) {
					int curr = 0;
					while (row[curr] != 0) {
						curr++;
					}
					row[curr] = matriz[i][j];
				}
			}
			matriz[i] = row;
		}
	}

	void moveright() {

		rotate();
		rotate();
		moveleft();
		rotate();
		rotate();
	}

	void movedown() {

		rotate();
		moveleft();
		rotate();
		rotate();
		rotate();
	}

	void moveup() {

		rotate();
		rotate();
		rotate();
		moveleft();
		rotate();
	}

	/**
	 * Roda matriz no sentido dos ponteiros do relógio
	 * util para evitar error ao escrever moveright() moveup() movedown()
	 */
	private void rotate() {
		int[][] rotated = new int[4][4];
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				rotated[c][3 - r] = matriz[r][c];
			}
		}
		matriz = rotated;
	}


	int[] getMatriz(int i) {
		return matriz[i];
	}

	static String MatrizToString(int[][] matriz) {
		String board = "";
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				board += Integer.toString(matriz[i][j]);
		return board;
	}

	public boolean isOver() {
		return isFull() && !isMoveAllowed();
	}

	//ineficiente?
	public boolean isFull() {
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 3; j++) {
				if (matriz[i][j] == 0) return false;
			}
		}
		return true;
	}


	//ineficiente?
	private boolean isMoveAllowed() {
		//verificar na vertical
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 2; j++) {
				int jj = j + 1;
				if (matriz[i][j] == matriz[i][jj]) return true;
			}
		}
		//verificar na horizontal, notar que i e j estao trocados no if
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 2; j++) {
				int jj = j + 1;
				if (matriz[j][i] == matriz[jj][i]) return true;
			}
		}
		return false;
	}

	public void quit() {
		PrintWriter writer = null;
		File file = new File("highscore.txt");
		if(!file.exists()) {
			try {
				PrintWriter writercreate = new PrintWriter("highscore.txt", "UTF-8");
				writercreate.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			writer = new PrintWriter("highscore.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(score>highscore) {
			writer.println(String.valueOf(score));
			writer.close();
		}
		else {
			writer.println(String.valueOf(highscore));
			writer.close();//escrever buffer no ficheiro e fechar, alternativamente writer.flush() para so escrever
		}
	}

	public void getHighscore() {
		File file = new File("highscore.txt");
		if(file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				while (line != null)                 // read the score file line by line
				{
					try {
						int scoreFile = Integer.parseInt(line.trim());   // parse each line as an int
						if (scoreFile > highscore)                       // and keep track of the largest
						{
							highscore = scoreFile;
						}
					} catch (NumberFormatException e1) {
						// ignore invalid scores
						//System.err.println("ignoring invalid score: " + line);
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (FileNotFoundException ex) {
				System.out.printf("ERROR: %s\n", ex);

			} catch (IOException ex) {
				System.err.println("ERROR");
			}
		} 
		//if file dosen't exist
		else highscore = 0;	
	}

}

//TALVEZ SEJA POSSIVEL CONTROLAR TAMANHO DA JANELA http://grepcode.com/file/repo1.maven.org/maven2/com.googlecode.lanterna/lanterna/2.1.6/com/googlecode/lanterna/terminal/TerminalSize.java#TerminalSize

public class Game2048 {
	private Terminal term;
	private int cursor_x=10, cursor_y=10;
	static Matriz tabuleiro;

	public Game2048() {
		//cria o terminal e lê o input
		term = TerminalFacade.createTerminal();
		term.enterPrivateMode();
		term.setCursorVisible(false);
		tabuleiro.getHighscore();
		show();
		while (true)
		{
			Key k = term.readInput();
			if (k != null) {
				if(tabuleiro.isOver()) {
					tabuleiro.quit();
					term.clearScreen();
					printLoss();
					try {
						Thread.sleep(10000);                 //1000 milliseconds is one second.
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					term.enterPrivateMode();
					System.exit(0);
				}

				if(tabuleiro.vitoria==true) {
					tabuleiro.quit();
					term.clearScreen();
					printWin();
					try {
						Thread.sleep(10000);                 //1000 milliseconds is one second.
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					term.exitPrivateMode();
					System.exit(0);
				}
				switch (k.getKind()) {
				case Escape:
					tabuleiro.quit();
					term.exitPrivateMode();
					return;
				case ArrowLeft:
					tabuleiro.moveleft();
					break;
				case ArrowRight:
					tabuleiro.moveright();
					break;
				case ArrowDown:
					tabuleiro.movedown();
					break;
				case ArrowUp:
					tabuleiro.moveup();
					break;
				}

				//GERAR NUMERO NOVO
				//TODO NAO GERAR NUMERO NOVO QUANDO MOVIMENTO NAO E VALIDO
				boolean flag = true;
				while(flag && !tabuleiro.isFull()) {
					Random rgen = new Random();

					int x = rgen.nextInt(4);
					int y = rgen.nextInt(4);

					if (tabuleiro.matriz[x][y] == 0) {
						tabuleiro.matriz[x][y] = (Math.random() >= .9 ? 4 : 2);
						flag = false;
					}
				}

				term.clearScreen();
				term.applyForegroundColor(Terminal.Color.WHITE);
				show();
				term.flush();
			}
		}

		//desenhar


	}

	
	public void printLoss() {
		//TODO Print loss ascii
		File file = new File("lossString.txt");
		if(!file.exists()) {
			try {
				PrintWriter writer = new PrintWriter("lossString.txt", "UTF-8");
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		cursor_x = cursor_y=0;
		term.moveCursor(0, 0);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null)                 // read the score file line by line
			{
				try {

					//actual code goes here

					for (int k = 0; k < line.length(); k++) {
						term.putCharacter(line.charAt(k));
					}
					cursor_y++;
					term.moveCursor(cursor_x, cursor_y);
				} catch (NumberFormatException e1) {
					// ignore invalid scores
					//System.err.println("ignoring invalid score: " + line);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException ex) {
			System.out.printf("ERROR: %s\n", ex);

		} catch (IOException ex) {
			System.err.println("ERROR");
		}

		cursor_y += 2;
		term.moveCursor(cursor_x, cursor_y);
		String linha = "Quitting in 10 seconds!";
		for (int k = 0; k < linha.length(); k++) {
			term.putCharacter(linha.charAt(k));
		}
		cursor_y += 2;
		term.moveCursor(cursor_x, cursor_y);
		if(tabuleiro.highscore>tabuleiro.score) {
			linha = "You didn't beat your highscore!  |  Score: " + tabuleiro.score + "  |  Highscore: " + tabuleiro.highscore;
		} else if(tabuleiro.highscore==tabuleiro.score) {
			linha = "You matched your highscore!  |  Highscore: " + tabuleiro.highscore;
		} else 
			linha = "You beat your highscore!  |  New Highscore: " + tabuleiro.score + "  |  Old Highscore: " + tabuleiro.highscore;
		for (int k = 0; k < linha.length(); k++) {
			term.putCharacter(linha.charAt(k));
		}
		String sc="Por: Rui Miguel 201203537 & Rui Balau 201103891 | LabProg 2016";
		term.moveCursor(5, 40);
		for (int a =0;a<sc.length();a++)       
			term.putCharacter(sc.charAt(a));

	}
	
	
	public void printWin() {
		//TODO Print win ascii
		File file = new File("winString.txt");
		if(!file.exists()) {
			try {
				PrintWriter writer = new PrintWriter("winString.txt", "UTF-8");
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			cursor_x = cursor_y=0;
			term.moveCursor(0, 0);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				while (line != null)                 // read the score file line by line
				{
					try {
						//actual code goes here
						term.applyForegroundColor(Terminal.Color.RED);
						for (int k = 0; k < line.length(); k++) {
							term.putCharacter(line.charAt(k));
						}
						cursor_y++;
						term.applyForegroundColor(Terminal.Color.DEFAULT);
						term.moveCursor(cursor_x, cursor_y);
					} catch (NumberFormatException e1) {
						// ignore invalid scores
						//System.err.println("ignoring invalid score: " + line);
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (FileNotFoundException ex) {
				System.out.printf("ERROR: %s\n", ex);

			} catch (IOException ex) {
				System.err.println("ERROR");
			}
			cursor_y += 2;
			term.moveCursor(cursor_x, cursor_y);
			String linha = "Quitting in 10 seconds!";
			for (int k = 0; k < linha.length(); k++) {
				term.putCharacter(linha.charAt(k));
			}
			cursor_y += 2;
			term.moveCursor(cursor_x, cursor_y);
			if(tabuleiro.highscore>tabuleiro.score) {
				linha = "You didn't beat your highscore!  |  Score: " + tabuleiro.score + "  |  Highscore: " + tabuleiro.highscore;
			} else if(tabuleiro.highscore==tabuleiro.score) {
				linha = "You matched your highscore!  |  Highscore: " + tabuleiro.highscore;
			} else 
				linha = "You beat your highscore!  |  New Highscore: " + tabuleiro.score + "  |  Old Highscore: " + tabuleiro.highscore;
			for (int k = 0; k < linha.length(); k++) {
				term.putCharacter(linha.charAt(k));
			}
			String sc="Por: Rui Miguel 201203537 & Rui Balau 201103891 | LabProg 2016";
			term.moveCursor(5, 40);
			for (int a =0;a<sc.length();a++)       
				term.putCharacter(sc.charAt(a));
		}
	}

	private String toStrings(int valor) {
		//int sizeValor = String.valueOf(valor).length();
		String linha = new String();
		linha = "" + valor;
		return linha;
	}

	private void colourItUp (int x, int y, int length, Terminal.Color color) {
		int xOrig = x;
		for(int i=0; i<=length;i++) {
			term.moveCursor(x, y);
			term.applyBackgroundColor(color);
			term.putCharacter(' ');
			x++;
		}
		term.moveCursor(xOrig, y);
	}

	private Terminal.Color colourPicker (int valor) {
		switch (valor) {
		case 0:
			return Terminal.Color.WHITE;
		case 2:
			return Terminal.Color.BLUE;
		case 4:
			return Terminal.Color.CYAN;
		case 8:
			return Terminal.Color.GREEN;
		case 16:
			return Terminal.Color.MAGENTA;
		case 32:
			return Terminal.Color.RED;
		case 64:
			return Terminal.Color.YELLOW;
		case 128:
			return Terminal.Color.BLUE;
		case 256:
			return Terminal.Color.CYAN;
		case 512:
			return Terminal.Color.GREEN;
		case 1024:
			return Terminal.Color.MAGENTA;
		case 2048:
			return Terminal.Color.RED;
		}


		return Terminal.Color.DEFAULT;
	}


	public void printTitle() {
		String[] lines = new String[7];
		lines[0]= " _______  _______  _   ___   _____     _______  ___      _______  __    _  _______ ";
		lines[1]= "|       ||  _    || | |   | |  _  |   |       ||   |    |       ||  |  | ||       |";
		lines[2]= "|____   || | |   || |_|   | | |_| |   |       ||   |    |   _   ||   |_| ||    ___|";
		lines[3]= " ____|  || | |   ||       ||   _   |  |       ||   |    |  | |  ||       ||   |___ ";
		lines[4]= "| ______|| |_|   ||___    ||  | |  |  |      _||   |___ |  |_|  ||  _    ||    ___|";
		lines[5]= "| |_____ |       |    |   ||  |_|  |  |     |_ |       ||       || | |   ||   |___ ";
		lines[6]= "|_______||_______|    |___||_______|  |_______||_______||_______||_|  |__||_______|";
		int cursory = 1;
		term.moveCursor(6, cursory);
		for(int i=0; i<7;i++) {
			for(int j=0;j<lines[i].length();j++) {
				term.putCharacter(lines[i].charAt(j));
			}
			cursory++;
			term.moveCursor(6, cursory);
		}
	}

	private void show() {
		term.applyForegroundColor(Terminal.Color.CYAN);
		printTitle();
		term.applyForegroundColor(Terminal.Color.DEFAULT);
		int cursorposx = 20;
		int cursorposy = 9;


		String sc = "Current Score = " + tabuleiro.score;        
		String hs = "Highscore = " + tabuleiro.highscore;


		//colour and print score
		term.moveCursor(50, 10);
		term.applyForegroundColor(Terminal.Color.GREEN);
		for (int a =0;a<sc.length();a++)       
			term.putCharacter(sc.charAt(a));
		term.applyForegroundColor(Terminal.Color.DEFAULT);

		//colour and print highscore
		term.applyForegroundColor(Terminal.Color.YELLOW);
		term.moveCursor(50, 11);
		for (int b =0;b<hs.length();b++)
			term.putCharacter(hs.charAt(b));
		term.applyForegroundColor(Terminal.Color.DEFAULT);

		for (int i=0;i<4;i++) {

			term.moveCursor(cursorposx,cursorposy);
			String termLine = "";
			for (int j = 0; j < 4; j++) {
				termLine = toStrings(tabuleiro.matriz[i][j]);
				colourItUp(cursorposx,cursorposy,6,colourPicker(tabuleiro.matriz[i][j]));
				for (int k = 0; k < termLine.length(); k++) {
					term.putCharacter(termLine.charAt(k));
				}
				cursorposx +=6;
				term.moveCursor(cursorposx, cursorposy);
			}
			cursorposx=20;
			cursorposy += 1;
		}
		term.applyBackgroundColor(Terminal.Color.DEFAULT);


		sc="Por: Rui Miguel 201203537 & Rui Balau 201103891 | LabProg 2016";
		term.moveCursor(5, 40);
		for (int a =0;a<sc.length();a++)       
			term.putCharacter(sc.charAt(a));
	}


	public static void main (String[] args)	{
		tabuleiro = new Matriz();
		//Game2048 corre jogo e faz print utilizando o terminal lanterna, controlos setas e escape para terminar
		new Game2048();
		//testPrint corre jogo e faz print utilizando o standard output, controlos numpad 2 down 4 left 8 up 6 right
		//testPrint();
	}

	/*
	static void testPrint() {

		Scanner in = new Scanner(System.in);
		tabuleiro.getHighscore();
		while (true)
		{

			int[] row = new int[4];
			for(int i=0; i<=3;i++) {
				row = tabuleiro.getMatriz(i);
				for(int j=0; j<=3;j++)
					System.out.print(row[j]+ " ");
				System.out.println();
				if (i==3) {
					System.out.println("score = " + tabuleiro.getScore());
					System.out.println("highscore =" +tabuleiro.highscore);
				}
			}

			if(tabuleiro.isOver()) {
				System.out.println("GAMEOVER");
				tabuleiro.quit();
			}
			if(tabuleiro.vitoria == true) {
				System.out.println("VICTORY");
				tabuleiro.quit();
			}

			int direcao = in.nextInt();
			switch (direcao) {
			case 0:
				break;
			case 4:
				tabuleiro.moveleft();
				break;
			case 6:
				tabuleiro.moveright();
				break;
			case 2:
				tabuleiro.movedown();
				break;
			case 8:
				tabuleiro.moveup();
				break;
			case 420:
				tabuleiro.quit();
				break;
			}


			//GERAR NUMERO NOVO
			//TODO NAO GERAR NUMERO NOVO QUANDO MOVIMENTO NAO E VALIDO|||| EM PRINCIPIO JÁ NAO GERA TODO TESTING
			boolean flag = true;
			while(flag && !tabuleiro.isFull()) {
				Random rgen = new Random();

				int x = rgen.nextInt(4);
				int y = rgen.nextInt(4);

				if (tabuleiro.matriz[x][y] == 0) {
					tabuleiro.matriz[x][y] = (Math.random() >= .9 ? 4 : 2);
					flag = false;
				}
			}
		}
	}*/

}
