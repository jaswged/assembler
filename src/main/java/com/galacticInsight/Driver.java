package com.galacticInsight;

import java.io.File;

public class Driver
{
	public static void main(String[] args)
	{
		System.out.println("Hello world");
		File assemblyFile = new File(args[0]);//new File("Rect.asm");
		Parser parser = new Parser(assemblyFile);
		parser.parse();
		System.out.println("Assembling completed");
	}
}