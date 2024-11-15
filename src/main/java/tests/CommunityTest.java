package tests;

import models.Community;
import models.Quest;

public class CommunityTest {
    public static void main(String[] args) {
        Quest q1 = new Quest("mencari cwek chindo di telkom","minimal dapet lah ya");
        Quest q2 = new Quest("mencari cwek","minimal dapet lah");
        Quest q3 = new Quest("mencari 7 bola","biar kaya dragon ball");
        Community community = new Community("Tel-U", null);

        community.addQuest(q1);
        community.addQuest(q2);
        community.addQuest(q3);

        System.out.println(community);

        System.out.println("Quest: " + q1.getTitle());
        System.out.println("Quest: " + q2.getTitle());
        System.out.println("Quest: " + q3.getTitle());
    }
}
