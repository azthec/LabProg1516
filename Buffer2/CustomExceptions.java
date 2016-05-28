package trabalho2;


/**
 * Caso posicao do cursor seja invalida
 *
 */
class InvalidCursorPosition extends Exception
{
	//http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private static final long serialVersionUID = 1L;
	
      //Parameterless Constructor
      public InvalidCursorPosition() {}

      //Constructor that accepts a message
 }

/**
 * Caso posicao da linha pedida nao exista
 *
 */
class LineInputException extends Exception 
{
		private static final long serialVersionUID = 1L;
		
	      //Parameterless Constructor
	      public LineInputException() {}

	      //Constructor that accepts a message
}