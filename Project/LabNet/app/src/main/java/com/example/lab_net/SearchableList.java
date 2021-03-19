package com.example.lab_net;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@RequiresApi(api = Build.VERSION_CODES.N)
/**
 * A utility class which will allows a list to be referenced and fed results from a database query.
 */
public class SearchableList {

    public static final int SEARCH_USERS = 1;
    public static final int SEARCH_EXPERIMENTS = 2;
    public static final int SEARCH_QA = 4;
    public static final int SEARCH_ALL = -1;

    private ArrayList<Searchable> data;
    private int selectedIndex=-1;

    private ListView listView;
    private Function<Searchable,Void> onClicked = null;
    private SearchableDisplayAdapter adapter = null;
    private AppCompatActivity parent = null;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Creates a new SearchableList from a ListView and an ArrayList
     * @param parentActivity The parent AppCompatActivity for the SearchableList to use when
     *                       changing activities.
     * @param toWrap The ListView to reference.
     * @param data The list that holds the data that the ListView displays.
     */
    public SearchableList(AppCompatActivity parentActivity,
                          ListView toWrap, ArrayList<Searchable> data) {
        this.parent = parentActivity;
        this.listView=toWrap;
        this.data = data;

        applyAdapter();

        this.updateList();

        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Searchable s = getFromIndex(position);
            this.selectedIndex=position;

            if(onClicked!=null) {
                onClicked.apply(s);
            } else {
                this.adapter.itemClicked(s);
            }
        });
    };

    /**
     * Gets and returns the Parent AppCompatActivity that this SearchableList is
     * responsible for servicing.
     * @return The parent AppCompatActivity.
     */
    public AppCompatActivity getParent() {
        return this.parent;
    }

    /**
     * Sets a custom function to run once an element is clicked. If this is set to a non null value,
     * this function will be called instead of the default clickListener which just navigates to the
     * appropriate activity.
     * @param func The function to call.
     */
    public void setOnClicked(Function<Searchable,Void> func) {
        onClicked = func;
    }

    /**
     *
     * @return Whether or not an element is currently selected
     */
    public boolean hasSelectedIndex() {
        return this.selectedIndex!=-1;
    }

    /**
     * Gets the selected object from the list.
     * @return The selected object, or null if none is selected.
     */
    public Searchable getSelected() {
        return this.hasSelectedIndex() ? this.getFromIndex(this.selectedIndex) : null;
    }

    /**
     * Performs a search with the specified input. Search uses the default options
     * @param query The string query to run
     */
    public void performSearch(String query) {
        performSearch(query, SearchableList.SEARCH_ALL);
    }

    /**
     * Performs a search with the specified input.
     * @param query The string query to run
     * @param filter The filter to use.
     */
    public void performSearch(String query, int filter) {
        performSearchOnDatabase(query, filter);
    }

    private void applyAdapter() {
        adapter=new SearchableDisplayAdapter(this.listView.getContext(),data,this);
        listView.setAdapter(adapter);
    }

    private void performSearchOnDatabase(String query, int filter) {
        List<String> list = Arrays.asList(query.split("\\s"));
        ArrayList<String> keywords = new ArrayList<>();
        keywords.addAll(list);
        Toast.makeText(SearchableList.this.listView.getContext(),
                "Searching...",
                Toast.LENGTH_SHORT)
                .show();
        final SearchProgressListener listener = new SearchProgressListener(1) {
            @Override
            public void onAllQueriesDone(ArrayList<Searchable> results) {
                SearchableList.this.setListContents(results);
            }
        };
        this.performSearchOnDatabaseExperiments(keywords, listener);


    }

    private void performSearchOnDatabaseExperiments(ArrayList<String> searchWordList,
            SearchProgressListener listener) {

        // Query all experiments and from there pick ones that fit.
        db.collection("Experiments")
                //.whereArrayContainsAny("arrayWord", searchWordList)
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = (String) document.get("Title");
                            String description = (String) document.get("Description");
                            String combined = (title + description).toLowerCase();
                            SearchableDocumentReference ref = new SearchableDocumentReference(
                                    "Experiments",document.getId());
                            // check if our experiment is actually valid.
                            for(String s : searchWordList)
                                if(combined.contains(s.toLowerCase())) {
                                    listener.addResults(new SearchableExperiment(title,
                                            description, ref));
                                    break; // matched word, break. TODO: Remove
                                }

                        }
                    } else {
                        // whoops.
                        Toast.makeText(SearchableList.this.listView.getContext(),
                                "Error getting documents: "+ task.getException(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    listener.onNextQueryDone();
                });
    }

    private void setListContents(Collection<Searchable> newContents) {
        this.data.clear();
        this.data.addAll(newContents);
        this.updateList();
    }

    private void updateList() {
        adapter.notifyDataSetChanged();
    }

    private Searchable getFromIndex(int index) {
        return data.get(index);
    }

    /**
     * A simple listener class to watch the queries and then execute a callback when all queries
     * are completed.
     */
    private abstract class SearchProgressListener {

        private final AtomicInteger queries = new AtomicInteger(0); // probably async so just use atomics.
        private final int totalQueries;
        private final ArrayList<Searchable> results = new ArrayList<>();

        public SearchProgressListener(int queries) {
           totalQueries=queries;
        }

        /**
         * Gets and returns the current results of the search. These results may be unfinished
         * if the search is not yet complete. It is better to await the callback onAllQueriesDone.
         *
         * @return The results of the the search.
         */
        public ArrayList<Searchable> getResults() {
            return this.results;
        }

        /**
         * Adds 1 or more results to the result list.
         * @param results Varargs results to add
         */
        public void addResults(Searchable... results) {
            for(Searchable s : results)
                this.results.add(s);
        }

        /**
         * Called when any collection query is completely finished processing.
         * Notifies the listener of the progress and if the progress is complete it will
         * call onAllQueriesDone.
         *
         * If this method is called too many times such that the expected number of queries
         * is less than the actual number of queries, an IllegalArgumentException will be raised.
         * @throws IllegalArgumentException
         */
        public void onNextQueryDone() {
            if (queries.addAndGet(1) == totalQueries) {
                this.onAllQueriesDone(this.getResults());
            }
            if (queries.get() > totalQueries) {
                throw new IllegalStateException("SearchProgressListener was not defined with"
                        + "the correct amount of queries.");
            }
        }

        /**
         * Called once all queries are done.
         * @param results the results of the search.
         */
        public abstract void onAllQueriesDone(ArrayList<Searchable> results);
    }


    /**
     * A SearchableDisplayAdapter is the general class that will handle all searchables and put them
     * into a displayable form. This is done by finding the appropriate ListDisplayAdapterSegment
     * and deferring control to it to create the View.
     */
    private class SearchableDisplayAdapter extends ArrayAdapter {

        private ArrayList<ListDisplayAdapterSegment> segments = new ArrayList<>();
        private Context context;
        private ArrayList<Searchable> listData;
        private SearchableList parent;

        public SearchableDisplayAdapter(Context context, ArrayList data,
                                        SearchableList parent) {
            super(context,0,data);
            this.context = context;
            this.listData=data;
            this.parent = parent;
            setupSegments();
        }

        /**
         * Gets the context that this SearchableDisplayAdapter was setup with.
         * @return The Context.
         */
        public Context getContext() {
            return this.context;
        }

        /**
         * Gets the parent SearchableList that this SearchableDisplayAdapter is tied to.
         * @return The SearchableList.
         */
        public SearchableList getSearchableList() {
            return this.parent;
        }

        /**
         * Gets the dataList responsible of the ListView.
         * @return The data list
         */
        public ArrayList<Searchable> getListData() {
            return this.listData;
        }


        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Searchable searchable = data.get(position);

            ListDisplayAdapterSegment segment
                    = this.findAdapterSegmentByClass(searchable.getClass());
            return segment.getView(position,convertView,parent);
        }

        /**
         * Called automatically when any element of the ListView is clicked. Defers the
         * execution to the approprate ListDisplayAdapterSegment based on the class of the clicked
         * Object.
         * @param searchable
         */
        public void itemClicked(Searchable searchable) {
            this.findAdapterSegmentByClass(searchable.getClass()).itemClicked(searchable);
        }

        private ListDisplayAdapterSegment findAdapterSegmentByClass(Class<? extends Searchable> type) {
            for(ListDisplayAdapterSegment segment : this.segments) {
                if(segment.canHandleClass(type))
                    return segment;
            }
            throw new UnsupportedOperationException("Class " + type.getSimpleName()
                    + " is not supported by SearchableList");
        }

        private void setupSegments() {
            segments.add(new UserDisplayAdapterSegment(this));
            segments.add(new ExperimentDisplayAdapterSegment(this));
        }
    }

    private abstract class ListDisplayAdapterSegment {

        protected final SearchableDisplayAdapter adapter;

        /**
         * Creates a new ListDisplayAdapterSegment and attaches a reference
         * to the parent SearchableDisplayAdapter
         * @param adapter
         */
        public ListDisplayAdapterSegment(SearchableDisplayAdapter adapter) {
            this.adapter = adapter;
        }

        /**
         * Gets and returns the Adapter that this AdapterSegment is repsonsible for servicing.
         * @return the Adapter
         */
        protected SearchableDisplayAdapter getAdapter() {
            return this.adapter;
        }

        /**
         * Creates the view for the list
         * @param position The position in the data.
         * @param convertView The view to set
         * @param parent The parent ViewGroup
         * @return The newly created View to display
         */
        public abstract View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent);

        /**
         * Called when the item in the list was clicked, assuming there was not a method provided
         * to the to handle item clicking already. This method should handle the generic case
         * that an item of this type was clicked by navigating to the appropraite activity.
         * @param searchable The searchable that was clicked.
         */
        public abstract void itemClicked(Searchable searchable);
        public abstract boolean canHandleClass(Class<? extends Searchable> cl);
    }

    private class UserDisplayAdapterSegment extends ListDisplayAdapterSegment {

        /**
         * Creates a new ListDisplayAdapterSegment and attaches a reference
         * to the parent SearchableDisplayAdapter
         *
         * @param adapter
         */
        public UserDisplayAdapterSegment(SearchableDisplayAdapter adapter) {
            super(adapter);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void itemClicked(Searchable t) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public boolean canHandleClass(Class<? extends Searchable> cl) {
            return cl == SearchableUser.class;
        }
    }

    private class ExperimentDisplayAdapterSegment extends ListDisplayAdapterSegment {

        /**
         * Creates a new ListDisplayAdapterSegment and attaches a reference
         * to the parent SearchableDisplayAdapter
         *
         * @param adapter
         */
        public ExperimentDisplayAdapterSegment(SearchableDisplayAdapter adapter) {
            super(adapter);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if(view==null) {
                view = LayoutInflater.from(this.adapter.getContext()).inflate(
                        R.layout.searchable_list_content_experiment,parent,false);
            }

            SearchableExperiment experiment = (SearchableExperiment) data.get(position);

            TextView name = view.findViewById(R.id.sr_user_name);
            TextView description = view.findViewById(R.id.sr_user_description);

            name.setText(experiment.getName());
            description.setText(experiment.getDescription());

            return view;
        }

        @Override
        public void itemClicked(Searchable searchable) {
            SearchableExperiment experiment = (SearchableExperiment) searchable;
            SearchableDocumentReference ref = experiment.getDocumentReference();
            // TODO: remove hard coded experiment.
            AppCompatActivity parentActivity = this.getAdapter().getSearchableList().getParent();
            Intent intent = new Intent(parentActivity,
                    ExperimentActivity.class);
            parentActivity.startActivity(intent);
        }

        @Override
        public boolean canHandleClass(Class<? extends Searchable> cl) {
            return cl == SearchableExperiment.class;
        }
    }
}
