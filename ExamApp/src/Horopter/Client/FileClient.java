/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Client;

/*
 * @author SantoshKumar
 */
public class FileClient  {

	public static void main(String args[]) {
                switch(args.length)
                {
                    case 0:
                        new FileUI("C:/Client/exam", "localhost", 5027);
			break;
                    case 1:
                        new FileUI(args[0], "localhost", 5027);
                        break;
                    case 2:
                        new FileUI(args[0], args[1], 5027);
                        break;
                    case 3:
                        new FileUI(args[0], args[1], Integer.parseInt(args[2]));
                        break;
                    default:
                        FileUI.commandRejected();
                        break;
                }
	}
}