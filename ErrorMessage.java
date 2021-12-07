// ErrorMessage class
// This class prints error messages.

public class ErrorMessage {

  public static void print (String message) {
    System . out . println ("***** Error: " + message + "Missing closing parenthesis for function.");
    System . exit (0);
  }
  public static void print1 (String message) {
    System . out . println ("***** Error: " + message + "Unknown function on input.");
    System . exit (0);
  }

  public static void print (int position, String message) {
    System . out . println ();
    for (int i = 0; i < position; i++) 
      System . out . print (" ");
    System . out . println ("^");
    print (message);
  }

}
