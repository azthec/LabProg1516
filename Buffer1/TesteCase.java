import static org.junit.Assert.*;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class TesteCase {
    private Buffer buffer;
    private boolean expected;
    
    /**
     * Inicia o buffer a ser utilizado para cada teste
     * @throws LineInputException 
     */
    @Before
    public void Initialize() throws LineInputException{
        buffer = new Buffer();
        buffer.insertString("primeira linha");
        buffer.breakLine();
        buffer.insertString("segundalinha");
        buffer.breakLine();
        buffer.insertString("terceiralinha!\\");
        buffer.breakLine();
        buffer.insertString("");
    }
    
    /**
     * Verifica que a funcao insertString nao faz parse a varios \n (ao contrario do buffer inicializado com string)
     * @throws LineInputException
     */
    @Test (expected = LineInputException.class)
    public void testLineInvalidString() throws LineInputException {
    	buffer.insertString("asd\n\nasd\n\nasd");
    }

    /**
     * Verifica que a funcao getLine no Initialize esta a fazer parse correcto
     */
    @Test
    public void testInitialize() {
    	assertEquals("primeira linha", buffer.getLine(0));
    	assertEquals("segundalinha", buffer.getLine(1));
    	assertEquals("terceiralinha!\\", buffer.getLine(2));
    	assertEquals("", buffer.getLine(3));
    }
    
    /**
     * Verifica que buffer inicializado com string faz parse correcto aos \n
     */
    @Test
    public void testBufferString() {
    	buffer = new Buffer("primeira linha\nsegundalinha\nterceiralinha!\\\n");
    	assertEquals("primeira linha", buffer.getLine(0));
    	assertEquals("segundalinha", buffer.getLine(1));
    	assertEquals("terceiralinha!\\", buffer.getLine(2));
    }
    
    /**
     * Verifica se estamos a garantir que caso nada seja introduzido temos sempre pelo menos uma linha na estrutura
     */
    @Test
    public void testBufferStringEmpty() {
    	buffer = new Buffer("");
    	assertEquals("", buffer.getLine(0));
    }
    
    /**
     * Verifica que se o caracter a inserir for \n cria uma nova linha, caso contrario insere o na posicao do cursor.
     * @throws LineInputException 
     */
    @Test
    public void testinsertCharacter() throws LineInputException {
    	assertEquals("", buffer.getLine(3));
    	buffer.insert('s');
    	assertEquals("s", buffer.getLine(3));
    	buffer.breakLine();
    	assertEquals("", buffer.getLine(4));
    }
    
    /**
     * Verifica que a funcao getLine retorna o erro devido com posicoes invalidas
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testeGetLineInvalidPos(){
        assertEquals(buffer.getLine(1000), "linha nao existe");
    }
    
    /**
     * Coloca o cursor numa posicao invalida (negativa) e numa posicao invalida (positiva) e esta a espera de um erro
     * @throws InvalidCursorPosition 
     */
    @Test (expected = InvalidCursorPosition.class)
    public void testSetCursorInvalidPos() throws InvalidCursorPosition {
			buffer.setCursor(-1,-5);
			buffer.setCursor(100, 100);
    }

    /**
     * Verifica se a posicao ser valida ou nao corresponde a funcao
     */
    @Test
    public void testvalidPosition() {
    	assertEquals(buffer.validPosition(-1, 0), false);
    	assertEquals(buffer.validPosition(0, -1), false);
    	assertEquals(buffer.validPosition(100, 0), false);
    	assertEquals(buffer.validPosition(0, 100), false);
    	assertEquals(buffer.validPosition(0, 0), true);
    }
    
    
    /**
     * Verifica que o cursor se mexe para baixo ate chegar a ultima linha
     */
    @Test
    public void testmoveDown(){
        try {
			buffer.setCursor(0,14);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(buffer.getcursorCol(),14);
        buffer.moveDown();
        assertEquals(buffer.getcursorCol(),12);
        assertEquals(buffer.getcursorRow(),1);
        buffer.moveDown();
        assertEquals(buffer.getcursorCol(),12);
        assertEquals(buffer.getcursorRow(),2);
        buffer.moveDown();
        assertEquals(buffer.getcursorCol(),0);
        assertEquals(buffer.getcursorRow(),3);
        buffer.moveDown();
        buffer.moveDown();
        assertEquals(buffer.getcursorCol(),0);
        assertEquals(buffer.getcursorRow(),3);
    }

    /**
     * Verifica que o cursor se mexe para cima ate chegar a primeira linha
     */
    @Test
    public void testmoveUp(){
        try {
			buffer.setCursor(2,14);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(buffer.getcursorCol(),14);
        assertEquals(buffer.getcursorRow(),2);
        buffer.moveUp();
        assertEquals(buffer.getcursorCol(),12);
        assertEquals(buffer.getcursorRow(),1);
        buffer.moveUp();
        assertEquals(buffer.getcursorCol(),12);
        assertEquals(buffer.getcursorRow(),0);
        buffer.moveUp();
        buffer.moveUp();
        buffer.moveUp();
        assertEquals(buffer.getcursorCol(),12);
        assertEquals(buffer.getcursorRow(),0);
    }

    /**
     * Verifica que quando o cursor se mexe para a direita e nao esta no fim do ficheiro 
     * ou vai para o proximo caracter ou para o inicio da proxima linha
     */
    @Test
    public void testmoveRight(){
        try {
			buffer.setCursor(1,11);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(buffer.getcursorCol(),11);
        buffer.moveRight();
        assertEquals(buffer.getcursorRow(),1);
        assertEquals(buffer.getcursorCol(),12);
        buffer.moveRight();
        assertEquals(buffer.getcursorRow(),2);
        try {
			buffer.setCursor(3,0);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        buffer.moveRight();
        assertEquals(buffer.getcursorCol(),0);
        assertEquals(buffer.getcursorRow(),3);
        
    }
    
    /**
     * Verifica que quando o cursor se mexe para a esquerda e nao esta no incio do ficheiro
     * ou vai para o caracter anterior ou para o final da linha anterior
     */
    @Test
    public void testmoveLeft(){
        try {
			buffer.setCursor(1,2);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       buffer.moveLeft();
       assertEquals(buffer.getcursorCol(),1);
       assertEquals(buffer.getcursorRow(),1);
       buffer.moveLeft();
       assertEquals(buffer.getcursorCol(),0);
       buffer.moveLeft();
       assertEquals(buffer.getcursorCol(),14);
       assertEquals(buffer.getcursorRow(),0);
       try {
			buffer.setCursor(0,0);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       assertEquals(buffer.getcursorCol(),0);
       assertEquals(buffer.getcursorRow(),0);
    }

    /**
     * Verifica que elimina caracteres correctamente e move o restante da string a direita do cursor para o final da linha anterior
     * se o cursor estiver na coluna 0, e que nao elimina nada se estiver no inicio do ficheiro
     */
    @Test
    public void testDelete(){
        try {
			buffer.setCursor(0,0);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        buffer.delete();
        assertEquals(buffer.getLine(0),"primeira linha");
        assertEquals(buffer.getcursorCol(),0);
        assertEquals(buffer.getcursorRow(),0);
        buffer.moveRight();
        buffer.moveRight();
        buffer.delete();
        assertEquals(buffer.getLine(0),"pimeira linha");
        
        try {
			buffer.setCursor(1,0);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(buffer.getLine(1),"segundalinha");
        buffer.delete();
        assertEquals(buffer.getLine(0),"pimeira linhasegundalinha");
    }

    /**
     * Verifica que gera uma nova linha abaixo da linha corrente do cursor com o restante da string a direita do cursor
     * (o que pode ser nada)
     */
    @Test
    public void testbreakLine(){
        try {
			buffer.setCursor(0,10);
		} catch (InvalidCursorPosition e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        buffer.breakLine();
        assertEquals(buffer.getcursorRow(),1);
        assertEquals(buffer.getcursorCol(),0);
        assertEquals(buffer.getLine(1),"inha");
    }

    /**
     * Testa numero de linhas, ter atencao que index das linhas comeca no 0 e que o numero de linhas comeca a contar no 1
     */
    @Test
    public void testgetNLines() {
    	assertEquals(buffer.getNLines(),4);
    }
}
