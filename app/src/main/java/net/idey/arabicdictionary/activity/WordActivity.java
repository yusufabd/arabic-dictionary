package net.idey.arabicdictionary.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.idey.arabicdictionary.HistoryDBHelper;
import net.idey.arabicdictionary.R;

import java.util.Locale;

public class WordActivity extends AppCompatActivity implements View.OnClickListener{

    /*
    Activity that shows chosen word and its translation. English and arabic word are received from intent and set to textViews respectively
     */

    private TextView mEnglish, mArabic;
    private ImageView mBackButton, mBookmark;
    private ImageButton mSound, mShare;

    boolean checkBookmark;
    private String mEnglishWord, mArabicWord;
    private static final String KEY_ENGLISH = "english";
    private static final String KEY_ARABIC = "arabic";
    HistoryDBHelper dbHelper;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please, wait");
        dialog.setMessage("Initializing text-to-speech");

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status != TextToSpeech.ERROR) // initialization me error to nae ha
                        {
                            tts.setLanguage(Locale.UK);
                            dialog.dismiss();
                        }
                    }
                });

            }

        });
        dbHelper = new HistoryDBHelper(this);


        mEnglish = (TextView)findViewById(R.id.mEnglish);
        mArabic = (TextView)findViewById(R.id.mArabic);
        mBackButton = (ImageView)findViewById(R.id.mBackButton);
        mBookmark = (ImageView)findViewById(R.id.mBookmark);
        mSound = (ImageButton)findViewById(R.id.mSound);
        mShare = (ImageButton)findViewById(R.id.mShare);
        mBackButton.setOnClickListener(this);
        mBookmark.setOnClickListener(this);
        mSound.setOnClickListener(this);
        mShare.setOnClickListener(this);



        /*
        Initializing text to speech
         */



        mEnglishWord = getIntent().getStringExtra(KEY_ENGLISH);
        mArabicWord = getIntent().getStringExtra(KEY_ARABIC);


        changeBookmarkState();

        mEnglish.setText(mEnglishWord);
        mArabic.setText(mArabicWord);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBackButton:
                finish();
                break;
            case R.id.mBookmark:
                /*
                if bookmark does exist it will be deleted; else bookmark will be created
                 */
                if (checkBookmark){
                    dbHelper.deleteBookmark(mEnglishWord);
                }else{
                    dbHelper.insertBookmark(mEnglishWord, mArabicWord);
                }
                changeBookmarkState();
                break;
            case R.id.mSound:
                /*
                pronouncing the word
                 */
                tts.speak(mEnglishWord, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.mShare:
                /*
                dialog with share options
                 */
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = mEnglishWord + " - " + mArabicWord;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "oED English-Arabic");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
        }
    }

    private void changeBookmarkState(){
        /*
        checking if bookmark exists; if so, image will be yellow star
         */
        checkBookmark = dbHelper.checkIfBookmarkExists(mEnglishWord);
        if (checkBookmark){
            mBookmark.setImageResource(R.drawable.bookmarks_on);
        }else{
            mBookmark.setImageResource(R.drawable.bookmarks);
        }
    }



}
