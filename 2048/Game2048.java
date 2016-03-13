

/**
 * Created by up201103891 on 2/29/16.
 */

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;

class Matriz {
    int[][] matriz;
    int score = 0;
    int highscore = 0;

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

    public void vitoria() {
        //TODO VITORIA E PARAR, VITORIA E CONTINUAR
    }

    public void quit() {
        PrintWriter writer = null;
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
        System.exit(0);
    }

    public void getHighscore() {
        //TODO CRIAR FICHEIRO SE NAO EXISTIR OU ERRO
        File file = new File("highscore.txt");
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
}

public class Game2048 {
    private Terminal term;
    private int cursor_x=10, cursor_y=10;
    static Matriz tabuleiro;

    public Game2048() {
        term = TerminalFacade.createTerminal();
        term.enterPrivateMode();
        tabuleiro.getHighscore();
        while (true)
        {
            Key k = term.readInput();
            if (k != null) {
                if(tabuleiro.isOver()) {
                    System.out.println("GAMEOVER");
                    tabuleiro.quit();
                }

                switch (k.getKind()) {
                    case Escape:
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

    private String toStrings(int valor) {
        int sizeValor = String.valueOf(valor).length();
        String linha = new String();
        switch (sizeValor) {
            case 1:
                linha = "    " + valor + " ";
            case 2:
                linha = "   " + valor + " ";
            case 3:
                linha = "  " + valor + " ";
            case 4:
                linha = " " + valor + " ";
        }
        return linha;
    }

    private void show() {
        int cursorpos = 0;
        for (int i=0;i<4;i++) {

            term.moveCursor(0,cursorpos);
            String termLine = "";
            for (int j = 0; j < 4; j++) {


                termLine = toStrings(tabuleiro.matriz[i][j]);


                for (int k = 0; k < termLine.length(); k++) {
                    term.putCharacter(termLine.charAt(k));
                }
            }
            //System.out.printnl();

            /*
            termLine = Arrays.toString(tabuleiro.matriz[i]);
            for(int j=0;j <termLine.length(); j++) {
                term.putCharacter(termLine.charAt(j));
            }

            */
            cursorpos += 1;
        }

    }


    public static void main (String[] args)	{
        tabuleiro = new Matriz();
        new Game2048();
        // testPrint();
    }


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
        }
    }

}
