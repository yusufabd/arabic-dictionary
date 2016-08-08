package net.idey.arabicdictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import net.idey.arabicdictionary.MyDatabase;
import net.idey.arabicdictionary.OCRActivity;
import net.idey.arabicdictionary.OnBackPressedListener;
import net.idey.arabicdictionary.R;
import net.idey.arabicdictionary.fragment.ArabicGrammarFragment;
import net.idey.arabicdictionary.fragment.BookmarksFragment;
import net.idey.arabicdictionary.fragment.DictionaryFragment;
import net.idey.arabicdictionary.fragment.GrammarFragment;
import net.idey.arabicdictionary.fragment.HistoryFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyDatabase database;
    boolean doubleBackToExitPressedOnce = false;
    DictionaryFragment df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String extra = getIntent().getStringExtra("english");


        Bundle b = new Bundle();
        b.putString("english", extra);

        df = new DictionaryFragment().newInstance(b);

        /*
            Adding (initializing) toolbar to activity
         */

        final Toolbar mToolbar = (Toolbar)findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        Setting dictionary fragment as main layout, i.e. it will be displayed when you start application
         */

        FragmentManager myManager = getSupportFragmentManager();
        FragmentTransaction myTransaction = myManager.beginTransaction();
        Fragment myFragment;
        myFragment = df;

        myTransaction.replace(R.id.mFragmentContainer, myFragment);
        myTransaction.commit();

        /*
            Adding drawer itself to activity: initializing its items' titles and icons, setting on click listener for items
         */
        DrawerBuilder mBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        new SecondaryDrawerItem().withIcon(R.drawable.dictionary).withName(R.string.nav_dictionary),
                        new SecondaryDrawerItem().withIcon(R.drawable.camera).withName(R.string.nav_ocr),
                        new SecondaryDrawerItem().withIcon(R.drawable.book).withName(R.string.nav_grammar),
                        new SecondaryDrawerItem().withIcon(R.drawable.shape).withName(R.string.nav_arabic_grammar),
                        new SecondaryDrawerItem().withIcon(R.drawable.history).withName(R.string.nav_history),
                        new SecondaryDrawerItem().withIcon(R.drawable.bookmarks).withName(R.string.nav_bookmarks),
                        new SecondaryDrawerItem().withIcon(R.drawable.direction).withName(R.string.nav_exit)
                        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {

                        if(drawerItem != null){
                            if(drawerItem instanceof Nameable){
                                mToolbar.setTitle(((Nameable)drawerItem).getNameRes());
                            }
                        }

                        FragmentManager mManager = getSupportFragmentManager();
                        FragmentTransaction mTransaction = mManager.beginTransaction();
                        Fragment mFragment = new Fragment();

                        switch (position){
                            case 0:
                                mFragment = df;
                                break;
                            case 1:
                                startActivity(new Intent(getApplicationContext(), OCRActivity.class));
//                                mFragment = new OCRFragment();
                                break;
                            case 2:
                                mFragment = new GrammarFragment();
                                break;
                            case 3:
                                mFragment = new ArabicGrammarFragment();
                                break;
                            case 4:
                                mFragment = new HistoryFragment();
                                break;
                            case 5:
                                mFragment = new BookmarksFragment();
                                break;
                            case 6:
                                System.exit(0);
                        }
                        mTransaction.replace(R.id.mFragmentContainer, mFragment);
                        mTransaction.commit();

                        return false;
                    }
                });
        Drawer mDrawer = mBuilder.build();
    }


//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
//    }


    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null){
            for (Fragment fragment : fragmentList){
                if (fragment instanceof OnBackPressedListener){
                    ((OnBackPressedListener)fragment).onBackPressed();
                }
            }
        }


    }
}
