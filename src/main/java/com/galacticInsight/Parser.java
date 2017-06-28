package com.galacticInsight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser
{
	HashMap<String, String> symbolTable = null;
	HashMap<String, Integer> memoryVariablesTable = null;
	File asmFile = null;
	File hackFile =null;
	int currentLineNumber = 0;
	int currentAvailableMemory = 16;
	//16 bit numberString "1001100100100100";
	
	public Parser(File pAsmFile)
	{
		super();
		this.asmFile = pAsmFile;
		hackFile = new File(hackFileName(asmFile));
		initializeSymbolTable();
		memoryVariablesTable = new HashMap<String, Integer>();
	}
	
	public File parse()
	{		
		populateSymbolTable();
		Scanner fileScanner = null;
		ArrayList<String> toHackFile = new ArrayList<String>();
		try
		{
			fileScanner = new Scanner(asmFile);
			while(fileScanner.hasNext())
			{
				//StringBuffer toWrite = new StringBuffer("");
				String toWrite ="";
				String line = fileScanner.nextLine().trim();
                
                System.out.println("Line to parse " + line);
                if(line.equals("") || line.startsWith("(") || line.startsWith("//"))
                {
                    currentLineNumber--;//You WILL BE DELETED
                    continue;
                }                
				else if(isACommand(line))
				{
					toWrite = createCommandA(line);
					System.out.println("A Command is: "+ toWrite);
				}
				else //if(isCommandC(line))
				{
					toWrite = createCommandC(line);
					System.out.println("C Command is: " + toWrite);
				}
                currentLineNumber++;
				toHackFile.add(toWrite);
			}			
			writetoTheHackFile(toHackFile);				
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally {
			fileScanner.close();
		}		
		return hackFile;
	}	

	private String createCommandC(String line) 
	{       // Dest = Comp ; JMP
		    // Dest or Jump can be empty
    String dest = "";
    int index = 0;
	/*  index  = line.indexOf("=");
		dest = line.substring(0,index).trim();  */
		String command = "";
		String jmp = null;
		boolean containsM;
		boolean HasJumpDirective = line.contains(";");
		boolean hasEqualsSign = line.contains("=");
		    
		if(hasEqualsSign) // Has Comp and dest
		{		    
		    index  = line.indexOf("=");
			dest = line.substring(0,index).trim(); 
		    if(HasJumpDirective)
		    {
			    command =  line.substring(index+1,line.indexOf(";")+1).trim();		
			    jmp = line.substring(line.indexOf(";")+1,line.length()).trim();
		    }
		    else
		    {
		    	jmp = null;
		    	command = line.substring(index+1);
		    }
		}
		else // just a comp and jump
		{
			dest = null;
//			command = line.substring(index+1);//+1, len);
//			command =  line.substring(line.indexOf(";")+1).trim();	
			if(HasJumpDirective)
			{
				command =  line.substring(0,line.indexOf(";")).trim();
				jmp = line.substring(line.indexOf(";")+1).trim();
			}
			else
			{
				command = line;
				jmp = null;
			}
		}
		containsM = command.contains("M");
	    
		System.out.println("Dest String is: " + dest);
		System.out.println("Command String is: " + command);
		System.out.println("Jump String is: " + jmp);		
        System.out.println("Main command for: " + command + " " + getMainCommand(containsM, command));
        System.out.println("Dest command for: " + dest + " " + getDestCommand(dest));
        System.out.println("Jump command for: " + jmp + " " + getJumpCommand(jmp));
		return 111 + getMainCommand(containsM, command) 
					+ getDestCommand(dest) 
					+ getJumpCommand(jmp);
	}

	private String createCommandA(String line) 
	{
		String toReturn = convertToBinary(line.substring(1));
		return toReturn;
	}

	private String convertToBinary(String substring) 
	{
		int value;
		try {
		value = Integer.parseInt(substring);
		}catch(Exception e)
		{
			System.out.println("Error occured with int parsing: " + substring);
			value = getVariablesValue(substring);
		}
		return convertToBinary(value);
	}
	
	private int getVariablesValue(String substring) 
	{
		int toReturn = -1;
		if(symbolTable.containsKey(substring))
		{
			toReturn = Integer.parseInt(symbolTable.get(substring),2);
		}
		else if(memoryVariablesTable.containsKey(substring))
		{
			toReturn = memoryVariablesTable.get(substring);
		}
		else {
			toReturn = currentAvailableMemory++;
			memoryVariablesTable.put(substring, toReturn);
		}
		return toReturn;
	}


	private String convertToBinary(int num)
	{		
//		String s = Integer.toBinaryString(value);
		String binaryString = Integer.toString(num,2);
		//System.out.println("Binary String " + binaryString);

		while(binaryString.length() < 16)
		{
		    binaryString = "0" + binaryString;
		}
		return binaryString;
	}
	
	private boolean isACommand(String line) 
	{
		return line.charAt(0) == '@'? true: false;
		//return line.charAt(0).equals('@')?true:false;
	}

	private void writetoTheHackFile(ArrayList<String> linesToWrite) 
	{
		BufferedWriter output = null;
		try{
			output = new BufferedWriter(new FileWriter(hackFile));
//			output.append(toWrite.toString());
			output.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			if(output !=  null) {try
			{
				output.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}}
		}	
		
		String hackPath = asmFile.getAbsolutePath();
		Path path = Paths.get(hackPath.substring(0,hackPath.length() -4)+".hack"); 
		try
		{
			Files.write(path, linesToWrite, StandardOpenOption.APPEND);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		/*PrintWriter writer = null;
		try
		{path
			writer = new PrintWriter(assemblyFile.getName()+".txt");
			writer.println("The first line"); writer.println("The second line");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		finally {writer.close();}*/
	}

	/**
	 * 	Go through the assembly code once to populate symbol table from the code.
	 */
	private void populateSymbolTable() 
	{
		Scanner fileScanner = null;
		int lineCount = 0;

		try
		{
			fileScanner = new Scanner(asmFile);
			while(fileScanner.hasNext())
			{
				String line = fileScanner.nextLine();
				if(line.equals("")) {continue;}
				if(line.charAt(0) == '(')// might have to escape character this 
				{
					// get sub-string up until the next ')'
					String symbolString =line.substring(line.indexOf("(")+1,line.indexOf(")"));
					// add symbolString to the symbol table
						// convert the lineCount number to its 16 bit binary equivalent.
					symbolTable.put(symbolString, 	convertToBinary(lineCount));
					lineCount--;
				}
				else if(line.startsWith("//"))
				{
					lineCount--;
				}
				lineCount++;
			}			
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally {
			fileScanner.close();
		}		
	}

	private String getDestCommand(String dest)
	{
		String returnString ="";
		if(dest == null)
			{return "000";}
		switch(dest)
		{
		case "M": returnString = "001";break;
		case "D": returnString = "010";break;
		case "MD": returnString = "011";break;
		case "A": returnString = "100";break;
		case "AM": returnString = "101";break;
		case "AD": returnString = "110";break;
		case "AMD": returnString = "111";break;
		}
		return returnString;
	}
	
	private String getMainCommand(boolean hasM, String command)
	{
		String returnString = "";
		if(hasM)
		{
			switch(command)
			{
			case "0": returnString = 	"1101010";break;
			case "1": returnString = 	"1111111";break;
			case "-1": returnString = 	"1111010";break;
			
			case "M": returnString = 	"1110000";break;
			case "!M": returnString= 	"1110001";break;
			case "-M": returnString = 	"1110011";break;
			case "M+1": returnString = 	"1110111";break;
			case "M-1": returnString = 	"1110010";break;
			case "D+M": returnString = 	"1000010";break;
			case "D-M": returnString = 	"1010011";break;
			case "M-D": returnString =  "1000111";break;
			case "D&M": returnString =  "1000000";break;
			case "D|M": returnString =  "1010101";break;
			}
		}
		else
		{
			switch(command)
			{
			case "0": returnString = 	"0101010";break;
			case "1": returnString = 	"0111111";break;
			case "-1": returnString = 	"0111010";break;
			case "D": returnString = 	"0001100";break;
			case "A": returnString = 	"0110000";break;
			case "!D": returnString = 	"0001101";break;
			case "!A": returnString = 	"0110001";break;
			case "-D": returnString = 	"0001111";break;
			case "-A": returnString = 	"0110011";break;
			case "D+1": returnString = 	"0011111";break;
			case "A+1": returnString = 	"0110111";break;
			case "D-1": returnString = 	"0001110";break;
			case "A-1": returnString = 	"0110010";break;
			case "D+A": returnString = 	"0000010";break;
			case "D-A": returnString = 	"0010011";break;
			case "A-D": returnString = 	"0000111";break;
			case "D&A": returnString = 	"0000000";break;
			case "D|A": returnString = 	"0010101";break;
			}
		}
		return returnString;
	}
	
	private String getJumpCommand(String command)
	{
		String returnString = "";
		if(command == null)
			return "000";
		switch(command)
		{
		case "JGT": returnString = "001";break;
		case "JEQ": returnString = "010";break;
		case "JGE": returnString = "011";break;
		case "JLT": returnString = "100";break;
		case "JNE": returnString = "101";break;
		case "JLE": returnString = "110";break;
		case "JMP": returnString = "111";break;
		}
		return returnString;
	}
	
	private void initializeSymbolTable() 
	{
		symbolTable = new HashMap<String, String>();
		symbolTable.put("SP", 	"0000000000000000");	
		symbolTable.put("LCL", 	"0000000000000001");
		symbolTable.put("ARG", 	"0000000000000010");
		symbolTable.put("THIS", "0000000000000011");
		symbolTable.put("THAT", "0000000000000100");
		symbolTable.put("R0", 	"0000000000000000");
		symbolTable.put("R1", 	"0000000000000001");
		symbolTable.put("R2", 	"0000000000000010");
		symbolTable.put("R3", 	"0000000000000011");
		symbolTable.put("R4", 	"0000000000000100");
		symbolTable.put("R5", 	"0000000000000101");
		symbolTable.put("R6", 	"0000000000000110");
		symbolTable.put("R7", 	"0000000000000111");
		symbolTable.put("R8", 	"0000000000001000");
		symbolTable.put("R9", 	"0000000000001001");
		symbolTable.put("R10", 	"0000000000001010");
		symbolTable.put("R11", 	"0000000000001011");
		symbolTable.put("R12", 	"0000000000001100");
		symbolTable.put("R13", 	"0000000000001101");
		symbolTable.put("R14", 	"0000000000001110");
		symbolTable.put("R15", 	"0000000000001111");
	   symbolTable.put("SCREEN","0100000000000000");	// 16384
		symbolTable.put("KBD", 	"0110000000000000");   	// 24576
	}
	
	public String hackFileName(File asmFile)
	{
		String hackName = asmFile.getName();
		String toName = hackName.substring(0,hackName.length()-4);
		return toName +".hack";
	}
}