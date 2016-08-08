package net.idey.arabicdictionary.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import net.idey.arabicdictionary.HistoryDBHelper;
import net.idey.arabicdictionary.MyDatabase;
import net.idey.arabicdictionary.OnBackPressedListener;
import net.idey.arabicdictionary.R;
import net.idey.arabicdictionary.activity.WordActivity;


public class DictionaryFragment extends Fragment implements OnBackPressedListener{

    private EditText mEditText;
    private ListView mWordsListView;
    private SimpleCursorAdapter mCursorAdapter;

    private static final String KEY_ENGLISH = "english";
    private static final String KEY_ARABIC = "arabic";
    String extraWord;

    String[] columns;
    int[] to;
    Cursor cursor;

    MyDatabase database;
    HistoryDBHelper dbHelper;

    public static DictionaryFragment newInstance(Bundle bundle){
        DictionaryFragment dictionaryFragment = new DictionaryFragment();
        dictionaryFragment.setArguments(bundle);
        return dictionaryFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        database = new MyDatabase(getActivity());
        dbHelper = new HistoryDBHelper(getActivity());


        mEditText = (EditText)view.findViewById(R.id.mSearch);

        mWordsListView = (ListView)view.findViewById(R.id.mWordsList);

        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mEditText.getText().toString().trim().length()<1){
//                    mWordsListView.setAdapter(null);
                }else if (keyCode == 66) {
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        //Adding listener that will look after changes in editText
        if (mEditText != null) {
            mEditText.addTextChangedListener(textWatcher);
        }

        //EditText will be cleared in case of long click
        mEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mEditText.setText("");
                return false;
            }
        });


        //Keyboard will hide when you touch list of words
        mWordsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });

        //The word, which will be receiver from OCR Fragment
        extraWord = getArguments().getString("english");


        //if it is not null, then it will be set to editText
        if (extraWord !=null && !extraWord.equals("") && !extraWord.equals(" ")) {
            mEditText.setText(extraWord);
        }
        showWordsList();
 //       t.start();
        return view;
    }

    private void hideKeyboard(View v) {
        //method for hiding keyboard
        InputMethodManager manager = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        /*
        Method that watches after editText changes (duh)
         */

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            showWordsList();
//            t.start();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void showWordsList(){
        /*
        Showing results from database: first define parameters for SimpleCursorAdapter (cursor, columns from db, layout elements
       where you put data from columns); then set adapter to listView and set onItemClickListener for it
         */

        cursor = database.getWordsList(mEditText.getText().toString());
        columns = new String[] {"_id", "word", "short"};
        to = new int[]{R.id.mIdx, R.id.mWord, R.id.mShort};
        mCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listitem, cursor, columns, to, 0);
        mWordsListView.setAdapter(mCursorAdapter);
        mWordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String engWord = ((TextView)view.findViewById(R.id.mWord)).getText().toString();
                String arabWord = ((TextView)view.findViewById(R.id.mShort)).getText().toString();

                dbHelper.insertWord(engWord, arabWord);

                Intent intent = new Intent(getActivity(), WordActivity.class);
                intent.putExtra(KEY_ENGLISH, engWord);
                intent.putExtra(KEY_ARABIC, arabWord);

                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        //method required for OnBackPressedListener Interface that our Fragment implements. It is analogue of onBackPressed in Activity


        //Dialog that will ask whether user wants to exit or no; It wil appear when back button will be pressed
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        System.exit(0);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        showWordsList();
                        break;
                }
            }
        };

        //Setting title and buttons' text for Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}
