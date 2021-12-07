public class MyInterpreter {
    ErrorMessage message = new ErrorMessage(); 
    class Expression {
        Expression arg1, arg2;
        int oper;
        final static int OP_ADD  = 1;   // Addition '+'
        final static int OP_SUB  = 2;   // Subtraction '-'
        final static int OP_MUL  = 3;   // Multiplication '*'
        final static int OP_DIV  = 4;   // Division '/'
        final static int OP_BNOT = 19;  // Boolean negation '.NOT.'
        final static int OP_NEG  = 20;  // Unary minus
         protected Expression(int op, Expression a, Expression b) throws BASICSyntaxError {
            arg1 = a;
            arg2 = b;
            oper = op;
            /*
             * If the operator is a boolean, both arguments must be boolean.
             */
            if (op > OP_ADD) {
                if ( (! (arg1 instanceof BooleanExpression)) ||
                     (! (arg2 instanceof BooleanExpression)) )
                    throw new BASICSyntaxError(typeError);
            } else {
                if ((arg1 instanceof BooleanExpression) || (arg2 instanceof BooleanExpression))
                    throw new BASICSyntaxError(typeError);
            }
        }
        protected Expression(int op, Expression a) throws BASICSyntaxError {
            arg2 = a;
            oper = op;
            if ((oper == OP_BNOT) && (! (arg2 instanceof BooleanExpression)))
                throw new BASICSyntaxError(typeError);
        }
    
    
    double value(Program pgm) throws BASICRuntimeError {
        switch (oper) {
            case OP_ADD :
                return arg1.value(pgm) + arg2.value(pgm);
            case OP_SUB :
                return arg1.value(pgm) - arg2.value(pgm);
    //... etc for all of the other operator types. ...

    class ConstantExpression extends Expression {
        private double v;
        ConstantExpression(double a) {
            super();
            v = a;
        }
        double value(Program pgm) throws BASICRuntimeError {
            return v;
        }
    
 class FunctionExpression extends Expression {
    //[ ... parsing stuff removed ...]
   double value(Program p) throws BASICRuntimeError {
             try {
                    switch (oper) {
                        case RND :
                            if (r == null)
                                r = p.getRandom();
                           return (r.nextDouble() * arg2.value(p));
                       case INT :
                           return Math.floor(arg2.value(p));
                       case SIN :
                           return Math.sin(arg2.value(p));
           //[ ... and so on for all of the functions implemented ... ]
                       default :
                           throw new BASICRuntimeError("Unknown or non-numeric function.");
                  }
              } catch (Exception e) {
                   if (e instanceof BASICRuntimeError)
                       throw (BASICRuntimeError) e;
                   else
                      throw new BASICRuntimeError("Arithmetic Exception.");
               }
           }
        static FunctionExpression parse(int ty, LexicalTokenizer lt) throws BASICSyntaxError {
                   FunctionExpression result;
                   Expression a;
                   Expression b;
                   Expression se;
                   Token t;
            //iterate over values
                   t = lt.nextToken();
                   if (! t.isSymbol('')) {
                      if (ty == RND) {
                          lt.unGetToken();
                          return new FunctionExpression(ty, new ConstantExpression(1));
                      } else if (ty == FRE) {
                          lt.unGetToken();
                          return new FunctionExpression(ty, new ConstantExpression(0));
                      }
                      throw new BASICSyntaxError("Missing argument for function.");
                  }
                  //switch case
                  switch (ty) {
                      case RND:
                      case INT:
                      case SIN:
                      case COS:
                      case TAN:
                      case ATN:
                      case SQR:
                      case ABS:
                      case CHR:
                      case VAL:
                      case STR:
                      case SPC:
                      case TAB:
                      case LOG:
                          a = ParseExpression.expression(lt);
                          if (a instanceof BooleanExpression) {
                              throw new BASICSyntaxError(functions[ty].toUpperCase()+" function cannot accept boolean expression.");
                          }
                          if ((ty == VAL) && (! a.isString()))
                              throw new BASICSyntaxError(functions[ty].toUpperCase()+" requires a string valued argument.");
                          result = new FunctionExpression(ty, a);
                          break;
                  //[ ... other cases omitted for brevity ... ]
                      default:
                          throw new BASICSyntaxError(ErrorMessage message = new ErrorMessage(););
           
                  }
                  t = lt.nextToken();
                  if (! t.isSymbol(')')) {
                      throw new BASICSyntaxError(ErrorMessage message = new ErrorMessage(););
                  }
                  return result;
              }

                    }
                }
            }
        }
    }
}
