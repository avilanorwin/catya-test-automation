/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.shell.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.catya.interpreter.DSLInterpreter;
import com.catya.interpreter.IInterpreter;
import com.catya.interpreter.exception.SyntaxException;

public class Shell 
{
    public static void main( String[] args ) throws IOException
    {
    	IInterpreter interpreter = new DSLInterpreter();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	String command = "";

    	System.out.println();
    	System.out.println("Welcome to CATYA shell!");
    	System.out.println("CATYA Prototype 0.01");
    	System.out.println();
    	
    	
        while (command != null && !command.equalsIgnoreCase("quit")) {
        	System.out.print("catya > ");
			command = reader.readLine();
			
			if (command != null && command.length() > 0) {
				try {
					interpreter.interpretCommand(command, 0);
				} catch (SyntaxException e) {
					System.out.println("Syntax error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("Error encountered: " + e.getMessage());
				}
			}
        }
    }
}
