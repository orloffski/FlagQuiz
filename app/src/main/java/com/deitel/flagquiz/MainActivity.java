package com.deitel.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regionsToInclude";
    public static final String PREFS = "pref_changes";

    private boolean phoneDevice = true;
    private boolean preferencesChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState != null)
            preferencesChanged = savedInstanceState.getBoolean(PREFS);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        if(screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            phoneDevice = false;

        if(phoneDevice)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivityFragment quizFragment  = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.quizFragment);

        if(preferencesChanged){
            updateFragmentUI(quizFragment, true, true, true);
        }else{
            updateFragmentUI(quizFragment, true, true, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(PREFS, preferencesChanged);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            preferencesChanged = true;

            MainActivityFragment quizFragment = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.quizFragment);

            if(s.equals(CHOICES)){
                updateFragmentUI(quizFragment, true, false, true);
            }
            else if(s.equals(REGIONS)){
                Set<String> regions = sharedPreferences.getStringSet(REGIONS, null);

                if(regions != null && regions.size() > 0){
                    updateFragmentUI(quizFragment, false, true, true);
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    regions.add(getString(R.string.default_region));
                    editor.putStringSet(REGIONS, regions);
                    editor.apply();

                    updateFragmentUI(quizFragment, false, true, true);

                    Toast.makeText(MainActivity.this, R.string.default_region_message, Toast.LENGTH_SHORT).show();
                }
            }

            Toast.makeText(MainActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
        }
    };

    private void updateFragmentUI(MainActivityFragment quizFragment, boolean updateGuessRows, boolean updateRegions, boolean resetQuiz){
        if(updateGuessRows)
            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));

        if(updateRegions)
            quizFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));

        if(resetQuiz)
            quizFragment.resetQuiz();
        else
            quizFragment.loadOldData();

        preferencesChanged = false;
    }
}
