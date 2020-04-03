/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elemental01.tictactoe;

/**
 *
 * @author Gabriel
 */
public class InvalidValueException extends Exception
{
    public InvalidValueException()
    {
        super();
    }
    
    public InvalidValueException(String message)
    {
        super(message);
    }
}
