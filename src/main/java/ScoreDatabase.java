
import java.io.*;
import java.util.*;

public class ScoreDatabase {
    private static final String DATABASE_FILE = "user_scores.dat";
    private List<UserScore> userScores;
    
    public ScoreDatabase() {
        userScores = new ArrayList<>();
        loadDatabase();
    }
    
    public void addUserScore(String username, int finalScore, List<String> weaknesses) {
        UserScore userScore = new UserScore(username, finalScore, weaknesses);
        userScores.add(userScore);
        saveDatabase();
    }
    
    public List<UserScore> getAllScores() {
        return new ArrayList<>(userScores);
    }
    
    public List<UserScore> getScoresByUsername(String username) {
        List<UserScore> userResults = new ArrayList<>();
        for (UserScore score : userScores) {
            if (score.getUsername().equalsIgnoreCase(username)) {
                userResults.add(score);
            }
        }
        return userResults;
    }
    
    public UserScore getBestScore(String username) {
        return getScoresByUsername(username).stream()
            .max(Comparator.comparingInt(UserScore::getFinalScore))
            .orElse(null);
    }
    
    public List<UserScore> getTopScores(int limit) {
        return userScores.stream()
            .sorted(Comparator.comparingInt(UserScore::getFinalScore).reversed())
            .limit(limit)
            .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }
    
    private void saveDatabase() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATABASE_FILE))) {
            oos.writeObject(userScores);
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadDatabase() {
        File file = new File(DATABASE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATABASE_FILE))) {
                userScores = (List<UserScore>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading database: " + e.getMessage());
                userScores = new ArrayList<>();
            }
        }
    }
    
    public void clearDatabase() {
        userScores.clear();
        saveDatabase();
    }
    
    public int getTotalUsers() {
        Set<String> uniqueUsers = new HashSet<>();
        for (UserScore score : userScores) {
            uniqueUsers.add(score.getUsername().toLowerCase());
        }
        return uniqueUsers.size();
    }
}
