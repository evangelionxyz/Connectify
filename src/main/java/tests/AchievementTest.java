package tests;

import models.Achievement;

import java.util.ArrayList;
import java.util.List;

public class AchievementTest {
    public static void main(String[] args) {
        List<String> initialTags = new ArrayList<>();
        Achievement achievement = new Achievement("Programming Achievement", initialTags);

        System.out.println("Menambahkan tag 'Java':");
        achievement.addTags("Java");
        System.out.println("Apakah tag 'Java' ada? " + achievement.isExists("Java"));

        System.out.println("\nMenambahkan tag 'Java' lagi:");
        achievement.addTags("Java");
        System.out.println("Jumlah tag setelah menambahkan tag 'Java' lagi: " + achievement.getTags().size());

        System.out.println("\nMenambahkan tag 'Python':");
        achievement.addTags("Python");
        System.out.println("Apakah tag 'Python' ada? " + achievement.isExists("Python"));

        System.out.println("\nMenghapus tag 'Java':");
        achievement.removeTags("Java");
        System.out.println("Apakah tag 'Java' masih ada? " + achievement.isExists("Java"));

        System.out.println("\nMenghapus tag 'C++' yang tidak ada:");
        achievement.removeTags("C++");
        System.out.println("Apakah tag 'C++' ada? " + achievement.isExists("C++"));
    }
}
