package com.galacticInsight;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class TestClass
{
	public static void main(String[] args) throws Exception
	{
	System.out.println("Hello world");
	File original = new File("Add.asm");
	System.out.println(original.getName());
	
	String hackName = original.getName();
	String toName = hackName.substring(0,hackName.length()-4);
	PrintWriter writer = new PrintWriter(toName +".hack");
	writer.println("The first line");
	writer.println("The second line");
	writer.close();
	
	System.out.println("Second attempt");
	
	ArrayList<String> linesToWrite = new ArrayList<String>();
	linesToWrite.add("hasdf");
	linesToWrite.add(";lkj");
	linesToWrite.add("fdsa");
	linesToWrite.add("jkl;");
	String hackPath = original.getAbsolutePath();
	Path path = Paths.get(hackPath.substring(0,hackPath.length() -4)+".hack"); 
	try
	{
		Files.write(path, linesToWrite, StandardOpenOption.APPEND);
	} catch (IOException e)
	{
		e.printStackTrace();
	}
	}
}