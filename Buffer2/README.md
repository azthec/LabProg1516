# Laboratório Programação FCUP 2015/2016

Rui Miguel & Rui Balau

Buffer.java
	Ficheiro principal
	
TestCase.java
	Ficheiro de testes JUnit
	
CustomExceptions.java
	Ficheiro com classes para custom errors

FileBuffer.java
	Ficheiro para gravar / ler de ficheiros, incompleto e nao avaliado na parte 1 do trabalho.
	

BufferView

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