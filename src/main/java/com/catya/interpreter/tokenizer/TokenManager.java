/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.tokenizer;

public class TokenManager {
	private String command;
	private int index;
	private TokenType tokenType;
	
	public TokenManager(String command) {
		this.command = command.trim();
	}
	
	public String getToken() {
		
		String nextToken = null;
		char c;
		
		if (index >= command.length())
			return nextToken;
		
		
		//strip whitespaces.
		while ((c = command.charAt(index)) == ' ' || c == '\t') 
			index++;
		
		nextToken = "";
		
		if ("=$".contains(String.valueOf(command.charAt(index)))) {
			nextToken += command.charAt(index);
			tokenType = TokenType.DELIMITER;
			index++;
		} else if (command.charAt(index) == '"' || command.charAt(index) == '\'') {
			char endQuote = command.charAt(index);
			index++;
			while (index < command.length() && command.charAt(index) != endQuote) {
				nextToken += command.charAt(index++);
				tokenType = TokenType.QUOTED;
			}
			index++;
		} else {
			while (index < command.length() 
				&& !Character.isWhitespace(command.charAt(index))
				&& command.charAt(index) != '=') {
				nextToken += command.charAt(index++);
				tokenType = getTokenTypeFromString(nextToken);
			}
		}
		
		return nextToken;
	}
	
	public TokenType getTokenType() {
		return tokenType;
	}
	
	private TokenType getTokenTypeFromString(String token) {
		TokenType t = TokenType.OTHER;
		
		for (TokenType tokenType: TokenType.values()) {
			if (tokenType.string().equalsIgnoreCase(token)) {
				t = tokenType;
				break;
			}
		}
		
		return t;
	}
}