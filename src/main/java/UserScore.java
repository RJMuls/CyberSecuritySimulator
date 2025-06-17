
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class UserScore implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private int finalScore;
    private List<String> weaknesses;
    
    public UserScore(String username, int finalScore, List<String> weaknesses) {
        this.username = username;
        this.finalScore = finalScore;
        this.weaknesses = new ArrayList<>(weaknesses);
    }
    
    // Getters
    public String getUsername() { return username; }
    public int getFinalScore() { return finalScore; }
    public List<String> getWeaknesses() { return new ArrayList<>(weaknesses); }
    
    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setFinalScore(int finalScore) { this.finalScore = finalScore; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = new ArrayList<>(weaknesses); }
    
    @Override
    public String toString() {
        return "User: " + username + ", Score: " + finalScore + ", Weaknesses: " + weaknesses;
    }
}
