import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AcesBot extends PircBot {

	Connection conn = DBConn.dbConnect();
	ResultSet rs;
	Statement stmt;
	String quote;
	String sayer;

	public AcesBot(String name) {
		setName(name);
	}

	public void onConnect() {
		 this.sendMessage(
		 "#4acesgaming",
		 "Hello there! I am 4AcesBot, a bot written by Alex to help make things in chat "
		 +
		 "a bit easier and more fun! If you have any questions feel free to ask!");
	}

	public void onJoin(String channel, String sender, String login,
			String hostname) {
	}

	public void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		if (message.startsWith("!")) {
			if (message.startsWith("!rps")) {
				Scanner scanner = new Scanner(message);
				scanner.next();
				if (scanner.hasNext()) {
					Type person = Type.parseType(scanner.next());
					if (!person.toString().equalsIgnoreCase("rock")
							&& !person.toString().equalsIgnoreCase("paper")
							&& !person.toString().equalsIgnoreCase("scissors")
							&& !person.toString().equalsIgnoreCase("scissor")) {
						this.sendMessage(
								"#4acesgaming",
								"Have you played this game before? "
										+ person.toString()
										+ " is not a choice!");
						scanner.close();
						return;
					}
					Type[] choices = { Type.parseType("Rock"),
							Type.parseType("Paper"), Type.parseType("Scissors") };
					Random rand = new Random();
					Type bot = choices[rand.nextInt(3)];
					if (person.equals(bot)) {
						this.sendMessage("#4acesgaming", "You both picked "
								+ person.toString() + ". It's a tie!");
					} else if (person.beats(bot)) {
						this.sendMessage("#4acesgaming", person.toString()
								+ " beats " + bot.toString() + ". You win!");
					} else {
						this.sendMessage("#4acesgaming", bot.toString()
								+ " beats " + person.toString()
								+ ". You lose. Better luck next time!");
					}
					scanner.close();
				} else {
					this.sendMessage("#4acesgaming",
							"Retry command including Rock, Paper, or Scissors");
				}
			} else if (message.startsWith("!8ball")) {
				String[] answers = { "It is certain", "It is decidedly so",
						"Without a doubt", "Yes definitely",
						"You may rely on it", "As I see it, yes",
						"Most likely", "Outlook good", "Yes",
						"Signs point to yes", "Reply hazy try again",
						"Ask again later", "Better not tell you now",
						"Cannot predict now", "Concentrate and ask again",
						"Don't count on it", "My reply is no",
						"My sources say no", "Outlook not so good",
						"Very doubtful", "Oh God no!",
						"Never in a million years", "We'll see!",
						"I say, Neigh", "Fuck that", "Awwwww hell no",
						"I'm not answering that question" };
				Random rand = new Random();
				this.sendMessage("#4acesgaming", "@" + sender + ": "
						+ answers[rand.nextInt(answers.length - 1)]); 
			} else if(message.startsWith("!skin")){
				this.sendMessage("#4acesgaming", "Skin created by Kogi: http://puu.sh/kIAEa.zip");
			}else if(message.startsWith("!newquote")){
				User[] users = this.getUsers("#4acesgaming");
				for(int i = 0; i < users.length; i++){
					if(users[i].getNick().equals(sender) && users[i].isOp()){
						try {
							stmt = conn.createStatement();
							Scanner mesread = new Scanner(message);
							mesread.useDelimiter("-|!newquote");
							if(mesread.hasNext()){
								quote = mesread.next().trim();
								sayer = mesread.next().trim();
							}
							stmt.executeUpdate("INSERT INTO quotes VALUES ('" + quote + "', '" + sayer + "')");
							mesread.close();
							break;
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}else if(users[i].getNick().equals(sender) && !users[i].isOp()){
						this.sendMessage("#4acesgaming", "Sorry @" + sender + ", you must be a mod to use this command");
					}
				}
			}else if(message.startsWith("!quote")){
				try {
					stmt = conn.createStatement();
					rs = stmt.executeQuery("SELECT * FROM user_points.quotes ORDER BY RAND() LIMIT 1");
					if(rs.next())
						this.sendMessage("#4acesgaming","\"" +rs.getString("quote") + "\" - " + rs.getString("speaker"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if(message.startsWith("!points")){
				try {
					stmt = conn.createStatement();
					rs = stmt.executeQuery("SELECT points from users WHERE name = '" + sender + "'");
					this.sendMessage("#4acesgaming", "@" + sender + " You have " + rs.getInt("points") + " points.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				this.sendMessage("#4acesgaming",
						"Unrecognized command. Please try again");
			}
		}
	}

	public enum Type {
		Rock {
			@Override
			public boolean beats(Type other) {
				return other == Scissors;
			}
		},
		Paper {
			@Override
			public boolean beats(Type other) {
				return other == Rock;
			}
		},
		Scissors {
			@Override
			public boolean beats(Type other) {
				return other == Paper;
			}
		};
		public abstract boolean beats(Type other);

		public static Type parseType(String value) {
			if (value.equalsIgnoreCase("rock")) {
				return Rock;
			} else if (value.equalsIgnoreCase("paper")) {
				return Paper;
			} else if (value.equalsIgnoreCase("scissor")
					|| value.equalsIgnoreCase("scissors")) {
				return Scissors;
			} else
				return null;
		}
	}
}
