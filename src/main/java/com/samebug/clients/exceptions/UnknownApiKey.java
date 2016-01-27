package com.samebug.clients.exceptions;

public class UnknownApiKey extends Exception {
    public UnknownApiKey(String apiKey) {
        super("Unknown API Key: "+ apiKey+ ".");
    }
}
