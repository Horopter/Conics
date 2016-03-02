/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vinata;

import java.util.ArrayList;
import java.util.EmptyStackException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author Horopter
 */
public class FXMLDocumentController
{
    
    @FXML
    private Label result; 
    @FXML
    private Label input;
    ArrayList<String> arrayList = new ArrayList<>();
    String init1 ="";
    String init2 = "";
    double sol=0;
    private boolean dot = false;
    private final Model model = new Model();
    public void uInput(ActionEvent event)
    {
        if(sol<0)
        {
            input.setText(Double.toString(sol));
            arrayList.clear();
            arrayList.add(0, Double.toString(0));
            arrayList.add(1,"-");
            arrayList.add(2, Double.toString(-sol));
            init2=Double.toString(sol);
            sol=0;
        }
        else if(sol!=0)
        {
            input.setText(Double.toString(sol));
            arrayList.clear();
            arrayList.add(0, Double.toString(sol));
            init2=Double.toString(sol);
            sol=0;
        }
        init1 = ((Button)event.getSource()).getText();
        //Fault in Sameer's calci : Multiple digit contingency
        if(!(model.isOperator(init1)||model.isParen(init1)||model.isExtra(init1)))
        {
            //System.out.println(init2+"_"+init1);
            if(!arrayList.isEmpty())
            {
            if(input.getText().endsWith("e")||input.getText().endsWith("π"))
            {
                input.setText(input.getText()+"*");
                arrayList.add("*");
                arrayList.add(init1);
            }
            else if(!model.isOperator(arrayList.get(arrayList.size()-1))&&!model.isParen(arrayList.get(arrayList.size()-1))&&!model.isExtra(arrayList.get(arrayList.size()-1)))
            {
                if(dot&&init1.equals("."))
                    init1="";
                else if(init1.equals("."))
                    dot=true;
                init2 = arrayList.get((arrayList.size()-1)) + init1;
                arrayList.remove((arrayList.size()-1));
                arrayList.add(init2);
            } 
            else if(")".equals(arrayList.get(arrayList.size()-1)))
            {
                arrayList.add("*");
                arrayList.add(init1);
            }
            }
            else
            {
                init2 = init2 + init1;
                arrayList.add(init2);
            }
            init2="";
            input.setText(input.getText()+init1);
            
        }
        else if(model.isParen(init1))
        {
            arrayList.add(init1);
            input.setText(input.getText()+init1);
            init2="";
        }
        else if(model.isOperator(init1))
        {
            if(arrayList.isEmpty())
            {
                input.setText("0");
                arrayList.add(Double.toString(0));
            }
            else if(arrayList.get((arrayList.size()-1)).equals("("))
            {
                arrayList.add("0");
            }
            else if(model.isOperator(arrayList.get((arrayList.size()-1))))
                    init1="";
            if(!init1.equals(""))
                arrayList.add(init1);
            dot = false;
            init2="";//avoiding multiple attachment
            input.setText(input.getText()+init1);
        }
        else if(model.isExtra(init1))
        {
            if(init1.contentEquals("e"))
            {
                if(!arrayList.isEmpty())
                {
                if("(".equals(arrayList.get(arrayList.size()-1)))
                {    
                    arrayList.add("0");
                    arrayList.add("+");
                    init1="0+"+init1;
                }
                else if(")".equals(arrayList.get(arrayList.size()-1)))
                {
                    arrayList.add("*");
                    init1="*"+init1;
                }
                else if(!model.isOperator(arrayList.get(arrayList.size()-1))&&!model.isParen(arrayList.get(arrayList.size()-1)))
                {    
                    arrayList.add("*");
                    init1="*"+init1;
                }
                }
                arrayList.add(Double.toString(Math.E));
                input.setText(input.getText()+init1);
            }
            else if(init1.contentEquals("r"))
            {
                if(!arrayList.isEmpty())
                {
                boolean b=false;
                String y = arrayList.get((arrayList.size()-1));
                if("e".equals(y))
                    y=Double.toString(Math.E);
                if("π".equals(y))
                    y=Double.toString(Math.PI);
                double id = 0,d = 0;
                if(!(model.isOperator(init1)||model.isParen(init1)))
                {
                    d = Double.parseDouble(y);
                    if(d!=0)
                        id= 1/d;
                    else
                        b=true;
                }
                else
                {
                    input.setText("NaN Error");
                }
                arrayList.remove(arrayList.size()-1);
                arrayList.add(Double.toString(id));
                String ip = input.getText();
                String s = ip.substring(0,ip.lastIndexOf(y));
                s = s + Double.toString(id);
                input.setText(s);//replace(y,Double.toString(id)));
                if(b)
                    input.setText("Div By Zero Error");
                }
            }
            else if(init1.contentEquals("√"))
            {
                if(!arrayList.isEmpty())
                {
                String y = arrayList.get((arrayList.size()-1));
                if("e".equals(y))
                    y=Double.toString(Math.E);
                if("π".equals(y))
                    y=Double.toString(Math.PI);
                double d = Double.parseDouble(y);
                double sqrtd= Math.sqrt(d);
                arrayList.remove((arrayList.size()-1));
                arrayList.add(Double.toString(sqrtd));
                init1="";
                }
            }
            else if(init1.contentEquals("π"))
            {
                if(!arrayList.isEmpty())
                {
                if("(".equals(arrayList.get(arrayList.size()-1)))
                {    
                    arrayList.add("0");
                    arrayList.add("+");
                    init1="0+"+init1;
                }
                else if(")".equals(arrayList.get(arrayList.size()-1)))
                {
                    arrayList.add("*");
                    init1="*"+init1;
                }
                else if(!model.isOperator(arrayList.get(arrayList.size()-1))&&!model.isParen(arrayList.get(arrayList.size()-1)))
                {    
                    arrayList.add("*");
                    init1="*"+init1;
                }
                }
                arrayList.add(Double.toString(Math.PI));
                input.setText(input.getText()+init1);
            }
            else if(init1.contentEquals("←"))
            {
                //for (String i : arrayList) {
                  //  System.out.println(i);
                //}
                if(!arrayList.isEmpty())
                {
                String y = arrayList.get((arrayList.size()-1));
                y = model.removeLastChar(y);
                arrayList.remove((arrayList.size()-1));
                arrayList.add(y);
                if("".equals(y))
                    arrayList.remove((arrayList.size()-1));
                input.setText(model.removeLastChar(input.getText()));
                init1="";
                }
            }
        }
    }
    public void mEvaluate(ActionEvent event)
    {
        for (String i : arrayList) {
            System.out.println(i);
        }
        try{
        ArrayList<String> res;
        res = model.getPostOrder(arrayList);
        sol = model.calculate(res);
        result.setText(Double.toString(sol));
        arrayList.clear();
        arrayList.add(0, Double.toString(sol));
        }
        catch(ArithmeticException e)
        {
            result.setText("Div By Zero error");
        }
        catch(EmptyStackException e)
        {
            result.setText("Incomplete expression / Parenthesis error");
        }
        catch(Exception e)
        {
            result.setText(e.getClass().getSimpleName());
        }
    }
    public void clear(ActionEvent event)
    {
        init1 = "";
        init2 ="";
        dot=false;
        input.setText("");
        result.setText("0");
        arrayList.clear();
        sol=0;
    }
    
}
