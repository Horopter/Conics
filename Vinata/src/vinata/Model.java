/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vinata;

import java.util.ArrayList;
import java.util.Stack;
import java.lang.ArithmeticException;

/**
 *
 * @author Horopter
 */
public class Model {
    public ArrayList<String> getPostOrder(ArrayList<String> inOrderList){

        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < inOrderList.size(); i++) {
            if('.'==inOrderList.get(i).charAt(0)||Character.isDigit(inOrderList.get(i).charAt(0))){
                result.add(inOrderList.get(i));
            }else{
                switch (inOrderList.get(i).charAt(0)) {
                    case '(':
                        stack.push(inOrderList.get(i));
                        break;
                    case ')':
                        while (!stack.peek().equals("(")) {
                            result.add(stack.pop());
                        }
                        stack.pop();
                        break;
                    default:
                        while (!stack.isEmpty() && compare(stack.peek(), inOrderList.get(i))){
                            result.add(stack.pop());
                        }
                        stack.push(inOrderList.get(i));
                        break;
                }
            }
        }
        while(!stack.isEmpty()){
            result.add(stack.pop());
        }
        return result;
    }
    public Double calculate(ArrayList<String> postOrder) throws ArithmeticException{
        Stack stack = new Stack();
        for (int i = 0; i < postOrder.size(); i++) {
            if(Character.isDigit(postOrder.get(i).charAt(0))||postOrder.get(i).charAt(0)=='.'){
                stack.push(Double.parseDouble(postOrder.get(i)));
            }else{
                Double back = (Double)stack.pop();
                Double front = (Double)stack.pop();
                Double res = 0.0;
                switch (postOrder.get(i).charAt(0)) {
                    case '+':
                        res = front + back;
                        break;
                    case '-':
                        res = front - back;
                        break;
                    case '*':
                        res = front * back;
                        break;
                    case '/':
                        if(back!=0.0)
                            res = front / back;
                        else
                        {
                            throw new ArithmeticException("Division By Zero");
                        }
                        break;
                    case '^':
                        res = (double) Math.pow(front,back);
                }
                stack.push(res);
            }
        }
        return (Double)stack.pop();
    }
    public static boolean compare(String peek, String cur){
        if("^".equals(peek)&&("/".equals(cur) || "*".equals(cur) ||"+".equals(cur) ||"-".equals(cur)||"^".equals(peek)))
            return true;
        if("*".equals(peek) && ("/".equals(cur) || "*".equals(cur) ||"+".equals(cur) ||"-".equals(cur))){
            return true;
        }
        else if("/".equals(peek) && ("/".equals(cur) || "*".equals(cur) ||"+".equals(cur) ||"-".equals(cur))){
            return true;
        }
        else if("+".equals(peek) && ("+".equals(cur) || "-".equals(cur))){
            return true;
        }
        else if("-".equals(peek) && ("+".equals(cur) || "-".equals(cur))){
            return true;
        }
        return false;
    }
    public boolean isOperator(String init1)
    {
        return (init1.contains("+")||init1.contains("-")||init1.contains("*")||init1.contains("/")||init1.contains("^"));
    }
    public boolean isParen(String init1)
    {
        return init1.contains("(")||init1.contains(")");
    }
    public boolean isExtra(String init1)
    {
        return (init1.contains("e")||init1.contains("r")||init1.contains("←")||init1.contains("π")||init1.contains("√"));
    }
    public String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
}
