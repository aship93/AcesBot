import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

public class AcesBotMain {

	public static void main(String[] args) {
		AcesBot aBot = new AcesBot("4AcesBot");
		aBot.setVerbose(true);
		try {
			String line;
			String pidInfo = "";

			Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while ((line = input.readLine()) != null) {
				pidInfo += line;
			}

			input.close();

			if (!pidInfo.contains("mysql.exe")) {
				Runtime rt = Runtime.getRuntime();
				rt.exec("C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql.exe");
				System.out.println("mysql.exe was not found in the process list, launching now");
			}

			aBot.connect("irc.twitch.tv", 6667, "oauth:sh2icdaq6rofz6gadhnf26vboa1ktw");
			aBot.joinChannel("#4acesgaming");
			BotThread aThread = new BotThread("Points Thread", aBot);
			//BotThread bThread = new BotThread("Data Thread", aBot);
			aThread.start();
			//bThread.start();
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
	}

}
