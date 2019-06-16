public class SymbolTableItem
{
 public String variable;//stores the name of the variable
 public int scope_level;//stores the scope at which the variable is declared at
 public static int current_scope;//stores the current_scope of the program
 SymbolTableItem(String variable_name)//called when a new row is added to the symbol table
 {
  /*
    Parametrized constructor that takes the variable name as an argument and creates an item belonging to the symbol table
    @variable_name:The name of the variable that needs to be stored in the symbol table
  */
  variable=variable_name;
  scope_level=current_scope;
 }
}
