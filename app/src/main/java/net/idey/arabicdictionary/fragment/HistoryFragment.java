package net.idey.arabicdictionary.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import net.idey.arabicdictionary.HistoryDBHelper;
import net.idey.arabicdictionary.OnBackPressedListener;
import net.idey.arabicdictionary.R;
import net.idey.arabicdictionary.activity.WordActivity;

public class HistoryFragment extends Fragment implements View.OnClickListener, OnBackPressedListener{

    HistoryDBHelper dbHelper;
    ListView mHistoryLV;
    SimpleCursorAdapter mListAdapter;
    RelativeLayout mCleanHistory;

    private static final String KEY_ENGLISH = "english";
    private static final String KEY_ARABIC = "arabic";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        /*
        Initialize layout, show list of viewed words from history Database and set click listener for list view items and go to WordActivity.
        Logic is similar to previous cases
         */

        dbHelper = new HistoryDBHelper(getActivity());
        mHistoryLV = (ListView)view.findViewById(R.id.mHistoryList);
        mCleanHistory = (RelativeLayout)view.findViewById(R.id.mCleanHistory);
        mCleanHistory.setOnClickListener(this);
        Cursor c = dbHelper.showHistory();
        String[] columns = new String[]{"_id", "eng", "arab"};
        int[] to = new int[]{R.id.mIdx, R.id.mWord, R.id.mShort};
        mListAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listitem, c, columns, to, 0);
        mHistoryLV.setAdapter(mListAdapter);

        if (mHistoryLV.getAdapter().isEmpty()){
            mCleanHistory.setVisibility(View.GONE);
        }

        mHistoryLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String engWord = ((TextView)view.findViewById(R.id.mWord)).getText().toString();
                String arabWord = ((TextView)view.findViewById(R.id.mShort)).getText().toString();

                Intent intent = new Intent(getActivity(), WordActivity.class);
                intent.putExtra(KEY_ENGLISH, engWord);
                intent.putExtra(KEY_ARABIC, arabWord);

                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mCleanHistory:
                dbHelper.cleanHistory();
                mListAdapter.notifyDataSetChanged();
                mHistoryLV.setAdapter(null);
                mCleanHistory.setVisibility(View.GONE);
                break;
        }
    }
    @Override
    public void onBackPressed() {
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
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}
