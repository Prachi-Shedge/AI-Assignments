import java.util.*;
import java.util.regex.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SmartCollegeChatbot {
    
    // Enhanced knowledge base with multiple keywords and context
    private static final Map<String, ResponseData> knowledgeBase = new HashMap<>();
    private static final Map<String, Integer> questionFrequency = new HashMap<>();
    private static final Scanner sc = new Scanner(System.in);
    
    static {
        // Initialize knowledge base with multiple keywords and rich responses
        initializeKnowledgeBase();
    }
    
    static class ResponseData {
        String response;
        String[] keywords;
        String category;
        int priority;
        
        ResponseData(String response, String[] keywords, String category, int priority) {
            this.response = response;
            this.keywords = keywords;
            this.category = category;
            this.priority = priority;
        }
    }
    
    private static void initializeKnowledgeBase() {
        // Admission related
        knowledgeBase.put("admission", new ResponseData(
            "🎓 *Admission Process:*\n" +
            "• Online applications open: June 1st\n" +
            "• Eligibility: 12th grade with 60% minimum\n" +
            "• Required documents: Marksheet, ID proof, Photographs\n" +
            "• Apply at: www.ourcollege.edu.in/admissions",
            new String[]{"admission", "apply", "application", "admit", "enroll"},
            "ADMISSION", 1
        ));
        
        // Courses with detailed info
        knowledgeBase.put("courses", new ResponseData(
            "📚 *Available Programs:*\n\n" +
            "🔹 *Engineering:*\n" +
            "   • B.Tech (CS, IT, Mechanical, Civil, ECE)\n" +
            "   • M.Tech (AI, Data Science, Structural Eng)\n\n" +
            "🔹 *Management:*\n" +
            "   • BBA, MBA (Finance, Marketing, HR)\n\n" +
            "🔹 *Computer Applications:*\n" +
            "   • BCA, MCA\n\n" +
            "💡 *Duration:* 3-4 years | *Seats:* 60-120 per course",
            new String[]{"courses", "programs", "degrees", "btech", "mba", "mca"},
            "ACADEMICS", 1
        ));
        
        // Fees with breakdown
        knowledgeBase.put("fees", new ResponseData(
            "💰 *Fee Structure (Annual):*\n\n" +
            "• B.Tech: ₹95,000 + ₹15,000 (development fee)\n" +
            "• M.Tech: ₹85,000 + ₹12,000 (development fee)\n" +
            "• MBA: ₹1,15,000 + ₹20,000 (development fee)\n" +
            "• MCA: ₹75,000 + ₹10,000 (development fee)\n\n" +
            "💳 *Payment Options:* Installments available",
            new String[]{"fees", "fee", "tuition", "cost", "payment"},
            "FINANCE", 2
        ));
        
        // Hostel facilities
        knowledgeBase.put("hostel", new ResponseData(
            "🏠 *Hostel Facilities:*\n\n" +
            "✅ Separate hostels for Boys & Girls\n" +
            "✅ AC/Non-AC rooms available\n" +
            "✅ WiFi, Laundry, Gym, Common Room\n" +
            "✅ Mess with vegetarian & non-vegetarian options\n" +
            "✅ 24/7 Security & Medical facilities\n\n" +
            "💵 *Hostel Fees:* ₹45,000 - ₹85,000 per year",
            new String[]{"hostel", "accommodation", "stay", "lodging"},
            "CAMPUS_LIFE", 2
        ));
        
        // Enhanced placements info
        knowledgeBase.put("placements", new ResponseData(
            "🏢 *Placement Statistics (2024):*\n\n" +
            "📊 *Overall Placement Rate:* 92%\n" +
            "💼 *Highest Package:* ₹42 LPA (Amazon)\n" +
            "💰 *Average Package:* ₹8.5 LPA\n\n" +
            "🏆 *Top Recruiters:*\n" +
            "• Tech: Google, Microsoft, Amazon, TCS, Infosys\n" +
            "• Finance: Goldman Sachs, Morgan Stanley\n" +
            "• Consulting: Deloitte, PwC, EY\n\n" +
            "🎯 *Training:* Dedicated placement cell with mock interviews",
            new String[]{"placement", "job", "career", "recruitment", "company"},
            "CAREER", 1
        ));
        
        // Library with timing and facilities
        knowledgeBase.put("library", new ResponseData(
            "📖 *Central Library:*\n\n" +
            "🕐 *Timings:* 8:00 AM - 10:00 PM (Mon-Sat)\n" +
            "📚 *Collection:* 50,000+ books, 100+ journals\n" +
            "💻 *Digital Resources:*\n" +
            "   • Online databases access\n" +
            "   • E-books and E-journals\n" +
            "   • Computer lab with internet\n\n" +
            "🎧 *Study Spaces:* Silent zones, Group study rooms",
            new String[]{"library", "books", "study", "research"},
            "FACILITIES", 3
        ));
        
        // Scholarship information
        knowledgeBase.put("scholarship", new ResponseData(
            "🎗️ *Scholarship Programs:*\n\n" +
            "• Merit-based: 100% fee waiver for top rankers\n" +
            "• Sports quota: Up to 50% fee waiver\n" +
            "• EWS category: As per government norms\n" +
            "• SC/ST scholarships available\n\n" +
            "📝 Apply within 15 days of admission",
            new String[]{"scholarship", "financial aid", "waiver", "free"},
            "FINANCE", 2
        ));
        
        // Campus facilities
        knowledgeBase.put("campus", new ResponseData(
            "🏛️ *Campus Highlights:*\n\n" +
            "• 50-acre green campus\n" +
            "• Smart classrooms with projectors\n" +
            "• 10+ specialized laboratories\n" +
            "• Sports complex & swimming pool\n" +
            "• Cafeteria & food court\n" +
            "• Medical center with ambulance",
            new String[]{"campus", "infrastructure", "facilities", "building"},
            "FACILITIES", 3
        ));
    }
    
    // Advanced matching with multiple keyword scoring
    private static String findBestResponse(String userInput) {
        userInput = userInput.toLowerCase().trim();
        String cleanInput = preprocessInput(userInput);
        
        // Track response scores
        Map<String, Integer> responseScores = new HashMap<>();
        Map<String, Integer> keywordMatches = new HashMap<>();
        
        // Score each response based on keyword matches
        for (Map.Entry<String, ResponseData> entry : knowledgeBase.entrySet()) {
            ResponseData data = entry.getValue();
            int score = calculateMatchScore(cleanInput, data.keywords);
            
            if (score > 0) {
                responseScores.put(entry.getKey(), score);
                keywordMatches.put(entry.getKey(), score);
            }
        }
        
        // Find best match
        if (!responseScores.isEmpty()) {
            String bestMatch = Collections.max(responseScores.entrySet(), 
                Map.Entry.comparingByValue()).getKey();
            
            // Track question frequency for analytics
            questionFrequency.put(bestMatch, 
                questionFrequency.getOrDefault(bestMatch, 0) + 1);
            
            return knowledgeBase.get(bestMatch).response;
        }
        
        // Fallback responses for unknown queries
        return generateFallbackResponse(userInput);
    }
    
    private static int calculateMatchScore(String input, String[] keywords) {
        int score = 0;
        for (String keyword : keywords) {
            if (input.contains(keyword.toLowerCase())) {
                score += 2; // Base score for exact match
                
                // Bonus for exact word match (not substring)
                if (Pattern.compile("\\b" + keyword + "\\b").matcher(input).find()) {
                    score += 3;
                }
            }
        }
        return score;
    }
    
    private static String preprocessInput(String input) {
        // Remove special characters and extra spaces
        return input.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
    }
    
    private static String generateFallbackResponse(String userInput) {
        // Context-aware fallback responses
        if (userInput.matches(".*(hi|hello|hey).*")) {
            return "👋 Hello! I'm SmartEduBot. How can I assist you with college information today?";
        }
        else if (userInput.matches(".*(thank|thanks).*")) {
            return "You're welcome! 😊 Feel free to ask if you need more information.";
        }
        else if (userInput.matches(".*(name|who are you).*")) {
            return "I'm SmartEduBot, your intelligent college assistant! 🤖";
        }
        else if (userInput.matches(".*(time|date).*")) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
            return "⏰ Current time: " + time;
        }
        else {
            List<String> suggestions = Arrays.asList(
                "admission process", "course details", "fee structure", 
                "hostel facilities", "placement records", "campus life"
            );
            
            return "🤔 I'm not sure I understand. Try asking about:\n" +
                   "• " + String.join("\n• ", suggestions) + 
                   "\n\nOr type 'help' for more options.";
        }
    }
    
    private static void showHelp() {
        System.out.println("\n📋 *Available Topics:*");
        System.out.println("┌───────────────────┬─────────────────────────────┐");
        System.out.println("│ Topic             │ Keywords to use             │");
        System.out.println("├───────────────────┼─────────────────────────────┤");
        System.out.println("│ Admission         │ admission, apply, enroll    │");
        System.out.println("│ Courses           │ courses, btech, mba, mca    │");
        System.out.println("│ Fees              │ fees, cost, payment         │");
        System.out.println("│ Hostel            │ hostel, accommodation       │");
        System.out.println("│ Placements        │ placement, job, career      │");
        System.out.println("│ Library           │ library, books, study       │");
        System.out.println("│ Scholarship       │ scholarship, financial aid  │");
        System.out.println("│ Campus            │ campus, facilities          │");
        System.out.println("└───────────────────┴─────────────────────────────┘");
    }
    
    private static void showAnalytics() {
        if (questionFrequency.isEmpty()) {
            System.out.println("No questions asked yet.");
            return;
        }
        
        System.out.println("\n📊 *Question Analytics:*");
        questionFrequency.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.println("• " + entry.getKey() + ": " + entry.getValue() + " times")
            );
    }
    
    public static void main(String[] args) {
        System.out.println("🎓 🤖 Welcome to SmartEduBot - Your Intelligent College Assistant!");
        System.out.println("=" .repeat(55));
        System.out.println("💡 Type 'help' for topics or 'exit' to quit");
        System.out.println("💡 Try: 'What are the fees for B.Tech?' or 'Tell me about placements'");
        System.out.println("=" .repeat(55));
        
        while (true) {
            System.out.print("\n👤 You: ");
            String userInput = sc.nextLine().trim();
            
            if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("quit")) {
                System.out.println("🤖 Bot: Thank you for chatting! 🎓 Good luck with your college journey!");
                if (!questionFrequency.isEmpty()) {
                    showAnalytics();
                }
                break;
            }
            else if (userInput.equalsIgnoreCase("help")) {
                showHelp();
                continue;
            }
            else if (userInput.equalsIgnoreCase("analytics")) {
                showAnalytics();
                continue;
            }
            else if (userInput.isEmpty()) {
                System.out.println("🤖 Bot: Please type your question. Type 'help' for options.");
                continue;
            }
            
            String response = findBestResponse(userInput);
            System.out.println("🤖 Bot: " + response);
        }
        
        sc.close();
    }
}