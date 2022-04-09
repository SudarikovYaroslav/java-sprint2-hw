package tests;

import java.io.File;

public class FileBackedTest {
    public static void main(String[] args) {
        String path = new File("").getAbsolutePath();
        File fileForSavingData = new File(path + "\\BackedData.txt");
    }
}
