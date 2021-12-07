// MicroRAST.java

// This program is a recursive descent parser for MicroR.  The abstract syntax 
// tree representation for the program is constructed.

public class MicroRAST {

  public static void main (String args []) throws java.io.IOException {
    System . out . println ("MyInterpreter.java");
    System . out . println ("--------------");
    System . out . println ();

    ParserAST microR = new ParserAST ();
    Program program = microR . program ();
  }

}
