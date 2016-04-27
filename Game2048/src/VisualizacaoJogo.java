package trabalho1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

//TALVEZ SEJA POSSIVEL CONTROLAR TAMANHO DA JANELA http://grepcode.com/file/repo1.maven.org/maven2/com.googlecode.lanterna/lanterna/2.1.6/com/googlecode/lanterna/terminal/TerminalSize.java#TerminalSize


/** Classe de visualizacao, usa biblioteca lanterna
* 
* @author
*
*/

class VisualizacaoJogo {
	private Terminal term;																									//janela terminal
	private int cursor_x=10, cursor_y=10;																					//variaveis geralmente utilizadas para controlar posicao do cursor
	Matriz tabuleiro;																										//estrutura tabuleiro, recebida da main

	public VisualizacaoJogo(Matriz tabuleiro) {																				//construtor da visualizacao, cria o terminal e le input
		this.tabuleiro = tabuleiro;
		term = TerminalFacade.createTerminal();																				//cria janela em si
		term.enterPrivateMode();
		term.setCursorVisible(false);																						//esconde blinking cursor do utilizador
		tabuleiro.getHighscore();																							//obtem highscore do ficheiro highscore.txt
		show();																												//desenha estado inicial da matriz apos incializacao
		while (true)
		{
			Key k = term.readInput();																						//le input no terminal
			if (k != null) {
	
				if(tabuleiro.vitoria==true) {																				//caso condicao vitoria tenha sido atingida
					tabuleiro.saveHighscore();																				//guarda o highscore (caso o current score seja maior que o highscore no ficheiro)
					term.clearScreen();																						
					printWin();																								//mostra ecra de vitoria
					try {																									//espera 10 segundos e fecha o terminal
						Thread.sleep(10000);                 																//1000 milliseconds is one second.
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					term.exitPrivateMode();
					System.exit(0);
				}
				
				if(tabuleiro.isOver()) {																					//caso condicao de derrota tenha sido atingida
					tabuleiro.saveHighscore();																				//guarda o highscore (caso o current score seja maior que o highscore no ficheiro)
					term.clearScreen();
					printLoss();																							//mostra ecra de derrota
					try {																									//espera 10 segundos e fecha o terminal
						Thread.sleep(10000);                																//1000 milliseconds is one second.
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					term.enterPrivateMode();
					System.exit(0);
				}

				
				switch (k.getKind()) {																						//le as 5 teclas distintas que o utilizador pode introduzir
				case Escape:																								//grava score e termina
					tabuleiro.saveHighscore();
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
				default:																									//caso a tecla introduzida seja invalida volta a espera pelo proximo input
					continue;
				}

				//GERAR NUMERO NOVO
				boolean flag = true;																						//gera um 2 e coloca numa posicao ao calhas cada jogado, dado que exista espaco
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

	}

	/** Mostra ecra de derrota, le ascii art de um ficheiro.
	 * 
	 */
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

	/** Mostra ecra de vitoria, le ascii art de um ficheiro.
	 * 
	 */
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

	
	/**	recebe ints e converte para string
	 * 
	 * @param valor							numero de uma dada posicao da matriz
	 * @return	valor em string
	 */
	private String toStrings(int valor) {
		//int sizeValor = String.valueOf(valor).length();
		String linha = new String();
		linha = "" + valor;
		return linha;
	}

	
	/**	Pinta linha horizontal no terminal com a cor que recebe, tem de imprimir algum caracter, caso contrario nao altera a cor na posicao do cursor, dai imprimir espaços
	 * 
	 * @param x								posicao corrente x do cursor
	 * @param y								posicao corrente y do cursor
	 * @param length						distancia (horizontal) para colorir desde o ponto (x,y)
	 * @param color							com que cor colorir
	 */
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

	/** recebe um int 0 ou 2^1 até 2^11 e retorna uma cor a utilizar no terminal para este caracter
	 * 
	 * @param valor
	 * @return 
	 */
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


	/**
	 * imprime art ascii no topo do terminal durante jogo
	 */
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

	
	/**
	 * funcao principal da visualizacao no terminal
	 */
	private void show() {
		term.applyForegroundColor(Terminal.Color.CYAN);																//applyForegroundcolor altera a cor das letras
		printTitle();																								//applyBackgroundColor altera a cor do background das letras
		term.applyForegroundColor(Terminal.Color.DEFAULT);															
		int cursorposx = 20;																						//varival geralmente usada para coordenada x do cursor no terminal
		int cursorposy = 9;																							//varival geralmente usada para coordenada y do cursor no terminal


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

		
		
		for (int i=0;i<4;i++) {																						//colour and print game board
			term.moveCursor(cursorposx,cursorposy);
			String termLine = "";
			for (int j = 0; j < 4; j++) {
				termLine = toStrings(tabuleiro.matriz[i][j]);
				colourItUp(cursorposx,cursorposy,6,colourPicker(tabuleiro.matriz[i][j]));							//define a cor do terminal, utilizando como parametro uma outra funcao que indica a cor a utilizar
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
}

