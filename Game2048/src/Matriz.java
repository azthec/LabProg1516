package trabalho1;

/**
 * Created by up201103891 on 3/10/16.
 */

import java.io.*;
import java.util.Random;



/**
 * Classe modelo de jogo, como pedido independente da visualização, (com algumas alteracoes na main), possivel testar algumas funcionalidades com testprint
 */
public class Matriz {
	int[][] matriz;																										//tabuleiro de jogo em si
	int score = 0;																										//pontuacao no jogo corrente
	int highscore = 0;																									//melhor pontuacao ao longo dos jogos, lida de ficheiro txt pela funcao getHighScore
	boolean vitoria = false;																							//condicao de vitoria, alterada assim que um 2048 é obtido

	Matriz() {																											//construtor do tabuleiro
		matriz = new int[4][4];
		Random rgen = new Random();

		// gera 2 posicoes aleatorias para os 2 primeiros numeros
		int x = rgen.nextInt(4);
		int y = rgen.nextInt(4);
		int z = rgen.nextInt(4);
		int w = rgen.nextInt(4);

		// gerar 2 com 90% de probabilidade e 4 com 10%
		// caso ambos os pares de randoms sejam iguais, gera um par novo
		if (x == z && y == w) {
			while (x == z && y == w) {
				y = rgen.nextInt(4);
				w = rgen.nextInt(4);
			}
			//This expression produces a random number 0 <= x < 1, then if x is between 0 and 0.9 (i.e. 90% probability), outputs 2, otherwise outputs 4 (10% probability).
			//http://stackoverflow.com/questions/28800349/random-number-in-random-cell-for-2048-game
			matriz[z][w] = (Math.random() >= .9 ? 4 : 2);
		} else {
			matriz[x][y] = (Math.random() >= .9 ? 4 : 2);
			matriz[z][w] = (Math.random() >= .9 ? 4 : 2);
		}
	}

	public int getScore() {
		return score;
	}


	/**
	 * encosta numeros a esquerda, se duas casas consecutivas são iguais sao somadas e volta a encostar a esquerda
	 */
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

	/**
	 * funcao para encostar a esquerda
	 */
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
		rotateRight();
		rotateRight();
		moveleft();
		rotateRight();
		rotateRight();
	}

	void movedown() {
		rotateRight();
		moveleft();
		rotateLeft();
	}

	void moveup() {
		rotateLeft();
		moveleft();
		rotateRight();
	}

	/**
	 * Roda matriz no sentido dos ponteiros do relógio
	 * Adaptado de http://stackoverflow.com/a/26374543/2559455
	 */
	private void rotateRight() {
		int[][] rotated = new int[4][4];
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				rotated[c][3 - r] = matriz[r][c];
			}
		}
		matriz = rotated;
	}

	/**
	 * Roda matriz no sentido oposto dos ponteiros do relógio
	 * Adaptado de http://stackoverflow.com/a/26374543/2559455
	 */
	private void rotateLeft() {
		int[][] rotated = new int[4][4];
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				rotated[3 - c][r] = matriz[r][c];
			}
		}
		matriz = rotated;
	}

	/** Retorna uma dada linha i da matriz
	 * 
	 * @param i
	 * @return matriz[i]
	 */
	int[] getMatrixRow(int i) {
		return matriz[i];
	}

	/** Verifica se o jogo está perdido, vendo se o tabuleiro está cheio e se já não existem movimentos possiveis
	 * 
	 */
	public boolean isOver() {
		return isFull() && !isMoveAllowed();
	}

	/** verifica se o tabuleiro está cheio
	 * 
	 */
	public boolean isFull() {
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 3; j++) {
				if (matriz[i][j] == 0) return false;
			}
		}
		return true;
	}


	/** Verifica se existem movimentos possiveis
	 * 
	 */
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

	/** Funcao a ser executada antes de terminar programa, compara score e highscore guarda num ficheiro comforme aplicavel
	 * 
	 */
	public void saveHighscore() {
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


	/** Funcao a ser executada no inicio do programa, vai buscar highscore a ficheiro
	 * 
	 */
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