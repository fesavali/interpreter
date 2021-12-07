// ParserAST class
// ParserAST is a class to represent a recursive descent parser for the 
// MicroR programming language which constructs an abstract syntax tree.

import java.io.*;
import java.util.*;

public class ParserAST {

  protected MicroRLexer lexer;	// lexical analyzer
  protected Token token;	// current token

  public ParserAST () throws IOException {
    lexer = new MicroRLexer (new InputStreamReader (System . in));
    getToken ();
  }

  private void getToken () throws IOException { 
    token = lexer . nextToken (); 
  }

  // Program ::= source ( “List.R” ) {FunctionDef} MainDef

  public Program program () throws IOException {
    Statement mainBody;
    if (token . symbol () != Symbol . SOURCE) 	// source
      ErrorMessage . print (lexer . position (), "source EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . LPAREN) 	// (
      ErrorMessage . print (lexer . position (), "( EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . LISTR) 	// "List.R"
      ErrorMessage . print (lexer . position (), "\"List.R\" EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . RPAREN ) 	// )
      ErrorMessage . print (lexer . position (), ") EXPECTED");
    getToken ();
    while (token . symbol () == Symbol . ID) 	// {FunctionDef}
      functionDef ();
    mainBody = mainDef ();               	// MainDef
    System . out . println ();
    System . out . println ("Abstract Syntax Tree for main");
    System . out . println ("-----------------------------");
    System . out . println ();
    System . out . println (mainBody);
    if (token . symbol () != Symbol . EOF) 
      ErrorMessage . print (lexer . position (), "END OF PROGRAM EXPECTED");
    return mainBody;
  }

  // FunctionDef ::= 
  //     id <− function ( [id {, id }] ) "{" {Statement} return ( Expr ) ; "}"

  public void functionDef () throws IOException {
    Statement functionBody, returnStatement;
    if (token . symbol () != Symbol . ID)        	// id
      ErrorMessage . print (lexer . position (), "id EXPECTED");
    String functionId = token . lexeme ();
    getToken ();
    if (token . symbol () != Symbol . ASSIGN)     	// <-
      ErrorMessage . print (lexer . position (), "<- EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . FUNCTION)  	// function
      ErrorMessage . print (lexer . position (), "function EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . LPAREN)     	// (
      ErrorMessage . print (lexer . position (), "( EXPECTED");
    getToken ();
    if (token . symbol () == Symbol . ID) {     	// [ id
      getToken ();
      while (token . symbol () == Symbol . COMMA) {	// { ,
        getToken ();
        if (token . symbol () != Symbol . ID)      	// id
          ErrorMessage . print (lexer . position (), "id EXPECTED");
        getToken ();
      }                                          	// }
    }                                             	// ]
    if (token . symbol () != Symbol . RPAREN)   	// )
        ErrorMessage . print (lexer . position (), ") EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . LBRACE)      	// "{"
      ErrorMessage . print (lexer . position (), "{ EXPECTED");
    getToken ();
    functionBody = null;
    while (token . symbol () == Symbol . IF        	// { 
        || token . symbol () == Symbol . WHILE
        || token . symbol () == Symbol . ID
        || token . symbol () == Symbol . PRINT) { 
      if (functionBody == null)                   	// Statement 
        functionBody = statement ();
      else
        functionBody = new Statement (functionBody, statement ());
    }                                            	// }
    if (token . symbol () != Symbol . RETURN)    	// return
      ErrorMessage . print (lexer . position (), "return EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . LPAREN)    	// (
      ErrorMessage . print (lexer . position (), "( EXPECTED");
    getToken ();
    Expression returnExp = expr ();                    	// Expr
    functionBody = 
      new Statement (functionBody, new ReturnStatement (returnExp));
    if (token . symbol () != Symbol . RPAREN)    	// )
      ErrorMessage . print (lexer . position (), ") EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . SEMICOLON)    	// ;
      ErrorMessage . print (lexer . position (), "; EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . RBRACE)    	// "}"
      ErrorMessage . print (lexer . position (), "} EXPECTED");
    System . out . println ();
    System . out . println ();
    System . out . println ("Abstract Syntax Tree for " + functionId);
    System . out . print   ("-------------------------");
    for (int i = 0; i < functionId . length (); i++)
      System . out . print ("-");
    System . out . println ();
    System . out . println ();
    System . out . println (functionBody);
    getToken ();
  }

  // MainDef ::= main < − function ( ) "{" StatementList "}"

  public Statement mainDef () throws IOException {
    if (token . symbol () != Symbol . MAIN)   	// main
      ErrorMessage . print (lexer . position (), "main EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . ASSIGN)  	// <-
      ErrorMessage . print (lexer . position (), "<- EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . FUNCTION) // function
      ErrorMessage . print (lexer . position (), "function EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . LPAREN) 	// (
      ErrorMessage . print (lexer . position (), "( EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . RPAREN) 	// )
      ErrorMessage . print (lexer . position (), ") EXPECTED");
    getToken ();
    if (token . symbol () != Symbol . LBRACE) 	// "{"
      ErrorMessage . print (lexer . position (), "{ EXPECTED");
    getToken ();
    Statement mainBody = statementList ();     	// StatementList
    if (token . symbol () != Symbol . RBRACE) 	// "}"
      ErrorMessage . print (lexer . position (), "} EXPECTED");
    getToken ();
    return mainBody;
  }

  // StatementList ::= Statement { Statement }

  public Statement statementList () throws IOException {
    Statement stmt, stmt1;
    stmt = statement ();                     	// Statement
    while (token . symbol () == Symbol . IF  	// {
        || token . symbol () == Symbol . WHILE
        || token . symbol () == Symbol . ID
        || token . symbol () == Symbol . PRINT) {
      stmt1 = statement ();                    	// Statement }
      stmt = new Statement (stmt, stmt1);
    }
    return stmt;
  }

  // Statement ::= if ( Cond ) { StatementList } [else { StatementList }]
  //   | while ( Cond ) { StatementList }
  //   | id < − Expr ;
  //   | print ( Expr ) ;

  public Statement statement () throws IOException {
    Expression exp;
    Statement stmt = null, stmt1, stmt2;

    switch (token . symbol ()) {

      case IF :                                  	// if
        getToken ();
        if (token . symbol () != Symbol . LPAREN)   	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        exp = cond ();                                 	// Cond
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . LBRACE) 	// "{"
          ErrorMessage . print (lexer . position (), "{ EXPECTED");
        getToken ();
        stmt1 = statementList ();                      	// StatementList
        if (token . symbol () != Symbol . RBRACE) 	// "}"
          ErrorMessage . print (lexer . position (), "} EXPECTED");
        getToken ();
        if (token . symbol () == Symbol . ELSE) {  	// [ else
          getToken ();
          if (token . symbol () != Symbol . LBRACE) 	// "{"
            ErrorMessage . print (lexer . position (), "{ EXPECTED");
          getToken ();
          stmt2 = statementList ();                    	// StatementList
          if (token . symbol () != Symbol . RBRACE) 	// "}"
            ErrorMessage . print (lexer . position (), "} EXPECTED");
          getToken ();
        }                                        	// ]
        else 
          stmt2 = null;
        stmt = new IfStatement (exp, stmt1, stmt2);
        break;

      case WHILE :                                  	// while
        getToken ();
        if (token . symbol () != Symbol . LPAREN)   	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        exp = cond ();                                 	// Cond
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . LBRACE) 	// "{"
          ErrorMessage . print (lexer . position (), "{ EXPECTED");
        getToken ();
        stmt1 = statementList ();                     	// StatementList
        if (token . symbol () != Symbol . RBRACE) 	// "}"
          ErrorMessage . print (lexer . position (), "} EXPECTED");
        getToken ();
        stmt = new WhileStatement (exp, stmt1);
        break;

      case ID :                                  	// id
        Identifier id = new Identifier (token . lexeme ());
        getToken ();
        if (token . symbol () != Symbol . ASSIGN)  	// <-
          ErrorMessage . print (lexer . position (), "<- EXPECTED");
        getToken ();
        exp = expr ();                                 	// Expr
        if (token . symbol () != Symbol . SEMICOLON)  	// ; 
          ErrorMessage . print (lexer . position (), "; EXPECTED");
        stmt = new Assignment (id, exp);
        getToken ();
        break;

      case PRINT :                                  	// print
        getToken ();
        if (token . symbol () != Symbol . LPAREN)  	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        exp = expr ();                                 	// Expr
        if (token . symbol () != Symbol . RPAREN) 	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . SEMICOLON)  	// ;
          ErrorMessage . print (lexer . position (), "; EXPECTED");
        stmt = new PrintStatement (exp);
        getToken ();
        break;

      default :
        ErrorMessage . print (lexer . position (), "UNRECOGNIZABLE SYMBOL");

    }
    return stmt;
  }

  // Cond ::= AndExpr {|| AndExpr}

  public Expression cond () throws IOException {
    Expression exp, exp1;
    exp = andExpr ();                          	// AndExpr
    while (token . symbol () == Symbol . OR) { 	// { ||
      getToken ();
      exp1 = andExpr ();                       	// AndExpr }
      exp = new Binary ("||", exp, exp1);
    }
    return exp;
  }

  // AndExpr ::= RelExpr {&& RelExpr}

  public Expression andExpr () throws IOException {
    Expression exp, exp1;
    exp = relExpr ();                          	// relExpr
    while (token . symbol () == Symbol . AND) { // { &&
      getToken ();
      exp1 = relExpr ();                       	// relExpr }
      exp = new Binary ("&&", exp, exp1);
    }
    return exp;
  }

  // RelExpr ::= [!] Expr RelOper Expr

  public Expression relExpr () throws IOException {
    Expression exp1, exp2 = null;
    String op = null;
    if (token . symbol () == Symbol . NOT) { 	// !
      getToken ();
      exp1 = expr ();                         	// Expr
      exp1 = new Unary ("!", exp1);
    }
    else
      exp1 = expr ();                         	// Expr
    switch (token . symbol ()) {
      case LT :                          	// <
      case LE :                          	// <=
      case GT :                          	// >
      case GE :                         	// >=
      case EQ :                         	// ==
      case NE :                         	// !=
        op = token . lexeme ();
        getToken ();
        exp2 = expr ();                        	// Expr
	break;
      default : 
        ErrorMessage . print (lexer . position (), "UNRECOGNIZABLE SYMBOL");
        break;
    }
    return new Binary (op, exp1, exp2);
  }

  // Expr ::= MulExpr {AddOper MulExpr}
  // AddOper ::= + | −

  public Expression expr () throws IOException {
    Expression exp, exp1;
    exp = mulExpr ();                                 	// MulExpr
    while (token . symbol () == Symbol . PLUS   	// { +
	|| token . symbol () == Symbol . MINUS) {      	//   -
      String op = token . lexeme ();
      getToken ();
      exp1 = mulExpr ();                               	// MulExpr }
      exp = new Binary (op, exp, exp1);
    }
    return exp;
  }

  // MulExpr ::= PrefixExpr {MulOper PrefixExpr}
  // MulOper ::= * | /

  public Expression mulExpr () throws IOException {
    Expression exp, exp1;
    exp = prefixExpr ();                               	// PrefixExpr
    while (token . symbol () == Symbol . TIMES  	// { *
        || token . symbol () == Symbol . SLASH) {  	//   /
      String op = token . lexeme ();
      getToken ();
      exp1 = prefixExpr ();                            	// PrefixExpr }
      exp = new Binary (op, exp, exp1);
    }
    return exp;
  }

  // PrefixExpr ::= [AddOper] SimpleExpr
  // SimpleExpr ::= integer | ( Expr ) | as.integer ( readline ( ) ) 
  //   | id [ ( [Expr {, Expr}] ) ] | cons ( Expr , Expr ) | head ( Expr ) 
  //   | tail ( Expr ) | null ( )

  public Expression prefixExpr () throws IOException {
    Expression exp = null, exp1, exp2;
    String unaryOp = null;
    if (token . symbol () == Symbol . PLUS      	// [ +
        || token . symbol () == Symbol . MINUS) { 	//   - ]
      unaryOp = token . lexeme ();
      getToken ();
    }

    switch (token . symbol ()) {

      case INTEGER :                             	// integer
        exp = new IntValue (token . lexeme ());
        getToken ();
        break;

      case LPAREN :                                  	// (
        getToken ();
        exp = expr ();                                	// Expr
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        getToken ();
        break;

      case ASINTEGER :                            	// as.integer
        getToken ();
        if (token . symbol () != Symbol . LPAREN)  	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . READLINE) 	// readline
          ErrorMessage . print (lexer . position (), "readline EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . LPAREN)  	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        exp = new Readline ();
        getToken ();
        break;

      case ID :                                 	// id
        String id = token . lexeme ();
        getToken ();
        if (token . symbol () != Symbol . LPAREN)  	// [ (
          exp = new Identifier (id);
        else {
          getToken ();
          ArrayList <Expression> expList = new ArrayList <Expression> ();
          if (token . symbol () != Symbol . RPAREN) { 	// [
            exp = expr ();                       	// Expr
            expList . add (exp);
            while (token . symbol () == Symbol . COMMA) { // { ,
              getToken ();
              exp = expr ();                      	// Expr }
              expList . add (exp);
            }                                    	// ]
            if (token . symbol () != Symbol . RPAREN) 	// )
              ErrorMessage . print (lexer . position (), ") EXPECTED");
          }                                      	// ]
          exp = new FunctionCall (id, expList);
          getToken ();
        }
        break;

      case CONS :                               	// cons
        getToken ();
        if (token . symbol () != Symbol . LPAREN)  	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        exp1 = expr ();                         	// Expr
        if (token . symbol () != Symbol . COMMA)        // ,
          ErrorMessage . print (lexer . position (), ", EXPECTED");
        getToken ();
        exp2 = expr ();                         	// Expr
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        exp = new Binary ("cons", exp1, exp2);
        getToken ();
        break;

      case HEAD :                               	// head
        getToken ();
        if (token . symbol () != Symbol . LPAREN) 	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        exp1 = expr ();                                	// Expr
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        exp = new Unary ("head", exp1);
        getToken ();
        break;

      case TAIL :                                	// tail
        getToken ();
        if (token . symbol () != Symbol . LPAREN) 	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        exp1 = expr ();                                	// Expr
        if (token . symbol () != Symbol . RPAREN)  	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        exp = new Unary ("tail", exp1);
        getToken ();
        break;

      case NULL :                               	// null
        getToken ();
        if (token . symbol () != Symbol . LPAREN) 	// (
          ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . RPAREN) 	// )
          ErrorMessage . print (lexer . position (), ") EXPECTED");
        exp = new Null ();
        getToken ();
        break;

      default :
        ErrorMessage . print (lexer . position (), "UNRECOGNIZABLE SYMBOL");
    }
    if (unaryOp == null)
      return exp;
    else
      return new Unary (unaryOp, exp);
  }

}
