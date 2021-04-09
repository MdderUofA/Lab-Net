package com.example.lab_net;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Various utility methods for making code smaller.
 * @author Marcus
 */
public class Utils {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Gets the collection reference associated with the specified DatabaseCollection. Alias
     * function to prevent having to get a reference to the Database.
     * @param collection The Enum collection type.
     * @return The collection
     */
    public static Query collection(DatabaseCollections collection) {
        return db.collection(collection.value());
    }

    /**
     * Convenience method that wraps up writing to the database. Exists mainly so that
     * other classes do not have to have a reference to the database.
     * @param collection
     * @param id
     * @param values
     */
    public static Task<Void> writeDatabase(DatabaseCollections collection, String id,
                                           Map<String,Object> values) {
        CollectionReference ref = db.collection(collection.value());
        return ref.document(id).set(values);
    }

    /**
     * Utility method to query the database in a single call.
     * @param collection The DatabaseCollection to query
     * @param listener The listener to assign to the task.
     */
    public static void queryDatabase(DatabaseCollections collection, OnCompleteListener listener) {
        CollectionReference ref = db.collection(collection.value());
        ref.get().addOnCompleteListener(listener);
    }

    /**
     * Creates and returns a new OnCompleteListener for use with databaseQueries. Creates a default
     * CompleteListenerWrapper, mainly used for functional interface reasons.
     * @param apply The SnapshotApply instance to run code on.
     * @return A new OnCompleteListener
     */
    public static OnCompleteListener<QuerySnapshot> applyAll(Consumer<QueryDocumentSnapshot> apply) {
        return Utils.applyAll(new CompleteListenerWrapper(apply));
    }

    /**
     * Attempts to find the specified entry from a database query using the supplied methods.
     * This method is an alias for find if you don't care about if the result was not found.
     *
     * @param tester The test function to call if the query matches
     * @param onFound The Consumer to call if the tester returned true
     * @return A new OnCompleteListener
     */
    public static OnCompleteListener<QuerySnapshot> find(Predicate<QueryDocumentSnapshot> tester,
                                                         Consumer<QueryDocumentSnapshot> onFound) {
        return Utils.find(tester,onFound,(b)->{});
    }

    /**
     * Attempts to find the specified entry from a database query using the supplied methods.
     * @param tester The test function to call if the query matches
     * @param onFound The Consumer to call if the tester returned true
     * @param onNotFound The Runnable to call if the tester failed to find any, or the query failed
     *                   The argument will be true if the error was a network error, false otherwise.
     * @return A new OnCompleteListener
     */
    public static OnCompleteListener<QuerySnapshot> find(Predicate<QueryDocumentSnapshot> tester,
                                                         Consumer<QueryDocumentSnapshot> onFound,
                                                         Consumer<Boolean> onNotFound) {
        OnCompleteListener<QuerySnapshot> val = task -> {
            if (!task.isSuccessful()){
                onNotFound.accept(true);
                return;
            }
            for(QueryDocumentSnapshot doc : task.getResult())
                if(tester.test(doc)) {
                    onFound.accept(doc);
                    return;
                }
            onNotFound.accept(false);
        };

        return val;
    }

    /**
     * Creates and returns a new OnCompleteListener for use with databaseQueries using the specified
     * CompleteListenerWrapper
     * @param wrapper The CompleteListenerWrapper to use
     * @return A new OnCompleteListener
     */
    public static OnCompleteListener<QuerySnapshot> applyAll(CompleteListenerWrapper wrapper) {
        OnCompleteListener<QuerySnapshot> val = task -> {
            if (!task.isSuccessful()){
                wrapper.onError();
                return;
            }
            wrapper.processResults(task.getResult());
        };

        return val;
    }

    /**
     * Utility class for handling an OnComplete with more options!
     */
    public static class CompleteListenerWrapper {
        private Consumer<QueryDocumentSnapshot> applyFunction;
        private Runnable errorFunction;
        private Object lock = new Object();
        private AtomicBoolean waiting = new AtomicBoolean(true);

        /**
         * Creates a CompleteListenerWrapper. Sets the error function to do nothing by default.
         * @param apply The Consumer to call for each document
         */
        public CompleteListenerWrapper(Consumer<QueryDocumentSnapshot> apply) {
            this(apply, ()->{});
        }

        /**
         *Creates a CompleteListenerWrapper
         * @param apply The Consumer to call for each document
         * @param errorFunc The Runnable to call on error
         */
        public CompleteListenerWrapper(Consumer<QueryDocumentSnapshot> apply, Runnable errorFunc) {
            this.applyFunction=apply;
            this.errorFunction=errorFunc;
        }

        /**
         * Called when an error is raied by the Query
         */
        private void onError() {
            this.errorFunction.run();
        }

        /**
         * Awaits the results of the document snapshot.
         */
        public void await() {
            if(this.waiting.get()==false)
                return;
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                return; // if you interrupted, i assume you know what you're doing...
            }

        }

        /**
         * Processes all the results from the QuerySnapshot
         * @param results The QuerySnapshot.
         */
        protected void processResults(QuerySnapshot results) {
            for(QueryDocumentSnapshot snapshot : results) {
                this.applyFunction.accept(snapshot);
            }
            synchronized (lock) { // if they're awaiting us, let them know we have the results now
                lock.notifyAll();
            }
        }

    }
}
