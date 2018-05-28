package pl.kosteklvp.bbbk_v2;

import java.util.List;

/**
 * Created by Piotrek on 18.05.2018.
 */

public class Lista {

    private int id;
    private int user_id;
    private int board_id;
    private String title;
    private String description;
    List<Task> listOfTasks;
    List<String> listOfTasksTitles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBoard_id() {
        return board_id;
    }

    public void setBoard_id(int board_id) {
        this.board_id = board_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
