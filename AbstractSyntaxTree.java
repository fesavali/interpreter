import java.util.*;

abstract class Program { }

class Statement extends Program {

   protected Statement stmt1, stmt2;

   public Statement () { }

   public Statement (Statement stmt1, Statement stmt2) {
     this . stmt1 = stmt1;
     this . stmt2 = stmt2;
   }

   public String toString () {
     if (stmt1 == null)
       return "()";
     else if (stmt2 == null)
       return "(" + stmt1 + ")";
     else
       return "(: " + stmt1 + " " + stmt2 + ")";
   }

}

class Assignment extends Statement {

   protected Identifier lhs;
   protected Expression rhs;

   public Assignment () { }

   public Assignment (Identifier lhs, Expression rhs) {
     this . lhs = lhs;
     this . rhs = rhs;
   }

   public String toString () {
     return "(<- " + lhs + " " + rhs + ")";
   }

}

class IfStatement extends Statement {

  protected Expression test;
  protected Statement thenStmt;
  protected Statement elseStmt;

  public IfStatement () { }

  public IfStatement (Expression test, Statement thenStmt) {
    this . test = test;
    this . thenStmt = thenStmt;
  }

  public IfStatement (Expression test, Statement thenStmt, Statement elseStmt) {
    this (test, thenStmt);
    this . elseStmt = elseStmt;
  }

  public String toString () {
     if (elseStmt == null)
       return "(if " + test + " " + thenStmt + ")";
     else
       return "(if " + test + " " + thenStmt + elseStmt + ")";
   }

}

class PrintStatement extends Statement {

   protected Expression exp;

   public PrintStatement () { }

   public PrintStatement (Expression exp) {
     this . exp = exp;
   }

   public String toString () {
     return "(print " + exp + ")";
   }

}

class ReturnStatement extends Statement {

   protected Expression exp;

   public ReturnStatement () { }

   public ReturnStatement (Expression exp) {
     this . exp = exp;
   }

   public String toString () {
     return "(return " + exp + ")";
   }

}

class WhileStatement extends Statement {

  protected Expression test;
  protected Statement body;

  public WhileStatement () { }

  public WhileStatement (Expression test, Statement body) {
    this . test = test;
    this . body = body;
  }

  public String toString () {
     return "(while " + test + " " + body + ")";
   }

}

abstract class Expression { }

class Identifier extends Expression {

  protected String id;

  public Identifier () { }

  public Identifier (String id) {
    this . id = id;
  }

  public String toString () {
    return "(id " + id + ")";
  }

}

class IntValue extends Expression {

  protected int intValue;

  public IntValue () { }

  public IntValue (int intValue) {
    this . intValue = intValue;
  }

  public IntValue (String intValue) {
    this . intValue = Integer . parseInt (intValue);
  }

  public String toString () {
    return "(integer " + intValue + ")";
  }

}

class Readline extends Expression {

  public Readline () { }

  public String toString () {
    return "(readline)";
  }

}

class Null extends Expression {

  public Null () { }

  public String toString () {
    return "(null)";
  }

}

class Unary extends Expression {

  protected String op;
  protected Expression exp;

  public Unary () { }

  public Unary (String op, Expression exp) {
    this . op = op;
    this . exp = exp;
  }

  public String toString () {
    return "(" + op + " " + exp + ")";
  }

}

class Binary extends Expression {

  protected String op;
  protected Expression exp1, exp2;

  public Binary () { }

  public Binary (String op, Expression exp1, Expression exp2) {
    this . op = op;
    this . exp1 = exp1;
    this . exp2 = exp2;
  }

  public String toString () {
    return "(" + op + " " + exp1 + " " + exp2 + ")";
  }

}

class FunctionCall extends Expression {

  protected String id;
  protected ArrayList <Expression> actualParameters;

  public FunctionCall () { }

  public FunctionCall (String id, ArrayList <Expression> actualParameters) {
    this . id = id;
    this . actualParameters = actualParameters;
  }

  public String toString () {
    String actualParameterList = "";
    Iterator <Expression> actualParameterIterator =
      actualParameters . iterator ();
    while (actualParameterIterator . hasNext ()) {
      Expression actualParameter = actualParameterIterator . next ();
      if (actualParameterList . equals (""))
        actualParameterList = actualParameterList + actualParameter;
      else
        actualParameterList = actualParameterList + " " + actualParameter;
    }
    return "(apply " + id + " (" + actualParameterList + "))";
  }

} 
