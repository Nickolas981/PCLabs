package fileio;

import models.Tasks;

import java.io.*;

public class TaskFileManager {

    private static final String filename = "tasks.pc";

    public void save(Tasks tasks) {
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(tasks);

            out.close();
            file.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("IOException is caught");
        }
    }

    public Tasks load() {
        Tasks tasks = null;

        try {
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            tasks = (Tasks) in.readObject();

            in.close();
            file.close();
        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }
        return tasks;
    }
}
