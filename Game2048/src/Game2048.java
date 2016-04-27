package trabalho1;

/**
 * Created by up201103891 on 2/29/16.
 */

import java.util.Random;
import java.util.Scanner;


public class Game2048 {
	static Matriz tabuleiro;


	public static void main (String[] args)	{
		tabuleiro = new Matriz();
		//Game2048 corre jogo e faz print utilizando o terminal lanterna, controlos setas e escape para terminar
		new VisualizacaoJogo(tabuleiro);
		//testPrint corre jogo e faz print utilizando o standard output, controlos numpad 2 down 4 left 8 up 6 right
		//testPrint();
	}

	
	
	

	/**
	 * Funcao para testar a estrutura matriz e algumas das suas funcionalidades no standard output, sem utlizacao de lanterna
	 */
	static void testPrint() {
		Scanner in = new Scanner(System.in);
		tabuleiro.getHighscore();
		while (true)
		{

			int[] row = new int[4];
			for(int i=0; i<=3;i++) {
				row = tabuleiro.getMatrixRow(i);
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
				tabuleiro.saveHighscore();
			}
			if(tabuleiro.vitoria == true) {
				System.out.println("VICTORY");
				tabuleiro.saveHighscore();
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
				tabuleiro.saveHighscore();
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
	}

}