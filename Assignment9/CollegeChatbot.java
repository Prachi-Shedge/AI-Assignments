
import java.util.*;

public class CollegeChatbot {

    // Knowledge base (questions and responses)
    static String[][] knowledgeBase = {
        {"admission", "You can apply online through the college portal. Admissions open in June."},
        {"courses", "We offer B.Tech, M.Tech, MBA, and MCA programs."},
        {"fees", "The annual fees vary between 80,000 to 1,20,000 INR depending on the course."},
        {"hostel", "Yes, hostel facilities are available for both boys and girls."},
        {"placements", "Top recruiters include TCS, Infosys, Wipro, and Accenture with 90% placement rate."},
        {"library", "The library is open from 8 AM to 8 PM with 20,000+ books."},
        {"exit", "Thank you! Have a great day."}
    };

    // Heuristic function: counts keyword matches
    static String bestMatchResponse(String userInput) {
        userInput = userInput.toLowerCase();
        int bestScore = 0;
        String bestResponse = "Sorry, I didn’t understand your question.";

        for (String[] entry : knowledgeBase) {
            String keyword = entry[0];
            String response = entry[1];

            int score = 0;
            if (userInput.contains(keyword)) {
                score++;
            }

            if (score > bestScore) {
                bestScore = score;
                bestResponse = response;
            }
        }

        return bestResponse;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("🤖 Hello! I’m EduBot – your college assistant.");
        System.out.println("Ask me about admission, courses, fees, hostel, placements, library. Type 'exit' to quit.");

        while (true) {
            System.out.print("\nYou: ");
            String userInput = sc.nextLine();

            if (userInput.toLowerCase().contains("exit")) {
                System.out.println("Bot: Thank you! Have a great day.");
                break;
            }

            String response = bestMatchResponse(userInput);
            System.out.println("Bot: " + response);
        }

        sc.close();
    }
}
