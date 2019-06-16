/* *** This file is given as part of the programming assignment. *** */
import java.util.*;//needed to import the ArrayList class
public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
	tok = scanner.scan();
    }
    private ArrayList<SymbolTableItem> sym_table;	
    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }
    
    private void enterScope()
    {
     /*
       Function that increments the current scope of the program upon entering a new scope
     */
     SymbolTableItem.current_scope++;
    }
    
    private void exitScope()
    {
     /*
       Function that decrements the current scope of the program and also updates the symbol table upon exiting a scope
     */
     if(!sym_table.isEmpty())
       {
        while(sym_table.get(sym_table.size()-1).scope_level==SymbolTableItem.current_scope)
             {
              sym_table.remove(sym_table.size()-1);
             }
       }
     SymbolTableItem.current_scope--;
    }
    
    private void program() {
    	sym_table=new ArrayList<SymbolTableItem>();//creates an empty symbol table for the entire program.
    	System.out.println("public class My_e2j{");
    	System.out.println("public static void main(String[] args){");
	block();
	System.out.println("}");
    	System.out.println("}");
	sym_table.clear();//removes all the elements from the symbol table
    }

    private void block(){
	declaration_list();
	statement_list();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
	mustbe(TK.DECLARE);
	boolean isFirst=false;
	System.out.println("int");
	if(sym_table.isEmpty())
	  {
	   sym_table.add(new SymbolTableItem(tok.string));
	   System.out.println(tok.string+"_"+SymbolTableItem.current_scope);
	  }
	else
	{
	 int flag=0;
         for(int i=0;i<sym_table.size();i++)
	    {
	     if(sym_table.get(i).variable.equals(tok.string))
	       {
	        flag=1;
	        if(sym_table.get(i).scope_level==SymbolTableItem.current_scope)
	          {
	           System.err.println("redeclaration of variable "+tok.string);
	           isFirst=true;
	          }
	        else
	        {
	         sym_table.add(new SymbolTableItem(tok.string));
	         System.out.println(tok.string+"_"+SymbolTableItem.current_scope);
	        }
	        break;
	       }
	    }
         if(flag==0)
           {
	    sym_table.add(new SymbolTableItem(tok.string));
	    System.out.println(tok.string+"_"+SymbolTableItem.current_scope);
           }
	}
	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();
	    int flag=0;
            for(int i=0;i<sym_table.size();i++)
	       {
	        if(sym_table.get(i).variable.equals(tok.string))
	          {
	           flag=1;
	           if(sym_table.get(i).scope_level==SymbolTableItem.current_scope)
	             {
	              System.err.println("redeclaration of variable "+tok.string);
	             }
	           else
	           {
	            sym_table.add(new SymbolTableItem(tok.string));
	            if(!isFirst)
	              {
	               System.out.println(",");
	              }
	            System.out.println(tok.string+"_"+SymbolTableItem.current_scope);
	           }
	           break;
	          }
	       }
            if(flag==0)
              {
	       sym_table.add(new SymbolTableItem(tok.string));
	       if(!isFirst)
                 {
	          System.out.println(",");
	         }
	       System.out.println(tok.string+"_"+SymbolTableItem.current_scope);
              }
	    mustbe(TK.ID);
	}
     System.out.println(";");
    }

    private void statement_list() {
    	//statement_list::={statement}
	while(is(TK.ID)||is(TK.TILDE)||is(TK.PRINT)||is(TK.DO)||is(TK.IF)){
		statement();
	}
    }
    
    private void statement()
    {
     //statement::=assignment|print|do|if
     if(is(TK.ID)||is(TK.TILDE))
       {
        assignment();
       }
     else if(is(TK.PRINT))
            {
             print();
            }
     else if(is(TK.DO))
            {
             Do();
            }
     else if(is(TK.IF))
            {
             If();
            }
     /*else
     {
      parse_error("Not a valid statement");
     }*/
    }
    
    private void print()
    {
     //print::='!' expr
     mustbe(TK.PRINT);
     System.out.println("System.out.println(");
     expr();
     System.out.println(");");
    }
    
    private void assignment()
    {
     //assignment::= ref_id '=' expr
     ref_id();
     mustbe(TK.ASSIGN);
     System.out.println("=");
     expr();
     System.out.println(";");
    }
    
    private void ref_id()
    {
     //ref_id::=['~'[number]] id
     String var_name="";
     String num="-1";
     int check_scope_level=-1000;
     if(is(TK.TILDE))
       {
        var_name+=tok.string;
        scan();
        if(is(TK.NUM))
          {
           var_name+=tok.string;
           num=tok.string;
           scan();
          }
       }
     var_name+=tok.string;
     if(var_name.charAt(0)=='~')
       {
        int num_int=Integer.parseInt(num);
        check_scope_level=SymbolTableItem.current_scope-num_int;
        if(check_scope_level<0)
          {
           System.err.println("no such variable "+var_name+" on line "+tok.lineNumber);
           System.exit(1);
          }
        else
        {
         if(num.equals("-1"))
           {
            check_scope_level=0;
           }
         else
         {
          check_scope_level=SymbolTableItem.current_scope-num_int;
         }
         int flag=0;
         for(int i=0;i<sym_table.size();i++)
            {
             if(sym_table.get(i).scope_level==check_scope_level)
               {
                if(sym_table.get(i).variable.equals(tok.string))
                  {
                   flag=1;
                   break;
                  }
               }
            }
         if(flag==0)
           {
            System.err.println("no such variable "+var_name+" on line "+tok.lineNumber);
            System.exit(1);
           }
        }
       }
     int flag=0;
     for(int i=0;i<sym_table.size();i++)
        {
         if(sym_table.get(i).variable.equals(tok.string))
           {
            flag=1;
            break;
           }
        }
     if(flag==0)
       {
        System.err.println(tok.string+" is an undeclared variable on line " +tok.lineNumber);
        System.exit(1);
       }
     if(var_name.charAt(0)=='~')
       {
        if(num.equals("-1"))
          {
           System.out.println(tok.string+"_0");
          }
        else
        {
         if(check_scope_level>=0)
           {
            System.out.println(tok.string+"_"+check_scope_level);
           }
        }
       }
     else
     {
      for(int i=sym_table.size()-1;i>=0;i--)
         {
          if(sym_table.get(i).variable.equals(tok.string))
            {
             System.out.println(tok.string+"_"+sym_table.get(i).scope_level);
             break;
            }
         }
     }
     mustbe(TK.ID);
    }
    
    private void Do()
    {
     //do::='<' guarded_command '>'
     mustbe(TK.DO);
     System.out.println("while(");
     guarded_command();
     mustbe(TK.ENDDO);
    }
    
    private void If()
    {
     //if::='[' guarded_command {'|' guarded_command} [% block] ']'
     mustbe(TK.IF);
     System.out.println("if(");
     guarded_command();
     while(is(TK.ELSEIF))
          {
           System.out.println("else if(");
           scan();
           guarded_command();
          }
     if(is(TK.ELSE))
       {
        System.out.println("else{");
        scan();
        enterScope();
        block();
        System.out.println("}");
        exitScope();
       }
     mustbe(TK.ENDIF);
    }
    
    private void guarded_command()
    {
     //guarded_command::= expr ':' block
     expr();
     System.out.println("<=0){");
     enterScope();
     mustbe(TK.THEN);
     block();
     System.out.println("}");
     exitScope();
    }
    
    private void expr()
    {
     //expr::=term{addop term}
     term();
     while(is(TK.PLUS)||is(TK.MINUS))
          {
           addop();
           term();
          }
    }
    
    private void term()
    {
     //term::=factor{multop factor}
     factor();
     while(is(TK.TIMES)||is(TK.DIVIDE))
          {
           multop();
           factor();
          }
    }
    
    private void factor()
    {
     //factor::='(' expr ')'|ref_id|number
     if(is(TK.LPAREN))
       {
        System.out.println("(");
        scan();
        expr();
        mustbe(TK.RPAREN);
        System.out.println(")");
       }
     else if(is(TK.ID)||is(TK.TILDE))
            {
             ref_id();
            }
     else
     {
      System.out.println(tok.string);
      mustbe(TK.NUM);
     }
    }
    
    private void addop()
    {
     //addop::='+'|'-'
     if(is(TK.PLUS)||is(TK.MINUS))
       {
        if(is(TK.PLUS))
          {
           System.out.println("+");
          }
        else if(is(TK.MINUS))
               {
                System.out.println("-");
               }
        scan();
       }
     /*else
     {
      parse_error("Not a valid add operator");
     }*/
    }
    
    private void multop()
    {
     //multop::='*'|'/'
     if(is(TK.TIMES)||is(TK.DIVIDE))
       {
        if(is(TK.TIMES))
          {
           System.out.println("*");
          }
        else if(is(TK.DIVIDE))
               {
                System.out.println("/");
               }
        scan();
       }
     /*else
     {
      parse_error("Not a valid mult operator");
     }*/
    }
    

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	System.exit(1);
    }
}
