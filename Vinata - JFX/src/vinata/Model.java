/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vinata;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Horopter
 */
public class Model {
    public double Calculate(double n1, double n2, String op)
    {
        switch(op)
        {
            case "+":
                return n1+n2;
            case "-":
                return n1-n2;
            case "*":
                return n1*n2;
            case "/":
                if(n2==0)
                    return 0;
                return n1/n2;
            default:
                return 0;
        }
    }
    public ArrayList<String> getPostOrder(ArrayList<String> inOrderList){

        ArrayList<String> result = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
        for (int i = 0; i < inOrderList.size(); i++) {
            if(Character.isDigit(inOrderList.get(i).charAt(0))){
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
    public Integer calculate(ArrayList<String> postOrder){
        Stack stack = new Stack();
        for (int i = 0; i < postOrder.size(); i++) {
            if(Character.isDigit(postOrder.get(i).charAt(0))){
                stack.push(Integer.parseInt(postOrder.get(i)));
            }else{
                Integer back = (Integer)stack.pop();
                Integer front = (Integer)stack.pop();
                Integer res = 0;
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
                        res = front / back;
                        break;
                    case '^':
                        res = (int) Math.pow(front,back);
                }
                stack.push(res);
            }
        }
        return (Integer)stack.pop();
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
}
