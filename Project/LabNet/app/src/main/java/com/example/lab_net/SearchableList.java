package com.example.lab_net;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A utility class which will allows a list to be referenced and fed results from a database query.
 */
public class SearchableList {

    public static final String SEARCHABLE_FILTER_EXTRA = "com.example.lab_net.searchable_list.filter";

    public static final int SEARCH_USERS = 1;
    public static final int SEARCH_EXPERIMENTS = 2;
    public static final int SEARCH_QA = 4;
    public static final int SEARCH_ALL = -1;

    private static final int TOTAL_SEARCH_OPTIONS = 3; // the different options as given above

    private ArrayList<Searchable> data;

    private int searchFilters = SearchableList.SEARCH_ALL;

    private ListView listView;
    private SearchableDisplayAdapter adapter;
    private AppCompatActivity parent;

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
            this.adapter.itemClicked(s);
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
     * Sets which types of objects to query from the database on search.
     * @param searchFilters The search flags to use.
     */
    public void setSearchFilters(int searchFilters) {
        this.searchFilters = searchFilters;
    }

    /**
     * Performs a search with the specified input. Search uses the default options
     * @param query The string query to run
     */
    public void performSearch(String query) {
        performSearchOnDatabase(query);
    }

    private void performSearchOnDatabase(String query) {
        List<String> list = Arrays.asList(query.split("\\s"));
        ArrayList<String> keywords = new ArrayList<>();
        keywords.addAll(list);

        Toast.makeText(this.listView.getContext(),
                "Searching...",
                Toast.LENGTH_SHORT)
                .show();
        final SearchProgressListener listener =
                new SearchProgressListener(this.getNumQueries()) {
            @Override
            public void onAllQueriesDone(ArrayList<Searchable> results) {
                SearchableList.this.setListContents(results);
            }
        };
        this.performSearchOnDatabaseElements(keywords, listener);
    }

    private int getNumQueries() {
        int total = 0;
        for(int i = 1;i < (1<<SearchableList.TOTAL_SEARCH_OPTIONS);i<<=1) {
            total+= this.checkFlag(i) ? 1 : 0;
        }
        return total;
    }

    private void performSearchOnDatabaseElements(ArrayList<String> searchWordList,
                                                 SearchProgressListener listener) {

        if(this.checkFlag(SearchableList.SEARCH_USERS))
            this.queryCollection("UserProfile", searchWordList, listener,
                        SearchableUser.class);

        if(this.checkFlag(SearchableList.SEARCH_EXPERIMENTS))
            this.queryCollection("Experiments", searchWordList, listener,
                        SearchableExperiment.class);

        if(this.checkFlag(SearchableList.SEARCH_QA))
            this.queryCollection("QA", searchWordList, listener, null);
    }

    private void queryCollection(String collection, ArrayList<String> searchWordList,
                                 SearchProgressListener listener, Class<? extends Searchable> cl) {
        db.collection(collection)
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Searchable s = SearchableList.this.createSearchableFromSnapshot(doc,cl);
                            if(SearchableList.this.testSearchable(s,searchWordList))
                                listener.addResults(s);
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

    private boolean testSearchable(Searchable searchable, ArrayList<String> searchWordList) {
        String combined = (searchable.getName()+searchable.getDescription()).toLowerCase();
        // check if our experiment is actually valid.
        for(String s : searchWordList) {
            if (combined.contains(s.toLowerCase())) {
                return true;
            }
        }
        return false;

    }

    private Searchable createSearchableFromSnapshot(QueryDocumentSnapshot doc,
                                                    Class<? extends Searchable> type) {
        try {
            // this will only throw an error if someone incorrectly implements Searchable.
            return type.newInstance().applyFromDatabase(doc);
        } catch (InstantiationException e) {
            throw new IllegalStateException("SearchableList contains non Searchable elements");
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Searchable "+type.getSimpleName()
                    + " lacks null constructor.");
        }

    }

    private boolean checkFlag(int flagValue) {
        return ( this.searchFilters & flagValue ) != 0;
    }

    private void setListContents(Collection<Searchable> newContents) {
        this.data.clear();
        this.data.addAll(newContents);
        this.updateList();
    }

    private void updateList() {
        adapter.notifyDataSetChanged();
    }

