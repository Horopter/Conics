/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vinata;

import java.util.ArrayList;
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
    private double n1;
    private String op = "";
    private boolean start = true;
    ArrayList<String> arrayList = new ArrayList<>();
    String init1 ="";
    String init2 = "";
    int sol=0;
    private Model model = new Model();
    public void processNumbers(ActionEvent event)
    {
        if(start)
        {
            result.setText("");
            start = false;
        }
        String value = ((Button)event.getSource()).getText();
        result.setText(result.getText()+value);
    }
    public void processOperator (ActionEvent event)
    {
        String value = ((Button)event.getSource()).getText();
        if(!value.equals("="))
        {
            if(!op.isEmpty())
                return;
            op = value;
            n1 = Double.parseDouble(result.getText());
            result.setText(""); 
        }
        else
        {
            if(op.isEmpty())
                return;
            double n2 =  Double.parseDouble(result.getText());
            double output = model.Calculate(n1,n2,op);
            result.setText(String.valueOf(output));
            op="";
            start = true;
        }
    }
    public void onClick1(ActionEvent event)
    {
        //TextView tv1 = (TextView) findViewById(R.id.textView);
        if(sol<0)
        {
            input.setText(Integer.toString(sol));
            arrayList.clear();
            arrayList.add(0, Integer.toString(0));
            arrayList.add(1,"-");
            arrayList.add(2, Integer.toString(-sol));
            init2=Integer.toString(sol);
            sol=0;
        }
        else if(sol!=0)
        {
            input.setText(Integer.toString(sol));
            arrayList.clear();
            arrayList.add(0, Integer.toString(sol));
            init2=Integer.toString(sol);
            sol=0;
        }
        init1 = ((Button)event.getSource()).getText();
        //Fault in Sameer's calci : Multiple digit contingency
        if(!(init1.contains("+")||init1.contains("-")||init1.contains("*")||init1.contains("/")||init1.contains("(")||init1.contains(")")||init1.contains("^")))
        {
            init2 = init2 + init1;
            if(arrayList.size()>0)
             {
                 arrayList.remove((arrayList.size()-1));
             }
            arrayList.add(init2);
        }
        else if(init1.contains("("))
        {
            if(arrayList.size()>0)
            {
                arrayList.remove((arrayList.size()-1));
            }
            arrayList.add(init1);
            arrayList.add(init2);
            init2 = ""; // make string null if multiple operators are pressed.
        }
        else if(init1.contains(")"))//reverse order for the sake of insertion
        {
            if(arrayList.size()>0)
            {
                arrayList.remove((arrayList.size()-1));
            }
            arrayList.add(init2);
            arrayList.add(init1);
            init2 = ""; // make string null if multiple operators are pressed.
        }
        else
        {
            if(arrayList.size()==0 && init1.contentEquals("-"))
            {
                arrayList.add(Integer.toString(0));
            }
            arrayList.add(init1);
            arrayList.add(init2);
            init2 = ""; // make string null if multiple operators are pressed.
        }
        input.setText(input.getText().toString()+init1);
        //tv1.setText(tv1.getText().toString()+init1);
        //tv1.setText(arrayList.toString()); // Debugger for lengthy string input.
    }
    public void onClick(ActionEvent event)
    {
        //TextView tv2 = (TextView) findViewById(R.id.textView2);
        //TextView tv1 = (TextView) findViewById(R.id.textView);
        ArrayList<String> res;
        res = model.getPostOrder(arrayList);
        sol = model.calculate(res);
        result.setText(Integer.toString(sol));
        arrayList.clear();
        arrayList.add(0, Integer.toString(sol));
        //tv1.setText(arrayList.toString());
    }
    public void clear(ActionEvent event)
    {
        //TextView tv1 = (TextView) findViewById(R.id.textView);
        //TextView tv2 = (TextView) findViewById(R.id.textView2);
        init1 = "";
        init2 ="";
        input.setText("");
        result.setText("0");
        arrayList.clear();
        sol=0;
    }
}
