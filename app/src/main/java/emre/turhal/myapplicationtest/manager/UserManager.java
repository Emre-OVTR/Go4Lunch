package emre.turhal.myapplicationtest.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import emre.turhal.myapplicationtest.repository.UserRepository;

public class UserManager {

    private static volatile UserManager instance;
    private static UserRepository userRepository;


    public UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public void createUser(){
        userRepository.createUser();
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    public Task<DocumentSnapshot> getWorkmate(String uid){
        return userRepository.getWorkmate(uid);

    }

    public Task<DocumentSnapshot> getUserData(){
        return userRepository.getUserData();
    }

    public static CollectionReference getWorkmateCollection(){
        return userRepository.getWorkmateCollection();
    }



}