    private void applyAdapter() {
        adapter=new SearchableDisplayAdapter(this.listView.getContext(),data,this);
        listView.setAdapter(adapter);
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

        private final ArrayList<ListDisplayAdapterSegment> segments = new ArrayList<>();
        private final Context context;
        private final ArrayList<Searchable> listData;
        private final SearchableList parent;

        /**
         * Constructs a new SearchableDisplayAdapater for use in a SearchableList
         * @param context The context
         * @param data The ArrayList representing the ListView's data
         * @param parent The SearchableList that this SearchableDisplayAdapter services.
         */
        public SearchableDisplayAdapter(Context context, ArrayList data,
                                        SearchableList parent) {
            super(context,0,data);
            this.context = context;
            this.listData = data;
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
         * Creates the view for the list. It is guaranteed that the data held at data.get(position)
         * will be the appropriate type based on the class.
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

        /**
         * Determines whether or not this DisplayAdapterSegment is capable of handling
         * the specified input class.
         * @param cl The class to test
         * @return True if this DisplayAdapterSegment claims to be able to handle the class,
         *          false otherwise.
         */
        public abstract boolean canHandleClass(Class<? extends Searchable> cl);
    }

    private class UserDisplayAdapterSegment extends ListDisplayAdapterSegment {

        /**
         * Creates a new ListDisplayAdapterSegment and attaches a reference
         * to the parent SearchableDisplayAdapter
         *
         * @param adapter The parent adapter
         */
        public UserDisplayAdapterSegment(SearchableDisplayAdapter adapter) {
            super(adapter);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if(view==null) {
                view = LayoutInflater.from(this.adapter.getContext()).inflate(
                        R.layout.searchable_list_content_user,parent,false);
            }

            SearchableUser user = (SearchableUser) data.get(position);

            TextView name = view.findViewById(R.id.sr_user_name);
            TextView description = view.findViewById(R.id.sr_user_description);

            name.setText(user.getName());
            description.setText(user.getDescription());

            return view;
        }

        @Override
        public void itemClicked(Searchable searchable) {
            SearchableUser user = (SearchableUser) searchable;
            SearchableDocumentReference ref = user.getDocumentReference();
            AppCompatActivity parentActivity = this.getAdapter().getSearchableList().getParent();
            Intent intent = new Intent(parentActivity,
                    SubscribedUserActivity.class);
            intent.putExtra(UserProfile.USER_ID_EXTRA,ref.getDocumentId());
            parentActivity.startActivity(intent);
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
         * @param adapter The parent adapter
         */
        public ExperimentDisplayAdapterSegment(SearchableDisplayAdapter adapter) {
            super(adapter);
        }

        /**
         * Creates an experiment view
         * @param position The position in the data.
         * @param convertView The view to set
         * @param parent The parent ViewGroup
         * @return
         */
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if(view==null) {
                view = LayoutInflater.from(this.adapter.getContext()).inflate(
                        R.layout.searchable_list_content_experiment,parent,false);
            }

            SearchableExperiment experiment = (SearchableExperiment) data.get(position);

            TextView name = view.findViewById(R.id.sr_experiment_name);
            TextView description = view.findViewById(R.id.sr_experiment_description);
            TextView ownerId = view.findViewById(R.id.sr_experiment_owner_id);
            TextView status = view.findViewById(R.id.sr_experiment_status);

            name.setText(experiment.getName());
            description.setText(experiment.getDescription());
            ownerId.setText(experiment.getOwnerId());
            status.setText((experiment.getStatus() ? "Open" : "Closed"));

            return view;
        }

        /**
         * Performs the standard on click behaviour and navigates the user to the experiment activity
         * @param searchable The searchable that was clicked.
         */
        @Override
        public void itemClicked(Searchable searchable) {
            SearchableExperiment experiment = (SearchableExperiment) searchable;
            SearchableDocumentReference ref = experiment.getDocumentReference();

            // get our data.
            AppCompatActivity parentActivity = this.getAdapter()
                    .getSearchableList().getParent();
            Class<? extends AppCompatActivity> target
                    = this.getActivityFromType(experiment.getType());

            // go to the activity.
            Intent intent = new Intent(parentActivity, target);
            intent.putExtra(ExperimentActivity.EXPERIMENT_ID_EXTRA,ref.getDocumentId());
            parentActivity.startActivity(intent);
        }

        /**
         * Finds the class associated with the experiment to subscribe to.
         *
         * @param type The string type of the subscribed experiment.
         * @throws IllegalArgumentException if the supplied string does not match to a valid class
         * @return The SubscribedExperiment class represented by the String experimentType
         */
        private Class<? extends AppCompatActivity> getActivityFromType(String type) {
            switch (type) {
                case("Count-based"):
                    return SubscribedCountExperimentActivity.class;
                case("Binomial"):
                    return SubscribedCountExperimentActivity.class;
                case("Measurement"):
                    return SubscribedMeasurementExperimentActivity.class;
                case("NonNegativeInteger"):
                    return SubscribedNonNegativeExperimentActivity.class;
                default:
                    throw new IllegalArgumentException("Type "+type+" does not match to a valid experiment type.");
            }
        }

        @Override
        public boolean canHandleClass(Class<? extends Searchable> cl) {
            return cl == SearchableExperiment.class;
        }
    }
}
