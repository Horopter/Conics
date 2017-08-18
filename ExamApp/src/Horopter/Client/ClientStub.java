/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Client;

/*
 * @author SantoshKumar
 */
public class ClientStub  {
        public String Username = "Santosh";
	public static void main(String args[]) {
                ClientStub clientStub = new ClientStub();
                new ClientGUI(clientStub.Username,"C:/Client/exam", "localhost", 5027);
	}
}