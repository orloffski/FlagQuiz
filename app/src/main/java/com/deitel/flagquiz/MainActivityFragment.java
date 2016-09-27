package com.deitel.flagquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements FinishDialogFragment.OnResetQuizListener{

    private static final String TAG = "FlagQuiz Activity";
    private static final int FLAG_IN_QUIZ = 10;

    private static final String FILENAMELIST = "fileNameList";
    private static final String QUIZCOUNTRIESLIST = "quizCountriesList";
    private static final String TOTALGUESSES = "totalGuesses";
    private static final String CORRECTANSWERS = "correctAnswers";
    private static final String GUESSROWS = "guessRows";
    private static final String ANSWERS = "answers";

    private List<String> fileNameList;
    private List<String> quizCountriesList;
    private Set<String> regionsSet;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private SecureRandom random;
    private Handler handler;
    private Animation shakeAnimation;

    private ArrayList<String> answers;

    private LinearLayout quizLinearLayout;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        random = new SecureRandom();
        handler = new Handler();

        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        quizLinearLayout = (LinearLayout) view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView =    (TextView) view.findViewById(R.id.questionNumberTextView);
        flagImageView = (ImageView) view.findViewById(R.id.flagImageView);

        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = (LinearLayout)view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = (LinearLayout)view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = (LinearLayout)view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = (LinearLayout)view.findViewById(R.id.row4LinearLayout);

        answerTextView = (TextView) view.findViewById(R.id.answerTextView);

        for(LinearLayout row : guessLinearLayouts){
            for(int column = 0; column < row.getChildCount(); column++){
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        if(savedInstanceState != null){
            fileNameList = savedInstanceState.getStringArrayList(FILENAMELIST);
            quizCountriesList = savedInstanceState.getStringArrayList(QUIZCOUNTRIESLIST);
            totalGuesses = savedInstanceState.getInt(TOTALGUESSES);
            correctAnswers = savedInstanceState.getInt(CORRECTANSWERS);
            guessRows = savedInstanceState.getInt(GUESSROWS);
            answers = savedInstanceState.getStringArrayList(ANSWERS);

            setQuestionNumber(correctAnswers + 1);
        }else {
            fileNameList = new ArrayList<>();
            quizCountriesList = new ArrayList<>();
            answers = new ArrayList<>();

            setQuestionNumber(1);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(FILENAMELIST, (ArrayList<String>) fileNameList);
        outState.putStringArrayList(QUIZCOUNTRIESLIST, (ArrayList<String>) quizCountriesList);
        outState.putInt(TOTALGUESSES, totalGuesses);
        outState.putInt(CORRECTANSWERS, correctAnswers);
        outState.putInt(GUESSROWS, guessRows);

        answers.clear();
        for(int row = 0; row < guessRows; row++){
            for(int column = 0; column < guessLinearLayouts[row].getChildCount(); column++){
                Button guessButton = (Button) guessLinearLayouts[row].getChildAt(column);

                answers.add(guessButton.getText().toString());
            }
        }
        outState.putStringArrayList(ANSWERS, answers);
    }

    public void updateGuessRows(SharedPreferences sharedPreferences){
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

        for(LinearLayout layout : guessLinearLayouts)
            layout.setVisibility(View.GONE);

        for(int row = 0; row < guessRows; row++)
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }

    public void updateRegions(SharedPreferences sharedPreferences){
        regionsSet = sharedPreferences.getStringSet(MainActivity.REGIONS, null);
    }

    public void resetQuiz(){
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear();

        try{
            for(String region : regionsSet){
                String[] paths = assets.list(region);

                for(String path : paths){
                    fileNameList.add(path.replace(".png", ""));
                }
            }
        }catch (IOException e){
            Log.e(TAG, "Error loading image file names", e);
        }

        correctAnswers = 0;
        totalGuesses = 0;

        quizCountriesList.clear();

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();
        Log.e(TAG, "numberOfFlags " + numberOfFlags + ", flagCounter " + flagCounter);
        while(flagCounter <= FLAG_IN_QUIZ){
            int randomIndex = random.nextInt(numberOfFlags);

            String filename = fileNameList.get(randomIndex);

            if(!quizCountriesList.contains(filename)){
                quizCountriesList.add(filename);
                ++flagCounter;
            }
        }

        loadNextFlag(true);
    }

    private void loadNextFlag(boolean newLoad){
        String nextImage = quizCountriesList.get(0);
        correctAnswer = nextImage;
        answerTextView.setText("");

        setQuestionNumber(correctAnswers + 1);

        String region = nextImage.substring(0, nextImage.indexOf('-'));

        AssetManager assets = getActivity().getAssets();

        try(InputStream stream = assets.open(region + "/" + nextImage + ".png")){
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);

            if(newLoad)
                animate(false);
        }catch(IOException e){
            Log.e(TAG, "Error loading " + nextImage, e);
        }

        if(newLoad) {
            Collections.shuffle(fileNameList);

            int correct = fileNameList.indexOf(correctAnswer);
            fileNameList.add(fileNameList.remove(correct));


            for (int row = 0; row < guessRows; row++) {
                for (int column = 0; column < guessLinearLayouts[row].getChildCount(); column++) {
                    Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                    newGuessButton.setEnabled(true);

                    String filename = fileNameList.get((row * 2) + column);
                    newGuessButton.setText(getCountryName(filename));
                }
            }

            int row = random.nextInt(guessRows);
            int column = random.nextInt(2);

            LinearLayout randomRow = guessLinearLayouts[row];
            String countryName = getCountryName(correctAnswer);
            ((Button) randomRow.getChildAt(column)).setText(countryName);
        }else{
            for(int row = 0; row < guessRows; row++){
                for(int column = 0; column < guessLinearLayouts[row].getChildCount(); column++){
                    Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                    newGuessButton.setEnabled(true);

                    newGuessButton.setText(answers.remove(0));
                }
            }
        }
    }

    private String getCountryName(String name){
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    private void animate(boolean animateOut){
        if(correctAnswers == 0)
            return;

        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;

        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());

        Animator animator;

        if(animateOut){
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextFlag(true);
                }
            });
        }else{
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(500);
        animator.start();
    }

    private OnClickListener guessButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Button guessButton = ((Button) view);
            String guess = guessButton.getText().toString();
            String answer = getCountryName(correctAnswer);
            ++totalGuesses;

            if(guess.equals(answer)){
                ++correctAnswers;
                quizCountriesList.remove(0);

                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_ancwer, getContext().getTheme()));

                disableButtons();

                if(correctAnswers == FLAG_IN_QUIZ){
                    createDialog();
                }else{
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animate(true);
                        }
                    }, 2000);
                }
            }else{
                flagImageView.startAnimation(shakeAnimation);

                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false);
            }
        }
    };

    private void disableButtons(){
        for(int row = 0; row < guessRows; row++){
            LinearLayout guessRow = guessLinearLayouts[row];
            for(int i = 0; i < guessRow.getChildCount(); i++){
                guessRow.getChildAt(i).setEnabled(false);
            }
        }
    }

    private void setQuestionNumber(int number){
        questionNumberTextView.setText(getString(R.string.question, number, FLAG_IN_QUIZ));
    }

    public void loadOldData(){
        loadNextFlag(false);
    }

    @Override
    public void onResetQuiz() {
        resetQuiz();
    }

    private void createDialog(){
        FinishDialogFragment quizResults = FinishDialogFragment
                .newInstance(getString(R.string.results, totalGuesses, (1000 / (double) totalGuesses)));
        quizResults.setTargetFragment(this, 0);
        quizResults.setCancelable(false);
        quizResults.show(getFragmentManager(), quizResults.getClass().getName());
    }
}
