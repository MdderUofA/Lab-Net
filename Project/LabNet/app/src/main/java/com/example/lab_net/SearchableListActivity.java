package com.example.lab_net;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.function.Function;

@RequiresApi(api = Build.VERSION_CODES.N)
/**
 * An activity which encapsulates a SearchableList and puts it into an independent activity.
 */
public class SearchableListActivity extends AppCompatActivity {

    public final int SEARCH_USERS = 1;
    public final int SEARCH_EXPERIMENTS = 2;
    public final int SEARCH_QA = 4;
    public final int SEARCH_ALL = -1;

    private int selectedIndex=-1;
    private int totalResults = 0;

    private EditText searchText;
    private Button searchButton;
    private ListView listView;
    private SearchableList searchableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_list);

        // search bar
        listView = findViewById(R.id.searchable_list_view);
        this.searchableList = new SearchableList(this, listView, new ArrayList<>()); // wrap the list

        searchText = findViewById(R.id.search_edit_text);
        searchText.setClickable(true);

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener((View v) -> {

            // code written by DeRagan https://stackoverflow.com/users/302328/deragan
            // https://stackoverflow.com/a/3553811
            // dismiss the keyboard if you press search.
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

            this.performSearch(this.searchText.getText().toString());
        });
    }

    private void performSearch(String query) {
        this.searchableList.performSearch(query);
    }
}
