package tests;

import models.Community;
import models.Quest;

public class CommunityTest {
    public static void main(String[] args) {
        Quest q1 = new Quest("mencari cwek chindo di telkom","minimal dapet lah ya");
        Quest q2 = new Quest("mencari cwek","minimal dapet lah");
        Quest q3 = new Quest("mencari 7 bola","biar kaya dragon ball");
        Community community = new Community("Tel-U", null);
    }
}
